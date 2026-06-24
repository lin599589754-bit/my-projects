package com.freshfood.backend.service;

import com.freshfood.backend.common.BusinessException;
import com.freshfood.backend.common.NotFoundException;
import com.freshfood.backend.entity.Address;
import com.freshfood.backend.entity.Cart;
import com.freshfood.backend.entity.OrderItem;
import com.freshfood.backend.entity.Orders;
import com.freshfood.backend.entity.Product;
import com.freshfood.backend.repository.AddressRepository;
import com.freshfood.backend.repository.CartRepository;
import com.freshfood.backend.repository.OrderItemRepository;
import com.freshfood.backend.repository.OrdersRepository;
import com.freshfood.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class OrderService {

    private static final Byte SELECTED = (byte) 1;
    private static final Byte ON_SALE = (byte) 1;
    private static final Byte WAIT_PAY = (byte) 0;
    private static final Byte WAIT_SHIP = (byte) 1;
    private static final Byte WAIT_RECEIVE = (byte) 2;
    private static final Byte COMPLETED = (byte) 3;
    private static final Byte CANCELED = (byte) 4;
    private static final Byte MOCK_PAY = (byte) 2;

    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final CartService cartService;

    public OrderService(OrdersRepository ordersRepository,
                        OrderItemRepository orderItemRepository,
                        CartRepository cartRepository,
                        ProductRepository productRepository,
                        AddressRepository addressRepository,
                        CartService cartService) {
        this.ordersRepository = ordersRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
        this.cartService = cartService;
    }

    public List<Orders> listByUserId(Long userId) {
        return ordersRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    public List<Orders> listByUserIdAndStatus(Long userId, Byte orderStatus) {
        return ordersRepository.findByUserIdAndOrderStatusOrderByCreateTimeDesc(userId, orderStatus);
    }

    public Orders getOrderDetail(Long id) {
        Orders order = ordersRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("订单不存在"));

        order.setOrderItems(orderItemRepository.findByOrderIdOrderByIdAsc(id));
        return order;
    }

    @Transactional
    public Orders createOrder(Long userId, Long addressId, String userRemark) {
        List<Cart> selectedCarts = cartRepository.findByUserIdAndSelectedOrderByIdDesc(userId, SELECTED);

        if (selectedCarts.isEmpty()) {
            throw new BusinessException("购物车中没有选中的商品");
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new BusinessException("收货地址不存在"));

        if (!userId.equals(address.getUserId())) {
            throw new BusinessException("收货地址不属于当前用户");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Cart cart : selectedCarts) {
            Product product = getAvailableProduct(cart);
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        LocalDateTime now = LocalDateTime.now();

        Orders order = new Orders();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setTotalAmount(totalAmount);
        order.setFreightAmount(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setActualAmount(totalAmount);
        order.setOrderStatus(WAIT_PAY);
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(buildReceiverAddress(address));
        order.setUserRemark(userRemark);
        order.setCreateTime(now);
        order.setUpdateTime(now);

        Orders savedOrder = ordersRepository.save(order);

        for (Cart cart : selectedCarts) {
            Product product = getAvailableProduct(cart);

            product.setStock(product.getStock() - cart.getQuantity());
            product.setUpdateTime(LocalDateTime.now());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(savedOrder.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setUnit(product.getUnit());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            orderItem.setCreateTime(LocalDateTime.now());
            orderItemRepository.save(orderItem);
        }

        cartService.clearSelectedByUserId(userId);

        return getOrderDetail(savedOrder.getId());
    }

    @Transactional
    public Orders mockPay(Long id) {
        Orders order = ordersRepository.findById(id)
                .orElseThrow(() -> new BusinessException("订单不存在"));

        if (!WAIT_PAY.equals(order.getOrderStatus())) {
            throw new BusinessException("当前订单状态不能支付");
        }

        order.setOrderStatus(WAIT_SHIP);
        order.setPaymentMethod(MOCK_PAY);
        order.setPaymentTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        increaseSaleVolume(id);

        return ordersRepository.save(order);
    }

    @Transactional
    public Orders ship(Long id, String trackingNo) {
        Orders order = ordersRepository.findById(id)
                .orElseThrow(() -> new BusinessException("订单不存在"));

        if (!WAIT_SHIP.equals(order.getOrderStatus())) {
            throw new BusinessException("当前订单状态不能发货");
        }

        order.setOrderStatus(WAIT_RECEIVE);
        order.setTrackingNo(trackingNo);
        order.setShipTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        return ordersRepository.save(order);
    }

    @Transactional
    public Orders confirmReceive(Long id) {
        Orders order = ordersRepository.findById(id)
                .orElseThrow(() -> new BusinessException("订单不存在"));

        if (!WAIT_RECEIVE.equals(order.getOrderStatus())) {
            throw new BusinessException("当前订单状态不能确认收货");
        }

        order.setOrderStatus(COMPLETED);
        order.setReceiveTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        return ordersRepository.save(order);
    }

    @Transactional
    public Orders cancel(Long id) {
        Orders order = ordersRepository.findById(id)
                .orElseThrow(() -> new BusinessException("订单不存在"));

        if (!WAIT_PAY.equals(order.getOrderStatus())) {
            throw new BusinessException("当前订单状态不能取消");
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrderIdOrderByIdAsc(id);

        for (OrderItem item : orderItems) {
            if (item.getProductId() == null) {
                continue;
            }

            Product product = productRepository.findById(item.getProductId()).orElse(null);

            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                product.setUpdateTime(LocalDateTime.now());
                productRepository.save(product);
            }
        }

        order.setOrderStatus(CANCELED);
        order.setCloseReason("用户取消订单");
        order.setUpdateTime(LocalDateTime.now());

        return ordersRepository.save(order);
    }

    private Product getAvailableProduct(Cart cart) {
        Product product = productRepository.findById(cart.getProductId())
                .orElseThrow(() -> new BusinessException("商品不存在，商品ID：" + cart.getProductId()));

        if (!ON_SALE.equals(product.getStatus())) {
            throw new BusinessException("商品已下架：" + product.getName());
        }

        if (product.getStock() < cart.getQuantity()) {
            throw new BusinessException("库存不足：" + product.getName());
        }

        return product;
    }

    private void increaseSaleVolume(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderIdOrderByIdAsc(orderId);

        for (OrderItem item : orderItems) {
            if (item.getProductId() == null) {
                continue;
            }

            Product product = productRepository.findById(item.getProductId()).orElse(null);

            if (product != null) {
                product.setSaleVolume(product.getSaleVolume() + item.getQuantity());
                product.setUpdateTime(LocalDateTime.now());
                productRepository.save(product);
            }
        }
    }

    private String buildReceiverAddress(Address address) {
        return address.getProvince()
                + address.getCity()
                + address.getDistrict()
                + address.getDetailAddress();
    }

    private String generateOrderNo() {
        String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomPart = String.format("%04d", new Random().nextInt(10000));
        return timePart + randomPart;
    }
}

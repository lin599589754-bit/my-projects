package com.freshfood.backend.service;

import com.freshfood.backend.common.NotFoundException;
import com.freshfood.backend.repository.AddressRepository;
import com.freshfood.backend.repository.CartRepository;
import com.freshfood.backend.repository.OrderItemRepository;
import com.freshfood.backend.repository.OrdersRepository;
import com.freshfood.backend.repository.ProductRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class OrderServiceTest {

    private final OrdersRepository ordersRepository = mock(OrdersRepository.class);
    private final OrderItemRepository orderItemRepository = mock(OrderItemRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final AddressRepository addressRepository = mock(AddressRepository.class);
    private final CartService cartService = mock(CartService.class);

    private final OrderService orderService = new OrderService(
            ordersRepository,
            orderItemRepository,
            cartRepository,
            productRepository,
            addressRepository,
            cartService);

    @Test
    void getOrderDetailThrowsNotFoundExceptionWhenOrderDoesNotExist() {
        given(ordersRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderDetail(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("订单不存在");
    }
}

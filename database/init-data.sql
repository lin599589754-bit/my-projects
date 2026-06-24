/*
  FreshFoodSystem-v2 demo data

  常见 SQL 关键字说明：
  - INSERT INTO：向表中插入数据。
  - VALUES：要插入的具体值。
  - DELETE FROM：删除表中的数据，这里用于重置演示数据。
  - SET：设置 MySQL 会话变量或系统开关。
  - ALTER TABLE ... AUTO_INCREMENT：重置自增 ID 的起始值。

  执行顺序：
  1. 先执行 init-schema.sql 创建表。
  2. 再执行本文件插入演示数据。
*/

USE `fresh_delivery_v2`;

SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM `order_item`;
DELETE FROM `orders`;
DELETE FROM `cart`;
DELETE FROM `address`;
DELETE FROM `product`;
DELETE FROM `category`;
DELETE FROM `user`;

ALTER TABLE `user` AUTO_INCREMENT = 1;
ALTER TABLE `category` AUTO_INCREMENT = 1;
ALTER TABLE `product` AUTO_INCREMENT = 1;
ALTER TABLE `cart` AUTO_INCREMENT = 1;
ALTER TABLE `address` AUTO_INCREMENT = 1;
ALTER TABLE `orders` AUTO_INCREMENT = 1;
ALTER TABLE `order_item` AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO `category` (`id`, `name`, `icon`, `sort_order`, `status`) VALUES
(1, '蔬菜', '/images/veg.png', 10, 1),
(2, '水果', '/images/fruit.png', 20, 1),
(3, '肉禽', '/images/meat.png', 30, 1),
(4, '水产', '/images/fish.png', 40, 1),
(5, '粮油调味', '/images/oil.png', 50, 1),
(6, '熟食面点', '/images/cooked.png', 60, 1),
(7, '饮料乳品', '/images/drink.png', 70, 1),
(8, '休闲食品', '/images/snack.png', 80, 1);

INSERT INTO `product`
(`id`, `category_id`, `name`, `main_image`, `detail_images`, `description`, `price`, `original_price`, `unit`, `stock`, `sale_volume`, `is_hot`, `is_new`, `status`, `sort_order`)
VALUES
(1, 1, '有机番茄', '/images/products/tomato.png', NULL, '自然成熟，酸甜多汁，适合凉拌和炒菜。', 6.90, 8.90, '斤', 120, 36, 1, 1, 1, 10),
(2, 1, '本地土豆', '/images/products/potato.png', NULL, '粉糯耐煮，适合炖菜、土豆泥和家常炒菜。', 3.80, 4.80, '斤', 160, 24, 0, 1, 1, 20),
(3, 1, '上海青', '/images/products/green-vegetables.png', NULL, '鲜嫩脆爽，当日采收，适合清炒。', 4.50, 5.50, '斤', 90, 42, 1, 0, 1, 30),
(4, 2, '红富士苹果', '/images/products/apple.png', NULL, '果香浓郁，脆甜多汁。', 7.90, 9.90, '斤', 100, 58, 1, 0, 1, 40),
(5, 2, '山东樱桃', '/images/products/cherry.png', NULL, '颗粒饱满，酸甜适中，适合作为下午茶水果。', 29.90, 36.90, '盒', 45, 19, 1, 1, 1, 50),
(6, 3, '鲜鸡蛋', '/images/products/egg.png', NULL, '蛋黄饱满，适合家庭日常烹饪。', 12.90, 15.90, '盒', 80, 65, 1, 0, 1, 60),
(7, 3, '冷鲜鸡胸肉', '/images/products/null.png', NULL, '低脂高蛋白，适合煎烤和健身餐。', 16.80, 19.80, '份', 60, 22, 0, 1, 1, 70),
(8, 4, '鲜活鲫鱼', '/images/products/null.png', NULL, '适合煲汤，肉质细嫩。', 18.80, 22.80, '条', 35, 16, 0, 1, 1, 80),
(9, 5, '东北大米', '/images/products/null.png', NULL, '米香自然，口感软糯。', 39.90, 45.90, '袋', 50, 31, 1, 0, 1, 90),
(10, 6, '手工馒头', '/images/products/null.png', NULL, '松软有嚼劲，早餐和正餐都合适。', 8.90, 10.90, '袋', 70, 28, 0, 1, 1, 100),
(11, 7, '鲜牛奶', '/images/products/milk.png', NULL, '低温鲜奶，冷藏配送。', 13.90, 16.90, '瓶', 75, 47, 1, 1, 1, 110),
(12, 8, '每日坚果', '/images/products/null.png', NULL, '独立小包装，办公室和家庭零食。', 25.90, 29.90, '盒', 55, 21, 0, 0, 1, 120);

INSERT INTO `user`
(`id`, `openid`, `unionid`, `nick_name`, `avatar_url`, `phone`, `gender`, `status`, `last_login_time`)
VALUES
(1, 'demo_openid_user_001', NULL, '社区用户A', '/images/user.png', '13800000001', 0, 1, '2026-06-23 09:20:00'),
(2, 'demo_openid_user_002', NULL, '社区用户B', '/images/user.png', '13800000002', 0, 1, '2026-06-23 10:15:00');

INSERT INTO `address`
(`id`, `user_id`, `receiver_name`, `receiver_phone`, `province`, `city`, `district`, `detail_address`, `label`, `is_default`)
VALUES
(1, 1, '张同学', '13800000001', '广东省', '广州市', '天河区', '社区花园1栋101', '家', 1),
(2, 1, '张同学', '13800000001', '广东省', '广州市', '天河区', '学校公寓3栋205', '学校', 0),
(3, 2, '李同学', '13800000002', '广东省', '广州市', '海珠区', '滨江小区6栋602', '家', 1);

INSERT INTO `cart`
(`id`, `user_id`, `product_id`, `quantity`, `selected`)
VALUES
(1, 1, 1, 2, 1),
(2, 1, 6, 1, 1),
(3, 1, 11, 1, 0),
(4, 2, 4, 3, 1);

INSERT INTO `orders`
(`id`, `order_no`, `user_id`, `address_id`, `total_amount`, `freight_amount`, `discount_amount`, `actual_amount`, `payment_method`, `payment_time`, `order_status`, `tracking_no`, `ship_time`, `receive_time`, `receiver_name`, `receiver_phone`, `receiver_address`, `user_remark`, `close_reason`, `create_time`)
VALUES
(1, '202606230001', 1, 1, 26.70, 0.00, 0.00, 26.70, 2, '2026-06-23 11:05:00', 3, 'SF1000000001', '2026-06-23 14:00:00', '2026-06-23 18:30:00', '张同学', '13800000001', '广东省广州市天河区社区花园1栋101', '请放门口保温袋', NULL, '2026-06-23 11:00:00'),
(2, '202606230002', 1, 1, 36.80, 0.00, 0.00, 36.80, 2, '2026-06-23 15:20:00', 2, 'SF1000000002', '2026-06-23 16:10:00', NULL, '张同学', '13800000001', '广东省广州市天河区社区花园1栋101', NULL, NULL, '2026-06-23 15:10:00'),
(3, '202606230003', 2, 3, 23.70, 0.00, 0.00, 23.70, NULL, NULL, 0, NULL, NULL, NULL, '李同学', '13800000002', '广东省广州市海珠区滨江小区6栋602', '傍晚配送', NULL, '2026-06-23 17:25:00');

INSERT INTO `order_item`
(`id`, `order_id`, `product_id`, `product_name`, `product_image`, `unit`, `price`, `quantity`, `total_amount`)
VALUES
(1, 1, 1, '有机番茄', '/images/products/tomato.png', '斤', 6.90, 2, 13.80),
(2, 1, 6, '鲜鸡蛋', '/images/products/egg.png', '盒', 12.90, 1, 12.90),
(3, 2, 5, '山东樱桃', '/images/products/cherry.png', '盒', 29.90, 1, 29.90),
(4, 2, 1, '有机番茄', '/images/products/tomato.png', '斤', 6.90, 1, 6.90),
(5, 3, 4, '红富士苹果', '/images/products/apple.png', '斤', 7.90, 3, 23.70);

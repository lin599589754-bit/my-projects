/*
  FreshFoodSystem-v2 database schema

  常见 SQL 关键字说明：
  - CREATE DATABASE：创建数据库。
  - USE：切换到指定数据库，后续建表都在这个数据库中执行。
  - DROP TABLE IF EXISTS：如果表已经存在就删除，方便重新初始化数据库。
  - CREATE TABLE：创建数据表。
  - PRIMARY KEY：主键，唯一标识一行数据。
  - AUTO_INCREMENT：自增，插入数据时自动生成下一个数字 ID。
  - NOT NULL：不能为空，插入数据时必须提供值。
  - DEFAULT：默认值，插入时不提供该字段就使用默认值。
  - UNIQUE KEY：唯一索引，保证某个字段或字段组合不能重复。
  - KEY / INDEX：普通索引，加快查询速度。
  - COMMENT：中文说明，不影响逻辑，但方便阅读和数据库工具展示。
  - ENGINE=InnoDB：使用 MySQL InnoDB 存储引擎，支持事务和外键能力。
  - CHARSET=utf8mb4：支持中文和 emoji。
  - COLLATE=utf8mb4_unicode_ci：字符排序和比较规则，ci 表示大小写不敏感。
*/

CREATE DATABASE IF NOT EXISTS `fresh_delivery_v2`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `fresh_delivery_v2`;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `order_item`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `cart`;
DROP TABLE IF EXISTS `address`;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `category`;
DROP TABLE IF EXISTS `user`;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键，自增',
  `openid` varchar(64) NOT NULL COMMENT '微信小程序用户唯一标识，真实登录后由微信返回',
  `unionid` varchar(64) DEFAULT NULL COMMENT '微信开放平台统一ID，可为空',
  `nick_name` varchar(50) DEFAULT NULL COMMENT '用户昵称',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '用户头像地址',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `gender` tinyint NOT NULL DEFAULT 0 COMMENT '性别：0未知，1男，2女',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '用户状态：1正常，0禁用',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_openid` (`openid`),
  KEY `idx_user_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID，主键，自增',
  `name` varchar(50) NOT NULL COMMENT '分类名称，如蔬菜、水果、肉禽',
  `icon` varchar(255) DEFAULT NULL COMMENT '分类图标地址',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序值，数字越小越靠前',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '分类状态：1启用，0停用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_name` (`name`),
  KEY `idx_category_status_sort` (`status`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID，主键，自增',
  `category_id` bigint NOT NULL COMMENT '分类ID，对应 category.id',
  `name` varchar(100) NOT NULL COMMENT '商品名称',
  `main_image` varchar(255) DEFAULT NULL COMMENT '商品主图地址',
  `detail_images` text COMMENT '商品详情图，多个地址可用JSON数组保存',
  `description` varchar(500) DEFAULT NULL COMMENT '商品描述',
  `price` decimal(10,2) NOT NULL COMMENT '销售价，单位元',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价，单位元',
  `unit` varchar(20) NOT NULL COMMENT '销售单位，如斤、份、盒、袋',
  `stock` int NOT NULL DEFAULT 0 COMMENT '库存数量',
  `sale_volume` int NOT NULL DEFAULT 0 COMMENT '销量',
  `is_hot` tinyint NOT NULL DEFAULT 0 COMMENT '是否热销：1是，0否',
  `is_new` tinyint NOT NULL DEFAULT 0 COMMENT '是否新品：1是，0否',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '商品状态：1上架，0下架',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序值，数字越小越靠前',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_category` (`category_id`),
  KEY `idx_product_status` (`status`),
  KEY `idx_product_hot` (`is_hot`, `status`),
  KEY `idx_product_new` (`is_new`, `status`),
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

CREATE TABLE `cart` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车项ID，主键，自增',
  `user_id` bigint NOT NULL COMMENT '用户ID，对应 user.id',
  `product_id` bigint NOT NULL COMMENT '商品ID，对应 product.id',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '购买数量',
  `selected` tinyint NOT NULL DEFAULT 1 COMMENT '是否选中结算：1选中，0未选中',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cart_user_product` (`user_id`, `product_id`),
  KEY `idx_cart_user_selected` (`user_id`, `selected`),
  KEY `idx_cart_product` (`product_id`),
  CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_cart_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

CREATE TABLE `address` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID，主键，自增',
  `user_id` bigint NOT NULL COMMENT '用户ID，对应 user.id',
  `receiver_name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) NOT NULL COMMENT '收货人电话',
  `province` varchar(30) NOT NULL COMMENT '省',
  `city` varchar(30) NOT NULL COMMENT '市',
  `district` varchar(30) NOT NULL COMMENT '区或县',
  `detail_address` varchar(200) NOT NULL COMMENT '详细地址',
  `label` varchar(20) DEFAULT NULL COMMENT '地址标签，如家、公司、学校',
  `is_default` tinyint NOT NULL DEFAULT 0 COMMENT '是否默认地址：1默认，0非默认',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_address_user` (`user_id`),
  KEY `idx_address_user_default` (`user_id`, `is_default`),
  CONSTRAINT `fk_address_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收货地址表';

CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID，主键，自增',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号，业务唯一',
  `user_id` bigint NOT NULL COMMENT '用户ID，对应 user.id',
  `address_id` bigint DEFAULT NULL COMMENT '下单时选择的地址ID，地址删除后订单仍保留快照',
  `total_amount` decimal(10,2) NOT NULL COMMENT '商品总金额',
  `freight_amount` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '运费金额',
  `discount_amount` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
  `actual_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `payment_method` tinyint DEFAULT NULL COMMENT '支付方式：1微信支付，2模拟支付',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `order_status` tinyint NOT NULL DEFAULT 0 COMMENT '订单状态：0待付款，1待发货，2待收货，3已完成，4已取消',
  `tracking_no` varchar(64) DEFAULT NULL COMMENT '运单号，后台发货时填写',
  `ship_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '确认收货时间',
  `receiver_name` varchar(50) NOT NULL COMMENT '收货人姓名快照',
  `receiver_phone` varchar(20) NOT NULL COMMENT '收货人电话快照',
  `receiver_address` varchar(255) NOT NULL COMMENT '收货地址快照',
  `user_remark` varchar(200) DEFAULT NULL COMMENT '用户备注',
  `close_reason` varchar(100) DEFAULT NULL COMMENT '订单关闭原因',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_orders_order_no` (`order_no`),
  KEY `idx_orders_user` (`user_id`),
  KEY `idx_orders_status` (`order_status`),
  KEY `idx_orders_create_time` (`create_time`),
  CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_orders_address` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单主表';

CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单明细ID，主键，自增',
  `order_id` bigint NOT NULL COMMENT '订单ID，对应 orders.id',
  `product_id` bigint DEFAULT NULL COMMENT '商品ID，对应 product.id，商品删除后明细仍保留快照',
  `product_name` varchar(100) NOT NULL COMMENT '商品名称快照',
  `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片快照',
  `unit` varchar(20) NOT NULL COMMENT '商品单位快照',
  `price` decimal(10,2) NOT NULL COMMENT '下单时单价快照',
  `quantity` int NOT NULL COMMENT '购买数量',
  `total_amount` decimal(10,2) NOT NULL COMMENT '明细小计，price * quantity',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_item_order` (`order_id`),
  KEY `idx_order_item_product` (`product_id`),
  CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_order_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';

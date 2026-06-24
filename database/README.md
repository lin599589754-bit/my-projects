# 数据库说明

本目录保存 FreshFoodSystem-v2 的数据库脚本。

当前数据库采用 MySQL，目标库名为：

```text
fresh_delivery_v2
```

使用独立 MySQL 实例：

```text
Host: 127.0.0.1
Port: 3307
Username: root
Password: 空
DataDir: D:\Projects\FreshFoodSystem-v2\mysql-data\Data
```

## 文件说明

| 文件 | 作用 |
| --- | --- |
| `init-schema.sql` | 创建数据库和核心业务表 |
| `init-data.sql` | 插入分类、商品、用户、地址、购物车、订单等演示数据 |
| `README.md` | 说明数据库结构、状态字段和常见 SQL 关键字 |

## 执行顺序

推荐直接使用脚本：

```powershell
.\scripts\reset-database.ps1
```

也可以手动执行 SQL。

先执行建表脚本：

```sql
SOURCE D:/Projects/FreshFoodSystem-v2/database/init-schema.sql;
```

再执行演示数据脚本：

```sql
SOURCE D:/Projects/FreshFoodSystem-v2/database/init-data.sql;
```

如果使用 DBeaver、Navicat 或 MySQL Workbench，也可以直接打开 SQL 文件后依次运行。

## 核心表

本版本先保留毕业设计需要的核心业务闭环，不加入优惠券、真实支付流水、复杂权限等扩展表。

| 表名 | 含义 |
| --- | --- |
| `user` | 用户表，保存微信用户身份、昵称、手机号、状态 |
| `category` | 商品分类表，替代旧项目中前端写死的分类 |
| `product` | 商品表，保存商品价格、库存、图片、上下架状态 |
| `cart` | 购物车表，保存用户加入购物车的商品和选中状态 |
| `address` | 收货地址表，保存用户地址和默认地址 |
| `orders` | 订单主表，保存订单金额、状态、收货快照、发货信息 |
| `order_item` | 订单明细表，保存每个订单中的商品快照 |

## DBeaver 连接信息

新建 MySQL 连接时填写：

| 项 | 值 |
| --- | --- |
| Server Host | `127.0.0.1` |
| Port | `3307` |
| Database | `fresh_delivery_v2` |
| Username | `root` |
| Password | 留空 |

MySQL 自带的 `mysql`、`performance_schema`、`information_schema`、`sys` 是系统库，不是本项目业务表。

## 关键设计

### 分类独立成表

旧项目中商品分类主要靠前端写死数字。v2 新增 `category` 表，让后端、小程序和管理后台都读取同一套分类数据。

### 购物车选中状态以后端为准

`cart.selected` 表示购物车项是否参与结算：

| 值 | 含义 |
| --- | --- |
| `1` | 选中 |
| `0` | 未选中 |

确认订单时只读取后端 `selected = 1` 的购物车项，避免前端缓存和数据库状态不一致。

### 订单保留收货快照

订单表里同时保存：

- `address_id`：下单时选择的地址 ID
- `receiver_name`：收货人姓名快照
- `receiver_phone`：收货人电话快照
- `receiver_address`：收货地址快照

这样即使用户之后修改或删除地址，历史订单仍然能展示当时的收货信息。

### 发货和收货时间拆分

旧项目中 `delivery_time` 同时表示发货时间和送达时间，语义容易混乱。v2 拆分为：

| 字段 | 含义 |
| --- | --- |
| `ship_time` | 后台发货时间 |
| `receive_time` | 用户确认收货时间 |
| `tracking_no` | 运单号 |

## 状态字段

### 用户状态 `user.status`

| 值 | 含义 |
| --- | --- |
| `1` | 正常 |
| `0` | 禁用 |

### 分类状态 `category.status`

| 值 | 含义 |
| --- | --- |
| `1` | 启用 |
| `0` | 停用 |

### 商品状态 `product.status`

| 值 | 含义 |
| --- | --- |
| `1` | 上架 |
| `0` | 下架 |

### 是否热销 `product.is_hot`

| 值 | 含义 |
| --- | --- |
| `1` | 热销 |
| `0` | 普通 |

### 是否新品 `product.is_new`

| 值 | 含义 |
| --- | --- |
| `1` | 新品 |
| `0` | 普通 |

### 地址默认状态 `address.is_default`

| 值 | 含义 |
| --- | --- |
| `1` | 默认地址 |
| `0` | 非默认地址 |

### 订单状态 `orders.order_status`

| 值 | 含义 | 触发动作 |
| --- | --- | --- |
| `0` | 待付款 | 创建订单 |
| `1` | 待发货 | 模拟支付或真实支付成功 |
| `2` | 待收货 | 后台发货 |
| `3` | 已完成 | 用户确认收货 |
| `4` | 已取消 | 用户取消待付款订单 |

### 支付方式 `orders.payment_method`

| 值 | 含义 |
| --- | --- |
| `1` | 微信支付 |
| `2` | 模拟支付 |
| `NULL` | 未支付 |

## 常见 SQL 关键字解释

| 关键字 | 含义 |
| --- | --- |
| `CREATE DATABASE` | 创建数据库 |
| `USE` | 选择当前要操作的数据库 |
| `CREATE TABLE` | 创建数据表 |
| `DROP TABLE IF EXISTS` | 如果表存在就删除，常用于重建数据库 |
| `INSERT INTO` | 插入数据 |
| `DELETE FROM` | 删除表中数据 |
| `PRIMARY KEY` | 主键，唯一标识一行数据 |
| `AUTO_INCREMENT` | 自增，常用于自动生成 ID |
| `NOT NULL` | 字段不能为空 |
| `DEFAULT` | 字段默认值 |
| `UNIQUE KEY` | 唯一索引，防止重复数据 |
| `KEY` / `INDEX` | 普通索引，用于提升查询速度 |
| `FOREIGN KEY` | 外键，用于表达表和表之间的关联 |
| `ON DELETE SET NULL` | 被关联数据删除时，当前字段自动置空 |
| `ON DELETE CASCADE` | 被关联数据删除时，当前关联数据一起删除 |
| `COMMENT` | 注释说明，方便阅读 |
| `ENGINE=InnoDB` | 使用 InnoDB 存储引擎，支持事务 |
| `CHARSET=utf8mb4` | 字符集，支持中文和 emoji |

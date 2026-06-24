# 数据库说明

本目录记录 FreshFoodSystem-v2 的 MySQL 数据库结构和初始化方式。

## 1. 当前数据库配置

本地开发环境使用 MySQL：

```text
host: 127.0.0.1
port: 3307
database: fresh_delivery_v2
username: root
password: 空
```

项目中的 MySQL 数据目录约定为：

```text
D:\Projects\FreshFoodSystem-v2\mysql-data
```

这样做的好处是：数据库数据和项目目录放在一起，迁移、备份和查看都更直观。

## 2. 初始化脚本

数据库初始化脚本：

```text
database/init-schema.sql
```

脚本包含：

- 建库语句
- 分类表
- 商品表
- 用户表
- 收货地址表
- 购物车表
- 订单主表
- 订单明细表
- 示例分类数据
- 示例商品数据
- 示例用户、地址、购物车、订单数据

## 3. 表结构概览

| 表名 | 含义 |
| --- | --- |
| `category` | 商品分类 |
| `product` | 商品 |
| `user` | 用户 |
| `address` | 收货地址 |
| `cart` | 购物车 |
| `orders` | 订单主表 |
| `order_item` | 订单明细表 |

## 4. Java 类型映射

后端实体字段需要和 MySQL 字段类型保持一致。

| MySQL 类型 | Java 类型 |
| --- | --- |
| `bigint` | `Long` |
| `int` | `Integer` |
| `tinyint` | `Byte` |
| `varchar` | `String` |
| `text` | `String` |
| `decimal(10,2)` | `BigDecimal` |
| `datetime` | `LocalDateTime` |

## 5. DBeaver 查看方式

可以用 DBeaver 连接本地数据库：

```text
Database: MySQL
Host: 127.0.0.1
Port: 3307
Database: fresh_delivery_v2
Username: root
Password: 空
```

连接后重点查看：

- `fresh_delivery_v2`：当前项目库
- `sys`：MySQL 自带系统库，不是本项目业务库

## 6. 当前注意事项

- 当前后端使用 JPA 操作数据库。
- 数据库结构以 `init-schema.sql` 为准。
- 修改表结构后，需要同步修改 Entity、Repository、Service、接口文档和测试。
- 当前库存扣减尚未做高并发锁控制，适合学习、演示和普通联调场景。

# FreshFoodSystem-v2

FreshFoodSystem-v2 是一个生鲜电商后端重写项目，当前重心是后端能力展示和后续 Web 前端联调。项目从原 FreshFoodSystem 模型出发，去掉小程序优先目标，改为更适合求职展示的 Spring Boot + MySQL + JWT 后端项目。

## 当前状态

已完成：

- MySQL 数据库结构与初始化数据
- 分类、商品、用户、地址、购物车、订单核心接口
- 统一响应结构 `ApiResponse<T>`
- 全局异常处理
- DTO 请求对象与参数校验
- Spring Security + JWT 登录认证
- 用户数据所有权校验
- 业务异常和未知异常分离
- 订单不存在返回 `404`

暂未实现：

- 管理员角色与管理后台
- 真实微信登录
- 真实支付
- 高并发库存扣减方案
- 生产部署

## 技术栈

| 模块 | 技术 |
| --- | --- |
| 后端 | Spring Boot 4.1.0 |
| Web | Spring WebMVC |
| ORM | Spring Data JPA |
| 数据库 | MySQL 8 |
| 参数校验 | Jakarta Validation |
| 认证授权 | Spring Security + OAuth2 Resource Server JWT |
| 构建工具 | Maven |
| Java | JDK 17 |
| 辅助工具 | Lombok |

## 项目结构

```text
FreshFoodSystem-v2
├─ backend/          Spring Boot 后端项目
├─ database/         数据库初始化脚本与说明
├─ docs/             项目设计、接口、认证和后端完善文档
├─ mysql-data/       本地 MySQL 数据目录
├─ scripts/          本地辅助脚本说明
└─ web/              预留 Web 前端目录
```

## 本地数据库

当前本地开发数据库配置：

```text
host: 127.0.0.1
port: 3307
database: fresh_delivery_v2
username: root
password: 空
```

数据库表和初始化数据见：

- [database/init-schema.sql](D:/Projects/FreshFoodSystem-v2/database/init-schema.sql)
- [database/README.md](D:/Projects/FreshFoodSystem-v2/database/README.md)

## 后端运行

进入后端目录：

```powershell
cd D:\Projects\FreshFoodSystem-v2\backend
```

启动项目：

```powershell
.\mvnw.cmd spring-boot:run
```

健康检查：

```http
GET http://localhost:8080/api/health
```

运行测试：

```powershell
.\mvnw.cmd test
```

## 认证说明

公开接口：

- `GET /api/health`
- `POST /api/users/login`
- `GET /api/categories`
- `GET /api/products/**`

其他用户相关接口默认需要：

```http
Authorization: Bearer {token}
```

当前用户优先使用 `/current` 风格接口，例如：

- `GET /api/users/current`
- `GET /api/addresses/current`
- `GET /api/carts/current`
- `GET /api/orders/current`

## 文档入口

- [docs/01-需求分析.md](D:/Projects/FreshFoodSystem-v2/docs/01-需求分析.md)
- [docs/02-后端接口.md](D:/Projects/FreshFoodSystem-v2/docs/02-后端接口.md)
- [docs/03-后端完善说明.md](D:/Projects/FreshFoodSystem-v2/docs/03-后端完善说明.md)
- [docs/04-JWT认证设计.md](D:/Projects/FreshFoodSystem-v2/docs/04-JWT认证设计.md)

## 后续方向

下一阶段建议先做 Web 前端联调。等用户端完整流程跑通后，再考虑：

- 管理员角色和后台接口
- Swagger/Knife4j 接口文档
- 库存并发控制
- 订单号生成策略优化
- 部署说明和项目截图

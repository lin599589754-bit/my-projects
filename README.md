# FreshFoodSystem-v2

社区生鲜 Web 全栈系统重写版。

本项目参考同级目录中的旧项目 `FreshFoodSystem`，但数据库和后端代码按新的结构重新实现。当前定位调整为偏后端实现的求职项目：优先完善 Spring Boot 后端、JWT 登录认证、数据库设计和 Web 前端联调，再逐步补充管理后台与工程化能力。

## 目录结构

```text
FreshFoodSystem-v2
├─ backend
├─ web
├─ admin
├─ database
├─ mysql-data
│  └─ Data
├─ scripts
└─ docs
```

## 模块说明

- `backend`：Spring Boot 后端服务。
- `web`：用户端 Web 前端。
- `admin`：管理后台前端。
- `database`：数据库初始化脚本、表结构说明、少量演示数据。
- `mysql-data/Data`：新项目独立 MySQL 数据目录，使用端口 `3307`。
- `scripts`：启动、停止、重置数据库等脚本。
- `docs`：需求分析、接口文档、开发记录。

## 当前进度

已完成：

- MySQL 独立数据目录和 `fresh_delivery_v2` 数据库。
- 核心业务表：用户、分类、商品、购物车、地址、订单、订单明细。
- 后端基础结构：统一响应、健康检查、JPA 数据库连接。
- 分类模块接口。
- 商品模块接口。
- 用户模块接口。
- 地址模块接口。
- 购物车模块。
- 订单模块。
- Spring Security + JWT 登录认证。

待完成：

- 用户端 Web 前端。
- 管理后台接口。
- 管理后台前端。
- Swagger/Knife4j 接口文档。
- Redis、Docker Compose 等工程化增强。

## 阅读入口

1. `docs/01-需求分析.md`
2. `docs/02-后端接口.md`
3. `database/README.md`
4. `scripts/README.md`

## 数据库

新项目使用独立 MySQL 实例：

```text
Host: 127.0.0.1
Port: 3307
Database: fresh_delivery_v2
Username: root
Password: 空
DataDir: D:\Projects\FreshFoodSystem-v2\mysql-data\Data
```

启动数据库：

```powershell
.\scripts\start-mysql-v2.ps1
```

重建数据库和演示数据：

```powershell
.\scripts\reset-database.ps1
```

## 后端

后端目录：

```text
D:\Projects\FreshFoodSystem-v2\backend
```

技术栈：

- Java 17
- Spring Boot 4.1.0
- Spring WebMVC
- Spring Data JPA
- MySQL Driver
- Validation
- Lombok

启动后端：

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

后端默认端口：

```text
http://localhost:8080
```

运行测试：

```powershell
cd backend
.\mvnw.cmd test
```

## Git

当前仓库远程地址：

```text
https://github.com/lin599589754-bit/my-projects.git
```

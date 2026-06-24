# JWT 认证设计

## 1. 目的

JWT 用于解决“谁在调用接口”的问题。登录成功后，后端签发 token，前端后续请求统一携带 token，后端从 token 中识别当前用户。

当前项目使用 JWT 的目标：

- 不让前端随意传 `userId` 操作其他用户数据。
- 让地址、购物车、订单等私有数据只属于当前登录用户。
- 为后续 Web 前端联调提供稳定认证方式。

## 2. 关键词解释

| 词 | 含义 |
| --- | --- |
| `JWT` | JSON Web Token，一种登录凭证格式 |
| `token` | 登录成功后后端发给前端的字符串凭证 |
| `Bearer` | 持有者模式，表示谁带着 token，谁就是当前登录用户 |
| `Authorization` | HTTP 请求头，专门放认证信息 |
| `subject` | JWT 的主体，本项目用用户 ID 作为 subject |
| `claim` | JWT 中额外保存的信息，例如 `userId`、`openid`、`nickName` |
| `SecurityContext` | Spring Security 保存当前登录状态的上下文 |
| `SecurityContextHolder` | Spring Security 提供的上下文入口 |
| `current` | 当前登录用户自己的数据，不再依赖前端传 `userId` |
| `denyAll` | Spring Security 权限规则，表示该接口当前禁止任何请求访问 |

## 3. 登录链路

```text
前端提交登录信息
  -> UserController
  -> UserService 创建或更新用户
  -> JwtService 生成 JWT
  -> 返回 token 给前端
  -> 前端保存 token
  -> 后续请求带 Authorization 请求头
  -> Spring Security 校验 token
  -> CurrentUser 读取当前 userId
```

## 4. 登录接口

```http
POST /api/users/login
Content-Type: application/json
```

请求体：

```json
{
  "openid": "test-openid",
  "nickName": "测试用户",
  "avatarUrl": "https://example.com/avatar.png"
}
```

返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "tokenType": "Bearer",
    "token": "xxxxx",
    "expiresIn": 7200,
    "user": {
      "id": 1,
      "openid": "test-openid"
    }
  }
}
```

说明：

- 当前项目仍使用 `openid` 作为模拟登录标识。
- 登录成功后返回 `Bearer` 类型 token。
- Web 前端请求私有接口时需要携带 `Authorization: Bearer {token}`。

## 5. JWT 内容

当前 token 中包含：

| 字段 | 含义 |
| --- | --- |
| `sub` | 用户 ID 字符串 |
| `userId` | 用户 ID |
| `openid` | 用户 openid |
| `nickName` | 用户昵称 |
| `iss` | token 签发者 |
| `iat` | 签发时间 |
| `exp` | 过期时间 |

## 6. 配置项

配置位置：

```text
backend/src/main/resources/application.properties
```

当前配置：

```properties
app.jwt.secret=${JWT_SECRET:freshfood-v2-jwt-secret-key-must-be-at-least-32-bytes}
app.jwt.issuer=${JWT_ISSUER:freshfood-backend}
app.jwt.expires-in=${JWT_EXPIRES_IN:7200}
```

说明：

- `JWT_SECRET`：签名密钥，部署时应该用环境变量覆盖。
- `JWT_ISSUER`：签发者，用于标识 token 来源。
- `JWT_EXPIRES_IN`：过期时间，单位是秒，当前默认 7200 秒。

## 7. 公开接口和受保护接口

公开接口：

- `GET /api/health`
- `POST /api/users/login`
- `GET /api/categories`
- `GET /api/products/**`

受保护接口：

- `GET /api/users/current`
- `GET /api/addresses/**`
- `GET /api/carts/**`
- `GET /api/orders/**`
- 地址、购物车、订单相关写操作

当前禁止普通用户访问：

- `GET /api/users`
- `GET /api/users/openid/{openid}`
- `PUT /api/orders/{id}/ship`

## 8. 当前用户接口

当前推荐前端优先使用 `/current` 风格接口：

| 接口 | 含义 |
| --- | --- |
| `GET /api/users/current` | 查询当前登录用户 |
| `GET /api/addresses/current` | 查询当前用户地址 |
| `POST /api/addresses/current` | 当前用户新增地址 |
| `PUT /api/addresses/current/{id}` | 当前用户修改地址 |
| `GET /api/carts/current` | 查询当前用户购物车 |
| `GET /api/carts/current/selected` | 查询当前用户选中购物车 |
| `POST /api/carts/current` | 当前用户加入购物车 |
| `DELETE /api/carts/current` | 清空当前用户购物车 |
| `GET /api/orders/current` | 查询当前用户订单 |
| `GET /api/orders/current/status/{orderStatus}` | 按状态查询当前用户订单 |
| `POST /api/orders/current` | 当前用户创建订单 |

好处：

- 前端不用传 `userId`。
- 后端统一从 token 中获取用户 ID。
- 能减少越权风险。

## 9. 所有权校验

旧接口仍保留，例如：

```http
GET /api/carts/user/{userId}
GET /api/orders/user/{userId}
```

但这些接口会校验：

```text
token 中的 userId == URL 中的 userId
```

如果不一致，返回：

```json
{
  "code": 403,
  "message": "不能访问其他用户的数据",
  "data": null
}
```

## 10. 错误返回

未登录：

```json
{
  "code": 401,
  "message": "请先登录",
  "data": null
}
```

无权限：

```json
{
  "code": 403,
  "message": "无权限访问",
  "data": null
}
```

访问其他用户数据：

```json
{
  "code": 403,
  "message": "不能访问其他用户的数据",
  "data": null
}
```

## 11. 当前设计取舍

当前没有实现管理员角色，所以发货接口和用户枚举接口先用 `denyAll` 禁止访问。

这样做的原因：

- 普通用户不应该能发货。
- 普通用户不应该能枚举所有用户。
- 当前阶段重点是用户端流程和后端质量，不扩展管理后台范围。

后续如果做管理后台，可以继续扩展：

- 用户表增加角色字段。
- JWT 中加入角色 claim。
- SecurityConfig 按角色控制接口。
- 发货接口只允许管理员调用。

## 12. 后续扩展

可以继续补：

- 管理员角色
- 刷新 token
- token 黑名单或登出机制
- Swagger/Knife4j 文档
- 真实微信登录或账号密码登录

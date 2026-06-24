# JWT 认证设计

## 1. 目的

JWT 用于解决“谁在调用接口”的问题。登录成功后，后端签发 token，前端后续请求统一携带 token。

## 2. 关键词解释

| 词 | 含义 |
| --- | --- |
| `JWT` | JSON Web Token，登录凭证格式 |
| `Bearer` | 持有者模式，表示谁带着 token，谁就是当前登录用户 |
| `Authorization` | HTTP 请求头，专门放认证信息 |
| `SecurityContext` | Spring Security 保存当前登录状态的上下文 |
| `SecurityContextHolder` | Spring Security 提供的上下文入口 |
| `current` | 当前登录用户自己的数据，不再依赖前端传 `userId` |

## 3. 登录链路

1. 前端提交 `openid`、昵称、头像。
2. 后端创建或更新用户。
3. 后端生成 JWT。
4. 后端返回 `tokenType`、`token`、`expiresIn`、`user`。
5. 前端把 token 保存起来。
6. 后续请求统一带上 `Authorization: Bearer {token}`。

## 4. 当前实现

### 4.1 登录接口

```http
POST /api/users/login
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
    "user": { }
  }
}
```

### 4.2 受保护接口

当前已保护：

- 地址接口
- 购物车接口
- 订单接口
- 用户当前信息接口

### 4.3 当前用户接口

现在优先使用：

- `/api/users/current`
- `/api/addresses/current`
- `/api/carts/current`
- `/api/orders/current`

这些接口都从 token 里识别当前用户。

### 4.4 配置项

```properties
app.jwt.secret=${JWT_SECRET:freshfood-v2-jwt-secret-key-must-be-at-least-32-bytes}
app.jwt.issuer=${JWT_ISSUER:freshfood-backend}
app.jwt.expires-in=${JWT_EXPIRES_IN:7200}
```

说明：

- `JWT_SECRET`：签名密钥，部署时应使用环境变量覆盖。
- `JWT_ISSUER`：签发者，用于标识 token 来源。
- `JWT_EXPIRES_IN`：过期时间，单位是秒。

## 5. 设计原则

1. 不让前端随意传 `userId`。
2. 当前用户数据走 `current` 路由。
3. 旧接口保留兼容，但必须校验 token 用户是否匹配。
4. 错误统一返回 `ApiResponse`。

## 6. 后续扩展

后面可以继续补：

- 管理员角色
- 接口级权限控制
- 刷新 token
- Swagger/Knife4j 文档

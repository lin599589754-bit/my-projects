# 脚本目录

这里用于保存项目启动和维护脚本。

当前脚本：

- `start-mysql-v2.ps1`：启动 D 盘独立 MySQL 实例。
- `stop-mysql-v2.ps1`：停止 D 盘独立 MySQL 实例。
- `reset-database.ps1`：重建 `fresh_delivery_v2` 数据库。

## 使用方式

在项目根目录执行：

```powershell
.\scripts\start-mysql-v2.ps1
```

停止 MySQL：

```powershell
.\scripts\stop-mysql-v2.ps1
```

重建数据库并导入演示数据：

```powershell
.\scripts\reset-database.ps1
```

## 数据库连接

```text
Host: 127.0.0.1
Port: 3307
Database: fresh_delivery_v2
Username: root
Password: 空
```

## 计划补充

- `start-backend.bat`：启动后端。
- `start-admin.bat`：启动管理后台。

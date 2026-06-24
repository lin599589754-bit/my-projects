# 脚本说明

本目录用于放置项目本地开发辅助脚本。

当前项目主要操作仍以手动命令为主，暂未放置复杂脚本。

## 常用命令

进入后端目录：

```powershell
cd D:\Projects\FreshFoodSystem-v2\backend
```

启动后端：

```powershell
.\mvnw.cmd spring-boot:run
```

运行测试：

```powershell
.\mvnw.cmd test
```

查看 Git 状态：

```powershell
git status -sb
```

查看最近提交：

```powershell
git log --oneline -5
```

## 后续可补充脚本

后续如果项目继续扩展，可以补充：

- 一键启动 MySQL 容器或本地 MySQL 服务
- 一键初始化数据库
- 一键启动后端
- 一键运行测试
- 一键打包后端

当前阶段不强行增加脚本，避免项目复杂度超过实际需要。

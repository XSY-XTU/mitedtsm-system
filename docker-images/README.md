# docker-images

离线 Docker 镜像目录。

## 为什么这里是空的

本目录原本存放项目所需的基础镜像离线包（`.tar`），单文件体积 85 MB ~ 550 MB，
合计约 **1.7 GB**，远超 GitHub 单文件 100 MB 的限制，因此 **不纳入版本库**。

## 获取镜像的方式

### 方式一：在线拉取（推荐）

```bash
docker pull eclipse-temurin:17-jdk
docker pull mysql:8.0
docker pull redis:6-alpine
docker pull rabbitmq:3-management-alpine
docker pull tdengine/tdengine:3.3.6.0
docker pull nginx:stable-alpine
```

### 方式二：使用离线包（内网 / 无外网环境）

将离线包放入本目录后执行：

```powershell
Get-ChildItem -Filter *.tar | ForEach-Object {
    Write-Host ">>> 正在导入 $($_.Name)"
    docker load -i $_.FullName
}
```

## 需要哪些镜像

| 镜像 | 用途 |
| --- | --- |
| `eclipse-temurin:17-jdk` | 后端构建 / 运行时基础镜像 |
| `mysql:8.0` | 关系型数据库 |
| `redis:6-alpine` | 缓存与分布式锁 |
| `rabbitmq:3-management-alpine` | 消息队列 |
| `tdengine/tdengine:3.3.6.0` | 时序数据库（IoT 模块） |
| `nginx:stable-alpine` | 前端静态资源服务 |
| `mitedtsm-server:latest` | 后端应用镜像（由 `docker-compose/Dockerfile.server` 构建） |
| `mitedtsm-init-service:latest` | TDengine 初始化服务（由 `docker-compose/Dockerfile.init` 构建） |

> 应用镜像通过 `cd docker-compose && docker compose up -d --build` 自动构建，
> 详见 [../docker-compose/DOCKER_COMPOSE_PACK.md](../docker-compose/DOCKER_COMPOSE_PACK.md)。

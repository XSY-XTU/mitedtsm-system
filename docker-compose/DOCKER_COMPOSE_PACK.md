# Docker Compose 部署指南

本文档详细说明如何使用 Docker Compose 部署整个芋道项目。

---

## 目录结构

```
docker-compose/
├── docker-compose.yml    # 主编排文件，定义6个服务
├── .env                  # 环境变量配置
├── nginx/
│   ├── web.conf          # Web管理后台的Nginx配置
│   └── mall.conf         # Mall商城前端的Nginx配置
├── init/
│   ├── init-mysql.sh     # MySQL数据库初始化脚本
│   └── init-tdengine.sh  # TDengine数据库初始化脚本
└── Dockerfile.server     # Server后端服务的Dockerfile
```

---

## 服务概览

| 服务名 | 容器名 | 镜像 | 端口 | 说明 |
|--------|--------|------|------|------|
| mysql | mitedtsm-mysql | mysql:8 | 3306 | MySQL数据库 |
| redis | mitedtsm-redis | redis:6-alpine | 6379 | Redis缓存 |
| tdengine | mitedtsm-tdengine | tdengine/tdengine:3.3.6.0 | 6041 | TDengine时序数据库 |
| server | mitedtsm-server | 自建 | 8080 | Spring Boot后端服务 |
| web | mitedtsm-web | nginx:alpine | 80 | 管理后台前端(Vue3) |
| mall | mitedtsm-mall | nginx:alpine | 81 | 商城H5前端(uni-app) |

---

## 文件说明

### 1. docker-compose.yml

**作用**: Docker Compose 主配置文件，定义了所有服务的编排方式。

**关键配置**:
- 服务依赖关系：server 依赖 mysql、redis、tdengine 健康检查通过后才启动
- 健康检查：每个服务都配置了 healthcheck
- 数据持久化：使用 Docker volumes 持久化 MySQL、Redis、TDengine 数据
- 网络隔离：所有服务在同一个 bridge 网络中通信

### 2. .env

**作用**: 环境变量配置文件，用于集中管理所有服务的配置参数。

**可修改项**:
```bash
# MySQL
MYSQL_ROOT_PASSWORD=1234          # MySQL root密码
MYSQL_DATABASE=ruoyi-vue-pro      # 数据库名

# TDengine
TDENGINE_PASSWORD=taosdata        # TDengine密码
TDENGINE_DATABASE=ruoyi_vue_pro   # TDengine数据库名

# Server
JAVA_OPTS=-Xms512m -Xmx512m       # JVM内存配置

# 端口（如需修改）
WEB_PORT=80
MALL_PORT=81
```

### 3. nginx/web.conf

**作用**: Web 管理后台前端的 Nginx 配置。

**功能**:
- 静态资源服务：部署 Vue3 构建产物
- API 反向代理：`/admin-api/*` → `server:8080/admin-api/*`
- WebSocket 支持：`/infra/ws` 代理
- History 路由模式支持
- Gzip 压缩

### 4. nginx/mall.conf

**作用**: Mall 商城前端的 Nginx 配置。

**功能**:
- 静态资源服务：部署 uni-app H5 构建产物
- API 反向代理：`/app-api/*` → `server:8080/app-api/*`
- WebSocket 支持
- History 路由模式支持

### 5. init/init-mysql.sh

**作用**: MySQL 容器启动时自动执行的初始化脚本。

**功能**:
- 按指定顺序执行 `database/` 目录下的所有 SQL 文件
- 执行顺序：
  1. `base/quartz.sql`
  2. `base/ruoyi-vue-pro.sql`
  3. `base/` 其余 SQL 文件（按字母顺序）
  4. `new/` 目录下所有 SQL 文件

**注意**: 仅在首次启动且数据库为空时执行。

### 6. init/init-tdengine.sh

**作用**: TDengine 容器启动时自动执行的初始化脚本。

**功能**:
- 创建 `ruoyi_vue_pro` 数据库

### 7. Dockerfile.server

**作用**: 构建 Server 后端服务的 Docker 镜像。

**基础镜像**: `eclipse-temurin:21-jre`

**构建要求**: 需要先在 Server 目录执行 Maven 打包

---

## 打包指南

### 1. MySQL 数据库

**不需要打包**，直接使用官方镜像 `mysql:8`。

**初始化数据**: 启动时通过 `init/init-mysql.sh` 脚本按顺序执行 `database/` 目录下所有 SQL 文件：
- `database/base/quartz.sql` - 定时任务表
- `database/base/ruoyi-vue-pro.sql` - 主表结构
- `database/base/` 其余模块 SQL - AI、BPM、CRM、ERP、IoT、Mall、Member、MP、Pay 等
- `database/new/new-large-file-upload.sql` - 新增功能表

### 2. Redis 缓存

**不需要打包**，直接使用官方镜像 `redis:6-alpine`。

### 3. TDengine 时序数据库

**不需要打包**，直接使用官方镜像 `tdengine/tdengine:3.3.6.0`。

**初始化**: 通过 `init/init-tdengine.sh` 自动创建数据库。

### 4. Server 后端服务

**步骤**:

```bash
# 1. 进入 Server 目录
cd D:\Data\product\Server

# 2. Maven 打包（跳过测试）
mvn clean package -DskipTests

# 3. 确认 JAR 文件生成
# 位置: Server/mitedtsm-server/target/mitedtsm-server.jar

# 4. 构建 Docker 镜像（由 docker-compose 自动执行）
# 或手动构建:
docker build -t mitedtsm-server:latest -f ../docker-compose/Dockerfile.server .
```

**注意**: 如果使用 Java 17，需要修改 Dockerfile.server 中的基础镜像为 `eclipse-temurin:17-jre`。

### 5. Web 前端

**步骤**:

```bash
# 1. 进入 Web 目录
cd D:\Data\product\Web

# 2. 安装依赖
pnpm install

# 3. 生产环境打包
pnpm build:prod

# 4. 确认构建产物
# 位置: Web/dist-prod/
```

**部署**: 构建产物会被挂载到 nginx 容器，**不需要单独构建 Docker 镜像**。

### 6. Mall 前端

**步骤**:

```bash
# 1. 使用 HBuilderX 打开 MallFrontend 项目

# 2. 发行 -> 网站-H5手机版

# 3. 确认构建产物
# 位置: MallFrontend/unpackage/dist/build/web/
```

**部署**: 构建产物会被挂载到 nginx 容器，**不需要单独构建 Docker 镜像**。

---

## 运行指南

### 首次启动

```bash
# 进入 docker-compose 目录
cd D:\Data\product\docker-compose

# 启动所有服务（后台运行）
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看所有日志
docker-compose logs -f

# 查看单个服务日志
docker-compose logs -f server
```

### 服务访问地址

| 服务 | 地址 |
|------|------|
| Web 管理后台 | http://localhost |
| Mall 商城 | http://localhost:81 |
| Server API | http://localhost:8080 |
| MySQL | localhost:3306 |
| Redis | localhost:6379 |
| TDengine | localhost:6041 |

### 常用命令

```bash
# 停止所有服务
docker-compose down

# 停止并删除数据卷（清除所有数据）
docker-compose down -v

# 重启单个服务
docker-compose restart server

# 重新构建 server 镜像
docker-compose build server

# 重新构建并启动
docker-compose up -d --build server

# 查看服务状态
docker-compose ps

# 进入容器
docker exec -it mitedtsm-mysql bash
docker exec -it mitedtsm-server sh

# 查看容器资源使用
docker stats
```

### 单独启动某个服务

```bash
# 只启动基础设施（MySQL、Redis、TDengine）
docker-compose up -d mysql redis tdengine

# 只启动后端
docker-compose up -d server

# 只启动前端
docker-compose up -d web mall
```

---

## 数据持久化

数据存储在 Docker volumes 中：

| Volume | 说明 |
|--------|------|
| mysql_data | MySQL 数据文件 |
| redis_data | Redis 数据文件 |
| tdengine_data | TDengine 数据文件 |

**备份数据**:

```bash
# 备份 MySQL
docker exec mitedtsm-mysql mysqldump -uroot -p1234 ruoyi-vue-pro > backup.sql

# 备份 Redis
docker exec mitedtsm-redis redis-cli BGSAVE
docker cp mitedtsm-redis:/data/dump.rdb ./redis-backup.rdb
```

**恢复数据**:

```bash
# 恢复 MySQL
docker exec -i mitedtsm-mysql mysql -uroot -p1234 ruoyi-vue-pro < backup.sql

# 恢复 Redis
docker cp ./redis-backup.rdb mitedtsm-redis:/data/dump.rdb
docker-compose restart redis
```

---

## 故障排查

### Server 启动失败

1. 检查 MySQL、Redis、TDengine 是否健康：
   ```bash
   docker-compose ps
   ```

2. 查看详细日志：
   ```bash
   docker-compose logs server
   ```

3. 常见原因：
   - 数据库连接失败：等待 MySQL 完全启动
   - TDengine 连接失败：检查数据库是否创建成功

### 前端无法访问后端 API

1. 检查 server 服务是否正常运行
2. 检查 nginx 配置是否正确
3. 进入 nginx 容器测试：
   ```bash
   docker exec -it mitedtsm-web sh
   wget -qO- http://server:8080/admin-api/system/auth/get-permission-info
   ```

### TDengine 404 错误

确保数据库已创建：
```bash
docker exec -it mitedtsm-tdengine taos -s "SHOW DATABASES;"
```

如果 `ruoyi_vue_pro` 不存在，手动创建：
```bash
docker exec -it mitedtsm-tdengine taos -s "CREATE DATABASE IF NOT EXISTS ruoyi_vue_pro;"
```

---

## 生产环境建议

1. **修改默认密码**：修改 `.env` 中的 `MYSQL_ROOT_PASSWORD` 和 `TDENGINE_PASSWORD`

2. **调整 JVM 内存**：根据服务器配置调整 `JAVA_OPTS`

3. **配置 HTTPS**：在 nginx 配置中添加 SSL 证书

4. **数据备份**：定期备份 MySQL 和 Redis 数据

5. **日志管理**：配置日志轮转避免磁盘占满

6. **资源限制**：在 docker-compose.yml 中添加资源限制：
   ```yaml
   deploy:
     resources:
       limits:
         cpus: '2'
         memory: 2G
   ```

<div align="center">

# MITEDTSM · 企业级综合管理平台

**基于 RuoYi-Vue-Pro 深度定制的企业级多模块管理平台**

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-brightgreen?logo=springboot&logoColor=white)
![Vue](https://img.shields.io/badge/Vue-3.5-42b883?logo=vuedotjs&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue)

</div>

---

## 📖 项目简介

**MITEDTSM** 是一套面向企业的多模块综合管理平台，以 [RuoYi-Vue-Pro](https://github.com/YunaiV/ruoyi-vue-pro) 为底座深度定制，采用前后端分离架构，提供**开箱即用的企业级基础能力**与**可插拔的业务模块**。

平台将认证鉴权、多租户、数据权限、工作流、消息、定时任务、文件存储、代码生成等通用能力沉淀到框架层，业务模块只关注自身领域逻辑，可按需挂载或裁剪。

| 维度 | 说明 |
| --- | --- |
| **架构形态** | 后端 Maven 多模块 + 前端多应用（管理后台 / 商城 / 移动端） |
| **可插拔设计** | `mitedtsm-server` 是唯一的启动入口与装配中心，其余业务模块均为可拆卸组件 |
| **多租户** | 共享数据库 + `tenant_id` 隔离（MyBatis-Plus 拦截器） |
| **国际化** | 内置中 / 英 / 阿 / 德 / 法 / 意 / 葡 / 俄 多语言能力 |
| **部署** | Docker Compose 一键部署，或本地开发 + 容器化中间件 |

---

## ✨ 核心特性

- **统一认证与授权** — OAuth2 双 Token（accessToken / refreshToken），支持多终端登录；RBAC 权限模型 + `@PreAuthorize` 按钮级控制。
- **多租户与数据权限** — `TenantBaseDO` + SQL 自动拦截实现租户隔离，`@DataPermission` 实现行级数据过滤。
- **工作流引擎** — 集成 Flowable，支持自定义流程、审批流转与表单绑定。
- **代码生成器** — 表结构一键生成 Controller / Service / Mapper / DO / VO 与前端页面，显著缩短 CRUD 开发周期。
- **操作日志与审计** — `@LogRecord` 注解式业务日志，自动记录操作人、入参、结果。
- **导入导出** — 基于 FastExcel 的注解式 Excel 导入导出。
- **其他基础能力** — 定时任务、消息队列、分布式锁、文件存储、API 加解密、接口限流与防重放、短信与邮件。

---

## 🧩 业务模块

| 模块 | 说明 |
| --- | --- |
| `mitedtsm-module-system` | 核心系统：认证、用户、角色、菜单、部门、字典、租户、站内信 |
| `mitedtsm-module-infra` | 基础设施：文件存储、配置管理、代码生成、API 日志、定时任务 |
| `mitedtsm-module-bpm` | 工作流：Flowable 流程定义、任务审批、表单 |
| `mitedtsm-module-crm` | 客户关系：客户、联系人、商机、合同、回款、**工单** |
| `mitedtsm-module-erp` | 企业资源：采购、销售、库存、财务 |
| `mitedtsm-module-mall` | 电商商城：商品、交易、营销、统计 |
| `mitedtsm-module-member` | 会员中心：用户、等级、积分、签到 |
| `mitedtsm-module-pay` | 统一支付：支付渠道、退款、转账 |
| `mitedtsm-module-mp` | 微信公众号：账号、素材、菜单、消息 |
| `mitedtsm-module-ai` | AI 能力：大模型接入、知识库、对话 |
| `mitedtsm-module-report` | 报表中心：积木报表、大屏 |
| `mitedtsm-module-iot` | 物联网：设备接入、时序数据（TDengine） |

> 模块按需引入：在 `mitedtsm-server/pom.xml` 中增删依赖即可完成挂载 / 卸载。

---

## 🛠 技术栈

**后端**

| 组件 | 版本 |
| --- | --- |
| JDK | 17 |
| Spring Boot | 3.5.9 |
| Maven | 3.9+（多模块） |
| MyBatis-Plus / Druid | 数据访问与连接池 |
| Flowable | 工作流引擎 |
| Spring Security + OAuth2 | 认证授权 |

**前端**

| 应用 | 技术栈 |
| --- | --- |
| `Web`（管理后台） | Vue 3.5 + Vite 5 + TypeScript + Element Plus + Pinia |
| `MallFrontend`（商城 / 移动端） | uni-app（需 HBuilderX） |

**中间件**

| 组件 | 版本 | 默认端口 |
| --- | --- | --- |
| MySQL | 8.0 | 3306 |
| Redis | 6 | 6379 |
| RabbitMQ | 3（management） | 5672 / 15672 |
| TDengine | 3.3.6.0 | 6041 |

---

## 📁 目录结构

```text
.
├── Server/                       # Java 后端（Maven 多模块）
│   ├── mitedtsm-dependencies/    # 依赖版本统一管理
│   ├── mitedtsm-framework/       # 框架层（Web / Security / MyBatis / Redis / MQ / Job / Tenant 等 Starter）
│   ├── mitedtsm-module-*/        # 业务模块（system / infra / bpm / crm / erp / mall / ...）
│   ├── mitedtsm-server/          # 启动模块：唯一入口 + 全部 application 配置
│   └── sql/                      # 多数据库方言初始化脚本
├── Web/                          # 管理后台前端（Vue 3 + Vite）
├── MallFrontend/                 # 商城 / 移动端前端（uni-app）
├── InitService/                  # TDengine 数据库初始化服务（一次性执行后退出）
├── database/                     # 按模块拆分的 DDL / 初始化 SQL
│   ├── base/                     # 系统基础数据
│   ├── new/                      # 业务增量数据
│   └── replace-en/               # 多语言数据替换（手动执行）
├── dev/                          # 本地开发环境 docker-compose
├── docker-compose/               # 生产部署编排、Nginx 与镜像构建文件
└── docs/                         # 开发与国际化指引
```

---

## 🚀 快速开始

### 环境要求

| 工具 | 版本 |
| --- | --- |
| JDK | 17 |
| Maven | 3.9+ |
| Node.js | 18+ |
| pnpm | 8+ |
| Docker | 24+（含 Docker Compose） |

> 商城端 `MallFrontend` 使用 uni-app，需安装 [HBuilderX](https://www.dcloud.io/hbuilderx.html)。

### 方式一：本地开发（推荐）

```bash
# 1. 启动中间件（MySQL / Redis / RabbitMQ / TDengine + 初始化服务）
cd dev
docker compose up -d

# 2. 打包并启动后端
cd ../Server
mvn clean package -DskipTests
java -jar mitedtsm-server/target/mitedtsm-server.jar --spring.profiles.active=local

# 3. 启动前端（另开一个终端）
cd ../Web
pnpm install
pnpm dev
```

启动成功后访问 <http://localhost>。

### 方式二：Docker Compose 一键部署

```bash
# 1. 离线导入基础镜像（首次执行）
cd docker-images
Get-ChildItem -Filter *.tar | ForEach-Object { docker load -i $_.FullName }

# 2. 构建并启动全部服务
cd ../docker-compose
docker compose up -d --build
```

> 详细步骤、端口释放、常见问题排查见 **[启动步骤.md](./启动步骤.md)** 与 **[README_BY_3031.md](./README_BY_3031.md)**。

### 默认账号

| 项目 | 值 |
| --- | --- |
| 访问地址 | <http://localhost> |
| 租户 | `密讯` |
| 用户名 | `admin` |
| 密码 | `admin123` |

---

## 📚 文档

| 文档 | 说明 |
| --- | --- |
| [启动步骤.md](./启动步骤.md) | 本地环境从零启动的完整步骤与排错手册 |
| [README_BY_3031.md](./README_BY_3031.md) | 打包、部署与环境说明 |
| [EDIT_GUIDE_BY_3031.md](./EDIT_GUIDE_BY_3031.md) | 二次开发修改指引 |
| [docs/develop](./docs/develop) | 开发规范与常见问题 |
| [docs/language](./docs/language) | 国际化添加指引 |
| [docker-compose/DOCKER_COMPOSE_PACK.md](./docker-compose/DOCKER_COMPOSE_PACK.md) | Docker 镜像打包说明 |

> `docker-images/*.tar` 为数百 MB 级的基础镜像文件，**不纳入版本库**，请自行通过 `docker pull` 或离线包获取，参见 [docker-images/README.md](./docker-images/README.md)。

---

## 🤝 参与贡献

1. Fork 本仓库并创建特性分支：`git checkout -b feature/your-feature`
2. 提交改动：`git commit -m 'feat(module): 说明'`
3. 推送分支并创建 Pull Request

提交信息遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范。

---

## 📄 开源协议

本项目基于 [MIT](./LICENSE) 协议开源。

本项目以 MIT 协议的 [RuoYi-Vue-Pro](https://github.com/YunaiV/ruoyi-vue-pro) 为基础深度定制开发，原始版权声明予以保留。

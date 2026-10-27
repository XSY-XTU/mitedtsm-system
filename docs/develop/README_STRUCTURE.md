# 项目详细结构

> 本文档详细讲解项目的架构设计、模块划分和代码组织，帮助开发者快速定位代码位置。

---

## 零、技术栈
### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 编程语言 |
| Spring Boot | 3.5.9 | 应用框架 |
| Spring Security | 6.x | 安全框架 |
| MyBatis Plus | 3.5.15 | ORM框架 |
| MySQL | 5.7+ / 8.x | 主数据库 |
| TDengine | 3.x | IoT时序数据库 |
| Redis | 6.x | 缓存、分布式锁 |
| Redisson | 3.52.0 | Redis客户端 |
| RocketMQ / Kafka / RabbitMQ | - | 消息队列（可选） |
| Flowable | 7.2.0 | 工作流引擎 |
| Knife4j | 4.5.0 | API文档 |

### 前端技术栈

| 项目 | 框架 | UI组件库 | 构建工具 |
|------|------|----------|----------|
| Web（管理后台） | Vue 3.5 + TypeScript 5.3 | Element Plus 2.11 | Vite 5.1 |
| MallFrontend（商城） | uni-app + Vue 3 | 自定义UI | Vite 5.x |
| LargeFileHandler | Vue 3 + TypeScript | Element Plus | Vite |

## 一、项目整体架构

### 1.1 系统架构图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              用户层 (Client)                                 │
├─────────────────────┬─────────────────────┬─────────────────────────────────┤
│   Web 管理后台       │   Mall 商城前端      │   微信小程序/App                │
│   (Vue3 + Element)  │   (uni-app)         │   (uni-app)                    │
└─────────┬───────────┴─────────┬───────────┴─────────────────────────────────┘
          │                     │
          │  HTTP/HTTPS         │  HTTP/HTTPS
          ▼                     ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           网关层 (Nginx)                                     │
│                    /admin-api  →  后端服务:8080                            │
│                    /app-api    →  后端服务:8080                            │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                        后端服务 (Spring Boot)                                │
│                        mitedtsm-server :8080                               │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │   System    │ │   Member    │ │    Mall     │ │     BPM     │           │
│  │  系统管理   │ │  会员中心   │ │   商城系统  │ │   工作流    │           │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘           │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │     Pay     │ │     CRM     │ │     ERP     │ │     AI      │           │
│  │   支付服务  │ │  客户管理   │ │  企业资源   │ │   大模型    │           │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘           │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐                           │
│  │     MP      │ │    Report   │ │     IoT     │                           │
│  │  微信公众号 │ │    报表     │ │   物联网    │                           │
│  └─────────────┘ └─────────────┘ └─────────────┘                           │
├─────────────────────────────────────────────────────────────────────────────┤
│                          框架层 (mitedtsm-framework)                        │
│  Web | Security | MyBatis | Redis | Job | MQ | Excel | WebSocket           │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
          ┌───────────────────────────┼───────────────────────────┐
          ▼                           ▼                           ▼
┌──────────────────┐       ┌──────────────────┐       ┌──────────────────┐
│     MySQL        │       │      Redis       │       │   TDengine       │
│    主数据库      │       │   缓存/分布式锁  │       │  IoT时序数据库   │
└──────────────────┘       └──────────────────┘       └──────────────────┘
```

### 1.2 项目目录总览

```
D:\Data\product\
│
├── Server/                      # Java 后端服务
│   ├── mitedtsm-dependencies/   # BOM 依赖版本管理
│   ├── mitedtsm-framework/      # 框架核心层（12个 starter）
│   ├── mitedtsm-server/         # 启动入口模块
│   └── mitedtsm-module-*/       # 业务模块（12个）
│
├── InitService/                 # 初始化服务（独立工程）
│   ├── pom.xml                  # Maven 配置
│   └── src/main/java/           # 纯 Java 程序
│       └── InitService.java     # TDengine 数据库初始化
│
├── Web/                         # 管理后台前端
│   ├── src/                     # 源代码
│   │   ├── api/                 # API 接口定义
│   │   ├── views/               # 页面组件
│   │   ├── store/               # Pinia 状态管理
│   │   └── router/              # 路由配置
│   └── dist-prod/               # 生产构建产物
│
├── MallFrontend/                # 商城前端 (uni-app)
│   ├── pages/                   # 页面（子包结构）
│   ├── sheep/                   # 核心业务代码
│   └── static/                  # 静态资源
│
├── LargeFileHandler/            # 大文件分片上传模块
│   ├── backend/                 # Spring Boot 后端
│   └── frontend/                # Vue 3 前端
│
├── database/                    # 数据库脚本
│   ├── base/                    # 基础 SQL（12个模块）
│   └── new/                     # 增量更新 SQL
│
├── docker-compose/              # Docker 编排
│   ├── docker-compose.yml       # 服务编排
│   ├── Dockerfile.server        # Server 镜像
│   ├── Dockerfile.init          # InitService 镜像
│   ├── .env                     # 环境变量
│   └── nginx/                   # Nginx 配置
│
├── deploy.bat                   # Windows 一键部署脚本
├── deploy.sh                    # Linux/Mac 一键部署脚本
│
└── uploads/                     # 文件上传目录
```

---

## 二、Server 后端详解

### 2.1 模块总览

```
Server/
├── pom.xml                          # 父 POM
├── mitedtsm-dependencies/           # 依赖版本管理（BOM）
│   └── pom.xml
│
├── mitedtsm-framework/              # 框架核心层
│   ├── mitedtsm-common/             # 公共工具类
│   ├── mitedtsm-spring-boot-starter-web/           # Web 层封装
│   ├── mitedtsm-spring-boot-starter-security/      # 安全认证
│   ├── mitedtsm-spring-boot-starter-mybatis/       # 数据库 ORM
│   ├── mitedtsm-spring-boot-starter-redis/         # Redis 缓存
│   ├── mitedtsm-spring-boot-starter-job/           # 定时任务
│   ├── mitedtsm-spring-boot-starter-mq/            # 消息队列
│   ├── mitedtsm-spring-boot-starter-excel/         # Excel 处理
│   ├── mitedtsm-spring-boot-starter-websocket/     # WebSocket
│   ├── mitedtsm-spring-boot-starter-monitor/       # 监控组件
│   ├── mitedtsm-spring-boot-starter-protection/    # 服务保障
│   ├── mitedtsm-spring-boot-starter-test/          # 测试组件
│   ├── mitedtsm-spring-boot-starter-biz-tenant/    # 多租户
│   ├── mitedtsm-spring-boot-starter-biz-data-permission/  # 数据权限
│   └── mitedtsm-spring-boot-starter-biz-ip/        # IP 地址解析
│
├── mitedtsm-server/                 # 启动入口（聚合所有模块）
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/
│       ├── java/.../MitedtsmServerApplication.java
│       └── resources/
│           ├── application.yaml          # 主配置
│           ├── application-local.yaml    # 本地环境
│           ├── application-dev.yaml      # 开发环境
│           └── application-docker.yaml   # Docker 环境
│
└── mitedtsm-module-*/               # 业务模块（见下表）
```

### 2.2 业务模块一览

| 模块 | 包名 | 职责 |
|------|------|------|
| **mitedtsm-module-system** | system | 用户、角色、权限、部门、菜单、字典、邮件、短信 |
| **mitedtsm-module-infra** | infra | 文件存储、代码生成、API 日志、配置管理、WebSocket |
| **mitedtsm-module-member** | member | 会员等级、积分、签到、收藏、地址 |
| **mitedtsm-module-bpm** | bpm | 流程定义、表单设计、审批中心（Flowable） |
| **mitedtsm-module-pay** | pay | 支付渠道、退款、订单 |
| **mitedtsm-module-mall** | mall | 商品、订单、营销、交易、统计 |
| **mitedtsm-module-crm** | crm | 客户、商机、合同、跟进 |
| **mitedtsm-module-erp** | erp | 采购、销售、库存、财务 |
| **mitedtsm-module-ai** | ai | 对话、绘画、写作（多模型支持） |
| **mitedtsm-module-iot** | iot | 设备管理、数据采集（TDengine） |
| **mitedtsm-module-mp** | mp | 微信公众号管理 |
| **mitedtsm-module-report** | report | 数据报表（积木报表） |

### 2.3 典型模块分层结构

每个业务模块遵循统一的分层架构：

```
mitedtsm-module-xxx/
└── src/main/java/com/meession/etm/module/xxx/
    │
    ├── api/                         # 对外 API 接口
    │   ├── XxxApi.java              # API 接口定义
    │   └── dto/                     # DTO 数据传输对象
    │
    ├── controller/                  # 控制器层
    │   ├── admin/                   # 管理后台接口
    │   │   └── user/
    │   │       ├── UserController.java
    │   │       └── vo/              # View Object
    │   │           ├── UserCreateReqVO.java
    │   │           ├── UserUpdateReqVO.java
    │   │           └── UserRespVO.java
    │   └── app/                     # App 端接口
    │
    ├── service/                     # 业务逻辑层
    │   └── user/
    │       ├── UserService.java         # 接口
    │       └── UserServiceImpl.java     # 实现
    │
    ├── dal/                         # 数据访问层
    │   ├── dataobject/              # DO 数据对象
    │   │   └── UserDO.java
    │   ├── mysql/                   # MyBatis Mapper
    │   │   └── UserMapper.java
    │   └── redis/                   # Redis 操作
    │       └── UserRedisDAO.java
    │
    ├── convert/                     # 对象转换（MapStruct）
    │   └── UserConvert.java
    │
    ├── enums/                       # 枚举定义
    │   └── UserTypeEnum.java
    │
    ├── framework/                   # 模块内框架配置
    │   └── XxxConfig.java
    │
    ├── job/                         # 定时任务
    │   └── XxxJob.java
    │
    └── mq/                          # 消息队列
        ├── producer/
        └── consumer/
```

### 2.4 各模块详细说明

#### 2.4.1 mitedtsm-module-system（系统管理）

**核心职责**：提供系统级基础能力

```
mitedtsm-module-system/
├── api/                    # 对外接口
│   ├── user/               # 用户 API
│   ├── role/               # 角色 API
│   ├── permission/         # 权限 API
│   └── dict/               # 字典 API
│
├── controller/admin/
│   ├── user/               # 用户管理
│   ├── role/               # 角色管理
│   ├── menu/               # 菜单管理
│   ├── dept/               # 部门管理
│   ├── dict/               # 字典管理
│   ├── notice/             # 通知公告
│   ├── mail/               # 邮件管理
│   ├── sms/                # 短信管理
│   └── auth/               # 认证授权
│
└── service/
    └── (对应各 controller)
```

**关键文件**：
- `dal/dataobject/user/AdminUserDO.java` - 管理员用户实体
- `service/auth/AdminAuthServiceImpl.java` - 登录认证核心逻辑

#### 2.4.2 mitedtsm-module-mall（商城系统）

**架构特点**：采用多子模块结构，解决循环依赖

```
mitedtsm-module-mall/
├── pom.xml                     # 聚合 POM
│
├── mitedtsm-module-product/    # 商品模块
│   └── spu, sku, category, brand, property
│
├── mitedtsm-module-promotion/  # 营销模块
│   └── coupon, bargain, combination, seckill, reward
│
├── mitedtsm-module-trade/      # 交易模块
│   └── order, cart, after-sale, delivery, brokerage
│
├── mitedtsm-module-statistics/ # 统计模块
│   └── trade, member, product
│
└── mitedtsm-module-trade-api/  # 交易 API（解耦用）
    └── dto, enums
```

**依赖关系**：
```
mitedtsm-module-promotion  →  mitedtsm-module-trade-api
mitedtsm-module-trade      →  mitedtsm-module-promotion
```

#### 2.4.3 mitedtsm-module-bpm（工作流）

**技术**：Flowable 7.2.0

```
mitedtsm-module-bpm/
├── controller/admin/
│   ├── definition/         # 流程定义
│   ├── process-instance/   # 流程实例
│   ├── task/               # 任务管理
│   └── form/               # 表单管理
│
├── service/
│   ├── definition/         # 流程定义服务
│   ├── instance/           # 实例服务
│   └── task/               # 任务服务
│
└── framework/              # Flowable 配置
    └── flowable/
```

#### 2.4.4 mitedtsm-module-ai（AI大模型）

**支持的大模型**：

| 国内 | 国外 |
|------|------|
| 通义千问、文心一言、讯飞星火 | OpenAI、Azure OpenAI |
| 智谱GLM、DeepSeek、豆包 | Claude、Gemini |
| 腾讯混元、百川、MiniMax | Ollama（本地） |

**核心目录**：
```
mitedtsm-module-ai/
├── controller/admin/
│   ├── chat/               # 对话管理
│   ├── image/              # 绘画管理
│   └── write/              # 写作管理
│
├── service/
│   ├── chat/               # 对话服务
│   ├── image/              # 绘画服务
│   └── model/              # 模型管理
│
└── framework/              # AI 框架配置
    └── ai/
```

#### 2.4.5 mitedtsm-module-iot（物联网）

**架构**：三子模块分离

```
mitedtsm-module-iot/
├── mitedtsm-module-iot-biz/       # 业务逻辑
│   └── device, thing, data
│
├── mitedtsm-module-iot-core/      # 核心组件
│   └── protocol, plugin
│
└── mitedtsm-module-iot-gateway/   # 网关服务
    └── mqtt, http
```

### 2.5 模块依赖关系

```
mitedtsm-server (启动入口)
    │
    ├─► mitedtsm-module-system ──► mitedtsm-module-infra ──► mitedtsm-framework
    │                              │
    │                              └── mitedtsm-spring-boot-starter-*
    │
    ├─► mitedtsm-module-member ──► mitedtsm-module-system
    │
    ├─► mitedtsm-module-bpm ──► mitedtsm-module-system
    │
    ├─► mitedtsm-module-pay ──► mitedtsm-module-system
    │
    ├─► mitedtsm-module-mall
    │   ├─► mitedtsm-module-product ──► mitedtsm-module-system
    │   ├─► mitedtsm-module-promotion ──► mitedtsm-module-trade-api
    │   ├─► mitedtsm-module-trade ──► mitedtsm-module-promotion
    │   └─► mitedtsm-module-statistics
    │
    ├─► mitedtsm-module-crm ──► mitedtsm-module-system
    │
    ├─► mitedtsm-module-erp ──► mitedtsm-module-system
    │
    ├─► mitedtsm-module-ai ──► mitedtsm-module-system + mitedtsm-module-infra
    │
    ├─► mitedtsm-module-iot-biz ──► mitedtsm-module-system
    │
    ├─► mitedtsm-module-mp ──► mitedtsm-module-system
    │
    └─► mitedtsm-module-report ──► mitedtsm-module-system
```

---

## 三、Web 管理后台详解

### 3.1 目录结构

```
Web/src/
│
├── api/                        # API 接口定义
│   ├── ai/                     # AI 模块
│   ├── bpm/                    # 工作流
│   ├── crm/                    # CRM
│   ├── erp/                    # ERP
│   ├── infra/                  # 基础设施
│   ├── iot/                    # 物联网
│   ├── login/                  # 登录
│   ├── mall/                   # 商城
│   │   ├── product/            # 商品
│   │   ├── promotion/          # 营销
│   │   └── trade/              # 交易
│   ├── member/                 # 会员
│   ├── mp/                     # 微信公众号
│   ├── pay/                    # 支付
│   └── system/                 # 系统
│       ├── user/               # 用户
│       ├── role/               # 角色
│       ├── menu/               # 菜单
│       └── dict/               # 字典
│
├── assets/                     # 静态资源
│   ├── icons/                  # 图标
│   └── images/                 # 图片
│
├── components/                 # 全局公共组件
│   ├── Table/                  # 表格组件
│   │   └── src/Table.vue
│   ├── Form/                   # 表单组件
│   ├── Dialog/                 # 对话框
│   ├── Editor/                 # 富文本编辑器
│   ├── UploadFile/             # 文件上传
│   ├── DictTag/                # 字典标签
│   ├── Icon/                   # 图标选择器
│   └── bpmnProcessDesigner/    # 流程设计器
│
├── config/axios/               # Axios 配置
│   ├── index.ts                # 导出 request
│   ├── service.ts              # Axios 实例 + 拦截器
│   └── config.ts               # 基础配置
│
├── hooks/                      # 组合式函数
│   ├── web/
│   │   ├── useTable.ts         # 表格操作
│   │   ├── useForm.ts          # 表单操作
│   │   ├── useMessage.ts       # 消息提示
│   │   └── useDict.ts          # 字典
│   └── event/
│
├── layout/                     # 布局组件
│   ├── Layout.vue              # 主布局
│   └── components/
│       ├── Menu/               # 侧边菜单
│       ├── Tabs/               # 标签页
│       └── Breadcrumb/         # 面包屑
│
├── locales/                    # 国际化
│   ├── zh-CN.ts
│   └── en.ts
│
├── plugins/                    # 插件配置
│   ├── elementPlus/            # Element Plus
│   ├── vueI18n/                # 国际化
│   ├── formCreate/             # 表单设计器
│   └── echarts/                # 图表
│
├── router/                     # 路由配置
│   ├── index.ts                # 路由实例
│   └── modules/
│       └── remaining.ts        # 静态路由
│
├── store/                      # Pinia 状态管理
│   ├── index.ts
│   └── modules/
│       ├── user.ts             # 用户状态
│       ├── permission.ts       # 权限/动态路由
│       ├── dict.ts             # 字典缓存
│       ├── tagsView.ts         # 标签页
│       └── app.ts              # 应用状态
│
├── styles/                     # 全局样式
│   ├── index.scss
│   └── variables.scss
│
├── types/                      # TypeScript 类型
│   └── global.d.ts
│
├── utils/                      # 工具函数
│   ├── auth.ts                 # Token 管理
│   ├── dict.ts                 # 字典工具
│   ├── routerHelper.ts         # 路由辅助
│   └── download.ts             # 下载工具
│
├── views/                      # 页面组件
│   ├── Home/                   # 首页
│   ├── Login/                  # 登录
│   ├── system/                 # 系统管理
│   │   ├── user/               # 用户管理
│   │   ├── role/               # 角色管理
│   │   ├── menu/               # 菜单管理
│   │   └── dept/               # 部门管理
│   ├── mall/                   # 商城管理
│   │   ├── product/            # 商品
│   │   ├── promotion/          # 营销
│   │   └── trade/              # 交易
│   ├── bpm/                    # 工作流
│   ├── crm/                    # CRM
│   ├── ai/                     # AI
│   └── iot/                    # 物联网
│
├── App.vue                     # 根组件
├── main.ts                     # 入口文件
└── permission.ts               # 路由权限守卫
```

### 3.2 路由机制

**双路由模式**：静态路由 + 动态路由

```
路由系统
│
├── 静态路由 (remaining.ts)
│   ├── /login              # 登录页
│   ├── /                   # 首页
│   ├── /403, /404, /500    # 错误页
│   └── 其他隐藏路由
│
└── 动态路由（后端返回）
    └── 根据 menus 生成路由
```

**路由 Meta 配置**：
```typescript
meta: {
  hidden: true,          // 不在侧边栏显示
  alwaysShow: true,      // 强制显示根路由
  title: '用户管理',      // 标题
  icon: 'ep:user',       // 图标
  noCache: true,         // 不被 keep-alive 缓存
  breadcrumb: false,     // 不在面包屑显示
  affix: true,           // 固定在标签栏
  activeMenu: '/system'  // 高亮的菜单路径
}
```

### 3.3 API 调用方式

**封装位置**：`src/config/axios/`

**使用示例**：
```typescript
// src/api/system/user/index.ts
import request from '@/config/axios'

// GET 请求
export const getUserPage = (params) => {
  return request.get({ url: '/system/user/page', params })
}

// POST 请求
export const createUser = (data) => {
  return request.post({ url: '/system/user/create', data })
}

// PUT 请求
export const updateUser = (data) => {
  return request.put({ url: '/system/user/update', data })
}

// DELETE 请求
export const deleteUser = (id) => {
  return request.delete({ url: '/system/user/delete?id=' + id })
}
```

### 3.4 状态管理

**Store 模块**：

| Store | 文件 | 职责 |
|-------|------|------|
| user | `store/modules/user.ts` | 用户信息、权限、角色 |
| permission | `store/modules/permission.ts` | 动态路由生成 |
| dict | `store/modules/dict.ts` | 字典数据缓存 |
| tagsView | `store/modules/tagsView.ts` | 标签页管理 |
| app | `store/modules/app.ts` | 应用状态 |

**使用示例**：
```typescript
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()

// 获取用户信息
const userInfo = userStore.user

// 检查权限
const hasPermission = userStore.permissions.has('system:user:create')
```

### 3.5 权限控制

**路由守卫**：`src/permission.ts`

```
用户访问 → 检查 Token → 无 Token → 跳转登录
                      ↓ 有 Token
                 获取用户信息 → 加载权限 → 动态添加路由 → 放行
```

**按钮权限**：
```vue
<el-button v-hasPermi="['system:user:create']">新增</el-button>
<el-button v-hasPermi="['system:user:update']">编辑</el-button>
```

---

## 四、MallFrontend 商城前端详解

> 基于 uni-app 开发，支持 H5、微信小程序、App 等多端。

### 4.1 目录结构

```
MallFrontend/
│
├── pages/                      # 页面（子包结构）
│   │
│   ├── index/                  # 主包（首页、分类、购物车、用户中心）
│   │   ├── index.vue           # 首页
│   │   ├── category.vue        # 分类
│   │   ├── cart.vue            # 购物车
│   │   └── user.vue            # 用户中心
│   │
│   ├── goods/                  # 商品模块（子包）
│   │   ├── list.vue            # 商品列表
│   │   ├── detail.vue          # 商品详情
│   │   └── comment.vue         # 商品评价
│   │
│   ├── order/                  # 订单模块（子包）
│   │   ├── list.vue            # 订单列表
│   │   ├── detail.vue          # 订单详情
│   │   ├── confirm.vue         # 确认订单
│   │   └── after-sale.vue      # 售后
│   │
│   ├── user/                   # 用户模块（子包）
│   │   ├── info.vue            # 个人信息
│   │   ├── address.vue         # 地址管理
│   │   ├── wallet.vue          # 钱包
│   │   └── collection.vue      # 收藏
│   │
│   ├── commission/             # 分销模块（子包）
│   ├── coupon/                 # 优惠券模块（子包）
│   ├── pay/                    # 支付模块（子包）
│   ├── activity/               # 营销活动（子包）
│   ├── chat/                   # 客服模块（子包）
│   ├── app/                    # 应用（子包）
│   └── public/                 # 公共页面（子包）
│
├── sheep/                      # 核心业务代码
│   ├── api/                    # API 接口
│   │   ├── user.js             # 用户相关
│   │   ├── goods.js            # 商品相关
│   │   ├── order.js            # 订单相关
│   │   └── cart.js             # 购物车
│   │
│   ├── components/             # 公共组件
│   │   ├── s-card.vue          # 卡片组件
│   │   ├── s-goods-item.vue    # 商品项组件
│   │   └── s-empty.vue         # 空状态组件
│   │
│   ├── store/                  # Pinia 状态管理
│   │   ├── user.js             # 用户状态
│   │   ├── cart.js             # 购物车状态
│   │   └── app.js              # 应用状态
│   │
│   ├── router/                 # 路由工具
│   │   └── index.js
│   │
│   ├── request/                # 网络请求封装
│   │   └── index.js
│   │
│   ├── helper/                 # 工具函数
│   │   ├── pay.js              # 支付工具
│   │   └── share.js            # 分享工具
│   │
│   ├── config/                 # 配置文件
│   │   └── index.js
│   │
│   ├── hooks/                  # Vue Hooks
│   │
│   ├── platform/               # 平台适配
│   │   ├── weixin.js           # 微信小程序
│   │   ├── h5.js               # H5
│   │   └── app.js              # App
│   │
│   └── ui/                     # UI 组件库
│       ├── s-button.vue
│       └── s-input.vue
│
├── static/                     # 静态资源
│   └── img/                    # 图片
│
├── uni_modules/                # uni-app 插件
│   ├── lime-painter/           # 画布绘制
│   ├── mp-html/                # 富文本
│   └── uni-badge/              # 徽标
│
├── App.vue                     # 应用入口
├── main.js                     # 主入口
├── pages.json                  # 页面路由配置
├── manifest.json               # 应用配置
├── vite.config.js              # Vite 配置
└── .env                        # 环境变量
```

### 4.2 子包结构说明

**主包**（`pages/index/`）：
- 首页、分类、购物车、用户中心
- 这些页面会随主包一起加载

**子包**（其他目录）：
- 按功能模块拆分
- 按需加载，减少首屏体积

**pages.json 配置**：
```json
{
  "pages": [
    { "path": "pages/index/index", "style": { ... } }
  ],
  "subPackages": [
    {
      "root": "pages/goods",
      "pages": [
        { "path": "list", "style": { ... } },
        { "path": "detail", "style": { ... } }
      ]
    }
  ]
}
```

### 4.3 核心代码说明

**API 调用**（`sheep/api/`）：
```javascript
// sheep/api/goods.js
import request from '@/sheep/request'

export default {
  // 商品详情
  detail: (id) => request({ url: '/product/spu/get-detail?id=' + id }),
  // 商品列表
  list: (params) => request({ url: '/product/spu/page', method: 'GET', params })
}
```

**状态管理**（`sheep/store/`）：
```javascript
// sheep/store/user.js
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: {},
    isLogin: false
  }),
  actions: {
    async getInfo() {
      const { data } = await userApi.info()
      this.userInfo = data
      this.isLogin = true
    }
  }
})
```

### 4.4 多端适配

**平台判断**：
```javascript
// sheep/platform/index.js
// #ifdef MP-WEIXIN
export * from './weixin'
// #endif

// #ifdef H5
export * from './h5'
// #endif

// #ifdef APP-PLUS
export * from './app'
// #endif
```

---

## 五、LargeFileHandler 大文件处理详解

### 5.1 目录结构

```
LargeFileHandler/LargeFileHandler/
│
├── backend/                    # Spring Boot 后端
│   └── src/main/java/com/filehandler/
│       ├── controller/
│       │   └── FileUploadController.java
│       ├── service/
│       │   ├── FileUploadService.java
│       │   └── impl/FileUploadServiceImpl.java
│       ├── mapper/
│       │   └── FileChunkMapper.java
│       └── pojo/
│           ├── FileChunk.java
│           └── FileInfo.java
│
├── frontend/                   # Vue 3 前端
│   └── src/
│       ├── api/
│       │   └── upload.js
│       ├── components/
│       │   └── FileUploader.vue
│       └── utils/
│           └── chunkUpload.js
│
├── database/
│   └── init.sql
│
├── uploads/                    # 上传文件存储
│
└── docker/                     # Docker 配置
```

### 5.2 核心功能

| 功能 | 说明 |
|------|------|
| 分片上传 | 默认 5MB 分片 |
| 断点续传 | 支持中断后继续上传 |
| 秒传检测 | MD5 检测文件是否已存在 |
| 进度追踪 | 实时显示上传进度 |

---

## 六、关键配置文件索引

### 后端配置文件

| 文件 | 路径 | 说明 |
|------|------|------|
| 主 POM | `Server/pom.xml` | 依赖版本 |
| 依赖管理 | `Server/mitedtsm-dependencies/pom.xml` | BOM 管理 |
| 主配置 | `Server/mitedtsm-server/src/main/resources/application.yaml` | 通用配置 |
| 本地配置 | `Server/mitedtsm-server/src/main/resources/application-local.yaml` | 本地开发 |
| 开发环境 | `Server/mitedtsm-server/src/main/resources/application-dev.yaml` | 开发服务器 |
| Docker | `Server/mitedtsm-server/src/main/resources/application-docker.yaml` | Docker 部署 |
| 日志配置 | `Server/mitedtsm-server/src/main/resources/logback-spring.xml` | 日志格式 |

### 前端配置文件

| 文件 | 路径 | 说明 |
|------|------|------|
| Web 主配置 | `Web/.env` | 默认配置 |
| Web 本地 | `Web/.env.local` | 本地开发 |
| Web 生产 | `Web/.env.prod` | 生产环境 |
| Mall 配置 | `MallFrontend/.env` | 商城环境 |
| Mall 路由 | `MallFrontend/pages.json` | 页面配置 |

### Docker 配置

| 文件 | 路径 | 说明 |
|------|------|------|
| Compose | `docker-compose/docker-compose.yml` | 服务编排 |
| Server 镜像 | `docker-compose/Dockerfile.server` | 后端服务镜像 |
| Init 镜像 | `docker-compose/Dockerfile.init` | 初始化服务镜像 |
| 环境变量 | `docker-compose/.env` | 环境配置 |
| Web Nginx | `docker-compose/nginx/web.conf` | 管理后台 Nginx |
| Mall Nginx | `docker-compose/nginx/mall.conf` | 商城 Nginx |

---

## 八、InitService 初始化服务详解

### 8.1 模块概述

InitService 是一个独立的 Maven 工程，与 Server 平级，用于在 Docker 环境中初始化 TDengine 数据库。

### 8.2 目录结构

```
InitService/
│
├── pom.xml                              # Maven 配置
│   - 纯 Java 工程（不依赖 Spring Boot）
│   - 使用 maven-shade-plugin 打包 fat jar
│   - 依赖 taos-jdbcdriver:3.7.9
│
└── src/main/java/com/meession/etm/init/
    └── InitService.java                 # 主程序
```

### 8.3 核心功能

| 功能 | 说明 |
|------|------|
| 连接 TDengine | 通过 REST API 方式连接（无需客户端库） |
| 创建数据库 | 创建 `mitedtsm_database` 数据库 |
| 验证数据库 | 确认数据库创建成功 |
| 重试机制 | 最多重试 30 次，每次间隔 2 秒 |

### 8.4 环境变量

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| TDENGINE_HOST | tdengine | TDengine 主机地址 |
| TDENGINE_PORT | 6041 | TDengine REST API 端口 |

### 8.5 工作流程

```
1. 等待所有基础设施服务（MySQL、Redis、RabbitMQ、TDengine）健康
       ↓
2. 连接 TDengine（通过 REST API）
       ↓
3. 创建 mitedtsm_database 数据库
       ↓
4. 验证数据库创建成功
       ↓
5. 退出（exit 0），Server 服务开始启动
```

### 8.6 本地构建

```powershell
cd D:\Data\product\InitService
mvn clean package -DskipTests
```

构建产物：`target/mitedtsm-init-service.jar`

---

## 九、下一步

了解项目结构后，建议阅读：

- **[README_EDIT_GUIDE.md](../../EDIT_GUIDE_BY_3031.md)** - 学习如何修改和新增模块

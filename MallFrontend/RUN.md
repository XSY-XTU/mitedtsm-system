# 芋道商城 - 运行文档

## 项目介绍

**芋道商城** 是一个基于 uni-app 的多端商城项目，支持 H5、微信小程序、App 等多个平台。

| 项目 | 信息 |
|------|------|
| **项目名称** | 芋道商城 |
| **项目标识** | shopro |
| **版本** | v2.4.1 (2026.01) |
| **技术栈** | uni-app + Vue3 + Vite + Pinia |
| **开源协议** | MIT |

---

## 环境准备

### 1. 安装 HBuilderX

本项目主要使用 **HBuilderX** 作为开发工具。

1. 访问 [HBuilderX 官网](https://www.dcloud.io/hbuilderx.html) 下载
2. 建议下载 **App 开发版**，内置 uni-app 编译器
3. 安装完成后启动 HBuilderX

### 2. 安装 Node.js

虽然项目主要通过 HBuilderX 运行，但需要 Node.js 环境来安装依赖。

1. 访问 [Node.js 官网](https://nodejs.org/) 下载并安装
2. 推荐使用 LTS 版本
3. 验证安装：
   ```bash
   node -v
   npm -v
   ```

### 3. 安装项目依赖

在项目根目录下执行：

```bash
npm install
```

---

## 开发运行

### 使用 HBuilderX 运行

1. **打开项目**
   - 启动 HBuilderX
   - 选择 `文件` -> `打开目录` -> 选择本项目根目录

2. **运行到 H5**
   - 点击菜单 `运行` -> `运行到浏览器` -> `Chrome`
   - 开发服务器将在 `http://localhost:3000` 启动

3. **运行到微信小程序**
   - 点击菜单 `运行` -> `运行到小程序模拟器` -> `微信开发者工具`
   - 首次运行需要配置微信开发者工具路径
   - 确保已在微信开发者工具中开启服务端口

4. **运行到手机或模拟器**
   - 点击菜单 `运行` -> `运行到手机或模拟器`
   - 选择已连接的 Android/iOS 设备或模拟器

### 微信开发者工具配置

1. 下载并安装 [微信开发者工具](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)
2. 打开微信开发者工具，进入 `设置` -> `安全设置`
3. 开启 `服务端口`
4. 在 HBuilderX 中配置微信开发者工具路径：
   - `工具` -> `设置` -> `运行配置` -> `微信开发者工具路径`

---

## 构建发布

### H5 发布

1. 点击菜单 `发行` -> `网站-H5手机版`
2. 配置网站标题、域名等信息
3. 点击 `发行`，等待构建完成
4. 构建产物位于 `unpackage/dist/build/h5` 目录

### 微信小程序发布

1. 点击菜单 `发行` -> `小程序-微信`
2. 填写小程序名称、AppID
3. 点击 `发行`，等待构建完成
4. 构建产物位于 `unpackage/dist/build/mp-weixin` 目录
5. 使用微信开发者工具打开该目录进行上传

### App 打包

1. 点击菜单 `发行` -> `原生App-云打包`
2. 配置 Android/iOS 证书信息
3. 点击 `打包`，等待云端打包完成
4. 下载安装包

---

## 环境配置

### .env 文件说明

项目根目录下的 `.env` 文件包含环境变量配置：

```bash
# 后端接口 - 正式环境
SHOPRO_BASE_URL=http://api-dashboard.mitedtsm.iocoder.cn

# 后端接口 - 测试环境（开发时使用）
SHOPRO_DEV_BASE_URL=http://127.0.0.1:8080

# API 路径前缀
SHOPRO_API_PATH=/app-api

# 开发环境端口
SHOPRO_DEV_PORT=3000

# 静态资源地址
SHOPRO_STATIC_URL=http://test.mitedtsm.iocoder.cn

# 租户ID
SHOPRO_TENANT_ID=1

# 是否开启直播功能（0: 关闭, 1: 开启）
SHOPRO_MPLIVE_ON=0
```

### 后端接口配置

- **测试环境**：`http://127.0.0.1:8080`
- **正式环境**：`http://api-dashboard.mitedtsm.iocoder.cn`

开发时默认使用测试环境接口，如需切换，修改 `SHOPRO_DEV_BASE_URL` 或 `SHOPRO_BASE_URL` 的值。

### 微信小程序配置

在 `manifest.json` 中配置微信小程序 AppID：

```json
{
  "mp-weixin": {
    "appid": "wx66186af0759f47c9"
  }
}
```

---

## 常见问题

### 1. H5 路由 history 模式配置

项目使用 `history` 路由模式，部署到服务器时需要配置：

**Nginx 配置示例：**
```nginx
location / {
    try_files $uri $uri/ /index.html;
}
```

### 2. 微信小程序真机预览白屏

- 检查 `manifest.json` 中的 AppID 是否正确
- 确保后端接口已在微信小程序后台配置合法域名
- 检查网络请求是否使用 HTTPS

### 3. App 端微信支付/分享配置

在 `manifest.json` 中配置微信开放平台 AppID：

```json
{
  "app-plus": {
    "distribute": {
      "sdkConfigs": {
        "payment": {
          "weixin": {
            "appid": "wxae7a0c156da9383b",
            "UniversalLinks": ""
          }
        }
      }
    }
  }
}
```

### 4. 依赖安装失败

尝试使用国内镜像：

```bash
npm install --registry=https://registry.npmmirror.com
```

或使用 pnpm：

```bash
npm install -g pnpm
pnpm install
```

---

## 相关链接

| 名称 | 地址 |
|------|------|
| **官方文档** | https://doc.iocoder.cn/ |
| **快速启动** | https://doc.iocoder.cn/quick-start/ |
| **视频教程** | https://doc.iocoder.cn/video/ |
| **在线演示** | https://doc.iocoder.cn/mall-preview/ |
| **HBuilderX 文档** | https://hx.dcloud.net.cn/ |
| **uni-app 文档** | https://uniapp.dcloud.net.cn/ |

---

## 项目结构

```
MallFrontend/
├── pages/              # 页面文件
│   ├── index/          # 主包页面（首页、分类、购物车、用户中心）
│   ├── goods/          # 商品模块
│   ├── order/          # 订单模块
│   ├── user/           # 用户模块
│   ├── commission/     # 分销模块
│   ├── coupon/         # 优惠券模块
│   ├── pay/            # 支付模块
│   ├── activity/       # 营销活动模块
│   └── chat/           # 客服模块
├── sheep/              # 核心模块
│   ├── api/            # API 接口
│   ├── components/     # 公共组件
│   ├── store/          # 状态管理
│   ├── router/         # 路由配置
│   ├── request/        # 网络请求封装
│   └── helper/         # 工具函数
├── static/             # 静态资源
├── uni_modules/        # uni-app 插件
├── App.vue             # 应用入口
├── main.js             # 主入口文件
├── pages.json          # 页面配置
├── manifest.json       # 应用配置
├── vite.config.js      # Vite 配置
└── .env                # 环境变量
```

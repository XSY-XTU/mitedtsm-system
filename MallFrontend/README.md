# 芋道商城前端

基于 uni-app + Vue3 + Vite 技术驱动的在线商城系统，支持 iOS、Android、H5、微信小程序等多个平台。

## 技术栈

- **前端框架**: Vue 3
- **跨平台框架**: uni-app
- **构建工具**: Vite 5
- **状态管理**: Pinia
- **UI组件**: uni-ui

## 环境要求

- Node.js >= 18
- pnpm（推荐）或 npm
- HBuilderX（用于运行和打包）

## 安装运行

```bash
# 安装依赖
pnpm install

# 或使用 npm
npm install
```

**运行方式**：

1. 使用 HBuilderX 打开项目
2. 点击「运行」-> 选择目标平台（浏览器、小程序、App等）

## 环境配置

项目所有环境配置都在根目录的 `.env` 文件中：

### 后端服务器地址配置

```bash
# 开发环境后端地址（本地开发时使用）
SHOPRO_DEV_BASE_URL=http://127.0.0.1:8080

# 生产环境后端地址（打包发布时使用）
SHOPRO_BASE_URL=http://api-dashboard.mitedtsm.iocoder.cn
```

> 修改后端地址只需更改 `SHOPRO_DEV_BASE_URL`（开发）或 `SHOPRO_BASE_URL`（生产）即可。

### 其他配置项

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `SHOPRO_API_PATH` | 后端接口前缀 | `/app-api` |
| `SHOPRO_WEBSOCKET_PATH` | WebSocket 接口前缀 | `/infra/ws` |
| `SHOPRO_DEV_PORT` | 开发环境端口 | `3000` |
| `SHOPRO_STATIC_URL` | 静态资源地址 | `http://test.mitedtsm.iocoder.cn` |
| `SHOPRO_H5_URL` | H5 访问域名 | `http://127.0.0.1:3000` |
| `SHOPRO_TENANT_ID` | 租户ID | `1` |
| `SHOPRO_UPLOAD_TYPE` | 文件上传类型 | `server` |
| `SHOPRO_MPLIVE_ON` | 是否开启直播 | `0` |

## 多平台配置

多平台配置在 `manifest.json` 文件中：

### 微信小程序

修改 `manifest.json` 中的 `mp-weixin.appid`：

```json
"mp-weixin": {
  "appid": "你的微信小程序AppID"
}
```

### 微信支付/分享

修改 `manifest.json` 中 `app-plus.distribute.sdkConfigs` 相关配置：

```json
"oauth": {
  "weixin": {
    "appid": "你的微信AppID",
    "UniversalLinks": "你的UniversalLinks"
  }
},
"payment": {
  "weixin": {
    "appid": "你的微信AppID",
    "UniversalLinks": "你的UniversalLinks"
  }
}
```

## 项目结构

```
MallFrontend/
├── .env                 # 环境配置文件
├── App.vue              # 应用入口组件
├── main.js              # 应用入口
├── pages/               # 页面文件
│   ├── index/           # 首页相关页面
│   ├── goods/           # 商品相关页面
│   ├── order/           # 订单相关页面
│   ├── user/            # 用户相关页面
│   └── ...
├── sheep/               # 核心业务代码
│   ├── api/             # API 接口
│   ├── components/      # 公共组件
│   ├── config/          # 配置文件
│   ├── router/          # 路由相关
│   ├── store/           # 状态管理
│   └── ...
├── static/              # 静态资源
├── uni_modules/         # uni-app 插件
├── manifest.json        # 应用配置
├── pages.json           # 页面路由配置
└── vite.config.js       # Vite 配置
```

## 常见问题

### 1. 运行报错 "Cannot find module 'sheep/scss/index'"

SCSS 导入语句需要添加 `.scss` 扩展名。检查 `App.vue` 中的样式导入：

```scss
/* 错误写法 */
@import '@/sheep/scss/index';

/* 正确写法 */
@import '@/sheep/scss/index.scss';
```

### 2. 修改后端地址后不生效

修改 `.env` 文件后需要：
1. 停止当前运行的项目
2. 重新运行项目

### 3. 微信小程序真机预览白屏

检查 `manifest.json` 中的 `mp-weixin.appid` 是否配置正确。

## 相关链接

- [uni-app 官方文档](https://uniapp.dcloud.net.cn/)
- [Vite 官方文档](https://vitejs.dev/)
- [芋道商城后端文档](https://doc.iocoder.cn/)

# 多语言配置指南

本文档介绍如何为系统的各个模块添加新的语言支持。系统目前支持中文（简体）和英文，以下说明如何添加其他语言。

## 目录

- [架构概览](#架构概览)
- [Web 管理后台添加新语言](#web-管理后台添加新语言)
- [MallFrontend 商城前端添加新语言](#mallfrontend-商城前端添加新语言)
- [Server 后端添加新语言](#server-后端添加新语言)
- [语言包规范](#语言包规范)
- [最佳实践](#最佳实践)

---

## 架构概览

```
┌─────────────────────────────────────────────────────────────┐
│                        用户请求                              │
└─────────────────────────────────────────────────────────────┘
                              │
          ┌───────────────────┼───────────────────┐
          ▼                   ▼                   ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│  Web 管理后台    │ │ MallFrontend   │ │  Server 后端    │
│  (Vue 3 +       │ │ (uni-app +     │ │ (Spring Boot)   │
│   vue-i18n)     │ │  vue-i18n)     │ │                 │
├─────────────────┤ ├─────────────────┤ ├─────────────────┤
│ src/locales/    │ │ locale/         │ │ resources/i18n/ │
│ ├── zh-CN.ts    │ │ ├── zh-CN.json  │ │ ├── messages_   │
│ └── en.ts       │ │ └── en.json     │ │ │   zh_CN.properties
│                 │ │                 │ │ └── messages_   │
│                 │ │                 │ │     en.properties
└─────────────────┘ └─────────────────┘ └─────────────────┘
```

---

## Web 管理后台添加新语言

### 1. 创建语言包文件

在 `Web/src/locales/` 目录下创建新的语言文件，例如日语 `ja.ts`：

```typescript
// Web/src/locales/ja.ts
export default {
  common: {
    inputText: '入力してください',
    selectText: '選択してください',
    login: 'ログイン',
    // ... 其他翻译
  },
  login: {
    welcome: 'システムへようこそ',
    username: 'ユーザー名',
    password: 'パスワード',
    // ... 其他翻译
  },
  // ... 其他模块
}
```

### 2. 修改类型定义

编辑 `Web/types/global.d.ts`，添加新的语言类型：

```typescript
type LocaleType = 'zh-CN' | 'en' | 'ja'  // 添加新语言
```

### 3. 注册语言到 Store

编辑 `Web/src/store/modules/locale.ts`：

```typescript
// 导入 Element Plus 语言包
import ja from 'element-plus/es/locale/lang/ja'

// 添加 Element Plus 语言映射
const elLocaleMap = {
  'zh-CN': zhCn,
  en: en,
  ja: ja  // 添加新语言
}

// 在 localeMap 中添加新语言
localeMap: [
  {
    lang: 'zh-CN',
    name: '简体中文'
  },
  {
    lang: 'en',
    name: 'English'
  },
  {
    lang: 'ja',
    name: '日本語'  // 添加新语言
  }
]
```

### 4. 使用示例

在组件中使用：

```vue
<script setup>
import { useI18n } from '@/hooks/web/useI18n'

const { t } = useI18n()
</script>

<template>
  <div>{{ t('common.login') }}</div>
</template>
```

---

## MallFrontend 商城前端添加新语言

### 1. 创建语言包文件

在 `MallFrontend/locale/` 目录下创建新的 JSON 文件，例如日语 `ja.json`：

```json
{
  "common": {
    "loading": "読み込み中...",
    "success": "成功",
    "confirm": "確認",
    "cancel": "キャンセル"
  },
  "login": {
    "title": "ログイン",
    "username": "ユーザー名",
    "password": "パスワード"
  }
}
```

### 2. 更新语言包导出

编辑 `MallFrontend/locale/index.js`：

```javascript
export default {
  'zh-CN': require('./zh-CN.json'),
  'en': require('./en.json'),
  'ja': require('./ja.json')  // 添加新语言
}
```

### 3. 更新 i18n 配置

编辑 `MallFrontend/sheep/i18n/index.js`：

```javascript
// 更新 getLocaleList 函数
export function getLocaleList() {
  return [
    { code: 'zh-CN', name: '简体中文' },
    { code: 'en', name: 'English' },
    { code: 'ja', name: '日本語' }  // 添加新语言
  ]
}
```

### 4. 更新 manifest.json

编辑 `MallFrontend/manifest.json`：

```json
{
  "locale": "zh-Hans",
  "fallbackLocale": "zh-Hans",
  "locales": [
    "zh-Hans",
    "en",
    "ja"  // 添加新语言
  ]
}
```

### 5. 使用示例

```vue
<script setup>
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
</script>

<template>
  <view>{{ t('common.loading') }}</view>
</template>
```

---

## Server 后端添加新语言

### 1. 创建语言文件

在 `Server/mitedtsm-server/src/main/resources/i18n/` 目录下创建新的 properties 文件，例如日语 `messages_ja.properties`：

```properties
# ========================================
# \u5168\u5C40\u9519\u8BEF\u7801 (0-999)
# ========================================
error.0=\u6210\u529F
error.400=\u30EA\u30AF\u30A8\u30B9\u30C8\u30D1\u30E9\u30E1\u30FC\u30BF\u304C\u4E0D\u6B63\u3067\u3059
error.401=\u30A2\u30AB\u30A6\u30F3\u30C8\u304C\u30ED\u30B0\u30A4\u30F3\u3057\u3066\u3044\u307E\u305B\u3093
error.403=\u3053\u306E\u64CD\u4F5C\u306E\u6A29\u9650\u304C\u3042\u308A\u307E\u305B\u3093
error.404=\u30EA\u30AF\u30A8\u30B9\u30C8\u304C\u898B\u3064\u304B\u308A\u307E\u305B\u3093
error.500=\u30B7\u30B9\u30C6\u30E0\u30A8\u30E9\u30FC

# ========================================
# \u901A\u7528\u30E1\u30C3\u30BB\u30FC\u30B8
# ========================================
common.success=\u6210\u529F
common.failed=\u64CD\u4F5C\u5931\u6557
common.confirm=\u78BA\u8A8D
common.cancel=\u30AD\u30E3\u30F3\u30BB\u30EB
```

> **注意**：properties 文件中的非 ASCII 字符需要使用 Unicode 转义。可以使用 `native2ascii` 工具或 IDE 插件自动转换。

### 2. 语言解析机制

系统已配置 `I18nLocaleResolver`，支持以下方式获取语言：

1. **请求参数**：`?lang=ja`
2. **请求头**：`Accept-Language: ja`
3. **默认语言**：简体中文

### 3. 使用示例

#### 在代码中获取国际化消息

```java
import com.meession.etm.framework.common.util.i18n.I18nUtil;

// 获取消息
String message = I18nUtil.getMessage("common.success");

// 带参数的消息
String message = I18nUtil.getMessage("validation.minLength", new Object[]{"用户名", "6"});

// 根据错误码获取消息
String errorMessage = I18nUtil.getErrorMessage(400);
```

#### 错误码自动国际化

`ErrorCode` 和 `ServiceException` 已支持国际化：

```java
// 创建错误码（msg 作为默认值）
ErrorCode error = new ErrorCode(400, "请求参数不正确");

// 自动获取国际化消息
String i18nMsg = error.getI18nMessage();  // 根据当前语言返回对应翻译

// ServiceException 也支持
ServiceException ex = new ServiceException(error);
String i18nMsg = ex.getI18nMessage();
```

---

## 语言包规范

### 命名规范

| 类型 | 文件命名 | 语言代码示例 |
|------|----------|-------------|
| 简体中文 | zh-CN.ts / zh-CN.json / messages_zh_CN.properties | zh-CN, zh-Hans, zh_CN |
| 英文 | en.ts / en.json / messages_en.properties | en, en-US, en_US |
| 繁体中文 | zh-TW.ts / zh-TW.json / messages_zh_TW.properties | zh-TW, zh-Hant, zh_TW |
| 日语 | ja.ts / ja.json / messages_ja.properties | ja, ja-JP, ja_JP |
| 韩语 | ko.ts / ko.json / messages_ko.properties | ko, ko-KR, ko_KR |

### 层级结构

```
模块.功能.具体项
```

示例：
```
common.success          # 通用 - 成功
common.confirm          # 通用 - 确认
login.username          # 登录 - 用户名
login.password          # 登录 - 密码
user.profile.title      # 用户中心 - 标题
order.status.unpaid     # 订单状态 - 待付款
```

### 错误码规范

后端错误码消息使用 `error.{code}` 格式：

```properties
error.0=成功
error.400=请求参数不正确
error.401=账号未登录
error.403=没有该操作权限
error.404=请求未找到
error.500=系统异常
```

---

## 最佳实践

### 1. 避免硬编码文本

**不推荐：**
```vue
<template>
  <div>用户名</div>
</template>
```

**推荐：**
```vue
<template>
  <div>{{ t('login.username') }}</div>
</template>
```

### 2. 提取文本技巧

使用正则表达式搜索硬编码的中文文本：

```bash
# 搜索 Vue 模板中的中文
grep -r "[\u4e00-\u9fa5]" --include="*.vue" src/

# 搜索 TypeScript 中的中文
grep -r "[\u4e00-\u9fa5]" --include="*.ts" src/
```

### 3. 参数化翻译

使用占位符处理动态内容：

**语言包：**
```json
{
  "validation.minLength": "{0}长度不能少于{1}个字符"
}
```

**使用：**
```javascript
t('validation.minLength', ['用户名', '6'])
// 输出：用户名长度不能少于6个字符
```

### 4. 语言切换持久化

**Web 前端**：使用 `localStorage` 自动持久化（已配置）

**MallFrontend**：使用 `uni.setStorageSync('locale', lang)` 持久化（已配置）

### 5. Element Plus / uni-ui 组件国际化

确保同时配置 UI 组件库的语言包：

**Web (Element Plus)：**
```typescript
import ja from 'element-plus/es/locale/lang/ja'
```

**MallFrontend (uni_modules)：**
各 uni_modules 组件通常有自己的 `i18n/` 目录，需要创建对应的语言文件。

---

## 常见问题

### Q: 为什么切换语言后部分文本没有变化？

A: 检查以下可能原因：
1. 文本是否使用了 `t()` 函数
2. 语言包中是否有对应的 key
3. 是否有缓存问题

### Q: 后端如何获取前端传递的语言？

A: 后端支持两种方式：
1. URL 参数：`?lang=en`
2. HTTP Header：`Accept-Language: en`

### Q: properties 文件中文显示乱码？

A: properties 文件中的非 ASCII 字符需要使用 Unicode 转义：
```properties
# 原始中文
common.success=成功

# 转义后
common.success=\u6210\u529F
```

可以使用 JDK 自带的 `native2ascii` 工具转换：
```bash
native2ascii -encoding UTF-8 messages_zh_CN.txt messages_zh_CN.properties
```

---

## 相关文件索引

### Web 前端
- 语言包：`Web/src/locales/`
- i18n 配置：`Web/src/plugins/vueI18n/index.ts`
- 语言 Store：`Web/src/store/modules/locale.ts`
- 语言切换：`Web/src/hooks/web/useLocale.ts`
- 翻译函数：`Web/src/hooks/web/useI18n.ts`

### MallFrontend
- 语言包：`MallFrontend/locale/`
- i18n 配置：`MallFrontend/sheep/i18n/index.js`
- 语言切换：`MallFrontend/sheep/hooks/useLocale.js`
- manifest 配置：`MallFrontend/manifest.json`

### Server 后端
- 语言文件：`Server/mitedtsm-server/src/main/resources/i18n/`
- i18n 配置：`Server/mitedtsm-framework/mitedtsm-spring-boot-starter-web/src/main/java/com/meession/etm/framework/web/config/I18nAutoConfiguration.java`
- 语言解析：`Server/mitedtsm-framework/mitedtsm-common/src/main/java/com/meession/etm/framework/common/util/i18n/I18nLocaleResolver.java`
- 工具类：`Server/mitedtsm-framework/mitedtsm-common/src/main/java/com/meession/etm/framework/common/util/i18n/I18nUtil.java`

/**
 * 语言包导出入口
 *
 * 支持模块化的语言包结构
 */
import zhCN from './zh-CN'
import en from './en'
import ar from './ar'

export default {
  'zh-CN': zhCN,
  'zh': zhCN,  // 别名，支持 vue-i18n locale 回退
  'en': en,
  'ar': ar
}

// 导出类型定义
export type LocaleCode = 'zh-CN' | 'en' | 'ar'
export type Messages = typeof zhCN

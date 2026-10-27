/**
 * 国际化配置
 * 
 * 基于 uni-app 的国际化方案
 * 支持 Composition API 和命名空间
 */
import { createI18n } from 'vue-i18n'
import messages from '../../locale'

// 顶级命名空间集合
const topLevelNamespaces = new Set([
  'common', 'user', 'menu', 'goods', 'cart',
  'order', 'pay', 'address', 'coupon', 'error',
  'activity', 'commission', 'chat', 'app',
  'http', 'sms', 'orderStatus', 'afterSaleStatus',
  'rewardActivity', 'promotion', 'delivery', 'sharePage', 'subscribe'
])

// 获取存储的语言或默认英文
function getLocale(): string {
  // 1. 优先从本地存储获取
  const storedLocale = uni.getStorageSync('locale')
  if (storedLocale) {
    return storedLocale
  }
  
  // 2. 默认返回英文
  return 'en'
}

// 创建 i18n 实例
const i18n = createI18n({
  legacy: false, // 使用 Composition API 模式
  globalInjection: true, // 全局注入 $t
  locale: getLocale(),
  messages,
  silentTranslationWarn: true,
  missingWarn: false
})

/**
 * 切换语言
 * @param locale - 语言代码，如 'zh-CN', 'en'
 */
export function setLocale(locale: string): void {
  if (!locale) return
  
  // 更新 i18n
  i18n.global.locale.value = locale
  
  // 持久化存储
  uni.setStorageSync('locale', locale)
  
  // 设置 uni-app 语言（影响原生组件）
  uni.setLocale(locale)
}

/**
 * 获取当前语言
 * @returns 当前语言代码
 */
export function getLocaleValue(): string {
  return i18n.global.locale.value
}

/**
 * 获取支持的语言列表
 * @returns 语言列表
 */
export function getLocaleList(): Array<{ code: string; name: string }> {
  return [
    { code: 'zh-CN', name: '简体中文' },
    { code: 'en', name: 'English' },
    { code: 'ar', name: 'العربية' }
  ]
}

/**
 * 翻译函数
 * @param key - 翻译 key
 * @param params - 参数
 * @returns 翻译后的文本
 */
export function t(key: string, params: Record<string, any> = {}): string {
  return i18n.global.t(key, params)
}

/**
 * useI18n Hook - 支持命名空间功能
 * @param namespace - 命名空间，如 'goods', 'order' 等
 * @returns i18n 相关方法和属性
 * 
 * @example
 * // 不使用命名空间
 * const { t } = useI18n()
 * t('common.loading') // 返回 '加载中...'
 * 
 * @example
 * // 使用命名空间
 * const { t } = useI18n('goods')
 * t('title') // 等价于 t('goods.title')
 * t('common.loading') // 顶级命名空间直接访问
 */
export function useI18n(namespace?: string) {
  const { t } = i18n.global
  
  /**
   * 带命名空间支持的翻译函数
   * - 如果 key 的第一部分是顶级命名空间，直接翻译
   * - 否则，添加命名空间前缀
   */
  const tFn = (key: string, ...args: any[]): string => {
    if (!key) return ''
    
    const firstPart = key.split('.')[0]
    
    // 如果是顶级命名空间，直接使用原 key
    if (topLevelNamespaces.has(firstPart)) {
      return t(key, ...args)
    }
    
    // 否则添加命名空间前缀
    const fullKey = namespace ? `${namespace}.${key}` : key
    return t(fullKey, ...args)
  }
  
  return {
    t: tFn,
    locale: i18n.global.locale,
    setLocale,
    getLocaleValue,
    getLocaleList
  }
}

export default i18n

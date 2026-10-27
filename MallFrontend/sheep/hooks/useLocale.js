/**
 * 语言切换 Hook
 * 
 * 提供语言切换和获取当前语言的功能
 */
import { ref, computed } from 'vue'
import { setLocale, getLocaleValue, getLocaleList } from '../i18n'

// 当前语言
const currentLocale = ref(getLocaleValue())

// 语言列表
const localeList = getLocaleList()

// 当前语言信息
const currentLocaleInfo = computed(() => {
  return localeList.find(item => item.code === currentLocale.value) || localeList[0]
})

/**
 * 使用语言切换 Hook
 */
export function useLocale() {
  /**
   * 切换语言
   * @param {string} locale - 语言代码
   */
  const changeLocale = (locale) => {
    setLocale(locale)
    currentLocale.value = locale
  }

  return {
    // 当前语言代码
    locale: currentLocale,
    // 当前语言信息
    localeInfo: currentLocaleInfo,
    // 语言列表
    localeList,
    // 切换语言
    changeLocale
  }
}

export default useLocale

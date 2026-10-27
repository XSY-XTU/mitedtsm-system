import { i18n } from '@/plugins/vueI18n'
import { useLocaleStoreWithOut } from '@/store/modules/locale'
import { setHtmlPageLang } from '@/plugins/vueI18n/helper'
import { useUserStoreWithOut } from '@/store/modules/user'
import { usePermissionStoreWithOut } from '@/store/modules/permission'
import { CACHE_KEY, useCache } from '@/hooks/web/useCache'

const { wsCache } = useCache()

const setI18nLanguage = (locale: LocaleType) => {
  const localeStore = useLocaleStoreWithOut()

  if (i18n.mode === 'legacy') {
    i18n.global.locale = locale
  } else {
    ;(i18n.global.locale as any).value = locale
  }
  localeStore.setCurrentLocale({
    lang: locale
  })
  setHtmlPageLang(locale)
}

export const useLocale = () => {
  // Switching the language will change the locale of useI18n
  // And submit to configuration modification
  const changeLocale = async (locale: LocaleType) => {
    const globalI18n = i18n.global

    // 支持模块化语言包：优先加载目录结构，回退到单文件
    let langModule
    try {
      langModule = await import(`../../locales/${locale}/index.ts`)
    } catch {
      langModule = await import(`../../locales/${locale}.ts`)
    }

    globalI18n.setLocaleMessage(locale, langModule.default)

    setI18nLanguage(locale)
    
    // 切换语言后，重新获取菜单以支持菜单国际化
    await refreshMenuData()
  }

  return {
    changeLocale
  }
}

/**
 * 刷新菜单数据
 * 用于语言切换后重新获取对应语言的菜单
 */
export const refreshMenuData = async () => {
  // 清除菜单缓存
  wsCache.delete(CACHE_KEY.ROLE_ROUTERS)
  
  // 重新获取用户信息（包含菜单）
  const userStore = useUserStoreWithOut()
  await userStore.setUserInfoAction(true)
  
  // 重新生成路由
  const permissionStore = usePermissionStoreWithOut()
  await permissionStore.generateRoutes()
}


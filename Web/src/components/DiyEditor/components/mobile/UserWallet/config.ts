import { ComponentStyle, DiyComponent } from '@/components/DiyEditor/util'
import { useI18n } from '@/hooks/web/useI18n'

/** 用户资产属性 */
export interface UserWalletProperty {
  // 组件样式
  style: ComponentStyle
}

// 定义组件
export const component = {
  id: 'UserWallet',
  get name() {
    const { t } = useI18n('mall.promotion.diy')
    return t('component.userWallet')
  },
  icon: 'ep:wallet-filled',
  property: {
    style: {
      bgType: 'color',
      bgColor: '',
      marginLeft: 8,
      marginRight: 8,
      marginBottom: 8
    } as ComponentStyle
  }
} as DiyComponent<UserWalletProperty>

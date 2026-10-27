import { DiyComponent } from '@/components/DiyEditor/util'
import { useI18n } from '@/hooks/web/useI18n'

/** 页面设置属性 */
export interface PageConfigProperty {
  // 页面描述
  description: string
  // 页面背景颜色
  backgroundColor: string
  // 页面背景图片
  backgroundImage: string
}

// 定义页面组件
export const component = {
  id: 'PageConfig',
  get name() {
    const { t } = useI18n('mall.promotion.diy')
    return t('component.pageConfig')
  },
  icon: 'ep:document',
  property: {
    description: '',
    backgroundColor: '#f5f5f5',
    backgroundImage: ''
  }
} as DiyComponent<PageConfigProperty>

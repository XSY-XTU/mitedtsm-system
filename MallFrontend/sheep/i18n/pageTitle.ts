/**
 * 页面标题动态设置工具
 * 
 * 根据当前语言自动设置页面标题
 */
import { getLocaleValue, setLocale, t } from './index'

// 页面标题映射表
const pageTitles: Record<string, Record<string, string>> = {
  // 首页模块
  'pages/index/index': { 'zh-CN': '首页', 'en': 'Home' },
  'pages/index/user': { 'zh-CN': '个人中心', 'en': 'Profile' },
  'pages/index/category': { 'zh-CN': '商品分类', 'en': 'Category' },
  'pages/index/cart': { 'zh-CN': '购物车', 'en': 'Cart' },
  'pages/index/login': { 'zh-CN': '登录', 'en': 'Login' },
  'pages/index/search': { 'zh-CN': '搜索', 'en': 'Search' },
  'pages/index/page': { 'zh-CN': '自定义页面', 'en': 'Custom Page' },
  
  // 商品模块
  'pages/goods/index': { 'zh-CN': '商品详情', 'en': 'Product Detail' },
  'pages/goods/groupon': { 'zh-CN': '拼团商品', 'en': 'Group Product' },
  'pages/goods/seckill': { 'zh-CN': '秒杀商品', 'en': 'Flash Sale' },
  'pages/goods/point': { 'zh-CN': '积分商品', 'en': 'Points Product' },
  'pages/goods/list': { 'zh-CN': '商品列表', 'en': 'Product List' },
  'pages/goods/comment/add': { 'zh-CN': '评价商品', 'en': 'Write Review' },
  'pages/goods/comment/list': { 'zh-CN': '商品评价', 'en': 'Product Reviews' },
  
  // 订单模块
  'pages/order/detail': { 'zh-CN': '订单详情', 'en': 'Order Detail' },
  'pages/order/confirm': { 'zh-CN': '确认订单', 'en': 'Confirm Order' },
  'pages/order/list': { 'zh-CN': '我的订单', 'en': 'My Orders' },
  'pages/order/aftersale/apply': { 'zh-CN': '申请售后', 'en': 'Apply After-sale' },
  'pages/order/aftersale/return-delivery': { 'zh-CN': '退货物流', 'en': 'Return Logistics' },
  'pages/order/aftersale/list': { 'zh-CN': '售后列表', 'en': 'After-sale List' },
  'pages/order/aftersale/detail': { 'zh-CN': '售后详情', 'en': 'After-sale Detail' },
  'pages/order/aftersale/log': { 'zh-CN': '售后进度', 'en': 'After-sale Progress' },
  'pages/order/express/log': { 'zh-CN': '物流轨迹', 'en': 'Logistics' },
  
  // 用户模块
  'pages/user/info': { 'zh-CN': '我的信息', 'en': 'My Info' },
  'pages/user/goods-collect': { 'zh-CN': '我的收藏', 'en': 'My Favorites' },
  'pages/user/goods-log': { 'zh-CN': '我的足迹', 'en': 'My History' },
  'pages/user/address/list': { 'zh-CN': '收货地址', 'en': 'Address' },
  'pages/user/address/edit': { 'zh-CN': '编辑地址', 'en': 'Edit Address' },
  'pages/user/goods_details_store/index': { 'zh-CN': '自提门店', 'en': 'Pick-up Store' },
  'pages/user/wallet/money': { 'zh-CN': '我的余额', 'en': 'My Balance' },
  'pages/user/wallet/score': { 'zh-CN': '我的积分', 'en': 'My Points' },
  
  // 分销模块
  'pages/commission/index': { 'zh-CN': '分销', 'en': 'Distribution' },
  'pages/commission/wallet': { 'zh-CN': '我的佣金', 'en': 'My Commission' },
  'pages/commission/goods': { 'zh-CN': '推广商品', 'en': 'Promote Products' },
  'pages/commission/order': { 'zh-CN': '分销订单', 'en': 'Distribution Orders' },
  'pages/commission/team': { 'zh-CN': '我的团队', 'en': 'My Team' },
  'pages/commission/promoter': { 'zh-CN': '推广人排行榜', 'en': 'Promoter Ranking' },
  'pages/commission/commission-ranking': { 'zh-CN': '佣金排行榜', 'en': 'Commission Ranking' },
  'pages/commission/withdraw': { 'zh-CN': '申请提现', 'en': 'Request Withdrawal' },
  
  // 应用模块
  'pages/app/sign': { 'zh-CN': '签到中心', 'en': 'Check-in Center' },
  
  // 公共页面
  'pages/public/setting': { 'zh-CN': '系统设置', 'en': 'Settings' },
  'pages/public/richtext': { 'zh-CN': '富文本', 'en': 'Rich Text' },
  'pages/public/faq': { 'zh-CN': '常见问题', 'en': 'FAQ' },
  'pages/public/error': { 'zh-CN': '错误页面', 'en': 'Error' },
  'pages/public/webview': { 'zh-CN': '网页', 'en': 'Web Page' },
  
  // 优惠券模块
  'pages/coupon/list': { 'zh-CN': '领券中心', 'en': 'Coupon Center' },
  'pages/coupon/detail': { 'zh-CN': '优惠券', 'en': 'Coupon' },
  
  // 客服模块
  'pages/chat/index': { 'zh-CN': '客服', 'en': 'Customer Service' },
  
  // 支付模块
  'pages/pay/index': { 'zh-CN': '收银台', 'en': 'Cashier' },
  'pages/pay/result': { 'zh-CN': '支付结果', 'en': 'Payment Result' },
  'pages/pay/recharge': { 'zh-CN': '充值余额', 'en': 'Recharge' },
  'pages/pay/recharge-log': { 'zh-CN': '充值记录', 'en': 'Recharge History' },
  
  // 活动模块
  'pages/activity/groupon/detail': { 'zh-CN': '拼团详情', 'en': 'Group Detail' },
  'pages/activity/groupon/order': { 'zh-CN': '我的拼团', 'en': 'My Groups' },
  'pages/activity/index': { 'zh-CN': '营销商品', 'en': 'Promotions' },
  'pages/activity/groupon/list': { 'zh-CN': '拼团活动', 'en': 'Group Activities' },
  'pages/activity/seckill/list': { 'zh-CN': '秒杀活动', 'en': 'Flash Sales' },
  'pages/activity/point/list': { 'zh-CN': '积分商城', 'en': 'Points Mall' }
}

/**
 * 设置页面标题
 * @param pagePath - 页面路径，如 'pages/index/index'
 */
export function setPageTitle(pagePath: string): void {
  const locale = getLocaleValue()
  const titleMap = pageTitles[pagePath]
  
  if (!titleMap) return
  
  const title = titleMap[locale] || titleMap['zh-CN'] || ''
  
  if (title) {
    uni.setNavigationBarTitle({ title })
  }
}

/**
 * 获取页面标题
 * @param pagePath - 页面路径
 * @param locale - 语言代码，可选，默认当前语言
 * @returns 页面标题
 */
export function getPageTitle(pagePath: string, locale?: string): string {
  const currentLocale = locale || getLocaleValue()
  const titleMap = pageTitles[pagePath]
  
  if (!titleMap) return ''
  
  return titleMap[currentLocale] || titleMap['zh-CN'] || ''
}

/**
 * 获取所有页面标题映射
 * @returns 页面标题映射表
 */
export function getAllPageTitles(): Record<string, Record<string, string>> {
  return { ...pageTitles }
}

/**
 * 语言切换后刷新当前页面标题
 * 在语言切换后调用
 */
export function refreshCurrentPageTitle(): void {
  const pages = getCurrentPages()
  if (pages.length === 0) return
  
  const currentPage = pages[pages.length - 1]
  const pagePath = currentPage.route || ''
  
  if (pagePath) {
    setPageTitle(pagePath)
  }
}

export {
  getLocaleValue,
  setLocale,
  t
}

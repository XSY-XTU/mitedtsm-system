// ========== COMMON - 公共模块 ==========

/**
 * 与后端Terminal枚举一一对应
 */
export const TerminalEnum = {
  UNKNOWN: 0, // 未知, 目的：在无法解析到 terminal 时，使用它
  WECHAT_MINI_PROGRAM: 10, //微信小程序
  WECHAT_WAP: 11, // 微信公众号
  H5: 20, // H5 网页
  APP: 31, // 手机 App
};

/**
 * 将 uni-app 提供的平台转换为后端所需的 terminal值
 *
 * @return 终端
 */
export const getTerminal = () => {
  const platformType = uni.getAppBaseInfo().uniPlatform;
  // 与后端terminal枚举一一对应
  switch (platformType) {
    case 'app':
      return TerminalEnum.APP;
    case 'web':
      return TerminalEnum.H5;
    case 'mp-weixin':
      return TerminalEnum.WECHAT_MINI_PROGRAM;
    default:
      return TerminalEnum.UNKNOWN;
  }
};

// ========== MALL - 营销模块 ==========

import dayjs from 'dayjs';
import { t } from '@/sheep/i18n';

/**
 * 优惠类型枚举
 * 注意：name 使用 i18n key，在显示时需要翻译
 */
export const PromotionDiscountTypeEnum = {
  PRICE: {
    type: 1,
    nameKey: 'promotion.priceDiscount',
  },
  PERCENT: {
    type: 2,
    nameKey: 'promotion.percentDiscount',
  },
};

/**
 * 获取优惠类型名称
 * @param {number} type - 类型值
 * @returns {string} 翻译后的名称
 */
export function getPromotionDiscountTypeName(type) {
  const item = Object.values(PromotionDiscountTypeEnum).find(e => e.type === type);
  return item ? t(item.nameKey) : '';
}

/**
 * 优惠劵模板的有限期类型的枚举
 */
export const CouponTemplateValidityTypeEnum = {
  DATE: {
    type: 1,
    nameKey: 'promotion.fixedDate',
  },
  TERM: {
    type: 2,
    nameKey: 'promotion.afterReceive',
  },
};

/**
 * 获取优惠券有效期类型名称
 * @param {number} type - 类型值
 * @returns {string} 翻译后的名称
 */
export function getCouponTemplateValidityTypeName(type) {
  const item = Object.values(CouponTemplateValidityTypeEnum).find(e => e.type === type);
  return item ? t(item.nameKey) : '';
}

// 时间段的状态枚举 key
const TimeStatusKeys = {
  WAIT_START: 'promotion.waitStart',
  STARTED: 'promotion.started',
  END: 'promotion.ended',
};

/**
 * 获取时间状态名称
 * @param {string} statusKey - 状态 key
 * @returns {string} 翻译后的名称
 */
export function getTimeStatusName(statusKey) {
  return t(TimeStatusKeys[statusKey] || statusKey);
}

/**
 * 获取时间段状态枚举值（用于比较）
 * @param startTime 开始时间
 * @param endTime 结束时间
 * @returns {string} 状态 key (WAIT_START, STARTED, END)
 */
export const getTimeStatusEnum = (startTime, endTime) => {
  const now = dayjs();
  if (now.isBefore(startTime)) {
    return 'WAIT_START';
  } else if (now.isAfter(endTime)) {
    return 'END';
  } else {
    return 'STARTED';
  }
};

/**
 * 时间段状态枚举
 * 与 getTimeStatusEnum 函数返回值对应
 */
export const TimeStatusEnum = {
  WAIT_START: 'WAIT_START',
  STARTED: 'STARTED',
  END: 'END',
};

/**
 * 微信小程序的订阅模版
 * 注意：name 使用 i18n key，在显示时需要翻译
 */
export const WxaSubscribeTemplate = {
  TRADE_ORDER_DELIVERY: 'subscribe.orderDelivery',
  PROMOTION_COMBINATION_SUCCESS: 'subscribe.combinationSuccess',
  PAY_WALLET_RECHARGER_SUCCESS: 'subscribe.rechargeSuccess',
};

/**
 * 获取微信订阅模板名称
 * @param {string} key - 模板 key
 * @returns {string} 翻译后的名称
 */
export function getWxaSubscribeTemplateName(key) {
  const nameKey = WxaSubscribeTemplate[key];
  return nameKey ? t(nameKey) : key;
}

export const PromotionActivityTypeEnum = {
  NORMAL: {
    type: 0,
    nameKey: 'promotion.normal',
  },
  SECKILL: {
    type: 1,
    nameKey: 'promotion.seckill',
  },
  BARGAIN: {
    type: 2,
    nameKey: 'promotion.bargain',
  },
  COMBINATION: {
    type: 3,
    nameKey: 'promotion.combination',
  },
  POINT: {
    type: 4,
    nameKey: 'promotion.point',
  },
};

/**
 * 获取营销活动类型名称
 * @param {number} type - 类型值
 * @returns {string} 翻译后的名称
 */
export function getPromotionActivityTypeName(type) {
  const item = Object.values(PromotionActivityTypeEnum).find(e => e.type === type);
  return item ? t(item.nameKey) : '';
}

/** 配送方式枚举 */
export const DeliveryTypeEnum = {
  EXPRESS: { type: 1, nameKey: 'delivery.express' },
  PICK_UP: { type: 2, nameKey: 'delivery.pickUp' },
};

/**
 * 获取配送方式名称
 * @param {number} type - 类型值
 * @returns {string} 翻译后的名称
 */
export function getDeliveryTypeName(type) {
  const item = Object.values(DeliveryTypeEnum).find(e => e.type === type);
  return item ? t(item.nameKey) : '';
}

/**
 * 分享页枚举
 * 按需扩展
 * 注意：name 使用 i18n key，在显示时需要翻译
 * */
export const SharePageEnum = {
  HOME: {
    nameKey: 'sharePage.home',
    page: '/pages/index/index',
    value: '1',
  },
  GOODS: {
    nameKey: 'sharePage.goods',
    page: '/pages/goods/index',
    value: '2',
  },
  GROUPON: {
    nameKey: 'sharePage.groupon',
    page: '/pages/goods/groupon',
    value: '3',
  },
  SECKILL: {
    nameKey: 'sharePage.seckill',
    page: '/pages/goods/seckill',
    value: '4',
  },
  GROUPON_DETAIL: {
    nameKey: 'sharePage.grouponDetail',
    page: '/pages/activity/groupon/detail',
    value: '5',
  },
  POINT: {
    nameKey: 'sharePage.point',
    page: '/pages/goods/point',
    value: '6',
  },
};

/**
 * 获取分享页名称
 * @param {string} value - 页面值
 * @returns {string} 翻译后的名称
 */
export function getSharePageName(value) {
  const item = Object.values(SharePageEnum).find(e => e.value === value);
  return item ? t(item.nameKey) : '';
}

import { ref } from 'vue';
import dayjs from 'dayjs';
import $url from '@/sheep/url';
import { formatDate } from '@/sheep/helper/utils';
import { t } from '@/sheep/i18n';
import { getCurrencyCode } from '@/sheep/config/currency';

/**
 * 格式化销量
 * @param {'exact' | string} type 格式类型：exact=精确值，其它=大致数量
 * @param {number} num 销量
 * @return {string} 格式化后的销量字符串
 */
export function formatSales(type, num) {
  let prefix = type !== 'exact' && num < 10 ? t('goods.salesPrefix') : t('goods.sold');
  return formatNum(prefix, type, num);
}

/**
 * 格式化兑换量
 * @param {'exact' | string} type 格式类型：exact=精确值，其它=大致数量
 * @param {number} num 销量
 * @return {string} 格式化后的销量字符串
 */
export function formatExchange(type, num) {
  return formatNum(t('goods.exchanged'), type, num);
}

/**
 * 格式化库存
 * @param {'exact' | any} type 格式类型：exact=精确值，其它=大致数量
 * @param {number} num 销量
 * @return {string} 格式化后的销量字符串
 */
export function formatStock(type, num) {
  return formatNum(t('goods.stock'), type, num);
}

/**
 * 格式化数字
 * @param {string} prefix 前缀
 * @param {'exact' | string} type 格式类型：exact=精确值，其它=大致数量
 * @param {number} num 销量
 * @return {string} 格式化后的销量字符串
 */
export function formatNum(prefix, type, num) {
  num = num || 0;
  // 情况一：精确数值
  if (type === 'exact') {
    return prefix + num;
  }
  // 情况二：小于等于 10
  if (num < 10) {
    return `${prefix}≤10`;
  }
  // 情况三：大于 10，除第一位外，其它位都显示为0
  // 例如：100  - 199  显示为 100+
  //      9000 - 9999 显示为 9000+
  const numStr = num.toString();
  const first = numStr[0];
  const other = '0'.repeat(numStr.length - 1);
  return `${prefix}${first}${other}+`;
}

// 格式化价格
export function formatPrice(e) {
  return e.length === 1 ? e[0] : e.join('~');
}

// 视频格式后缀列表
const VIDEO_SUFFIX_LIST = ['.avi', '.mp4'];

/**
 * 转换商品轮播的链接列表：根据链接的后缀，判断是视频链接还是图片链接
 *
 * @param {string[]} urlList 链接列表
 * @return {{src: string, type: 'video' | 'image' }[]}  转换后的链接列表
 */
export function formatGoodsSwiper(urlList) {
  return (
    urlList
      ?.filter((url) => url)
      .map((url, key) => {
        const isVideo = VIDEO_SUFFIX_LIST.some((suffix) => url.includes(suffix));
        const type = isVideo ? 'video' : 'image';
        const src = $url.cdn(url);
        return {
          type,
          src,
        };
      }) || []
  );
}

/**
 * 格式化订单状态的颜色
 *
 * @param order 订单
 * @return {string} 颜色的 class 名称
 */
export function formatOrderColor(order) {
  if (order.status === 0) {
    return 'info-color';
  }
  if (order.status === 10 || order.status === 20 || (order.status === 30 && !order.commentStatus)) {
    return 'warning-color';
  }
  if (order.status === 30 && order.commentStatus) {
    return 'success-color';
  }
  return 'danger-color';
}

/**
 * 格式化订单状态
 *
 * @param order 订单
 */
export function formatOrderStatus(order) {
  if (order.status === 0) {
    return t('orderStatus.unpaid');
  }
  if (order.status === 10 && order.deliveryType === 1) {
    return t('orderStatus.unshipped');
  }
  if (order.status === 10 && order.deliveryType === 2) {
    return t('orderStatus.toBeVerified');
  }
  if (order.status === 20) {
    return t('orderStatus.unreceived');
  }
  if (order.status === 30 && !order.commentStatus) {
    return t('orderStatus.unreviewed');
  }
  if (order.status === 30 && order.commentStatus) {
    return t('orderStatus.completed');
  }
  return t('orderStatus.closed');
}

/**
 * 格式化订单状态的描述
 *
 * @param order 订单
 */
export function formatOrderStatusDescription(order) {
  if (order.status === 0) {
    return t('orderStatus.unpaidDesc', { time: formatDate(order.payExpireTime) });
  }
  if (order.status === 10) {
    return t('orderStatus.unshippedDesc');
  }
  if (order.status === 20) {
    return t('orderStatus.unreceivedDesc');
  }
  if (order.status === 30 && !order.commentStatus) {
    return t('orderStatus.unreviewedDesc');
  }
  if (order.status === 30 && order.commentStatus) {
    return t('orderStatus.completedDesc');
  }
  return t('orderStatus.closedDesc');
}

/**
 * 处理订单的 button 操作按钮数组
 *
 * @param order 订单
 */
export function handleOrderButtons(order) {
  order.buttons = [];
  if (order.type === 3) {
    // 查看拼团
    order.buttons.push('combination');
  }
  if (order.status === 20) {
    // 确认收货
    order.buttons.push('confirm');
  }
  if (order.logisticsId > 0) {
    // 查看物流
    order.buttons.push('express');
  }
  if (order.status === 0) {
    // 取消订单 / 发起支付
    order.buttons.push('cancel');
    order.buttons.push('pay');
  }
  if (order.status === 30 && !order.commentStatus) {
    // 发起评价
    order.buttons.push('comment');
  }
  if (order.status === 40) {
    // 删除订单
    order.buttons.push('delete');
  }
}

/**
 * 格式化售后状态
 *
 * @param afterSale 售后
 */
export function formatAfterSaleStatus(afterSale) {
  if (afterSale.status === 10) {
    return t('afterSaleStatus.applying');
  }
  if (afterSale.status === 20) {
    return t('afterSaleStatus.waitingReturn');
  }
  if (afterSale.status === 30) {
    return t('afterSaleStatus.waitingReceive');
  }
  if (afterSale.status === 40) {
    return t('afterSaleStatus.waitingRefund');
  }
  if (afterSale.status === 50) {
    return t('afterSaleStatus.refundSuccess');
  }
  if (afterSale.status === 61) {
    return t('afterSaleStatus.buyerCanceled');
  }
  if (afterSale.status === 62) {
    return t('afterSaleStatus.merchantRejected');
  }
  if (afterSale.status === 63) {
    return t('afterSaleStatus.merchantRefusedReceive');
  }
  return t('afterSaleStatus.unknown');
}

/**
 * 格式化售后状态的描述
 *
 * @param afterSale 售后
 */
export function formatAfterSaleStatusDescription(afterSale) {
  if (afterSale.status === 10) {
    return t('afterSaleStatus.applyingDesc');
  }
  if (afterSale.status === 20) {
    return t('afterSaleStatus.waitingReturnDesc');
  }
  if (afterSale.status === 30) {
    return t('afterSaleStatus.waitingReceiveDesc');
  }
  if (afterSale.status === 40) {
    return t('afterSaleStatus.waitingRefundDesc');
  }
  if (afterSale.status === 50) {
    return t('afterSaleStatus.refundSuccessDesc');
  }
  if (afterSale.status === 61) {
    return t('afterSaleStatus.buyerCanceledDesc');
  }
  if (afterSale.status === 62) {
    return t('afterSaleStatus.merchantRejectedDesc', { reason: afterSale.auditReason });
  }
  if (afterSale.status === 63) {
    return t('afterSaleStatus.merchantRefusedReceiveDesc', { reason: afterSale.auditReason });
  }
  return t('afterSaleStatus.unknownDesc');
}

/**
 * 处理售后的 button 操作按钮数组
 *
 * @param afterSale 售后
 */
export function handleAfterSaleButtons(afterSale) {
  afterSale.buttons = [];
  if ([10, 20, 30].includes(afterSale.status)) {
    // 取消订单
    afterSale.buttons.push('cancel');
  }
  if (afterSale.status === 20) {
    // 退货信息
    afterSale.buttons.push('delivery');
  }
}

/**
 * 倒计时
 * @param toTime   截止时间
 * @param fromTime 起始时间，默认当前时间
 * @return {{s: string, ms: number, h: string, m: string}} 持续时间
 */
export function useDurationTime(toTime, fromTime = '') {
  toTime = getDayjsTime(toTime);
  if (fromTime === '') {
    fromTime = dayjs();
  }
  let duration = ref(toTime - fromTime);
  if (duration.value > 0) {
    setTimeout(() => {
      if (duration.value > 0) {
        duration.value -= 1000;
      }
    }, 1000);
  }

  let durationTime = dayjs.duration(duration.value);
  return {
    h: (durationTime.months() * 30 * 24 + durationTime.days() * 24 + durationTime.hours())
      .toString()
      .padStart(2, '0'),
    m: durationTime.minutes().toString().padStart(2, '0'),
    s: durationTime.seconds().toString().padStart(2, '0'),
    ms: durationTime.$ms,
  };
}

/**
 * 转换为 Dayjs
 * @param {any} time 时间
 * @return {dayjs.Dayjs}
 */
function getDayjsTime(time) {
  time = time.toString();
  if (time.indexOf('-') > 0) {
    // 'date'
    return dayjs(time);
  }
  if (time.length > 10) {
    // 'timestamp'
    return dayjs(parseInt(time));
  }
  if (time.length === 10) {
    // 'unixTime'
    return dayjs.unix(parseInt(time));
  }
}

/**
 * 将分转成元（带货币代码）
 *
 * @param price 分，例如说 100 分
 * @param {boolean} withCurrency 是否带货币代码，默认 true
 * @returns {string} 元，例如说 "1.00 CNY" 或 "1.00"
 */
export function fen2yuan(price, withCurrency = true) {
  const yuan = (price / 100.0).toFixed(2);
  if (withCurrency) {
    return `${yuan} ${getCurrencyCode()}`;
  }
  return yuan;
}

/**
 * 将分转成元（带货币代码，无多余小数点）
 *
 * 如果没有小数点，则不展示小数点部分
 *
 * @param price 分，例如说 100 分
 * @param {boolean} withCurrency 是否带货币代码，默认 true
 * @returns {string} 元，例如说 "1 CNY" 或 "1"
 */
export function fen2yuanSimple(price, withCurrency = true) {
  const yuan = fen2yuan(price, false).replace(/\.?0+$/, '');
  if (withCurrency) {
    return `${yuan} ${getCurrencyCode()}`;
  }
  return yuan;
}

/**
 * 将折扣百分比转化为“打x者”的 x 部分
 *
 * @param discountPercent
 */
export function formatDiscountPercent(discountPercent) {
  return 100 - discountPercent;
}

/**
 * 从商品 SKU 数组中，转换出商品属性的数组
 *
 * 类似结构：[{
 *    id: // 属性的编号
 *    name: // 属性的名字
 *    values: [{
 *      id: // 属性值的编号
 *      name: // 属性值的名字
 *    }]
 * }]
 *
 * @param skus 商品 SKU 数组
 */
export function convertProductPropertyList(skus) {
  let result = [];
  for (const sku of skus) {
    if (!sku.properties) {
      continue;
    }
    for (const property of sku.properties) {
      // ① 先处理属性
      let resultProperty = result.find((item) => item.id === property.propertyId);
      if (!resultProperty) {
        resultProperty = {
          id: property.propertyId,
          name: property.propertyName,
          values: [],
        };
        result.push(resultProperty);
      }
      // ② 再处理属性值
      let resultValue = resultProperty.values.find((item) => item.id === property.valueId);
      if (!resultValue) {
        resultProperty.values.push({
          id: property.valueId,
          name: property.valueName,
        });
      }
    }
  }
  return result;
}

export function appendSettlementProduct(spus, settlementInfos) {
  if (!settlementInfos || settlementInfos.length === 0) {
    return;
  }
  for (const spu of spus) {
    const settlementInfo = settlementInfos.find((info) => info.spuId === spu.id);
    if (!settlementInfo) {
      return;
    }
    // 选择价格最小的 SKU 设置到 SPU 上
    const settlementSku = settlementInfo.skus
      .filter((sku) => sku.promotionPrice > 0)
      .reduce((prev, curr) => (prev.promotionPrice < curr.promotionPrice ? prev : curr), []);
    if (settlementSku) {
      spu.promotionType = settlementSku.promotionType;
      spu.promotionPrice = settlementSku.promotionPrice;
    }
    // 设置【满减送】活动
    if (settlementInfo.rewardActivity) {
      spu.rewardActivity = settlementInfo.rewardActivity;
    }
  }
}

// 获得满减送活动的规则描述（group）
export function getRewardActivityRuleGroupDescriptions(activity) {
  if (!activity || !activity.rules || activity.rules.length === 0) {
    return [];
  }
  const result = [
    { name: t('rewardActivity.discount'), values: [] },
    { name: t('rewardActivity.gift'), values: [] },
    { name: t('rewardActivity.freeShipping'), values: [] },
  ];
  activity.rules.forEach((rule) => {
    const conditionTypeStr =
      activity.conditionType === 10 
        ? t('rewardActivity.fullYuan', { amount: fen2yuanSimple(rule.limit) }) 
        : t('rewardActivity.fullPieces', { count: rule.limit });
    // 满减
    if (rule.limit) {
      result[0].values.push(`${conditionTypeStr} ${t('rewardActivity.discountYuan', { amount: fen2yuanSimple(rule.discountPrice) })}`);
    }
    // 赠品
    if (rule.point || (rule.giveCouponTemplateCounts && rule.giveCouponTemplateCounts.length > 0)) {
      let tips = [];
      if (rule.point) {
        tips.push(t('rewardActivity.givePoints', { points: rule.point }));
      }
      if (rule.giveCouponTemplateCounts && rule.giveCouponTemplateCounts.length > 0) {
        tips.push(t('rewardActivity.giveCoupons', { count: rule.giveCouponTemplateCounts.length }));
      }
      result[1].values.push(`${conditionTypeStr} ${tips.join('、')}`);
    }
    // 包邮
    if (rule.freeDelivery) {
      result[2].values.push(`${conditionTypeStr} ${t('rewardActivity.freeShippingText')}`);
    }
  });
  // 移除 values 为空的元素
  result.forEach((item) => {
    if (item.values.length === 0) {
      result.splice(result.indexOf(item), 1);
    }
  });
  return result;
}

// 获得满减送活动的规则描述（item）
export function getRewardActivityRuleItemDescriptions(activity) {
  if (!activity || !activity.rules || activity.rules.length === 0) {
    return [];
  }
  const result = [];
  activity.rules.forEach((rule) => {
    const conditionTypeStr =
      activity.conditionType === 10 
        ? t('rewardActivity.fullYuan', { amount: fen2yuanSimple(rule.limit) })
        : t('rewardActivity.fullPieces', { count: rule.limit });
    // 满减
    if (rule.limit) {
      result.push(`${conditionTypeStr} ${t('rewardActivity.discountYuan', { amount: fen2yuanSimple(rule.discountPrice) })}`);
    }
    // 赠品
    if (rule.point) {
      result.push(`${conditionTypeStr} ${t('rewardActivity.givePoints', { points: rule.point })}`);
    }
    if (rule.giveCouponTemplateCounts && rule.giveCouponTemplateCounts.length > 0) {
      result.push(`${conditionTypeStr} ${t('rewardActivity.giveCoupons', { count: rule.giveCouponTemplateCounts.length })}`);
    }
    // 包邮
    if (rule.freeDelivery) {
      result.push(`${conditionTypeStr} ${t('rewardActivity.freeShippingText')}`);
    }
  });
  return result;
}

/** 单规格，要默认选中 */
export function initDefaultSelect(propertyList, onSelectSku) {
  if (propertyList.length === 0) {
    return;
  }
  // 遍历每一个属性
  for (const property of propertyList) {
    const firstValue = (property.values || [])[0];
    // 不是禁用直接选中
    if (firstValue && !firstValue.disabled) {
      onSelectSku(property.id, firstValue.id);
    }
  }
}

import { CouponTemplateValidityTypeEnum, PromotionDiscountTypeEnum } from '@/utils/constants'
import { formatDate } from '@/utils/formatTime'
import { CouponTemplateVO } from '@/api/mall/promotion/coupon/couponTemplate'
import { floatToFixed2 } from '@/utils'

const { t } = useI18n()

// 格式化【优惠金额/折扣】
export const discountFormat = (row: CouponTemplateVO) => {
  if (row.discountType === PromotionDiscountTypeEnum.PRICE.type) {
    return `￥${floatToFixed2(row.discountPrice)}`
  }
  if (row.discountType === PromotionDiscountTypeEnum.PERCENT.type) {
    return `${row.discountPercent}%`
  }
  return t('mall.promotion.coupon.unknownType', { type: row.discountType })
}

// 格式化【领取上限】
export const takeLimitCountFormat = (row: CouponTemplateVO) => {
  if (row.takeLimitCount) {
    if (row.takeLimitCount === -1) {
      return t('mall.promotion.coupon.noLimit')
    }
    return `${row.takeLimitCount} ${t('mall.promotion.coupon.perPerson')}`
  } else {
    return ' '
  }
}

// 格式化【有效期限】
export const validityTypeFormat = (row: CouponTemplateVO) => {
  if (row.validityType === CouponTemplateValidityTypeEnum.DATE.type) {
    return `${formatDate(row.validStartTime)} ${t('common.to')} ${formatDate(row.validEndTime)}`
  }
  if (row.validityType === CouponTemplateValidityTypeEnum.TERM.type) {
    return `${t('mall.promotion.coupon.from')} ${row.fixedStartTerm} - ${row.fixedEndTerm} ${t('mall.promotion.coupon.daysValid')}`
  }
  return t('mall.promotion.coupon.unknownType', { type: row.validityType })
}

// 格式化【totalCount】
export const totalCountFormat = (row: CouponTemplateVO) => {
  if (row.totalCount === -1) {
    return t('mall.promotion.coupon.unlimited')
  }
  return row.totalCount
}

// 格式化【剩余数量】
export const remainedCountFormat = (row: CouponTemplateVO) => {
  if (row.totalCount === -1) {
    return t('mall.promotion.coupon.unlimited')
  }
  return row.totalCount - row.takeCount
}

// 格式化【最低消费】
export const usePriceFormat = (row: CouponTemplateVO) => {
  return `￥${floatToFixed2(row.usePrice)}`
}

import { floatToFixed2 } from '@/utils'

// 获取货币代码（默认CNY，后续可从商家设置获取）
const getCurrencyCode = (): string => {
  return localStorage.getItem('currency-code') || 'CNY'
}

// 格式化金额【分转元】
// @ts-ignore
export const fenToYuanFormat = (_, __, cellValue: any, ___) => {
  return `${floatToFixed2(cellValue)} ${getCurrencyCode()}`
}

// 格式化金额带货币代码
export const formatPriceWithCurrency = (price: number | string, currencyCode?: string): string => {
  const code = currencyCode || getCurrencyCode()
  return `${floatToFixed2(price)} ${code}`
}


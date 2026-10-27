/**
 * 货币配置模块
 * 统一使用货币代码（CNY/USD/SAR），不使用货币符号
 */

// 默认货币代码
export const CURRENCY_CODE = 'CNY';

// 货币列表
export const CURRENCY_LIST = [
  { code: 'CNY', name: '人民币' },
  { code: 'USD', name: 'US Dollar' },
  { code: 'SAR', name: 'Saudi Riyal' }
];

// 存储 key
const CURRENCY_STORAGE_KEY = 'currency-code';

/**
 * 获取当前货币代码
 * @returns {string} 货币代码，默认 CNY
 */
export function getCurrencyCode() {
  return uni.getStorageSync(CURRENCY_STORAGE_KEY) || CURRENCY_CODE;
}

/**
 * 设置货币代码
 * @param {string} code 货币代码
 */
export function setCurrencyCode(code) {
  uni.setStorageSync(CURRENCY_STORAGE_KEY, code);
}

/**
 * 格式化价格（带货币代码）
 * @param {number|string} price 价格（元）
 * @param {string} currencyCode 货币代码，不传则使用当前设置的货币
 * @returns {string} 格式化后的价格，如 "100.00 CNY"
 */
export function formatPrice(price, currencyCode) {
  const code = currencyCode || getCurrencyCode();
  const priceStr = typeof price === 'number' ? price.toFixed(2) : parseFloat(price).toFixed(2);
  return `${priceStr} ${code}`;
}

/**
 * 获取货币代码后缀（用于显示）
 * @param {string} currencyCode 货币代码，不传则使用当前设置的货币
 * @returns {string} 货币代码，如 "CNY"
 */
export function getCurrencySuffix(currencyCode) {
  return currencyCode || getCurrencyCode();
}

export default {
  CURRENCY_CODE,
  CURRENCY_LIST,
  getCurrencyCode,
  setCurrencyCode,
  formatPrice,
  getCurrencySuffix
};

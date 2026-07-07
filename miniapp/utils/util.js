/**
 * 通用工具函数
 */

/**
 * 格式化金额
 * @param {number} amount
 * @returns {string}
 */
function formatMoney(amount) {
  if (amount == null) return '0.00';
  return parseFloat(amount).toFixed(2);
}

/**
 * 格式化日期
 * @param {string|Date} date
 * @returns {string} yyyy-MM-dd
 */
function formatDate(date) {
  if (!date) {
    date = new Date();
  }
  if (typeof date === 'string') {
    date = new Date(date.replace(/-/g, '/'));
  }
  const y = date.getFullYear();
  const m = (date.getMonth() + 1).toString().padStart(2, '0');
  const d = date.getDate().toString().padStart(2, '0');
  return `${y}-${m}-${d}`;
}

/**
 * 获取今天的日期字符串
 */
function getToday() {
  return formatDate(new Date());
}

/**
 * 获取本周一的日期
 */
function getWeekStart() {
  const now = new Date();
  const day = now.getDay() || 7;
  now.setDate(now.getDate() - day + 1);
  return formatDate(now);
}

/**
 * 获取本月1号的日期
 */
function getMonthStart() {
  const now = new Date();
  now.setDate(1);
  return formatDate(now);
}

/**
 * 计价方式中文
 */
function priceTypeText(type) {
  const map = {
    'PRICE_WEIGHT': '按斤',
    'PRICE_PER': '按份',
    'PRICE_UNIT': '按个'
  };
  return map[type] || type;
}

/**
 * Toast 提示
 */
function toast(title, icon = 'none') {
  wx.showToast({ title, icon, duration: 2000 });
}

module.exports = {
  formatMoney,
  formatDate,
  getToday,
  getWeekStart,
  getMonthStart,
  priceTypeText,
  toast
};

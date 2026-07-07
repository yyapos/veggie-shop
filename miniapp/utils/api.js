/**
 * API 请求封装
 * 统一处理请求拦截、错误处理
 */
const app = getApp();

const BASE_URL = app?.globalData?.baseUrl || 'http://192.168.1.209:8080';

/**
 * 发起请求
 * @param {string} url    - 接口路径（不含 baseUrl）
 * @param {object} options - 请求配置
 * @returns {Promise}
 */
function request(url, options = {}) {
  return new Promise((resolve, reject) => {
    wx.request({
      url: BASE_URL + url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        ...options.header
      },
      success(res) {
        if (res.statusCode === 200) {
          const result = res.data;
          if (result.code === 200) {
            resolve(result.data);
          } else {
            wx.showToast({ title: result.message || '请求失败', icon: 'none' });
            reject(result);
          }
        } else {
          wx.showToast({ title: '服务器异常(' + res.statusCode + ')', icon: 'none' });
          reject(res);
        }
      },
      fail(err) {
        wx.showToast({ title: '网络请求失败', icon: 'none' });
        reject(err);
      }
    });
  });
}

// 快捷方法
const api = {
  get(url, params = {}) {
    return request(url, { method: 'GET', data: params });
  },
  post(url, data = {}) {
    return request(url, { method: 'POST', data });
  },
  put(url, data = {}) {
    return request(url, { method: 'PUT', data });
  },
  delete(url, data = {}) {
    return request(url, { method: 'DELETE', data });
  }
};

// ==================== 接口列表 ====================

// --- 商品分类 ---
api.getCategoryTree = () => api.get('/api/category/tree');
api.getCategories = (params) => api.get('/api/category/page', params);

// --- 商品 ---
api.getProducts = (params) => api.get('/api/product/page', params);
api.getOnSaleProducts = () => api.get('/api/product/on-sale');
api.getWarningStock = () => api.get('/api/product/warning-stock');
api.getProductById = (id) => api.get('/api/product/' + id);
api.addProduct = (data) => api.post('/api/product', data);
api.updateProduct = (data) => api.put('/api/product', data);
api.deleteProduct = (id) => api.delete('/api/product/' + id);
api.putOnSale = (id) => api.put('/api/product/put-on-sale/' + id);
api.takeOffSale = (id) => api.put('/api/product/take-off-sale/' + id);

// --- 进货 ---
api.getPurchases = (params) => api.get('/api/purchase/page', params);
api.addPurchase = (data) => api.post('/api/purchase', data);

// --- 销售 ---
api.getSales = (params) => api.get('/api/sale/page', params);
api.addSale = (data) => api.post('/api/sale', data);
api.getTodayStats = () => api.get('/api/sale/today-stats');

// --- 赊账人 ---
api.getDebtors = (params) => api.get('/api/debtor/page', params);
api.getAllDebtors = () => api.get('/api/debtor/all');
api.addDebtor = (data) => api.post('/api/debtor', data);
api.updateDebtor = (data) => api.put('/api/debtor', data);
api.deleteDebtor = (id) => api.delete('/api/debtor/' + id);

// --- 赊账记录 ---
api.getDebts = (params) => api.get('/api/debt/page', params);
api.addDebt = (data) => api.post('/api/debt', data);
api.markPaid = (id) => api.put('/api/debt/mark-paid/' + id);
api.getUnpaidDebts = (debtorId) => api.get('/api/debt/unpaid/' + debtorId);

// --- 还款 ---
api.getRepayments = (params) => api.get('/api/repayment/page', params);
api.addRepayment = (data) => api.post('/api/repayment', data);

// --- 仪表盘 ---
api.getDashboard = () => api.get('/api/dashboard');

module.exports = api;

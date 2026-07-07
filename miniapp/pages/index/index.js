const api = require('../../utils/api');
const util = require('../../utils/util');

Page({
  data: {
    today: {},
    todayStats: {},
    warningProducts: [],
    loading: true
  },

  onShow() {
    this.loadData();
  },

  onPullDownRefresh() {
    this.loadData().then(() => wx.stopPullDownRefresh());
  },

  async loadData() {
    this.setData({ loading: true });
    try {
      const [dashboard, warnings] = await Promise.all([
        api.getDashboard().catch(() => null),
        api.getWarningStock().catch(() => [])
      ]);

      if (dashboard) {
        this.setData({
          today: dashboard.today || {},
          todayStats: dashboard.todayStats || {}
        });
      }

      if (warnings) {
        this.setData({ warningProducts: warnings });
      }
    } catch (e) {
      console.error('加载数据失败', e);
    } finally {
      this.setData({ loading: false });
    }
  },

  // 跳转商品管理
  goProduct() {
    wx.switchTab({ url: '/pages/product/product' });
  },

  // 跳转销售记账
  goSale() {
    wx.switchTab({ url: '/pages/sale/sale' });
  },

  // 跳转进货
  goPurchase() {
    wx.navigateTo({ url: '/pages/purchase/purchase' });
  },

  // 跳转赊账
  goDebtor() {
    wx.switchTab({ url: '/pages/debtor/debtor' });
  },

  // 跳转数据看板
  goDashboard() {
    wx.switchTab({ url: '/pages/dashboard/dashboard' });
  }
});

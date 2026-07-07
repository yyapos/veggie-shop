const api = require('../../utils/api');
const util = require('../../utils/util');

Page({
  data: {
    debtors: [],
    showAddForm: false,
    newName: '',
    newPhone: ''
  },

  onShow() {
    this.loadDebtors();
  },

  onPullDownRefresh() {
    this.loadDebtors().then(() => wx.stopPullDownRefresh());
  },

  async loadDebtors() {
    try {
      const result = await api.getDebtors({ page: 1, size: 50 });
      this.setData({ debtors: result.records || [] });
    } catch (e) { /* ignore */ }
  },

  // 跳转赊账人详情
  goDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/debtor/detail?id=' + id });
  },

  // 显示添加表单
  showAdd() {
    this.setData({ showAddForm: true, newName: '', newPhone: '' });
  },

  hideAdd() {
    this.setData({ showAddForm: false });
  },

  // 添加赊账人
  async addDebtor() {
    if (!this.data.newName) { util.toast('请输入姓名'); return; }
    try {
      await api.addDebtor({
        name: this.data.newName,
        phone: this.data.newPhone
      });
      util.toast('添加成功', 'success');
      this.hideAdd();
      this.loadDebtors();
    } catch (e) { /* handled */ }
  }
});

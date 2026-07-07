const api = require('../../utils/api');
const util = require('../../utils/util');

Page({
  data: {
    debtorId: null,
    debtor: null,
    unpaidDebts: [],
    repayments: [],
    showRepayForm: false,
    repayAmount: '',
    repayRemark: ''
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ debtorId: options.id });
      this.loadData();
    }
  },

  onShow() {
    if (this.data.debtorId) this.loadData();
  },

  async loadData() {
    const id = this.data.debtorId;
    try {
      const [debtor, unpaidDebts, repayResult] = await Promise.all([
        api.get('/api/debtor/' + id),
        api.getUnpaidDebts(id),
        api.getRepayments({ debtorId: id, page: 1, size: 20 })
      ]);
      this.setData({
        debtor: debtor,
        unpaidDebts: unpaidDebts || [],
        repayments: repayResult.records || []
      });
    } catch (e) {
      util.toast('加载数据失败');
    }
  },

  // 显示还款表单
  showRepay() {
    this.setData({ showRepayForm: true, repayAmount: '', repayRemark: '' });
  },

  hideRepay() {
    this.setData({ showRepayForm: false });
  },

  // 确认还款
  async confirmRepay() {
    const amount = parseFloat(this.data.repayAmount);
    if (!amount || amount <= 0) { util.toast('请输入有效金额'); return; }
    if (amount > parseFloat(this.data.debtor.unpaidAmount)) {
      util.toast('还款金额不能超过未还金额'); return;
    }
    try {
      await api.addRepayment({
        debtorId: parseInt(this.data.debtorId),
        amount: amount,
        repayDate: util.getToday(),
        remark: this.data.repayRemark || '还款'
      });
      util.toast('还款记录已保存', 'success');
      this.hideRepay();
      this.loadData();
    } catch (e) { /* handled */ }
  },

  // 标记赊账单条为已还
  async markSinglePaid(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '确认还款',
      content: '确认将该笔赊账标记为已还？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await api.markPaid(id);
            util.toast('已标记', 'success');
            this.loadData();
          } catch (e) { /* handled */ }
        }
      }
    });
  }
});

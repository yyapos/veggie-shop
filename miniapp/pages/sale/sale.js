const api = require('../../utils/api');
const util = require('../../utils/util');

Page({
  data: {
    products: [],
    selectedProduct: null,
    showProductPicker: false,
    quantity: '',
    saleType: 'CASH',
    debtorId: null,
    debtors: [],
    calculatedAmount: '0.00',
    // 历史记录
    recentSales: [],
    todayStats: null
  },

  onShow() {
    this.loadProducts();
    this.loadTodayStats();
    this.loadRecentSales();
  },

  async loadProducts() {
    try {
      const list = await api.getOnSaleProducts();
      this.setData({ products: list || [] });
    } catch (e) { /* ignore */ }
  },

  async loadTodayStats() {
    try {
      const stats = await api.getTodayStats();
      this.setData({ todayStats: stats });
    } catch (e) { /* ignore */ }
  },

  async loadRecentSales() {
    try {
      const today = util.getToday();
      const result = await api.getSales({ page: 1, size: 10, startDate: today, endDate: today });
      this.setData({ recentSales: result.records || [] });
    } catch (e) { /* ignore */ }
  },

  async loadDebtors() {
    try {
      const list = await api.getAllDebtors();
      this.setData({ debtors: list || [] });
    } catch (e) { /* ignore */ }
  },

  // 选择商品
  onSelectProduct() {
    this.setData({ showProductPicker: true });
  },

  onProductTap(e) {
    const product = this.data.products.find(p => p.id == e.currentTarget.dataset.id);
    if (product) {
      this.setData({
        selectedProduct: product,
        showProductPicker: false,
        quantity: '',
        calculatedAmount: '0.00'
      });
    }
  },

  closePicker() {
    this.setData({ showProductPicker: false });
  },

  // 输入数量
  onQuantityInput(e) {
    const quantity = e.detail.value;
    this.setData({ quantity });
    this.calculateAmount(quantity);
  },

  // 计算金额
  calculateAmount(quantity) {
    const product = this.data.selectedProduct;
    if (product && quantity && parseFloat(quantity) > 0) {
      const amount = product.price * parseFloat(quantity);
      this.setData({ calculatedAmount: amount.toFixed(2) });
    } else {
      this.setData({ calculatedAmount: '0.00' });
    }
  },

  // 切换收款方式
  onSaleTypeChange(e) {
    const type = e.currentTarget.dataset.type;
    this.setData({ saleType: type });
    if (type === 'DEBT') {
      this.loadDebtors();
    }
  },

  // 选择赊账人
  onDebtorChange(e) {
    const index = e.detail.value;
    this.setData({ debtorId: this.data.debtors[index].id });
  },

  // 确认销售
  async confirmSale() {
    const { selectedProduct, quantity, saleType, debtorId } = this.data;

    if (!selectedProduct) { util.toast('请选择商品'); return; }
    if (!quantity || parseFloat(quantity) <= 0) { util.toast('请输入数量'); return; }
    if (saleType === 'DEBT' && !debtorId) { util.toast('请选择赊账人'); return; }

    const data = {
      productId: selectedProduct.id,
      quantity: parseFloat(quantity),
      price: selectedProduct.price,
      saleType: saleType,
      saleDate: util.getToday()
    };

    try {
      await api.addSale(data);

      // 如果是赊账，同时创建赊账记录
      if (saleType === 'DEBT' && debtorId) {
        await api.addDebt({
          debtorId: debtorId,
          productId: selectedProduct.id,
          quantity: parseFloat(quantity),
          amount: parseFloat(this.data.calculatedAmount),
          debtDate: util.getToday()
        });
      }

      util.toast('记账成功！收款 ¥' + this.data.calculatedAmount, 'success');
      this.resetForm();
      this.loadProducts();
      this.loadTodayStats();
      this.loadRecentSales();
    } catch (e) { /* handled */ }
  },

  resetForm() {
    this.setData({
      selectedProduct: null,
      quantity: '',
      saleType: 'CASH',
      debtorId: null,
      calculatedAmount: '0.00'
    });
  }
});

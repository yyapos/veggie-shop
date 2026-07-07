const api = require('../../utils/api');
const util = require('../../utils/util');

Page({
  data: {
    products: [],
    selectedProduct: null,
    showProductPicker: false,
    quantity: '',
    costPrice: '',
    market: '',
    remark: '',
    calculatedTotal: '0.00',
    // 历史
    recentPurchases: []
  },

  onShow() {
    this.loadProducts();
    this.loadRecentPurchases();
  },

  async loadProducts() {
    try {
      // 进货可以选全部商品（含下架的补货）
      const result = await api.getProducts({ page: 1, size: 200 });
      this.setData({ products: result.records || [] });
    } catch (e) { /* ignore */ }
  },

  async loadRecentPurchases() {
    try {
      const today = util.getToday();
      const result = await api.getPurchases({ page: 1, size: 10, startDate: today, endDate: today });
      this.setData({ recentPurchases: result.records || [] });
    } catch (e) { /* ignore */ }
  },

  onSelectProduct() {
    this.setData({ showProductPicker: true });
  },

  onProductTap(e) {
    const product = this.data.products.find(p => p.id == e.currentTarget.dataset.id);
    if (product) {
      this.setData({
        selectedProduct: product,
        showProductPicker: false,
        costPrice: String(product.costPrice || ''),
        quantity: '',
        calculatedTotal: '0.00'
      });
    }
  },

  closePicker() {
    this.setData({ showProductPicker: false });
  },

  onQuantityInput(e) {
    this.setData({ quantity: e.detail.value });
    this.calculateTotal(e.detail.value, this.data.costPrice);
  },

  onCostPriceInput(e) {
    this.setData({ costPrice: e.detail.value });
    this.calculateTotal(this.data.quantity, e.detail.value);
  },

  calculateTotal(quantity, price) {
    if (quantity && price && parseFloat(quantity) > 0 && parseFloat(price) > 0) {
      this.setData({ calculatedTotal: (parseFloat(quantity) * parseFloat(price)).toFixed(2) });
    } else {
      this.setData({ calculatedTotal: '0.00' });
    }
  },

  // 确认进货
  async confirmPurchase() {
    const { selectedProduct, quantity, costPrice, market, remark } = this.data;

    if (!selectedProduct) { util.toast('请选择商品'); return; }
    if (!quantity || parseFloat(quantity) <= 0) { util.toast('请输入进货数量'); return; }
    if (!costPrice || parseFloat(costPrice) <= 0) { util.toast('请输入进价'); return; }

    try {
      await api.addPurchase({
        productId: selectedProduct.id,
        quantity: parseFloat(quantity),
        costPrice: parseFloat(costPrice),
        market: market || '本地批发市场',
        remark: remark,
        purchaseDate: util.getToday()
      });
      util.toast('进货记录已保存', 'success');
      this.setData({
        selectedProduct: null, quantity: '', costPrice: '',
        market: '', remark: '', calculatedTotal: '0.00'
      });
      this.loadRecentPurchases();
    } catch (e) { /* handled */ }
  }
});

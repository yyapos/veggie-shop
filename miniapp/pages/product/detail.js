const api = require('../../utils/api');
const util = require('../../utils/util');

Page({
  data: {
    isEdit: false,
    id: null,
    form: {
      name: '',
      categoryId: null,
      priceType: 'PRICE_WEIGHT',
      price: '',
      costPrice: '',
      stock: '',
      unit: '斤',
      warningStock: '10',
      status: 1
    },
    categories: [],
    priceTypeLabel: '按斤',
    priceTypes: [
      { value: 'PRICE_WEIGHT', label: '按斤' },
      { value: 'PRICE_PER', label: '按份' },
      { value: 'PRICE_UNIT', label: '按个' }
    ],
    units: ['斤', '袋', '个', '把', '盒', '桶', '瓶']
  },

  onLoad(options) {
    this.loadCategories();
    if (options.id) {
      this.setData({ isEdit: true, id: options.id });
      this.loadProduct(options.id);
      wx.setNavigationBarTitle({ title: '编辑商品' });
    } else {
      wx.setNavigationBarTitle({ title: '新增商品' });
    }
  },

  async loadCategories() {
    try {
      const tree = await api.getCategoryTree();
      this.setData({ categories: tree || [] });
    } catch (e) { /* ignore */ }
  },

  getPriceTypeLabel(value) {
    const t = this.data.priceTypes.find(p => p.value === value);
    return t ? t.label : '按斤';
  },

  async loadProduct(id) {
    try {
      const product = await api.getProductById(id);
      const pt = product.priceType || 'PRICE_WEIGHT';
      this.setData({
        priceTypeLabel: this.getPriceTypeLabel(pt),
        form: {
          name: product.name || '',
          categoryId: product.categoryId,
          priceType: pt,
          price: String(product.price || ''),
          costPrice: String(product.costPrice || ''),
          stock: String(product.stock || ''),
          unit: product.unit || '斤',
          warningStock: String(product.warningStock || '10'),
          status: product.status
        }
      });
    } catch (e) {
      util.toast('加载商品信息失败');
    }
  },

  // 表单字段变化
  onFieldChange(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ ['form.' + field]: e.detail.value });
  },

  // 选择分类
  onCategoryChange(e) {
    const index = e.detail.value;
    this.setData({ 'form.categoryId': this.data.categories[index].id });
  },

  // 选择计价方式
  onPriceTypeChange(e) {
    const pt = this.data.priceTypes[e.detail.value];
    this.setData({ 'form.priceType': pt.value, priceTypeLabel: pt.label });
  },

  // 选择单位
  onUnitChange(e) {
    this.setData({ 'form.unit': this.data.units[e.detail.value] });
  },

  // 保存
  async onSave() {
    const form = this.data.form;
    if (!form.name) { util.toast('请输入商品名称'); return; }
    if (!form.categoryId) { util.toast('请选择分类'); return; }
    if (!form.price || parseFloat(form.price) <= 0) { util.toast('请输入有效售价'); return; }

    const data = {
      name: form.name,
      categoryId: form.categoryId,
      priceType: form.priceType,
      price: parseFloat(form.price),
      costPrice: parseFloat(form.costPrice) || 0,
      stock: parseFloat(form.stock) || 0,
      unit: form.unit,
      warningStock: parseFloat(form.warningStock) || 10,
      status: form.status
    };

    try {
      if (this.data.isEdit) {
        data.id = parseInt(this.data.id);
        await api.updateProduct(data);
      } else {
        await api.addProduct(data);
      }
      util.toast('保存成功', 'success');
      setTimeout(() => wx.navigateBack(), 1500);
    } catch (e) { /* handled */ }
  }
});

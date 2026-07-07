const api = require('../../utils/api');
const util = require('../../utils/util');

Page({
  data: {
    products: [],
    categories: [],
    currentCategoryId: null,
    searchName: '',
    page: 1,
    size: 20,
    total: 0,
    totalPages: 0,
    loading: false,
    hasMore: true
  },

  onLoad() {
    this.loadCategories();
    this.loadProducts();
  },

  onShow() {
    // 从详情页返回时刷新
    this.loadProducts(true);
  },

  onPullDownRefresh() {
    this.loadProducts(true).then(() => wx.stopPullDownRefresh());
  },

  onReachBottom() {
    this.loadMore();
  },

  loadMore() {
    if (!this.data.hasMore || this.data.loading) return;
    const next = this.data.page + 1;
    this.setData({ page: next }, () => this.loadProducts());
  },

  // 加载分类
  async loadCategories() {
    try {
      const tree = await api.getCategoryTree();
      this.setData({ categories: tree || [] });
    } catch (e) { /* ignore */ }
  },

  // 加载商品列表
  async loadProducts(reset = false) {
    if (this.data.loading) return;
    if (reset) this.setData({ page: 1, products: [], hasMore: true });

    this.setData({ loading: true });
    try {
      const params = {
        page: this.data.page,
        size: 20
      };
      if (this.data.searchName) params.name = this.data.searchName;
      if (this.data.currentCategoryId) params.categoryId = this.data.currentCategoryId;

      const result = await api.getProducts(params);
      const records = (result.records || []).map(p => ({
        ...p,
        priceTypeText: p.priceType === 'PRICE_WEIGHT' ? '按斤' : p.priceType === 'PRICE_PER' ? '按份' : p.priceType === 'PRICE_UNIT' ? '按个' : p.priceType,
        formattedPrice: parseFloat(p.price || 0).toFixed(2)
      }));
      const list = reset ? records : [...this.data.products, ...records];
      const total = result.total || 0;
      this.setData({
        products: list,
        total: total,
        totalPages: Math.ceil(total / this.data.size),
        hasMore: list.length < total
      });
    } catch (e) {
      console.error('加载商品失败', e);
    } finally {
      this.setData({ loading: false });
    }
  },

  // 搜索
  onSearch(e) {
    this.setData({ searchName: e.detail.value });
  },
  doSearch() {
    this.loadProducts(true);
  },

  // 分类筛选
  onCategoryTap(e) {
    const id = e.currentTarget.dataset.id;
    this.setData({ currentCategoryId: id === this.data.currentCategoryId ? null : id });
    this.loadProducts(true);
  },

  // 跳转商品详情
  goDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/product/detail?id=' + id });
  },

  // 新增商品
  goAdd() {
    wx.navigateTo({ url: '/pages/product/detail' });
  },

  // 快速上下架
  async toggleStatus(e) {
    const { id, status } = e.currentTarget.dataset;
    try {
      if (status === 1) {
        await api.takeOffSale(id);
      } else {
        await api.putOnSale(id);
      }
      util.toast('操作成功', 'success');
      this.loadProducts(true);
    } catch (e) { /* handled in api */ }
  }
});

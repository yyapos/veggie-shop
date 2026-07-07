const api = require('../../utils/api');
const util = require('../../utils/util');

Page({
  data: {
    categories: [],       // 当前层级的分类列表
    parentId: 0,           // 当前父级ID，0=根分类
    parentName: '',        // 父级名称，用于导航
    breadcrumb: [],        // 面包屑导航
    showForm: false,
    isEdit: false,
    form: { id: null, name: '', parentId: 0, icon: '' },
    emojis: ['🥬','🥒','🍅','🥔','🧅','🥕','🌽','🍆','🥦','🍄','🧄','🥜','🍎','🍊','🍋','🍇','🍓','🍑','🥭','🍌','🧂','🍚','🍞','🥛','🧃','🥫','📦'],
    showProducts: false,   // 是否显示商品列表（叶子节点）
    currentCategoryId: null,
    currentCategoryName: '',
    products: [],
    page: 1, total: 0, totalPages: 0, hasMore: false, loading: false
  },

  onLoad(options) {
    const parentId = parseInt(options.parentId) || 0;
    const parentName = options.name || '';
    const breadcrumb = options.breadcrumb ? JSON.parse(decodeURIComponent(options.breadcrumb)) : [];
    this.setData({ parentId, parentName, breadcrumb });
    if (parentId === 0) {
      wx.setNavigationBarTitle({ title: '商品分类' });
    } else {
      wx.setNavigationBarTitle({ title: parentName || '商品分类' });
    }
    this.loadCategories();
  },

  onShow() {
    const refresh = wx.getStorageSync('catNeedRefresh');
    if (refresh) {
      wx.removeStorageSync('catNeedRefresh');
      this.loadCategories();
    }
  },

  async loadCategories() {
    try {
      const tree = await api.getCategoryTree();
      // 找到当前层级
      let cats = tree || [];
      if (this.data.parentId > 0) {
        const node = this.findNode(tree, this.data.parentId);
        cats = node ? (node.children || []) : [];
      }
      this.setData({ categories: cats });
    } catch (e) {
      console.error(e);
    }
  },

  findNode(tree, id) {
    for (const node of tree) {
      if (node.id === id) return node;
      if (node.children) {
        const found = this.findNode(node.children, id);
        if (found) return found;
      }
    }
    return null;
  },

  // 点击分类 → 进入下级
  tapCategory(e) {
    const { id, name } = e.currentTarget.dataset;
    const bc = [...this.data.breadcrumb, { id: this.data.parentId, name: this.data.parentName || '首页' }];
    wx.navigateTo({
      url: '/pages/product/product?parentId=' + id + '&name=' + name + '&breadcrumb=' + encodeURIComponent(JSON.stringify(bc))
    });
  },

  // 面包屑返回
  tapBreadcrumb(e) {
    const { index } = e.currentTarget.dataset;
    const idx = parseInt(index);
    if (idx < 0) {
      // 回到根
      wx.redirectTo({ url: '/pages/product/product' });
    } else {
      const bc = this.data.breadcrumb.slice(0, idx);
      const target = this.data.breadcrumb[idx];
      wx.redirectTo({
        url: '/pages/product/product?parentId=' + target.id + '&name=' + target.name + '&breadcrumb=' + encodeURIComponent(JSON.stringify(bc))
      });
    }
  },

  // 查看该分类下的商品
  viewProducts(e) {
    const { id, name } = e.currentTarget.dataset;
    this.setData({ showProducts: true, currentCategoryId: id, currentCategoryName: name, products: [], page: 1 });
    this.loadProducts(true);
  },

  async loadProducts(reset = false) {
    if (this.data.loading) return;
    if (reset) this.setData({ page: 1, products: [], hasMore: true, total: 0, totalPages: 0 });
    this.setData({ loading: true });
    try {
      const result = await api.getProducts({
        page: this.data.page,
        size: 20,
        categoryId: this.data.currentCategoryId
      });
      const records = (result.records || []).map(p => ({
        ...p,
        priceTypeText: p.priceType === 'PRICE_WEIGHT' ? '按斤' : p.priceType === 'PRICE_PER' ? '按份' : p.priceType === 'PRICE_UNIT' ? '按个' : p.priceType,
        formattedPrice: parseFloat(p.price || 0).toFixed(2)
      }));
      const list = reset ? records : [...this.data.products, ...records];
      const total = result.total || 0;
      this.setData({ products: list, total, totalPages: Math.ceil(total / 20), hasMore: list.length < total });
    } catch (e) { console.error(e); }
    finally { this.setData({ loading: false }); }
  },

  loadMore() {
    if (!this.data.hasMore || this.data.loading) return;
    const next = this.data.page + 1;
    this.setData({ page: next }, () => this.loadProducts());
  },

  hideProducts() {
    this.setData({ showProducts: false });
  },

  // ---- 分类管理 ----
  showAdd(e) {
    const pid = e.currentTarget.dataset.parentId || this.data.parentId;
    this.setData({ showForm: true, isEdit: false, form: { id: null, name: '', parentId: pid, icon: '' } });
  },

  showEdit(e) {
    const { id, name, icon } = e.currentTarget.dataset;
    this.setData({ showForm: true, isEdit: true, form: { id, name, parentId: this.data.parentId, icon: icon || '' } });
  },

  onNameInput(e) { this.setData({ 'form.name': e.detail.value }); },

  async saveCategory() {
    const f = this.data.form;
    if (!f.name.trim()) { util.toast('请输入名称'); return; }
    try {
      if (this.data.isEdit) {
        await api.updateCategory({ id: f.id, name: f.name, parentId: f.parentId, icon: f.icon });
      } else {
        await api.addCategory({ name: f.name, parentId: f.parentId, icon: f.icon });
      }
      util.toast('保存成功', 'success');
      this.setData({ showForm: false });
      this.loadCategories();
    } catch (e) { /* handled */ }
  },

  onSelectIcon(e) {
    const icon = e.currentTarget.dataset.icon;
    this.setData({ 'form.icon': icon });
  },

  async deleteCategory(e) {
    const { id, name } = e.currentTarget.dataset;
    const res = await new Promise(r => wx.showModal({ title: '删除「' + name + '」？', success: r }));
    if (res.confirm) {
      await api.deleteCategory(id);
      util.toast('已删除');
      this.loadCategories();
    }
  },

  hideForm() { this.setData({ showForm: false }); }
});

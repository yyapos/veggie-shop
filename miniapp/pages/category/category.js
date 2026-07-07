const api = require('../../utils/api');
const util = require('../../utils/util');

Page({
  data: {
    categories: [],
    showForm: false,
    isEdit: false,
    form: { id: null, name: '', parentId: 0, icon: '' },
    parentOptions: [],
    emojis: ['🥬','🥒','🍅','🥔','🧅','🥕','🌽','🍆','🥦','🍄','🧄','🥜','🍎','🍊','🍋','🍇','🍓','🍑','🥭','🍌','🧂','🍚','🍞','🥛','🧃','🥫','📦']
  },

  onShow() {
    this.loadCategories();
  },

  async loadCategories() {
    try {
      const tree = await api.getCategoryTree();
      const flat = this.flatten(tree);
      this.setData({ categories: tree || [], parentOptions: flat });
    } catch (e) {
      console.error(e);
    }
  },

  flatten(tree, level = 0) {
    let result = [];
    (tree || []).forEach(item => {
      result.push({ id: item.id, name: '　'.repeat(level) + item.name });
      if (item.children && item.children.length) {
        result = result.concat(this.flatten(item.children, level + 1));
      }
    });
    return result;
  },

  showAdd(parentId) {
    this.setData({
      showForm: true,
      isEdit: false,
      form: { id: null, name: '', parentId: parentId || 0, icon: '' }
    });
  },

  showEdit(e) {
    const { id, name, parentid, icon } = e.currentTarget.dataset;
    this.setData({
      showForm: true,
      isEdit: true,
      form: { id, name, parentId: parentid || 0, icon: icon || '' }
    });
  },

  onNameInput(e) {
    this.setData({ 'form.name': e.detail.value });
  },

  onParentChange(e) {
    const idx = e.detail.value;
    this.setData({ 'form.parentId': this.data.parentOptions[idx].id });
  },

  async saveCategory() {
    const { id, name, parentId, icon } = this.data.form;
    if (!name.trim()) { util.toast('请输入分类名称'); return; }
    try {
      if (this.data.isEdit) {
        await api.updateCategory({ id, name, parentId, icon });
      } else {
        await api.addCategory({ name, parentId, icon });
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
    wx.showModal({
      title: '确认删除',
      content: '删除「' + name + '」？',
      success: async (res) => {
        if (res.confirm) {
          await api.deleteCategory(id);
          util.toast('已删除', 'success');
          this.loadCategories();
        }
      }
    });
  },

  hideForm() {
    this.setData({ showForm: false });
  }
});

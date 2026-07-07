const api = require('../../utils/api');
const util = require('../../utils/util');

Page({
  data: {
    today: null,
    todayStats: null,
    weekTrend: [],
    period: 'today' // today / week / month
  },

  onShow() {
    this.loadDashboard();
    if (this.data.period !== 'today') {
      this.loadTrendData();
    }
  },

  onPullDownRefresh() {
    Promise.all([
      this.loadDashboard(),
      this.data.period !== 'today' ? this.loadTrendData() : Promise.resolve()
    ]).then(() => wx.stopPullDownRefresh());
  },

  async loadDashboard() {
    try {
      const data = await api.getDashboard();
      this.setData({
        today: data.today,
        todayStats: data.todayStats,
        weekTrend: data.weekTrend || []
      });
    } catch (e) {
      console.error('加载看板数据失败', e);
    }
  },

  async loadTrendData() {
    const period = this.data.period;
    let startDate, endDate = util.getToday();

    if (period === 'today') {
      startDate = util.getToday();
    } else if (period === 'week') {
      startDate = util.getWeekStart();
    } else {
      startDate = util.getMonthStart();
    }

    try {
      const summaries = await api.get('/api/summary/by-date?' +
        'startDate=' + startDate + '&endDate=' + endDate);
      this.setData({ weekTrend: summaries || [] });
    } catch (e) {
      this.setData({ weekTrend: this.data.weekTrend });
    }
  },

  // 切换时间维度
  switchPeriod(e) {
    const period = e.currentTarget.dataset.period;
    this.setData({ period });
    this.loadTrendData();
  }
});

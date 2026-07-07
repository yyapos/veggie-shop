App({
  globalData: {
    baseUrl: 'http://localhost:8080',  // 后端 API 基础地址，上线改为云服务器地址
    userInfo: null
  },

  onLaunch() {
    // 小程序启动时执行
    console.log('蔬菜店管理系统启动');
  }
});

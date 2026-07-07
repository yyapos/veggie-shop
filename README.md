# 🥬 蔬菜店管理系统 (Veggie Shop)

一个为小村庄蔬菜商店设计的微信小程序 + Spring Boot 后端管理系统。

## 功能模块

| 模块 | 说明 |
|------|------|
| 📊 首页概览 | 今日销售额、利润、赊账、库存预警 |
| 🛒 商品管理 | 商品分类、添加/编辑/上下架 |
| 💰 销售记账 | 选商品→输数量→自动算金额→确认（支持按斤/份/个） |
| 📦 进货管理 | 选商品→输数量和进价→记录批发市场 |
| 📝 赊账管理 | 赊账人列表、赊账记录、还款 |
| 📈 数据看板 | 今日/本周/本月销售趋势 |

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.1.5 |
| ORM | MyBatis-Plus 3.5.3.2 |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis + Lettuce |
| 连接池 | Druid 1.2.20 |
| API 文档 | Knife4j 4.3.0 |
| 工具库 | Lombok + Hutool 5.8.23 |
| 前端 | 微信小程序原生框架 |

## 项目结构

```
veggie-shop/
├── pom.xml                          # Maven 配置
├── veggie_shop.sql                   # 数据库初始化脚本（含示例数据）
├── README.md
├── src/main/java/com/veggie/shop/
│   ├── VeggieShopApplication.java   # 启动类
│   ├── common/
│   │   ├── Result.java              # 统一响应封装
│   │   ├── GlobalExceptionHandler.java  # 全局异常处理
│   │   └── MyMetaObjectHandler.java     # MP 审计字段自动填充
│   ├── config/
│   │   ├── MyBatisPlusConfig.java   # MP 分页/逻辑删除配置
│   │   ├── RedisConfig.java         # Redis 序列化配置
│   │   ├── Knife4jConfig.java       # API 文档配置
│   │   └── CorsConfig.java          # 跨域配置
│   ├── util/
│   │   └── RedisUtil.java           # Redis 工具类
│   ├── entity/                      # 实体类
│   ├── mapper/                      # MyBatis Mapper
│   ├── service/                     # 业务接口
│   │   └── impl/                    # 业务实现
│   └── controller/                  # REST 控制器
├── src/main/resources/
│   └── application.yml              # 应用配置
└── miniapp/                         # 微信小程序前端
    ├── app.js / app.json / app.wxss
    ├── utils/
    │   ├── api.js                   # API 请求封装
    │   └── util.js                  # 工具函数
    └── pages/
        ├── index/                   # 首页-经营概览
        ├── product/                 # 商品管理
        ├── sale/                    # 销售记账
        ├── purchase/                # 进货管理
        ├── debtor/                  # 赊账管理
        └── dashboard/               # 数据看板
```

## 快速启动

### 1. 环境要求

- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.8+
- 微信开发者工具

### 2. 初始化数据库

```bash
mysql -u root -p < veggie_shop.sql
```

### 3. 修改配置

编辑 `src/main/resources/application.yml`，修改数据库和 Redis 连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/veggie_shop?...
    username: your_username
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password
```

### 4. 启动后端

```bash
# 打包
mvn clean package -DskipTests

# 运行
mvn spring-boot:run

# 或直接运行 jar
java -jar target/veggie-shop-1.0.0.jar
```

### 5. 访问 API 文档

启动后访问：http://localhost:8080/doc.html

### 6. 启动小程序

1. 打开微信开发者工具
2. 导入项目，目录选择 `veggie-shop/miniapp`
3. 填写 AppID（或使用测试号）
4. 点击编译预览

## API 接口概览

| 模块 | 路径前缀 | 说明 |
|------|----------|------|
| 商品分类 | `/api/category` | 分类 CRUD |
| 商品管理 | `/api/product` | 商品 CRUD + 上下架 |
| 进货记录 | `/api/purchase` | 进货记录 CRUD |
| 销售记录 | `/api/sale` | 销售记录 + 库存扣减 |
| 赊账人 | `/api/debtor` | 赊账人管理 |
| 赊账记录 | `/api/debt` | 赊账记录管理 |
| 还款记录 | `/api/repayment` | 还款记录管理 |
| 每日汇总 | `/api/summary` | 每日经营汇总 |
| 首页概览 | `/api/dashboard` | 今日概览/趋势数据 |

## 核心设计

- **库存并发控制**：销售时通过 Redis 分布式锁防止库存超卖
- **逻辑删除**：所有表支持逻辑删除，数据可恢复
- **审计字段**：create_time/update_time/create_by/update_by 自动填充
- **三种计价方式**：PRICE_WEIGHT（按斤）、PRICE_PER（按份）、PRICE_UNIT（按个）

## License

MIT

-- =============================================
-- 蔬菜店管理系统 数据库初始化脚本
-- Database: veggie_shop
-- =============================================

CREATE DATABASE IF NOT EXISTS veggie_shop DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE veggie_shop;

-- =============================================
-- 1. 商品分类表
-- =============================================
DROP TABLE IF EXISTS category;
CREATE TABLE category (
    id          BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    name        VARCHAR(64)     NOT NULL                 COMMENT '分类名称',
    icon        VARCHAR(10)     DEFAULT ''               COMMENT '图标emoji',
    parent_id   BIGINT          DEFAULT 0                COMMENT '父级分类ID，0为顶级',
    sort        INT             DEFAULT 0                COMMENT '排序值，越小越靠前',
    status      TINYINT         DEFAULT 1                COMMENT '状态：1启用 0禁用',
    -- 审计字段
    create_time DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by   BIGINT          DEFAULT NULL             COMMENT '创建人ID',
    update_by   BIGINT          DEFAULT NULL             COMMENT '更新人ID',
    deleted     TINYINT         DEFAULT 0                COMMENT '逻辑删除：0未删除 1已删除',
    PRIMARY KEY (id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- =============================================
-- 2. 商品表
-- =============================================
DROP TABLE IF EXISTS product;
CREATE TABLE product (
    id            BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    category_id   BIGINT        NOT NULL                 COMMENT '所属分类ID',
    name          VARCHAR(128)  NOT NULL                 COMMENT '商品名称',
    price_type    VARCHAR(32)   NOT NULL DEFAULT 'PRICE_WEIGHT' COMMENT '计价方式：PRICE_WEIGHT按斤 PRICE_PER按份 PRICE_UNIT按个',
    price         DECIMAL(10,2) NOT NULL DEFAULT 0.00    COMMENT '售价',
    cost_price    DECIMAL(10,2) NOT NULL DEFAULT 0.00    COMMENT '进价（成本价）',
    stock         DECIMAL(10,2) NOT NULL DEFAULT 0.00    COMMENT '库存',
    unit          VARCHAR(16)   NOT NULL DEFAULT '斤'     COMMENT '单位：斤/袋/个/把',
    image         VARCHAR(512)  DEFAULT NULL             COMMENT '商品图片URL',
    status        TINYINT       DEFAULT 1                COMMENT '状态：1上架 0下架',
    warning_stock DECIMAL(10,2) DEFAULT 10.00            COMMENT '低库存预警阈值',
    -- 审计字段
    create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by     BIGINT        DEFAULT NULL             COMMENT '创建人ID',
    update_by     BIGINT        DEFAULT NULL             COMMENT '更新人ID',
    deleted       TINYINT       DEFAULT 0                COMMENT '逻辑删除：0未删除 1已删除',
    PRIMARY KEY (id),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- =============================================
-- 3. 进货记录表
-- =============================================
DROP TABLE IF EXISTS purchase_record;
CREATE TABLE purchase_record (
    id            BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    product_id    BIGINT        NOT NULL                 COMMENT '商品ID',
    quantity      DECIMAL(10,2) NOT NULL DEFAULT 0.00    COMMENT '进货数量',
    cost_price    DECIMAL(10,2) NOT NULL DEFAULT 0.00    COMMENT '进货单价',
    total_cost    DECIMAL(10,2) NOT NULL DEFAULT 0.00    COMMENT '总成本',
    market        VARCHAR(128)  DEFAULT NULL             COMMENT '批发市场名称',
    purchase_date DATE          NOT NULL                 COMMENT '进货日期',
    remark        VARCHAR(512)  DEFAULT NULL             COMMENT '备注',
    -- 审计字段
    create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by     BIGINT        DEFAULT NULL             COMMENT '创建人ID',
    update_by     BIGINT        DEFAULT NULL             COMMENT '更新人ID',
    deleted       TINYINT       DEFAULT 0                COMMENT '逻辑删除：0未删除 1已删除',
    PRIMARY KEY (id),
    INDEX idx_product_id (product_id),
    INDEX idx_purchase_date (purchase_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='进货记录表';

-- =============================================
-- 4. 销售记录表
-- =============================================
DROP TABLE IF EXISTS sale_record;
CREATE TABLE sale_record (
    id            BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    product_id    BIGINT        NOT NULL                 COMMENT '商品ID',
    quantity      DECIMAL(10,2) NOT NULL DEFAULT 0.00    COMMENT '销售数量',
    price         DECIMAL(10,2) NOT NULL DEFAULT 0.00    COMMENT '销售单价',
    total_amount  DECIMAL(10,2) NOT NULL DEFAULT 0.00    COMMENT '总金额',
    sale_date     DATE          NOT NULL                 COMMENT '销售日期',
    sale_type     VARCHAR(16)   NOT NULL DEFAULT 'CASH'  COMMENT '销售类型：CASH现金 DEBT赊账',
    -- 审计字段
    create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by     BIGINT        DEFAULT NULL             COMMENT '创建人ID',
    update_by     BIGINT        DEFAULT NULL             COMMENT '更新人ID',
    deleted       TINYINT       DEFAULT 0                COMMENT '逻辑删除：0未删除 1已删除',
    PRIMARY KEY (id),
    INDEX idx_product_id (product_id),
    INDEX idx_sale_date (sale_date),
    INDEX idx_sale_type (sale_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售记录表';

-- =============================================
-- 5. 赊账人表
-- =============================================
DROP TABLE IF EXISTS debtor;
CREATE TABLE debtor (
    id            BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    name          VARCHAR(64)   NOT NULL                 COMMENT '赊账人姓名',
    phone         VARCHAR(20)   DEFAULT NULL             COMMENT '联系电话',
    total_debt    DECIMAL(10,2) NOT NULL DEFAULT 0.00    COMMENT '累计欠款',
    paid_amount   DECIMAL(10,2) NOT NULL DEFAULT 0.00    COMMENT '已还金额',
    unpaid_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00    COMMENT '未还金额',
    status        TINYINT       DEFAULT 1                COMMENT '状态：1正常 0已结清',
    -- 审计字段
    create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by     BIGINT        DEFAULT NULL             COMMENT '创建人ID',
    update_by     BIGINT        DEFAULT NULL             COMMENT '更新人ID',
    deleted       TINYINT       DEFAULT 0                COMMENT '逻辑删除：0未删除 1已删除',
    PRIMARY KEY (id),
    INDEX idx_name (name),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赊账人表';

-- =============================================
-- 6. 赊账记录表
-- =============================================
DROP TABLE IF EXISTS debt_record;
CREATE TABLE debt_record (
    id            BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    debtor_id     BIGINT        NOT NULL                 COMMENT '赊账人ID',
    product_id    BIGINT        DEFAULT NULL             COMMENT '商品ID',
    quantity      DECIMAL(10,2) NOT NULL DEFAULT 0.00    COMMENT '数量',
    amount        DECIMAL(10,2) NOT NULL DEFAULT 0.00    COMMENT '赊账金额',
    debt_date     DATE          NOT NULL                 COMMENT '赊账日期',
    status        TINYINT       DEFAULT 0                COMMENT '状态：0未还 1已还',
    -- 审计字段
    create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by     BIGINT        DEFAULT NULL             COMMENT '创建人ID',
    update_by     BIGINT        DEFAULT NULL             COMMENT '更新人ID',
    deleted       TINYINT       DEFAULT 0                COMMENT '逻辑删除：0未删除 1已删除',
    PRIMARY KEY (id),
    INDEX idx_debtor_id (debtor_id),
    INDEX idx_debt_date (debt_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赊账记录表';

-- =============================================
-- 7. 还款记录表
-- =============================================
DROP TABLE IF EXISTS repayment_record;
CREATE TABLE repayment_record (
    id            BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    debtor_id     BIGINT        NOT NULL                 COMMENT '赊账人ID',
    amount        DECIMAL(10,2) NOT NULL DEFAULT 0.00    COMMENT '还款金额',
    repay_date    DATE          NOT NULL                 COMMENT '还款日期',
    remark        VARCHAR(512)  DEFAULT NULL             COMMENT '备注',
    -- 审计字段
    create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by     BIGINT        DEFAULT NULL             COMMENT '创建人ID',
    update_by     BIGINT        DEFAULT NULL             COMMENT '更新人ID',
    deleted       TINYINT       DEFAULT 0                COMMENT '逻辑删除：0未删除 1已删除',
    PRIMARY KEY (id),
    INDEX idx_debtor_id (debtor_id),
    INDEX idx_repay_date (repay_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='还款记录表';

-- =============================================
-- 8. 每日汇总表
-- =============================================
DROP TABLE IF EXISTS daily_summary;
CREATE TABLE daily_summary (
    id            BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    summary_date  DATE          NOT NULL                 COMMENT '汇总日期',
    total_sales   DECIMAL(10,2) NOT NULL DEFAULT 0.00    COMMENT '总销售额',
    total_cost    DECIMAL(10,2) NOT NULL DEFAULT 0.00    COMMENT '总成本',
    total_profit  DECIMAL(10,2) NOT NULL DEFAULT 0.00    COMMENT '总利润',
    total_debt    DECIMAL(10,2) NOT NULL DEFAULT 0.00    COMMENT '总赊账金额',
    order_count   INT           NOT NULL DEFAULT 0       COMMENT '订单数',
    -- 审计字段
    create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by     BIGINT        DEFAULT NULL             COMMENT '创建人ID',
    update_by     BIGINT        DEFAULT NULL             COMMENT '更新人ID',
    deleted       TINYINT       DEFAULT 0                COMMENT '逻辑删除：0未删除 1已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_summary_date (summary_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日汇总表';

-- =============================================
-- 示例数据
-- =============================================

-- 商品分类
INSERT INTO category (id, name, icon, parent_id, sort, status) VALUES
(1, '蔬菜', '🥬', 0, 1, 1),
(2, '水果', '🍎', 0, 2, 1),
(3, '粮油调料', '🧂', 0, 3, 1),
(4, '叶菜类', '🥗', 1, 1, 1),
(5, '根茎类', '🥔', 1, 2, 1),
(6, '茄果类', '🍅', 1, 3, 1),
(7, '瓜类', '🥒', 1, 4, 1),
(8, '浆果类', '🍓', 2, 1, 1),
(9, '柑橘类', '🍊', 2, 2, 1),
(10, '热带水果', '🥭', 2, 3, 1);

-- 商品（蔬菜水果）
INSERT INTO product (id, category_id, name, price_type, price, cost_price, stock, unit, status, warning_stock) VALUES
(1,  4, '大白菜',    'PRICE_WEIGHT', 1.50, 0.80,  50.00, '斤', 1, 20.00),
(2,  4, '菠菜',      'PRICE_WEIGHT', 4.00, 2.50,  30.00, '斤', 1, 10.00),
(3,  4, '生菜',      'PRICE_PER',    3.50, 2.00,  25.00, '袋', 1, 10.00),
(4,  5, '土豆',      'PRICE_WEIGHT', 2.50, 1.50,  80.00, '斤', 1, 20.00),
(5,  5, '胡萝卜',    'PRICE_WEIGHT', 3.00, 1.80,  40.00, '斤', 1, 15.00),
(6,  5, '白萝卜',    'PRICE_WEIGHT', 1.80, 1.00,  35.00, '斤', 1, 15.00),
(7,  6, '西红柿',    'PRICE_WEIGHT', 4.50, 3.00,  45.00, '斤', 1, 15.00),
(8,  6, '黄瓜',      'PRICE_WEIGHT', 3.50, 2.00,  40.00, '斤', 1, 15.00),
(9,  6, '茄子',      'PRICE_WEIGHT', 4.00, 2.50,  30.00, '斤', 1, 10.00),
(10, 7, '冬瓜',      'PRICE_WEIGHT', 2.00, 1.20,  60.00, '斤', 1, 20.00),
(11, 7, '南瓜',      'PRICE_WEIGHT', 2.50, 1.50,  35.00, '斤', 1, 15.00),
(12, 8, '草莓',      'PRICE_PER',   15.00, 10.00, 10.00, '盒', 1, 5.00),
(13, 8, '葡萄',      'PRICE_WEIGHT', 8.00, 5.00,  20.00, '斤', 1, 5.00),
(14, 9, '橘子',      'PRICE_WEIGHT', 5.00, 3.00,  30.00, '斤', 1, 10.00),
(15, 9, '橙子',      'PRICE_WEIGHT', 6.00, 3.50,  25.00, '斤', 1, 10.00),
(16, 10, '香蕉',     'PRICE_WEIGHT', 3.50, 2.50,  20.00, '斤', 1, 10.00),
(17, 3,  '大米(5kg)','PRICE_PER',   35.00, 28.00, 15.00, '袋', 1, 5.00),
(18, 3,  '花生油',   'PRICE_PER',   89.00, 75.00, 8.00,  '桶', 1, 3.00),
(19, 7, '玉米',      'PRICE_UNIT',  3.00,  2.00,  50.00, '个', 1, 15.00),
(20, 6, '青椒',      'PRICE_WEIGHT', 5.00, 3.00,  25.00, '斤', 1, 10.00);

-- 赊账人
INSERT INTO debtor (id, name, phone, total_debt, paid_amount, unpaid_amount, status) VALUES
(1, '张三', '13800001111', 150.00, 100.00, 50.00, 1),
(2, '李四', '13800002222', 80.00,  0.00,   80.00, 1),
(3, '王五', '13800003333', 200.00, 200.00, 0.00,  0);

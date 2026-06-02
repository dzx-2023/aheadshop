-- ============================================================
-- AheadShop-Plus 初始数据
-- 执行前请确保已执行 DDL 创建好 4 个库和所有表
-- 注意：有 deleted 字段的表（继承 BaseEntity）必须包含 version 列
-- ============================================================

-- ------------------------------------------------------------
-- 1. 用户库 aheadshop_user
-- ------------------------------------------------------------

-- 超级管理员（admin / admin123，BCrypt 加密）
INSERT INTO aheadshop_user.user (id, username, password, nickname, phone, email, gender, status, version)
VALUES (1, 'admin', '$2a$10$8t3jtaGATm.yqzp1S5l3VeCMwrNHbsABgyY1j/1OTusa22YKD7iei', '超级管理员', '13800000001', 'admin@aheadshop.com', 1, 1, 1);

-- 测试用户
INSERT INTO aheadshop_user.user (id, username, password, nickname, phone, email, gender, status, version)
VALUES (2, 'testuser', '$2a$10$8t3jtaGATm.yqzp1S5l3VeCMwrNHbsABgyY1j/1OTusa22YKD7iei', '测试用户', '13800000002', 'test@aheadshop.com', 1, 1, 1);

-- 默认角色（无 deleted/version）
INSERT INTO aheadshop_user.role (id, role_name, role_key, status) VALUES (1, '超级管理员', 'ADMIN', 1);
INSERT INTO aheadshop_user.role (id, role_name, role_key, status) VALUES (2, '普通用户', 'USER', 1);

-- 用户角色关联（无 deleted/version）
INSERT INTO aheadshop_user.user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO aheadshop_user.user_role (user_id, role_id) VALUES (2, 2);

-- 菜单权限（无 deleted/version）
INSERT INTO aheadshop_user.menu (id, parent_id, menu_name, path, component, perms, menu_type, icon, sort_order) VALUES
(1,  0, '系统管理',   '/system',       NULL,                  NULL,                    'M', 'setting',  1),
(2,  1, '用户管理',   '/system/user',  'system/user/index',   'system:user:list',      'C', 'user',     1),
(3,  1, '角色管理',   '/system/role',  'system/role/index',   'system:role:list',      'C', 'peoples',  2),
(4,  1, '菜单管理',   '/system/menu',  'system/menu/index',   'system:menu:list',      'C', 'tree-table', 3),
(5,  0, '商品管理',   '/product',      NULL,                  NULL,                    'M', 'shopping', 2),
(6,  5, '分类管理',   '/product/category', 'product/category/index', 'product:category:list', 'C', 'tree', 1),
(7,  5, '品牌管理',   '/product/brand',    'product/brand/index',    'product:brand:list',    'C', 'brand', 2),
(8,  5, 'SPU管理',    '/product/spu',      'product/spu/index',      'product:spu:list',      'C', 'goods',  3),
(9,  5, 'SKU管理',    '/product/sku',      'product/sku/index',      'product:sku:list',      'C', 'list',   4),
(10, 0, '订单管理',   '/order',        NULL,                  NULL,                    'M', 'form',     3),
(11, 10, '订单列表',  '/order/list',   'order/list/index',    'order:list:list',       'C', 'list',     1),
(12, 10, '退款管理',  '/order/refund', 'order/refund/index',  'order:refund:list',     'C', 'money',    2);

-- ADMIN 拥有全部菜单权限
INSERT INTO aheadshop_user.role_menu (role_id, menu_id)
SELECT 1, id FROM aheadshop_user.menu;

-- 测试用户收货地址（有 deleted，需 version）
INSERT INTO aheadshop_user.user_address (user_id, receiver_name, receiver_phone, province, city, district, detail_address, is_default, version)
VALUES (2, '张三', '13800000002', '广东省', '深圳市', '南山区', '科技园南路100号A栋1001室', 1, 1);

-- ------------------------------------------------------------
-- 2. 商品库 aheadshop_product
-- ------------------------------------------------------------

-- 商品分类（有 deleted，需 version）
-- 一级分类
INSERT INTO aheadshop_product.category (id, parent_id, name, level, sort_order, version) VALUES
(1,  0, '手机数码', 1, 1, 1),
(2,  0, '电脑办公', 1, 2, 1),
(3,  0, '家用电器', 1, 3, 1),
(4,  0, '服饰鞋包', 1, 4, 1);

-- 二级分类
INSERT INTO aheadshop_product.category (id, parent_id, name, level, sort_order, version) VALUES
(10, 1, '手机',     2, 1, 1),
(11, 1, '平板电脑', 2, 2, 1),
(12, 1, '耳机',     2, 3, 1),
(20, 2, '笔记本',   2, 1, 1),
(21, 2, '台式机',   2, 2, 1),
(22, 2, '显示器',   2, 3, 1),
(30, 3, '冰箱',     2, 1, 1),
(31, 3, '洗衣机',   2, 2, 1),
(32, 3, '空调',     2, 3, 1),
(40, 4, '男装',     2, 1, 1),
(41, 4, '女装',     2, 2, 1),
(42, 4, '运动鞋',   2, 3, 1);

-- 三级分类
INSERT INTO aheadshop_product.category (id, parent_id, name, level, sort_order, version) VALUES
(100, 10, '智能手机',   3, 1, 1),
(101, 10, '游戏手机',   3, 2, 1),
(110, 11, '安卓平板',   3, 1, 1),
(111, 11, 'iPad',       3, 2, 1),
(120, 12, '有线耳机',   3, 1, 1),
(121, 12, '蓝牙耳机',   3, 2, 1),
(200, 20, '轻薄本',     3, 1, 1),
(201, 20, '游戏本',     3, 2, 1),
(210, 21, '办公台式机', 3, 1, 1),
(220, 22, '电竞显示器', 3, 1, 1),
(300, 30, '双门冰箱',   3, 1, 1),
(310, 31, '滚筒洗衣机', 3, 1, 1),
(320, 32, '壁挂式空调', 3, 1, 1);

-- 品牌（有 deleted，需 version）
INSERT INTO aheadshop_product.brand (id, name, logo, description, sort_order, version) VALUES
(1,  '华为',    'https://img.aheadshop.com/brand/huawei.png',   '华为技术有限公司',    1, 1),
(2,  '苹果',    'https://img.aheadshop.com/brand/apple.png',    'Apple Inc.',          2, 1),
(3,  '小米',    'https://img.aheadshop.com/brand/xiaomi.png',   '小米科技有限公司',    3, 1),
(4,  '联想',    'https://img.aheadshop.com/brand/lenovo.png',   '联想集团',            4, 1),
(5,  '戴尔',    'https://img.aheadshop.com/brand/dell.png',     'Dell Technologies',   5, 1),
(6,  '三星',    'https://img.aheadshop.com/brand/samsung.png',  'Samsung Electronics', 6, 1),
(7,  '海尔',    'https://img.aheadshop.com/brand/haier.png',    '海尔集团',            7, 1),
(8,  '格力',    'https://img.aheadshop.com/brand/gree.png',     '格力电器',            8, 1),
(9,  '索尼',    'https://img.aheadshop.com/brand/sony.png',     'Sony Corporation',    9, 1),
(10, 'Nike',    'https://img.aheadshop.com/brand/nike.png',     '耐克',               10, 1);

-- 分类品牌关联（无 deleted/version）
INSERT INTO aheadshop_product.category_brand (category_id, brand_id) VALUES
(100, 1), (100, 2), (100, 3), (100, 6),
(101, 1), (101, 3),
(110, 1), (110, 3), (110, 6),
(111, 2),
(120, 9), (121, 9), (121, 2),
(200, 4), (200, 5), (200, 2),
(201, 4), (201, 5),
(210, 4), (210, 5),
(220, 5), (220, 6),
(300, 7), (310, 7), (320, 8);

-- 商品属性（无 deleted/version）
INSERT INTO aheadshop_product.attribute (category_id, name, type, input_type, value_list, sort_order) VALUES
(100, '颜色',   1, 1, '黑色,白色,蓝色,绿色,紫色', 1),
(100, '存储',   1, 1, '128GB,256GB,512GB,1TB',     2),
(100, '内存',   1, 1, '8GB,12GB,16GB',             3),
(200, '颜色',   1, 1, '银色,灰色,黑色',             1),
(200, '处理器', 1, 1, 'i5,i7,i9,M3,M3 Pro',       2),
(200, '内存',   1, 1, '8GB,16GB,32GB',             3),
(200, '硬盘',   1, 1, '256GB,512GB,1TB',           4);

-- SPU（有 deleted，需 version）
INSERT INTO aheadshop_product.spu (id, name, subtitle, category_id, brand_id, main_image, images, status, sales, version) VALUES
(1, 'Huawei Mate 70 Pro',   '旗舰芯片 · 超感知影像 · 鸿蒙系统',         100, 1,
    'https://img.aheadshop.com/spu/mate70pro-main.jpg',
    '["https://img.aheadshop.com/spu/mate70pro-1.jpg","https://img.aheadshop.com/spu/mate70pro-2.jpg"]',
    1, 580, 1),
(2, 'iPhone 16 Pro Max',    'A18 Pro 芯片 · 钛金属设计 · 4K 120fps',    100, 2,
    'https://img.aheadshop.com/spu/iphone16promax-main.jpg',
    '["https://img.aheadshop.com/spu/iphone16promax-1.jpg","https://img.aheadshop.com/spu/iphone16promax-2.jpg"]',
    1, 1200, 1),
(3, 'Xiaomi 15 Ultra',      '徕卡光学 · 骁龙 8 至尊版 · 5400mAh',      100, 3,
    'https://img.aheadshop.com/spu/mi15ultra-main.jpg',
    '["https://img.aheadshop.com/spu/mi15ultra-1.jpg","https://img.aheadshop.com/spu/mi15ultra-2.jpg"]',
    1, 320, 1),
(4, 'MacBook Pro 14 M3 Pro', 'M3 Pro 芯片 · Liquid Retina XDR · 18h续航', 200, 2,
    'https://img.aheadshop.com/spu/macbookpro14-main.jpg',
    '["https://img.aheadshop.com/spu/macbookpro14-1.jpg"]',
    1, 210, 1),
(5, 'ThinkPad X1 Carbon',    '14英寸 · 超轻薄 · 商务旗舰',              200, 4,
    'https://img.aheadshop.com/spu/x1carbon-main.jpg',
    '["https://img.aheadshop.com/spu/x1carbon-1.jpg"]',
    1, 150, 1),
(6, 'Sony WH-1000XM5',      '行业领先降噪 · 30h续航 · LDAC',           121, 9,
    'https://img.aheadshop.com/spu/wh1000xm5-main.jpg',
    '["https://img.aheadshop.com/spu/wh1000xm5-1.jpg"]',
    1, 890, 1);

-- SKU（有 deleted，需 version）
INSERT INTO aheadshop_product.sku (id, spu_id, sku_name, specs, price, original_price, stock, image, version) VALUES
(101, 1, 'Huawei Mate 70 Pro 128GB 曜石黑',  '{"颜色":"曜石黑","存储":"128GB","内存":"12GB"}', 5999.00, 6499.00, 200, 'https://img.aheadshop.com/sku/mate70pro-128-black.jpg', 1),
(102, 1, 'Huawei Mate 70 Pro 256GB 曜石黑',  '{"颜色":"曜石黑","存储":"256GB","内存":"12GB"}', 6499.00, 6999.00, 150, 'https://img.aheadshop.com/sku/mate70pro-256-black.jpg', 1),
(103, 1, 'Huawei Mate 70 Pro 512GB 星河银',  '{"颜色":"星河银","存储":"512GB","内存":"12GB"}', 7499.00, 7999.00, 100, 'https://img.aheadshop.com/sku/mate70pro-512-silver.jpg', 1),
(201, 2, 'iPhone 16 Pro Max 256GB 沙漠钛金属', '{"颜色":"沙漠钛金属","存储":"256GB","内存":"8GB"}', 9999.00, 9999.00, 300, 'https://img.aheadshop.com/sku/iphone16pm-256-titan.jpg', 1),
(202, 2, 'iPhone 16 Pro Max 512GB 原色钛金属', '{"颜色":"原色钛金属","存储":"512GB","内存":"8GB"}', 11999.00, 11999.00, 200, 'https://img.aheadshop.com/sku/iphone16pm-512-titan.jpg', 1),
(203, 2, 'iPhone 16 Pro Max 1TB 黑色钛金属',   '{"颜色":"黑色钛金属","存储":"1TB","内存":"8GB"}',   13999.00, 13999.00, 80,  'https://img.aheadshop.com/sku/iphone16pm-1tb-black.jpg', 1),
(301, 3, 'Xiaomi 15 Ultra 256GB 黑色', '{"颜色":"黑色","存储":"256GB","内存":"16GB"}', 5299.00, 5999.00, 180, 'https://img.aheadshop.com/sku/mi15u-256-black.jpg', 1),
(302, 3, 'Xiaomi 15 Ultra 512GB 白色', '{"颜色":"白色","存储":"512GB","内存":"16GB"}', 5999.00, 6499.00, 120, 'https://img.aheadshop.com/sku/mi15u-512-white.jpg', 1),
(401, 4, 'MacBook Pro 14 M3 Pro 18GB+512GB 银色', '{"颜色":"银色","处理器":"M3 Pro","内存":"18GB","硬盘":"512GB"}', 14999.00, 16999.00, 60, 'https://img.aheadshop.com/sku/mbp14-m3pro-silver.jpg', 1),
(402, 4, 'MacBook Pro 14 M3 Pro 36GB+1TB 深空灰', '{"颜色":"深空灰","处理器":"M3 Pro","内存":"36GB","硬盘":"1TB"}', 19999.00, 21999.00, 40, 'https://img.aheadshop.com/sku/mbp14-m3pro-gray.jpg', 1),
(501, 5, 'ThinkPad X1 Carbon i7 16GB+512GB 黑色', '{"颜色":"黑色","处理器":"i7","内存":"16GB","硬盘":"512GB"}', 9999.00, 11999.00, 80, 'https://img.aheadshop.com/sku/x1c-i7-black.jpg', 1),
(601, 6, 'Sony WH-1000XM5 黑色', '{"颜色":"黑色"}', 2399.00, 2999.00, 500, 'https://img.aheadshop.com/sku/xm5-black.jpg', 1),
(602, 6, 'Sony WH-1000XM5 银色', '{"颜色":"银色"}', 2399.00, 2999.00, 390, 'https://img.aheadshop.com/sku/xm5-silver.jpg', 1);

-- 背景图表（继承 BaseEntity，需 deleted/version）
-- DDL:
-- CREATE TABLE aheadshop_product.background (
--     id          BIGINT AUTO_INCREMENT PRIMARY KEY,
--     image_url   VARCHAR(500) NOT NULL COMMENT '图片URL',
--     sort_order  INT DEFAULT 0 COMMENT '排序',
--     enabled     TINYINT DEFAULT 1 COMMENT '0=禁用 1=启用',
--     create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
--     update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--     deleted     TINYINT DEFAULT 0,
--     version     INT DEFAULT 1
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='背景图';
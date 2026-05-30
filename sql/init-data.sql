-- ============================================================
-- AheadShop-Plus 初始数据
-- 执行前请确保已执行 init.sql 创建好 4 个库和所有表
-- ============================================================

-- ------------------------------------------------------------
-- 1. 用户库 aheadshop_user
-- ------------------------------------------------------------

-- 超级管理员（admin / admin123，BCrypt 加密）
INSERT INTO aheadshop_user.t_user (id, username, password, nickname, phone, email, gender, status)
VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '超级管理员', '13800000001', 'admin@aheadshop.com', 1, 1);

-- 测试用户
INSERT INTO aheadshop_user.t_user (id, username, password, nickname, phone, email, gender, status)
VALUES (2, 'testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '测试用户', '13800000002', 'test@aheadshop.com', 1, 1);

-- 默认角色
INSERT INTO aheadshop_user.t_role (id, role_name, role_key, status) VALUES (1, '超级管理员', 'ADMIN', 1);
INSERT INTO aheadshop_user.t_role (id, role_name, role_key, status) VALUES (2, '普通用户', 'USER', 1);

-- 用户角色关联：admin -> ADMIN，testuser -> USER
INSERT INTO aheadshop_user.t_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO aheadshop_user.t_user_role (user_id, role_id) VALUES (2, 2);

-- 菜单权限（管理后台基础菜单）
INSERT INTO aheadshop_user.t_menu (id, parent_id, menu_name, path, component, perms, menu_type, icon, sort_order) VALUES
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
INSERT INTO aheadshop_user.t_role_menu (role_id, menu_id)
SELECT 1, id FROM aheadshop_user.t_menu;

-- 测试用户收货地址
INSERT INTO aheadshop_user.t_user_address (user_id, receiver_name, receiver_phone, province, city, district, detail_address, is_default)
VALUES (2, '张三', '13800000002', '广东省', '深圳市', '南山区', '科技园南路100号A栋1001室', 1);

-- ------------------------------------------------------------
-- 2. 商品库 aheadshop_product
-- ------------------------------------------------------------

-- 商品分类（三级树）
-- 一级分类
INSERT INTO aheadshop_product.t_category (id, parent_id, name, level, sort_order) VALUES
(1,  0, '手机数码', 1, 1),
(2,  0, '电脑办公', 1, 2),
(3,  0, '家用电器', 1, 3),
(4,  0, '服饰鞋包', 1, 4);

-- 二级分类
INSERT INTO aheadshop_product.t_category (id, parent_id, name, level, sort_order) VALUES
(10, 1, '手机',     2, 1),
(11, 1, '平板电脑', 2, 2),
(12, 1, '耳机',     2, 3),
(20, 2, '笔记本',   2, 1),
(21, 2, '台式机',   2, 2),
(22, 2, '显示器',   2, 3),
(30, 3, '冰箱',     2, 1),
(31, 3, '洗衣机',   2, 2),
(32, 3, '空调',     2, 3),
(40, 4, '男装',     2, 1),
(41, 4, '女装',     2, 2),
(42, 4, '运动鞋',   2, 3);

-- 三级分类
INSERT INTO aheadshop_product.t_category (id, parent_id, name, level, sort_order) VALUES
(100, 10, '智能手机',   3, 1),
(101, 10, '游戏手机',   3, 2),
(110, 11, '安卓平板',   3, 1),
(111, 11, 'iPad',       3, 2),
(120, 12, '有线耳机',   3, 1),
(121, 12, '蓝牙耳机',   3, 2),
(200, 20, '轻薄本',     3, 1),
(201, 20, '游戏本',     3, 2),
(210, 21, '办公台式机', 3, 1),
(220, 22, '电竞显示器', 3, 1),
(300, 30, '双门冰箱',   3, 1),
(310, 31, '滚筒洗衣机', 3, 1),
(320, 32, '壁挂式空调', 3, 1);

-- 品牌
INSERT INTO aheadshop_product.t_brand (id, name, logo, description, sort_order) VALUES
(1,  '华为',    'https://img.aheadshop.com/brand/huawei.png',   '华为技术有限公司',    1),
(2,  '苹果',    'https://img.aheadshop.com/brand/apple.png',    'Apple Inc.',          2),
(3,  '小米',    'https://img.aheadshop.com/brand/xiaomi.png',   '小米科技有限公司',    3),
(4,  '联想',    'https://img.aheadshop.com/brand/lenovo.png',   '联想集团',            4),
(5,  '戴尔',    'https://img.aheadshop.com/brand/dell.png',     'Dell Technologies',   5),
(6,  '三星',    'https://img.aheadshop.com/brand/samsung.png',  'Samsung Electronics', 6),
(7,  '海尔',    'https://img.aheadshop.com/brand/haier.png',    '海尔集团',            7),
(8,  '格力',    'https://img.aheadshop.com/brand/gree.png',     '格力电器',            8),
(9,  '索尼',    'https://img.aheadshop.com/brand/sony.png',     'Sony Corporation',    9),
(10, 'Nike',    'https://img.aheadshop.com/brand/nike.png',     '耐克',               10);

-- 分类品牌关联
INSERT INTO aheadshop_product.t_category_brand (category_id, brand_id) VALUES
(100, 1), (100, 2), (100, 3), (100, 6),   -- 智能手机：华为/苹果/小米/三星
(101, 1), (101, 3),                         -- 游戏手机：华为/小米
(110, 1), (110, 3), (110, 6),              -- 安卓平板：华为/小米/三星
(111, 2),                                   -- iPad：苹果
(120, 9), (121, 9), (121, 2),              -- 耳机：索尼/苹果
(200, 4), (200, 5), (200, 2),              -- 轻薄本：联想/戴尔/苹果
(201, 4), (201, 5),                         -- 游戏本：联想/戴尔
(210, 4), (210, 5),                         -- 台式机：联想/戴尔
(220, 5), (220, 6),                         -- 电竞显示器：戴尔/三星
(300, 7), (310, 7), (320, 8);              -- 家电：海尔/格力

-- 商品属性（手机分类下的规格属性）
INSERT INTO aheadshop_product.t_attribute (category_id, name, type, input_type, value_list, sort_order) VALUES
(100, '颜色',   1, 1, '黑色,白色,蓝色,绿色,紫色', 1),
(100, '存储',   1, 1, '128GB,256GB,512GB,1TB',     2),
(100, '内存',   1, 1, '8GB,12GB,16GB',             3),
(200, '颜色',   1, 1, '银色,灰色,黑色',             1),
(200, '处理器', 1, 1, 'i5,i7,i9,M3,M3 Pro',       2),
(200, '内存',   1, 1, '8GB,16GB,32GB',             3),
(200, '硬盘',   1, 1, '256GB,512GB,1TB',           4);

-- SPU 测试数据
INSERT INTO aheadshop_product.t_spu (id, name, subtitle, category_id, brand_id, main_image, images, status, sales) VALUES
(1, 'Huawei Mate 70 Pro',   '旗舰芯片 · 超感知影像 · 鸿蒙系统',         100, 1,
    'https://img.aheadshop.com/spu/mate70pro-main.jpg',
    '["https://img.aheadshop.com/spu/mate70pro-1.jpg","https://img.aheadshop.com/spu/mate70pro-2.jpg"]',
    1, 580),
(2, 'iPhone 16 Pro Max',    'A18 Pro 芯片 · 钛金属设计 · 4K 120fps',    100, 2,
    'https://img.aheadshop.com/spu/iphone16promax-main.jpg',
    '["https://img.aheadshop.com/spu/iphone16promax-1.jpg","https://img.aheadshop.com/spu/iphone16promax-2.jpg"]',
    1, 1200),
(3, 'Xiaomi 15 Ultra',      '徕卡光学 · 骁龙 8 至尊版 · 5400mAh',      100, 3,
    'https://img.aheadshop.com/spu/mi15ultra-main.jpg',
    '["https://img.aheadshop.com/spu/mi15ultra-1.jpg","https://img.aheadshop.com/spu/mi15ultra-2.jpg"]',
    1, 320),
(4, 'MacBook Pro 14 M3 Pro', 'M3 Pro 芯片 · Liquid Retina XDR · 18h续航', 200, 2,
    'https://img.aheadshop.com/spu/macbookpro14-main.jpg',
    '["https://img.aheadshop.com/spu/macbookpro14-1.jpg"]',
    1, 210),
(5, 'ThinkPad X1 Carbon',    '14英寸 · 超轻薄 · 商务旗舰',              200, 4,
    'https://img.aheadshop.com/spu/x1carbon-main.jpg',
    '["https://img.aheadshop.com/spu/x1carbon-1.jpg"]',
    1, 150),
(6, 'Sony WH-1000XM5',      '行业领先降噪 · 30h续航 · LDAC',           121, 9,
    'https://img.aheadshop.com/spu/wh1000xm5-main.jpg',
    '["https://img.aheadshop.com/spu/wh1000xm5-1.jpg"]',
    1, 890);

-- SKU 测试数据
INSERT INTO aheadshop_product.t_sku (id, spu_id, sku_name, specs, price, original_price, stock, image) VALUES
-- Mate 70 Pro 规格组合
(101, 1, 'Huawei Mate 70 Pro 128GB 曜石黑',  '{"颜色":"曜石黑","存储":"128GB","内存":"12GB"}', 5999.00, 6499.00, 200, 'https://img.aheadshop.com/sku/mate70pro-128-black.jpg'),
(102, 1, 'Huawei Mate 70 Pro 256GB 曜石黑',  '{"颜色":"曜石黑","存储":"256GB","内存":"12GB"}', 6499.00, 6999.00, 150, 'https://img.aheadshop.com/sku/mate70pro-256-black.jpg'),
(103, 1, 'Huawei Mate 70 Pro 512GB 星河银',  '{"颜色":"星河银","存储":"512GB","内存":"12GB"}', 7499.00, 7999.00, 100, 'https://img.aheadshop.com/sku/mate70pro-512-silver.jpg'),
-- iPhone 16 Pro Max 规格组合
(201, 2, 'iPhone 16 Pro Max 256GB 沙漠钛金属', '{"颜色":"沙漠钛金属","存储":"256GB","内存":"8GB"}', 9999.00, 9999.00, 300, 'https://img.aheadshop.com/sku/iphone16pm-256-titan.jpg'),
(202, 2, 'iPhone 16 Pro Max 512GB 原色钛金属', '{"颜色":"原色钛金属","存储":"512GB","内存":"8GB"}', 11999.00, 11999.00, 200, 'https://img.aheadshop.com/sku/iphone16pm-512-titan.jpg'),
(203, 2, 'iPhone 16 Pro Max 1TB 黑色钛金属',   '{"颜色":"黑色钛金属","存储":"1TB","内存":"8GB"}',   13999.00, 13999.00, 80,  'https://img.aheadshop.com/sku/iphone16pm-1tb-black.jpg'),
-- Xiaomi 15 Ultra
(301, 3, 'Xiaomi 15 Ultra 256GB 黑色', '{"颜色":"黑色","存储":"256GB","内存":"16GB"}', 5299.00, 5999.00, 180, 'https://img.aheadshop.com/sku/mi15u-256-black.jpg'),
(302, 3, 'Xiaomi 15 Ultra 512GB 白色', '{"颜色":"白色","存储":"512GB","内存":"16GB"}', 5999.00, 6499.00, 120, 'https://img.aheadshop.com/sku/mi15u-512-white.jpg'),
-- MacBook Pro 14
(401, 4, 'MacBook Pro 14 M3 Pro 18GB+512GB 银色', '{"颜色":"银色","处理器":"M3 Pro","内存":"18GB","硬盘":"512GB"}', 14999.00, 16999.00, 60, 'https://img.aheadshop.com/sku/mbp14-m3pro-silver.jpg'),
(402, 4, 'MacBook Pro 14 M3 Pro 36GB+1TB 深空灰', '{"颜色":"深空灰","处理器":"M3 Pro","内存":"36GB","硬盘":"1TB"}', 19999.00, 21999.00, 40, 'https://img.aheadshop.com/sku/mbp14-m3pro-gray.jpg'),
-- ThinkPad X1 Carbon
(501, 5, 'ThinkPad X1 Carbon i7 16GB+512GB 黑色', '{"颜色":"黑色","处理器":"i7","内存":"16GB","硬盘":"512GB"}', 9999.00, 11999.00, 80, 'https://img.aheadshop.com/sku/x1c-i7-black.jpg'),
-- Sony WH-1000XM5
(601, 6, 'Sony WH-1000XM5 黑色', '{"颜色":"黑色"}', 2399.00, 2999.00, 500, 'https://img.aheadshop.com/sku/xm5-black.jpg'),
(602, 6, 'Sony WH-1000XM5 银色', '{"颜色":"银色"}', 2399.00, 2999.00, 390, 'https://img.aheadshop.com/sku/xm5-silver.jpg');
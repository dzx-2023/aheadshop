-- ============================================================
-- 修复 SKU/SPU/Brand/OrderItem 图片 URL
-- 将描述性文件名映射到 upload/product/ 目录下的实际文件
-- 在 MySQL 中执行: mysql -u root -p < sql/fix-sku-images.sql
-- ============================================================

USE aheadshop_product;

-- ==================== SKU 表 ====================
-- Mate 70 Pro 系列
UPDATE sku SET image = '/upload/product/08e7645d42204829b2119ae50889f2a8.png' WHERE id = 101;
UPDATE sku SET image = '/upload/product/121c30d75b184b809698e35d7d7d6043.png' WHERE id = 102;
UPDATE sku SET image = '/upload/product/16b28a3333454d48b9dd4f37ef4fefa1.jpg' WHERE id = 103;

-- iPhone 16 Pro Max 系列
UPDATE sku SET image = '/upload/product/1b05d1c6d11f490c84be04e2a5c9e876.jpg' WHERE id = 201;
UPDATE sku SET image = '/upload/product/21e977b358164c9e80e2f4832d54d46b.png' WHERE id = 202;
UPDATE sku SET image = '/upload/product/221119d7b313406084b545c2131a55d2.png' WHERE id = 203;

-- Xiaomi 15 Ultra 系列
UPDATE sku SET image = '/upload/product/311c0c946d17475e967c0d8f133b22f2.jpg' WHERE id = 301;
UPDATE sku SET image = '/upload/product/341687b198d846a0a8e0a30656721840.png' WHERE id = 302;

-- MacBook Pro 14 系列
UPDATE sku SET image = '/upload/product/36194fe655ad467889694060339c2c0d.png' WHERE id = 401;
UPDATE sku SET image = '/upload/product/3aef436e4f064d00ad662a8520c70a0d.png' WHERE id = 402;

-- ThinkPad X1 Carbon
UPDATE sku SET image = '/upload/product/4190f1bef28c4c0ab4ba7ed5a5a93c90.png' WHERE id = 501;

-- Sony WH-1000XM5 系列
UPDATE sku SET image = '/upload/product/52c0935c1b3441eaa9ea78cfe41acc73.png' WHERE id = 601;
UPDATE sku SET image = '/upload/product/62cbbb54d21c42f6b6b81e6a789e1b76.png' WHERE id = 602;

-- 历史订单中的 SKU 副本 (id 607-609, 614-623)
UPDATE sku SET image = '/upload/product/1b05d1c6d11f490c84be04e2a5c9e876.jpg' WHERE id IN (607, 614);
UPDATE sku SET image = '/upload/product/21e977b358164c9e80e2f4832d54d46b.png' WHERE id IN (608, 615);
UPDATE sku SET image = '/upload/product/221119d7b313406084b545c2131a55d2.png' WHERE id IN (609, 616);
UPDATE sku SET image = '/upload/product/311c0c946d17475e967c0d8f133b22f2.jpg' WHERE id IN (301, 617);
UPDATE sku SET image = '/upload/product/341687b198d846a0a8e0a30656721840.png' WHERE id IN (302, 618);
UPDATE sku SET image = '/upload/product/36194fe655ad467889694060339c2c0d.png' WHERE id IN (401, 619);
UPDATE sku SET image = '/upload/product/3aef436e4f064d00ad662a8520c70a0d.png' WHERE id IN (402, 620);
UPDATE sku SET image = '/upload/product/4190f1bef28c4c0ab4ba7ed5a5a93c90.png' WHERE id IN (501, 621);
UPDATE sku SET image = '/upload/product/52c0935c1b3441eaa9ea78cfe41acc73.png' WHERE id IN (601, 622);
UPDATE sku SET image = '/upload/product/62cbbb54d21c42f6b6b81e6a789e1b76.png' WHERE id IN (602, 623);

-- ==================== SPU 表 ====================
UPDATE spu SET main_image = '/upload/product/08e7645d42204829b2119ae50889f2a8.png' WHERE id = 1;
UPDATE spu SET main_image = '/upload/product/1b05d1c6d11f490c84be04e2a5c9e876.jpg' WHERE id = 2;
UPDATE spu SET main_image = '/upload/product/311c0c946d17475e967c0d8f133b22f2.jpg' WHERE id = 3;
UPDATE spu SET main_image = '/upload/product/36194fe655ad467889694060339c2c0d.png' WHERE id = 4;
UPDATE spu SET main_image = '/upload/product/4190f1bef28c4c0ab4ba7ed5a5a93c90.png' WHERE id = 5;
UPDATE spu SET main_image = '/upload/product/52c0935c1b3441eaa9ea78cfe41acc73.png' WHERE id = 6;

-- ==================== Brand 表 ====================
-- 如果品牌 logo 也是外部 URL，需要改为本地路径
-- 当前 brand.logo 在 SQL 中已是 /upload/brand/ 前缀，无需修改

-- ==================== OrderItem 表 ====================
-- order_item.sku_image 是下单时的快照，也需要修复
UPDATE order_item SET sku_image = '/upload/product/1b05d1c6d11f490c84be04e2a5c9e876.jpg'
WHERE sku_image LIKE '%iphone16pm-256%' OR sku_image LIKE '%titan%';
UPDATE order_item SET sku_image = '/upload/product/311c0c946d17475e967c0d8f133b22f2.jpg'
WHERE sku_image LIKE '%mi15u-256%' OR sku_image LIKE '%mi15u%';
UPDATE order_item SET sku_image = '/upload/product/36194fe655ad467889694060339c2c0d.png'
WHERE sku_image LIKE '%mbp14-m3pro-silver%' OR sku_image LIKE '%mbp14%';
UPDATE order_item SET sku_image = '/upload/product/4190f1bef28c4c0ab4ba7ed5a5a93c90.png'
WHERE sku_image LIKE '%x1c-i7%' OR sku_image LIKE '%x1c%';

-- 验证
SELECT id, image FROM sku WHERE image IS NOT NULL AND image NOT LIKE '/upload/%';
SELECT id, sku_image FROM order_item WHERE sku_image IS NOT NULL AND sku_image NOT LIKE '/upload/%';
SELECT id, logo FROM brand WHERE logo IS NOT NULL AND logo NOT LIKE '/upload/%';

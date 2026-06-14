-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: aheadshop_user
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `aheadshop_user`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `aheadshop_user` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `aheadshop_user`;

--
-- Table structure for table `menu`
--

DROP TABLE IF EXISTS `menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint DEFAULT '0',
  `menu_name` varchar(50) NOT NULL,
  `path` varchar(200) DEFAULT NULL,
  `component` varchar(200) DEFAULT NULL,
  `perms` varchar(100) DEFAULT NULL,
  `menu_type` char(1) DEFAULT NULL COMMENT 'M目录 C菜单 F按钮',
  `icon` varchar(100) DEFAULT NULL,
  `sort_order` int DEFAULT '0',
  `status` tinyint DEFAULT '1',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu`
--

LOCK TABLES `menu` WRITE;
/*!40000 ALTER TABLE `menu` DISABLE KEYS */;
INSERT INTO `menu` VALUES (1,0,'系统管理','/system',NULL,NULL,'M','setting',1,1,'2026-05-29 21:28:49','2026-05-29 21:28:49'),(2,1,'用户管理','/system/user','system/user/index','system:user:list','C','user',1,1,'2026-05-29 21:28:49','2026-05-29 21:28:49'),(3,1,'角色管理','/system/role','system/role/index','system:role:list','C','peoples',2,1,'2026-05-29 21:28:49','2026-05-29 21:28:49'),(4,1,'菜单管理','/system/menu','system/menu/index','system:menu:list','C','tree-table',3,1,'2026-05-29 21:28:49','2026-05-29 21:28:49'),(5,0,'商品管理','/product',NULL,NULL,'M','shopping',2,1,'2026-05-29 21:28:49','2026-05-29 21:28:49'),(6,5,'分类管理','/product/category','product/category/index','product:category:list','C','tree',1,1,'2026-05-29 21:28:49','2026-05-29 21:28:49'),(7,5,'品牌管理','/product/brand','product/brand/index','product:brand:list','C','brand',2,1,'2026-05-29 21:28:49','2026-05-29 21:28:49'),(8,5,'SPU管理','/product/spu','product/spu/index','product:spu:list','C','goods',3,1,'2026-05-29 21:28:49','2026-05-29 21:28:49'),(9,5,'SKU管理','/product/sku','product/sku/index','product:sku:list','C','list',4,1,'2026-05-29 21:28:49','2026-05-29 21:28:49'),(10,0,'订单管理','/order',NULL,NULL,'M','form',3,1,'2026-05-29 21:28:49','2026-05-29 21:28:49'),(11,10,'订单列表','/order/list','order/list/index','order:list:list','C','list',1,1,'2026-05-29 21:28:49','2026-05-29 21:28:49'),(12,10,'退款管理','/order/refund','order/refund/index','order:refund:list','C','money',2,1,'2026-05-29 21:28:49','2026-05-29 21:28:49');
/*!40000 ALTER TABLE `menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) NOT NULL,
  `role_key` varchar(50) NOT NULL,
  `status` tinyint DEFAULT '1',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` int DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'超级管理员','ADMIN',1,'2026-05-29 21:28:49','2026-05-29 21:28:49',1),(2,'普通用户','USER',1,'2026-05-29 21:28:49','2026-05-29 21:28:49',1);
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_menu`
--

DROP TABLE IF EXISTS `role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `menu_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`,`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_menu`
--

LOCK TABLES `role_menu` WRITE;
/*!40000 ALTER TABLE `role_menu` DISABLE KEYS */;
INSERT INTO `role_menu` VALUES (1,1,1),(2,1,2),(3,1,3),(4,1,4),(5,1,5),(6,1,6),(7,1,7),(8,1,8),(9,1,9),(10,1,10),(11,1,11),(12,1,12);
/*!40000 ALTER TABLE `role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `nickname` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `gender` tinyint DEFAULT '0',
  `status` tinyint DEFAULT '1',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT '0',
  `version` int DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','$2a$10$8t3jtaGATm.yqzp1S5l3VeCMwrNHbsABgyY1j/1OTusa22YKD7iei','超级管理员','13800000001','admin@aheadshop.com','http://localhost:8081/avatar/11d950c71bee4975800ba5ffc24ad947.jpg',1,1,'2026-05-29 21:28:49','2026-05-30 16:28:30',0,2),(2,'testuser','$2a$10$8t3jtaGATm.yqzp1S5l3VeCMwrNHbsABgyY1j/1OTusa22YKD7iei','zhangsan123','13800000002','test@aheadshop.com','',0,1,'2026-05-29 21:28:49','2026-05-30 16:28:30',0,2),(3,'zhangsan','$2a$10$3VXfSFyfirSnmlMwACFjp.hZSk61tY1x66EgSKslOg2psJ96pe99C','zhangsna','18890901010','123@zhangsan','http://localhost:8081/avatar/3cf72153f4e546d8846a27b351e3c2b4.jpg',1,1,'2026-05-30 16:52:37','2026-05-30 16:52:37',0,2),(5,'lisi','$2a$10$7Z3VZoK7be2Vmte5236PNOA2kmtLrbwRfmBzRsnyX1PB0eNbEK3Gi','lisi',NULL,NULL,NULL,0,1,'2026-05-30 18:24:51','2026-05-30 18:24:51',0,1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_address`
--

DROP TABLE IF EXISTS `user_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_address` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `receiver_name` varchar(50) NOT NULL,
  `receiver_phone` varchar(20) NOT NULL,
  `province` varchar(50) NOT NULL,
  `city` varchar(50) NOT NULL,
  `district` varchar(50) NOT NULL,
  `detail_address` varchar(200) NOT NULL,
  `is_default` tinyint DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT '0',
  `version` int DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='收货地址表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_address`
--

LOCK TABLES `user_address` WRITE;
/*!40000 ALTER TABLE `user_address` DISABLE KEYS */;
INSERT INTO `user_address` VALUES (1,2,'张三','13800000002','广东省','深圳市','南山区','科技园南路100号A栋1001室',1,'2026-05-29 21:28:49','2026-05-29 21:28:49',0,1),(2,3,'张三','13812345678','广东省','深圳市','南山区','科技园南区虚拟大学园B栋203室',1,'2026-05-30 22:44:41','2026-05-30 22:44:41',0,1),(3,2,'牛爱花','18701234567','江苏省','南京市','鼓楼区','中山路321号紫峰大厦2007室',0,'2026-05-30 22:45:22','2026-05-30 22:45:22',0,2),(4,2,'王芳','18701234567','江苏省','南京市','鼓楼区','中山路321号紫峰大厦2007室',0,'2026-05-30 22:45:26','2026-05-30 22:45:26',0,1),(5,1,'admin','13078907890','北京','北京','海定','东楼001',1,'2026-06-02 14:34:11','2026-06-02 14:34:11',0,1);
/*!40000 ALTER TABLE `user_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `version` int DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1,1,1,1),(2,2,2,1),(3,3,2,1),(4,5,2,1);
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `aheadshop_product`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `aheadshop_product` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `aheadshop_product`;

--
-- Table structure for table `attribute`
--

DROP TABLE IF EXISTS `attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attribute` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_id` bigint NOT NULL COMMENT '所属分类',
  `name` varchar(50) NOT NULL COMMENT '属性名',
  `type` tinyint DEFAULT '1' COMMENT '1=规格 0=参数',
  `input_type` tinyint DEFAULT '0' COMMENT '0=手动 1=列表选择',
  `value_list` varchar(500) DEFAULT NULL COMMENT '可选值列表，逗号分隔',
  `sort_order` int DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品属性表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attribute`
--

LOCK TABLES `attribute` WRITE;
/*!40000 ALTER TABLE `attribute` DISABLE KEYS */;
INSERT INTO `attribute` VALUES (1,100,'颜色',1,1,'黑色,白色,蓝色,绿色,紫色',1,'2026-05-29 21:30:21','2026-05-29 21:30:21'),(2,100,'存储',1,1,'128GB,256GB,512GB,1TB',2,'2026-05-29 21:30:21','2026-05-29 21:30:21'),(3,100,'内存',1,1,'8GB,12GB,16GB',3,'2026-05-29 21:30:21','2026-05-29 21:30:21'),(4,200,'颜色',1,1,'银色,灰色,黑色',1,'2026-05-29 21:30:21','2026-05-29 21:30:21'),(5,200,'处理器',1,1,'i5,i7,i9,M3,M3 Pro',2,'2026-05-29 21:30:21','2026-05-29 21:30:21'),(6,200,'内存',1,1,'8GB,16GB,32GB',3,'2026-05-29 21:30:21','2026-05-29 21:30:21'),(7,200,'硬盘',1,1,'256GB,512GB,1TB',4,'2026-05-29 21:30:21','2026-05-29 21:30:21');
/*!40000 ALTER TABLE `attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `background`
--

DROP TABLE IF EXISTS `background`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `background` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `image_url` varchar(500) NOT NULL COMMENT '图片URL',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `enabled` tinyint DEFAULT '1' COMMENT '0=禁用 1=启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT '0',
  `version` int DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='背景图';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `background`
--

LOCK TABLES `background` WRITE;
/*!40000 ALTER TABLE `background` DISABLE KEYS */;
INSERT INTO `background` VALUES (1,'/upload/background/bb4bde41814141e99c596435990c40e2.png',0,1,'2026-06-02 19:03:26','2026-06-02 19:03:26',0,5),(2,'/upload/background/1433dcb067824638978cdf79f9b7ef2f.png',0,1,'2026-06-02 19:04:07','2026-06-02 19:04:07',0,5),(3,'/upload/background/2b1d79182c684bd7bafacbbd14315423.png',0,1,'2026-06-02 19:04:09','2026-06-02 19:04:09',0,3),(4,'/upload/background/2affa0764a2f4a1bacaad18a9b53f5b9.png',0,1,'2026-06-02 19:04:11','2026-06-02 19:04:11',0,3),(5,'/upload/background/6f92ba0386f74620a4a39aaf8f77d9e1.png',0,1,'2026-06-02 19:04:13','2026-06-02 19:04:13',0,1),(6,'/upload/background/65aee517e4db42ec8be5bf5bb6f65222.png',0,1,'2026-06-02 19:04:15','2026-06-02 19:04:15',0,3),(7,'/upload/background/b9f1d0b1be0449ceac9fb3a544e57b79.png',0,1,'2026-06-02 19:04:20','2026-06-02 19:04:20',0,3);
/*!40000 ALTER TABLE `background` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `brand`
--

DROP TABLE IF EXISTS `brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `brand` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '品牌名称',
  `logo` varchar(255) DEFAULT NULL COMMENT '品牌 Logo',
  `description` varchar(500) DEFAULT NULL COMMENT '品牌描述',
  `sort_order` int DEFAULT '0',
  `status` tinyint DEFAULT '1',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT '0',
  `version` int DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='品牌表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `brand`
--

LOCK TABLES `brand` WRITE;
/*!40000 ALTER TABLE `brand` DISABLE KEYS */;
INSERT INTO `brand` VALUES (1,'华为','https://img.aheadshop.com/brand/huawei.png','华为技术有限公司',1,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(2,'苹果','https://img.aheadshop.com/brand/apple.png','Apple Inc.',2,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(3,'小米','https://img.aheadshop.com/brand/xiaomi.png','小米科技有限公司',3,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(4,'联想','https://img.aheadshop.com/brand/lenovo.png','联想集团',4,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(5,'戴尔','https://img.aheadshop.com/brand/dell.png','Dell Technologies',5,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(6,'三星','https://img.aheadshop.com/brand/samsung.png','Samsung Electronics',6,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(7,'海尔','https://img.aheadshop.com/brand/haier.png','海尔集团',7,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(8,'格力','https://img.aheadshop.com/brand/gree.png','格力电器',8,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(9,'索尼','https://img.aheadshop.com/brand/sony.png','Sony Corporation',9,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(10,'Nike','https://img.aheadshop.com/brand/nike.png','耐克',10,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1);
/*!40000 ALTER TABLE `brand` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint DEFAULT '0',
  `name` varchar(50) NOT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `level` tinyint DEFAULT '1',
  `sort_order` int DEFAULT '0',
  `status` tinyint DEFAULT '1',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT '0',
  `version` int DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=322 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,0,'手机数码','📱',1,1,1,'2026-05-29 21:30:21','2026-06-01 13:28:46',0,1),(2,0,'电脑办公','🖥️',1,2,1,'2026-05-29 21:30:21','2026-06-01 13:28:46',0,1),(3,0,'家用电器','☎️',1,3,1,'2026-05-29 21:30:21','2026-06-02 18:07:07',0,1),(4,0,'服饰鞋包','💼',1,4,1,'2026-05-29 21:30:21','2026-06-01 13:28:46',0,1),(10,1,'手机','📱',2,1,1,'2026-05-29 21:30:21','2026-06-02 18:07:07',0,1),(11,1,'平板电脑','💻',2,2,1,'2026-05-29 21:30:21','2026-06-02 18:07:07',0,1),(12,1,'耳机','🎧',2,3,1,'2026-05-29 21:30:21','2026-06-02 18:07:07',0,1),(20,2,'笔记本','💻',2,1,1,'2026-05-29 21:30:21','2026-06-02 18:07:07',0,1),(21,2,'台式机','🖥️',2,2,1,'2026-05-29 21:30:21','2026-06-02 18:07:07',0,1),(22,2,'显示器','🖥️',2,3,1,'2026-05-29 21:30:21','2026-06-02 18:07:07',0,1),(30,3,'冰箱',NULL,2,1,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(31,3,'洗衣机',NULL,2,2,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(32,3,'空调',NULL,2,3,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(40,4,'男装',NULL,2,1,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(41,4,'女装',NULL,2,2,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(42,4,'运动鞋',NULL,2,3,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(100,10,'智能手机',NULL,3,1,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(101,10,'游戏手机',NULL,3,2,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(110,11,'安卓平板',NULL,3,1,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(111,11,'iPad',NULL,3,2,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(120,12,'有线耳机',NULL,3,1,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(121,12,'蓝牙耳机',NULL,3,2,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(200,20,'轻薄本',NULL,3,1,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(201,20,'游戏本',NULL,3,2,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(210,21,'办公台式机',NULL,3,1,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(220,22,'电竞显示器',NULL,3,1,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(300,30,'双门冰箱',NULL,3,1,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(310,31,'滚筒洗衣机',NULL,3,1,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(320,32,'壁挂式空调',NULL,3,1,1,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,1),(321,4,'包','💼',2,1,1,'2026-06-02 19:45:04','2026-06-02 19:45:04',0,1);
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category_brand`
--

DROP TABLE IF EXISTS `category_brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category_brand` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_id` bigint NOT NULL,
  `brand_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cat_brand` (`category_id`,`brand_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='分类品牌关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category_brand`
--

LOCK TABLES `category_brand` WRITE;
/*!40000 ALTER TABLE `category_brand` DISABLE KEYS */;
INSERT INTO `category_brand` VALUES (1,100,1),(2,100,2),(3,100,3),(4,100,6),(5,101,1),(6,101,3),(7,110,1),(8,110,3),(9,110,6),(10,111,2),(11,120,9),(13,121,2),(12,121,9),(16,200,2),(14,200,4),(15,200,5),(17,201,4),(18,201,5),(19,210,4),(20,210,5),(21,220,5),(22,220,6),(23,300,7),(24,310,7),(25,320,8);
/*!40000 ALTER TABLE `category_brand` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `spu_id` bigint NOT NULL,
  `sku_id` bigint NOT NULL,
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `score` tinyint NOT NULL COMMENT '评分 1-5',
  `content` varchar(1000) DEFAULT NULL COMMENT '评论内容',
  `images` text COMMENT '评论图片 JSON',
  `is_anonymous` tinyint DEFAULT '0' COMMENT '是否匿名',
  `reply` varchar(500) DEFAULT NULL COMMENT '商家回复',
  `status` tinyint DEFAULT '1' COMMENT '0=隐藏 1=显示',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_spu` (`spu_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_order` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品评价表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sku`
--

DROP TABLE IF EXISTS `sku`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sku` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `spu_id` bigint NOT NULL,
  `sku_name` varchar(200) NOT NULL,
  `specs` text COMMENT '规格JSON，如 {"颜色":"黑色","存储":"128G"}',
  `price` decimal(10,2) NOT NULL,
  `original_price` decimal(10,2) DEFAULT NULL,
  `stock` int DEFAULT '0',
  `locked_stock` int DEFAULT '0' COMMENT '锁定库存',
  `image` varchar(500) DEFAULT NULL,
  `status` tinyint DEFAULT '1',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT '0',
  `version` int DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `idx_spu_id` (`spu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=629 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='SKU 表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sku`
--

LOCK TABLES `sku` WRITE;
/*!40000 ALTER TABLE `sku` DISABLE KEYS */;
INSERT INTO `sku` VALUES (101,1,'Huawei Mate 70 Pro 128GB 曜石黑','{\"颜色\":\"曜石黑\",\"存储\":\"128GB\",\"内存\":\"12GB\"}',5999.00,6499.00,200,0,'https://img.aheadshop.com/sku/mate70pro-128-black.jpg',1,'2026-05-29 21:30:21','2026-06-01 13:23:41',1,1),(102,1,'Huawei Mate 70 Pro 256GB 曜石黑','{\"颜色\":\"曜石黑\",\"存储\":\"256GB\",\"内存\":\"12GB\"}',6499.00,6999.00,150,0,'https://img.aheadshop.com/sku/mate70pro-256-black.jpg',1,'2026-05-29 21:30:21','2026-06-01 13:23:41',1,1),(103,1,'Huawei Mate 70 Pro 512GB 星河银','{\"颜色\":\"星河银\",\"存储\":\"512GB\",\"内存\":\"12GB\"}',7499.00,7999.00,100,0,'https://img.aheadshop.com/sku/mate70pro-512-silver.jpg',1,'2026-05-29 21:30:21','2026-06-01 13:23:41',1,1),(201,2,'iPhone 16 Pro Max 256GB 沙漠钛金属','{\"颜色\":\"沙漠钛金属\",\"存储\":\"256GB\",\"内存\":\"8GB\"}',9999.00,9999.00,300,0,'https://img.aheadshop.com/sku/iphone16pm-256-titan.jpg',1,'2026-05-29 21:30:21','2026-06-01 13:50:31',1,1),(202,2,'iPhone 16 Pro Max 512GB 原色钛金属','{\"颜色\":\"原色钛金属\",\"存储\":\"512GB\",\"内存\":\"8GB\"}',11999.00,11999.00,200,0,'https://img.aheadshop.com/sku/iphone16pm-512-titan.jpg',1,'2026-05-29 21:30:21','2026-06-01 13:50:31',1,1),(203,2,'iPhone 16 Pro Max 1TB 黑色钛金属','{\"颜色\":\"黑色钛金属\",\"存储\":\"1TB\",\"内存\":\"8GB\"}',13999.00,13999.00,80,0,'https://img.aheadshop.com/sku/iphone16pm-1tb-black.jpg',1,'2026-05-29 21:30:21','2026-06-01 13:50:31',1,1),(301,3,'Xiaomi 15 Ultra 256GB 黑色','{\"颜色\":\"黑色\",\"存储\":\"256GB\",\"内存\":\"16GB\"}',5299.00,5999.00,179,0,'https://img.aheadshop.com/sku/mi15u-256-black.jpg',1,'2026-05-29 21:30:21','2026-06-02 17:56:31',1,1),(302,3,'Xiaomi 15 Ultra 512GB 白色','{\"颜色\":\"白色\",\"存储\":\"512GB\",\"内存\":\"16GB\"}',5999.00,6499.00,120,0,'https://img.aheadshop.com/sku/mi15u-512-white.jpg',1,'2026-05-29 21:30:21','2026-06-02 17:56:31',1,1),(401,4,'MacBook Pro 14 M3 Pro 18GB+512GB 银色','{\"颜色\":\"银色\",\"处理器\":\"M3 Pro\",\"内存\":\"18GB\",\"硬盘\":\"512GB\"}',14999.00,16999.00,59,0,'https://img.aheadshop.com/sku/mbp14-m3pro-silver.jpg',1,'2026-05-29 21:30:21','2026-06-02 17:56:54',1,1),(402,4,'MacBook Pro 14 M3 Pro 36GB+1TB 深空灰','{\"颜色\":\"深空灰\",\"处理器\":\"M3 Pro\",\"内存\":\"36GB\",\"硬盘\":\"1TB\"}',19999.00,21999.00,40,0,'https://img.aheadshop.com/sku/mbp14-m3pro-gray.jpg',1,'2026-05-29 21:30:21','2026-06-02 17:56:54',1,1),(501,5,'ThinkPad X1 Carbon i7 16GB+512GB 黑色','{\"颜色\":\"黑色\",\"处理器\":\"i7\",\"内存\":\"16GB\",\"硬盘\":\"512GB\"}',9999.00,11999.00,80,0,'https://img.aheadshop.com/sku/x1c-i7-black.jpg',1,'2026-05-29 21:30:21','2026-06-02 17:57:17',1,1),(601,6,'Sony WH-1000XM5 黑色','{\"颜色\":\"黑色\"}',2399.00,2999.00,500,0,'https://img.aheadshop.com/sku/xm5-black.jpg',1,'2026-05-29 21:30:21','2026-06-02 17:57:30',1,1),(602,6,'Sony WH-1000XM5 银色','{\"颜色\":\"银色\"}',2399.00,2999.00,390,0,'https://img.aheadshop.com/sku/xm5-silver.jpg',1,'2026-05-29 21:30:21','2026-06-02 17:57:30',1,1),(603,1,'128',NULL,1.00,1.00,1,0,NULL,1,'2026-06-01 13:23:42','2026-06-01 13:38:23',1,1),(604,1,'128g',NULL,1.00,NULL,0,0,NULL,1,'2026-06-01 13:38:24','2026-06-01 13:57:06',1,1),(605,7,'鞋子',NULL,1.00,NULL,0,0,NULL,1,'2026-06-01 13:46:53','2026-06-01 13:47:49',1,1),(606,7,'鞋子',NULL,1.00,NULL,0,0,NULL,1,'2026-06-01 13:47:50','2026-06-01 13:47:50',0,1),(607,2,'iPhone 16 Pro Max 256GB 沙漠钛金属','{\"颜色\":\"沙漠钛金属\",\"存储\":\"256GB\",\"内存\":\"8GB\"}',9999.00,9999.00,296,3,'https://img.aheadshop.com/sku/iphone16pm-256-titan.jpg',1,'2026-06-01 13:50:32','2026-06-02 17:56:00',1,1),(608,2,'iPhone 16 Pro Max 512GB 原色钛金属','{\"颜色\":\"原色钛金属\",\"存储\":\"512GB\",\"内存\":\"8GB\"}',11999.00,11999.00,200,0,'https://img.aheadshop.com/sku/iphone16pm-512-titan.jpg',1,'2026-06-01 13:50:32','2026-06-02 17:56:00',1,1),(609,2,'iPhone 16 Pro Max 1TB 黑色钛金属','{\"颜色\":\"黑色钛金属\",\"存储\":\"1TB\",\"内存\":\"8GB\"}',13999.00,13999.00,80,0,'https://img.aheadshop.com/sku/iphone16pm-1tb-black.jpg',1,'2026-06-01 13:50:32','2026-06-02 17:56:00',1,1),(610,1,'128g',NULL,1.00,NULL,0,0,NULL,1,'2026-06-01 13:57:07','2026-06-02 17:55:28',1,1),(611,8,'买就对了','牛',1.00,1.00,999,1,NULL,1,'2026-06-02 14:44:25','2026-06-02 15:37:17',0,1),(612,9,'1分',NULL,0.01,0.01,9997,0,NULL,1,'2026-06-02 15:29:07','2026-06-02 17:57:57',0,1),(613,1,'128g',NULL,1.00,NULL,0,0,NULL,1,'2026-06-02 17:55:28','2026-06-02 17:55:28',0,1),(614,2,'iPhone 16 Pro Max 256GB 沙漠钛金属','{\"颜色\":\"沙漠钛金属\",\"存储\":\"256GB\",\"内存\":\"8GB\"}',9999.00,9999.00,294,2,'https://img.aheadshop.com/sku/iphone16pm-256-titan.jpg',1,'2026-06-02 17:56:00','2026-06-02 20:12:55',0,1),(615,2,'iPhone 16 Pro Max 512GB 原色钛金属','{\"颜色\":\"原色钛金属\",\"存储\":\"512GB\",\"内存\":\"8GB\"}',11999.00,11999.00,200,0,'https://img.aheadshop.com/sku/iphone16pm-512-titan.jpg',1,'2026-06-02 17:56:00','2026-06-02 17:56:00',0,1),(616,2,'iPhone 16 Pro Max 1TB 黑色钛金属','{\"颜色\":\"黑色钛金属\",\"存储\":\"1TB\",\"内存\":\"8GB\"}',13999.00,13999.00,80,0,'https://img.aheadshop.com/sku/iphone16pm-1tb-black.jpg',1,'2026-06-02 17:56:00','2026-06-02 17:56:00',0,1),(617,3,'Xiaomi 15 Ultra 256GB 黑色','{\"颜色\":\"黑色\",\"存储\":\"256GB\",\"内存\":\"16GB\"}',5299.00,5999.00,179,0,'https://img.aheadshop.com/sku/mi15u-256-black.jpg',1,'2026-06-02 17:56:32','2026-06-02 17:56:32',0,1),(618,3,'Xiaomi 15 Ultra 512GB 白色','{\"颜色\":\"白色\",\"存储\":\"512GB\",\"内存\":\"16GB\"}',5999.00,6499.00,120,0,'https://img.aheadshop.com/sku/mi15u-512-white.jpg',1,'2026-06-02 17:56:32','2026-06-02 17:56:32',0,1),(619,4,'MacBook Pro 14 M3 Pro 18GB+512GB 银色','{\"颜色\":\"银色\",\"处理器\":\"M3 Pro\",\"内存\":\"18GB\",\"硬盘\":\"512GB\"}',14999.00,16999.00,59,0,'https://img.aheadshop.com/sku/mbp14-m3pro-silver.jpg',1,'2026-06-02 17:56:55','2026-06-02 17:56:55',0,1),(620,4,'MacBook Pro 14 M3 Pro 36GB+1TB 深空灰','{\"颜色\":\"深空灰\",\"处理器\":\"M3 Pro\",\"内存\":\"36GB\",\"硬盘\":\"1TB\"}',19999.00,21999.00,40,0,'https://img.aheadshop.com/sku/mbp14-m3pro-gray.jpg',1,'2026-06-02 17:56:55','2026-06-02 17:56:55',0,1),(621,5,'ThinkPad X1 Carbon i7 16GB+512GB 黑色','{\"颜色\":\"黑色\",\"处理器\":\"i7\",\"内存\":\"16GB\",\"硬盘\":\"512GB\"}',9999.00,11999.00,79,0,'https://img.aheadshop.com/sku/x1c-i7-black.jpg',1,'2026-06-02 17:57:18','2026-06-04 15:10:43',0,1),(622,6,'Sony WH-1000XM5 黑色','{\"颜色\":\"黑色\"}',2399.00,2999.00,500,0,'https://img.aheadshop.com/sku/xm5-black.jpg',1,'2026-06-02 17:57:31','2026-06-02 17:57:31',0,1),(623,6,'Sony WH-1000XM5 银色','{\"颜色\":\"银色\"}',2399.00,2999.00,390,0,'https://img.aheadshop.com/sku/xm5-silver.jpg',1,'2026-06-02 17:57:31','2026-06-02 17:57:31',0,1),(624,10,'512 黑色','黑，白，红，黄',2199.00,2499.00,1000,0,NULL,1,'2026-06-02 18:13:14','2026-06-02 18:13:14',0,1),(625,11,'40码','白鞋',99.00,120.00,100,0,NULL,1,'2026-06-02 19:43:53','2026-06-02 19:43:53',0,1),(626,12,'法国品牌','棕色',9.99,999.00,100,0,NULL,1,'2026-06-02 19:46:25','2026-06-02 19:46:25',0,1),(627,13,'一级能耗','白',2999.00,3000.00,100,0,NULL,1,'2026-06-02 19:49:01','2026-06-02 19:49:01',0,1),(628,14,'电竞显示器','24寸',698.00,777.00,199,0,NULL,1,'2026-06-02 19:50:10','2026-06-02 19:50:10',0,1);
/*!40000 ALTER TABLE `sku` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spu`
--

DROP TABLE IF EXISTS `spu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `spu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `subtitle` varchar(500) DEFAULT NULL,
  `category_id` bigint NOT NULL,
  `brand_id` bigint DEFAULT NULL,
  `main_image` varchar(500) DEFAULT NULL,
  `images` text COMMENT 'JSON 数组',
  `detail` text COMMENT '富文本详情',
  `status` tinyint DEFAULT '0',
  `sales` int DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT '0',
  `version` int DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_brand` (`brand_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='SPU 表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spu`
--

LOCK TABLES `spu` WRITE;
/*!40000 ALTER TABLE `spu` DISABLE KEYS */;
INSERT INTO `spu` VALUES (1,'Huawei Mate 70 Pro','旗舰芯片 · 超感知影像 · 鸿蒙系统',100,1,'/upload/product/221119d7b313406084b545c2131a55d2.png','/upload/product/3aef436e4f064d00ad662a8520c70a0d.png,/upload/product/8e3f2e95d1504873b4a2103a40928b90.png',NULL,1,580,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,5),(2,'iPhone 16 Pro Max','A18 Pro 芯片 · 钛金属设计 · 4K 120fps',100,2,'/upload/product/9e134998a6fb45488599ef2cdf124ebb.png','/upload/product/f30ceeb8c49b40abaee0b580991bb2b7.png,/upload/product/08e7645d42204829b2119ae50889f2a8.png',NULL,1,1200,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,3),(3,'Xiaomi 15 Ultra','徕卡光学 · 骁龙 8 至尊版 · 5400mAh',100,3,'/upload/product/7bc26e67fdff43fab0b96dc85ec78e21.png','/upload/product/21e977b358164c9e80e2f4832d54d46b.png',NULL,1,320,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,2),(4,'MacBook Pro 14 M3 Pro','M3 Pro 芯片 · Liquid Retina XDR · 18h续航',200,2,'/upload/product/52c0935c1b3441eaa9ea78cfe41acc73.png','/upload/product/ef13cb6ece9e48ed945ad3b86044af9c.png',NULL,1,210,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,2),(5,'ThinkPad X1 Carbon','14英寸 · 超轻薄 · 商务旗舰',200,4,'/upload/product/9c54428d6e2e4ddc97468a26f24c3a07.png','/upload/product/62cbbb54d21c42f6b6b81e6a789e1b76.png',NULL,1,150,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,2),(6,'Sony WH-1000XM5','行业领先降噪 · 30h续航 · LDAC',121,9,'/upload/product/121c30d75b184b809698e35d7d7d6043.png','/upload/product/ab2115b6fb3a4008aad0ec7eadcb133b.png',NULL,1,890,'2026-05-29 21:30:21','2026-05-29 21:30:21',0,2),(7,'adidashi','三条杠',40,NULL,'/upload/product/88a87bf8972246ec8d339b99b0aec329.jpg','/upload/product/16b28a3333454d48b9dd4f37ef4fefa1.jpg',NULL,0,0,'2026-06-01 13:46:53','2026-06-01 13:46:53',0,4),(8,'遥遥领先先','领先',100,1,'/upload/product/eb26d9d8d7b843fbb0303e2d3606d5ed.png','/upload/product/c926bc1d9c1044f8b1566b1888e35d8b.jpg','遥遥领先',0,0,'2026-06-02 14:44:25','2026-06-02 14:44:25',0,3),(9,'0.01','0.01',100,1,'/upload/product/6741cdf4c14949ccbdda38e880656e03.jpg','/upload/product/9eb5fc1139aa43c19fef08b36ff403f4.jpg','yifen',0,0,'2026-06-02 15:29:07','2026-06-02 15:29:07',0,5),(10,'红魔','续航',101,3,'/upload/product/8c3b1c4bf8dc473f9d44210483776d5c.png','/upload/product/a0e3e1950955421798c4d8e5ee94ca8e.png,/upload/product/eba2a7540eba4ec88e935da664bc2d43.png',NULL,1,0,'2026-06-02 18:13:14','2026-06-02 18:13:14',0,2),(11,'鞋子',NULL,40,NULL,'/upload/product/c096471dea2e47eba0d0515ddf5bc4f0.png',NULL,NULL,1,0,'2026-06-02 19:43:53','2026-06-02 19:43:53',0,2),(12,'名牌包包','好看的包包',321,NULL,'/upload/product/7e0979f6493544839320491e1af73d4d.png','/upload/product/4190f1bef28c4c0ab4ba7ed5a5a93c90.png,/upload/product/7a743c66912f4b4886f5bd667a0b894f.png','快来买',1,0,'2026-06-02 19:46:25','2026-06-02 19:46:25',0,2),(13,'超级大冰箱','冰箱',300,7,'/upload/product/341687b198d846a0a8e0a30656721840.png','/upload/product/36194fe655ad467889694060339c2c0d.png','好用',1,0,'2026-06-02 19:49:01','2026-06-02 19:49:01',0,2),(14,'显示器','显示器',220,5,'/upload/product/f933894e65c54b2eb9365245c80637e8.png','/upload/product/a1654025adac498fbd084a79f32416ab.png,/upload/product/772be17e56984bf18f7d6f695bf29fd7.png','打游戏乱杀',1,0,'2026-06-02 19:50:10','2026-06-02 19:50:10',0,2);
/*!40000 ALTER TABLE `spu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `aheadshop_order`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `aheadshop_order` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `aheadshop_order`;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) NOT NULL,
  `user_id` bigint NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `pay_amount` decimal(10,2) NOT NULL,
  `discount_amount` decimal(10,2) DEFAULT '0.00',
  `freight_amount` decimal(10,2) DEFAULT '0.00',
  `status` tinyint DEFAULT '0',
  `pay_type` tinyint DEFAULT NULL,
  `pay_time` datetime DEFAULT NULL,
  `ship_time` datetime DEFAULT NULL,
  `tracking_number` varchar(100) DEFAULT NULL COMMENT '快递单号',
  `receive_time` datetime DEFAULT NULL,
  `receiver_name` varchar(50) NOT NULL,
  `receiver_phone` varchar(20) NOT NULL,
  `receiver_address` varchar(300) NOT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT '0',
  `version` int DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` VALUES (1,'320088445823356928',1,9999.00,9999.00,0.00,0.00,0,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 14:37:12','2026-06-02 14:37:12',0,1),(2,'320088550005673984',1,9999.00,9999.00,0.00,0.00,0,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 14:37:37','2026-06-02 14:37:37',0,1),(3,'320089706090074112',1,9999.00,9999.00,0.00,0.00,0,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 14:42:13','2026-06-02 14:42:13',0,1),(4,'320090361135501312',1,1.00,1.00,0.00,0.00,2,NULL,NULL,'2026-06-02 14:59:18',NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 14:44:49','2026-06-02 14:55:20',0,2),(5,'320094210227310592',1,1.00,1.00,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 15:00:07','2026-06-02 15:00:07',0,2),(6,'320095281087647744',1,1.00,1.00,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 15:04:22','2026-06-02 15:04:22',0,2),(7,'320095536059387904',1,1.00,1.00,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 15:05:23','2026-06-02 15:05:23',0,2),(8,'320096016869232640',1,1.00,1.00,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 15:07:18','2026-06-02 15:07:18',0,2),(9,'320098038246936576',1,1.00,1.00,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 15:15:19','2026-06-02 15:15:19',0,2),(10,'320100679869927424',1,1.00,1.00,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 15:25:49','2026-06-02 15:25:49',0,2),(11,'320101739070099456',1,0.01,0.01,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 15:30:02','2026-06-02 15:30:02',0,2),(12,'320105098825043968',1,0.01,0.01,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 15:43:23','2026-06-02 15:43:23',0,2),(13,'320107840406687744',1,0.01,0.01,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 15:54:16','2026-06-02 15:54:16',0,2),(14,'320110767473037312',1,0.01,0.01,0.00,0.00,2,NULL,'2026-06-02 16:06:51','2026-06-02 16:16:30',NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 16:05:54','2026-06-02 16:05:54',0,3),(15,'320112108937285632',1,0.01,0.01,0.00,0.00,4,NULL,'2026-06-02 16:11:49','2026-06-02 16:16:28',NULL,'2026-06-02 16:16:46','admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 16:11:14','2026-06-02 16:11:14',0,4),(16,'320112414458777600',1,0.01,0.01,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 16:12:27','2026-06-02 16:12:27',0,2),(17,'320112524349542400',1,0.01,0.01,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 16:12:53','2026-06-02 16:12:53',0,2),(18,'320112707145699328',1,9999.00,9999.00,0.00,0.00,2,NULL,'2026-06-02 16:18:05','2026-06-04 15:11:31',NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 16:13:37','2026-06-02 16:13:37',0,3),(19,'320122935912304640',1,0.01,0.01,0.00,0.00,2,NULL,'2026-06-02 16:54:40','2026-06-04 15:11:29',NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 16:54:16','2026-06-02 16:54:16',0,3),(20,'320125270831009792',1,0.01,0.01,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 17:03:32','2026-06-02 17:03:32',0,2),(21,'320126794592292864',1,5299.00,5299.00,0.00,0.00,2,NULL,'2026-06-02 17:10:02','2026-06-04 15:11:27',NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 17:09:35','2026-06-02 17:09:35',0,3),(22,'320131414349713408',1,0.01,0.01,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 17:27:57','2026-06-02 17:27:57',0,2),(23,'320131482578456576',1,14999.00,14999.00,0.00,0.00,7,NULL,'2026-06-02 17:30:31',NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 17:28:13','2026-06-02 17:28:13',0,5),(24,'320172933916004352',1,19998.00,19998.00,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 20:12:56','2026-06-02 20:12:56',0,2),(25,'320819717453516800',1,9999.00,9999.00,0.00,0.00,2,NULL,'2026-06-04 15:10:43','2026-06-04 15:11:22',NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-04 15:03:01','2026-06-04 15:03:01',0,3);
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `order_no` varchar(50) NOT NULL,
  `sku_id` bigint NOT NULL,
  `spu_id` bigint NOT NULL,
  `sku_name` varchar(200) NOT NULL,
  `sku_image` varchar(500) DEFAULT NULL,
  `specs` text COMMENT '规格快照',
  `price` decimal(10,2) NOT NULL,
  `quantity` int NOT NULL,
  `total_price` decimal(10,2) NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` VALUES (1,4,'320090361135501312',611,8,'买就对了',NULL,'牛',1.00,1,1.00,'2026-06-02 14:44:49'),(2,5,'320094210227310592',611,8,'买就对了',NULL,'牛',1.00,1,1.00,'2026-06-02 15:00:06'),(3,6,'320095281087647744',611,8,'买就对了',NULL,'牛',1.00,1,1.00,'2026-06-02 15:04:22'),(4,7,'320095536059387904',611,8,'买就对了',NULL,'牛',1.00,1,1.00,'2026-06-02 15:05:22'),(5,8,'320096016869232640',611,8,'买就对了',NULL,'牛',1.00,1,1.00,'2026-06-02 15:07:17'),(6,9,'320098038246936576',611,8,'买就对了',NULL,'牛',1.00,1,1.00,'2026-06-02 15:15:19'),(7,10,'320100679869927424',611,8,'买就对了',NULL,'牛',1.00,1,1.00,'2026-06-02 15:25:49'),(8,11,'320101739070099456',612,9,'1分',NULL,NULL,0.01,1,0.01,'2026-06-02 15:30:01'),(9,12,'320105098825043968',612,9,'1分',NULL,NULL,0.01,1,0.01,'2026-06-02 15:43:22'),(10,13,'320107840406687744',612,9,'1分',NULL,NULL,0.01,1,0.01,'2026-06-02 15:54:16'),(11,14,'320110767473037312',612,9,'1分',NULL,NULL,0.01,1,0.01,'2026-06-02 16:05:54'),(12,15,'320112108937285632',612,9,'1分',NULL,NULL,0.01,1,0.01,'2026-06-02 16:11:14'),(13,16,'320112414458777600',612,9,'1分',NULL,NULL,0.01,1,0.01,'2026-06-02 16:12:27'),(14,17,'320112524349542400',612,9,'1分',NULL,NULL,0.01,1,0.01,'2026-06-02 16:12:53'),(15,18,'320112707145699328',607,2,'iPhone 16 Pro Max 256GB 沙漠钛金属','https://img.aheadshop.com/sku/iphone16pm-256-titan.jpg','{\"颜色\":\"沙漠钛金属\",\"存储\":\"256GB\",\"内存\":\"8GB\"}',9999.00,1,9999.00,'2026-06-02 16:13:36'),(16,19,'320122935912304640',612,9,'1分',NULL,NULL,0.01,1,0.01,'2026-06-02 16:54:15'),(17,20,'320125270831009792',612,9,'1分',NULL,NULL,0.01,1,0.01,'2026-06-02 17:03:32'),(18,21,'320126794592292864',301,3,'Xiaomi 15 Ultra 256GB 黑色','https://img.aheadshop.com/sku/mi15u-256-black.jpg','{\"颜色\":\"黑色\",\"存储\":\"256GB\",\"内存\":\"16GB\"}',5299.00,1,5299.00,'2026-06-02 17:09:35'),(19,22,'320131414349713408',612,9,'1分',NULL,NULL,0.01,1,0.01,'2026-06-02 17:27:57'),(20,23,'320131482578456576',401,4,'MacBook Pro 14 M3 Pro 18GB+512GB 银色','https://img.aheadshop.com/sku/mbp14-m3pro-silver.jpg','{\"颜色\":\"银色\",\"处理器\":\"M3 Pro\",\"内存\":\"18GB\",\"硬盘\":\"512GB\"}',14999.00,1,14999.00,'2026-06-02 17:28:13'),(21,24,'320172933916004352',614,2,'iPhone 16 Pro Max 256GB 沙漠钛金属','https://img.aheadshop.com/sku/iphone16pm-256-titan.jpg','{\"颜色\":\"沙漠钛金属\",\"存储\":\"256GB\",\"内存\":\"8GB\"}',9999.00,2,19998.00,'2026-06-02 20:12:56'),(22,25,'320819717453516800',621,5,'ThinkPad X1 Carbon i7 16GB+512GB 黑色','https://img.aheadshop.com/sku/x1c-i7-black.jpg','{\"颜色\":\"黑色\",\"处理器\":\"i7\",\"内存\":\"16GB\",\"硬盘\":\"512GB\"}',9999.00,1,9999.00,'2026-06-04 15:03:01');
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_log`
--

DROP TABLE IF EXISTS `order_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) NOT NULL,
  `operator` varchar(50) DEFAULT NULL COMMENT '操作人',
  `status_before` tinyint DEFAULT NULL COMMENT '变更前状态',
  `status_after` tinyint DEFAULT NULL COMMENT '变更后状态',
  `note` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_log`
--

LOCK TABLES `order_log` WRITE;
/*!40000 ALTER TABLE `order_log` DISABLE KEYS */;
INSERT INTO `order_log` VALUES (1,'320090361135501312','system',NULL,0,'创建订单','2026-06-02 14:44:49'),(2,'320090361135501312','admin',1,2,'管理员发货','2026-06-02 14:59:17'),(3,'320094210227310592','system',NULL,0,'创建订单','2026-06-02 15:00:06'),(4,'320095281087647744','system',NULL,0,'创建订单','2026-06-02 15:04:22'),(5,'320095536059387904','system',NULL,0,'创建订单','2026-06-02 15:05:22'),(6,'320096016869232640','system',NULL,0,'创建订单','2026-06-02 15:07:17'),(7,'320098038246936576','system',NULL,0,'创建订单','2026-06-02 15:15:19'),(8,'320100679869927424','system',NULL,0,'创建订单','2026-06-02 15:25:49'),(9,'320100679869927424','1',0,5,'用户取消订单','2026-06-02 15:27:09'),(10,'320098038246936576','1',0,5,'用户取消订单','2026-06-02 15:28:17'),(11,'320101739070099456','system',NULL,0,'创建订单','2026-06-02 15:30:01'),(12,'320105098825043968','system',NULL,0,'创建订单','2026-06-02 15:43:22'),(13,'320107840406687744','system',NULL,0,'创建订单','2026-06-02 15:54:16'),(14,'320110767473037312','system',NULL,0,'创建订单','2026-06-02 16:05:54'),(15,'320110767473037312','system',0,1,'支付成功','2026-06-02 16:06:51'),(16,'320112108937285632','system',NULL,0,'创建订单','2026-06-02 16:11:14'),(17,'320112108937285632','system',0,1,'支付成功','2026-06-02 16:11:48'),(18,'320112414458777600','system',NULL,0,'创建订单','2026-06-02 16:12:27'),(19,'320112524349542400','system',NULL,0,'创建订单','2026-06-02 16:12:53'),(20,'320112707145699328','system',NULL,0,'创建订单','2026-06-02 16:13:36'),(21,'320112108937285632','admin',1,2,'管理员发货','2026-06-02 16:16:28'),(22,'320110767473037312','admin',1,2,'管理员发货','2026-06-02 16:16:30'),(23,'320112108937285632','1',2,4,'用户确认收货','2026-06-02 16:16:46'),(24,'320112707145699328','system',0,1,'支付成功','2026-06-02 16:18:04'),(25,'320122935912304640','system',NULL,0,'创建订单','2026-06-02 16:54:15'),(26,'320122935912304640','system',0,1,'支付成功','2026-06-02 16:54:40'),(27,'320125270831009792','system',NULL,0,'创建订单','2026-06-02 17:03:32'),(28,'320126794592292864','system',NULL,0,'创建订单','2026-06-02 17:09:35'),(29,'320126794592292864','system',0,1,'支付成功','2026-06-02 17:10:01'),(30,'320131414349713408','system',NULL,0,'创建订单','2026-06-02 17:27:57'),(31,'320131482578456576','system',NULL,0,'创建订单','2026-06-02 17:28:13'),(32,'320131482578456576','system',0,1,'支付成功','2026-06-02 17:30:30'),(33,'320172933916004352','system',NULL,0,'创建订单','2026-06-02 20:12:56'),(34,'320819717453516800','system',NULL,0,'创建订单','2026-06-04 15:03:01'),(35,'320819717453516800','system',0,1,'支付成功','2026-06-04 15:10:43'),(36,'320819717453516800','admin',1,2,'管理员发货','2026-06-04 15:11:22'),(37,'320126794592292864','admin',1,2,'管理员发货','2026-06-04 15:11:26'),(38,'320122935912304640','admin',1,2,'管理员发货','2026-06-04 15:11:28'),(39,'320112707145699328','admin',1,2,'管理员发货','2026-06-04 15:11:30');
/*!40000 ALTER TABLE `order_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refund`
--

DROP TABLE IF EXISTS `refund`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refund` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `refund_no` varchar(50) NOT NULL COMMENT '退款单号',
  `order_no` varchar(50) NOT NULL,
  `user_id` bigint NOT NULL,
  `refund_amount` decimal(10,2) NOT NULL,
  `refund_reason` varchar(500) DEFAULT NULL,
  `refund_type` tinyint DEFAULT NULL COMMENT '1=仅退款 2=退货退款',
  `status` tinyint DEFAULT '0' COMMENT '0=待审核 1=已审核 2=退款中 3=已退款 4=已拒绝',
  `audit_remark` varchar(500) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_refund_no` (`refund_no`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='退款表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refund`
--

LOCK TABLES `refund` WRITE;
/*!40000 ALTER TABLE `refund` DISABLE KEYS */;
INSERT INTO `refund` VALUES (1,'320132108997758976','320131482578456576',1,14999.00,'123',2,2,NULL,'2026-06-02 17:30:42','2026-06-02 17:30:42');
/*!40000 ALTER TABLE `refund` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `aheadshop_pay`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `aheadshop_pay` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `aheadshop_pay`;

--
-- Table structure for table `pay_record`
--

DROP TABLE IF EXISTS `pay_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pay_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `pay_no` varchar(50) NOT NULL COMMENT '支付流水号',
  `order_no` varchar(50) NOT NULL,
  `user_id` bigint NOT NULL,
  `pay_amount` decimal(10,2) NOT NULL,
  `pay_type` tinyint NOT NULL COMMENT '1=支付宝 2=微信',
  `trade_no` varchar(100) DEFAULT NULL COMMENT '第三方交易号',
  `status` tinyint DEFAULT '0' COMMENT '0=待支付 1=支付成功 2=支付失败 3=已退款',
  `pay_time` datetime DEFAULT NULL,
  `callback_time` datetime DEFAULT NULL,
  `callback_content` text,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pay_no` (`pay_no`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_trade_no` (`trade_no`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pay_record`
--

LOCK TABLES `pay_record` WRITE;
/*!40000 ALTER TABLE `pay_record` DISABLE KEYS */;
INSERT INTO `pay_record` VALUES (1,'320100693140705280','320100679869927424',1,1.00,1,NULL,0,NULL,NULL,NULL,'2026-06-02 15:25:52','2026-06-02 15:25:52'),(2,'320101029876207616','320098038246936576',1,1.00,1,NULL,0,NULL,NULL,NULL,'2026-06-02 15:27:12','2026-06-02 15:27:12'),(3,'320101753838243840','320101739070099456',1,0.01,1,NULL,0,NULL,NULL,NULL,'2026-06-02 15:30:05','2026-06-02 15:30:05'),(4,'320105110531346432','320105098825043968',1,0.01,1,NULL,0,NULL,NULL,NULL,'2026-06-02 15:43:25','2026-06-02 15:43:25'),(5,'320107847725748224','320107840406687744',1,0.01,1,NULL,0,NULL,NULL,NULL,'2026-06-02 15:54:18','2026-06-02 15:54:18'),(6,'320110775760982016','320110767473037312',1,0.01,1,'2026060222001472230509656905',1,'2026-06-02 16:06:51','2026-06-02 16:06:51','{gmt_create=2026-06-02 16:06:25, charset=utf-8, gmt_payment=2026-06-02 16:06:50, notify_time=2026-06-02 16:06:51, subject=AheadShop订单-320110767473037312, buyer_id=2088722101872239, invoice_amount=0.01, version=1.0, notify_id=2026060201222160651172230509586367, fund_bill_list=[{\"amount\":\"0.01\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=320110775760982016, total_amount=0.01, trade_status=TRADE_SUCCESS, trade_no=2026060222001472230509656905, auth_app_id=9021000164627929, receipt_amount=0.01, point_amount=0.00, buyer_pay_amount=0.01, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-02 16:05:56','2026-06-02 16:05:56'),(7,'320112117866958848','320112108937285632',1,0.01,1,'2026060222001472230509661645',1,'2026-06-02 16:11:49','2026-06-02 16:11:49','{gmt_create=2026-06-02 16:11:30, charset=utf-8, gmt_payment=2026-06-02 16:11:48, notify_time=2026-06-02 16:11:49, subject=AheadShop订单-320112108937285632, buyer_id=2088722101872239, invoice_amount=0.01, version=1.0, notify_id=2026060201222161148172230509582061, fund_bill_list=[{\"amount\":\"0.01\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=320112117866958848, total_amount=0.01, trade_status=TRADE_SUCCESS, trade_no=2026060222001472230509661645, auth_app_id=9021000164627929, receipt_amount=0.01, point_amount=0.00, buyer_pay_amount=0.01, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-02 16:11:16','2026-06-02 16:11:16'),(8,'320112421970776064','320112414458777600',1,0.01,1,NULL,0,NULL,NULL,NULL,'2026-06-02 16:12:28','2026-06-02 16:12:28'),(9,'320112531781849088','320112524349542400',1,0.01,1,NULL,0,NULL,NULL,NULL,'2026-06-02 16:12:54','2026-06-02 16:12:54'),(10,'320112716142481408','320112707145699328',1,9999.00,1,'2026060222001472230509663605',1,'2026-06-02 16:18:05','2026-06-02 16:18:05','{gmt_create=2026-06-02 16:17:49, charset=utf-8, gmt_payment=2026-06-02 16:18:04, notify_time=2026-06-02 16:18:05, subject=AheadShop订单-320112707145699328, buyer_id=2088722101872239, invoice_amount=9999.00, version=1.0, notify_id=2026060201222161805172230509586368, fund_bill_list=[{\"amount\":\"9999.00\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=320112716142481408, total_amount=9999.00, trade_status=TRADE_SUCCESS, trade_no=2026060222001472230509663605, auth_app_id=9021000164627929, receipt_amount=9999.00, point_amount=0.00, buyer_pay_amount=9999.00, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-02 16:13:38','2026-06-02 16:13:38'),(11,'320122944963612672','320122935912304640',1,0.01,1,'2026060222001472230509658619',1,'2026-06-02 16:54:40','2026-06-02 16:54:40','{gmt_create=2026-06-02 16:54:32, charset=utf-8, gmt_payment=2026-06-02 16:54:39, notify_time=2026-06-02 16:54:41, subject=AheadShop订单-320122935912304640, buyer_id=2088722101872239, invoice_amount=0.01, version=1.0, notify_id=2026060201222165440172230509582063, fund_bill_list=[{\"amount\":\"0.01\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=320122944963612672, total_amount=0.01, trade_status=TRADE_SUCCESS, trade_no=2026060222001472230509658619, auth_app_id=9021000164627929, receipt_amount=0.01, point_amount=0.00, buyer_pay_amount=0.01, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-02 16:54:17','2026-06-02 16:54:17'),(12,'320125298286923776','320125270831009792',1,0.01,1,NULL,0,NULL,NULL,NULL,'2026-06-02 17:03:38','2026-06-02 17:03:38'),(13,'320126815970660352','320126794592292864',1,5299.00,1,'2026060222001472230509663606',1,'2026-06-02 17:10:02','2026-06-02 17:10:02','{gmt_create=2026-06-02 17:09:54, charset=utf-8, gmt_payment=2026-06-02 17:10:01, notify_time=2026-06-02 17:10:02, subject=AheadShop订单-320126794592292864, buyer_id=2088722101872239, invoice_amount=5299.00, version=1.0, notify_id=2026060201222171001172230509587596, fund_bill_list=[{\"amount\":\"5299.00\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=320126815970660352, total_amount=5299.00, trade_status=TRADE_SUCCESS, trade_no=2026060222001472230509663606, auth_app_id=9021000164627929, receipt_amount=5299.00, point_amount=0.00, buyer_pay_amount=5299.00, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-02 17:09:40','2026-06-02 17:09:40'),(14,'320131425422675968','320131414349713408',1,0.01,1,NULL,0,NULL,NULL,NULL,'2026-06-02 17:27:59','2026-06-02 17:27:59'),(15,'320131491357134848','320131482578456576',1,14999.00,1,'2026060222001472230509658620',3,'2026-06-02 17:30:31','2026-06-02 17:30:31','{gmt_create=2026-06-02 17:30:23, charset=utf-8, gmt_payment=2026-06-02 17:30:30, notify_time=2026-06-02 17:30:31, subject=AheadShop订单-320131482578456576, buyer_id=2088722101872239, invoice_amount=14999.00, version=1.0, notify_id=2026060201222173030172230509587597, fund_bill_list=[{\"amount\":\"14999.00\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=320131491357134848, total_amount=14999.00, trade_status=TRADE_SUCCESS, trade_no=2026060222001472230509658620, auth_app_id=9021000164627929, receipt_amount=14999.00, point_amount=0.00, buyer_pay_amount=14999.00, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-02 17:28:15','2026-06-02 17:28:15'),(16,'320172947480383488','320172933916004352',1,19998.00,1,NULL,0,NULL,NULL,NULL,'2026-06-02 20:12:59','2026-06-02 20:12:59'),(17,'320819731957420032','320819717453516800',1,9999.00,1,'2026060422001472230509672476',1,'2026-06-04 15:10:43','2026-06-04 15:10:43','{gmt_create=2026-06-04 15:10:34, charset=utf-8, gmt_payment=2026-06-04 15:10:42, notify_time=2026-06-04 15:10:43, subject=AheadShop订单-320819717453516800, buyer_id=2088722101872239, invoice_amount=9999.00, version=1.0, notify_id=2026060401222151043172230509599387, fund_bill_list=[{\"amount\":\"9999.00\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=320819731957420032, total_amount=9999.00, trade_status=TRADE_SUCCESS, trade_no=2026060422001472230509672476, auth_app_id=9021000164627929, receipt_amount=9999.00, point_amount=0.00, buyer_pay_amount=9999.00, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-04 15:03:04','2026-06-04 15:03:04');
/*!40000 ALTER TABLE `pay_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `expr`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `expr` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `expr`;

--
-- Table structure for table `order_info`
--

DROP TABLE IF EXISTS `order_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_info` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `num` int NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`order_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_info`
--

LOCK TABLES `order_info` WRITE;
/*!40000 ALTER TABLE `order_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_info`
--

DROP TABLE IF EXISTS `stock_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stock_info` (
  `stock_id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `stock_num` int NOT NULL,
  PRIMARY KEY (`stock_id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_info`
--

LOCK TABLES `stock_info` WRITE;
/*!40000 ALTER TABLE `stock_info` DISABLE KEYS */;
INSERT INTO `stock_info` VALUES (1,1,5),(2,2,10),(3,3,15);
/*!40000 ALTER TABLE `stock_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `undo_log`
--

DROP TABLE IF EXISTS `undo_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `undo_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `branch_id` bigint NOT NULL,
  `xid` varchar(100) NOT NULL,
  `context` varchar(128) NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `undo_log`
--

LOCK TABLES `undo_log` WRITE;
/*!40000 ALTER TABLE `undo_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `undo_log` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-12 11:22:37

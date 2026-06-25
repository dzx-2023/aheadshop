
-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: aheadshop_aichat
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
-- Current Database: `aheadshop_aichat`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `aheadshop_aichat` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `aheadshop_aichat`;

--
-- Table structure for table `chat_faq`
--

DROP TABLE IF EXISTS `chat_faq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_faq` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `question` varchar(500) NOT NULL COMMENT '问题/关键词',
  `answer` text NOT NULL COMMENT '标准回答',
  `keywords` varchar(500) DEFAULT NULL COMMENT '匹配关键词，逗号分隔',
  `sort_order` int DEFAULT '0' COMMENT '排序权重，数值越大优先级越高',
  `status` tinyint DEFAULT '1' COMMENT '1-启用 0-禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT '0',
  `version` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI客服常见问题FAQ表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_faq`
--

LOCK TABLES `chat_faq` WRITE;
/*!40000 ALTER TABLE `chat_faq` DISABLE KEYS */;
INSERT INTO `chat_faq` VALUES (1,'退换货政策是什么？','我们的退换货政策：7天无理由退换货，商品需保持原包装完好。退款将在收到退货后3-5个工作日内原路返回。','退货,退换,退款,退换货',100,1,'2026-06-20 22:33:42','2026-06-20 22:33:42',0,0),(2,'发货和物流信息','发货说明：下单后1-2个工作日内发货，支持顺丰、中通、圆通等快递。满99元包邮，不满99元运费8元。','发货,快递,物流,配送,邮费,运费',90,1,'2026-06-20 22:33:42','2026-06-20 22:33:42',0,0),(3,'如何开发票？','发票说明：所有订单均支持开具电子普通发票，下单时可在备注中注明发票抬头和税号。','发票,开票',80,1,'2026-06-20 22:33:42','2026-06-20 22:33:42',0,0),(4,'有什么优惠活动？','优惠活动：请关注首页活动专区，不定期推出限时折扣和满减优惠。新用户注册即享首单9折优惠。','优惠,优惠券,折扣,活动,促销',85,1,'2026-06-20 22:33:42','2026-06-20 22:33:42',0,0),(5,'支持哪些支付方式？','支付方式：支持微信支付、支付宝、银行卡等多种支付方式。如遇支付问题，请联系人工客服。','支付,付款,怎么付',75,1,'2026-06-20 22:33:42','2026-06-20 22:33:42',0,0),(6,'如何联系人工客服？','如需人工客服，请点击页面右上角「联系人工客服」按钮，工作时间为周一至周五 9:00-18:00。','人工,人工客服,转人工',95,1,'2026-06-20 22:33:42','2026-06-20 22:33:42',0,0);
/*!40000 ALTER TABLE `chat_faq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_message`
--

DROP TABLE IF EXISTS `chat_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` bigint NOT NULL,
  `role` varchar(20) NOT NULL,
  `content` text NOT NULL,
  `token_count` int DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT '0',
  `version` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_session_id` (`session_id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI客服消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_message`
--

LOCK TABLES `chat_message` WRITE;
/*!40000 ALTER TABLE `chat_message` DISABLE KEYS */;
INSERT INTO `chat_message` VALUES (1,1,'user','你好',10,'2026-06-20 17:42:58','2026-06-20 21:04:04',1,0),(2,1,'assistant','你好！有什么可以帮您？',20,'2026-06-20 17:42:58','2026-06-20 21:04:04',1,0),(5,2,'user','你好，你是谁？',0,'2026-06-20 19:13:29','2026-06-20 21:45:52',1,0),(6,2,'assistant','你好！我是 AheadShop 的智能客服助手 🛍️。很高兴为您服务，请问有什么我可以帮您的吗？😊',0,'2026-06-20 19:13:38','2026-06-20 21:45:52',1,0),(7,2,'user','你能干什么？',0,'2026-06-20 19:52:10','2026-06-20 21:45:52',1,0),(8,2,'assistant','你好呀！我是 AheadShop 的智能客服助手 🛍️，主要可以帮您处理以下事情哦：\n\n1. **查询商品信息** 📦：了解商品详情、规格、库存等（基于现有数据）。\n2. **订单咨询** 📝：查看订单状态、物流信息等。\n3. **售后指导** 🔄：解答关于退换货流程的问题，比如我们的“7天无理由退换货”政策。\n\n⚠️ 如果您需要办理**退款、投诉**等敏感操作，或者遇到我无法直接解决的特殊情况，我会引导您联系**人工客服**为您处理 💁‍♀️。\n\n请问您现在想了解哪方面的信息呢？😊',0,'2026-06-20 19:53:21','2026-06-20 21:45:52',1,0),(10,3,'user','你好，还在吗？',0,'2026-06-20 21:11:19','2026-06-20 21:45:54',1,0),(11,3,'assistant','在的呢！👋 很高兴为您服务～ 😊\n\n请问有什么可以帮您的吗？无论是查询商品详情、订单状态还是其他问题，我都在这里随时为您解答！✨',0,'2026-06-20 21:11:29','2026-06-20 21:45:54',1,0),(12,4,'user','你知道ai是什么吗？',0,'2026-06-20 21:35:20','2026-06-21 15:50:57',1,0),(13,4,'assistant','你好呀！😊 我是 AheadShop 的智能客服助手，主要负责为您解答关于商品、订单和售后服务的问题哦。\n\n如果您对我们的产品有任何疑问，或者需要帮助处理退换货事宜，欢迎随时告诉我！🛍️✨',0,'2026-06-20 21:36:35','2026-06-21 15:50:57',1,0),(14,4,'user','查询我的购物车',0,'2026-06-20 21:46:01','2026-06-21 15:50:57',1,0),(15,4,'assistant','抱歉，作为智能客服助手，我无法直接访问您的个人账户数据或查看购物车内容哦 🛒。\n\n建议您登录 AheadShop 官网或 APP，在“个人中心”或“购物车”页面查看您的商品详情。如果有其他关于商品或订单的问题，欢迎随时问我！😊',0,'2026-06-20 21:46:32','2026-06-21 15:50:57',1,0),(16,4,'user','1+1',0,'2026-06-20 21:47:04','2026-06-21 15:50:57',1,0),(17,4,'assistant','1 + 1 = 2 😊\n\n如果您有其他数学问题或关于 AheadShop 的疑问，欢迎随时告诉我！🛍️',0,'2026-06-20 21:47:12','2026-06-21 15:50:57',1,0),(18,4,'user','显示器多少钱',0,'2026-06-20 23:39:01','2026-06-21 15:50:57',1,0),(19,4,'assistant','您好呀！👋 目前我这边没有具体的显示器商品库存信息，无法直接告诉您确切的价格哦。\n\n建议您前往 AheadShop 的首页或搜索栏查看最新上架的显示器型号及价格 🖥️✨。如果您需要推荐或对比不同款式，也可以告诉我您的预算和需求，我会尽力为您提供参考建议！💡',91,'2026-06-20 23:39:15','2026-06-21 15:50:57',1,0),(20,4,'user','鞋子多少钱？',0,'2026-06-20 23:41:31','2026-06-21 15:50:57',1,0),(21,4,'assistant','您好呀！👟 鞋子种类很多，价格差异也很大哦～\n\n为了给您更准确的报价，能告诉我您想了解**具体品牌、款式**或者**大概的预算范围**吗？💰\n\n您可以去 AheadShop 搜索一下心仪的鞋款，或者把商品链接/名称发给我，我帮您看看是否有相关优惠信息！✨',90,'2026-06-20 23:41:52','2026-06-21 15:50:57',1,0),(22,4,'user','有什么手机推荐',0,'2026-06-20 23:42:52','2026-06-21 15:50:57',1,0),(23,4,'assistant','您好呀！📱 目前我这边没有具体的手机商品库存信息，无法直接为您推荐特定型号哦。\n\n建议您前往 AheadShop 的手机专区浏览最新款式，或者告诉我您的**预算范围**和**主要需求**（比如拍照、游戏、续航等），我可以帮您梳理选购要点！✨ 如有具体订单或售后问题，也欢迎随时咨询～',98,'2026-06-20 23:43:00','2026-06-21 15:50:57',1,0),(24,5,'user','有什么热销商品推荐吗？',0,'2026-06-21 14:19:54','2026-06-21 14:19:54',0,0),(25,5,'assistant','您好呀！👋 目前我们店里的热销商品更新很快，但我需要您提供具体的**商品知识库内容**才能为您准确推荐哦。😅\n\n您可以告诉我您感兴趣的具体商品名称，或者如果您有订单号，我也可以帮您查询相关商品详情！📦✨\n\n如果有其他具体问题，也欢迎随时问我～ 🛍️',584,'2026-06-21 14:20:05','2026-06-21 14:20:05',0,0),(26,4,'user','有没有手机',0,'2026-06-21 14:56:51','2026-06-21 15:50:57',1,0),(27,4,'assistant','亲，AheadShop 目前主要专注于家居生活、时尚服饰及精选好物哦 🏠👗。暂时还没有上架手机类电子产品呢 📱❌。\n\n不过，如果您对我们现有的商品感兴趣，或者想了解具体的款式和价格，随时可以问我！我会很乐意为您介绍～ ✨',78,'2026-06-21 14:57:00','2026-06-21 15:50:57',1,0),(28,4,'user','有没有时尚服饰',0,'2026-06-21 14:57:32','2026-06-21 15:50:57',1,0),(29,4,'assistant','有的哦！👗 AheadShop 拥有丰富的时尚服饰系列，涵盖潮流女装、男士休闲装以及当季新品 🧥👖。\n\n您可以告诉我您喜欢的风格（如简约、复古、运动等）或具体品类，我来为您推荐合适的款式吧！✨🛍️',72,'2026-06-21 14:58:25','2026-06-21 15:50:57',1,0),(30,4,'user','你随便推荐一款',0,'2026-06-21 15:00:04','2026-06-21 15:50:57',1,0),(31,4,'assistant','没问题！给您推荐一款我们的爆款单品：**极简风纯棉基础款T恤** 👕\n\n✨ **推荐理由：**\n*   **面料舒适**：100%精梳棉，透气吸汗，亲肤感满分 💯\n*   **百搭经典**：简约纯色设计，单穿或内搭都很合适，轻松驾驭各种场合 🌟\n*   **品质保证**：做工精细，久洗不易变形，性价比超高！\n\n这款有多种颜色可选哦，您喜欢什么色系呢？😍🛍️',126,'2026-06-21 15:00:06','2026-06-21 15:50:57',1,0),(32,4,'user','那运动',0,'2026-06-21 15:00:46','2026-06-21 15:50:57',1,0),(33,4,'assistant','那就为您推荐这款：**高弹速干运动套装** 🏃‍♂️💨\n\n✨ **推荐理由：**\n*   **轻盈透气**：采用高科技速干面料，运动出汗也不黏身，时刻保持干爽舒适 🌬️\n*   **灵活自如**：四向弹力设计，大幅度动作也能完美贴合，无论是跑步、健身还是瑜伽都超合适 🧘‍♀️\n*   **时尚剪裁**：修身版型不紧绷，展现运动线条美，日常穿也很有型！\n\n这套有黑、白、灰等多种颜色，您想看看哪一款？😎🛒',140,'2026-06-21 15:00:48','2026-06-21 15:50:57',1,0),(34,4,'user','有没有链接我直达',0,'2026-06-21 15:01:06','2026-06-21 15:50:57',1,0),(35,4,'assistant','抱歉呀，我目前无法直接发送商品购买链接或跳转链接哦 🔗❌。\n\n建议您直接在 AheadShop 的首页搜索栏输入 **“高弹速干运动套装”** 即可找到该商品啦！🔍 或者您也可以浏览“运动专区”，会有更多精选推荐哦～ 🏃‍♂️✨\n\n如果有其他关于尺码或材质的疑问，随时问我！😊',99,'2026-06-21 15:02:01','2026-06-21 15:50:57',1,0),(36,4,'user','我想查询一下我的订单',0,'2026-06-21 15:51:01','2026-06-21 15:51:01',0,0),(37,4,'assistant','您好！😊 为了帮您查询订单，请您提供一下**订单号**或者**注册手机号**。\n\n我会尽快为您核实订单状态～ 📦',44,'2026-06-21 15:51:15','2026-06-21 15:51:15',0,0),(38,6,'user','有什么热销商品推荐吗？',0,'2026-06-21 15:51:42','2026-06-21 15:51:42',0,0),(39,6,'assistant','您好呀！👋 目前我们店的热销商品主要包括以下几类，非常受欢迎哦：\n\n1. **经典基础款 T恤** 🎽：透气舒适，百搭首选，很多顾客回购！\n2. **潮流卫衣** 👕：版型宽松时尚，秋冬季节销量冠军。\n3. **休闲束脚裤** 👖：活动自如，日常通勤或运动都很合适。\n\n不过，具体的库存和最新爆款可能会实时变动呢～ 📦\n\n如果您想查看当前最火的单品详情或下单，建议您直接联系我们的**人工客服**小姐姐，她会为您提供一对一的精准推荐和服务！💁‍♀️✨\n\n期待为您服务！',167,'2026-06-21 15:51:44','2026-06-21 15:51:44',0,0),(40,6,'user','你好？',0,'2026-06-24 09:47:44','2026-06-24 09:47:44',0,0),(41,6,'assistant','你好呀！👋 我是 AheadShop 的智能客服助手。请问有什么可以帮您的吗？比如查询商品、订单状态或者售后服务等问题～ 😊',44,'2026-06-24 09:48:54','2026-06-24 09:48:54',0,0),(42,6,'user','推荐衣服商品',0,'2026-06-24 09:49:08','2026-06-24 09:49:08',0,0),(43,6,'assistant','您好呀！👋 很高兴为您服务。\n\n由于我目前无法直接访问 AheadShop 的实时商品库存列表，为了给您提供最准确的推荐，能否请您告诉我一些具体的偏好呢？例如：\n\n*   **风格**：休闲、商务、运动、甜美还是酷飒？😎\n*   **场景**：日常通勤、约会、旅行还是居家？\n*   **季节/天气**：需要保暖、透气还是防晒？☀️\n*   **预算范围**：大概的心理价位？💰\n\n或者，您可以直接告诉我您想找什么类型的衣服（比如“黑色风衣”或“白色T恤”），我会尽力为您匹配最合适的商品信息！✨',171,'2026-06-24 09:50:16','2026-06-24 09:50:16',0,0),(44,6,'user','推荐手机',0,'2026-06-24 11:40:11','2026-06-24 11:40:11',0,0),(45,6,'assistant','您好！👋 我是 AheadShop 的智能客服助手。\n\n目前我的商品知识库中**暂未收录具体的手机型号信息**，因此无法直接为您推荐特定机型，以免给您提供不准确的数据 📱❌。\n\n为了帮您找到最合适的手机，建议您：\n1. 访问 AheadShop 官网或 APP 查看最新上架的数码产品；\n2. 或者**联系我们的在线人工客服**，他们会根据您的预算和需求提供更专业的选购建议哦！😊\n\n如有其他非手机类的问题，欢迎随时向我提问！✨',146,'2026-06-24 11:40:14','2026-06-24 11:40:14',0,0),(46,6,'user','你的知识库有什么？',0,'2026-06-24 11:40:25','2026-06-24 11:40:25',0,0),(47,6,'assistant','您好！👋 我是 AheadShop 的智能客服助手。\n\n关于我内部具体包含哪些商品数据或知识库细节，属于店铺内部信息，暂时无法向您详细列举 🤫。\n\n不过，您可以直接在 AheadShop 的官方渠道（如网站或 APP）浏览我们目前在售的所有商品 🛍️。如果您想了解特定类别（如服装、数码、家居等）是否有货，或者需要查询订单、售后等具体服务支持，欢迎随时告诉我，我会尽力为您解答！😊',135,'2026-06-24 11:40:35','2026-06-24 11:40:35',0,0),(48,6,'user','你是谁',0,'2026-06-24 20:04:26','2026-06-24 20:04:26',0,0),(49,6,'assistant','你好呀！👋 我是 AheadShop 的智能客服助手。很高兴为您服务！😊\n\n请问有什么我可以帮您的吗？无论是查询商品信息、订单状态还是售后问题，都欢迎随时问我～ 🛍️✨',60,'2026-06-24 20:05:05','2026-06-24 20:05:05',0,0);
/*!40000 ALTER TABLE `chat_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_session`
--

DROP TABLE IF EXISTS `chat_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_session` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(100) DEFAULT NULL,
  `status` tinyint DEFAULT '1',
  `total_tokens` int DEFAULT '0' COMMENT '会话累计token用量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT '0',
  `version` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI客服对话会话表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_session`
--

LOCK TABLES `chat_session` WRITE;
/*!40000 ALTER TABLE `chat_session` DISABLE KEYS */;
INSERT INTO `chat_session` VALUES (1,1,'测试会话',1,0,'2026-06-20 17:42:50','2026-06-20 22:03:25',1,5),(2,1,'你好，你是谁？',1,0,'2026-06-20 18:26:49','2026-06-20 22:03:22',1,5),(3,1,'你好，还在吗？',1,0,'2026-06-20 21:03:51','2026-06-20 22:03:21',1,5),(4,1,'你知道ai是什么吗？',1,794,'2026-06-20 21:07:45','2026-06-21 15:50:57',1,3),(5,3,'有什么热销商品推荐吗？',1,0,'2026-06-21 14:19:54','2026-06-21 14:19:54',0,1),(6,1,'有什么热销商品推荐吗？',1,723,'2026-06-21 15:51:01','2026-06-24 20:05:05',0,1);
/*!40000 ALTER TABLE `chat_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `aheadshop_distribution`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `aheadshop_distribution` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `aheadshop_distribution`;

--
-- Table structure for table `commission_record`
--

DROP TABLE IF EXISTS `commission_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `commission_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `distributor_id` bigint NOT NULL COMMENT '分销商ID',
  `order_no` varchar(50) NOT NULL COMMENT '关联订单号',
  `buyer_id` bigint NOT NULL COMMENT '购买者ID',
  `order_amount` decimal(10,2) NOT NULL COMMENT '订单实付金额',
  `commission_rate` decimal(5,4) NOT NULL COMMENT '佣金比例（如0.1000=10%）',
  `commission_amount` decimal(10,2) NOT NULL COMMENT '佣金金额',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态 0=冻结 1=已结算 2=已取消(退款)',
  `settle_time` datetime DEFAULT NULL COMMENT '结算时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  `version` int NOT NULL DEFAULT '1' COMMENT '乐观锁版本',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_distributor_id` (`distributor_id`),
  KEY `idx_buyer_id` (`buyer_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='佣金记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commission_record`
--

LOCK TABLES `commission_record` WRITE;
/*!40000 ALTER TABLE `commission_record` DISABLE KEYS */;
INSERT INTO `commission_record` VALUES (1,1,'328181295572193280',12,9999.00,0.1000,999.90,1,'2026-06-24 22:48:03','2026-06-24 22:37:19','2026-06-24 22:48:03',0,1),(2,1,'328182714689785856',12,5299.00,0.1000,529.90,1,'2026-06-24 22:48:03','2026-06-24 22:42:13','2026-06-24 22:48:03',0,1),(3,1,'328185907985387520',13,14999.00,0.1000,1499.90,0,NULL,'2026-06-24 22:56:59','2026-06-24 22:56:59',0,1);
/*!40000 ALTER TABLE `commission_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `distributor`
--

DROP TABLE IF EXISTS `distributor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `distributor` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分销商ID（=用户ID）',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `invite_code` varchar(16) NOT NULL COMMENT '邀请码',
  `level` tinyint NOT NULL DEFAULT '1' COMMENT '分销等级 1=普通 2=高级',
  `total_commission` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '累计佣金',
  `available_commission` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '可提现佣金',
  `frozen_commission` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '冻结佣金（保护期）',
  `total_team_count` int NOT NULL DEFAULT '0' COMMENT '团队总人数',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 0=禁用 1=正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除 0=正常 1=删除',
  `version` int NOT NULL DEFAULT '1' COMMENT '乐观锁版本',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  UNIQUE KEY `uk_invite_code` (`invite_code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='分销商表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `distributor`
--

LOCK TABLES `distributor` WRITE;
/*!40000 ALTER TABLE `distributor` DISABLE KEYS */;
INSERT INTO `distributor` VALUES (1,9,'P6H2V8LL',1,0.00,0.00,0.00,0,1,'2026-06-24 21:20:23','2026-06-24 21:20:23',0,1),(2,3,'7610A2',1,0.00,0.00,0.00,1,1,'2026-06-24 22:03:13','2026-06-24 22:03:13',0,1),(3,1,'A88888',1,3029.70,0.00,3029.70,2,1,'2026-06-24 22:07:17','2026-06-24 22:48:03',0,14);
/*!40000 ALTER TABLE `distributor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `referral_relation`
--

DROP TABLE IF EXISTS `referral_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `referral_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '被推荐人（新用户）',
  `referrer_id` bigint NOT NULL COMMENT '推荐人（上级分销商）',
  `invite_code` varchar(16) NOT NULL COMMENT '使用的邀请码',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_referrer_id` (`referrer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='推荐关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `referral_relation`
--

LOCK TABLES `referral_relation` WRITE;
/*!40000 ALTER TABLE `referral_relation` DISABLE KEYS */;
INSERT INTO `referral_relation` VALUES (1,10,3,'7610A2','2026-06-24 22:03:12'),(3,12,1,'A88888','2026-06-24 22:09:58'),(4,13,1,'A88888','2026-06-24 22:52:27');
/*!40000 ALTER TABLE `referral_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `withdrawal_record`
--

DROP TABLE IF EXISTS `withdrawal_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `withdrawal_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `distributor_id` bigint NOT NULL COMMENT '分销商ID',
  `amount` decimal(10,2) NOT NULL COMMENT '提现金额',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态 0=待审核 1=已通过 2=已打款 3=已拒绝',
  `audit_user_id` bigint DEFAULT NULL COMMENT '审核人ID',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `pay_time` datetime DEFAULT NULL COMMENT '打款时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注/拒绝原因',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  `version` int NOT NULL DEFAULT '1' COMMENT '乐观锁版本',
  PRIMARY KEY (`id`),
  KEY `idx_distributor_id` (`distributor_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='提现记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `withdrawal_record`
--

LOCK TABLES `withdrawal_record` WRITE;
/*!40000 ALTER TABLE `withdrawal_record` DISABLE KEYS */;
INSERT INTO `withdrawal_record` VALUES (1,1,100.00,0,NULL,NULL,NULL,NULL,'2026-06-24 22:48:27','2026-06-24 22:48:27',0,1),(2,1,100.00,0,NULL,NULL,NULL,NULL,'2026-06-24 22:48:40','2026-06-24 22:48:40',0,1),(3,1,100.00,0,NULL,NULL,NULL,NULL,'2026-06-24 22:48:50','2026-06-24 22:48:50',0,1),(4,1,100.00,0,NULL,NULL,NULL,NULL,'2026-06-24 22:49:00','2026-06-24 22:49:00',0,1),(5,1,400.00,0,NULL,NULL,NULL,NULL,'2026-06-24 23:06:10','2026-06-24 23:06:10',0,1),(6,1,200.00,0,NULL,NULL,NULL,NULL,'2026-06-24 23:06:53','2026-06-24 23:06:53',0,1),(7,1,529.80,0,NULL,NULL,NULL,NULL,'2026-06-24 23:07:07','2026-06-24 23:07:07',0,1);
/*!40000 ALTER TABLE `withdrawal_record` ENABLE KEYS */;
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
  `referrer_id` bigint DEFAULT NULL COMMENT '推荐人ID（用于佣金归属）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_referrer_id` (`referrer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` VALUES (1,'320088445823356928',1,9999.00,9999.00,0.00,0.00,0,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 14:37:12','2026-06-02 14:37:12',0,1,NULL),(2,'320088550005673984',1,9999.00,9999.00,0.00,0.00,0,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 14:37:37','2026-06-02 14:37:37',0,1,NULL),(3,'320089706090074112',1,9999.00,9999.00,0.00,0.00,0,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 14:42:13','2026-06-02 14:42:13',0,1,NULL),(4,'320090361135501312',1,1.00,1.00,0.00,0.00,2,NULL,NULL,'2026-06-02 14:59:18',NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 14:44:49','2026-06-02 14:55:20',0,2,NULL),(5,'320094210227310592',1,1.00,1.00,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 15:00:07','2026-06-02 15:00:07',0,2,NULL),(6,'320095281087647744',1,1.00,1.00,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 15:04:22','2026-06-02 15:04:22',0,2,NULL),(7,'320095536059387904',1,1.00,1.00,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 15:05:23','2026-06-02 15:05:23',0,2,NULL),(8,'320096016869232640',1,1.00,1.00,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 15:07:18','2026-06-02 15:07:18',0,2,NULL),(9,'320098038246936576',1,1.00,1.00,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 15:15:19','2026-06-02 15:15:19',0,2,NULL),(10,'320100679869927424',1,1.00,1.00,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 15:25:49','2026-06-02 15:25:49',0,2,NULL),(11,'320101739070099456',1,0.01,0.01,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 15:30:02','2026-06-02 15:30:02',0,2,NULL),(12,'320105098825043968',1,0.01,0.01,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 15:43:23','2026-06-02 15:43:23',0,2,NULL),(13,'320107840406687744',1,0.01,0.01,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 15:54:16','2026-06-02 15:54:16',0,2,NULL),(14,'320110767473037312',1,0.01,0.01,0.00,0.00,2,NULL,'2026-06-02 16:06:51','2026-06-02 16:16:30',NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 16:05:54','2026-06-02 16:05:54',0,3,NULL),(15,'320112108937285632',1,0.01,0.01,0.00,0.00,4,NULL,'2026-06-02 16:11:49','2026-06-02 16:16:28',NULL,'2026-06-02 16:16:46','admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 16:11:14','2026-06-02 16:11:14',0,4,NULL),(16,'320112414458777600',1,0.01,0.01,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 16:12:27','2026-06-02 16:12:27',0,2,NULL),(17,'320112524349542400',1,0.01,0.01,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 16:12:53','2026-06-02 16:12:53',0,2,NULL),(18,'320112707145699328',1,9999.00,9999.00,0.00,0.00,2,NULL,'2026-06-02 16:18:05','2026-06-04 15:11:31',NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 16:13:37','2026-06-02 16:13:37',0,3,NULL),(19,'320122935912304640',1,0.01,0.01,0.00,0.00,4,NULL,'2026-06-02 16:54:40','2026-06-04 15:11:29',NULL,'2026-06-14 16:41:06','admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 16:54:16','2026-06-02 16:54:16',0,4,NULL),(20,'320125270831009792',1,0.01,0.01,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 17:03:32','2026-06-02 17:03:32',0,2,NULL),(21,'320126794592292864',1,5299.00,5299.00,0.00,0.00,4,NULL,'2026-06-02 17:10:02','2026-06-04 15:11:27',NULL,'2026-06-14 16:41:05','admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 17:09:35','2026-06-02 17:09:35',0,4,NULL),(22,'320131414349713408',1,0.01,0.01,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 17:27:57','2026-06-02 17:27:57',0,2,NULL),(23,'320131482578456576',1,14999.00,14999.00,0.00,0.00,7,NULL,'2026-06-02 17:30:31',NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 17:28:13','2026-06-02 17:28:13',0,5,NULL),(24,'320172933916004352',1,19998.00,19998.00,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-02 20:12:56','2026-06-02 20:12:56',0,2,NULL),(25,'320819717453516800',1,9999.00,9999.00,0.00,0.00,4,NULL,'2026-06-04 15:10:43','2026-06-04 15:11:22',NULL,'2026-06-12 11:45:14','admin','13078907890','北京北京海定东楼001',NULL,'2026-06-04 15:03:01','2026-06-04 15:03:01',0,4,NULL),(26,'323668078208618496',1,698.00,698.00,0.00,0.00,4,NULL,'2026-06-12 11:48:03','2026-06-12 11:48:37',NULL,'2026-06-14 16:41:03','admin','13078907890','北京北京海定东楼001',NULL,'2026-06-12 11:41:23','2026-06-12 11:41:23',0,4,NULL),(27,'323670672356282368',1,698.00,698.00,0.00,0.00,4,NULL,'2026-06-12 11:56:51','2026-06-12 11:57:34',NULL,'2026-06-14 16:41:01','admin','13078907890','北京北京海定东楼001',NULL,'2026-06-12 11:51:42','2026-06-12 11:51:42',0,4,NULL),(28,'324446900508037120',1,99.00,99.00,0.00,0.00,4,NULL,'2026-06-14 15:25:48','2026-06-15 10:01:17',NULL,'2026-06-15 10:03:13','admin','13078907890','北京北京海定东楼001',NULL,'2026-06-14 15:16:09','2026-06-14 15:16:09',0,4,NULL),(29,'324461451827875840',1,698.00,698.00,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-14 16:13:58','2026-06-14 16:13:58',0,2,NULL),(30,'324461569553600512',1,698.00,698.00,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-14 16:14:26','2026-06-14 16:14:26',0,2,NULL),(31,'324467866437029888',1,698.00,698.00,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001','','2026-06-14 16:39:28','2026-06-14 16:39:28',0,2,NULL),(32,'324472569744986112',1,698.00,698.00,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001','','2026-06-14 16:58:09','2026-06-14 16:58:09',0,2,NULL),(33,'324475914350104576',1,1.00,1.00,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001','','2026-06-14 17:11:26','2026-06-14 17:11:26',0,2,NULL),(34,'324729414334156800',1,698.00,698.00,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-15 09:58:46','2026-06-15 09:58:46',0,2,NULL),(35,'324729785509089280',1,698.00,698.00,0.00,0.00,4,NULL,'2026-06-15 10:00:48','2026-06-15 10:01:15',NULL,'2026-06-15 10:03:11','admin','13078907890','北京北京海定东楼001',NULL,'2026-06-15 10:00:14','2026-06-15 10:00:14',0,4,NULL),(36,'324730242851803136',1,698.00,698.00,0.00,0.00,2,NULL,'2026-06-15 10:02:32','2026-06-15 10:03:47',NULL,NULL,'admin','13078907890','北京北京海定东楼001','','2026-06-15 10:02:03','2026-06-15 10:02:03',0,3,NULL),(37,'324730951525601280',1,9999.00,9999.00,0.00,0.00,2,NULL,'2026-06-15 10:05:41','2026-06-24 20:34:25',NULL,NULL,'admin','13078907890','北京北京海定东楼001','','2026-06-15 10:04:52','2026-06-15 10:04:52',0,3,NULL),(38,'326981838755205120',1,2999.00,2999.00,0.00,0.00,5,NULL,NULL,NULL,NULL,NULL,'admin','13078907890','北京北京海定东楼001',NULL,'2026-06-21 15:09:05','2026-06-21 15:09:05',0,2,NULL),(39,'328177276208418816',12,9999.00,9999.00,0.00,0.00,4,NULL,'2026-06-24 22:22:52','2026-06-24 22:23:40',NULL,'2026-06-24 22:34:28','zhangsan','13012124545','希望省希望市老大县101','送号','2026-06-24 22:19:20','2026-06-24 22:19:20',0,4,NULL),(40,'328181295572193280',12,9999.00,9999.00,0.00,0.00,4,NULL,'2026-06-24 22:37:19','2026-06-24 22:37:41',NULL,'2026-06-24 22:37:51','zhangsan','13012124545','希望省希望市老大县101',NULL,'2026-06-24 22:35:18','2026-06-24 22:35:18',0,4,NULL),(41,'328182714689785856',12,5299.00,5299.00,0.00,0.00,4,NULL,'2026-06-24 22:42:13','2026-06-24 22:42:26',NULL,'2026-06-24 22:42:43','zhangsan','13012124545','希望省希望市老大县101',NULL,'2026-06-24 22:40:57','2026-06-24 22:40:57',0,4,NULL),(42,'328185907985387520',13,14999.00,14999.00,0.00,0.00,1,NULL,'2026-06-24 22:56:59',NULL,NULL,NULL,'lisi','13456789314','北京北京海定区101',NULL,'2026-06-24 22:53:38','2026-06-24 22:53:38',0,2,NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` VALUES (1,4,'320090361135501312',611,8,'买就对了',NULL,'牛',1.00,1,1.00,'2026-06-02 14:44:49'),(2,5,'320094210227310592',611,8,'买就对了',NULL,'牛',1.00,1,1.00,'2026-06-02 15:00:06'),(3,6,'320095281087647744',611,8,'买就对了',NULL,'牛',1.00,1,1.00,'2026-06-02 15:04:22'),(4,7,'320095536059387904',611,8,'买就对了',NULL,'牛',1.00,1,1.00,'2026-06-02 15:05:22'),(5,8,'320096016869232640',611,8,'买就对了',NULL,'牛',1.00,1,1.00,'2026-06-02 15:07:17'),(6,9,'320098038246936576',611,8,'买就对了',NULL,'牛',1.00,1,1.00,'2026-06-02 15:15:19'),(7,10,'320100679869927424',611,8,'买就对了',NULL,'牛',1.00,1,1.00,'2026-06-02 15:25:49'),(8,11,'320101739070099456',612,9,'1分',NULL,NULL,0.01,1,0.01,'2026-06-02 15:30:01'),(9,12,'320105098825043968',612,9,'1分',NULL,NULL,0.01,1,0.01,'2026-06-02 15:43:22'),(10,13,'320107840406687744',612,9,'1分',NULL,NULL,0.01,1,0.01,'2026-06-02 15:54:16'),(11,14,'320110767473037312',612,9,'1分',NULL,NULL,0.01,1,0.01,'2026-06-02 16:05:54'),(12,15,'320112108937285632',612,9,'1分',NULL,NULL,0.01,1,0.01,'2026-06-02 16:11:14'),(13,16,'320112414458777600',612,9,'1分',NULL,NULL,0.01,1,0.01,'2026-06-02 16:12:27'),(14,17,'320112524349542400',612,9,'1分',NULL,NULL,0.01,1,0.01,'2026-06-02 16:12:53'),(15,18,'320112707145699328',607,2,'iPhone 16 Pro Max 256GB 沙漠钛金属','https://img.aheadshop.com/sku/iphone16pm-256-titan.jpg','{\"颜色\":\"沙漠钛金属\",\"存储\":\"256GB\",\"内存\":\"8GB\"}',9999.00,1,9999.00,'2026-06-02 16:13:36'),(16,19,'320122935912304640',612,9,'1分',NULL,NULL,0.01,1,0.01,'2026-06-02 16:54:15'),(17,20,'320125270831009792',612,9,'1分',NULL,NULL,0.01,1,0.01,'2026-06-02 17:03:32'),(18,21,'320126794592292864',301,3,'Xiaomi 15 Ultra 256GB 黑色','https://img.aheadshop.com/sku/mi15u-256-black.jpg','{\"颜色\":\"黑色\",\"存储\":\"256GB\",\"内存\":\"16GB\"}',5299.00,1,5299.00,'2026-06-02 17:09:35'),(19,22,'320131414349713408',612,9,'1分',NULL,NULL,0.01,1,0.01,'2026-06-02 17:27:57'),(20,23,'320131482578456576',401,4,'MacBook Pro 14 M3 Pro 18GB+512GB 银色','https://img.aheadshop.com/sku/mbp14-m3pro-silver.jpg','{\"颜色\":\"银色\",\"处理器\":\"M3 Pro\",\"内存\":\"18GB\",\"硬盘\":\"512GB\"}',14999.00,1,14999.00,'2026-06-02 17:28:13'),(21,24,'320172933916004352',614,2,'iPhone 16 Pro Max 256GB 沙漠钛金属','https://img.aheadshop.com/sku/iphone16pm-256-titan.jpg','{\"颜色\":\"沙漠钛金属\",\"存储\":\"256GB\",\"内存\":\"8GB\"}',9999.00,2,19998.00,'2026-06-02 20:12:56'),(22,25,'320819717453516800',621,5,'ThinkPad X1 Carbon i7 16GB+512GB 黑色','https://img.aheadshop.com/sku/x1c-i7-black.jpg','{\"颜色\":\"黑色\",\"处理器\":\"i7\",\"内存\":\"16GB\",\"硬盘\":\"512GB\"}',9999.00,1,9999.00,'2026-06-04 15:03:01'),(23,26,'323668078208618496',628,14,'电竞显示器',NULL,'24寸',698.00,1,698.00,'2026-06-12 11:41:23'),(24,27,'323670672356282368',628,14,'电竞显示器',NULL,'24寸',698.00,1,698.00,'2026-06-12 11:51:41'),(25,28,'324446900508037120',625,11,'40码',NULL,'白鞋',99.00,1,99.00,'2026-06-14 15:16:09'),(26,29,'324461451827875840',628,14,'电竞显示器',NULL,'24寸',698.00,1,698.00,'2026-06-14 16:13:58'),(27,30,'324461569553600512',628,14,'电竞显示器',NULL,'24寸',698.00,1,698.00,'2026-06-14 16:14:26'),(28,31,'324467866437029888',628,14,'电竞显示器',NULL,'24寸',698.00,1,698.00,'2026-06-14 16:39:27'),(29,32,'324472569744986112',628,14,'电竞显示器',NULL,'24寸',698.00,1,698.00,'2026-06-14 16:58:09'),(30,33,'324475914350104576',611,8,'买就对了',NULL,'牛',1.00,1,1.00,'2026-06-14 17:11:26'),(31,34,'324729414334156800',628,14,'电竞显示器',NULL,'24寸',698.00,1,698.00,'2026-06-15 09:58:45'),(32,35,'324729785509089280',628,14,'电竞显示器',NULL,'24寸',698.00,1,698.00,'2026-06-15 10:00:14'),(33,36,'324730242851803136',628,14,'电竞显示器',NULL,'24寸',698.00,1,698.00,'2026-06-15 10:02:03'),(34,37,'324730951525601280',614,2,'iPhone 16 Pro Max 256GB 沙漠钛金属','https://img.aheadshop.com/sku/iphone16pm-256-titan.jpg','{\"颜色\":\"沙漠钛金属\",\"存储\":\"256GB\",\"内存\":\"8GB\"}',9999.00,1,9999.00,'2026-06-15 10:04:52'),(35,38,'326981838755205120',627,13,'一级能耗',NULL,'白',2999.00,1,2999.00,'2026-06-21 15:09:05'),(36,39,'328177276208418816',639,5,'ThinkPad X1 Carbon i7 16GB+512GB 黑色','/upload/product/4190f1bef28c4c0ab4ba7ed5a5a93c90.png','{\"颜色\":\"黑色\",\"处理器\":\"i7\",\"内存\":\"16GB\",\"硬盘\":\"512GB\"}',9999.00,1,9999.00,'2026-06-24 22:19:19'),(37,40,'328181295572193280',647,2,'iPhone 16 Pro Max 256GB 沙漠钛金属','/upload/product/1b05d1c6d11f490c84be04e2a5c9e876.jpg','{\"颜色\":\"沙漠钛金属\",\"存储\":\"256GB\",\"内存\":\"8GB\"}',9999.00,1,9999.00,'2026-06-24 22:35:18'),(38,41,'328182714689785856',643,3,'Xiaomi 15 Ultra 256GB 黑色','/upload/product/311c0c946d17475e967c0d8f133b22f2.jpg','{\"颜色\":\"黑色\",\"存储\":\"256GB\",\"内存\":\"16GB\"}',5299.00,1,5299.00,'2026-06-24 22:40:56'),(39,42,'328185907985387520',619,4,'MacBook Pro 14 M3 Pro 18GB+512GB 银色','/upload/product/36194fe655ad467889694060339c2c0d.png','{\"颜色\":\"银色\",\"处理器\":\"M3 Pro\",\"内存\":\"18GB\",\"硬盘\":\"512GB\"}',14999.00,1,14999.00,'2026-06-24 22:53:37');
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
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_log`
--

LOCK TABLES `order_log` WRITE;
/*!40000 ALTER TABLE `order_log` DISABLE KEYS */;
INSERT INTO `order_log` VALUES (1,'320090361135501312','system',NULL,0,'创建订单','2026-06-02 14:44:49'),(2,'320090361135501312','admin',1,2,'管理员发货','2026-06-02 14:59:17'),(3,'320094210227310592','system',NULL,0,'创建订单','2026-06-02 15:00:06'),(4,'320095281087647744','system',NULL,0,'创建订单','2026-06-02 15:04:22'),(5,'320095536059387904','system',NULL,0,'创建订单','2026-06-02 15:05:22'),(6,'320096016869232640','system',NULL,0,'创建订单','2026-06-02 15:07:17'),(7,'320098038246936576','system',NULL,0,'创建订单','2026-06-02 15:15:19'),(8,'320100679869927424','system',NULL,0,'创建订单','2026-06-02 15:25:49'),(9,'320100679869927424','1',0,5,'用户取消订单','2026-06-02 15:27:09'),(10,'320098038246936576','1',0,5,'用户取消订单','2026-06-02 15:28:17'),(11,'320101739070099456','system',NULL,0,'创建订单','2026-06-02 15:30:01'),(12,'320105098825043968','system',NULL,0,'创建订单','2026-06-02 15:43:22'),(13,'320107840406687744','system',NULL,0,'创建订单','2026-06-02 15:54:16'),(14,'320110767473037312','system',NULL,0,'创建订单','2026-06-02 16:05:54'),(15,'320110767473037312','system',0,1,'支付成功','2026-06-02 16:06:51'),(16,'320112108937285632','system',NULL,0,'创建订单','2026-06-02 16:11:14'),(17,'320112108937285632','system',0,1,'支付成功','2026-06-02 16:11:48'),(18,'320112414458777600','system',NULL,0,'创建订单','2026-06-02 16:12:27'),(19,'320112524349542400','system',NULL,0,'创建订单','2026-06-02 16:12:53'),(20,'320112707145699328','system',NULL,0,'创建订单','2026-06-02 16:13:36'),(21,'320112108937285632','admin',1,2,'管理员发货','2026-06-02 16:16:28'),(22,'320110767473037312','admin',1,2,'管理员发货','2026-06-02 16:16:30'),(23,'320112108937285632','1',2,4,'用户确认收货','2026-06-02 16:16:46'),(24,'320112707145699328','system',0,1,'支付成功','2026-06-02 16:18:04'),(25,'320122935912304640','system',NULL,0,'创建订单','2026-06-02 16:54:15'),(26,'320122935912304640','system',0,1,'支付成功','2026-06-02 16:54:40'),(27,'320125270831009792','system',NULL,0,'创建订单','2026-06-02 17:03:32'),(28,'320126794592292864','system',NULL,0,'创建订单','2026-06-02 17:09:35'),(29,'320126794592292864','system',0,1,'支付成功','2026-06-02 17:10:01'),(30,'320131414349713408','system',NULL,0,'创建订单','2026-06-02 17:27:57'),(31,'320131482578456576','system',NULL,0,'创建订单','2026-06-02 17:28:13'),(32,'320131482578456576','system',0,1,'支付成功','2026-06-02 17:30:30'),(33,'320172933916004352','system',NULL,0,'创建订单','2026-06-02 20:12:56'),(34,'320819717453516800','system',NULL,0,'创建订单','2026-06-04 15:03:01'),(35,'320819717453516800','system',0,1,'支付成功','2026-06-04 15:10:43'),(36,'320819717453516800','admin',1,2,'管理员发货','2026-06-04 15:11:22'),(37,'320126794592292864','admin',1,2,'管理员发货','2026-06-04 15:11:26'),(38,'320122935912304640','admin',1,2,'管理员发货','2026-06-04 15:11:28'),(39,'320112707145699328','admin',1,2,'管理员发货','2026-06-04 15:11:30'),(40,'323668078208618496','system',NULL,0,'创建订单','2026-06-12 11:41:23'),(41,'320819717453516800','1',2,4,'用户确认收货','2026-06-12 11:45:14'),(42,'323668078208618496','system',0,1,'支付成功','2026-06-12 11:48:03'),(43,'323668078208618496','admin',1,2,'管理员发货','2026-06-12 11:48:37'),(44,'323670672356282368','system',NULL,0,'创建订单','2026-06-12 11:51:41'),(45,'323670672356282368','system',0,1,'支付成功','2026-06-12 11:56:51'),(46,'323670672356282368','admin',1,2,'管理员发货','2026-06-12 11:57:34'),(47,'324446900508037120','system',NULL,0,'创建订单','2026-06-14 15:16:09'),(48,'324446900508037120','system',0,1,'支付成功','2026-06-14 15:25:48'),(49,'324461451827875840','system',NULL,0,'创建订单','2026-06-14 16:13:58'),(50,'324461569553600512','system',NULL,0,'创建订单','2026-06-14 16:14:26'),(51,'324467866437029888','system',NULL,0,'创建订单','2026-06-14 16:39:27'),(52,'323670672356282368','1',2,4,'用户确认收货','2026-06-14 16:41:01'),(53,'323668078208618496','1',2,4,'用户确认收货','2026-06-14 16:41:02'),(54,'320126794592292864','1',2,4,'用户确认收货','2026-06-14 16:41:04'),(55,'320122935912304640','1',2,4,'用户确认收货','2026-06-14 16:41:06'),(56,'324472569744986112','system',NULL,0,'创建订单','2026-06-14 16:58:09'),(57,'324475914350104576','system',NULL,0,'创建订单','2026-06-14 17:11:26'),(58,'324729414334156800','system',NULL,0,'创建订单','2026-06-15 09:58:45'),(59,'324729785509089280','system',NULL,0,'创建订单','2026-06-15 10:00:14'),(60,'324729785509089280','system',0,1,'支付成功','2026-06-15 10:00:47'),(61,'324729785509089280','admin',1,2,'管理员发货','2026-06-15 10:01:14'),(62,'324446900508037120','admin',1,2,'管理员发货','2026-06-15 10:01:17'),(63,'324730242851803136','system',NULL,0,'创建订单','2026-06-15 10:02:03'),(64,'324730242851803136','system',0,1,'支付成功','2026-06-15 10:02:32'),(65,'324729785509089280','1',2,4,'用户确认收货','2026-06-15 10:03:11'),(66,'324446900508037120','1',2,4,'用户确认收货','2026-06-15 10:03:12'),(67,'324730242851803136','admin',1,2,'管理员发货','2026-06-15 10:03:46'),(68,'324730951525601280','system',NULL,0,'创建订单','2026-06-15 10:04:52'),(69,'324730951525601280','system',0,1,'支付成功','2026-06-15 10:05:41'),(70,'326981838755205120','system',NULL,0,'创建订单','2026-06-21 15:09:05'),(71,'324730951525601280','admin',1,2,'管理员发货','2026-06-24 20:34:24'),(72,'328177276208418816','system',NULL,0,'创建订单','2026-06-24 22:19:19'),(73,'328177276208418816','system',0,1,'支付成功','2026-06-24 22:22:51'),(74,'328177276208418816','admin',1,2,'管理员发货','2026-06-24 22:23:40'),(75,'328177276208418816','12',2,4,'用户确认收货','2026-06-24 22:34:27'),(76,'328181295572193280','system',NULL,0,'创建订单','2026-06-24 22:35:18'),(77,'328181295572193280','system',0,1,'支付成功','2026-06-24 22:37:19'),(78,'328181295572193280','admin',1,2,'管理员发货','2026-06-24 22:37:40'),(79,'328181295572193280','12',2,4,'用户确认收货','2026-06-24 22:37:50'),(80,'328182714689785856','system',NULL,0,'创建订单','2026-06-24 22:40:56'),(81,'328182714689785856','system',0,1,'支付成功','2026-06-24 22:42:12'),(82,'328182714689785856','admin',1,2,'管理员发货','2026-06-24 22:42:26'),(83,'328182714689785856','12',2,4,'用户确认收货','2026-06-24 22:42:42'),(84,'328185907985387520','system',NULL,0,'创建订单','2026-06-24 22:53:37'),(85,'328185907985387520','system',0,1,'支付成功','2026-06-24 22:56:58');
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
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pay_record`
--

LOCK TABLES `pay_record` WRITE;
/*!40000 ALTER TABLE `pay_record` DISABLE KEYS */;
INSERT INTO `pay_record` VALUES (1,'320100693140705280','320100679869927424',1,1.00,1,NULL,0,NULL,NULL,NULL,'2026-06-02 15:25:52','2026-06-02 15:25:52'),(2,'320101029876207616','320098038246936576',1,1.00,1,NULL,0,NULL,NULL,NULL,'2026-06-02 15:27:12','2026-06-02 15:27:12'),(3,'320101753838243840','320101739070099456',1,0.01,1,NULL,0,NULL,NULL,NULL,'2026-06-02 15:30:05','2026-06-02 15:30:05'),(4,'320105110531346432','320105098825043968',1,0.01,1,NULL,0,NULL,NULL,NULL,'2026-06-02 15:43:25','2026-06-02 15:43:25'),(5,'320107847725748224','320107840406687744',1,0.01,1,NULL,0,NULL,NULL,NULL,'2026-06-02 15:54:18','2026-06-02 15:54:18'),(6,'320110775760982016','320110767473037312',1,0.01,1,'2026060222001472230509656905',1,'2026-06-02 16:06:51','2026-06-02 16:06:51','{gmt_create=2026-06-02 16:06:25, charset=utf-8, gmt_payment=2026-06-02 16:06:50, notify_time=2026-06-02 16:06:51, subject=AheadShop订单-320110767473037312, buyer_id=2088722101872239, invoice_amount=0.01, version=1.0, notify_id=2026060201222160651172230509586367, fund_bill_list=[{\"amount\":\"0.01\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=320110775760982016, total_amount=0.01, trade_status=TRADE_SUCCESS, trade_no=2026060222001472230509656905, auth_app_id=9021000164627929, receipt_amount=0.01, point_amount=0.00, buyer_pay_amount=0.01, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-02 16:05:56','2026-06-02 16:05:56'),(7,'320112117866958848','320112108937285632',1,0.01,1,'2026060222001472230509661645',1,'2026-06-02 16:11:49','2026-06-02 16:11:49','{gmt_create=2026-06-02 16:11:30, charset=utf-8, gmt_payment=2026-06-02 16:11:48, notify_time=2026-06-02 16:11:49, subject=AheadShop订单-320112108937285632, buyer_id=2088722101872239, invoice_amount=0.01, version=1.0, notify_id=2026060201222161148172230509582061, fund_bill_list=[{\"amount\":\"0.01\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=320112117866958848, total_amount=0.01, trade_status=TRADE_SUCCESS, trade_no=2026060222001472230509661645, auth_app_id=9021000164627929, receipt_amount=0.01, point_amount=0.00, buyer_pay_amount=0.01, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-02 16:11:16','2026-06-02 16:11:16'),(8,'320112421970776064','320112414458777600',1,0.01,1,NULL,0,NULL,NULL,NULL,'2026-06-02 16:12:28','2026-06-02 16:12:28'),(9,'320112531781849088','320112524349542400',1,0.01,1,NULL,0,NULL,NULL,NULL,'2026-06-02 16:12:54','2026-06-02 16:12:54'),(10,'320112716142481408','320112707145699328',1,9999.00,1,'2026060222001472230509663605',1,'2026-06-02 16:18:05','2026-06-02 16:18:05','{gmt_create=2026-06-02 16:17:49, charset=utf-8, gmt_payment=2026-06-02 16:18:04, notify_time=2026-06-02 16:18:05, subject=AheadShop订单-320112707145699328, buyer_id=2088722101872239, invoice_amount=9999.00, version=1.0, notify_id=2026060201222161805172230509586368, fund_bill_list=[{\"amount\":\"9999.00\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=320112716142481408, total_amount=9999.00, trade_status=TRADE_SUCCESS, trade_no=2026060222001472230509663605, auth_app_id=9021000164627929, receipt_amount=9999.00, point_amount=0.00, buyer_pay_amount=9999.00, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-02 16:13:38','2026-06-02 16:13:38'),(11,'320122944963612672','320122935912304640',1,0.01,1,'2026060222001472230509658619',1,'2026-06-02 16:54:40','2026-06-02 16:54:40','{gmt_create=2026-06-02 16:54:32, charset=utf-8, gmt_payment=2026-06-02 16:54:39, notify_time=2026-06-02 16:54:41, subject=AheadShop订单-320122935912304640, buyer_id=2088722101872239, invoice_amount=0.01, version=1.0, notify_id=2026060201222165440172230509582063, fund_bill_list=[{\"amount\":\"0.01\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=320122944963612672, total_amount=0.01, trade_status=TRADE_SUCCESS, trade_no=2026060222001472230509658619, auth_app_id=9021000164627929, receipt_amount=0.01, point_amount=0.00, buyer_pay_amount=0.01, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-02 16:54:17','2026-06-02 16:54:17'),(12,'320125298286923776','320125270831009792',1,0.01,1,NULL,0,NULL,NULL,NULL,'2026-06-02 17:03:38','2026-06-02 17:03:38'),(13,'320126815970660352','320126794592292864',1,5299.00,1,'2026060222001472230509663606',1,'2026-06-02 17:10:02','2026-06-02 17:10:02','{gmt_create=2026-06-02 17:09:54, charset=utf-8, gmt_payment=2026-06-02 17:10:01, notify_time=2026-06-02 17:10:02, subject=AheadShop订单-320126794592292864, buyer_id=2088722101872239, invoice_amount=5299.00, version=1.0, notify_id=2026060201222171001172230509587596, fund_bill_list=[{\"amount\":\"5299.00\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=320126815970660352, total_amount=5299.00, trade_status=TRADE_SUCCESS, trade_no=2026060222001472230509663606, auth_app_id=9021000164627929, receipt_amount=5299.00, point_amount=0.00, buyer_pay_amount=5299.00, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-02 17:09:40','2026-06-02 17:09:40'),(14,'320131425422675968','320131414349713408',1,0.01,1,NULL,0,NULL,NULL,NULL,'2026-06-02 17:27:59','2026-06-02 17:27:59'),(15,'320131491357134848','320131482578456576',1,14999.00,1,'2026060222001472230509658620',3,'2026-06-02 17:30:31','2026-06-02 17:30:31','{gmt_create=2026-06-02 17:30:23, charset=utf-8, gmt_payment=2026-06-02 17:30:30, notify_time=2026-06-02 17:30:31, subject=AheadShop订单-320131482578456576, buyer_id=2088722101872239, invoice_amount=14999.00, version=1.0, notify_id=2026060201222173030172230509587597, fund_bill_list=[{\"amount\":\"14999.00\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=320131491357134848, total_amount=14999.00, trade_status=TRADE_SUCCESS, trade_no=2026060222001472230509658620, auth_app_id=9021000164627929, receipt_amount=14999.00, point_amount=0.00, buyer_pay_amount=14999.00, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-02 17:28:15','2026-06-02 17:28:15'),(16,'320172947480383488','320172933916004352',1,19998.00,1,NULL,0,NULL,NULL,NULL,'2026-06-02 20:12:59','2026-06-02 20:12:59'),(17,'320819731957420032','320819717453516800',1,9999.00,1,'2026060422001472230509672476',1,'2026-06-04 15:10:43','2026-06-04 15:10:43','{gmt_create=2026-06-04 15:10:34, charset=utf-8, gmt_payment=2026-06-04 15:10:42, notify_time=2026-06-04 15:10:43, subject=AheadShop订单-320819717453516800, buyer_id=2088722101872239, invoice_amount=9999.00, version=1.0, notify_id=2026060401222151043172230509599387, fund_bill_list=[{\"amount\":\"9999.00\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=320819731957420032, total_amount=9999.00, trade_status=TRADE_SUCCESS, trade_no=2026060422001472230509672476, auth_app_id=9021000164627929, receipt_amount=9999.00, point_amount=0.00, buyer_pay_amount=9999.00, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-04 15:03:04','2026-06-04 15:03:04'),(18,'323668092049821696','323668078208618496',1,698.00,1,'2026061222001472230509711102',1,'2026-06-12 11:48:03','2026-06-12 11:48:03','{gmt_create=2026-06-12 11:47:49, charset=utf-8, gmt_payment=2026-06-12 11:48:01, notify_time=2026-06-12 11:48:02, subject=AheadShop订单-323668078208618496, buyer_id=2088722101872239, invoice_amount=698.00, version=1.0, notify_id=2026061201222114802172230509636869, fund_bill_list=[{\"amount\":\"698.00\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=323668092049821696, total_amount=698.00, trade_status=TRADE_SUCCESS, trade_no=2026061222001472230509711102, auth_app_id=9021000164627929, receipt_amount=698.00, point_amount=0.00, buyer_pay_amount=698.00, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-12 11:41:26','2026-06-12 11:41:26'),(19,'323670683815120896','323670672356282368',1,698.00,1,'2026061222001472230509715333',1,'2026-06-12 11:56:51','2026-06-12 11:56:51','{gmt_create=2026-06-12 11:56:30, charset=utf-8, gmt_payment=2026-06-12 11:56:49, notify_time=2026-06-12 11:56:51, subject=AheadShop订单-323670672356282368, buyer_id=2088722101872239, invoice_amount=698.00, version=1.0, notify_id=2026061201222115650172230509638785, fund_bill_list=[{\"amount\":\"698.00\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=323670683815120896, total_amount=698.00, trade_status=TRADE_SUCCESS, trade_no=2026061222001472230509715333, auth_app_id=9021000164627929, receipt_amount=698.00, point_amount=0.00, buyer_pay_amount=698.00, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-12 11:51:44','2026-06-12 11:51:44'),(20,'324446911144792064','324446900508037120',1,99.00,1,'2026061422001472230509725211',1,'2026-06-14 15:25:48','2026-06-14 15:25:48','{gmt_create=2026-06-14 15:25:37, charset=utf-8, gmt_payment=2026-06-14 15:25:46, notify_time=2026-06-14 15:25:48, subject=AheadShop订单-324446900508037120, buyer_id=2088722101872239, invoice_amount=99.00, version=1.0, notify_id=2026061401222152547172230509645556, fund_bill_list=[{\"amount\":\"99.00\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=324446911144792064, total_amount=99.00, trade_status=TRADE_SUCCESS, trade_no=2026061422001472230509725211, auth_app_id=9021000164627929, receipt_amount=99.00, point_amount=0.00, buyer_pay_amount=99.00, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-14 15:16:11','2026-06-14 15:16:11'),(21,'324461459377623040','324461451827875840',1,698.00,1,NULL,0,NULL,NULL,NULL,'2026-06-14 16:14:00','2026-06-14 16:14:00'),(22,'324461576042188800','324461569553600512',1,698.00,1,NULL,0,NULL,NULL,NULL,'2026-06-14 16:14:27','2026-06-14 16:14:27'),(23,'324467914986098688','324467866437029888',1,698.00,1,NULL,0,NULL,NULL,NULL,'2026-06-14 16:39:39','2026-06-14 16:39:39'),(24,'324472578846625792','324472569744986112',1,698.00,1,NULL,0,NULL,NULL,NULL,'2026-06-14 16:58:11','2026-06-14 16:58:11'),(25,'324475921623027712','324475914350104576',1,1.00,1,NULL,0,NULL,NULL,NULL,'2026-06-14 17:11:28','2026-06-14 17:11:28'),(26,'324729425306456064','324729414334156800',1,698.00,1,NULL,0,NULL,NULL,NULL,'2026-06-15 09:58:48','2026-06-15 09:58:48'),(27,'324729794199687168','324729785509089280',1,698.00,1,'2026061522001472230509729993',1,'2026-06-15 10:00:48','2026-06-15 10:00:48','{gmt_create=2026-06-15 10:00:37, charset=utf-8, gmt_payment=2026-06-15 10:00:46, notify_time=2026-06-15 10:00:47, subject=AheadShop订单-324729785509089280, buyer_id=2088722101872239, invoice_amount=698.00, version=1.0, notify_id=2026061501222100046172230509648674, fund_bill_list=[{\"amount\":\"698.00\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=324729794199687168, total_amount=698.00, trade_status=TRADE_SUCCESS, trade_no=2026061522001472230509729993, auth_app_id=9021000164627929, receipt_amount=698.00, point_amount=0.00, buyer_pay_amount=698.00, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-15 10:00:16','2026-06-15 10:00:16'),(28,'324730248837074944','324730242851803136',1,698.00,1,'2026061522001472230509726429',1,'2026-06-15 10:02:32','2026-06-15 10:02:32','{gmt_create=2026-06-15 10:02:17, charset=utf-8, gmt_payment=2026-06-15 10:02:30, notify_time=2026-06-15 10:02:31, subject=AheadShop订单-324730242851803136, buyer_id=2088722101872239, invoice_amount=698.00, version=1.0, notify_id=2026061501222100231172230509648675, fund_bill_list=[{\"amount\":\"698.00\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=324730248837074944, total_amount=698.00, trade_status=TRADE_SUCCESS, trade_no=2026061522001472230509726429, auth_app_id=9021000164627929, receipt_amount=698.00, point_amount=0.00, buyer_pay_amount=698.00, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-15 10:02:04','2026-06-15 10:02:04'),(29,'324730956818812928','324730951525601280',1,9999.00,1,'2026061522001472230509729994',1,'2026-06-15 10:05:41','2026-06-15 10:05:41','{gmt_create=2026-06-15 10:05:28, charset=utf-8, gmt_payment=2026-06-15 10:05:39, notify_time=2026-06-15 10:05:40, subject=AheadShop订单-324730951525601280, buyer_id=2088722101872239, invoice_amount=9999.00, version=1.0, notify_id=2026061501222100540172230509653103, fund_bill_list=[{\"amount\":\"9999.00\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=324730956818812928, total_amount=9999.00, trade_status=TRADE_SUCCESS, trade_no=2026061522001472230509729994, auth_app_id=9021000164627929, receipt_amount=9999.00, point_amount=0.00, buyer_pay_amount=9999.00, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-15 10:04:53','2026-06-15 10:04:53'),(30,'328177291639263232','328177276208418816',12,9999.00,1,'2026062422001472230509782978',1,'2026-06-24 22:22:52','2026-06-24 22:22:52','{gmt_create=2026-06-24 22:22:39, charset=utf-8, gmt_payment=2026-06-24 22:22:53, notify_time=2026-06-24 22:22:54, subject=AheadShop订单-328177276208418816, buyer_id=2088722101872239, invoice_amount=9999.00, version=1.0, notify_id=2026062401222222254172230509703641, fund_bill_list=[{\"amount\":\"9999.00\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=328177291639263232, total_amount=9999.00, trade_status=TRADE_SUCCESS, trade_no=2026062422001472230509782978, auth_app_id=9021000164627929, receipt_amount=9999.00, point_amount=0.00, buyer_pay_amount=9999.00, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-24 22:19:23','2026-06-24 22:19:23'),(31,'328181309350481920','328181295572193280',12,9999.00,1,'2026062422001472230509783998',1,'2026-06-24 22:37:19','2026-06-24 22:37:19','{gmt_create=2026-06-24 22:37:00, charset=utf-8, gmt_payment=2026-06-24 22:37:20, notify_time=2026-06-24 22:37:21, subject=AheadShop订单-328181295572193280, buyer_id=2088722101872239, invoice_amount=9999.00, version=1.0, notify_id=2026062401222223721172230509703642, fund_bill_list=[{\"amount\":\"9999.00\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=328181309350481920, total_amount=9999.00, trade_status=TRADE_SUCCESS, trade_no=2026062422001472230509783998, auth_app_id=9021000164627929, receipt_amount=9999.00, point_amount=0.00, buyer_pay_amount=9999.00, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-24 22:35:21','2026-06-24 22:35:21'),(32,'328182724848390144','328182714689785856',12,5299.00,1,'2026062422001472230509783999',1,'2026-06-24 22:42:13','2026-06-24 22:42:13','{gmt_create=2026-06-24 22:42:00, charset=utf-8, gmt_payment=2026-06-24 22:42:14, notify_time=2026-06-24 22:42:15, subject=AheadShop订单-328182714689785856, buyer_id=2088722101872239, invoice_amount=5299.00, version=1.0, notify_id=2026062401222224215172230509705520, fund_bill_list=[{\"amount\":\"5299.00\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=328182724848390144, total_amount=5299.00, trade_status=TRADE_SUCCESS, trade_no=2026062422001472230509783999, auth_app_id=9021000164627929, receipt_amount=5299.00, point_amount=0.00, buyer_pay_amount=5299.00, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-24 22:40:58','2026-06-24 22:40:58'),(33,'328185916776648704','328185907985387520',13,14999.00,1,'2026062422001472230509779808',1,'2026-06-24 22:56:59','2026-06-24 22:56:59','{gmt_create=2026-06-24 22:56:46, charset=utf-8, gmt_payment=2026-06-24 22:57:00, notify_time=2026-06-24 22:57:01, subject=AheadShop订单-328185907985387520, buyer_id=2088722101872239, invoice_amount=14999.00, version=1.0, notify_id=2026062401222225700172230509703643, fund_bill_list=[{\"amount\":\"14999.00\",\"fundChannel\":\"ALIPAYACCOUNT\"}], notify_type=trade_status_sync, out_trade_no=328185916776648704, total_amount=14999.00, trade_status=TRADE_SUCCESS, trade_no=2026062422001472230509779808, auth_app_id=9021000164627929, receipt_amount=14999.00, point_amount=0.00, buyer_pay_amount=14999.00, app_id=9021000164627929, seller_id=2088721101872221}','2026-06-24 22:53:39','2026-06-24 22:53:39');
/*!40000 ALTER TABLE `pay_record` ENABLE KEYS */;
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
INSERT INTO `background` VALUES (1,'/upload/background/bb4bde41814141e99c596435990c40e2.png',0,1,'2026-06-02 19:03:26','2026-06-02 19:03:26',0,9),(2,'/upload/background/1433dcb067824638978cdf79f9b7ef2f.png',0,1,'2026-06-02 19:04:07','2026-06-02 19:04:07',0,7),(3,'/upload/background/2b1d79182c684bd7bafacbbd14315423.png',0,1,'2026-06-02 19:04:09','2026-06-02 19:04:09',0,3),(4,'/upload/background/2affa0764a2f4a1bacaad18a9b53f5b9.png',0,1,'2026-06-02 19:04:11','2026-06-02 19:04:11',0,3),(5,'/upload/background/6f92ba0386f74620a4a39aaf8f77d9e1.png',0,1,'2026-06-02 19:04:13','2026-06-02 19:04:13',0,3),(6,'/upload/background/65aee517e4db42ec8be5bf5bb6f65222.png',0,1,'2026-06-02 19:04:15','2026-06-02 19:04:15',0,5),(7,'/upload/background/b9f1d0b1be0449ceac9fb3a544e57b79.png',0,1,'2026-06-02 19:04:20','2026-06-02 19:04:20',0,5);
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
INSERT INTO `brand` VALUES (1,'华为','/upload/brand/huawei.png','华为技术有限公司',1,1,'2026-05-29 21:30:21','2026-06-21 14:45:54',0,1),(2,'苹果','/upload/brand/apple.png','Apple Inc.',2,1,'2026-05-29 21:30:21','2026-06-21 14:45:54',0,1),(3,'小米','/upload/brand/xiaomi.png','小米科技有限公司',3,1,'2026-05-29 21:30:21','2026-06-21 14:45:54',0,1),(4,'联想','/upload/brand/lenovo.png','联想集团',4,1,'2026-05-29 21:30:21','2026-06-21 14:45:54',0,1),(5,'戴尔','/upload/brand/dell.png','Dell Technologies',5,1,'2026-05-29 21:30:21','2026-06-21 14:45:54',0,1),(6,'三星','/upload/brand/samsung.png','Samsung Electronics',6,1,'2026-05-29 21:30:21','2026-06-21 14:45:54',0,1),(7,'海尔','/upload/brand/haier.png','海尔集团',7,1,'2026-05-29 21:30:21','2026-06-21 14:45:54',0,1),(8,'格力','/upload/brand/gree.png','格力电器',8,1,'2026-05-29 21:30:21','2026-06-21 14:45:54',0,1),(9,'索尼','/upload/brand/sony.png','Sony Corporation',9,1,'2026-05-29 21:30:21','2026-06-21 14:45:54',0,1),(10,'Nike','/upload/brand/nike.png','耐克',10,1,'2026-05-29 21:30:21','2026-06-21 14:45:54',0,1);
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品评价表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
INSERT INTO `review` VALUES (1,1,14,628,'324729785509089280',5,'不错啊','',0,NULL,1,'2026-06-24 20:06:22','2026-06-24 20:06:22');
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
) ENGINE=InnoDB AUTO_INCREMENT=650 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='SKU 表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sku`
--

LOCK TABLES `sku` WRITE;
/*!40000 ALTER TABLE `sku` DISABLE KEYS */;
INSERT INTO `sku` VALUES (101,1,'Huawei Mate 70 Pro 128GB 曜石黑','{\"颜色\":\"曜石黑\",\"存储\":\"128GB\",\"内存\":\"12GB\"}',5999.00,6499.00,200,0,'/upload/product/08e7645d42204829b2119ae50889f2a8.png',1,'2026-05-29 21:30:21','2026-06-21 14:44:53',1,1),(102,1,'Huawei Mate 70 Pro 256GB 曜石黑','{\"颜色\":\"曜石黑\",\"存储\":\"256GB\",\"内存\":\"12GB\"}',6499.00,6999.00,150,0,'/upload/product/121c30d75b184b809698e35d7d7d6043.png',1,'2026-05-29 21:30:21','2026-06-21 14:44:53',1,1),(103,1,'Huawei Mate 70 Pro 512GB 星河银','{\"颜色\":\"星河银\",\"存储\":\"512GB\",\"内存\":\"12GB\"}',7499.00,7999.00,100,0,'/upload/product/16b28a3333454d48b9dd4f37ef4fefa1.jpg',1,'2026-05-29 21:30:21','2026-06-21 14:44:53',1,1),(201,2,'iPhone 16 Pro Max 256GB 沙漠钛金属','{\"颜色\":\"沙漠钛金属\",\"存储\":\"256GB\",\"内存\":\"8GB\"}',9999.00,9999.00,300,0,'/upload/product/1b05d1c6d11f490c84be04e2a5c9e876.jpg',1,'2026-05-29 21:30:21','2026-06-21 14:44:53',1,1),(202,2,'iPhone 16 Pro Max 512GB 原色钛金属','{\"颜色\":\"原色钛金属\",\"存储\":\"512GB\",\"内存\":\"8GB\"}',11999.00,11999.00,200,0,'/upload/product/21e977b358164c9e80e2f4832d54d46b.png',1,'2026-05-29 21:30:21','2026-06-21 14:44:53',1,1),(203,2,'iPhone 16 Pro Max 1TB 黑色钛金属','{\"颜色\":\"黑色钛金属\",\"存储\":\"1TB\",\"内存\":\"8GB\"}',13999.00,13999.00,80,0,'/upload/product/221119d7b313406084b545c2131a55d2.png',1,'2026-05-29 21:30:21','2026-06-21 14:44:53',1,1),(301,3,'Xiaomi 15 Ultra 256GB 黑色','{\"颜色\":\"黑色\",\"存储\":\"256GB\",\"内存\":\"16GB\"}',5299.00,5999.00,179,0,'/upload/product/311c0c946d17475e967c0d8f133b22f2.jpg',1,'2026-05-29 21:30:21','2026-06-21 14:44:53',1,1),(302,3,'Xiaomi 15 Ultra 512GB 白色','{\"颜色\":\"白色\",\"存储\":\"512GB\",\"内存\":\"16GB\"}',5999.00,6499.00,120,0,'/upload/product/341687b198d846a0a8e0a30656721840.png',1,'2026-05-29 21:30:21','2026-06-21 14:44:53',1,1),(401,4,'MacBook Pro 14 M3 Pro 18GB+512GB 银色','{\"颜色\":\"银色\",\"处理器\":\"M3 Pro\",\"内存\":\"18GB\",\"硬盘\":\"512GB\"}',14999.00,16999.00,59,0,'/upload/product/36194fe655ad467889694060339c2c0d.png',1,'2026-05-29 21:30:21','2026-06-21 14:44:53',1,1),(402,4,'MacBook Pro 14 M3 Pro 36GB+1TB 深空灰','{\"颜色\":\"深空灰\",\"处理器\":\"M3 Pro\",\"内存\":\"36GB\",\"硬盘\":\"1TB\"}',19999.00,21999.00,40,0,'/upload/product/3aef436e4f064d00ad662a8520c70a0d.png',1,'2026-05-29 21:30:21','2026-06-21 14:44:53',1,1),(501,5,'ThinkPad X1 Carbon i7 16GB+512GB 黑色','{\"颜色\":\"黑色\",\"处理器\":\"i7\",\"内存\":\"16GB\",\"硬盘\":\"512GB\"}',9999.00,11999.00,80,0,'/upload/product/4190f1bef28c4c0ab4ba7ed5a5a93c90.png',1,'2026-05-29 21:30:21','2026-06-21 14:44:53',1,1),(601,6,'Sony WH-1000XM5 黑色','{\"颜色\":\"黑色\"}',2399.00,2999.00,500,0,'/upload/product/52c0935c1b3441eaa9ea78cfe41acc73.png',1,'2026-05-29 21:30:21','2026-06-21 14:44:53',1,1),(602,6,'Sony WH-1000XM5 银色','{\"颜色\":\"银色\"}',2399.00,2999.00,390,0,'/upload/product/62cbbb54d21c42f6b6b81e6a789e1b76.png',1,'2026-05-29 21:30:21','2026-06-21 14:44:53',1,1),(603,1,'128',NULL,1.00,1.00,1,0,NULL,1,'2026-06-01 13:23:42','2026-06-01 13:38:23',1,1),(604,1,'128g',NULL,1.00,NULL,0,0,NULL,1,'2026-06-01 13:38:24','2026-06-01 13:57:06',1,1),(605,7,'鞋子',NULL,1.00,NULL,0,0,NULL,1,'2026-06-01 13:46:53','2026-06-01 13:47:49',1,1),(606,7,'鞋子',NULL,1.00,NULL,0,0,NULL,1,'2026-06-01 13:47:50','2026-06-01 13:47:50',0,1),(607,2,'iPhone 16 Pro Max 256GB 沙漠钛金属','{\"颜色\":\"沙漠钛金属\",\"存储\":\"256GB\",\"内存\":\"8GB\"}',9999.00,9999.00,296,3,'/upload/product/1b05d1c6d11f490c84be04e2a5c9e876.jpg',1,'2026-06-01 13:50:32','2026-06-21 14:58:42',1,1),(608,2,'iPhone 16 Pro Max 512GB 原色钛金属','{\"颜色\":\"原色钛金属\",\"存储\":\"512GB\",\"内存\":\"8GB\"}',11999.00,11999.00,200,0,'/upload/product/21e977b358164c9e80e2f4832d54d46b.png',1,'2026-06-01 13:50:32','2026-06-21 14:58:42',1,1),(609,2,'iPhone 16 Pro Max 1TB 黑色钛金属','{\"颜色\":\"黑色钛金属\",\"存储\":\"1TB\",\"内存\":\"8GB\"}',13999.00,13999.00,80,0,'/upload/product/221119d7b313406084b545c2131a55d2.png',1,'2026-06-01 13:50:32','2026-06-21 14:58:42',1,1),(610,1,'128g',NULL,1.00,NULL,0,0,NULL,1,'2026-06-01 13:57:07','2026-06-02 17:55:28',1,1),(611,8,'买就对了','牛',1.00,1.00,999,1,NULL,1,'2026-06-02 14:44:25','2026-06-15 09:56:01',0,1),(612,9,'1分',NULL,0.01,0.01,9997,0,NULL,1,'2026-06-02 15:29:07','2026-06-02 17:57:57',0,1),(613,1,'128g',NULL,1.00,NULL,0,0,NULL,1,'2026-06-02 17:55:28','2026-06-21 14:36:06',1,1),(614,2,'iPhone 16 Pro Max 256GB 沙漠钛金属','{\"颜色\":\"沙漠钛金属\",\"存储\":\"256GB\",\"内存\":\"8GB\"}',9999.00,9999.00,293,2,'/upload/product/1b05d1c6d11f490c84be04e2a5c9e876.jpg',1,'2026-06-02 17:56:00','2026-06-21 15:18:32',1,1),(615,2,'iPhone 16 Pro Max 512GB 原色钛金属','{\"颜色\":\"原色钛金属\",\"存储\":\"512GB\",\"内存\":\"8GB\"}',11999.00,11999.00,200,0,'/upload/product/21e977b358164c9e80e2f4832d54d46b.png',1,'2026-06-02 17:56:00','2026-06-21 15:18:32',1,1),(616,2,'iPhone 16 Pro Max 1TB 黑色钛金属','{\"颜色\":\"黑色钛金属\",\"存储\":\"1TB\",\"内存\":\"8GB\"}',13999.00,13999.00,80,0,'/upload/product/221119d7b313406084b545c2131a55d2.png',1,'2026-06-02 17:56:00','2026-06-21 15:18:32',1,1),(617,3,'Xiaomi 15 Ultra 256GB 黑色','{\"颜色\":\"黑色\",\"存储\":\"256GB\",\"内存\":\"16GB\"}',5299.00,5999.00,179,0,'/upload/product/311c0c946d17475e967c0d8f133b22f2.jpg',1,'2026-06-02 17:56:32','2026-06-21 15:18:46',1,1),(618,3,'Xiaomi 15 Ultra 512GB 白色','{\"颜色\":\"白色\",\"存储\":\"512GB\",\"内存\":\"16GB\"}',5999.00,6499.00,120,0,'/upload/product/341687b198d846a0a8e0a30656721840.png',1,'2026-06-02 17:56:32','2026-06-21 15:18:46',1,1),(619,4,'MacBook Pro 14 M3 Pro 18GB+512GB 银色','{\"颜色\":\"银色\",\"处理器\":\"M3 Pro\",\"内存\":\"18GB\",\"硬盘\":\"512GB\"}',14999.00,16999.00,58,0,'/upload/product/36194fe655ad467889694060339c2c0d.png',1,'2026-06-02 17:56:55','2026-06-24 22:56:58',0,1),(620,4,'MacBook Pro 14 M3 Pro 36GB+1TB 深空灰','{\"颜色\":\"深空灰\",\"处理器\":\"M3 Pro\",\"内存\":\"36GB\",\"硬盘\":\"1TB\"}',19999.00,21999.00,40,0,'/upload/product/3aef436e4f064d00ad662a8520c70a0d.png',1,'2026-06-02 17:56:55','2026-06-21 14:58:42',0,1),(621,5,'ThinkPad X1 Carbon i7 16GB+512GB 黑色','{\"颜色\":\"黑色\",\"处理器\":\"i7\",\"内存\":\"16GB\",\"硬盘\":\"512GB\"}',9999.00,11999.00,79,0,'/upload/product/4190f1bef28c4c0ab4ba7ed5a5a93c90.png',1,'2026-06-02 17:57:18','2026-06-21 15:18:20',1,1),(622,6,'Sony WH-1000XM5 黑色','{\"颜色\":\"黑色\"}',2399.00,2999.00,500,0,'/upload/product/52c0935c1b3441eaa9ea78cfe41acc73.png',1,'2026-06-02 17:57:31','2026-06-21 15:17:39',1,1),(623,6,'Sony WH-1000XM5 银色','{\"颜色\":\"银色\"}',2399.00,2999.00,390,0,'/upload/product/62cbbb54d21c42f6b6b81e6a789e1b76.png',1,'2026-06-02 17:57:31','2026-06-21 15:17:39',1,1),(624,10,'512 黑色','黑，白，红，黄',2199.00,2499.00,1000,0,NULL,1,'2026-06-02 18:13:14','2026-06-21 15:19:01',1,1),(625,11,'40码','白鞋',99.00,120.00,99,0,NULL,1,'2026-06-02 19:43:53','2026-06-21 15:19:43',1,1),(626,12,'法国品牌','棕色',9.99,999.00,100,0,NULL,1,'2026-06-02 19:46:25','2026-06-21 14:28:37',1,1),(627,13,'一级能耗','白',2999.00,3000.00,99,1,NULL,1,'2026-06-02 19:49:01','2026-06-21 15:17:24',1,1),(628,14,'电竞显示器','24寸',698.00,777.00,195,0,NULL,1,'2026-06-02 19:50:10','2026-06-21 14:43:20',1,1),(629,12,'法国品牌','棕色',9.99,999.00,100,0,NULL,1,'2026-06-21 14:28:38','2026-06-21 14:28:38',0,1),(630,1,'128g',NULL,1.00,NULL,1000,0,NULL,1,'2026-06-21 14:36:07','2026-06-21 14:36:07',0,1),(631,14,'电竞显示器','24寸',698.00,777.00,195,0,NULL,1,'2026-06-21 14:43:21','2026-06-21 15:12:33',1,1),(632,14,'电竞显示器','24寸',698.00,777.00,195,0,NULL,1,'2026-06-21 15:12:33','2026-06-21 15:17:12',1,1),(633,14,'电竞显示器','24寸',698.00,777.00,195,0,NULL,1,'2026-06-21 15:17:12','2026-06-21 15:17:12',0,1),(634,13,'一级能耗','白',2999.00,3000.00,99,0,NULL,1,'2026-06-21 15:17:25','2026-06-21 15:17:25',0,1),(635,6,'Sony WH-1000XM5 黑色','{\"颜色\":\"黑色\"}',2399.00,2999.00,500,0,'/upload/product/52c0935c1b3441eaa9ea78cfe41acc73.png',1,'2026-06-21 15:17:39','2026-06-21 15:17:52',1,1),(636,6,'Sony WH-1000XM5 银色','{\"颜色\":\"银色\"}',2399.00,2999.00,390,0,'/upload/product/62cbbb54d21c42f6b6b81e6a789e1b76.png',1,'2026-06-21 15:17:39','2026-06-21 15:17:52',1,1),(637,6,'Sony WH-1000XM5 黑色','{\"颜色\":\"黑色\"}',2399.00,2999.00,500,0,'/upload/product/52c0935c1b3441eaa9ea78cfe41acc73.png',1,'2026-06-21 15:17:53','2026-06-21 15:17:53',0,1),(638,6,'Sony WH-1000XM5 银色','{\"颜色\":\"银色\"}',2399.00,2999.00,390,0,'/upload/product/62cbbb54d21c42f6b6b81e6a789e1b76.png',1,'2026-06-21 15:17:53','2026-06-21 15:17:53',0,1),(639,5,'ThinkPad X1 Carbon i7 16GB+512GB 黑色','{\"颜色\":\"黑色\",\"处理器\":\"i7\",\"内存\":\"16GB\",\"硬盘\":\"512GB\"}',9999.00,11999.00,78,0,'/upload/product/4190f1bef28c4c0ab4ba7ed5a5a93c90.png',1,'2026-06-21 15:18:21','2026-06-24 22:22:51',0,1),(640,2,'iPhone 16 Pro Max 256GB 沙漠钛金属','{\"颜色\":\"沙漠钛金属\",\"存储\":\"256GB\",\"内存\":\"8GB\"}',9999.00,9999.00,293,0,'/upload/product/1b05d1c6d11f490c84be04e2a5c9e876.jpg',1,'2026-06-21 15:18:32','2026-06-21 15:50:34',1,1),(641,2,'iPhone 16 Pro Max 512GB 原色钛金属','{\"颜色\":\"原色钛金属\",\"存储\":\"512GB\",\"内存\":\"8GB\"}',11999.00,11999.00,200,0,'/upload/product/21e977b358164c9e80e2f4832d54d46b.png',1,'2026-06-21 15:18:32','2026-06-21 15:50:34',1,1),(642,2,'iPhone 16 Pro Max 1TB 黑色钛金属','{\"颜色\":\"黑色钛金属\",\"存储\":\"1TB\",\"内存\":\"8GB\"}',13999.00,13999.00,80,0,'/upload/product/221119d7b313406084b545c2131a55d2.png',1,'2026-06-21 15:18:32','2026-06-21 15:50:34',1,1),(643,3,'Xiaomi 15 Ultra 256GB 黑色','{\"颜色\":\"黑色\",\"存储\":\"256GB\",\"内存\":\"16GB\"}',5299.00,5999.00,178,0,'/upload/product/311c0c946d17475e967c0d8f133b22f2.jpg',1,'2026-06-21 15:18:47','2026-06-24 22:42:12',0,1),(644,3,'Xiaomi 15 Ultra 512GB 白色','{\"颜色\":\"白色\",\"存储\":\"512GB\",\"内存\":\"16GB\"}',5999.00,6499.00,120,0,'/upload/product/341687b198d846a0a8e0a30656721840.png',1,'2026-06-21 15:18:47','2026-06-21 15:18:47',0,1),(645,10,'512 黑色','黑，白，红，黄',2199.00,2499.00,1000,0,NULL,1,'2026-06-21 15:19:01','2026-06-21 15:19:01',0,1),(646,11,'40码','白鞋',99.00,120.00,99,0,NULL,1,'2026-06-21 15:19:43','2026-06-21 15:19:43',0,1),(647,2,'iPhone 16 Pro Max 256GB 沙漠钛金属','{\"颜色\":\"沙漠钛金属\",\"存储\":\"256GB\",\"内存\":\"8GB\"}',9999.00,9999.00,292,0,'/upload/product/1b05d1c6d11f490c84be04e2a5c9e876.jpg',1,'2026-06-21 15:50:35','2026-06-24 22:37:19',0,1),(648,2,'iPhone 16 Pro Max 512GB 原色钛金属','{\"颜色\":\"原色钛金属\",\"存储\":\"512GB\",\"内存\":\"8GB\"}',11999.00,11999.00,200,0,'/upload/product/21e977b358164c9e80e2f4832d54d46b.png',1,'2026-06-21 15:50:35','2026-06-21 15:50:35',0,1),(649,2,'iPhone 16 Pro Max 1TB 黑色钛金属','{\"颜色\":\"黑色钛金属\",\"存储\":\"1TB\",\"内存\":\"8GB\"}',13999.00,13999.00,80,0,'/upload/product/221119d7b313406084b545c2131a55d2.png',1,'2026-06-21 15:50:35','2026-06-21 15:50:35',0,1);
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
INSERT INTO `spu` VALUES (1,'Huawei Mate 70 Pro','旗舰芯片 · 超感知影像 · 鸿蒙系统',100,1,'/upload/product/08e7645d42204829b2119ae50889f2a8.png','/upload/product/3aef436e4f064d00ad662a8520c70a0d.png,/upload/product/8e3f2e95d1504873b4a2103a40928b90.png',NULL,1,580,'2026-05-29 21:30:21','2026-06-21 14:44:53',0,6),(2,'iPhone 16 Pro Max','A18 Pro 芯片 · 钛金属设计 · 4K 120fps',100,2,'/upload/product/0e811300f0c94e27b75f2985c03cd994.png','/upload/product/f30ceeb8c49b40abaee0b580991bb2b7.png,/upload/product/08e7645d42204829b2119ae50889f2a8.png',NULL,1,1200,'2026-05-29 21:30:21','2026-06-21 14:44:53',0,5),(3,'Xiaomi 15 Ultra','徕卡光学 · 骁龙 8 至尊版 · 5400mAh',100,3,'/upload/product/56db41bef3754878a271f10f89c90733.png','/upload/product/21e977b358164c9e80e2f4832d54d46b.png',NULL,1,320,'2026-05-29 21:30:21','2026-06-21 14:44:53',0,3),(4,'MacBook Pro 14 M3 Pro','M3 Pro 芯片 · Liquid Retina XDR · 18h续航',200,2,'/upload/product/36194fe655ad467889694060339c2c0d.png','/upload/product/ef13cb6ece9e48ed945ad3b86044af9c.png',NULL,1,210,'2026-05-29 21:30:21','2026-06-21 14:44:53',0,2),(5,'ThinkPad X1 Carbon','14英寸 · 超轻薄 · 商务旗舰',200,4,'/upload/product/12ec86f905944c1492e3337bdff63908.png','/upload/product/831e0f1794a445f488a8534eb49bf59e.png,/upload/product/bc4c03d39afd426db7856d8cc35da2cb.png',NULL,1,150,'2026-05-29 21:30:21','2026-06-21 14:44:53',0,3),(6,'Sony WH-1000XM5','行业领先降噪 · 30h续航 · LDAC',121,9,'/upload/product/b28dee7b1ade48ee9c6fece651b044ed.png','/upload/product/ab2115b6fb3a4008aad0ec7eadcb133b.png',NULL,1,890,'2026-05-29 21:30:21','2026-06-21 14:44:53',0,4),(7,'adidashi','三条杠',40,NULL,'/upload/product/88a87bf8972246ec8d339b99b0aec329.jpg','/upload/product/16b28a3333454d48b9dd4f37ef4fefa1.jpg',NULL,0,0,'2026-06-01 13:46:53','2026-06-01 13:46:53',0,4),(8,'遥遥领先先','领先',100,1,'/upload/product/eb26d9d8d7b843fbb0303e2d3606d5ed.png','/upload/product/c926bc1d9c1044f8b1566b1888e35d8b.jpg','遥遥领先',0,0,'2026-06-02 14:44:25','2026-06-02 14:44:25',0,3),(9,'0.01','0.01',100,1,'/upload/product/6741cdf4c14949ccbdda38e880656e03.jpg','/upload/product/9eb5fc1139aa43c19fef08b36ff403f4.jpg','yifen',0,0,'2026-06-02 15:29:07','2026-06-02 15:29:07',0,5),(10,'红魔','续航',101,3,'/upload/product/6fccdb8484a448928dbf748863cfd7d8.png','/upload/product/a0e3e1950955421798c4d8e5ee94ca8e.png,/upload/product/eba2a7540eba4ec88e935da664bc2d43.png',NULL,1,0,'2026-06-02 18:13:14','2026-06-02 18:13:14',0,3),(11,'鞋子',NULL,40,NULL,'/upload/product/417ff06590514cccb26b798d0fe2debf.png','/upload/product/226e66b25a87488fb6592a050e31493e.png',NULL,1,0,'2026-06-02 19:43:53','2026-06-02 19:43:53',0,3),(12,'名牌包包','好看的包包',321,NULL,'/upload/product/7e0979f6493544839320491e1af73d4d.png','/upload/product/4190f1bef28c4c0ab4ba7ed5a5a93c90.png,/upload/product/7a743c66912f4b4886f5bd667a0b894f.png','快来买',1,0,'2026-06-02 19:46:25','2026-06-02 19:46:25',0,3),(13,'超级大冰箱','冰箱',300,7,'/upload/product/2eb739569d67479db7d8ca40d243cfcc.png','/upload/product/36194fe655ad467889694060339c2c0d.png','好用',1,0,'2026-06-02 19:49:01','2026-06-02 19:49:01',0,3),(14,'显示器','显示器',220,5,'/upload/product/ee9c640328a2402f8e1fff2fad4bb616.png','/upload/product/a1654025adac498fbd084a79f32416ab.png,/upload/product/772be17e56984bf18f7d6f695bf29fd7.png','打游戏乱杀',1,0,'2026-06-02 19:50:10','2026-06-02 19:50:10',0,5);
/*!40000 ALTER TABLE `spu` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Table structure for table `points_log`
--

DROP TABLE IF EXISTS `points_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `points_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `points` int NOT NULL COMMENT '积分变动(正数获取,负数消耗)',
  `type` tinyint NOT NULL COMMENT '类型: 1=每日签到 2=连续奖励 3=补签消耗 4=兑换消耗 5=系统赠送',
  `description` varchar(100) NOT NULL DEFAULT '' COMMENT '变动说明',
  `biz_no` varchar(64) DEFAULT NULL COMMENT '业务编号(如补签日期)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='积分流水表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `points_log`
--

LOCK TABLES `points_log` WRITE;
/*!40000 ALTER TABLE `points_log` DISABLE KEYS */;
INSERT INTO `points_log` VALUES (1,1,10,1,'每日签到',NULL,'2026-06-21 18:12:03'),(2,3,10,1,'每日签到',NULL,'2026-06-21 18:12:27'),(3,5,10,1,'每日签到',NULL,'2026-06-21 18:13:00'),(4,1,10,1,'每日签到',NULL,'2026-06-24 09:46:55'),(5,1,10,1,'每日签到',NULL,'2026-06-24 11:31:32'),(6,1,10,1,'每日签到',NULL,'2026-06-24 20:03:21'),(7,3,10,1,'每日签到',NULL,'2026-06-24 20:10:51'),(8,7,10,1,'每日签到',NULL,'2026-06-24 20:27:53');
/*!40000 ALTER TABLE `points_log` ENABLE KEYS */;
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
  `invite_code` varchar(16) DEFAULT NULL COMMENT '个人邀请码',
  `referrer_id` bigint DEFAULT NULL COMMENT '推荐人ID（即邀请人的用户ID）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_invite_code` (`invite_code`),
  KEY `idx_email` (`email`),
  KEY `idx_referrer_id` (`referrer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','$2a$10$8t3jtaGATm.yqzp1S5l3VeCMwrNHbsABgyY1j/1OTusa22YKD7iei','超级管理员','13800000001','admin@aheadshop.com','http://localhost:8081/avatar/11d950c71bee4975800ba5ffc24ad947.jpg',1,1,'2026-05-29 21:28:49','2026-06-24 21:56:50',0,2,'A88888',1),(12,'zhangsan','$2a$10$u/Xl.ndp8KbyW6IatyJ7y.AAKefM6Ul26CdojLlRzuoslT8w/0A/y','zhangsan','13456784512',NULL,NULL,0,1,'2026-06-24 22:09:58','2026-06-24 22:09:58',0,1,'AJ6FJ2',1),(13,'lisi','$2a$10$OJ41Ql7VHnYihugRMqnnG.TDC8vQCEyEceu3Gbft5cDLuYECIhgG.','lisi','13456788765',NULL,NULL,0,1,'2026-06-24 22:52:27','2026-06-24 22:52:27',0,1,'8XJ5AR',1);
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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='收货地址表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_address`
--

LOCK TABLES `user_address` WRITE;
/*!40000 ALTER TABLE `user_address` DISABLE KEYS */;
INSERT INTO `user_address` VALUES (1,2,'张三','13800000002','广东省','深圳市','南山区','科技园南路100号A栋1001室',1,'2026-05-29 21:28:49','2026-05-29 21:28:49',0,1),(2,3,'张三','13812345678','广东省','深圳市','南山区','科技园南区虚拟大学园B栋203室',1,'2026-05-30 22:44:41','2026-05-30 22:44:41',0,1),(3,2,'牛爱花','18701234567','江苏省','南京市','鼓楼区','中山路321号紫峰大厦2007室',0,'2026-05-30 22:45:22','2026-05-30 22:45:22',0,2),(4,2,'王芳','18701234567','江苏省','南京市','鼓楼区','中山路321号紫峰大厦2007室',0,'2026-05-30 22:45:26','2026-05-30 22:45:26',0,1),(5,1,'admin','13078907890','北京','北京','海定','东楼001',1,'2026-06-02 14:34:11','2026-06-02 14:34:11',0,1),(6,12,'zhangsan','13012124545','希望省','希望市','老大县','101',1,'2026-06-24 22:19:09','2026-06-24 22:19:09',0,1),(7,13,'lisi','13456789314','北京','北京','海定区','101',1,'2026-06-24 22:53:34','2026-06-24 22:53:34',0,1);
/*!40000 ALTER TABLE `user_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_points`
--

DROP TABLE IF EXISTS `user_points`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_points` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_points` int NOT NULL DEFAULT '0' COMMENT '当前总积分',
  `month_points` int NOT NULL DEFAULT '0' COMMENT '本月获得积分',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户积分账户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_points`
--

LOCK TABLES `user_points` WRITE;
/*!40000 ALTER TABLE `user_points` DISABLE KEYS */;
INSERT INTO `user_points` VALUES (1,1,40,40,'2026-06-21 18:12:03','2026-06-21 18:12:03'),(2,3,20,20,'2026-06-21 18:12:27','2026-06-21 18:12:27'),(3,5,10,10,'2026-06-21 18:13:00','2026-06-21 18:13:00'),(4,7,10,10,'2026-06-24 20:27:53','2026-06-24 20:27:53');
/*!40000 ALTER TABLE `user_points` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1,1,1,1),(2,2,2,1),(3,3,2,1),(4,5,2,1),(5,6,2,1),(6,7,2,1),(7,8,2,1),(8,9,2,1),(9,10,2,1),(10,11,2,1),(11,12,2,1),(12,13,2,1);
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-25 10:34:45

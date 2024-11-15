-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: eproject2ttt
-- ------------------------------------------------------
-- Server version	8.0.36

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_unique` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
INSERT INTO `admin` VALUES (1,'admin','$2a$10$3wUX4YW5gOsIBCmZ.q61HOOJGcpQHR4mh18kprJJHH4ul5KDv4ufa','leminhduc1212001@gmail.com');
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attendance`
--

DROP TABLE IF EXISTS `attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance` (
  `id` int NOT NULL AUTO_INCREMENT,
  `staff_id` varchar(255) DEFAULT NULL,
  `check_in` datetime DEFAULT NULL,
  `check_out` datetime DEFAULT NULL,
  `status` enum('On Time','Late','Early Leave','No Shift','Explanation Required') DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `staff_id` (`staff_id`),
  CONSTRAINT `attendance_fk_staff_id` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`staff_id`)
) ENGINE=InnoDB AUTO_INCREMENT=176 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance`
--

LOCK TABLES `attendance` WRITE;
/*!40000 ALTER TABLE `attendance` DISABLE KEYS */;
INSERT INTO `attendance` VALUES (8,'60000001','2024-10-01 08:14:00','2024-10-01 16:33:00','Explanation Required'),(9,'60000002','2024-10-01 08:00:00','2024-10-01 16:13:00',NULL),(10,'60000003','2024-10-01 09:36:00','2024-10-01 18:03:00',NULL),(11,'60000004','2024-10-01 08:01:00','2024-10-01 16:01:00',NULL),(12,'60000005','2024-10-01 08:08:00','2024-10-01 16:11:00',NULL),(13,'60000006','2024-10-01 08:03:00','2024-10-01 16:21:00',NULL),(26,'60000001','2024-10-02 08:06:00','2024-10-02 16:13:00','Explanation Required'),(27,'60000002','2024-10-02 08:09:00','2024-10-02 16:46:00',NULL),(28,'60000003','2024-10-02 08:03:00','2024-10-02 16:29:00',NULL),(29,'60000004','2024-10-02 08:01:00','2024-10-02 16:10:00',NULL),(30,'60000005','2024-10-02 08:06:00','2024-10-02 16:22:00',NULL),(31,'60000006','2024-10-02 08:08:00','2024-10-02 16:43:00',NULL),(32,'60000001','2024-10-02 07:10:00','2024-10-02 15:10:00','Explanation Required'),(33,'60000002','2024-10-02 15:01:00','2024-10-02 23:01:00',NULL),(34,'60000003','2024-10-02 09:10:00','2024-10-02 18:10:00',NULL),(35,'60000004',NULL,NULL,NULL),(36,'60000005','2024-10-02 07:10:00','2024-10-02 15:10:00',NULL),(37,'60000006','2024-10-02 15:10:00','2024-10-02 23:10:00',NULL),(38,'60000001','2024-10-03 07:00:00','2024-10-03 15:00:00','On Time'),(39,'60000002','2024-10-03 15:00:00','2024-10-03 23:00:00',NULL),(40,'60000003','2024-10-03 09:00:00','2024-10-03 18:00:00',NULL),(41,'60000004',NULL,NULL,NULL),(42,'60000005','2024-10-03 06:00:00','2024-10-03 13:09:00',NULL),(43,'60000006','2024-10-03 15:27:00','2024-10-03 23:27:00',NULL),(44,'60000001','2024-10-03 07:00:00','2024-10-03 15:00:00','On Time'),(45,'60000002','2024-10-03 15:00:00','2024-10-03 23:00:00',NULL),(46,'60000003','2024-10-03 09:00:00','2024-10-03 18:00:00',NULL),(47,'60000004',NULL,NULL,NULL),(48,'60000005','2024-10-03 08:17:00','2024-10-03 16:17:00',NULL),(49,'60000006','2024-10-03 15:00:00','2024-10-03 23:00:00',NULL),(50,'60000001','2024-10-03 07:00:00','2024-10-03 15:00:00','On Time'),(51,'60000002','2024-10-03 15:21:00','2024-10-03 23:21:00',NULL),(52,'60000003','2024-10-03 09:00:00','2024-10-03 18:00:00',NULL),(53,'60000004',NULL,NULL,NULL),(54,'60000005','2024-10-03 08:06:00','2024-10-03 16:06:00',NULL),(55,'60000006','2024-10-03 15:00:00','2024-10-03 23:00:00',NULL),(145,'60000001','2024-09-01 07:15:00','2024-09-01 15:00:00','Late'),(146,'60000001','2024-09-02 07:00:00','2024-09-02 15:00:00','On Time'),(147,'60000001','2024-09-03 08:00:00','2024-09-03 14:30:00','Early Leave'),(148,'60000001','2024-09-04 09:05:00','2024-09-04 18:00:00','Late'),(149,'60000001','2024-09-05 00:00:00','2024-10-28 10:29:21','No Shift'),(150,'60000001','2024-09-06 08:45:00','2024-09-06 15:45:00','On Time'),(151,'60000001','2024-09-07 07:00:00','2024-09-07 15:00:00','On Time'),(152,'60000001','2024-09-08 09:30:00','2024-09-08 18:00:00','Late'),(153,'60000001','2024-09-09 07:00:00','2024-09-09 15:00:00','On Time'),(154,'60000001','2024-09-10 08:00:00','2024-09-10 15:00:00','Early Leave'),(155,'60000001','2024-09-11 09:00:00','2024-09-11 18:00:00','On Time'),(156,'60000001','2024-09-12 00:00:00','2024-10-28 10:29:21','No Shift'),(157,'60000001','2024-09-13 07:00:00','2024-09-13 15:00:00','On Time'),(158,'60000001','2024-09-14 08:30:00','2024-09-14 15:00:00','Late'),(159,'60000001','2024-09-15 07:00:00','2024-09-15 15:00:00','On Time'),(160,'60000001','2024-09-16 09:00:00','2024-09-16 17:00:00','On Time'),(161,'60000001','2024-09-17 07:00:00','2024-09-17 15:00:00','On Time'),(162,'60000001','2024-09-18 07:00:00','2024-09-18 14:30:00','Early Leave'),(163,'60000001','2024-09-19 07:00:00','2024-09-19 15:30:00','On Time'),(164,'60000001','2024-09-20 08:15:00','2024-09-20 15:00:00','Late'),(165,'60000001','2024-09-21 07:00:00','2024-09-21 15:00:00','On Time'),(166,'60000001','2024-09-22 00:00:00','2024-10-28 10:29:21','No Shift'),(167,'60000001','2024-09-23 07:00:00','2024-09-23 15:00:00','On Time'),(168,'60000001','2024-09-24 07:00:00','2024-09-24 15:00:00','On Time'),(169,'60000001','2024-09-25 07:00:00','2024-09-25 15:00:00','On Time'),(170,'60000001','2024-09-26 07:00:00','2024-09-26 14:00:00','Early Leave'),(171,'60000001','2024-09-27 09:00:00','2024-09-27 18:00:00','Late'),(172,'60000001','2024-09-28 07:00:00','2024-09-28 15:00:00','On Time'),(173,'60000001','2024-09-29 08:45:00','2024-09-29 15:00:00','Late'),(174,'60000001','2024-09-30 07:00:00','2024-09-30 15:00:00','On Time'),(175,'60000001','2024-10-28 10:29:20','2024-10-28 10:29:21',NULL);
/*!40000 ALTER TABLE `attendance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bill`
--

DROP TABLE IF EXISTS `bill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bill` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nameTable` varchar(100) DEFAULT NULL,
  `productName` varchar(100) DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `price` double DEFAULT NULL,
  `voucher_id` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bill`
--

LOCK TABLES `bill` WRITE;
/*!40000 ALTER TABLE `bill` DISABLE KEYS */;
INSERT INTO `bill` VALUES (56,'Table A08','Nước ngọt',1,20000,NULL),(64,'Table A02','Trà Sữa',1,15000,NULL),(65,'Table A02','Nước cam',1,25000,NULL),(68,'Table A10','Cà phê sữa',1,29750,15),(69,'Table A10','Trà Sữa',1,15000,NULL),(70,'Table A10','Trà Đào',1,25000,NULL);
/*!40000 ALTER TABLE `bill` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bill_detail`
--

DROP TABLE IF EXISTS `bill_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bill_detail` (
  `id` int NOT NULL AUTO_INCREMENT,
  `bill_order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `voucher_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `bill_id` (`bill_order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `bill_detail_ibfk_1` FOREIGN KEY (`bill_order_id`) REFERENCES `bill_order` (`id`) ON DELETE CASCADE,
  CONSTRAINT `bill_detail_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4440 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bill_detail`
--

LOCK TABLES `bill_detail` WRITE;
/*!40000 ALTER TABLE `bill_detail` DISABLE KEYS */;
INSERT INTO `bill_detail` VALUES (4435,2441,2,1,29750.00,15),(4436,2441,6,1,20000.00,0),(4437,2442,4,1,20000.00,0),(4438,2442,2,1,35000.00,0),(4439,2443,2,1,29750.00,15);
/*!40000 ALTER TABLE `bill_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bill_order`
--

DROP TABLE IF EXISTS `bill_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bill_order` (
  `id` int NOT NULL AUTO_INCREMENT,
  `table_id` int NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `payment_method_id` int NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `table_id` (`table_id`),
  KEY `payment_method_id` (`payment_method_id`),
  CONSTRAINT `bill_order_ibfk_1` FOREIGN KEY (`table_id`) REFERENCES `tables` (`id`),
  CONSTRAINT `bill_order_ibfk_2` FOREIGN KEY (`payment_method_id`) REFERENCES `payment_method` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2444 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bill_order`
--

LOCK TABLES `bill_order` WRITE;
/*!40000 ALTER TABLE `bill_order` DISABLE KEYS */;
INSERT INTO `bill_order` VALUES (2441,1,49750.00,3,'2024-11-14 15:57:08'),(2442,10,55000.00,3,'2024-11-14 18:02:31'),(2443,18,29750.00,3,'2024-11-14 18:21:14');
/*!40000 ALTER TABLE `bill_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cashier`
--

DROP TABLE IF EXISTS `cashier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cashier` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_unique` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cashier`
--

LOCK TABLES `cashier` WRITE;
/*!40000 ALTER TABLE `cashier` DISABLE KEYS */;
INSERT INTO `cashier` VALUES (1,'9999','$2a$10$3UHdjzv.D7WrQTBABUueGuKEcC2pHiPabhejCrldOZy2COgGDzVRK');
/*!40000 ALTER TABLE `cashier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'Cà phê'),(2,'Nước ngọt'),(3,'Nước hoa quả'),(4,'Trà'),(5,'Đồ ăn vặt '),(6,'Đồ ăn'),(7,'Sữa chua');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `delivery_order`
--

DROP TABLE IF EXISTS `delivery_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `delivery_order` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_number` varchar(20) NOT NULL,
  `supplier_id` varchar(10) NOT NULL,
  `order_date` datetime NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'Chưa Nhập Hàng',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_order_number` (`order_number`),
  KEY `fk_supplier` (`supplier_id`),
  CONSTRAINT `fk_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`suppliers_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `delivery_order`
--

LOCK TABLES `delivery_order` WRITE;
/*!40000 ALTER TABLE `delivery_order` DISABLE KEYS */;
INSERT INTO `delivery_order` VALUES (2,'4077596714','10002323','2024-10-17 07:24:41','Chưa Nhập Hàng'),(3,'4045665207','10002323','2024-10-17 07:26:13','Chưa Nhập Hàng'),(4,'4064601519','10002323','2024-10-17 07:28:27','Chưa Nhập Hàng'),(5,'4000613108','10002323','2024-10-17 07:29:17','Chưa Nhập Hàng'),(6,'4031831390','10002323','2024-10-17 07:30:13','Chưa Nhập Hàng'),(7,'4069783484','10002323','2024-10-17 10:39:27','Chưa Nhập Hàng'),(8,'458975342','10002323','2024-10-24 00:00:00','Chưa Nhập Hàng'),(9,'855224633','10002323','2024-10-24 00:00:00','Chưa Nhập Hàng'),(10,'458618309','10002323','2024-10-24 00:00:00','Chưa Nhập Hàng'),(11,'778694950','10002323','2024-10-24 00:00:00','Chưa Nhập Hàng');
/*!40000 ALTER TABLE `delivery_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `delivery_order_items`
--

DROP TABLE IF EXISTS `delivery_order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `delivery_order_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_number` varchar(20) NOT NULL,
  `supply_code` varchar(8) NOT NULL,
  `quantity` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_delivery_order` (`order_number`),
  KEY `fk_supply` (`supply_code`),
  CONSTRAINT `fk_delivery_order` FOREIGN KEY (`order_number`) REFERENCES `delivery_order` (`order_number`),
  CONSTRAINT `fk_supply` FOREIGN KEY (`supply_code`) REFERENCES `supplies` (`supply_code`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `delivery_order_items`
--

LOCK TABLES `delivery_order_items` WRITE;
/*!40000 ALTER TABLE `delivery_order_items` DISABLE KEYS */;
INSERT INTO `delivery_order_items` VALUES (2,'4077596714','CP001',1.00),(3,'4077596714','CP002',2.00),(4,'4045665207','CP001',1.00),(5,'4045665207','CP002',2.00),(6,'4064601519','CP001',1.00),(7,'4064601519','CP002',2.00),(8,'4000613108','CP001',1.00),(9,'4000613108','CP002',2.00),(10,'4031831390','CP001',1.00),(11,'4031831390','CP003',2.00),(12,'4069783484','CP001',1.00),(13,'4069783484','CP002',1.00),(14,'458975342','CP001',2.00),(15,'458975342','CP003',2.00),(16,'855224633','CP001',2.00),(17,'855224633','CP002',2.00),(18,'458618309','CP001',10.00),(19,'458618309','CP003',10.00),(20,'778694950','CP001',2.00),(21,'778694950','CP003',2.00);
/*!40000 ALTER TABLE `delivery_order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `discount`
--

DROP TABLE IF EXISTS `discount`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discount` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `discount_name` varchar(255) NOT NULL,
  `discount_percentage` decimal(5,2) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `discount_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discount`
--

LOCK TABLES `discount` WRITE;
/*!40000 ALTER TABLE `discount` DISABLE KEYS */;
INSERT INTO `discount` VALUES (1,1,'Autumn Sale',10.00,'2024-09-01','2024-12-31'),(2,2,'Holiday Special',12.50,'2024-10-01','2024-12-31'),(3,3,'Flash Discount',15.00,'2024-09-01','2024-11-30'),(4,4,'Back to School',10.50,'2024-08-15','2024-11-15'),(5,5,'Winter Deal',14.00,'2024-10-15','2025-01-15'),(6,1,'Happy',0.15,'2025-10-01','2024-11-30');
/*!40000 ALTER TABLE `discount` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `import_order`
--

DROP TABLE IF EXISTS `import_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `import_order` (
  `id` int NOT NULL AUTO_INCREMENT,
  `supplier_id` int NOT NULL,
  `delivery_date` timestamp NOT NULL,
  `total_value` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `supplier_id` (`supplier_id`),
  CONSTRAINT `import_order_ibfk_1` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `import_order`
--

LOCK TABLES `import_order` WRITE;
/*!40000 ALTER TABLE `import_order` DISABLE KEYS */;
INSERT INTO `import_order` VALUES (1,1,'2024-10-21 01:57:10',2300.00),(2,1,'2024-10-21 01:57:40',1150.00),(3,1,'2024-10-21 02:02:28',1150.00),(4,1,'2024-10-21 02:02:45',2300.00),(5,1,'2024-10-21 02:03:30',1150.00),(6,1,'2024-10-21 03:51:17',1150.00);
/*!40000 ALTER TABLE `import_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `import_order_detail`
--

DROP TABLE IF EXISTS `import_order_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `import_order_detail` (
  `id` int NOT NULL AUTO_INCREMENT,
  `import_order_id` int NOT NULL,
  `supply_id` int NOT NULL,
  `ordered_quantity` decimal(10,2) NOT NULL,
  `received_quantity` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `import_order_id` (`import_order_id`),
  KEY `supply_id` (`supply_id`),
  CONSTRAINT `import_order_detail_ibfk_1` FOREIGN KEY (`import_order_id`) REFERENCES `import_order` (`id`),
  CONSTRAINT `import_order_detail_ibfk_2` FOREIGN KEY (`supply_id`) REFERENCES `supplies` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `import_order_detail`
--

LOCK TABLES `import_order_detail` WRITE;
/*!40000 ALTER TABLE `import_order_detail` DISABLE KEYS */;
INSERT INTO `import_order_detail` VALUES (1,1,1,10.00,9.00),(2,1,2,5.00,5.00),(3,1,1,10.00,9.00),(4,1,2,5.00,5.00),(5,2,1,10.00,9.00),(6,2,2,5.00,5.00),(7,3,1,10.00,9.00),(8,3,2,5.00,5.00),(9,4,1,10.00,9.00),(10,4,2,5.00,5.00),(11,4,1,10.00,9.00),(12,4,2,5.00,5.00),(13,5,1,10.00,9.00),(14,5,2,5.00,5.00),(15,6,1,10.00,9.00),(16,6,2,5.00,5.00);
/*!40000 ALTER TABLE `import_order_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_method`
--

DROP TABLE IF EXISTS `payment_method`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_method` (
  `id` int NOT NULL AUTO_INCREMENT,
  `method` enum('CASH','QR','CARD') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_method`
--

LOCK TABLES `payment_method` WRITE;
/*!40000 ALTER TABLE `payment_method` DISABLE KEYS */;
INSERT INTO `payment_method` VALUES (1,'CASH'),(2,'QR'),(3,'CARD');
/*!40000 ALTER TABLE `payment_method` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` int NOT NULL AUTO_INCREMENT,
  `image_link` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `category_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_category_id` (`category_id`),
  CONSTRAINT `fk_category_id` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'/images/products/cafe_den.jpg','Cà phê đen',30000.00,1),(2,'/images/products/cafe_sua.jpg','Cà phê sữa',35000.00,1),(3,'/images/products/nuoc_cam.jpg','Nước cam',25000.00,3),(4,'/images/products/nuoc_ngot.jpg','Nước ngọt',20000.00,2),(5,'/images/products/tra_sua.jpg','Trà Sữa',15000.00,4),(6,'/images/products/matcha.jpg','Matcha',20000.00,4),(7,'/images/products/tra_dao.jpg','Trà Đào',25000.00,4),(8,'/images/products/tra_dau.jpg','Trà Dâu',20000.00,4),(9,'/images/products/tra_vai.jpg','Trà Vải',15000.00,4);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(100) NOT NULL,
  `basic_salary` decimal(10,2) NOT NULL,
  `allowance` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_name` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'Cửa hàng trưởng',55000.00,4000000.00),(2,'Cửa hàng phó',40000.00,3000000.00),(3,'Nhân viên fulltime',26000.00,2000000.00),(4,'Nhân viên parttime',24000.00,0.00);
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `salary`
--

DROP TABLE IF EXISTS `salary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `salary` (
  `id` int NOT NULL AUTO_INCREMENT,
  `staff_id` int DEFAULT NULL,
  `daily_rate` decimal(10,2) NOT NULL,
  `days_attended` int NOT NULL,
  `bonus` decimal(10,2) DEFAULT '0.00',
  `deductions` decimal(10,2) DEFAULT '0.00',
  `payment_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `total_salary` decimal(10,2) GENERATED ALWAYS AS ((((`daily_rate` * `days_attended`) + `bonus`) - `deductions`)) STORED,
  PRIMARY KEY (`id`),
  KEY `staff_id` (`staff_id`),
  CONSTRAINT `salary_ibfk_1` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `salary`
--

LOCK TABLES `salary` WRITE;
/*!40000 ALTER TABLE `salary` DISABLE KEYS */;
/*!40000 ALTER TABLE `salary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shift`
--

DROP TABLE IF EXISTS `shift`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shift` (
  `shift_id` int NOT NULL AUTO_INCREMENT,
  `shift_name` varchar(255) NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  PRIMARY KEY (`shift_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shift`
--

LOCK TABLES `shift` WRITE;
/*!40000 ALTER TABLE `shift` DISABLE KEYS */;
INSERT INTO `shift` VALUES (1,'Ca sáng','07:00:00','15:00:00'),(2,'Ca chiều','15:00:00','23:00:00'),(3,'Ca hành chính','09:00:00','18:00:00'),(4,'Off','00:00:00','00:00:00');
/*!40000 ALTER TABLE `shift` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `staff`
--

DROP TABLE IF EXISTS `staff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `staff` (
  `id` int NOT NULL AUTO_INCREMENT,
  `staff_id` varchar(255) DEFAULT NULL,
  `password_hash` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `contact_number` varchar(15) DEFAULT NULL,
  `role_id` int DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `gender` enum('male','female','other') DEFAULT NULL,
  `status_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_role` (`role_id`),
  KEY `idx_staff_id` (`staff_id`),
  KEY `fk_status` (`status_id`),
  CONSTRAINT `fk_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_status` FOREIGN KEY (`status_id`) REFERENCES `staff_status` (`status_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `staff`
--

LOCK TABLES `staff` WRITE;
/*!40000 ALTER TABLE `staff` DISABLE KEYS */;
INSERT INTO `staff` VALUES (4,'60000001','$2a$10$3wUX4YW5gOsIBCmZ.q61HOOJGcpQHR4mh18kprJJHH4ul5KDv4ufa','Nguyễn Văn A','0123456789',1,'nguyenvana@example.com','male',1),(5,'60000002','$2a$10$3wUX4YW5gOsIBCmZ.q61HOOJGcpQHR4mh18kprJJHH4ul5KDv4ufa','Trần Thị B','0987654321',2,'tranthib@example.com','female',1),(6,'60000003','$2a$10$3wUX4YW5gOsIBCmZ.q61HOOJGcpQHR4mh18kprJJHH4ul5KDv4ufa','Lê Văn C','0123987654',3,'levanc@example.com','male',1),(7,'60000004','$2a$10$3wUX4YW5gOsIBCmZ.q61HOOJGcpQHR4mh18kprJJHH4ul5KDv4ufa','Phạm Thị D','0981234567',3,'phamthid@example.com','female',1),(8,'60000005','$2a$10$3wUX4YW5gOsIBCmZ.q61HOOJGcpQHR4mh18kprJJHH4ul5KDv4ufa','Ngô Văn E','0123456780',3,'ngovane@example.com','male',1),(9,'60000006','$2a$10$3wUX4YW5gOsIBCmZ.q61HOOJGcpQHR4mh18kprJJHH4ul5KDv4ufa','Bùi Thị F','0987654322',4,'buithif@example.com','female',1),(10,'64292547','$2a$10$ZAcXQLQkYHbQMEIpu0/MFu4wo4ZvlNvQCQ1NVYmB/QZ3CkSkOH6mS','le minh duc','0972176252',3,'leminhduc1212001@gmail.com','male',3),(11,'69653222','$2a$10$xGtNV95MS9SX8hmiXelBcOLgup5uoS/c7dqCSHRgm82HPXc/WNiZ6','le duc','0000000000',3,'leduc@gmail.com','male',3);
/*!40000 ALTER TABLE `staff` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `staff_status`
--

DROP TABLE IF EXISTS `staff_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `staff_status` (
  `status_id` int NOT NULL AUTO_INCREMENT,
  `status_name` varchar(50) NOT NULL,
  PRIMARY KEY (`status_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `staff_status`
--

LOCK TABLES `staff_status` WRITE;
/*!40000 ALTER TABLE `staff_status` DISABLE KEYS */;
INSERT INTO `staff_status` VALUES (1,'active'),(2,'dropout'),(3,'fire');
/*!40000 ALTER TABLE `staff_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `status_table`
--

DROP TABLE IF EXISTS `status_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `status_table` (
  `id` int NOT NULL AUTO_INCREMENT,
  `status` enum('USING','CLEANING','AVAILABLE') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `status_table`
--

LOCK TABLES `status_table` WRITE;
/*!40000 ALTER TABLE `status_table` DISABLE KEYS */;
INSERT INTO `status_table` VALUES (1,'USING'),(2,'CLEANING'),(3,'AVAILABLE');
/*!40000 ALTER TABLE `status_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_info`
--

DROP TABLE IF EXISTS `store_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `store_info` (
  `id` int NOT NULL AUTO_INCREMENT,
  `store_name` varchar(255) NOT NULL,
  `store_email` varchar(255) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `store_address` text NOT NULL,
  `tax_code` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_info`
--

LOCK TABLES `store_info` WRITE;
/*!40000 ALTER TABLE `store_info` DISABLE KEYS */;
INSERT INTO `store_info` VALUES (1,'Highland','highland@gmail.com','0909199226','656 Hà Huy Tập','010241214');
/*!40000 ALTER TABLE `store_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `suppliers`
--

DROP TABLE IF EXISTS `suppliers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `suppliers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `suppliers_id` varchar(10) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `contact_number` varchar(15) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_suppliers_id` (`suppliers_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `suppliers`
--

LOCK TABLES `suppliers` WRITE;
/*!40000 ALTER TABLE `suppliers` DISABLE KEYS */;
INSERT INTO `suppliers` VALUES (1,'10002323','Cà Phê Trung Nguyên','0123456891','caphe@trungnguyen.com'),(2,'10002324','Bánh Mỳ Nguyễn Sơn','0987654321','info@nguyenson.com'),(3,'10002325','Hoa Quả Hội An','0912345678','support@hoaquahoiann.com'),(4,'10783948','Bánh Ngọt Nam Dương','09217628','leminhduc1212001@gmail.com'),(5,'10297940','Cốc nhựa','0991232813','cocnhua@contact.vn');
/*!40000 ALTER TABLE `suppliers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `supplies`
--

DROP TABLE IF EXISTS `supplies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `supplies` (
  `id` int NOT NULL AUTO_INCREMENT,
  `supply_code` varchar(8) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `unit` varchar(255) DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `quantity` decimal(10,2) NOT NULL,
  `suppliers_id` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_supply_code` (`supply_code`),
  KEY `supplies_ibfk_1` (`suppliers_id`),
  CONSTRAINT `supplies_ibfk_1` FOREIGN KEY (`suppliers_id`) REFERENCES `suppliers` (`suppliers_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supplies`
--

LOCK TABLES `supplies` WRITE;
/*!40000 ALTER TABLE `supplies` DISABLE KEYS */;
INSERT INTO `supplies` VALUES (1,'CP001','Cà Phê Arabica','kg',300000.00,82.00,'10002323'),(2,'CP002','Cà Phê Robusta','kg',280000.00,55.00,'10002323'),(3,'CP003','Cà Phê Gói','g',20000.00,100.00,'10002323'),(4,'BM001','Bánh Mỳ Pháp','cái',15000.00,50.00,'10002324'),(5,'BM002','Bánh Mỳ Sandwich','cái',20000.00,30.00,'10002324'),(6,'BM003','Bánh Mỳ Bơ','cái',18000.00,25.00,'10002324'),(7,'HQ001','Bưởi Năm Roi','kg',20000.00,20.00,'10002325'),(8,'HQ002','Dưa Hấu','kg',15000.00,25.00,'10002325'),(9,'HQ003','Nho','kg',60000.00,15.00,'10002325'),(10,'CP004','Cà phê G7','G1',50000.00,0.00,'10002323'),(11,'00000','Coffe','G1',50000.00,0.00,'10002323'),(12,'CN001','Cốc nhựa size M','CAI',500.00,0.00,'10297940'),(13,'CN002','Cốc nhựa size L','CAI',700.00,0.00,'10297940');
/*!40000 ALTER TABLE `supplies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tables`
--

DROP TABLE IF EXISTS `tables`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tables` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `status_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `status_id` (`status_id`),
  CONSTRAINT `tables_ibfk_1` FOREIGN KEY (`status_id`) REFERENCES `status_table` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tables`
--

LOCK TABLES `tables` WRITE;
/*!40000 ALTER TABLE `tables` DISABLE KEYS */;
INSERT INTO `tables` VALUES (1,'Table A01',2),(3,'Table A03',3),(4,'Table A04',3),(5,'Table A05',3),(6,'Table A06',3),(7,'Table A07',3),(8,'Table A08',1),(9,'Table A09',3),(10,'Table A10',1),(11,'Table A11',3),(12,'Table A12',3),(13,'Table A13',3),(14,'Table A14',3),(15,'Table A15',3),(16,'Table A16',3),(17,'Table A17',3),(18,'Table A18',2),(19,'Table A19',3),(20,'Table A20',3),(21,'Table A21',3),(22,'Table A22',3),(23,'Table A23',3),(25,'Table A02',1),(26,'Table A24',2);
/*!40000 ALTER TABLE `tables` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `voucher`
--

DROP TABLE IF EXISTS `voucher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `voucher` (
  `id` int NOT NULL AUTO_INCREMENT,
  `voucher_code` varchar(50) NOT NULL,
  `voucher_name` varchar(255) NOT NULL,
  `voucher_percentage` decimal(5,2) DEFAULT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `status` enum('ACTIVE','INACTIVE','USED') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `voucher_code` (`voucher_code`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `voucher`
--

LOCK TABLES `voucher` WRITE;
/*!40000 ALTER TABLE `voucher` DISABLE KEYS */;
INSERT INTO `voucher` VALUES (1,'VOUCHER-1731374175710','Happy',15.00,'2024-11-01','2024-11-30','ACTIVE');
/*!40000 ALTER TABLE `voucher` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-11-15  1:23:24

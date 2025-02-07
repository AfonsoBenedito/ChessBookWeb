-- MySQL dump 10.13  Distrib 9.2.0, for macos15.2 (arm64)
--
-- Host: 127.0.0.1    Database: chess
-- ------------------------------------------------------
-- Server version	9.2.0

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
-- Table structure for table `ChessGame`
--

USE chess;

DROP TABLE IF EXISTS `ChessGame`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ChessGame` (
  `ID` int NOT NULL,
  `BLACKNOTIFICATION` tinyint(1) DEFAULT '0',
  `DATE` datetime DEFAULT NULL,
  `ENDGAMEDESCRIPTION` varchar(255) DEFAULT NULL,
  `FINISHED` tinyint(1) DEFAULT '0',
  `OPENDATE` datetime DEFAULT NULL,
  `WHITENOTIFICATION` tinyint(1) DEFAULT '0',
  `black_ChessPlayer_id` int NOT NULL,
  `white_ChessPlayer_id` int NOT NULL,
  `winner_ChessPlayer_id` int DEFAULT NULL,
  `TIEOFFER_ID` int DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ChessGame_black_ChessPlayer_id` (`black_ChessPlayer_id`),
  KEY `FK_ChessGame_TIEOFFER_ID` (`TIEOFFER_ID`),
  KEY `FK_ChessGame_white_ChessPlayer_id` (`white_ChessPlayer_id`),
  KEY `FK_ChessGame_winner_ChessPlayer_id` (`winner_ChessPlayer_id`),
  CONSTRAINT `FK_ChessGame_black_ChessPlayer_id` FOREIGN KEY (`black_ChessPlayer_id`) REFERENCES `ChessPlayer` (`ID`),
  CONSTRAINT `FK_ChessGame_TIEOFFER_ID` FOREIGN KEY (`TIEOFFER_ID`) REFERENCES `ChessPlayer` (`ID`),
  CONSTRAINT `FK_ChessGame_white_ChessPlayer_id` FOREIGN KEY (`white_ChessPlayer_id`) REFERENCES `ChessPlayer` (`ID`),
  CONSTRAINT `FK_ChessGame_winner_ChessPlayer_id` FOREIGN KEY (`winner_ChessPlayer_id`) REFERENCES `ChessPlayer` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ChessGame`
--

LOCK TABLES `ChessGame` WRITE;
/*!40000 ALTER TABLE `ChessGame` DISABLE KEYS */;
/*!40000 ALTER TABLE `ChessGame` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ChessGame_ChessMove`
--

DROP TABLE IF EXISTS `ChessGame_ChessMove`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ChessGame_ChessMove` (
  `ChessGame_ID` int NOT NULL,
  `moves_idMove` int NOT NULL,
  PRIMARY KEY (`ChessGame_ID`,`moves_idMove`),
  KEY `FK_ChessGame_ChessMove_moves_idMove` (`moves_idMove`),
  CONSTRAINT `FK_ChessGame_ChessMove_ChessGame_ID` FOREIGN KEY (`ChessGame_ID`) REFERENCES `ChessGame` (`ID`),
  CONSTRAINT `FK_ChessGame_ChessMove_moves_idMove` FOREIGN KEY (`moves_idMove`) REFERENCES `ChessMove` (`idMove`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ChessGame_ChessMove`
--

LOCK TABLES `ChessGame_ChessMove` WRITE;
/*!40000 ALTER TABLE `ChessGame_ChessMove` DISABLE KEYS */;
/*!40000 ALTER TABLE `ChessGame_ChessMove` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ChessMove`
--

DROP TABLE IF EXISTS `ChessMove`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ChessMove` (
  `idMove` int NOT NULL AUTO_INCREMENT,
  `PROMOTION` int DEFAULT NULL,
  `TIMEMILLI` int DEFAULT NULL,
  `Color` int DEFAULT NULL,
  `ID` int DEFAULT NULL,
  `PieceType` int DEFAULT NULL,
  `fromCol` int DEFAULT NULL,
  `fromRow` int DEFAULT NULL,
  `toCol` int DEFAULT NULL,
  `toRow` int DEFAULT NULL,
  `PLAYER_ID` int DEFAULT NULL,
  PRIMARY KEY (`idMove`),
  KEY `FK_ChessMove_PLAYER_ID` (`PLAYER_ID`),
  CONSTRAINT `FK_ChessMove_PLAYER_ID` FOREIGN KEY (`PLAYER_ID`) REFERENCES `ChessPlayer` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ChessMove`
--

LOCK TABLES `ChessMove` WRITE;
/*!40000 ALTER TABLE `ChessMove` DISABLE KEYS */;
/*!40000 ALTER TABLE `ChessMove` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ChessPlayer`
--

DROP TABLE IF EXISTS `ChessPlayer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ChessPlayer` (
  `ID` int NOT NULL,
  `EMAIL` varchar(255) DEFAULT NULL,
  `NAME` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ChessPlayer`
--

LOCK TABLES `ChessPlayer` WRITE;
/*!40000 ALTER TABLE `ChessPlayer` DISABLE KEYS */;
/*!40000 ALTER TABLE `ChessPlayer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQUENCE`
--

DROP TABLE IF EXISTS `SEQUENCE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SEQUENCE` (
  `SEQ_NAME` varchar(50) NOT NULL,
  `SEQ_COUNT` decimal(38,0) DEFAULT NULL,
  PRIMARY KEY (`SEQ_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQUENCE`
--

LOCK TABLES `SEQUENCE` WRITE;
/*!40000 ALTER TABLE `SEQUENCE` DISABLE KEYS */;
INSERT INTO `SEQUENCE` VALUES ('SEQ_GEN',0);
/*!40000 ALTER TABLE `SEQUENCE` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-02-06 15:06:18

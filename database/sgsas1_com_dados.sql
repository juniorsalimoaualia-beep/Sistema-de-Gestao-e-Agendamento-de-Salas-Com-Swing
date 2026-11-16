-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: sgsas1
-- ------------------------------------------------------
-- Server version	8.0.43

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
-- Table structure for table `cursos`
--

DROP TABLE IF EXISTS `cursos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cursos` (
  `id` int NOT NULL,
  `nome` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `departamento_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_cursos_nome` (`nome`),
  KEY `ix_cursos_departamento` (`departamento_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cursos`
--

LOCK TABLES `cursos` WRITE;
/*!40000 ALTER TABLE `cursos` DISABLE KEYS */;
INSERT INTO `cursos` VALUES (1,'Engenharia Informatica',1),(2,'Engenharia Electrica',1),(3,'Engenharia Mecanica',3),(4,'Engenharia de Gestao Industrial',3),(5,'Engenharia Quimica',5),(6,'Engenharia Civil',1);
/*!40000 ALTER TABLE `cursos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `departamentos`
--

DROP TABLE IF EXISTS `departamentos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `departamentos` (
  `id` int NOT NULL,
  `nome` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `curso_id` int DEFAULT NULL,
  `secretario_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_departamentos_nome` (`nome`),
  UNIQUE KEY `uq_departamentos_secretario` (`secretario_id`),
  KEY `ix_departamentos_curso` (`curso_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `departamentos`
--

LOCK TABLES `departamentos` WRITE;
/*!40000 ALTER TABLE `departamentos` DISABLE KEYS */;
INSERT INTO `departamentos` VALUES (1,'Eletrotecnica',NULL,2),(2,'Cadeiras Gerais',NULL,NULL),(3,'Mecanica',NULL,NULL),(4,'Civil',NULL,NULL),(5,'Quimica',NULL,NULL);
/*!40000 ALTER TABLE `departamentos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `disciplinas`
--

DROP TABLE IF EXISTS `disciplinas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `disciplinas` (
  `id` int NOT NULL,
  `nome` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `docente_id` int DEFAULT NULL,
  `curso_id` int DEFAULT NULL,
  `ano` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_disciplinas_nome` (`nome`),
  KEY `ix_disciplinas_docente` (`docente_id`),
  KEY `ix_disciplinas_curso` (`curso_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `disciplinas`
--

LOCK TABLES `disciplinas` WRITE;
/*!40000 ALTER TABLE `disciplinas` DISABLE KEYS */;
INSERT INTO `disciplinas` VALUES (1,'Programacao Orientada a Objetos 2',3,1,2),(2,'Inteligencia Artificial 1',3,1,4),(3,'Investigacao Operacional',NULL,NULL,3),(4,'Teoria de Circuitos',4,2,2);
/*!40000 ALTER TABLE `disciplinas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `horarios`
--

DROP TABLE IF EXISTS `horarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `horarios` (
  `id` int NOT NULL,
  `curso_id` int NOT NULL,
  `disciplina_id` int DEFAULT NULL,
  `sala_id` int NOT NULL,
  `dia_semana` enum('SEGUNDA','TERCA','QUARTA','QUINTA','SEXTA','SABADO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `hora_inicio` time NOT NULL,
  `hora_fim` time NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ix_horarios_curso` (`curso_id`),
  KEY `ix_horarios_disciplina` (`disciplina_id`),
  KEY `ix_horarios_sala` (`sala_id`),
  KEY `ix_horarios_dia_hora` (`dia_semana`,`hora_inicio`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `horarios`
--

LOCK TABLES `horarios` WRITE;
/*!40000 ALTER TABLE `horarios` DISABLE KEYS */;
INSERT INTO `horarios` VALUES (1,1,1,1,'SEGUNDA','17:05:00','19:50:00'),(2,1,3,1,'TERCA','19:50:00','21:35:00'),(3,1,2,1,'QUINTA','18:50:00','21:35:00');
/*!40000 ALTER TABLE `horarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inscricoes`
--

DROP TABLE IF EXISTS `inscricoes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inscricoes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `estudante_id` int NOT NULL,
  `disciplina_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_inscricoes_estudante_disciplina` (`estudante_id`,`disciplina_id`),
  KEY `ix_inscricoes_estudante` (`estudante_id`),
  KEY `ix_inscricoes_disciplina` (`disciplina_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inscricoes`
--

LOCK TABLES `inscricoes` WRITE;
/*!40000 ALTER TABLE `inscricoes` DISABLE KEYS */;
INSERT INTO `inscricoes` VALUES (1,5,1);
/*!40000 ALTER TABLE `inscricoes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservas`
--

DROP TABLE IF EXISTS `reservas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservas` (
  `id` int NOT NULL,
  `sala_id` int NOT NULL,
  `docente_id` int NOT NULL,
  `disciplina_id` int DEFAULT NULL,
  `turma_id` int DEFAULT NULL,
  `data` date NOT NULL,
  `hora_inicio` time NOT NULL,
  `hora_fim` time NOT NULL,
  `tipo` enum('AULA','EVENTO','EXAME','TESTE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `dia_semana` enum('SEGUNDA','TERCA','QUARTA','QUINTA','SEXTA','SABADO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `estado` enum('PENDENTE','CONFIRMADA','RECUSADA','CANCELADA') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDENTE',
  PRIMARY KEY (`id`),
  KEY `ix_reservas_sala_data` (`sala_id`,`data`),
  KEY `ix_reservas_docente` (`docente_id`),
  KEY `ix_reservas_disciplina` (`disciplina_id`),
  KEY `ix_reservas_turma` (`turma_id`),
  KEY `ix_reservas_intervalo` (`sala_id`,`data`,`hora_inicio`,`hora_fim`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservas`
--

LOCK TABLES `reservas` WRITE;
/*!40000 ALTER TABLE `reservas` DISABLE KEYS */;
/*!40000 ALTER TABLE `reservas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `salas`
--

DROP TABLE IF EXISTS `salas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `salas` (
  `id` int NOT NULL,
  `nome` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `estado` enum('LIVRE','OCUPADA','MANUTENCAO','RESERVADA') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'LIVRE',
  `reserva_id` int NOT NULL DEFAULT '-1',
  `departamento_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_salas_departamento_nome` (`departamento_id`,`nome`),
  KEY `ix_salas_nome` (`nome`),
  KEY `ix_salas_reserva` (`reserva_id`),
  KEY `ix_salas_departamento` (`departamento_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `salas`
--

LOCK TABLES `salas` WRITE;
/*!40000 ALTER TABLE `salas` DISABLE KEYS */;
INSERT INTO `salas` VALUES (1,'Lab. Informatica','LIVRE',-1,1),(2,'H2E1','LIVRE',-1,1),(3,'H2A1','LIVRE',-1,1),(4,'Lab. Telecomunicacoes','LIVRE',-1,1),(5,'Lab. Digital','LIVRE',-1,1),(6,'118','LIVRE',-1,2),(7,'119','LIVRE',-1,2),(8,'123','LIVRE',-1,2),(9,'109','LIVRE',-1,2);
/*!40000 ALTER TABLE `salas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `turma_estudantes`
--

DROP TABLE IF EXISTS `turma_estudantes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `turma_estudantes` (
  `turma_id` int NOT NULL,
  `estudante_id` int NOT NULL,
  PRIMARY KEY (`turma_id`,`estudante_id`),
  KEY `ix_turma_estudantes_estudante` (`estudante_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `turma_estudantes`
--

LOCK TABLES `turma_estudantes` WRITE;
/*!40000 ALTER TABLE `turma_estudantes` DISABLE KEYS */;
INSERT INTO `turma_estudantes` VALUES (1,5);
/*!40000 ALTER TABLE `turma_estudantes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `turmas`
--

DROP TABLE IF EXISTS `turmas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `turmas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ano` int NOT NULL,
  `curso_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_turmas_ano_curso` (`ano`,`curso_id`),
  KEY `ix_turmas_curso` (`curso_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `turmas`
--

LOCK TABLES `turmas` WRITE;
/*!40000 ALTER TABLE `turmas` DISABLE KEYS */;
INSERT INTO `turmas` VALUES (1,3,1);
/*!40000 ALTER TABLE `turmas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `perfil` enum('ADMIN','SECRETARIO','DOCENTE','ESTUDANTE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `nome` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `apelido` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `nivel_academico` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cargo` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `curso_id` int DEFAULT NULL,
  `ano` int DEFAULT NULL,
  `celular` int NOT NULL DEFAULT '0',
  `email` varchar(190) COLLATE utf8mb4_unicode_ci NOT NULL,
  `senha_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_usuarios_perfil_email` (`perfil`,`email`),
  UNIQUE KEY `uq_usuarios_celular` (`celular`),
  KEY `ix_usuarios_curso` (`curso_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'ADMIN','Administrador','',NULL,NULL,NULL,NULL,0,'admin@admin.com','$2a$12$GyW8KbH0u4tqWhJndFpoo.yV5OPi9Sgk4gQUksH5BwYuQfNpLZfLC'),(2,'SECRETARIO','Ayrton','senna',NULL,'secretario',NULL,NULL,873672882,'ayrtonsenna608@uem.com','$2a$12$tLhIg7Pih21iU5ez94kyvuUhOc2grbwseGzI1hPeWkFUneMaHvUwq'),(3,'DOCENTE','Ruben','Manhica','Mestrado',NULL,NULL,NULL,876482821,'rubenmanhica735@uem.com','$2a$12$OlnSPIYxR24VL.XehcbqqevZiD9o2AYdKNdzCVdvhFVgicqqT3Pxi'),(4,'DOCENTE','Acacio','Zimbico','Doutorado',NULL,NULL,NULL,847373838,'acaciozimbico135@uem.com','$2a$12$Cp2fCBURtKqYxyQRYe9eAOzjCT1fK4vg3Jc.CTPC.ZcyoWO2oRjjW'),(5,'ESTUDANTE','Salimo','Aualia',NULL,NULL,1,3,849189617,'salimoaualia917@uem.com','$2a$12$qS5p1sE6adNQPRf1GLu32.6SJCC5IJYwlCcRwHOPn99MMif5jrBmK');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-16 23:38:04

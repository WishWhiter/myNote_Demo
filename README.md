# 个人笔记网站（Spring Boot + Editor.md）

## 技术栈
- 后端：Java 17, Spring Boot 3.5.6, Spring Security, Spring Data JPA
- 数据库：MySQL 5.7+
- 前端：Thymeleaf + Editor.md
- 构建：Maven

## 本地运行指南

### 1. 环境准备
- 安装 **Java 17**
- 安装 **Maven 3.8+**
- 安装 **MySQL 5.7+**

### 2. 数据库配置
1. 启动 MySQL
2. 创建数据库（名称必须为 `note_app`）：
   ```sql
   CREATE DATABASE note_app CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

/*
 Navicat Premium Data Transfer

 Source Server         : T2 trade
 Source Server Type    : MySQL
 Source Server Version : 80011
 Source Host           : 10.188.56.56:9442
 Source Schema         : trade

 Target Server Type    : MySQL
 Target Server Version : 80011
 File Encoding         : 65001

 Date: 09/12/2019 17:16:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for order_delay_tracking
-- ----------------------------
DROP TABLE IF EXISTS `order_delay_tracking`;
CREATE TABLE `order_delay_tracking`  (
  `tracking_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL COMMENT '订单id',
  `order_delay_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单延迟类型 delivery_date: 收货时间',
  `delay_days` int(11) NOT NULL COMMENT '延迟天数',
  `oper_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作人类型  buyer：买家；seller：商家',
  `created_when` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`tracking_id`) USING BTREE,
  INDEX `index_n1`(`order_id`, `order_delay_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 48 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单延迟记录表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

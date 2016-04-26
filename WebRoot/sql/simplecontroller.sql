/*
MySQL Data Transfer
Source Host: localhost
Source Database: simplecontroller
Target Host: localhost
Target Database: simplecontroller
Date: 2015/11/27 22:34:58
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for users
-- ----------------------------
CREATE TABLE `users` (
  `id` varchar(32) NOT NULL,
  `userName` varchar(32) DEFAULT NULL,
  `password` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
INSERT INTO `users` VALUES ('402881e75033c8c2015033cc90910000', '123456', '123456');

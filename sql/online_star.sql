/*
 Navicat Premium Data Transfer

 Source Server         : BI
 Source Server Type    : MySQL
 Source Server Version : 50636
 Source Host           : 192.168.70.145:3306
 Source Schema         : online_star

 Target Server Type    : MySQL
 Target Server Version : 50636
 File Encoding         : 65001

 Date: 26/04/2019 14:50:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for business_line_user
-- ----------------------------
DROP TABLE IF EXISTS `business_line_user`;
CREATE TABLE `business_line_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_name` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '用户id',
  `real_name` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '真实名称',
  `department` varchar(256) COLLATE utf8_bin DEFAULT '' COMMENT '部门',
  `email` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '邮箱',
  `mobile` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '手机号',
  `business_line` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '业务线id',
  `creater` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6940 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for dashboard
-- ----------------------------
DROP TABLE IF EXISTS `dashboard`;
CREATE TABLE `dashboard` (
  `res_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '资源ID',
  `name` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '资源名称',
  `layout_json` text COLLATE utf8_bin COMMENT '定义大盘中的图表显示',
  PRIMARY KEY (`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for data_table
-- ----------------------------
DROP TABLE IF EXISTS `data_table`;
CREATE TABLE `data_table` (
  `res_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '资源ID',
  `name` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '资源名称',
  `datasource_id` varchar(255) COLLATE utf8_bin DEFAULT '' COMMENT '数据源ID',
  `table_name` varchar(255) COLLATE utf8_bin DEFAULT '' COMMENT 'ES事实表名称',
  `database_name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT 'ES数据库名称',
  PRIMARY KEY (`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for dataset
-- ----------------------------
DROP TABLE IF EXISTS `dataset`;
CREATE TABLE `dataset` (
  `res_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '资源id',
  `name` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '资源名称',
  `data_json` text COLLATE utf8_bin COMMENT '数据源',
  `time_primary_key` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '时间主键',
  PRIMARY KEY (`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for datasource
-- ----------------------------
DROP TABLE IF EXISTS `datasource`;
CREATE TABLE `datasource` (
  `id` varchar(64) NOT NULL COMMENT '数据源ID',
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT '数据源名称',
  `type` int(3) NOT NULL COMMENT '数据源类型：100 : ES/101 : Mysql/102 : Solr',
  `config` text NOT NULL COMMENT '数据源连接串',
  `description` varchar(128) DEFAULT '' COMMENT '资源描述',
  `creater` varchar(64) DEFAULT '' COMMENT '创建人',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_modifier` varchar(64) DEFAULT '' COMMENT '最后修改人',
  `last_modify_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最近修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for datasource_permission
-- ----------------------------
DROP TABLE IF EXISTS `datasource_permission`;
CREATE TABLE `datasource_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `datasource_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '数据源Id',
  `datasource_type` int(3) DEFAULT NULL COMMENT '数据源类型',
  `business_line` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '业务线',
  `role_ids` varchar(2048) COLLATE utf8_bin DEFAULT NULL COMMENT '角色',
  `description` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '资源描述',
  `creater` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '创建人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_modifier` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '最后修改人',
  `last_modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最近修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for filter_component
-- ----------------------------
DROP TABLE IF EXISTS `filter_component`;
CREATE TABLE `filter_component` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '资源id',
  `name` varchar(64) DEFAULT NULL COMMENT '行级权限名称',
  `res_type` int(3) DEFAULT NULL COMMENT '资源类型',
  `res_id` varchar(64) NOT NULL COMMENT '资源Id',
  `role_id` varchar(64) NOT NULL COMMENT '角色id',
  `connector_type` int(3) NOT NULL DEFAULT '0' COMMENT '连接符类型',
  `col_key` varchar(1024) NOT NULL COMMENT '添加行级权限的字段',
  `col_name` varchar(1024) DEFAULT NULL COMMENT '添加行级权限的字段名称',
  `col_type` varchar(64) DEFAULT 'string' COMMENT '添加行级权限的字段类型',
  `filter_type` varchar(64) DEFAULT NULL COMMENT '行级权限过滤类型',
  `filter_value` varchar(1024) DEFAULT NULL COMMENT '行级权限过滤值',
  `description` varchar(128) DEFAULT NULL COMMENT '资源描述',
  `creater` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_modifier` varchar(64) DEFAULT NULL COMMENT '最后修改人',
  `last_modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最近修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=183 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for log
-- ----------------------------
DROP TABLE IF EXISTS `log`;
CREATE TABLE `log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` varchar(32) DEFAULT NULL COMMENT '谁',
  `ip` varchar(32) DEFAULT NULL,
  `log_time` datetime DEFAULT NULL COMMENT '什么时间',
  `info_type` varchar(32) DEFAULT NULL COMMENT '什么场景，一级场景',
  `op_type` varchar(32) DEFAULT NULL COMMENT '什么场景，二级场景',
  `act` text COMMENT '做了什么事儿，json格式',
  `act_desc` varchar(1024) DEFAULT NULL COMMENT '做了什么事儿，描述字段',
  `optional` varchar(1024) DEFAULT NULL COMMENT '额外信息',
  `department` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12151 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for operation_resource
-- ----------------------------
DROP TABLE IF EXISTS `operation_resource`;
CREATE TABLE `operation_resource` (
  `id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '资源ID',
  `name` varchar(128) COLLATE utf8_bin DEFAULT '' COMMENT '资源名称',
  `key` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '资源标识符',
  `code` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '资源编码',
  `pid` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '父id',
  `type` int(3) DEFAULT NULL COMMENT '资源类型：1页面、2按钮',
  `order` int(11) DEFAULT NULL COMMENT '资源顺序',
  `description` varchar(128) COLLATE utf8_bin DEFAULT '' COMMENT '资源描述',
  `creater` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '创建人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_modifier` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '最后修改人',
  `last_modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最近修改时间',
  `state` int(3) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(64) COLLATE utf8_bin DEFAULT '',
  `name` varchar(64) COLLATE utf8_bin DEFAULT '',
  `type` int(3) DEFAULT NULL,
  `description` varchar(128) COLLATE utf8_bin DEFAULT '',
  `available_resource` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `state` int(3) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for report
-- ----------------------------
DROP TABLE IF EXISTS `report`;
CREATE TABLE `report` (
  `res_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '资源ID',
  `name` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '资源名称',
  `dataset_id` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '数据源ID',
  `layout_json` text COLLATE utf8_bin COMMENT '图表数据',
  `type` int(6) DEFAULT NULL COMMENT '报表类型：、100 : commonReport、101 : realtimeReport、201 : dailyReport、202 : weekReport、203 : mounthReport',
  PRIMARY KEY (`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for res_tree
-- ----------------------------
DROP TABLE IF EXISTS `res_tree`;
CREATE TABLE `res_tree` (
  `id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '资源id',
  `name` varchar(128) COLLATE utf8_bin DEFAULT '' COMMENT '资源名称',
  `pid` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '父id',
  `type` int(3) DEFAULT NULL COMMENT '资源类型：0 : 节点、100 ： 业务线、101 ： 大盘、102 ： 报表目录、103 ： 报表、104 ： 图表目录、105 ： 图表、200 ： 事实表目录、201 ： 事实表、300 ： 数据集目录、301 ： 数据集',
  `sort` int(11) DEFAULT NULL COMMENT '同级中节点显示顺序',
  `busines_line` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '业务线Id,关联资源表中业务线类型的资源Id',
  `description` varchar(128) COLLATE utf8_bin DEFAULT '' COMMENT '资源描述',
  `creater` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '创建人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_modifier` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '最后修改人',
  `last_modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最近修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '角色ID',
  `name` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '角色名称',
  `type` int(3) DEFAULT NULL COMMENT '201 : 业务线角色、202 : 业务线管理员角色',
  `business_line` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '业务线Id',
  `description` varchar(128) COLLATE utf8_bin DEFAULT '' COMMENT '资源描述',
  `creater` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '创建人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_modifier` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '最后修改人',
  `last_modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最近修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for role_res_rel
-- ----------------------------
DROP TABLE IF EXISTS `role_res_rel`;
CREATE TABLE `role_res_rel` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `res_id` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '资源ID',
  `res_type` int(3) DEFAULT NULL COMMENT '资源类型：100 ： 业务线、101 ： 大盘、102 ： 报表目录、103 ： 报表、104 ： 图表目录、105 ： 图表、200 ： 事实表目录、201 ： 事实表、300 ： 数据集目录、301 ： 数据集、501 ：菜单权限',
  `role_id` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '角色ID',
  `recursion` int(1) DEFAULT NULL COMMENT '0: 不递归赋权、1: 递归赋权',
  `permissions` text COLLATE utf8_bin COMMENT '资源操作权限列表',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3048 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for user_role_rel
-- ----------------------------
DROP TABLE IF EXISTS `user_role_rel`;
CREATE TABLE `user_role_rel` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `role_id` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '角色id',
  `user_id` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '用户id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=422 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for widget
-- ----------------------------
DROP TABLE IF EXISTS `widget`;
CREATE TABLE `widget` (
  `res_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '资源ID',
  `name` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '资源名称',
  `data_json` text COLLATE utf8_bin COMMENT '图表数据',
  `dataset_id` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '数据源ID',
  `chart_type` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '图表类型',
  PRIMARY KEY (`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

SET FOREIGN_KEY_CHECKS = 1;

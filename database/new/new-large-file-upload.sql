/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1 MySQL
 Source Server Type    : MySQL
 Source Server Version : 80200 (8.2.0)
 Source Host           : 127.0.0.1:3306
 Source Schema         : ruoyi-vue-pro

 Target Server Type    : MySQL
 Target Server Version : 80200 (8.2.0)
 File Encoding         : 65001

 Date: 22/03/2026
 Description: 文件分片上传功能 - 参照 LargeFileHandler 设计
              设计理念：分片是临时数据，通过文件系统管理，不需要 chunks 表
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for infra_file_upload_task
-- ----------------------------
DROP TABLE IF EXISTS `infra_file_upload_task`;
CREATE TABLE `infra_file_upload_task`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务编号',
  `file_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件唯一标识(UUID)',
  `filename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名',
  `file_size` bigint NOT NULL COMMENT '文件大小(字节)',
  `chunk_size` int NOT NULL COMMENT '分片大小(字节)',
  `total_chunks` int NOT NULL COMMENT '总分片数',
  `uploaded_chunks` int NOT NULL DEFAULT 0 COMMENT '已上传分片数',
  `merged_chunks` int NOT NULL DEFAULT 0 COMMENT '已合并分片数(用于跟踪合并进度)',
  `file_hash` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件哈希(用于秒传检测)',
  `status` varchar(20) NOT NULL DEFAULT 'uploading' COMMENT '状态(uploading/uploading_failed/merging/merging_failed/completed/interrupted)',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户编号',
  `path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件存储路径',
  `content_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'MIME类型',
  `error_message` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '错误信息',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_file_id`(`file_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_file_hash`(`file_hash`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件分片上传任务表';

-- ----------------------------
-- Records of infra_file_upload_task
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- 注意：不需要 infra_file_chunk 表！
-- 分片信息通过文件系统管理：chunks/{fileId}/{chunkIndex}.chunk
-- 获取已上传分片：扫描文件系统目录
-- 参照 LargeFileHandler 的设计理念
-- ----------------------------

-- ----------------------------
-- 定时任务配置 - 清理过期文件上传任务
-- ----------------------------
INSERT INTO `infra_job` (`name`, `status`, `handler_name`, `handler_param`, `cron_expression`, `retry_count`, `retry_interval`, `monitor_timeout`, `creator`, `create_time`, `updater`, `update_time`)
VALUES ('清理过期文件上传任务', 1, 'fileUploadTaskCleanJob', '7', '0 0 3 * * ?', 3, 1000, 0, 'admin', NOW(), 'admin', NOW());

-- ----------------------------
-- 修改 infra_file 表 size 字段，支持大文件（int 最大约 2GB，bigint 可支持超大文件）
-- ----------------------------
ALTER TABLE `infra_file` MODIFY COLUMN `size` bigint NOT NULL COMMENT '文件大小';

-- ----------------------------
-- 修改 infra_file_upload_task 表 error_message 字段，存储完整错误信息
-- ----------------------------
ALTER TABLE `infra_file_upload_task` MODIFY COLUMN `error_message` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '错误信息';

SET FOREIGN_KEY_CHECKS = 1;


/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1 MySQL
 Source Server Type    : MySQL
 Source Server Version : 80200 (8.2.0)
 Source Host           : 127.0.0.1:3306
 Source Schema         : ruoyi-vue-pro

 Target Server Type    : MySQL
 Target Server Version : 80200 (8.2.0)
 File Encoding         : 65001

 Date: 05/04/2026
 Description: 大文件上传菜单配置 - 独立模块
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 大文件上传菜单（放在文件管理目录下，parent_id=1243）
-- 菜单ID使用2800+系列，避免与现有菜单冲突
-- ----------------------------

-- 1. 大文件上传菜单（页面菜单）
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (6000, '大文件上传', '', 2, 6, 1243, 'large-file', 'ep:upload-filled', 'infra/largeFile/index', 'InfraLargeFile', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');

-- 2. 大文件上传 - 查询权限
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (6001, '大文件上传查询', 'infra:large-file:query', 3, 1, 6000, '', '', '', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');

-- 3. 大文件上传 - 更新权限（用于重置任务等操作）
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (6002, '大文件上传更新', 'infra:large-file:update', 3, 2, 6000, '', '', '', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');

-- 4. 大文件上传 - 删除权限
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (6003, '大文件上传删除', 'infra:large-file:delete', 3, 3, 6000, '', '', '', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');

-- ----------------------------
-- 更新定时任务处理器名称（如果之前已经添加过）
-- ----------------------------
UPDATE `infra_job` SET `handler_name` = 'largeFileCleanJob', `name` = '清理过期大文件上传任务'
WHERE `handler_name` = 'fileUploadTaskCleanJob';

-- ----------------------------
-- 大文件上传菜单国际化
-- ----------------------------
INSERT INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (6000, 'zh-CN', '大文件上传', '1', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (6000, 'en', 'Large File Upload', '1', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (6001, 'zh-CN', '大文件上传查询', '1', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (6001, 'en', 'Query', '1', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (6002, 'zh-CN', '大文件上传更新', '1', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (6002, 'en', 'Update', '1', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (6003, 'zh-CN', '大文件上传删除', '1', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (6003, 'en', 'Delete', '1', NOW(), '1', NOW(), b'0');

SET FOREIGN_KEY_CHECKS = 1;

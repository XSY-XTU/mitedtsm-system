-- Update system_dict_data label to English
-- Generated on 2026-04-05
-- This file contains UPDATE statements to translate Chinese labels to English

-- ============================================
-- system_user_sex (用户性别)
-- ============================================
UPDATE system_dict_data SET label = 'Male' WHERE id = 1 AND dict_type = 'system_user_sex';
UPDATE system_dict_data SET label = 'Female' WHERE id = 2 AND dict_type = 'system_user_sex';

-- ============================================
-- infra_job_status (定时任务状态)
-- ============================================
UPDATE system_dict_data SET label = 'Normal' WHERE id = 8 AND dict_type = 'infra_job_status';
UPDATE system_dict_data SET label = 'Paused' WHERE id = 9 AND dict_type = 'infra_job_status';
UPDATE system_dict_data SET label = 'Initializing' WHERE id = 53 AND dict_type = 'infra_job_status';

-- ============================================
-- infra_config_type (参数类型)
-- ============================================
UPDATE system_dict_data SET label = 'System Built-in' WHERE id = 12 AND dict_type = 'infra_config_type';
UPDATE system_dict_data SET label = 'Custom' WHERE id = 13 AND dict_type = 'infra_config_type';

-- ============================================
-- system_notice_type (通知类型)
-- ============================================
UPDATE system_dict_data SET label = 'Notification' WHERE id = 14 AND dict_type = 'system_notice_type';
UPDATE system_dict_data SET label = 'Announcement' WHERE id = 15 AND dict_type = 'system_notice_type';

-- ============================================
-- common_status (系统状态)
-- ============================================
UPDATE system_dict_data SET label = 'Enabled' WHERE id = 27 AND dict_type = 'common_status';
UPDATE system_dict_data SET label = 'Disabled' WHERE id = 28 AND dict_type = 'common_status';

-- ============================================
-- system_menu_type (菜单类型)
-- ============================================
UPDATE system_dict_data SET label = 'Directory' WHERE id = 29 AND dict_type = 'system_menu_type';
UPDATE system_dict_data SET label = 'Menu' WHERE id = 30 AND dict_type = 'system_menu_type';
UPDATE system_dict_data SET label = 'Button' WHERE id = 31 AND dict_type = 'system_menu_type';

-- ============================================
-- system_role_type (角色类型)
-- ============================================
UPDATE system_dict_data SET label = 'Built-in' WHERE id = 32 AND dict_type = 'system_role_type';
UPDATE system_dict_data SET label = 'Custom' WHERE id = 33 AND dict_type = 'system_role_type';

-- ============================================
-- system_data_scope (数据权限)
-- ============================================
UPDATE system_dict_data SET label = 'All Data' WHERE id = 34 AND dict_type = 'system_data_scope';
UPDATE system_dict_data SET label = 'Dept Data' WHERE id = 35 AND dict_type = 'system_data_scope';
UPDATE system_dict_data SET label = 'Dept Only' WHERE id = 36 AND dict_type = 'system_data_scope';
UPDATE system_dict_data SET label = 'Dept Below' WHERE id = 37 AND dict_type = 'system_data_scope';
UPDATE system_dict_data SET label = 'Self Only' WHERE id = 38 AND dict_type = 'system_data_scope';

-- ============================================
-- system_login_result (登录结果)
-- ============================================
UPDATE system_dict_data SET label = 'Success' WHERE id = 39 AND dict_type = 'system_login_result';
UPDATE system_dict_data SET label = 'Invalid credentials' WHERE id = 40 AND dict_type = 'system_login_result';
UPDATE system_dict_data SET label = 'User disabled' WHERE id = 41 AND dict_type = 'system_login_result';
UPDATE system_dict_data SET label = 'Captcha not found' WHERE id = 42 AND dict_type = 'system_login_result';
UPDATE system_dict_data SET label = 'Captcha error' WHERE id = 43 AND dict_type = 'system_login_result';
UPDATE system_dict_data SET label = 'Unknown error' WHERE id = 44 AND dict_type = 'system_login_result';

-- ============================================
-- infra_boolean_string (是否类型)
-- ============================================
UPDATE system_dict_data SET label = 'Yes' WHERE id = 45 AND dict_type = 'infra_boolean_string';
UPDATE system_dict_data SET label = 'No' WHERE id = 46 AND dict_type = 'infra_boolean_string';

-- ============================================
-- infra_operate_type (操作类型)
-- ============================================
UPDATE system_dict_data SET label = 'Other' WHERE id = 16 AND dict_type = 'infra_operate_type';
UPDATE system_dict_data SET label = 'Query' WHERE id = 17 AND dict_type = 'infra_operate_type';
UPDATE system_dict_data SET label = 'Create' WHERE id = 18 AND dict_type = 'infra_operate_type';
UPDATE system_dict_data SET label = 'Update' WHERE id = 19 AND dict_type = 'infra_operate_type';
UPDATE system_dict_data SET label = 'Delete' WHERE id = 20 AND dict_type = 'infra_operate_type';
UPDATE system_dict_data SET label = 'Export' WHERE id = 22 AND dict_type = 'infra_operate_type';
UPDATE system_dict_data SET label = 'Import' WHERE id = 23 AND dict_type = 'infra_operate_type';

-- ============================================
-- infra_job_log_status (任务日志状态)
-- ============================================
UPDATE system_dict_data SET label = 'Running' WHERE id = 57 AND dict_type = 'infra_job_log_status';
UPDATE system_dict_data SET label = 'Success' WHERE id = 58 AND dict_type = 'infra_job_log_status';
UPDATE system_dict_data SET label = 'Failed' WHERE id = 59 AND dict_type = 'infra_job_log_status';

-- ============================================
-- user_type (用户类型)
-- ============================================
UPDATE system_dict_data SET label = 'Member' WHERE id = 60 AND dict_type = 'user_type';
UPDATE system_dict_data SET label = 'Admin' WHERE id = 61 AND dict_type = 'user_type';

-- ============================================
-- infra_api_error_log_process_status (API错误处理状态)
-- ============================================
UPDATE system_dict_data SET label = 'Pending' WHERE id = 62 AND dict_type = 'infra_api_error_log_process_status';
UPDATE system_dict_data SET label = 'Processed' WHERE id = 63 AND dict_type = 'infra_api_error_log_process_status';
UPDATE system_dict_data SET label = 'Ignored' WHERE id = 64 AND dict_type = 'infra_api_error_log_process_status';

-- ============================================
-- system_sms_channel_code (短信渠道)
-- ============================================
UPDATE system_dict_data SET label = 'Aliyun' WHERE id = 66 AND dict_type = 'system_sms_channel_code';
UPDATE system_dict_data SET label = 'Debug (DingTalk)' WHERE id = 77 AND dict_type = 'system_sms_channel_code';

-- ============================================
-- system_sms_template_type (短信模板类型)
-- ============================================
UPDATE system_dict_data SET label = 'Verification Code' WHERE id = 67 AND dict_type = 'system_sms_template_type';
UPDATE system_dict_data SET label = 'Notification' WHERE id = 68 AND dict_type = 'system_sms_template_type';
UPDATE system_dict_data SET label = 'Marketing' WHERE id = 69 AND dict_type = 'system_sms_template_type';

-- ============================================
-- system_sms_send_status (短信发送状态)
-- ============================================
UPDATE system_dict_data SET label = 'Initializing' WHERE id = 70 AND dict_type = 'system_sms_send_status';
UPDATE system_dict_data SET label = 'Sent' WHERE id = 71 AND dict_type = 'system_sms_send_status';
UPDATE system_dict_data SET label = 'Failed' WHERE id = 72 AND dict_type = 'system_sms_send_status';
UPDATE system_dict_data SET label = 'Not Sent' WHERE id = 73 AND dict_type = 'system_sms_send_status';

-- ============================================
-- system_sms_receive_status (短信接收状态)
-- ============================================
UPDATE system_dict_data SET label = 'Waiting' WHERE id = 74 AND dict_type = 'system_sms_receive_status';
UPDATE system_dict_data SET label = 'Received' WHERE id = 75 AND dict_type = 'system_sms_receive_status';
UPDATE system_dict_data SET label = 'Failed' WHERE id = 76 AND dict_type = 'system_sms_receive_status';

-- ============================================
-- system_login_type (登录类型)
-- ============================================
UPDATE system_dict_data SET label = 'Account Login' WHERE id = 80 AND dict_type = 'system_login_type';
UPDATE system_dict_data SET label = 'Social Login' WHERE id = 81 AND dict_type = 'system_login_type';
UPDATE system_dict_data SET label = 'Logout' WHERE id = 83 AND dict_type = 'system_login_type';
UPDATE system_dict_data SET label = 'Force Logout' WHERE id = 85 AND dict_type = 'system_login_type';

-- ============================================
-- bpm_oa_leave_type (OA请假类型)
-- ============================================
UPDATE system_dict_data SET label = 'Sick Leave' WHERE id = 86 AND dict_type = 'bpm_oa_leave_type';
UPDATE system_dict_data SET label = 'Personal Leave' WHERE id = 87 AND dict_type = 'bpm_oa_leave_type';
UPDATE system_dict_data SET label = 'Marriage Leave' WHERE id = 88 AND dict_type = 'bpm_oa_leave_type';

-- ============================================
-- pay_channel_code (支付渠道)
-- ============================================
UPDATE system_dict_data SET label = 'WeChat WAP Payment' WHERE id = 112 AND dict_type = 'pay_channel_code';
UPDATE system_dict_data SET label = 'WeChat Official Account Payment' WHERE id = 113 AND dict_type = 'pay_channel_code';
UPDATE system_dict_data SET label = 'WeChat Mini Program Payment' WHERE id = 114 AND dict_type = 'pay_channel_code';
UPDATE system_dict_data SET label = 'WeChat App Payment' WHERE id = 115 AND dict_type = 'pay_channel_code';
UPDATE system_dict_data SET label = 'Alipay PC Payment' WHERE id = 116 AND dict_type = 'pay_channel_code';
UPDATE system_dict_data SET label = 'Alipay WAP Payment' WHERE id = 117 AND dict_type = 'pay_channel_code';
UPDATE system_dict_data SET label = 'Alipay App Payment' WHERE id = 118 AND dict_type = 'pay_channel_code';
UPDATE system_dict_data SET label = 'Alipay QR Code Payment' WHERE id = 119 AND dict_type = 'pay_channel_code';

-- ============================================
-- pay_notify_status (支付通知状态)
-- ============================================
UPDATE system_dict_data SET label = 'Notified' WHERE id = 120 AND dict_type = 'pay_notify_status';
UPDATE system_dict_data SET label = 'Notification Failed' WHERE id = 121 AND dict_type = 'pay_notify_status';
UPDATE system_dict_data SET label = 'Waiting' WHERE id = 122 AND dict_type = 'pay_notify_status';

-- ============================================
-- pay_order_status (支付订单状态)
-- ============================================
UPDATE system_dict_data SET label = 'Paid' WHERE id = 123 AND dict_type = 'pay_order_status';
UPDATE system_dict_data SET label = 'Closed' WHERE id = 124 AND dict_type = 'pay_order_status';
UPDATE system_dict_data SET label = 'Pending Payment' WHERE id = 125 AND dict_type = 'pay_order_status';

-- ============================================
-- pay_refund_status (退款状态)
-- ============================================
UPDATE system_dict_data SET label = 'Refunding' WHERE id = 1118 AND dict_type = 'pay_refund_status';
UPDATE system_dict_data SET label = 'Refund Failed' WHERE id = 1119 AND dict_type = 'pay_refund_status';
UPDATE system_dict_data SET label = 'Refunded' WHERE id = 1124 AND dict_type = 'pay_refund_status';

-- ============================================
-- bpm_process_instance_status (流程状态)
-- ============================================
UPDATE system_dict_data SET label = 'Pending' WHERE id = 1127 AND dict_type = 'bpm_process_instance_status';
-- Note: '审批通过' translation not found in ruoyi-vue-pro.sql, may need manual addition

-- ============================================
-- promotion_banner_position (Banner位置)
-- ============================================
UPDATE system_dict_data SET label = 'Home' WHERE id = 600 AND dict_type = 'promotion_banner_position';
UPDATE system_dict_data SET label = 'Seckill Page' WHERE id = 601 AND dict_type = 'promotion_banner_position';
UPDATE system_dict_data SET label = 'Bargain Page' WHERE id = 602 AND dict_type = 'promotion_banner_position';
UPDATE system_dict_data SET label = 'Discount Page' WHERE id = 603 AND dict_type = 'promotion_banner_position';
UPDATE system_dict_data SET label = 'Campaign Page' WHERE id = 604 AND dict_type = 'promotion_banner_position';

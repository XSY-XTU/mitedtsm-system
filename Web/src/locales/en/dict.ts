export default {
  // ==================== 通用类型 ====================
  // 用户类型
  user_type: {
    '1': 'Member',
    '2': 'Admin'
  },
  // 通用状态
  common_status: {
    '0': 'Enabled',
    '1': 'Disabled'
  },
  // 终端
  terminal: {
    '10': 'WeChat Mini Program',
    '11': 'WeChat Official Account',
    '20': 'H5 Web',
    '31': 'iOS App',
    '32': 'Android App'
  },
  // 时间间隔
  date_interval: {
    '1': 'Day',
    '2': 'Week',
    '3': 'Month',
    '4': 'Quarter',
    '5': 'Year'
  },

  // ==================== SYSTEM 模块 ====================
  // 用户性别
  system_user_sex: {
    '1': 'Male',
    '2': 'Female'
  },
  // 菜单类型
  system_menu_type: {
    '1': 'Directory',
    '2': 'Menu',
    '3': 'Button'
  },
  // 角色类型
  system_role_type: {
    '1': 'Built-in',
    '2': 'Custom'
  },
  // 数据权限范围
  system_data_scope: {
    '1': 'All Data Permission',
    '2': 'Specified Department Data Permission',
    '3': 'Department Data Permission',
    '4': 'Department and Below Data Permission',
    '5': 'Self Data Permission Only'
  },
  // 通知类型
  system_notice_type: {
    '1': 'Notice',
    '2': 'Announcement'
  },
  // 登录类型
  system_login_type: {
    '100': 'Account Login',
    '101': 'Social Login',
    '103': 'SMS Login',
    '200': 'Active Logout',
    '202': 'Force Logout'
  },
  // 登录结果
  system_login_result: {
    '0': 'Success',
    '10': 'Incorrect Account or Password',
    '20': 'User Disabled',
    '30': 'Verification Code Not Found',
    '31': 'Incorrect Verification Code',
    '100': 'Unknown Error'
  },
  // 短信渠道编码
  system_sms_channel_code: {
    'ALIYUN': 'Aliyun',
    'DEBUG_DING_TALK': 'Debug (DingTalk)'
  },
  // 短信模板类型
  system_sms_template_type: {
    '1': 'Verification Code',
    '2': 'Notification',
    '3': 'Marketing'
  },
  // 短信发送状态
  system_sms_send_status: {
    '0': 'Initializing',
    '10': 'Send Success',
    '20': 'Send Failed',
    '30': 'Not Send'
  },
  // 短信接收状态
  system_sms_receive_status: {
    '0': 'Waiting Result',
    '10': 'Receive Success',
    '20': 'Receive Failed'
  },
  // OAuth2 授权类型
  system_oauth2_grant_type: {
    'password': 'Password',
    'authorization_code': 'Authorization Code',
    'implicit': 'Implicit',
    'client_credentials': 'Client Credentials',
    'refresh_token': 'Refresh Token'
  },
  // 邮件发送状态
  system_mail_send_status: {
    '0': 'Initializing',
    '10': 'Send Success',
    '20': 'Send Failed',
    '30': 'Not Send'
  },
  // 站内信模板类型
  system_notify_template_type: {
    '1': 'Notice',
    '2': 'System Message'
  },
  // 社交类型
  system_social_type: {
    '10': 'Gitee',
    '20': 'DingTalk',
    '30': 'WeCom',
    '31': 'WeChat Official Account',
    '32': 'WeChat Open Platform',
    '34': 'WeChat Mini Program',
    '40': 'Alipay Mini Program'
  },

  // ==================== INFRA 模块 ====================
  // 布尔值字符串
  infra_boolean_string: {
    'true': 'Yes',
    'false': 'No'
  },
  // 任务状态
  infra_job_status: {
    '0': 'Initializing',
    '1': 'Normal',
    '2': 'Paused'
  },
  // 任务日志状态
  infra_job_log_status: {
    '0': 'Running',
    '1': 'Success',
    '2': 'Failed'
  },
  // API 错误日志处理状态
  infra_api_error_log_process_status: {
    '0': 'Unhandled',
    '1': 'Handled',
    '2': 'Ignored'
  },
  // 参数配置类型
  infra_config_type: {
    '1': 'System Built-in',
    '2': 'Custom'
  },
  // 代码生成模板类型
  infra_codegen_template_type: {
    '1': 'Single Table (CRUD)',
    '2': 'Tree Table (CRUD)'
  },
  // 代码生成前端类型
  infra_codegen_front_type: {
    '10': 'Vue2 Element UI Standard Template',
    '20': 'Vue3 Element Plus Standard Template',
    '30': 'Vben2.0 Ant Design Schema Template'
  },
  // 代码生成场景
  infra_codegen_scene: {
    '1': 'Admin Backend',
    '2': 'User APP'
  },
  // 文件存储类型
  infra_file_storage: {
    '1': 'Database',
    '10': 'Local Disk',
    '11': 'FTP Server',
    '12': 'SFTP Server',
    '20': 'S3 Object Storage'
  },
  // 操作类型
  infra_operate_type: {
    '0': 'Other',
    '1': 'Query',
    '2': 'Create',
    '3': 'Update',
    '4': 'Delete',
    '5': 'Export',
    '6': 'Import'
  },

  // ==================== BPM 模块 ====================
  // 流程模型类型
  bpm_model_type: {
    '10': 'BPMN Designer',
    '20': 'SIMPLE Designer'
  },
  // 流程表单类型
  bpm_model_form_type: {
    '10': 'Process Form',
    '20': 'Business Form'
  },
  // 任务候选人策略
  bpm_task_candidate_strategy: {
    '10': 'Role',
    '20': 'Department Members',
    '21': 'Department Manager',
    '22': 'Position',
    '30': 'User',
    '35': 'Initiator Selection',
    '40': 'User Group',
    '60': 'Process Expression'
  },
  // 流程实例状态
  bpm_process_instance_status: {
    '1': 'In Progress',
    '2': 'Approved',
    '3': 'Rejected',
    '4': 'Cancelled'
  },
  // 任务状态
  bpm_task_status: {
    '0': 'Pending',
    '1': 'In Progress',
    '2': 'Approved',
    '3': 'Rejected',
    '4': 'Cancelled',
    '5': 'Returned',
    '6': 'Delegated',
    '7': 'Approving'
  },
  // OA 请假类型
  bpm_oa_leave_type: {
    '1': 'Sick Leave',
    '2': 'Personal Leave',
    '3': 'Marriage Leave'
  },
  // 流程监听器类型
  bpm_process_listener_type: {
    'execution': 'Execution Listener',
    'task': 'Task Listener'
  },
  // 流程监听器值类型
  bpm_process_listener_value_type: {
    'class': 'Java Class',
    'expression': 'Expression',
    'delegateExpression': 'Delegate Expression'
  },

  // ==================== PAY 模块 ====================
  // 支付渠道编码
  pay_channel_code: {
    'wx_wap': 'WeChat WAP Payment',
    'wx_pub': 'WeChat Public Account Payment',
    'wx_lite': 'WeChat Mini Program Payment',
    'wx_app': 'WeChat App Payment',
    'wx_native': 'WeChat QR Code Payment',
    'wx_bar': 'WeChat Barcode Payment',
    'alipay_pc': 'Alipay PC Payment',
    'alipay_wap': 'Alipay WAP Payment',
    'alipay_app': 'Alipay App Payment',
    'alipay_qr': 'Alipay QR Code Payment',
    'alipay_bar': 'Alipay Barcode Payment',
    'mock': 'Mock Payment',
    'wallet': 'Wallet Payment'
  },
  // 支付订单状态
  pay_order_status: {
    '0': 'Waiting Payment',
    '10': 'Payment Success',
    '20': 'Refunded',
    '30': 'Payment Closed'
  },
  // 退款状态
  pay_refund_status: {
    '0': 'Waiting Refund',
    '10': 'Refund Success',
    '20': 'Refund Failed'
  },
  // 通知状态
  pay_notify_status: {
    '0': 'Waiting Notify',
    '10': 'Notify Success',
    '20': 'Notify Failed',
    '21': 'Request Success but Result Failed',
    '22': 'Request Failed'
  },
  // 通知类型
  pay_notify_type: {
    '1': 'Payment Order',
    '2': 'Refund Order'
  },
  // 转账状态
  pay_transfer_status: {
    '0': 'Waiting Transfer',
    '5': 'Transfer In Progress',
    '10': 'Transfer Success',
    '20': 'Transfer Failed'
  },

  // ==================== MP 模块 ====================
  // 自动回复请求匹配模式
  mp_auto_reply_request_match: {
    '1': 'Exact Match',
    '2': 'Partial Match'
  },
  // 消息类型
  mp_message_type: {
    'text': 'Text',
    'image': 'Image',
    'voice': 'Voice',
    'video': 'Video',
    'shortvideo': 'Short Video',
    'news': 'News',
    'music': 'Music',
    'location': 'Location',
    'link': 'Link',
    'event': 'Event'
  },

  // ==================== MEMBER 模块 ====================
  // 积分业务类型
  member_point_biz_type: {
    '1': 'Check-in',
    '2': 'Admin Adjustment',
    '11': 'Order Points Deduction',
    '12': 'Order Points Deduction (Full Cancel)',
    '13': 'Order Points Deduction (Single Refund)',
    '21': 'Order Points Reward',
    '22': 'Order Points Reward (Full Cancel)',
    '23': 'Order Points Reward (Single Refund)'
  },
  // 经验业务类型
  member_experience_biz_type: {
    '0': 'Admin Adjustment',
    '1': 'Invitation Reward',
    '4': 'Check-in Reward',
    '5': 'Lottery Reward',
    '11': 'Order Reward',
    '12': 'Order Reward (Full Cancel)',
    '13': 'Order Reward (Single Refund)'
  },

  // ==================== MALL 商品模块 ====================
  // 商品 SPU 状态
  product_spu_status: {
    '-1': 'Recycle Bin',
    '0': 'In Warehouse',
    '1': 'On Sale'
  },

  // ==================== MALL 交易模块 ====================
  // 快递计费方式
  trade_delivery_express_charge_mode: {
    '1': 'By Piece',
    '2': 'By Weight',
    '3': 'By Volume'
  },
  // 售后状态
  trade_after_sale_status: {
    '10': 'Applied',
    '20': 'Waiting for Return',
    '30': 'Waiting for Receipt',
    '40': 'Waiting for Refund',
    '50': 'Refund Success',
    '61': 'Buyer Cancelled',
    '62': 'Seller Rejected',
    '63': 'Seller Rejected Goods'
  },
  // 售后方式
  trade_after_sale_way: {
    '10': 'Refund Only',
    '20': 'Return and Refund'
  },
  // 售后类型
  trade_after_sale_type: {
    '10': 'During Sale Refund',
    '20': 'After Sale Refund'
  },
  // 订单类型
  trade_order_type: {
    '0': 'Normal Order',
    '1': 'Seckill Order',
    '2': 'Bargain Order',
    '3': 'Group Buy Order'
  },
  // 订单状态
  trade_order_status: {
    '0': 'Pending Payment',
    '10': 'Pending Shipment',
    '20': 'Shipped',
    '30': 'Completed',
    '40': 'Cancelled'
  },
  // 订单项售后状态
  trade_order_item_after_sale_status: {
    '0': 'No After Sale',
    '10': 'After Sale In Progress',
    '20': 'Refunded'
  },
  // 配送方式
  trade_delivery_type: {
    '1': 'Express Delivery',
    '2': 'Self Pickup'
  },
  // 分佣模式
  brokerage_enabled_condition: {
    '1': 'Everyone Distribution',
    '2': 'Specified Distribution'
  },
  // 分销绑定模式
  brokerage_bind_mode: {
    '1': 'First Bind',
    '2': 'Register Bind',
    '3': 'Override Bind'
  },
  // 佣金提现银行
  brokerage_bank_name: {
    '0': 'ICBC',
    '1': 'CCB',
    '2': 'ABC',
    '3': 'BOC',
    '4': 'BCM',
    '5': 'CMB'
  },
  // 佣金提现类型
  brokerage_withdraw_type: {
    '1': 'Wallet',
    '2': 'Bank Card',
    '3': 'WeChat QR Code',
    '4': 'Alipay QR Code',
    '5': 'WeChat Change'
  },
  // 佣金业务类型
  brokerage_record_biz_type: {
    '1': 'Order Commission',
    '2': 'Withdrawal Request',
    '3': 'Withdrawal Rejected'
  },
  // 佣金状态
  brokerage_record_status: {
    '0': 'Pending Settlement',
    '1': 'Settled',
    '2': 'Cancelled'
  },
  // 佣金提现状态
  brokerage_withdraw_status: {
    '0': 'Under Review',
    '10': 'Review Passed',
    '11': 'Withdrawal Success',
    '20': 'Review Rejected',
    '21': 'Withdrawal Failed'
  },

  // ==================== MALL 营销模块 ====================
  // 优惠类型
  promotion_discount_type: {
    '1': 'Money Off',
    '2': 'Discount'
  },
  // 商品范围
  promotion_product_scope: {
    '1': 'All Products',
    '2': 'Specific Products',
    '3': 'Category Products'
  },
  // 优惠券有效期类型
  promotion_coupon_template_validity_type: {
    '1': 'Fixed Date',
    '2': 'After Receive'
  },
  // 优惠券状态
  promotion_coupon_status: {
    '1': 'Unused',
    '2': 'Used',
    '3': 'Expired'
  },
  // 优惠券领取方式
  promotion_coupon_take_type: {
    '1': 'Direct Receive',
    '2': 'Specified Issue',
    '3': 'New User Coupon'
  },
  // 营销条件类型
  promotion_condition_type: {
    '10': 'Money Off (N Yuan)',
    '20': 'Quantity Off (N Items)'
  },
  // 砍价记录状态
  promotion_bargain_record_status: {
    '1': 'Bargaining',
    '2': 'Bargain Success',
    '3': 'Bargain Failed'
  },
  // 拼团记录状态
  promotion_combination_record_status: {
    '0': 'In Progress',
    '1': 'Success',
    '2': 'Failed'
  },
  // Banner 位置
  promotion_banner_position: {
    '1': 'Home Page',
    '2': 'Seckill Page',
    '3': 'Bargain Page',
    '4': 'Flash Sale Page',
    '5': 'Full Reduction Page'
  },

  // ==================== CRM 模块 ====================
  // 审批状态
  crm_audit_status: {
    '0': 'Not Submitted',
    '10': 'Under Review',
    '20': 'Approved',
    '30': 'Rejected',
    '40': 'Cancelled'
  },
  // 业务类型
  crm_biz_type: {
    '1': 'Leads',
    '2': 'Customer',
    '3': 'Contact',
    '4': 'Business',
    '5': 'Contract',
    '6': 'Product',
    '7': 'Receivable',
    '8': 'Clue'
  },
  // 商机结束状态类型
  crm_business_end_status_type: {
    '1': 'Win',
    '2': 'Lose',
    '3': 'Invalid'
  },
  // 回款方式
  crm_receivable_return_type: {
    '1': 'Check',
    '2': 'Cash',
    '3': 'Postal Remittance',
    '4': 'Wire Transfer',
    '5': 'Online Transfer',
    '6': 'Alipay',
    '7': 'WeChat Pay',
    '8': 'Other'
  },
  // 客户行业
  crm_customer_industry: {
    '1': 'IT',
    '2': 'Finance',
    '3': 'Real Estate',
    '4': 'Business Service',
    '5': 'Transportation/Logistics',
    '6': 'Manufacturing',
    '7': 'Government',
    '8': 'Media & Culture'
  },
  // 客户级别
  crm_customer_level: {
    '1': 'A (Key Customer)',
    '2': 'B (Regular Customer)',
    '3': 'C (Low Priority Customer)'
  },
  // 客户来源
  crm_customer_source: {
    '1': 'Promotion',
    '2': 'Search Engine',
    '3': 'Advertisement',
    '4': 'Referral',
    '5': 'Online Registration',
    '6': 'Online Consultation',
    '7': 'Appointment Visit',
    '8': 'Cold Visit',
    '9': 'Phone Consultation',
    '10': 'Email Consultation'
  },
  // 产品状态
  crm_product_status: {
    '0': 'Off Shelf',
    '1': 'On Shelf'
  },
  // 权限级别
  crm_permission_level: {
    '1': 'Owner',
    '2': 'Read Only',
    '3': 'Read/Write'
  },
  // 产品单位
  crm_product_unit: {
    '1': 'Piece',
    '2': 'Block',
    '3': 'Unit',
    '4': 'Handle',
    '5': 'Piece',
    '6': 'Bottle',
    '7': 'Box',
    '8': 'Set',
    '9': 'Ton',
    '10': 'Kilogram',
    '11': 'Meter',
    '12': 'Case',
    '13': 'Set'
  },
  // 跟进方式
  crm_follow_up_type: {
    '1': 'Phone Call',
    '2': 'SMS',
    '3': 'Visit',
    '4': 'WeChat'
  },

  // ==================== ERP 模块 ====================
  // 审批状态
  erp_audit_status: {
    '10': 'Not Audited',
    '20': 'Audited'
  },
  // 库存记录业务类型
  erp_stock_record_biz_type: {
    '10': 'Other Inbound',
    '11': 'Other Inbound (Void)',
    '20': 'Other Outbound',
    '21': 'Other Outbound (Void)',
    '30': 'Transfer Inbound',
    '31': 'Transfer Inbound (Void)',
    '32': 'Transfer Outbound',
    '33': 'Transfer Outbound (Void)',
    '40': 'Inventory Gain',
    '41': 'Inventory Gain (Void)',
    '42': 'Inventory Loss',
    '43': 'Inventory Loss (Void)',
    '50': 'Sales Outbound',
    '51': 'Sales Outbound (Void)',
    '60': 'Sales Return Inbound',
    '61': 'Sales Return Inbound (Void)',
    '70': 'Purchase Inbound',
    '71': 'Purchase Inbound (Void)',
    '80': 'Purchase Return Outbound',
    '81': 'Purchase Return Outbound (Void)'
  },

  // ==================== AI 模块 ====================
  // AI 平台
  ai_platform: {
    'OpenAI': 'OpenAI',
    'Ollama': 'Ollama',
    'YiYan': 'Baidu Ernie',
    'XingHuo': 'iFlytek Spark',
    'TongYi': 'Alibaba TongYi',
    'StableDiffusion': 'Stable Diffusion',
    'Midjourney': 'Midjourney',
    'Suno': 'Suno',
    'DeepSeek': 'DeepSeek',
    'ZhiPu': 'ZhiPu',
    'AzureOpenAI': 'Azure OpenAI',
    'DouBao': 'ByteDance Doubao',
    'HunYuan': 'Tencent HunYuan',
    'SiliconFlow': 'SiliconFlow',
    'MiniMax': 'MiniMax',
    'Moonshot': 'Moonshot'
  },
  // AI 模型类型
  ai_model_type: {
    '1': 'Chat',
    '2': 'Image',
    '3': 'Audio',
    '4': 'Video',
    '5': 'Embedding',
    '6': 'Rerank'
  },
  // AI 图片状态
  ai_image_status: {
    '10': 'In Progress',
    '20': 'Completed',
    '30': 'Failed'
  },
  // AI 音乐状态
  ai_music_status: {
    '10': 'In Progress',
    '20': 'Completed',
    '30': 'Failed'
  },
  // AI 生成模式
  ai_generate_mode: {
    '1': 'Lyrics Mode',
    '2': 'Description Mode'
  },
  // AI 写作类型
  ai_write_type: {
    '1': 'Write',
    '2': 'Reply'
  },
  // AI 写作长度
  ai_write_length: {
    '1': 'Auto',
    '2': 'Short',
    '3': 'Medium',
    '4': 'Long'
  },
  // AI 写作格式
  ai_write_format: {
    '1': 'Auto',
    '2': 'Email',
    '3': 'Message',
    '4': 'Comment',
    '5': 'Paragraph',
    '6': 'Article',
    '7': 'Blog Post',
    '8': 'Idea',
    '9': 'Outline'
  },
  // AI 写作语气
  ai_write_tone: {
    '1': 'Auto',
    '2': 'Friendly',
    '3': 'Casual',
    '4': 'Warm',
    '5': 'Professional',
    '6': 'Witty',
    '7': 'Funny',
    '8': 'Formal'
  },
  // AI 写作语言
  ai_write_language: {
    '1': 'Auto',
    '2': 'Chinese',
    '3': 'English',
    '4': 'Korean',
    '5': 'Japanese'
  },

  // ==================== IOT 模块 ====================
  // 联网方式
  iot_net_type: {
    '0': 'Wi-Fi',
    '1': 'Mobile Network',
    '2': 'Ethernet',
    '3': 'Other'
  },
  // 产品状态
  iot_product_status: {
    '0': 'Development',
    '1': 'Published'
  },
  // 产品设备类型
  iot_product_device_type: {
    '0': 'Direct Device',
    '1': 'Gateway Sub Device',
    '2': 'Gateway Device'
  },
  // 设备状态
  iot_device_state: {
    '0': 'Inactive',
    '1': 'Online',
    '2': 'Offline'
  },
  // 物模型类型
  iot_thing_model_type: {
    '1': 'Property',
    '2': 'Service',
    '3': 'Event'
  },
  // 读写类型
  iot_rw_type: {
    'r': 'Read',
    'w': 'Write',
    'rw': 'Read/Write'
  }
}
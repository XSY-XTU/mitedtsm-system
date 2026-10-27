# (1).我想新增一个功能
    1. 改初始化SQL文件
        -- 在【system_menu】表里面 按照层级 插入数据（1条）
        -- 在【system_menu_i18n】表里面，插入对应的国际化翻译（目前支持zh-CN和en，共2条）
        -- 请在【database/new】下面新建 初始化SQL文件
        -- 示例：
            INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
            VALUES (3000, '新功能名称', '', 1, 1, 0, '/new-feature', 'ep:document', 'newFeature/index', 'NewFeature', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');
            
            INSERT INTO `system_menu_i18n` (`menu_id`, `language`, `name`) VALUES (3000, 'zh-CN', '新功能名称');
            INSERT INTO `system_menu_i18n` (`menu_id`, `language`, `name`) VALUES (3000, 'en', 'New Feature');
    2. 新增你的前端代码（管理端网页）
        -- 在【Web/src/views】里面，按照功能层级 找到对应的文件夹，创建你自己的页面
        -- 在【Web/src/api】里面，按照功能层级 找到对应的文件夹，创建你的接口文件
        -- 目录对应关系：
            --- 【views/system】对应系统管理（用户、角色、菜单、部门等）
            --- 【views/infra】对应基础设施（文件、任务、代码生成等）
            --- 【views/mall】对应商城模块（商品、订单、营销等）
            --- 【views/member】对应会员模块
            --- 【views/pay】对应支付模块
            --- 【views/bpm】对应工作流模块
            --- 【views/crm】对应客户管理模块
            --- 【views/erp】对应企业资源模块
            --- 【views/ai】对应AI模块
            --- 【views/iot】对应物联网模块
            --- 【views/mp】对应微信公众号模块
            --- 【views/report】对应报表模块
    3. 新增你的前端代码（商城前端）
        -- 在【MallFrontend/pages】里面，按照功能创建页面
        -- 在【MallFrontend/sheep/api】里面，创建接口文件
        -- 目录对应关系：
            --- 【pages/goods】商品相关页面
            --- 【pages/order】订单相关页面
            --- 【pages/user】用户中心页面
            --- 【pages/pay】支付相关页面
            --- 【pages/activity】活动相关页面（拼团、秒杀等）
            --- 【pages/coupon】优惠券相关页面
            --- 【pages/commission】分销相关页面
            --- 【pages/chat】客服聊天页面
        -- 如果新增了菜单项，记得在【promotion_diy_menu_i18n】表里添加国际化数据
    4. 新增你的后端代码
        -- 在【Server】里面，找到你要修改的功能模块，然后进去修改具体业务代码
        -- 模块对应关系：
            --- 【mitedtsm-module-system】系统管理（用户、角色、权限、OAuth2、租户）
            --- 【mitedtsm-module-infra】基础设施（文件、代码生成、定时任务、接口文档）
            --- 【mitedtsm-module-member】会员管理
            --- 【mitedtsm-module-mall】商城管理（商品、营销、交易、统计）
            --- 【mitedtsm-module-pay】支付管理
            --- 【mitedtsm-module-bpm】工作流管理
            --- 【mitedtsm-module-crm】客户关系管理
            --- 【mitedtsm-module-erp】企业资源管理
            --- 【mitedtsm-module-ai】AI大模型管理
            --- 【mitedtsm-module-iot】物联网管理
            --- 【mitedtsm-module-mp】微信公众号管理
            --- 【mitedtsm-module-report】报表管理
        -- 在【Server/mitedtsm-server/src/main/resources】里面，修改项目配置文件
        -- 在【Server/mitedtsm-dependencies】里面，修改项目依赖的版本
        -- 在【Server/mitedtsm-framework】里面，修改项目依赖、属性类、配置类
        -- 注：如果你要建表，除了创建DB类之外，还要注意是否需要租户区分，不需要的话请加上 @TenantIgnore 或继承 BaseDO 而不是 TenantBaseDO

# (2).我想修改一个功能
    1. 修改菜单/权限
        -- 在【system_menu】表里找到对应的菜单记录，修改字段
        -- 同时在【system_menu_i18n】表里修改对应的国际化名称
        -- 如果是修改权限标识（permission字段），记得同步修改后端 @PreAuthorize 注解
    2. 修改后端业务逻辑
        -- 找到对应模块的 Service 实现类进行修改
        -- 一般路径：【Server/mitedtsm-module-xxx/mitedtsm-module-xxx-biz/src/main/java/.../service/】
        -- 如果涉及数据库字段变更：
            --- 修改对应的 DO 类（实体类）
            --- 修改对应的 Mapper.xml（如果用了XML写SQL）
            --- 新增字段要在数据库表里同步添加
            --- 在【database/new】下新建增量SQL文件记录变更
    3. 修改前端页面
        -- Web端：在【Web/src/views】找到对应页面修改
        -- MallFrontend端：在【MallFrontend/pages】找到对应页面修改
        -- 如果接口参数变了，记得同步修改【api】目录下的接口文件
    4. 修改国际化文本
        -- Web端：修改【Web/src/locales/zh-CN】和【Web/src/locales/en】下对应的ts文件
        -- MallFrontend端：修改【MallFrontend/locale/zh-CN】和【MallFrontend/locale/en】下对应的ts文件
        -- Server端：修改【Server/mitedtsm-server/src/main/resources/i18n】下的properties文件
        -- 菜单国际化：修改【system_menu_i18n】表
        -- 商城菜单国际化：修改【promotion_diy_menu_i18n】表
    5. 修改配置
        -- 应用配置：【Server/mitedtsm-server/src/main/resources/application.yaml】
        -- 本地开发配置：【Server/mitedtsm-server/src/main/resources/application-local.yaml】
        -- Docker部署配置：【Server/mitedtsm-server/src/main/resources/application-docker.yaml】

# (3).我想在国际化这里新增一门语言
    1. 改初始化SQL文件
        -- 侧边栏菜单国际化，对应【system_menu_i18n】表
        -- 商城用户端菜单国际化，对应【promotion_diy_menu_i18n】表
        -- 请在【database/new】下面新建 初始化SQL文件
        -- 示例（新增日语）：
            INSERT INTO `system_menu_i18n` (`menu_id`, `language`, `name`) VALUES (1, 'ja', 'システム管理');
            INSERT INTO `system_menu_i18n` (`menu_id`, `language`, `name`) VALUES (2, 'ja', 'インフラ');
    2. 改 Web 前端代码：
        -- 在【Web/src/locales】下面，新增语言包文件夹（如 ja）
        -- 复制【Web/src/locales/en】下的所有文件到新文件夹
        -- 翻译所有文件中的文本
        -- 在【Web/src/store/modules/locale.ts】里面，新增语言选项：
            {
              lang: 'ja',
              name: '日本語'
            }
        -- 同时在 elLocaleMap 对象里添加 Element Plus 的语言包
    3. 改 MallFrontend 代码：
        -- 在【MallFrontend/locale】下面，新增语言包文件夹（如 ja）
        -- 复制【MallFrontend/locale/en】下的所有文件到新文件夹
        -- 翻译所有文件中的文本
        -- 在【MallFrontend/locale/index.ts】里面，导入并添加新语言
        -- 在【MallFrontend/sheep/i18n/index.ts】的 getLocaleList 里添加新语言选项
    4. 改 Server 后端代码：
        -- 在【Server/mitedtsm-server/src/main/resources/i18n】下面，新建 messages_ja.properties
        -- 复制 messages_en.properties 内容并翻译

# (4). 各类功能的实现逻辑：
    1. 登录
        -- 登录入口：
            --- 管理后台：【mitedtsm-module-system】下的 AuthController.login()
            --- 会员端：【mitedtsm-module-member】下的 AppAuthController.login()
        -- 登录方式：
            --- 账号密码登录：POST /login
            --- 短信验证码登录：POST /sms-login
            --- 社交账号登录：POST /social-login（微信、企业微信、钉钉等）
            --- 微信小程序登录：POST /weixin-mini-app-login
        -- 登录流程：
            --- 1. 校验验证码（滑块验证）
            --- 2. 验证账号密码
            --- 3. 创建 OAuth2 Token
            --- 4. 返回 Token 给前端
        -- Token 认证：
            --- 前端请求时在 Header 带上 Authorization: Bearer {token}
            --- 后端 TokenAuthenticationFilter 拦截请求，验证 Token 有效性
            --- 验证通过后，构建 LoginUser 存入 SecurityContext
        -- 关键代码位置：
            --- 【mitedtsm-module-system/.../controller/admin/auth/AuthController.java】
            --- 【mitedtsm-module-system/.../service/auth/AdminAuthServiceImpl.java】
            --- 【mitedtsm-framework/.../security/core/filter/TokenAuthenticationFilter.java】
    2. 租户区分
        -- 实现原理：
            --- 基于 ThreadLocal 存储当前请求的租户ID
            --- MyBatis Plus 拦截器自动在 SQL 中拼接 tenant_id 条件
        -- 实体类：
            --- 继承【TenantBaseDO】的实体自动支持租户隔离（包含 tenantId 字段）
            --- 继承【BaseDO】的实体不进行租户隔离
        -- 忽略租户过滤：
            --- 方法上加 @TenantIgnore 注解，该方法内的查询不拼接租户条件
            --- 使用 TenantUtils.executeIgnore(() -> { ... }) 代码块忽略租户
        -- 租户ID传递：
            --- 前端请求 Header 中带 tenant-id
            --- TenantContextWebFilter 从 Header 解析并设置到 ThreadLocal
        -- 关键代码位置：
            --- 【mitedtsm-framework/.../tenant/core/context/TenantContextHolder.java】
            --- 【mitedtsm-framework/.../tenant/core/aop/TenantIgnore.java】
            --- 【mitedtsm-framework/.../tenant/core/db/TenantDatabaseInterceptor.java】
            --- 【mitedtsm-framework/.../tenant/core/db/TenantBaseDO.java】
        -- 配置项（application.yaml）：
            mitedtsm:
              tenant:
                enable: true
                ignore-urls: [/jmreport/*]  # 忽略租户的URL
                ignore-tables: []            # 忽略租户的表
    3. Web的国际化
        -- 语言包位置：【Web/src/locales/{lang}/】
        -- 语言包结构：按模块分文件（如 system.ts, mall.ts, common.ts 等）
        -- 当前语言存储：localStorage 中的 CACHE_KEY.LANG
        -- 语言切换：
            --- 调用 useLocaleStore().setCurrentLocale({ lang: 'en' })
            --- 页面刷新后会从 localStorage 恢复语言设置
        -- 在代码中使用：
            --- 组合式API：const { t } = useI18n(); t('common.loading')
            --- 模板中：{{ t('common.loading') }}
        -- 新增翻译：
            --- 在【Web/src/locales/zh-CN/xxx.ts】添加中文
            --- 在【Web/src/locales/en/xxx.ts】添加英文
        -- 关键代码位置：
            --- 【Web/src/store/modules/locale.ts】语言状态管理
            --- 【Web/src/locales/】语言包目录
    4. Server的国际化
        -- 语言解析：
            --- 优先从请求参数 ?lang=en 获取
            --- 其次从请求头 Accept-Language 获取
        -- 语言包位置：【Server/mitedtsm-server/src/main/resources/i18n/】
        -- 语言包文件：
            --- messages_zh_CN.properties 中文
            --- messages_en.properties 英文
        -- 在代码中使用：
            --- I18nUtil.getMessage("login.success")
            --- I18nUtil.getMessage("validation.required", new Object[]{"用户名"})
            --- I18nUtil.getErrorMessage(401) 获取错误码对应的国际化消息
        -- 数据库级国际化：
            --- 菜单名称：system_menu_i18n 表
            --- 商城菜单：promotion_diy_menu_i18n 表
        -- 关键代码位置：
            --- 【mitedtsm-framework/.../common/util/i18n/I18nUtil.java】
            --- 【mitedtsm-framework/.../common/util/i18n/I18nLocaleResolver.java】
    5. MallFrontend的国际化
        -- 语言包位置：【MallFrontend/locale/{lang}/】
        -- 语言包结构：按模块分文件（如 goods.ts, order.ts, user.ts 等）
        -- 当前语言存储：uni.getStorageSync('locale')
        -- 语言切换：
            --- 调用 setLocale('en')
            --- 会同时更新 uni-app 的语言设置
        -- 在代码中使用：
            --- 组合式API：const { t } = useI18n('goods'); t('title') // 相当于 t('goods.title')
            --- 支持命名空间，简化调用
        -- 顶级命名空间（可直接访问）：
            --- common, login, user, menu, goods, cart, order, pay, address, coupon, error, activity, commission, chat, app
        -- 新增翻译：
            --- 在【MallFrontend/locale/zh-CN/xxx.ts】添加中文
            --- 在【MallFrontend/locale/en/xxx.ts】添加英文
            --- 如果是新的模块文件，需要在 index.ts 中导入并导出
        -- 关键代码位置：
            --- 【MallFrontend/sheep/i18n/index.ts】国际化配置和 useI18n Hook
            --- 【MallFrontend/locale/index.ts】语言包导出

# (5). 各类表的职责
    1.【system_】     #【ruoyi-vue-pro.sql】    // 系统、用户、租户……
        -- 【system_users】用户表：管理员账号信息
        -- 【system_role】角色表：角色定义
        -- 【system_menu】菜单表：菜单、按钮权限定义
        -- 【system_menu_i18n】菜单国际化表：菜单名称多语言
        -- 【system_dept】部门表：组织架构
        -- 【system_post】岗位表：岗位定义
        -- 【system_dict_type】字典类型表：字典分类
        -- 【system_dict_data】字典数据表：字典项
        -- 【system_config】系统配置表：系统参数配置
        -- 【system_notice】通知公告表：系统公告
        -- 【system_operate_log】操作日志表：用户操作记录
        -- 【system_login_log】登录日志表：登录记录
        -- 【system_oauth2_client】OAuth2客户端表：第三方应用接入
        -- 【system_oauth2_access_token】访问令牌表：Token记录
        -- 【system_oauth2_refresh_token】刷新令牌表
        -- 【system_social_user】社交用户表：第三方登录绑定
        -- 【system_tenant】租户表：多租户数据
        -- 【system_tenant_package】租户套餐表：租户功能包
        -- 【system_user_role】用户角色关联表
        -- 【system_user_post】用户岗位关联表
        -- 【system_role_menu】角色菜单关联表
        -- 【system_sms_channel】短信渠道表
        -- 【system_sms_template】短信模板表
        -- 【system_sms_log】短信日志表
        -- 【system_mail_account】邮箱账号表
        -- 【system_mail_template】邮件模板表
        -- 【system_mail_log】邮件日志表
        -- 【system_sensitive_word】敏感词表
        
    2.【infra_】      #【ruoyi-vue-pro.sql】    // 基础设施……
        -- 【infra_file】文件表：上传文件记录
        -- 【infra_file_config】文件配置表：存储配置（本地/七牛/阿里云等）
        -- 【infra_file_content】文件内容表：数据库存储方式时使用
        -- 【infra_config】参数配置表：业务参数
        -- 【infra_job】定时任务表：定时任务定义
        -- 【infra_job_log】定时任务日志表
        -- 【infra_api_access_log】API访问日志表：接口调用记录
        -- 【infra_api_error_log】API错误日志表：接口异常记录
        -- 【infra_codegen_table】代码生成表定义
        -- 【infra_codegen_column】代码生成字段定义
        -- 【infra_data_source_config】数据源配置表：多数据源支持
        -- 【infra_demo_student】示例学生表：演示用
        
    3.【member_】     #【member-2024-01-18.sql】    // 会员中心……
        -- 【member_user】会员用户表：商城用户信息
        -- 【member_level】会员等级表：等级定义
        -- 【member_level_record】会员等级记录表：等级变更记录
        -- 【member_group】会员分组表
        -- 【member_tag】会员标签表
        -- 【member_address】会员地址表：收货地址
        -- 【member_point_record】积分记录表
        -- 【member_point_rule】积分规则表
        -- 【member_signin_config】签到配置表
        -- 【member_signin_record】签到记录表
        -- 【member_experience_record】经验值记录表
        
    4.【mall_】       #【mall-2025-05-12.sql】    // 商城核心……
        -- 【mall_product_spu】商品SPU表：商品信息
        -- 【mall_product_sku】商品SKU表：商品规格
        -- 【mall_product_category】商品分类表
        -- 【mall_product_brand】商品品牌表
        -- 【mall_product_property】商品属性表
        -- 【mall_product_property_value】商品属性值表
        -- 【mall_product_comment】商品评价表
        -- 【mall_product_favorite】商品收藏表
        -- 【mall_product_browse_history】浏览记录表
        -- 【mall_promotion_banner】轮播图表
        -- 【mall_promotion_coupon】优惠券表
        -- 【mall_promotion_coupon_template】优惠券模板表
        -- 【mall_promotion_seckill_activity】秒杀活动表
        -- 【mall_promotion_combination_activity】拼团活动表
        -- 【mall_promotion_bargain_activity】砍价活动表
        -- 【mall_promotion_reward_activity】奖励活动表
        -- 【mall_promotion_diy_template】DIY模板表
        -- 【mall_promotion_diy_page】DIY页面表
        -- 【mall_promotion_diy_menu_i18n】商城菜单国际化表
        -- 【mall_trade_order】交易订单表
        -- 【mall_trade_order_item】订单项表
        -- 【mall_trade_cart】购物车表
        -- 【mall_trade_brokerage_record】佣金记录表
        -- 【mall_trade_brokerage_user】分销用户表
        -- 【mall_trade_after_sale】售后订单表
        -- 【mall_trade_after_sale_log】售后日志表
        -- 【mall_trade_delivery_express】快递公司表
        -- 【mall_trade_delivery_pick_up_store】自提门店表
        -- 【mall_statistics_product】商品统计表
        -- 【mall_statistics_member】会员统计表
        -- 【mall_statistics_payment】支付统计表
        -- 【mall_statistics_trade】交易统计表
        
    5.【pay_】        #【pay-2025-07-27.sql】    // 支付相关……
        -- 【pay_app】支付应用表：接入支付的应用
        -- 【pay_channel】支付渠道表：支付方式配置（微信/支付宝等）
        -- 【pay_order】支付订单表：支付单
        -- 【pay_order_extension】支付订单扩展表
        -- 【pay_refund】退款订单表
        -- 【pay_merchant】商户表：支付商户信息
        -- 【pay_notify_task】通知任务表：支付结果通知
        -- 【pay_notify_log】通知日志表
        -- 【pay_wallet】钱包表：会员钱包
        -- 【pay_wallet_recharge】钱包充值表
        -- 【pay_wallet_transaction】钱包流水表
        
    6.【bpm_】        #【bpm-2025-10-04.sql】    // 工作流……
        -- 【bpm_model】流程模型表：流程定义
        -- 【bpm_form】表单表：流程表单
        -- 【bpm_process_instance】流程实例表
        -- 【bpm_process_instance_ext】流程实例扩展表
        -- 【bpm_process_listener】流程监听器表
        -- 【bpm_task_ext】任务扩展表
        -- 【bpm_activity_ext】活动扩展表
        -- 【bpm_process_definition_info】流程定义信息表
        -- 【bpm_oa_leave】OA请假表：示例流程
        -- 【bpm_user_group】用户组表
        
    7.【crm_】        #【crm-2024-09-30.sql】    // 客户关系管理……
        -- 【crm_customer】客户表
        -- 【crm_customer_limit_config】客户限制配置表
        -- 【crm_customer_pool_config】公海规则配置表
        -- 【crm_contact】联系人表
        -- 【crm_business】商机表
        -- 【crm_business_status】商机状态表
        -- 【crm_business_status_type】商机状态类型表
        -- 【crm_contract】合同表
        -- 【crm_contract_config】合同配置表
        -- 【crm_clue】线索表
        -- 【crm_follow_up_record】跟进记录表
        -- 【crm_product】产品表
        -- 【crm_receivable】回款表
        -- 【crm_receivable_plan】回款计划表
        -- 【crm_permission】数据权限表
        -- 【crm_statistics_funnel】漏斗统计表
        
    8.【erp_】        #【erp-2024-05-03.sql】    // 企业资源……
        -- 【erp_stock】库存表
        -- 【erp_stock_in】入库单表
        -- 【erp_stock_in_item】入库单项表
        -- 【erp_stock_out】出库单表
        -- 【erp_stock_out_item】出库单项表
        -- 【erp_stock_move】调拨单表
        -- 【erp_stock_move_item】调拨单项表
        -- 【erp_stock_check】盘点单表
        -- 【erp_stock_check_item】盘点单项表
        -- 【erp_stock_record】库存记录表
        -- 【erp_stock_record_summary】库存汇总表
        -- 【erp_warehouse】仓库表
        -- 【erp_supplier】供应商表
        -- 【erp_customer】客户表（ERP侧）
        -- 【erp_product】产品表（ERP侧）
        -- 【erp_product_unit】产品单位表
        -- 【erp_product_category】产品分类表
        -- 【erp_account】结算账户表
        -- 【erp_purchase】采购订单表
        -- 【erp_purchase_item】采购订单项表
        -- 【erp_purchase_return】采购退货表
        -- 【erp_sale】销售订单表
        -- 【erp_sale_item】销售订单项表
        -- 【erp_sale_return】销售退货表
        -- 【erp_finance_payment】付款单表
        -- 【erp_finance_receipt】收款单表
        
    9.【ai_】         #【ai-2025-08-29.sql】    // AI大模型……
        -- 【ai_api_key】API密钥表：大模型API配置
        -- 【ai_chat_model】对话模型表：模型配置
        -- 【ai_chat_message】对话消息表：聊天记录
        -- 【ai_chat_conversation】对话会话表
        -- 【ai_chat_role】对话角色表：AI角色定义
        -- 【ai_image】图像表：AI生成的图像
        -- 【ai_write】写作表：AI写作记录
        -- 【ai_mind_map】思维导图表：AI生成思维导图
        -- 【ai_music】音乐表：AI生成音乐
        -- 【ai_knowledge】知识库表
        -- 【ai_knowledge_document】知识库文档表
        -- 【ai_knowledge_segment】知识库分段表
        -- 【ai_chat_message_image】消息图像关联表
        
    10.【iot_】       #【iot-2026-02-10.sql】    // 物联网……
        -- 【iot_product】产品表：设备产品定义
        -- 【iot_device】设备表：设备实例
        -- 【iot_device_group】设备分组表
        -- 【iot_device_data】设备数据表：上报数据
        -- 【iot_device_event】设备事件表
        -- 【iot_device_service】设备服务表：下发指令
        -- 【iot_device_property】设备属性表
        -- 【iot_rule_engine】规则引擎表
        -- 【iot_scene】场景表
        -- 【iot_thing_model】物模型表
        -- 【iot_topic】Topic表：消息主题
        -- 【iot_protocol】协议表
        -- 【iot_gateway】网关表
        
    11.【mp_】        #【mp-2025-12-07.sql】    // 微信公众号……
        -- 【mp_account】公众号账号表
        -- 【mp_menu】公众号菜单表
        -- 【mp_message】公众号消息表
        -- 【mp_auto_reply】自动回复表
        -- 【mp_tag】粉丝标签表
        -- 【mp_user】粉丝表
        -- 【mp_user_tag】粉丝标签关联表
        -- 【mp_material】素材表
        -- 【mp_draft】草稿表
        -- 【mp_free_publish】发布记录表
        
    12.【report_】    #【jimureport.mysql5.7.create.sql】    // 积木报表……
        -- 【jimureport_data_source】数据源表
        -- 【jimureport_data_source_field】数据源字段表
        -- 【jimureport_data_source_param】数据源参数表
        -- 【jimureport_db】报表数据库表
        -- 【jimureport_link】报表链接表
        -- 【jimureport_map】地图数据表
        -- 【jimureport_share】报表分享表
        -- 【jimureport_widget】组件表
        -- 【jimu_dict】报表字典表
        -- 【jimu_report】报表定义表
        -- 【jimu_report_db】报表数据源表
        -- 【jimu_report_link】报表链接表
        -- 【jimu_report_map】报表地图表
        -- 【jimu_report_share】报表分享表

# (6). 常见问题
    1. 新增的菜单不显示？
        -- 检查【system_menu】表的 parent_id 是否正确
        -- 检查【system_menu】表的 status 是否为 0（启用）
        -- 检查【system_menu】表的 visible 是否为 1（可见）
        -- 检查当前用户是否有该菜单的权限（角色菜单关联）
    2. 国际化不生效？
        -- 检查语言包文件是否正确导入
        -- 检查 key 是否正确（区分大小写）
        -- 检查是否重启了服务/刷新了页面
    3. 租户数据隔离不生效？
        -- 检查实体类是否继承了 TenantBaseDO
        -- 检查是否有 @TenantIgnore 注解
        -- 检查请求 Header 是否带了 tenant-id
    4. Token 失效？
        -- 检查 Redis 是否正常运行
        -- 检查 Token 是否过期（默认30天）
        -- 检查 Token 是否被手动删除（令牌管理）
    5. 文件上传失败？
        -- 检查【infra_file_config】表是否有主配置（master=1）
        -- 检查存储配置是否正确（accessKey、accessSecret等）
        -- 检查文件大小是否超过限制

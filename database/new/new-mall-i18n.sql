-- =============================================
-- Mall 前端菜单国际化迁移脚本
-- 用于装修模块菜单名称的多语言支持
-- =============================================

SET NAMES utf8mb4;

-- ----------------------------
-- Table structure for promotion_diy_menu_i18n
-- ----------------------------
DROP TABLE IF EXISTS `promotion_diy_menu_i18n`;
CREATE TABLE `promotion_diy_menu_i18n`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `menu_key` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单标识(url路径)',
  `language` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '语言代码(zh-CN, en等)',
  `position` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'home' COMMENT '菜单位置(home=首页, user=我的页面, tabbar=底部导航, float=浮动按钮)',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称翻译',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_menu_lang_position`(`menu_key`, `language`, `position`) USING BTREE,
  INDEX `idx_language`(`language`) USING BTREE,
  INDEX `idx_position`(`position`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '装修菜单国际化表';

-- ----------------------------
-- Records of promotion_diy_menu_i18n
-- ----------------------------
BEGIN;

-- =============================================
-- 首页 MenuGrid 菜单 (position = home)
-- =============================================

-- 拼团
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/activity/groupon/list', 'zh-CN', 'home', '拼团');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/activity/groupon/list', 'en', 'home', 'Group Buy');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/activity/groupon/list', 'ar', 'home', 'شراء جماعي');

-- 秒杀
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/activity/seckill/list', 'zh-CN', 'home', '秒杀');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/activity/seckill/list', 'en', 'home', 'Flash Sale');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/activity/seckill/list', 'ar', 'home', 'صفقة سريعة');

-- 充值
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/pay/recharge', 'zh-CN', 'home', '充值');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/pay/recharge', 'en', 'home', 'Recharge');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/pay/recharge', 'ar', 'home', 'شحن الرصيد');

-- 领劵
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/coupon/list', 'zh-CN', 'home', '领劵');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/coupon/list', 'en', 'home', 'Coupons');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/coupon/list', 'ar', 'home', 'قسائم');

-- 签到
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/app/sign', 'zh-CN', 'home', '签到');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/app/sign', 'en', 'home', 'Check-in');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/app/sign', 'ar', 'home', 'تسجيل الوصول');

-- 分销
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/commission/index', 'zh-CN', 'home', '分销');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/commission/index', 'en', 'home', 'Distribution');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/commission/index', 'ar', 'home', 'توزيع');

-- 商品
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/goods/list', 'zh-CN', 'home', '商品');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/goods/list', 'en', 'home', 'Products');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/goods/list', 'ar', 'home', 'منتجات');

-- 收藏
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/user/goods-collect', 'zh-CN', 'home', '收藏');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/user/goods-collect', 'en', 'home', 'Favorites');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/user/goods-collect', 'ar', 'home', 'المفضلة');

-- 收藏 (user position) - 用于个人中心页面
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/user/goods-collect', 'zh-CN', 'user', '收藏');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/user/goods-collect', 'en', 'user', 'Favorites');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/user/goods-collect', 'ar', 'user', 'المفضلة');

-- =============================================
-- 我的页面 MenuGrid 菜单 (position = user)
-- =============================================

-- 提现
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/commission/withdraw', 'zh-CN', 'user', '提现');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/commission/withdraw', 'en', 'user', 'Withdraw');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/commission/withdraw', 'ar', 'user', 'سحب');

-- 设置
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/public/setting', 'zh-CN', 'user', '设置');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/public/setting', 'en', 'user', 'Settings');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/public/setting', 'ar', 'user', 'الإعدادات');

-- 浏览足迹
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/user/goods-log', 'zh-CN', 'user', '浏览足迹');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/user/goods-log', 'en', 'user', 'Browse History');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/user/goods-log', 'ar', 'user', 'سجل التصفح');

-- 意见反馈
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/public/richtext?title=意见反馈', 'zh-CN', 'user', '意见反馈');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/public/richtext?title=意见反馈', 'en', 'user', 'Feedback');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/public/richtext?title=意见反馈', 'ar', 'user', 'ملاحظات');

-- 分销中心
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/commission/index', 'zh-CN', 'user', '分销中心');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/commission/index', 'en', 'user', 'Distribution Center');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/commission/index', 'ar', 'user', 'مركز التوزيع');

-- 拼团订单
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/activity/groupon/order', 'zh-CN', 'user', '拼团订单');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/activity/groupon/order', 'en', 'user', 'Group Orders');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/activity/groupon/order', 'ar', 'user', 'طلبات المجموعة');

-- 常见问题
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/public/richtext?title=常见问题', 'zh-CN', 'user', '常见问题');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/public/richtext?title=常见问题', 'en', 'user', 'FAQ');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/public/richtext?title=常见问题', 'ar', 'user', 'الأسئلة الشائعة');

-- 积分商城
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/activity/point/list', 'zh-CN', 'user', '积分商城');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/activity/point/list', 'en', 'user', 'Points Mall');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/activity/point/list', 'ar', 'user', 'متجر النقاط');

-- 关于我们
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/public/richtext?title=关于我们', 'zh-CN', 'user', '关于我们');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/public/richtext?title=关于我们', 'en', 'user', 'About Us');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/public/richtext?title=关于我们', 'ar', 'user', 'من نحن');

-- 隐私协议
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/public/richtext?title=隐私协议', 'zh-CN', 'user', '隐私协议');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/public/richtext?title=隐私协议', 'en', 'user', 'Privacy Policy');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/public/richtext?title=隐私协议', 'ar', 'user', 'سياسة الخصوصية');

-- 收货地址
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/user/address/list', 'zh-CN', 'user', '收货地址');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/user/address/list', 'en', 'user', 'Address');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/user/address/list', 'ar', 'user', 'العنوان');

-- 发票管理
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/user/invoice', 'zh-CN', 'user', '发票管理');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/user/invoice', 'en', 'user', 'Invoice');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/user/invoice', 'ar', 'user', 'الفواتير');

-- 联系客服
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/chat/index', 'zh-CN', 'user', '联系客服');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/chat/index', 'en', 'user', 'Customer Service');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/chat/index', 'ar', 'user', 'خدمة العملاء');

-- 签到 (用户页面)
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/app/sign', 'zh-CN', 'user', '签到');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/app/sign', 'en', 'user', 'Check-in');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/app/sign', 'ar', 'user', 'تسجيل الوصول');

-- 充值 (用户页面)
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/pay/recharge', 'zh-CN', 'user', '充值');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/pay/recharge', 'en', 'user', 'Recharge');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/pay/recharge', 'ar', 'user', 'شحن الرصيد');

-- =============================================
-- TabBar 底部导航 (position = tabbar)
-- =============================================

-- 首页
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/index/index', 'zh-CN', 'tabbar', '首页');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/index/index', 'en', 'tabbar', 'Home');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/index/index', 'ar', 'tabbar', 'الرئيسية');

-- 分类
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/index/category', 'zh-CN', 'tabbar', '分类');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/index/category', 'en', 'tabbar', 'Category');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/index/category', 'ar', 'tabbar', 'الفئات');

-- 购物车
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/index/cart', 'zh-CN', 'tabbar', '购物车');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/index/cart', 'en', 'tabbar', 'Cart');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/index/cart', 'ar', 'tabbar', 'عربة التسوق');

-- 我的
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/index/user', 'zh-CN', 'tabbar', '我的');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/index/user', 'en', 'tabbar', 'Me');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/index/user', 'ar', 'tabbar', 'حسابي');

-- =============================================
-- 浮动按钮 (position = float)
-- =============================================

-- 客服 (浮动按钮)
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/chat/index', 'zh-CN', 'float', '客服');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/chat/index', 'en', 'float', 'Support');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/chat/index', 'ar', 'float', 'الدعم');

-- 首页 (浮动按钮)
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/index/index', 'zh-CN', 'float', '首页');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/index/index', 'en', 'float', 'Home');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('/pages/index/index', 'ar', 'float', 'الرئيسية');

-- =============================================
-- 按钮文本翻译 (position = button)
-- 用于商品卡片、秒杀、拼团、积分等组件的按钮文本
-- =============================================

-- 立即购买
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('立即购买', 'zh-CN', 'button', '立即购买');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('立即购买', 'en', 'button', 'Buy Now');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('立即购买', 'ar', 'button', 'اشتر الآن');

-- 立即秒杀
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('立即秒杀', 'zh-CN', 'button', '立即秒杀');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('立即秒杀', 'en', 'button', 'Flash Sale');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('立即秒杀', 'ar', 'button', 'صفقة سريعة');

-- 立即兑换
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('立即兑换', 'zh-CN', 'button', '立即兑换');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('立即兑换', 'en', 'button', 'Redeem');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('立即兑换', 'ar', 'button', 'استبدال الآن');

-- 立即开团
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('立即开团', 'zh-CN', 'button', '立即开团');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('立即开团', 'en', 'button', 'Group Buy');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('立即开团', 'ar', 'button', 'شراء جماعي');

-- 加入购物车
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('加入购物车', 'zh-CN', 'button', '加入购物车');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('加入购物车', 'en', 'button', 'Add to Cart');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('加入购物车', 'ar', 'button', 'إضافة للعربة');

-- 立即抢购
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('立即抢购', 'zh-CN', 'button', '立即抢购');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('立即抢购', 'en', 'button', 'Buy Now');
INSERT INTO `promotion_diy_menu_i18n` (`menu_key`, `language`, `position`, `name`) VALUES ('立即抢购', 'ar', 'button', 'اشتر الآن');

-- =============================================
-- 货币单位支持迁移脚本
-- 为租户表添加货币代码字段
-- =============================================

-- 为租户表添加货币代码字段
ALTER TABLE `system_tenant` ADD COLUMN `currency_code` VARCHAR(10) NOT NULL DEFAULT 'CNY' COMMENT '货币代码（CNY/USD/SAR等）' AFTER `account_count`;

-- 更新现有租户的默认货币代码
UPDATE `system_tenant` SET `currency_code` = 'CNY' WHERE `currency_code` IS NULL OR `currency_code` = '';

COMMIT;

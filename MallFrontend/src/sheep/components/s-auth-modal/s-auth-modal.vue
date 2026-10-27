<template>
  <!-- 规格弹窗 -->
  <su-popup :show="authType !== ''" round="10" :showClose="true" @close="closeAuthModal">
    <view class="login-wrap">
      <!-- 1. 账号密码登录 accountLogin -->
      <account-login
        v-if="authType === 'accountLogin'"
        :agreeStatus="state.protocol"
        @onConfirm="onConfirm"
      />

      <!-- 2. 短信登录  smsLogin -->
      <sms-login
        v-if="authType === 'smsLogin'"
        :agreeStatus="state.protocol"
        @onConfirm="onConfirm"
      />

      <!-- 3. 忘记密码 resetPassword-->
      <reset-password v-if="authType === 'resetPassword'" />

      <!-- 4. 绑定手机号 changeMobile -->
      <change-mobile v-if="authType === 'changeMobile'" />

      <!-- 5. 修改密码 changePassword-->
      <changePassword v-if="authType === 'changePassword'" />

      <!-- 6. 微信小程序授权 -->
      <mp-authorization v-if="authType === 'mpAuthorization'" />

      <!-- 7. 第三方登录 -->
      <view
        v-if="['accountLogin', 'smsLogin'].includes(authType)"
        class="auto-login-box ss-flex ss-flex-col ss-row-center ss-col-center"
      >
        <!-- 7.1 微信小程序的快捷登录 -->
        <view v-if="sheep.$platform.name === 'WechatMiniProgram'" class="ss-flex register-box">
          <view class="register-title">{{ t('noAccount') }}</view>
          <button
            class="ss-reset-button login-btn"
            open-type="getPhoneNumber"
            @getphonenumber="getPhoneNumber"
          >
            {{ t('quickLogin') }}
          </button>
          <view class="circle" />
        </view>

        <!-- 7.2 微信的公众号、App、小程序的登录，基于 openid + code -->
        <button
          v-if="
            ['WechatOfficialAccount', 'WechatMiniProgram', 'App'].includes(sheep.$platform.name) &&
            sheep.$platform.isWechatInstalled
          "
          @tap="thirdLogin('wechat')"
          class="ss-reset-button auto-login-btn"
        >
          <image
            class="auto-login-img"
            :src="sheep.$url.static('/static/img/shop/platform/wechat.png')"
          />
        </button>

        <!-- 7.3 iOS 登录 TODO 芋艿：等后面搞 App 再弄 -->
        <button
          v-if="sheep.$platform.os === 'ios' && sheep.$platform.name === 'App'"
          @tap="thirdLogin('apple')"
          class="ss-reset-button auto-login-btn"
        >
          <image
            class="auto-login-img"
            :src="sheep.$url.static('/static/img/shop/platform/apple.png')"
          />
        </button>

        <!-- 7.4 支付宝小程序登录 -->
        <button
          v-if="sheep.$platform.name === 'alipayMiniProgram'"
          @tap="thirdLogin('alipay')"
          class="ss-reset-button auto-login-btn"
        >
          <image
            class="auto-login-img"
            :src="sheep.$url.static('/static/img/shop/pay/alipay.png')"
          />
        </button>
      </view>

      <!-- 用户协议的勾选 -->
      <view
        v-if="['accountLogin', 'smsLogin'].includes(authType)"
        class="agreement-box"
        :class="{ shake: currentProtocol }"
      >
        <view class="agreement-title">{{ t('protocolTip') }}</view>
        
        <!-- 同意选项 -->
        <view class="agreement-item" @tap="onAgree">
          <view class="agreement-radio-wrap">
            <radio
              :checked="state.protocol === true"
              color="var(--ui-BG-Main)"
              style="transform: scale(0.8)"
            />
          </view>
          <view class="agreement-content">
            <text class="agreement-text-main">{{ t('agreeProtocolText') }}</text>
            <text class="tcp-text" @tap.stop="onProtocol(t('app.userAgreement'))">《{{ t('app.userAgreement') }}》</text>
            <text class="agreement-and">{{ t('app.agreementAnd') }}</text>
            <text class="tcp-text" @tap.stop="onProtocol(t('app.privacyPolicy'))">《{{ t('app.privacyPolicy') }}》</text>
          </view>
        </view>
        
        <!-- 拒绝选项 -->
        <view class="agreement-item" @tap="onRefuse">
          <view class="agreement-radio-wrap">
            <radio
              :checked="state.protocol === false"
              color="#ff4d4f"
              style="transform: scale(0.8)"
            />
          </view>
          <view class="agreement-content">
            <text class="agreement-text-main">{{ t('refuseProtocolText') }}</text>
            <text class="tcp-text" @tap.stop="onProtocol(t('app.userAgreement'))">《{{ t('app.userAgreement') }}》</text>
            <text class="agreement-and">{{ t('app.agreementAnd') }}</text>
            <text class="tcp-text" @tap.stop="onProtocol(t('app.privacyPolicy'))">《{{ t('app.privacyPolicy') }}》</text>
          </view>
        </view>
      </view>
      <view class="safe-box" />
    </view>
  </su-popup>
</template>

<script setup>
  import { computed, reactive, ref } from 'vue';
  import sheep from '@/sheep';
  import accountLogin from './components/account-login.vue';
  import smsLogin from './components/sms-login.vue';
  import resetPassword from './components/reset-password.vue';
  import changeMobile from './components/change-mobile.vue';
  import changePassword from './components/change-password.vue';
  import mpAuthorization from './components/mp-authorization.vue';
  import { closeAuthModal, showAuthModal } from '@/sheep/hooks/useModal';
  import { useI18n } from '@/sheep/i18n';

  const { t } = useI18n('common');

  const modalStore = sheep.$store('modal');
  // 授权弹窗类型
  const authType = computed(() => modalStore.auth);

  const state = reactive({
    protocol: null, // null表示未选择，true表示同意，false表示拒绝
  });

  const currentProtocol = ref(false);

  // 同意协议
  function onAgree() {
    state.protocol = true;
  }
  
  // 拒绝协议
  function onRefuse() {
    state.protocol = false;
  }

  // 查看协议
  function onProtocol(title) {
    closeAuthModal();
    sheep.$router.go('/pages/public/richtext', {
      title,
    });
  }

  // 点击登录 / 注册事件
  function onConfirm(e) {
    currentProtocol.value = e;
    setTimeout(() => {
      currentProtocol.value = false;
    }, 1000);
  }

  // 第三方授权登陆（微信小程序、Apple）
  const thirdLogin = async (provider) => {
    if (state.protocol !== true) {
      currentProtocol.value = true;
      setTimeout(() => {
        currentProtocol.value = false;
      }, 1000);
      
      if (state.protocol === false) {
        sheep.$helper.toast(t('refusedProtocol'));
      } else {
        sheep.$helper.toast(t('agreeProtocol'));
      }
      return;
    }
    const loginRes = await sheep.$platform.useProvider(provider).login();
    if (loginRes) {
      const userInfo = await sheep.$store('user').getInfo();
      closeAuthModal();
      // 如果用户已经有头像和昵称，不需要再次授权
      if (userInfo.avatar && userInfo.nickname) {
        return;
      }

      // 触发小程序授权信息弹框
      // #ifdef MP-WEIXIN
      showAuthModal('mpAuthorization');
      // #endif
    }
  };

  // 微信小程序的“手机号快速验证”：https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/getPhoneNumber.html
  const getPhoneNumber = async (e) => {
    if (e.detail.errMsg !== 'getPhoneNumber:ok') {
      sheep.$helper.toast(t('quickLoginFailed'));
      return;
    }
    let result = await sheep.$platform.useProvider().mobileLogin(e.detail);
    if (result) {
      closeAuthModal();
    }
  };
</script>

<style lang="scss" scoped>
  @import 'index';

  .shake {
    animation: shake 0.05s linear 4 alternate;
  }

  @keyframes shake {
    from {
      transform: translateX(-10rpx);
    }
    to {
      transform: translateX(10rpx);
    }
  }

  .register-box {
    position: relative;
    justify-content: center;
    .register-btn {
      color: #999999;
      font-size: 30rpx;
      font-weight: 500;
    }
    .register-title {
      color: #999999;
      font-size: 30rpx;
      font-weight: 400;
      margin-right: 24rpx;
    }
    .or-title {
      margin: 0 16rpx;
      color: #999999;
      font-size: 30rpx;
      font-weight: 400;
    }
    .login-btn {
      color: var(--ui-BG-Main);
      font-size: 30rpx;
      font-weight: 500;
    }
    .circle {
      position: absolute;
      right: 0rpx;
      top: 18rpx;
      width: 8rpx;
      height: 8rpx;
      border-radius: 8rpx;
      background: var(--ui-BG-Main);
    }
  }
  .safe-box {
    height: calc(constant(safe-area-inset-bottom) / 5 * 3);
    height: calc(env(safe-area-inset-bottom) / 5 * 3);
  }

  .tcp-text {
    color: var(--ui-BG-Main);
  }

  .agreement-box {
    padding: 30rpx 40rpx;
    background: #fafafa;
    border-radius: 16rpx;
    margin: 20rpx;
  }

  .agreement-title {
    font-size: 28rpx;
    color: $dark-9;
    text-align: center;
    margin-bottom: 24rpx;
  }

  .agreement-item {
    display: flex;
    align-items: flex-start;
    padding: 16rpx 0;
    cursor: pointer;
  }

  .agreement-radio-wrap {
    flex-shrink: 0;
    padding-top: 4rpx;
  }

  .agreement-content {
    flex: 1 !important;
    margin-left: 12rpx !important;
    line-height: 1.6 !important;
    font-size: 26rpx !important;
    color: $dark-9 !important;
    word-break: break-word !important;
  }

  .agreement-text-main {
    margin-right: 4rpx;
  }

  .agreement-and {
    margin: 0 4rpx;
  }
</style>

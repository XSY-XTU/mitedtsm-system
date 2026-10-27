<!-- 短信登录 - smsLogin  -->
<template>
  <view>
    <!-- 标题栏 -->
    <view class="head-box ss-m-b-60">
      <view class="head-title head-title-line head-title-animation">{{ t('phoneLogin') }}</view>
      <view class="head-subtitle">{{ t('autoRegisterTip') }}</view>
    </view>

    <!-- 表单项 -->
    <uni-forms
      ref="smsLoginRef"
      v-model="state.model"
      :rules="state.rules"
      validateTrigger="bind"
      labelPosition="top"
    >
      <uni-forms-item name="mobile" :label="t('phone')">
        <uni-easyinput
          :placeholder="t('phonePlaceholder')"
          v-model="state.model.mobile"
          :inputBorder="false"
          type="number"
        >
          <template v-slot:right>
            <button
              class="ss-reset-button code-btn code-btn-start"
              :disabled="state.isMobileEnd || props.agreeStatus === false"
              :class="{ 'code-btn-end': state.isMobileEnd || props.agreeStatus === false }"
              @tap="checkAgreementAndGetSmsCode"
            >
              {{ getSmsTimer('smsLogin') }}
            </button>
          </template>
        </uni-easyinput>
      </uni-forms-item>

      <uni-forms-item name="code" :label="t('smsCode')">
        <uni-easyinput
          :placeholder="t('smsCodePlaceholder')"
          v-model="state.model.code"
          :inputBorder="false"
          type="number"
          maxlength="4"
        >
          <template v-slot:right>
            <button class="ss-reset-button login-btn-start" @tap="smsLoginSubmit"> {{ t('login') }} </button>
          </template>
        </uni-easyinput>
      </uni-forms-item>
    </uni-forms>
  </view>
</template>

<script setup>
  import { ref, reactive, unref } from 'vue';
  import sheep from '@/sheep';
  import { code, mobile } from '@/sheep/validate/form';
  import { showAuthModal, closeAuthModal, getSmsCode, getSmsTimer } from '@/sheep/hooks/useModal';
  import AuthUtil from '@/sheep/api/member/auth';
  import { useI18n } from '@/sheep/i18n';

  const { t } = useI18n('login');
  const smsLoginRef = ref(null);

  const emits = defineEmits(['onConfirm']);

  const props = defineProps({
    agreeStatus: {
      type: [Boolean, null],
      default: null,
    },
  });

  // 数据
  const state = reactive({
    isMobileEnd: false, // 手机号输入完毕
    codeText: '获取验证码',
    model: {
      mobile: '', // 手机号
      code: '', // 验证码
    },
    rules: {
      code,
      mobile,
    },
  });

  // 检查协议并获取验证码
  function checkAgreementAndGetSmsCode() {
    if (props.agreeStatus !== true) {
      emits('onConfirm', true);
      if (props.agreeStatus === false) {
        sheep.$helper.toast(t('agreementSmsRejected'));
      } else {
        sheep.$helper.toast(t('agreementRequired'));
      }
      return;
    }
    getSmsCode('smsLogin', state.model.mobile);
  }

  // 短信登录
  async function smsLoginSubmit() {
    // 参数校验
    const validate = await unref(smsLoginRef)
      .validate()
      .catch((error) => {
        console.log('error: ', error);
      });
    if (!validate) {
      return;
    }
    // 检查协议状态
    if (props.agreeStatus !== true) {
      emits('onConfirm', true);
      if (props.agreeStatus === false) {
        sheep.$helper.toast(t('agreementRejected'));
      } else {
        sheep.$helper.toast(t('agreementRequired'));
      }
      return;
    }
    // 提交数据
    const { code } = await AuthUtil.smsLogin(state.model);
    if (code === 0) {
      closeAuthModal();
    }
  }
</script>

<style lang="scss" scoped>
  @import '../index';
</style>

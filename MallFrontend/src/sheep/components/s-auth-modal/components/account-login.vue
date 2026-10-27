<!-- 账号密码登录 accountLogin  -->
<template>
  <view>
    <!-- 标题栏 -->
    <view class="head-box ss-m-b-60">
      <view class="head-title head-title-line head-title-animation">{{ t('accountLogin') }}</view>
      <view class="head-subtitle">{{ t('passwordResetTip') }}</view>
    </view>

    <!-- 表单项 -->
    <uni-forms
      ref="accountLoginRef"
      v-model="state.model"
      :rules="state.rules"
      validateTrigger="bind"
      labelPosition="top"
    >
      <uni-forms-item name="mobile" :label="t('account')">
        <uni-easyinput :placeholder="t('accountPlaceholder')" v-model="state.model.mobile" :inputBorder="false">
          <template v-slot:right>
            <button class="ss-reset-button forgot-btn" @tap="showAuthModal('resetPassword')">
              {{ t('forgotPassword') }}
            </button>
          </template>
        </uni-easyinput>
      </uni-forms-item>

      <uni-forms-item name="password" :label="t('password')">
        <uni-easyinput
          type="password"
          :placeholder="t('passwordPlaceholder')"
          v-model="state.model.password"
          :inputBorder="false"
        >
          <template v-slot:right>
            <button class="ss-reset-button login-btn-start" @tap="accountLoginSubmit">{{ t('login') }}</button>
          </template>
        </uni-easyinput>
      </uni-forms-item>
    </uni-forms>
  </view>
</template>

<script setup>
  import { ref, reactive, unref } from 'vue';
  import sheep from '@/sheep';
  import { mobile, password } from '@/sheep/validate/form';
  import { showAuthModal, closeAuthModal } from '@/sheep/hooks/useModal';
  import AuthUtil from '@/sheep/api/member/auth';
  import { useI18n } from '@/sheep/i18n';

  const { t } = useI18n('login');
  const accountLoginRef = ref(null);

  const emits = defineEmits(['onConfirm']);

  const props = defineProps({
    agreeStatus: {
      type: [Boolean, null],
      default: null,
    },
  });

  // 数据
  const state = reactive({
    model: {
      mobile: '', // 账号
      password: '', // 密码
    },
    rules: {
      mobile,
      password,
    },
  });

  // 账号登录
  async function accountLoginSubmit() {
    // 表单验证
    const validate = await unref(accountLoginRef)
      .validate()
      .catch((error) => {
        console.log('error: ', error);
      });
    if (!validate) return;

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
    const { code, data } = await AuthUtil.login(state.model);
    if (code === 0) {
      closeAuthModal();
    }
  }
</script>

<style lang="scss" scoped>
  @import '../index';
</style>

<!-- 错误界面 -->
<template>
  <view class="error-page">
    <s-empty
      v-if="errCode === 'NetworkError'"
      icon="/static/internet-empty.png"
      :text="t('networkConnectFailed')"
      showAction
      :actionText="t('reconnect')"
      @clickAction="onReconnect"
      buttonColor="#ff3000"
    />
    <s-empty
      v-else-if="errCode === 'TemplateError'"
      icon="/static/internet-empty.png"
      :text="t('templateNotFound')"
      showAction
      :actionText="t('reload')"
      @clickAction="onReconnect"
      buttonColor="#ff3000"
    />
    <s-empty
      v-else-if="errCode !== ''"
      icon="/static/internet-empty.png"
      :text="errMsg"
      showAction
      :actionText="t('reload')"
      @clickAction="onReconnect"
      buttonColor="#ff3000"
    />
  </view>
</template>

<script setup>
  import { onLoad } from '@dcloudio/uni-app';
  import { ref } from 'vue';
  import { ShoproInit } from '@/sheep';
  import { useI18n } from '@/sheep/i18n';

  const { t } = useI18n('error');

  const errCode = ref('');
  const errMsg = ref('');

  onLoad((options) => {
    errCode.value = options.errCode;
    errMsg.value = options.errMsg;
  });

  // 重新连接
  async function onReconnect() {
    uni.reLaunch({
      url: '/pages/index/index',
    });
    await ShoproInit();
  }
</script>

<style lang="scss" scoped>
  .error-page {
    width: 100%;
  }
</style>

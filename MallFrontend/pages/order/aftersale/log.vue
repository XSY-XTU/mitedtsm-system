<!-- 售后日志列表  -->
<template>
  <s-layout :title="t('aftersaleProgress')">
    <view class="log-box">
      <view v-for="(item, index) in state.list" :key="item.id">
        <log-item :item="item" :index="index" :data="state.list" />
      </view>
    </view>
  </s-layout>
</template>

<script setup>
  import { onLoad, onReady } from '@dcloudio/uni-app';
  import { reactive } from 'vue';
  import logItem from './log-item.vue';
  import AfterSaleApi from '@/sheep/api/trade/afterSale';
  import { useI18n } from '@/sheep/i18n';
  import { setPageTitle } from '@/sheep/i18n/pageTitle';

  const { t } = useI18n('order');

  onReady(() => {
    setPageTitle('pages/order/aftersale/log');
  });

  const state = reactive({
    list: [],
  });

  async function getDetail(id) {
    const { data } = await AfterSaleApi.getAfterSaleLogList(id);
    state.list = data;
  }

  onLoad((options) => {
    state.aftersaleId = options.id;
    getDetail(options.id);
  });
</script>

<style lang="scss" scoped>
  .log-box {
    padding: 24rpx 24rpx 24rpx 40rpx;
    background-color: #fff;
  }
</style>

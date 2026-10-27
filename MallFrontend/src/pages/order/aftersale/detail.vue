<!-- 售后详情 -->
<template>
  <s-layout :title="t('aftersaleDetail')" :navbar="!isEmpty(state.info) && state.loading ? 'inner' : 'normal'">
    <view class="content_box" v-if="!isEmpty(state.info) && state.loading">
      <!-- 步骤条 -->
      <view
        class="steps-box ss-flex"
        :style="[
          {
            marginTop: '-' + Number(statusBarHeight + 88) + 'rpx',
            paddingTop: Number(statusBarHeight + 88) + 'rpx',
          },
        ]"
      >
        <view class="ss-flex">
          <view class="steps-item" v-for="(item, index) in state.list" :key="index">
            <view class="ss-flex">
              <text
                class="sicon-circleclose"
                v-if="state.list.length - 1 === index && [61, 62, 63].includes(state.info.status)"
              />
              <text
                class="sicon-circlecheck"
                v-else
                :class="state.active >= index ? 'activity-color' : 'info-color'"
              />

              <view
                v-if="state.list.length - 1 !== index"
                class="line"
                :class="state.active >= index ? 'activity-bg' : 'info-bg'"
              />
            </view>
            <view
              class="steps-item-title"
              :class="state.active >= index ? 'activity-color' : 'info-color'"
            >
              {{ item.title }}
            </view>
          </view>
        </view>
      </view>

      <!-- 服务状态 -->
      <view
        class="status-box ss-flex ss-col-center ss-row-between ss-m-x-20"
        @tap="sheep.$router.go('/pages/order/aftersale/log', { id: state.id })"
      >
        <view class="">
          <view class="status-text">
            {{ formatAfterSaleStatusDescription(state.info) }}
          </view>
          <view class="status-time">
            {{ sheep.$helper.timeFormat(state.info.updateTime, 'yyyy-mm-dd hh:MM:ss') }}
          </view>
        </view>
        <text class="ss-iconfont _icon-forward" style="color: #666" />
      </view>

      <!-- 退款金额 -->
      <view class="aftersale-money ss-flex ss-col-center ss-row-between">
        <view class="aftersale-money--title">{{ t('refundTotal') }}</view>
        <view class="aftersale-money--num">{{ fen2yuan(state.info.refundPrice) }}</view>
      </view>
      <!-- 服务商品 -->
      <view class="order-shop">
        <s-goods-item
          :img="state.info.picUrl"
          :title="state.info.spuName"
          :titleWidth="480"
          :skuText="state.info.properties.map((property) => property.valueName).join(' ')"
          :num="state.info.count"
        />
      </view>

      <!-- 服务内容 -->
      <view class="aftersale-content">
        <view class="aftersale-item ss-flex ss-col-center">
          <view class="item-title">{{ t('serviceNoLabel') }}</view>
          <view class="item-content ss-m-r-16">{{ state.info.no }}</view>
          <button class="ss-reset-button copy-btn" @tap="onCopy">{{ t('copy', {}, { scope: 'common' }) }}</button>
        </view>
        <view class="aftersale-item ss-flex ss-col-center">
          <view class="item-title">{{ t('applyTimeLabel') }}</view>
          <view class="item-content">
            {{ sheep.$helper.timeFormat(state.info.createTime, 'yyyy-mm-dd hh:MM:ss') }}
          </view>
        </view>
        <view class="aftersale-item ss-flex ss-col-center">
          <view class="item-title">{{ t('aftersaleTypeLabel') }}</view>
          <view class="item-content">{{ state.info.way === 10 ? t('aftersaleRefund') : t('aftersaleReturn') }}</view>
        </view>
        <view class="aftersale-item ss-flex ss-col-center">
          <view class="item-title">{{ t('applyReasonLabel') }}</view>
          <view class="item-content">{{ state.info.applyReason }}</view>
        </view>
        <view class="aftersale-item ss-flex ss-col-center">
          <view class="item-title">{{ t('relatedDescriptionLabel') }}</view>
          <view class="item-content">{{ state.info.applyDescription }}</view>
        </view>
      </view>
    </view>

    <!-- 操作区 -->
    <s-empty
      v-if="isEmpty(state.info) && state.loading"
      icon="/static/order-empty.png"
      :text="t('noAftersaleDetail')"
    />
    <su-fixed bottom placeholder bg="bg-white" v-if="!isEmpty(state.info)">
      <view class="foot_box">
        <button
          class="ss-reset-button btn"
          v-if="state.info.buttons?.includes('cancel')"
          @tap="onApply(state.info.id)"
        >
          {{ t('cancelApply') }}
        </button>
        <button
          class="ss-reset-button btn"
          v-if="state.info.buttons?.includes('delivery')"
          @tap="sheep.$router.go('/pages/order/aftersale/return-delivery', { id: state.info.id })"
        >
          {{ t('fillReturn') }}
        </button>
        <button
          class="ss-reset-button contcat-btn btn"
          @tap="sheep.$router.go('/pages/chat/index')"
        >
          {{ t('contactService') }}
        </button>
      </view>
    </su-fixed>
  </s-layout>
</template>

<script setup>
  import sheep from '@/sheep';
  import { onLoad, onReady } from '@dcloudio/uni-app';
  import { reactive, computed } from 'vue';
  import { isEmpty } from 'lodash-es';
  import {
    fen2yuan,
    formatAfterSaleStatusDescription,
    handleAfterSaleButtons,
  } from '@/sheep/hooks/useGoods';
  import AfterSaleApi from '@/sheep/api/trade/afterSale';
  import { useI18n } from '@/sheep/i18n';
  import { setPageTitle } from '@/sheep/i18n/pageTitle';

  const { t } = useI18n('order');

  onReady(() => {
    setPageTitle('pages/order/aftersale/detail');
  });

  const statusBarHeight = sheep.$platform.device.statusBarHeight * 2;
  const headerBg = sheep.$url.css('/static/img/shop/order/order_bg.png');
  const state = reactive({
    id: 0, // 售后编号
    info: {}, // 收货信息
    loading: false,
    active: 0, // 在 list 的激活位置
    list: computed(() => [
      {
        title: t('submitApply'),
      },
      {
        title: t('aftersaleProcessing'),
      },
      {
        title: t('aftersaleCompleted'),
      },
    ]), // 时间轴
  });

  function onApply(id) {
    uni.showModal({
      title: t('tip', {}, { scope: 'common' }),
      content: t('cancelApplyConfirm'),
      success: async function (res) {
        if (!res.confirm) {
          return;
        }
        const { code } = await AfterSaleApi.cancelAfterSale(id);
        if (code === 0) {
          await getDetail(id);
        }
      },
    });
  }

  // 复制
  const onCopy = () => {
    sheep.$helper.copyText(state.info.no);
  };

  async function getDetail(id) {
    state.loading = true;
    const { code, data } = await AfterSaleApi.getAfterSale(id);
    if (code !== 0) {
      state.info = null;
      return;
    }
    state.info = data;
    handleAfterSaleButtons(state.info);

    // 处理时间轴
    if ([10].includes(state.info.status)) {
      state.active = 0;
    } else if ([20, 30].includes(state.info.status)) {
      state.active = 1;
    } else if ([40, 50].includes(state.info.status)) {
      state.active = 2;
    } else if ([61, 62, 63].includes(state.info.status)) {
      state.active = 2;
    }
  }

  onLoad((options) => {
    if (!options.id) {
      sheep.$helper.toast(t('missingOrderInfo'));
      return;
    }
    state.id = options.id;
    getDetail(options.id);
  });
</script>

<style lang="scss" scoped>
  // 步骤条
  .steps-box {
    width: 100%;
    height: 190rpx;
    background: v-bind(headerBg) no-repeat,
      linear-gradient(90deg, var(--ui-BG-Main), var(--ui-BG-Main-gradient));
    background-size: 750rpx 100%;
    padding-left: 72rpx;

    .steps-item {
      .sicon-circleclose {
        font-size: 24rpx;
        color: #fff;
      }

      .sicon-circlecheck {
        font-size: 24rpx;
      }

      .steps-item-title {
        font-size: 24rpx;
        font-weight: 400;
        margin-top: 16rpx;
        margin-left: -36rpx;
        width: 100rpx;
        text-align: center;
      }
    }
  }

  .activity-color {
    color: #fff;
  }

  .info-color {
    color: rgba(#fff, 0.4);
  }

  .activity-bg {
    background: #fff;
  }

  .info-bg {
    background: rgba(#fff, 0.4);
  }

  .line {
    width: 270rpx;
    height: 4rpx;
  }

  // 服务状态
  .status-box {
    position: relative;
    z-index: 3;
    background-color: #fff;
    border-radius: 20rpx 20rpx 0px 0px;
    padding: 20rpx;
    margin-top: -20rpx;

    .status-text {
      font-size: 28rpx;

      font-weight: 500;
      color: rgba(51, 51, 51, 1);
      margin-bottom: 20rpx;
    }

    .status-time {
      font-size: 24rpx;

      font-weight: 400;
      color: rgba(153, 153, 153, 1);
    }
  }

  // 退款金额
  .aftersale-money {
    background-color: #fff;
    height: 98rpx;
    padding: 0 20rpx;
    margin: 20rpx;

    .aftersale-money--title {
      font-size: 28rpx;

      font-weight: 500;
      color: rgba(51, 51, 51, 1);
    }

    .aftersale-money--num {
      font-size: 28rpx;
      font-family: OPPOSANS;
      font-weight: 500;
      color: #ff3000;
    }
  }

  // order-shop
  .order-shop {
    padding: 20rpx;
    background-color: #fff;
    margin: 0 20rpx 20rpx 20rpx;
  }

  // 服务内容
  .aftersale-content {
    background-color: #fff;
    padding: 20rpx;
    margin: 0 20rpx;

    .aftersale-item {
      height: 60rpx;

      .copy-btn {
        background: #eeeeee;
        color: #333;
        border-radius: 20rpx;
        width: 75rpx;
        height: 40rpx;
        font-size: 22rpx;
      }

      .item-title {
        color: #999;
        font-size: 28rpx;
      }

      .item-content {
        color: #333;
        font-size: 28rpx;
      }
    }
  }

  // 底部功能
  .foot_box {
    height: 100rpx;
    background-color: #fff;
    display: flex;
    align-items: center;
    justify-content: flex-end;

    .btn {
      width: 160rpx;
      line-height: 60rpx;
      background: rgba(238, 238, 238, 1);
      border-radius: 30rpx;
      padding: 0;
      margin-right: 20rpx;
      font-size: 26rpx;

      font-weight: 400;
      color: rgba(51, 51, 51, 1);
    }
  }
</style>

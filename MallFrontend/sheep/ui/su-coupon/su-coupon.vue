<template>
  <view class="ui-coupon-wrap">
    <!-- xs: 一行三个，竖向 -->
    <view
      v-if="props.size === 'xs'"
      class="xs-coupon-card ss-flex ss-flex-col ss-row-between"
      :style="[cardStyle]"
      @tap="
        sheep.$router.go('/pages/coupon/detail', {
          id: couponId,
        })
      "
    >
      <view class="ss-flex ss-flex-col ss-row-center ss-col-center">
        <view class="face-value-box ss-flex ss-col-bottom ss-m-t-50 ss-m-b-28">
          <view class="value-text ss-m-r-4">{{ type === 'reduce' ? value : Number(value) }}</view>
          <view class="value-unit">{{ type === 'reduce' ? t('yuan') : t('discount') }}</view>
        </view>
        <view class="title-text">{{ props.title }}</view>
      </view>
      <view class="card-bottom ss-m-b-30 ss-flex ss-row-center">
        <slot name="btn">
          <button class="ss-reset-button card-btn">{{ state.stateMap[props.state] }}</button>
        </slot>
      </view>
    </view>

    <!-- md: 一行两个，横向 -->
    <view
      v-if="props.size === 'md'"
      class="md-coupon-card ss-flex ss-row-between"
      :style="[cardStyle]"
      @tap="
        sheep.$router.go('/pages/coupon/detail', {
          id: couponId,
        })
      "
    >
      <view class="card-left ss-flex ss-flex-col ss-row-between ss-col-top ss-m-l-40">
        <view class="face-value-box ss-flex ss-col-bottom ss-m-t-28">
          <view class="value-text ss-m-r-4">{{ type === 'reduce' ? value : Number(value) }}</view>
          <view class="value-unit">{{ type === 'reduce' ? t('yuan') : t('discount') }}</view>
        </view>
        <view class="ss-m-b-28">
          <view class="title-text ss-m-b-10">{{ props.title }}</view>
          <view class="surplus-text" v-if="props.surplus >= 0">{{ t('onlyLeft') }}：{{ props.surplus }}{{ t('unitSheet') }}</view>
          <view class="surplus-text" v-else-if="props.surplus === -1">{{ t('limitOnly') }}：{{ t('unlimited') }}</view>
        </view>
      </view>
      <view class="card-right ss-flex ss-row-center">
        <slot name="btn">
          <button class="ss-reset-button card-btn ss-flex ss-row-center ss-col-center">
            <view class="btn-text">{{ state.stateMap[props.state] }}</view>
          </button>
        </slot>
      </view>
    </view>

    <!-- lg: 一行一个，横向 -->
    <view
      v-if="props.size === 'lg'"
      class="lg-coupon-card ss-flex ss-row-between"
      :style="[cardStyle]"
      @tap="
        sheep.$router.go('/pages/coupon/detail', {
          id: couponId,
        })
      "
    >
      <view class="card-left ss-flex ss-flex-col ss-row-between ss-col-top ss-m-l-40">
        <view class="face-value-box ss-flex ss-col-bottom ss-m-t-28">
          <view class="value-text ss-m-r-4">{{ type === 'reduce' ? value : Number(value) }}</view>
          <view class="value-unit">{{ type === 'reduce' ? t('yuan') : t('discount') }}</view>
        </view>
        <view class="ss-m-b-20">
          <view class="title-text ss-m-b-10">{{ props.title }}</view>
          <view class="sellby-text">{{ t('validityPeriod') }}：{{ props.sellBy }}</view>
        </view>
      </view>
      <view class="card-right ss-flex ss-flex-col ss-col-center ss-row-center">
        <slot name="btn">
          <button class="ss-reset-button card-btn ss-flex ss-row-center ss-col-center">
            {{ state.stateMap[props.state] }}
          </button>
        </slot>
        <view class="surplus-text ss-m-t-24" v-if="props.surplus">{{ t('onlyLeft') }}：{{ props.surplus }}{{ t('unitSheet') }}</view>
      </view>
    </view>
  </view>
</template>

<script setup>
  /**
   * 优惠券 卡片
   *
   * @property {String} size = ['xs','md','lg']					- 类型 xs:一行三个，md:一行两个，lg:一行一个
   * @property {String} textColor 								- 文字颜色
   * @property {String} background 								- 背景
   * @property {String} btnBg										- 按钮背景
   * @property {String} btnTextColor								- 按钮文字颜色
   * @property {Number} state = [0,1]								- 状态，0:未领取，1:已领取
   * @property {String} title										- 标题
   * @property {String | Number} value							- 面值
   * @property {String} sellBy									- 有效期
   * @property {String | Number} surplus							- 剩余
   *
   */
  import { computed, reactive } from 'vue';
  import sheep from '@/sheep';
  import { useI18n } from '@/sheep/i18n';
  
  const { t } = useI18n('coupon');

  // 数据
  const state = reactive({
    stateMap: {
      0: t('claimNow'),
      1: t('toUse'),
    },
  });

  // 接受参数
  const props = defineProps({
    size: {
      type: String,
      default: 'lg',
    },
    textColor: {
      type: String,
      default: '#FF6000',
    },
    background: {
      type: String,
      default: '#FFC19C',
    },
    btnBg: {
      type: String,
      default: '#fff',
    },
    btnTextColor: {
      type: String,
      default: '#FF6000',
    },
    state: {
      type: Number,
      default: 0,
    },
    couponId: {
      type: Number,
      default: 0,
    },
    title: {
      type: String,
      default: '这是优惠券',
    },
    value: {
      type: [Number, String],
      default: 50,
    },
    sellBy: {
      type: String,
      default: '2019.11.25至2019.12.25',
    },
    surplus: {
      type: [Number, String],
      default: 0,
    },
    type: {
      type: String,
      default: '',
    },
  });

  const cardStyle = computed(() => {
    return {
      background: props.background,
    };
  });
</script>

<style lang="scss" scoped>
  // xs
  .xs-coupon-card {
    width: 227rpx;
    // height: 145px;
    border-radius: 10rpx;
    overflow: hidden;

    .value-text {
      font-size: 50rpx;
      line-height: 50rpx;
      font-weight: bold;
      color: v-bind('textColor');
      vertical-align: text-bottom;
    }

    .value-unit {
      color: v-bind('textColor');
      font-size: 24rpx;
      line-height: 38rpx;
    }

    .title-text {
      color: v-bind('textColor');
      font-size: 24rpx;
      line-height: 30rpx;
      word-break: break-word;
      text-align: center;
    }

    .card-btn {
      width: 140rpx;
      height: 50rpx;
      border-radius: 25rpx;
      border-style: solid;
      border-color: v-bind('btnTextColor');
      border-width: 1px;
      color: v-bind('btnTextColor');
      background-color: v-bind('btnBg');
      font-size: 24rpx;
      line-height: 50rpx;
    }
  }

  // md

  .md-coupon-card {
    width: 330rpx;
    min-height: 168rpx;
    border-radius: 10rpx;
    overflow: visible;
    padding: 16rpx 20rpx;

    .card-right,
    .card-left {
      // height removed for i18n flexibility
    }

    .value-text {
      font-size: 36rpx;
      line-height: 36rpx;
      font-weight: bold;
      color: v-bind('textColor');
      vertical-align: text-bottom;
    }

    .value-unit {
      color: v-bind('textColor');
      font-size: 22rpx;
      line-height: 28rpx;
    }

    .title-text,
    .surplus-text {
      color: v-bind('textColor');
      font-size: 22rpx;
      line-height: 1.4;
      word-break: break-word;
    }

    .card-btn {
      width: 60rpx;
      height: 100%;

      .btn-text {
        color: v-bind('btnTextColor');
        font-size: 24rpx;
        text-align: center;
        writing-mode: vertical-lr;
        writing-mode: tb-lr;
      }
    }
  }

  // lg
  .lg-coupon-card {
    width: 708rpx;
    min-height: 168rpx;
    border-radius: 10rpx;
    overflow: visible;
    padding: 16rpx 20rpx;

    .card-right,
    .card-left {
      // height removed for i18n flexibility
    }

    .value-text {
      font-size: 50rpx;
      line-height: 50rpx;
      font-weight: bold;
      color: v-bind('textColor');
      vertical-align: text-bottom;
    }

    .value-unit {
      color: v-bind('textColor');
      font-size: 22rpx;
      line-height: 32rpx;
    }

    .title-text,
    .sellby-text,
    .surplus-text {
      color: v-bind('textColor');
      font-size: 22rpx;
      line-height: 1.4;
      word-break: break-word;
    }

    .card-right {
      width: 200rpx;
      .card-btn {
        width: 140rpx;
        height: 50rpx;
        border-radius: 25rpx;
        border-style: solid;
        border-color: v-bind('btnTextColor');
        border-width: 1px;
        color: v-bind('btnTextColor');
        background-color: v-bind('btnBg');
        font-size: 24rpx;
        line-height: 50rpx;
      }
    }
  }
</style>

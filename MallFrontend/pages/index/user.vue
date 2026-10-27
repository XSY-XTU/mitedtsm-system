<!-- 个人中心：支持装修 -->
<template>
  <s-layout
    :title="t('pageTitle.mine')"
    tabbar="/pages/index/user"
    navbar="custom"
    :bgStyle="template.page"
    :navbarStyle="template.navigationBar"
    onShareAppMessage
  >
    <s-block
      v-for="(item, index) in template.components"
      :key="index"
      :styles="item.property.style"
    >
      <s-block-item :type="item.id" :data="item.property" :styles="item.property.style" />
    </s-block>

    <!-- 语言切换下拉选择器 -->
    <view class="lang-selector" v-if="!state.showDropdown" @tap="state.showDropdown = true">
      <text class="lang-current">{{ currentLangName }}</text>
      <text class="lang-arrow">▼</text>
    </view>

    <!-- 语言下拉列表 -->
    <view class="lang-dropdown-mask" v-if="state.showDropdown" @tap="state.showDropdown = false">
      <view class="lang-dropdown" @tap.stop>
        <view
          class="lang-option"
          v-for="(lang, index) in langList"
          :key="index"
          :class="{ active: lang.code === currentLocale }"
          @tap="selectLanguage(lang.code)"
        >
          <text class="lang-option-text">{{ lang.name }}</text>
          <text class="lang-check" v-if="lang.code === currentLocale">✓</text>
        </view>
      </view>
    </view>
  </s-layout>
</template>

<script setup>
  import { computed, reactive } from 'vue';
  import { onShow, onPageScroll, onPullDownRefresh } from '@dcloudio/uni-app';
  import sheep from '@/sheep';
  import { useI18n, getLocaleValue, setLocale, getLocaleList } from '@/sheep/i18n';

  const { t } = useI18n('menu');

  const state = reactive({
    showDropdown: false
  });

  // 隐藏原生tabBar
  uni.hideTabBar({
    fail: () => {},
  });

  const template = computed(() => sheep.$store('app').template.user);

  // 语言列表
  const langList = computed(() => getLocaleList());

  // 当前语言代码
  const currentLocale = computed(() => getLocaleValue());

  // 当前语言名称
  const currentLangName = computed(() => {
    const lang = langList.value.find(item => item.code === currentLocale.value);
    return lang ? lang.name : currentLocale.value;
  });

  // 选择语言
  async function selectLanguage(code) {
    if (code !== currentLocale.value) {
      setLocale(code);
      state.showDropdown = false;
      // 刷新 DIY 模板数据以应用新语言
      await sheep.$store('app').refreshTemplate();
      // 刷新页面
      uni.reLaunch({ url: '/pages/index/user' });
    } else {
      state.showDropdown = false;
    }
  }

  onShow(() => {
    sheep.$store('user').updateUserData();
  });

  onPullDownRefresh(() => {
    sheep.$store('user').updateUserData();
    setTimeout(function () {
      uni.stopPullDownRefresh();
    }, 800);
  });

  onPageScroll(() => {});
</script>

<style lang="scss" scoped>
  .lang-selector {
    position: fixed;
    right: 30rpx;
    top: 180rpx;
    display: flex;
    align-items: center;
    padding: 16rpx 24rpx;
    background: #fff;
    border: 1rpx solid #e5e5e5;
    border-radius: 40rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
    z-index: 100;
    transition: all 0.2s ease;

    &:active {
      background: #f5f5f5;
    }

    .lang-current {
      font-size: 26rpx;
      font-weight: 500;
      color: #333;
      margin-right: 8rpx;
    }

    .lang-arrow {
      font-size: 16rpx;
      color: #999;
    }
  }

  .lang-dropdown-mask {
    position: fixed;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
    z-index: 200;
    background: rgba(0, 0, 0, 0.3);
  }

  .lang-dropdown {
    position: absolute;
    right: 30rpx;
    top: 260rpx;
    min-width: 200rpx;
    background: #fff;
    border-radius: 16rpx;
    box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.12);
    overflow: hidden;
    animation: dropdownIn 0.2s ease;
  }

  @keyframes dropdownIn {
    from {
      opacity: 0;
      transform: translateY(-16rpx);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  .lang-option {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 28rpx 32rpx;
    transition: background 0.15s ease;

    &:active {
      background: #f8f8f8;
    }

    &.active {
      background: #f5f5f5;

      .lang-option-text {
        color: #000;
        font-weight: 600;
      }
    }

    &:not(:last-child) {
      border-bottom: 1rpx solid #f0f0f0;
    }

    .lang-option-text {
      font-size: 28rpx;
      color: #333;
    }

    .lang-check {
      font-size: 28rpx;
      color: #000;
      font-weight: 600;
    }
  }
</style>

<template>
  <s-layout :bgStyle="{ color: '#fff' }" class="set-wrap" :title="t('systemSetting')">
    <view class="header-box ss-flex-col ss-row-center ss-col-center">
      <image
        class="logo-img ss-m-b-46"
        :src="sheep.$url.cdn(appInfo.logo)"
        mode="aspectFit"
      ></image>
      <view class="name ss-m-b-24">{{ appInfo.name }}</view>
    </view>

    <view class="container-list">
      <uni-list :border="false">
        <uni-list-item
          :title="t('version')"
          :rightText="appInfo.version"
          showArrow
          clickable
          :border="false"
          class="list-border"
          @tap="onCheckUpdate"
        />
        <uni-list-item
          :title="t('language')"
          :rightText="currentLangName"
          showArrow
          clickable
          :border="false"
          class="list-border"
          @tap="onLanguageChange"
        />
        <uni-list-item
          :title="t('localCache')"
          :rightText="storageSize"
          showArrow
          :border="false"
          class="list-border"
        />
        <uni-list-item
          :title="t('about')"
          showArrow
          clickable
          :border="false"
          class="list-border"
          @tap="
            sheep.$router.go('/pages/public/richtext', {
              title: t('about')
            })
          "
        />
        <!-- 为了过审 只有 iOS-App 有注销账号功能 -->
        <uni-list-item
          v-if="isLogin && sheep.$platform.os === 'ios' && sheep.$platform.name === 'App'"
          :title="t('logoff')"
          rightText=""
          showArrow
          clickable
          :border="false"
          class="list-border"
          @click="onLogoff"
        />
      </uni-list>
    </view>
    <view class="set-footer ss-flex-col ss-row-center ss-col-center">
      <view class="agreement-box ss-flex ss-col-center ss-m-b-40">
        <view class="ss-flex ss-col-center ss-m-b-10">
          <view
            class="tcp-text"
            @tap="
              sheep.$router.go('/pages/public/richtext', {
                title: t('userAgreement')
              })
            "
          >
            《{{ t('userAgreement') }}》
          </view>
          <view class="agreement-text">{{ t('agreementAnd') }}</view>
          <view
            class="tcp-text"
            @tap="
              sheep.$router.go('/pages/public/richtext', {
                title: t('privacyPolicy')
              })
            "
          >
            《{{ t('privacyPolicy') }}》
          </view>
        </view>
      </view>
      <view class="copyright-text ss-m-b-10">{{ appInfo.copyright }}</view>
      <view class="copyright-text">{{ appInfo.copytime }}</view>
    </view>
    <su-fixed bottom placeholder>
      <view class="ss-p-x-20 ss-p-b-40">
        <button
          class="loginout-btn ss-reset-button ui-BG-Main ui-Shadow-Main"
          @tap="onLogout"
          v-if="isLogin"
        >
          {{ t('logout') }}
        </button>
      </view>
    </su-fixed>
  </s-layout>
</template>

<script setup>
  import sheep from '@/sheep';
  import { computed, reactive } from 'vue';
  import AuthUtil from '@/sheep/api/member/auth';
  import { useI18n, getLocaleValue, setLocale, getLocaleList } from '@/sheep/i18n';
  import { setPageTitle } from '@/sheep/i18n/pageTitle';

  const { t } = useI18n('app');

  const appInfo = computed(() => sheep.$store('app').info);
  const isLogin = computed(() => sheep.$store('user').isLogin);
  const storageSize = uni.getStorageInfoSync().currentSize + 'Kb';
  const state = reactive({
    showModal: false,
  });

  // 当前语言显示名称
  const currentLangName = computed(() => {
    const currentLocale = getLocaleValue();
    const langList = getLocaleList();
    const lang = langList.find(item => item.code === currentLocale);
    return lang ? lang.name : currentLocale;
  });

  // 语言切换
  function onLanguageChange() {
    const langList = getLocaleList();
    uni.showActionSheet({
      itemList: langList.map(item => item.name),
      success: (res) => {
        const selectedLang = langList[res.tapIndex];
        if (selectedLang && selectedLang.code !== getLocaleValue()) {
          setLocale(selectedLang.code);
          // 刷新页面以应用新语言
          uni.reLaunch({ url: '/pages/public/setting' });
        }
      }
    });
  }

  function onCheckUpdate() {
    sheep.$platform.checkUpdate();
    // 小程序初始化时已检查更新
    // H5实时更新无需检查
    // App 1.跳转应用市场更新 2.手动热更新 3.整包更新
  }

  // 注销账号
  function onLogoff() {
    uni.showModal({
      title: t('common.tip'),
      content: t('logoffConfirm'),
      success: async function (res) {
        if (!res.confirm) {
          return;
        }
        const { code } = await AuthUtil.logout();
        if (code !== 0) {
          return;
        }
        sheep.$store('user').logout();
        sheep.$router.go('/pages/index/user');
      },
    });
  }

  // 退出账号
  function onLogout() {
    uni.showModal({
      title: t('common.tip'),
      content: t('logoutConfirm'),
      success: async function (res) {
        if (!res.confirm) {
          return;
        }
        const { code } = await AuthUtil.logout();
        if (code !== 0) {
          return;
        }
        sheep.$store('user').logout();
        sheep.$router.go('/pages/index/user');
      },
    });
  }

  setPageTitle('pages/public/setting');
</script>

<style lang="scss" scoped>
  .container-list {
    width: 100%;
  }

  .set-title {
    margin: 0 30rpx;
  }

  .header-box {
    padding: 100rpx 0;

    .logo-img {
      width: 160rpx;
      height: 160rpx;
      border-radius: 50%;
    }

    .name {
      font-size: 42rpx;
      font-weight: 400;
      color: $dark-3;
    }

    .version {
      font-size: 32rpx;
      font-weight: 500;
      line-height: 32rpx;
      color: $gray-b;
    }
  }

  .set-footer {
    margin: 100rpx 0 0 0;

    .copyright-text {
      font-size: 22rpx;
      font-weight: 500;
      color: $gray-c;
      line-height: 30rpx;
    }

    .agreement-box {
      font-size: 26rpx;
      font-weight: 500;

      .tcp-text {
        color: var(--ui-BG-Main);
      }

      .agreement-text {
        color: $dark-9;
      }
    }
  }

  .loginout-btn {
    width: 100%;
    height: 80rpx;
    border-radius: 40rpx;
    font-size: 30rpx;
  }

  .list-border {
    font-size: 28rpx;
    font-weight: 400;
    color: #333333;
    border-bottom: 2rpx solid #eeeeee;
  }

  :deep(.uni-list-item__content-title) {
    font-size: 28rpx;
    font-weight: 500;
    color: #333;
  }

  :deep(.uni-list-item__extra-text) {
    color: #bbbbbb;
    font-size: 28rpx;
  }
</style>

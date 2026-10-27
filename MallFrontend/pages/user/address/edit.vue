<!-- 收货地址的新增/编辑 -->
<template>
  <s-layout :title="state.model.id ? t('editTitle') : t('addTitle')">
    <uni-forms
      ref="addressFormRef"
      v-model="state.model"
      :rules="rules"
      validateTrigger="bind"
      labelWidth="160"
      labelAlign="left"
      border
      :labelStyle="{ fontWeight: 'bold' }"
    >
      <view class="bg-white form-box ss-p-x-30">
        <uni-forms-item name="name" :label="t('nameField')" class="form-item">
          <uni-easyinput
            v-model="state.model.name"
            :placeholder="t('namePlaceholder')"
            :inputBorder="false"
            placeholderStyle="color:#BBBBBB;font-size:30rpx;font-weight:400;line-height:normal"
          />
        </uni-forms-item>

        <uni-forms-item name="mobile" :label="t('mobile')" class="form-item">
          <uni-easyinput
            v-model="state.model.mobile"
            type="number"
            :placeholder="t('mobilePlaceholder')"
            :inputBorder="false"
            placeholderStyle="color:#BBBBBB;font-size:30rpx;font-weight:400;line-height:normal"
          >
          </uni-easyinput>
        </uni-forms-item>
        <uni-forms-item
          name="areaName"
          :label="t('areaField')"
          @tap="state.showRegion = true"
          class="form-item"
        >
          <uni-easyinput
            v-model="state.model.areaName"
            disabled
            :inputBorder="false"
            :styles="{ disableColor: '#fff', color: '#333' }"
            placeholderStyle="color:#BBBBBB;font-size:30rpx;font-weight:400;line-height:normal"
            :placeholder="t('areaFieldPlaceholder')"
          >
            <template v-slot:right>
              <uni-icons type="right" />
            </template>
          </uni-easyinput>
        </uni-forms-item>
        <uni-forms-item
          name="detailAddress"
          :label="t('detailAddress')"
          :formItemStyle="{ alignItems: 'flex-start' }"
          :labelStyle="{ lineHeight: '5em' }"
          class="textarea-item"
        >
          <uni-easyinput
            :inputBorder="false"
            type="textarea"
            v-model="state.model.detailAddress"
            placeholderStyle="color:#BBBBBB;font-size:30rpx;font-weight:400;line-height:normal"
            :placeholder="t('detailAddressPlaceholder')"
            clearable
          />
        </uni-forms-item>
      </view>
      <view class="ss-m-y-20 bg-white ss-p-x-30 ss-flex ss-row-between ss-col-center default-box">
        <view class="default-box-title"> {{ t('setDefaultAddress') }} </view>
        <su-switch style="transform: scale(0.8)" v-model="state.model.defaultStatus" />
      </view>
    </uni-forms>
    <su-fixed bottom :opacity="false" bg="" placeholder :noFixed="false" :index="10">
      <view class="footer-box ss-flex-col ss-row-between ss-p-20">
        <view class="ss-m-b-20">
          <button class="ss-reset-button save-btn ui-Shadow-Main" @tap="onSave">{{ t('save', 'common') }}</button>
        </view>
        <button v-if="state.model.id" class="ss-reset-button cancel-btn" @tap="onDelete">
          {{ t('delete', 'common') }}
        </button>
      </view>
    </su-fixed>

    <!-- 省市区弹窗 -->
    <su-region-picker
      :show="state.showRegion"
      @cancel="state.showRegion = false"
      @confirm="onRegionConfirm"
    />
  </s-layout>
</template>

<script setup>
  import { ref, reactive, unref } from 'vue';
  import sheep from '@/sheep';
  import { onLoad } from '@dcloudio/uni-app';
  import { isEmpty } from 'lodash-es';
  import { mobile } from '@/sheep/validate/form';
  import AreaApi from '@/sheep/api/system/area';
  import AddressApi from '@/sheep/api/member/address';
  import { useI18n } from '@/sheep/i18n';

  const { t } = useI18n('address');

  const addressFormRef = ref(null);
  const state = reactive({
    showRegion: false,
    model: {
      name: '',
      mobile: '',
      detailAddress: '',
      defaultStatus: false,
      areaName: '',
    },
    rules: {},
  });

  const rules = {
    name: {
      rules: [
        {
          required: true,
          errorMessage: t('nameRequired'),
        },
      ],
    },
    mobile,
    detailAddress: {
      rules: [
        {
          required: true,
          errorMessage: t('detailRequired'),
        },
      ],
    },
    areaName: {
      rules: [
        {
          required: true,
          errorMessage: t('areaRequired'),
        },
      ],
    },
  };

  // 确认选择地区
  const onRegionConfirm = (e) => {
    state.model.areaName = `${e.province_name} ${e.city_name} ${e.district_name}`;
    state.model.areaId = e.district_id;
    state.showRegion = false;
  };

  // 获得地区数据
  const getAreaData = () => {
    if (isEmpty(uni.getStorageSync('areaData'))) {
      AreaApi.getAreaTree().then((res) => {
        if (res.code === 0) {
          uni.setStorageSync('areaData', res.data);
        }
      });
    }
  };

  // 保存收货地址
  const onSave = async () => {
    // 参数校验
    const validate = await unref(addressFormRef)
      .validate()
      .catch((error) => {
        console.log('error: ', error);
      });
    if (!validate) {
      return;
    }

    // 提交请求
    const formData = {
      ...state.model,
    };
    const { code } =
      state.model.id > 0
        ? await AddressApi.updateAddress(formData)
        : await AddressApi.createAddress(formData);
    if (code === 0) {
      sheep.$router.back();
    }
  };

  // 删除收货地址
  const onDelete = () => {
    uni.showModal({
      title: t('hint'),
      content: t('deleteAddressConfirm'),
      success: async function (res) {
        if (!res.confirm) {
          return;
        }
        const { code } = await AddressApi.deleteAddress(state.model.id);
        if (code === 0) {
          sheep.$router.back();
        }
      },
    });
  };

  onLoad(async (options) => {
    // 获得地区数据
    getAreaData();
    // 情况一：基于 id 获得收件地址
    if (options.id) {
      let { code, data } = await AddressApi.getAddress(options.id);
      if (code !== 0) {
        return;
      }
      state.model = data;
    }
    // 情况二：微信导入
    if (options.data) {
      let data = JSON.parse(options.data);
      const areaData = uni.getStorageSync('areaData');
      const findAreaByName = (areas, name) => areas.find((item) => item.name === name);

      let provinceObj = findAreaByName(areaData, data.province_name);
      let cityObj = provinceObj ? findAreaByName(provinceObj.children, data.city_name) : undefined;
      let districtObj = cityObj ? findAreaByName(cityObj.children, data.district_name) : undefined;
      let areaId = (districtObj || cityObj || provinceObj).id;

      state.model = {
        ...state.model,
        areaId,
        areaName: [data.province_name, data.city_name, data.district_name]
          .filter(Boolean)
          .join(' '),
        defaultStatus: false,
        detailAddress: data.address,
        mobile: data.mobile,
        name: data.consignee,
      };
    }
  });
</script>

<style lang="scss" scoped>
  :deep() {
    .uni-forms-item__label .label-text {
      font-size: 28rpx !important;
      color: #333333 !important;
      line-height: normal !important;
    }

    .uni-easyinput__content-input {
      font-size: 28rpx !important;
      color: #333333 !important;
      line-height: normal !important;
      padding-left: 0 !important;
    }

    .uni-easyinput__content-textarea {
      font-size: 28rpx !important;
      color: #333333 !important;
      line-height: normal !important;
      margin-top: 8rpx !important;
    }

    .uni-icons {
      font-size: 40rpx !important;
    }

    .is-textarea-icon {
      margin-top: 22rpx;
    }

    .is-disabled {
      color: #333333;
    }
  }

  .default-box {
    width: 100%;
    box-sizing: border-box;
    height: 100rpx;

    .default-box-title {
      font-size: 28rpx;
      color: #333333;
      line-height: normal;
    }
  }

  .footer-box {
    .save-btn {
      width: 710rpx;
      height: 80rpx;
      border-radius: 40rpx;
      background: linear-gradient(90deg, var(--ui-BG-Main), var(--ui-BG-Main-gradient));
      color: $white;
    }

    .cancel-btn {
      width: 710rpx;
      height: 80rpx;
      border-radius: 40rpx;
      background: var(--ui-BG);
    }
  }
</style>
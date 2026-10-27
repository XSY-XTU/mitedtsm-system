<template>
  <doc-alert title="【交易】交易订单" url="https://doc.iocoder.cn/mall/trade-order/" />
  <doc-alert title="【交易】购物车" url="https://doc.iocoder.cn/mall/trade-cart/" />

  <ContentWrap>
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="120px"
    >
      <el-form-item v-show="false" label="hideId">
        <el-input v-model="formData.id" />
      </el-form-item>
      <el-tabs>
        <!-- 售后 -->
        <el-tab-pane :label="t('mall.trade.config.afterSaleTab')">
          <el-form-item :label="t('mall.trade.config.refundReason')" prop="afterSaleRefundReasons">
            <el-select
              v-model="formData.afterSaleRefundReasons"
              allow-create
              filterable
              multiple
              :placeholder="t('mall.trade.config.refundReasonPlaceholder')"
            >
              <el-option
                v-for="reason in formData.afterSaleRefundReasons"
                :key="reason"
                :label="reason"
                :value="reason"
              />
            </el-select>
          </el-form-item>
          <el-form-item :label="t('mall.trade.config.returnReason')" prop="afterSaleReturnReasons">
            <el-select
              v-model="formData.afterSaleReturnReasons"
              allow-create
              filterable
              multiple
              :placeholder="t('mall.trade.config.returnReasonPlaceholder')"
            >
              <el-option
                v-for="reason in formData.afterSaleReturnReasons"
                :key="reason"
                :label="reason"
                :value="reason"
              />
            </el-select>
          </el-form-item>
        </el-tab-pane>
        <!-- 配送 -->
        <el-tab-pane :label="t('mall.trade.config.deliveryTab')">
          <el-form-item :label="t('mall.trade.config.enableFreeShipping')" prop="deliveryExpressFreeEnabled">
            <el-switch v-model="formData.deliveryExpressFreeEnabled" style="user-select: none" />
            <el-text class="w-full" size="small" type="info"> {{ t('mall.trade.config.enableFreeShippingTip') }}</el-text>
          </el-form-item>
          <el-form-item :label="t('mall.trade.config.freeShippingAmount')" prop="deliveryExpressFreePrice">
            <el-input-number
              v-model="formData.deliveryExpressFreePrice"
              :min="0"
              :precision="2"
              class="!w-xs"
              :placeholder="t('mall.trade.config.freeShippingAmountPlaceholder')"
            />
            <el-text class="w-full" size="small" type="info">
              {{ t('mall.trade.config.freeShippingAmountTip') }}
            </el-text>
          </el-form-item>
          <el-form-item :label="t('mall.trade.config.enablePickUp')" prop="deliveryPickUpEnabled">
            <el-switch v-model="formData.deliveryPickUpEnabled" style="user-select: none" />
          </el-form-item>
        </el-tab-pane>
        <!-- 分销 -->
        <el-tab-pane :label="t('mall.trade.config.distributionTab')">
          <el-form-item :label="t('mall.trade.config.enableCommission')" prop="brokerageEnabled">
            <el-switch v-model="formData.brokerageEnabled" style="user-select: none" />
            <el-text class="w-full" size="small" type="info"> {{ t('mall.trade.config.enableCommissionTip') }}</el-text>
          </el-form-item>
          <el-form-item :label="t('mall.trade.config.commissionMode')" prop="brokerageEnabledCondition">
            <el-radio-group v-model="formData.brokerageEnabledCondition">
              <el-radio
                v-for="dict in getIntDictOptions(DICT_TYPE.BROKERAGE_ENABLED_CONDITION)"
                :key="dict.value"
                :value="dict.value"
              >
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
            <el-text class="w-full" size="small" type="info">
              {{ t('mall.trade.config.commissionModeTip1') }}
            </el-text>
            <el-text class="w-full" size="small" type="info">
              {{ t('mall.trade.config.commissionModeTip2') }}
            </el-text>
          </el-form-item>
          <el-form-item :label="t('mall.trade.config.bindMode')" prop="brokerageBindMode">
            <el-radio-group v-model="formData.brokerageBindMode">
              <el-radio
                v-for="dict in getIntDictOptions(DICT_TYPE.BROKERAGE_BIND_MODE)"
                :key="dict.value"
                :value="dict.value"
              >
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
            <el-text class="w-full" size="small" type="info">
              {{ t('mall.trade.config.bindModeTip1') }}
            </el-text>
            <el-text class="w-full" size="small" type="info">
              {{ t('mall.trade.config.bindModeTip2') }}
            </el-text>
          </el-form-item>
          <el-form-item :label="t('mall.trade.config.posterImage')">
            <UploadImgs v-model="formData.brokeragePosterUrls" height="125px" width="75px" />
            <el-text class="w-full" size="small" type="info">
              {{ t('mall.trade.config.posterImageTip') }}
            </el-text>
          </el-form-item>
          <el-form-item :label="t('mall.trade.config.firstCommissionRate')" prop="brokerageFirstPercent">
            <el-input-number
              v-model="formData.brokerageFirstPercent"
              :max="100"
              :min="0"
              class="!w-xs"
              :placeholder="t('mall.trade.config.firstCommissionRatePlaceholder')"
            />
            <el-text class="w-full" size="small" type="info">
              {{ t('mall.trade.config.firstCommissionRateTip') }}
            </el-text>
          </el-form-item>
          <el-form-item :label="t('mall.trade.config.secondCommissionRate')" prop="brokerageSecondPercent">
            <el-input-number
              v-model="formData.brokerageSecondPercent"
              :max="100"
              :min="0"
              class="!w-xs"
              :placeholder="t('mall.trade.config.secondCommissionRatePlaceholder')"
            />
            <el-text class="w-full" size="small" type="info">
              {{ t('mall.trade.config.secondCommissionRateTip') }}
            </el-text>
          </el-form-item>
          <el-form-item :label="t('mall.trade.config.frozenDays')" prop="brokerageFrozenDays">
            <el-input-number
              v-model="formData.brokerageFrozenDays"
              :min="0"
              class="!w-xs"
              :placeholder="t('mall.trade.config.frozenDaysPlaceholder')"
            />
            <el-text class="w-full" size="small" type="info">
              {{ t('mall.trade.config.frozenDaysTip') }}
            </el-text>
          </el-form-item>
          <el-form-item :label="t('mall.trade.config.minWithdrawAmount')" prop="brokerageWithdrawMinPrice">
            <el-input-number
              v-model="formData.brokerageWithdrawMinPrice"
              :min="0"
              :precision="2"
              class="!w-xs"
              :placeholder="t('mall.trade.config.minWithdrawAmountPlaceholder')"
            />
            <el-text class="w-full" size="small" type="info">
              {{ t('mall.trade.config.minWithdrawAmountTip') }}
            </el-text>
          </el-form-item>
          <el-form-item :label="t('mall.trade.config.withdrawFee')" prop="brokerageWithdrawFeePercent">
            <el-input-number
              v-model="formData.brokerageWithdrawFeePercent"
              :max="100"
              :min="0"
              class="!w-xs"
              :placeholder="t('mall.trade.config.withdrawFeePlaceholder')"
            />
            <el-text class="w-full" size="small" type="info">
              {{ t('mall.trade.config.withdrawFeeTip') }}
            </el-text>
          </el-form-item>
          <el-form-item :label="t('mall.trade.config.withdrawMethod')" prop="brokerageWithdrawTypes">
            <el-checkbox-group v-model="formData.brokerageWithdrawTypes">
              <el-checkbox
                v-for="dict in getIntDictOptions(DICT_TYPE.BROKERAGE_WITHDRAW_TYPE)"
                :key="dict.value"
                :value="dict.value"
              >
                {{ dict.label }}
              </el-checkbox>
            </el-checkbox-group>
            <el-text class="w-full" size="small" type="info"> {{ t('mall.trade.config.withdrawMethodTip') }}</el-text>
          </el-form-item>
        </el-tab-pane>
      </el-tabs>
      <!-- 保存 -->
      <el-form-item>
        <el-button :loading="formLoading" type="primary" @click="submitForm"> {{ t('mall.trade.config.save') }}</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>
</template>

<script lang="ts" setup>
import * as ConfigApi from '@/api/mall/trade/config'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { cloneDeep } from 'lodash-es'

defineOptions({ name: 'TradeConfig' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formRef = ref()
const formData = ref({
  id: null,
  afterSaleRefundReasons: [],
  afterSaleReturnReasons: [],
  deliveryExpressFreeEnabled: false,
  deliveryExpressFreePrice: 0,
  deliveryPickUpEnabled: false,
  brokerageEnabled: false,
  brokerageEnabledCondition: undefined,
  brokerageBindMode: undefined,
  brokeragePosterUrls: [],
  brokerageFirstPercent: 0,
  brokerageSecondPercent: 0,
  brokerageWithdrawMinPrice: 0,
  brokerageWithdrawFeePercent: 0,
  brokerageFrozenDays: 0,
  brokerageWithdrawTypes: []
})
const formRules = reactive({
  deliveryExpressFreePrice: [{ required: true, message: t('mall.trade.config.freeShippingAmountRequired'), trigger: 'blur' }],
  brokerageEnabledCondition: [{ required: true, message: t('mall.trade.config.commissionModeRequired'), trigger: 'blur' }],
  brokerageBindMode: [{ required: true, message: t('mall.trade.config.bindModeRequired'), trigger: 'blur' }],
  brokerageFirstPercent: [{ required: true, message: t('mall.trade.config.firstCommissionRateRequired'), trigger: 'blur' }],
  brokerageSecondPercent: [{ required: true, message: t('mall.trade.config.secondCommissionRateRequired'), trigger: 'blur' }],
  brokerageWithdrawMinPrice: [
    { required: true, message: t('mall.trade.config.minWithdrawAmountRequired'), trigger: 'blur' }
  ],
  brokerageWithdrawFeePercent: [{ required: true, message: t('mall.trade.config.withdrawFeeRequired'), trigger: 'blur' }],
  brokerageFrozenDays: [{ required: true, message: t('mall.trade.config.frozenDaysRequired'), trigger: 'blur' }],
  brokerageWithdrawTypes: [
    {
      required: true,
      message: t('mall.trade.config.withdrawMethodRequired'),
      trigger: 'change'
    }
  ]
})

const submitForm = async () => {
  if (formLoading.value) return
  // 校验表单
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    const data = cloneDeep(unref(formData.value)) as unknown as ConfigApi.ConfigVO
    // 金额放大
    data.deliveryExpressFreePrice = data.deliveryExpressFreePrice * 100
    data.brokerageWithdrawMinPrice = data.brokerageWithdrawMinPrice * 100
    await ConfigApi.saveTradeConfig(data)
    message.success(t('mall.trade.config.saveSuccess'))
  } finally {
    formLoading.value = false
  }
}

/** 查询交易中心配置 */
const getConfig = async () => {
  formLoading.value = true
  try {
    const data = await ConfigApi.getTradeConfig()
    if (data != null) {
      formData.value = data
      // 金额缩小
      formData.value.deliveryExpressFreePrice = data.deliveryExpressFreePrice / 100
      formData.value.brokerageWithdrawMinPrice = data.brokerageWithdrawMinPrice / 100
    }
  } finally {
    formLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getConfig()
})
</script>

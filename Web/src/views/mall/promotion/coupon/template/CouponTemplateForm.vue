<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="auto"
    >
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('mall.promotion.coupon.name')" prop="name">
            <el-input v-model="formData.name" :placeholder="t('mall.promotion.coupon.namePlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('mall.promotion.coupon.productScope')" prop="productScope">
            <el-radio-group v-model="formData.productScope">
              <el-radio
                v-for="dict in getIntDictOptions(DICT_TYPE.PROMOTION_PRODUCT_SCOPE)"
                :key="dict.value"
                :value="dict.value"
              >
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item :label="t('mall.promotion.coupon.description')" prop="description">
            <el-input
              v-model="formData.description"
              :autosize="{ minRows: 2, maxRows: 2 }"
              :clearable="true"
              :show-word-limit="true"
              class="w-1/1!"
              maxlength="512"
              :placeholder="t('mall.promotion.coupon.description')"
              type="textarea"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item
            v-if="formData.productScope === PromotionProductScopeEnum.SPU.scope"
            :label="t('mall.promotion.coupon.product')"
            prop="productSpuIds"
          >
            <SpuShowcase v-model="formData.productSpuIds" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item
            v-if="formData.productScope === PromotionProductScopeEnum.CATEGORY.scope"
            :label="t('mall.promotion.coupon.category')"
            prop="productCategoryIds"
          >
            <ProductCategorySelect v-model="formData.productCategoryIds" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('mall.promotion.coupon.discountType')" prop="discountType">
            <el-radio-group v-model="formData.discountType">
              <el-radio
                v-for="dict in getIntDictOptions(DICT_TYPE.PROMOTION_DISCOUNT_TYPE)"
                :key="dict.value"
                :value="dict.value"
              >
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            v-if="formData.discountType === PromotionDiscountTypeEnum.PRICE.type"
            :label="t('mall.promotion.coupon.discountPriceLabel')"
            prop="discountPrice"
          >
            <el-input-number
              v-model="formData.discountPrice"
              :min="0"
              :precision="2"
              class="mr-2 !w-400px"
              :placeholder="t('mall.promotion.coupon.discountPricePlaceholder')"
            />
            {{ t('mall.promotion.coupon.yuan') }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item
            v-if="formData.discountType === PromotionDiscountTypeEnum.PERCENT.type"
            :label="t('mall.promotion.coupon.discountPercentLabel')"
            prop="discountPercent"
          >
            <el-input-number
              v-model="formData.discountPercent"
              :max="9.9"
              :min="1"
              :precision="1"
              class="mr-2 !w-400px"
              :placeholder="t('mall.promotion.coupon.discountPercentPlaceholder')"
            />
            {{ t('mall.promotion.coupon.zhe') }}
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            v-if="formData.discountType === PromotionDiscountTypeEnum.PERCENT.type"
            :label="t('mall.promotion.coupon.discountLimitPrice')"
            prop="discountLimitPrice"
          >
            <el-input-number
              v-model="formData.discountLimitPrice"
              :min="0"
              :precision="2"
              class="mr-2 !w-400px"
              :placeholder="t('mall.promotion.coupon.discountLimitPricePlaceholder')"
            />
            {{ t('mall.promotion.coupon.yuan') }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('mall.promotion.coupon.usePriceLabel')" prop="usePrice">
            <el-input-number
              v-model="formData.usePrice"
              :min="0"
              :precision="2"
              class="mr-2 !w-400px"
              :placeholder="t('mall.promotion.coupon.usePricePlaceholder')"
            />
            {{ t('mall.promotion.coupon.yuan') }}
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('mall.promotion.coupon.takeType')" prop="takeType">
            <el-radio-group v-model="formData.takeType">
              <el-radio :key="1" :value="1">{{ t('mall.promotion.coupon.takeTypeDirect') }}</el-radio>
              <el-radio :key="2" :value="2">{{ t('mall.promotion.coupon.takeTypeAssign') }}</el-radio>
              <el-radio :key="3" :value="3">{{ t('mall.promotion.coupon.takeTypeNew') }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item v-if="formData.takeType === 1" :label="t('mall.promotion.coupon.totalCount')" prop="totalCount">
            <el-input-number
              v-model="formData.totalCount"
              :min="-1"
              :precision="0"
              class="mr-2 !w-400px"
              :placeholder="t('mall.promotion.coupon.totalCountPlaceholder')"
            />
            {{ t('mall.promotion.coupon.countUnit') }}
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item v-if="formData.takeType === 1" :label="t('mall.promotion.coupon.takeLimitCountLabel')" prop="takeLimitCount">
            <el-input-number
              v-model="formData.takeLimitCount"
              :min="-1"
              :precision="0"
              class="mr-2 !w-400px"
              :placeholder="t('mall.promotion.coupon.takeLimitCountPlaceholder')"
            />
            {{ t('mall.promotion.coupon.countUnit') }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('mall.promotion.coupon.validityType')" prop="validityType">
            <el-radio-group v-model="formData.validityType">
              <el-radio
                v-for="dict in getIntDictOptions(DICT_TYPE.PROMOTION_COUPON_TEMPLATE_VALIDITY_TYPE)"
                :key="dict.value"
                :value="dict.value"
              >
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            v-if="formData.validityType === CouponTemplateValidityTypeEnum.DATE.type"
            :label="t('mall.promotion.coupon.fixedDate')"
            prop="validTimes"
          >
            <el-date-picker
              v-model="formData.validTimes"
              :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 2, 1, 23, 59, 59)]"
              type="datetimerange"
              value-format="x"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item
            v-if="formData.validityType === CouponTemplateValidityTypeEnum.TERM.type"
            :label="t('mall.promotion.coupon.validityDays')"
            prop="fixedStartTerm"
          >
            {{ t('mall.promotion.coupon.fromDay') }}
            <el-input-number
              v-model="formData.fixedStartTerm"
              :min="0"
              :precision="0"
              class="mx-2"
              :placeholder="t('mall.promotion.coupon.todayValid')"
            />
            {{ t('mall.promotion.coupon.toDay') }}
            <el-input-number
              v-model="formData.fixedEndTerm"
              :min="0"
              :precision="0"
              class="mx-2"
              :placeholder="t('mall.promotion.coupon.fixedEndTermPlaceholder')"
            />
            {{ t('mall.promotion.coupon.dayValid') }}
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">{{ t('common.ok') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import * as CouponTemplateApi from '@/api/mall/promotion/coupon/couponTemplate'
import {
  CouponTemplateValidityTypeEnum,
  PromotionDiscountTypeEnum,
  PromotionProductScopeEnum
} from '@/utils/constants'
import SpuShowcase from '@/views/mall/product/spu/components/SpuShowcase.vue'
import ProductCategorySelect from '@/views/mall/product/category/components/ProductCategorySelect.vue'
import { convertToInteger, formatToFraction } from '@/utils'

defineOptions({ name: 'CouponTemplateForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  name: undefined,
  discountType: PromotionDiscountTypeEnum.PRICE.type,
  discountPrice: undefined,
  discountPercent: undefined,
  discountLimitPrice: undefined,
  usePrice: undefined,
  takeType: 1,
  totalCount: undefined,
  takeLimitCount: undefined,
  validityType: CouponTemplateValidityTypeEnum.DATE.type,
  validTimes: [],
  validStartTime: undefined,
  validEndTime: undefined,
  fixedStartTerm: undefined,
  fixedEndTerm: undefined,
  productScope: PromotionProductScopeEnum.ALL.scope,
  description: undefined,
  productScopeValues: [], // 商品范围：值为 品类编号列表 或 商品编号列表 ，用于提交
  productCategoryIds: [], // 仅用于表单，不提交
  productSpuIds: [] // 仅用于表单，不提交
})
const formRules = reactive({
  name: [{ required: true, message: t('mall.promotion.coupon.nameRequired'), trigger: 'blur' }],
  discountType: [{ required: true, message: t('mall.promotion.coupon.discountTypeRequired'), trigger: 'change' }],
  discountPrice: [{ required: true, message: t('mall.promotion.coupon.discountPriceRequired'), trigger: 'blur' }],
  discountPercent: [{ required: true, message: t('mall.promotion.coupon.discountPercentRequired'), trigger: 'blur' }],
  discountLimitPrice: [{ required: true, message: t('mall.promotion.coupon.discountLimitPriceRequired'), trigger: 'blur' }],
  usePrice: [{ required: true, message: t('mall.promotion.coupon.usePriceRequired'), trigger: 'blur' }],
  takeType: [{ required: true, message: t('mall.promotion.coupon.takeTypeRequired'), trigger: 'change' }],
  totalCount: [{ required: true, message: t('mall.promotion.coupon.totalCountRequired'), trigger: 'blur' }],
  takeLimitCount: [{ required: true, message: t('mall.promotion.coupon.takeLimitCountRequired'), trigger: 'blur' }],
  validityType: [{ required: true, message: t('mall.promotion.coupon.validityTypeRequired'), trigger: 'change' }],
  validTimes: [{ required: true, message: t('mall.promotion.coupon.validTimesRequired'), trigger: 'change' }],
  fixedStartTerm: [{ required: true, message: t('mall.promotion.coupon.fixedStartTermRequired'), trigger: 'blur' }],
  fixedEndTerm: [{ required: true, message: t('mall.promotion.coupon.fixedEndTermRequired'), trigger: 'blur' }],
  productScope: [{ required: true, message: t('mall.promotion.coupon.productScopeRequired'), trigger: 'blur' }],
  productSpuIds: [{ required: true, message: t('mall.promotion.coupon.productSpuIdsRequired'), trigger: 'blur' }],
  productCategoryIds: [{ required: true, message: t('mall.promotion.coupon.productCategoryIdsRequired'), trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      const data = await CouponTemplateApi.getCouponTemplate(id)
      formData.value = {
        ...data,
        discountPrice: formatToFraction(data.discountPrice),
        discountPercent:
          data.discountPercent !== undefined ? data.discountPercent / 10.0 : undefined,
        discountLimitPrice: formatToFraction(data.discountLimitPrice),
        usePrice: formatToFraction(data.usePrice),
        validTimes: [data.validStartTime, data.validEndTime]
      }
      // 获得商品范围
      await getProductScope()
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    const data = {
      ...formData.value,
      discountPrice: convertToInteger(formData.value.discountPrice),
      discountPercent:
        formData.value.discountPercent !== undefined
          ? formData.value.discountPercent * 10
          : undefined,
      discountLimitPrice: convertToInteger(formData.value.discountLimitPrice),
      usePrice: convertToInteger(formData.value.usePrice),
      validStartTime:
        formData.value.validTimes && formData.value.validTimes.length === 2
          ? formData.value.validTimes[0]
          : undefined,
      validEndTime:
        formData.value.validTimes && formData.value.validTimes.length === 2
          ? formData.value.validTimes[1]
          : undefined,
      totalCount: formData.value.takeType === 1 ? formData.value.totalCount : -1,
      takeLimitCount: formData.value.takeType === 1 ? formData.value.takeLimitCount : -1
    } as unknown as CouponTemplateApi.CouponTemplateVO

    // 设置商品范围
    setProductScopeValues(data)

    if (formType.value === 'create') {
      await CouponTemplateApi.createCouponTemplate(data)
      message.success(t('common.createSuccess'))
    } else {
      await CouponTemplateApi.updateCouponTemplate(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    name: undefined,
    description: undefined,
    discountType: PromotionDiscountTypeEnum.PRICE.type,
    discountPrice: undefined,
    discountPercent: undefined,
    discountLimitPrice: undefined,
    usePrice: undefined,
    takeType: 1,
    totalCount: undefined,
    takeLimitCount: undefined,
    validityType: CouponTemplateValidityTypeEnum.DATE.type,
    validTimes: [],
    validStartTime: undefined,
    validEndTime: undefined,
    fixedStartTerm: undefined,
    fixedEndTerm: undefined,
    productScope: PromotionProductScopeEnum.ALL.scope,
    productScopeValues: [],
    productSpuIds: [],
    productCategoryIds: []
  }
  formRef.value?.resetFields()
}

/** 获得商品范围 */
const getProductScope = async () => {
  switch (formData.value.productScope) {
    case PromotionProductScopeEnum.SPU.scope:
      // 设置商品编号
      formData.value.productSpuIds = formData.value.productScopeValues
      break
    case PromotionProductScopeEnum.CATEGORY.scope:
      await nextTick(() => {
        let productCategoryIds = formData.value.productScopeValues
        if (Array.isArray(productCategoryIds) && productCategoryIds.length > 0) {
          // 单选时使用数组不能反显
          productCategoryIds = productCategoryIds[0]
        }
        // 设置品类编号
        formData.value.productCategoryIds = productCategoryIds
      })
      break
    default:
      break
  }
}

/** 设置商品范围 */
function setProductScopeValues(data: CouponTemplateApi.CouponTemplateVO) {
  switch (formData.value.productScope) {
    case PromotionProductScopeEnum.SPU.scope:
      data.productScopeValues = formData.value.productSpuIds
      break
    case PromotionProductScopeEnum.CATEGORY.scope:
      data.productScopeValues = Array.isArray(formData.value.productCategoryIds)
        ? formData.value.productCategoryIds
        : [formData.value.productCategoryIds]
      break
    default:
      break
  }
}
</script>

<style lang="scss" scoped></style>

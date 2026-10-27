<template>
  <!-- 核销对话框 -->
  <Dialog v-model="dialogVisible" :title="t('mall.trade.order.pickUpTitle')" width="35%">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="auto"
    >
      <el-row>
        <el-col :span="24">
          <el-form-item prop="pickUpVerifyCode" :label="t('mall.trade.order.pickUpCode')">
            <el-input v-model="formData.pickUpVerifyCode" :placeholder="t('mall.trade.order.pickUpCodePlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button type="primary" :disabled="formLoading" @click="getOrderByPickUpVerifyCodeClick">
        {{ t('common.query') }}
      </el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
  <!-- 核销确认对话框 -->
  <Dialog v-model="detailDialogVisible" :title="t('mall.trade.order.detail')" width="55%">
    <TradeOrderDetail v-if="orderDetails.id" :id="orderDetails.id" :show-pick-up="false" />
    <template #footer>
      <el-button type="primary" :disabled="formLoading" @click="submitForm"> {{ t('mall.trade.order.confirmPickUp') }} </el-button>
      <el-button @click="detailDialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import * as TradeOrderApi from '@/api/mall/trade/order'
import { OrderVO } from '@/api/mall/trade/order'
import { DeliveryTypeEnum, TradeOrderStatusEnum } from '@/utils/constants'
import TradeOrderDetail from '@/views/mall/trade/order/detail/index.vue'

/** 订单核销表单 */
defineOptions({ name: 'OrderPickUpForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const detailDialogVisible = ref(false) // 详情弹窗的是否展示
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formRules = reactive({
  pickUpVerifyCode: [{ required: true, message: t('mall.trade.order.pickUpCodeRequired'), trigger: 'blur' }]
})
const formData = ref({
  pickUpVerifyCode: '' // 核销码
})
const formRef = ref() // 表单 Ref
const orderDetails = ref<OrderVO>({})

/** 打开弹窗 */
const open = async (pickUpVerifyCode: string) => {
  resetForm()
  if(pickUpVerifyCode != null){
    formData.value.pickUpVerifyCode = pickUpVerifyCode;
    await getOrderByPickUpVerifyCode()
  }else{
    dialogVisible.value = true
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 提交请求
  formLoading.value = true
  try {
    await TradeOrderApi.pickUpOrderByVerifyCode(formData.value.pickUpVerifyCode)
    message.success(t('mall.trade.order.pickUpSuccess'))
    detailDialogVisible.value = false
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success', true)
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    pickUpVerifyCode: '' // 核销码
  }
  formRef.value?.resetFields()
}

const getOrderByPickUpVerifyCodeClick = async () => {
  // 校验表单
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  await getOrderByPickUpVerifyCode()
}

/** 查询核销码对应的订单 */
const getOrderByPickUpVerifyCode = async () => {
  formLoading.value = true
  const data = await TradeOrderApi.getOrderByPickUpVerifyCode(formData.value.pickUpVerifyCode)
  formLoading.value = false
  if (data?.deliveryType !== DeliveryTypeEnum.PICK_UP.type) {
    message.error(t('mall.trade.order.orderNotExist'))
    return
  }
  if (data?.status !== TradeOrderStatusEnum.UNDELIVERED.status) {
    message.error(t('mall.trade.order.orderNotPickUpStatus'))
    return
  }
  orderDetails.value = data
  // 显示详情对话框
  detailDialogVisible.value = true
}
</script>

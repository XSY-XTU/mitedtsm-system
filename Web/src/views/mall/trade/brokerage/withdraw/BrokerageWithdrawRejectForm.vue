<template>
  <Dialog :title="t('mall.trade.brokerage.auditReason')" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="auto"
      v-loading="formLoading"
    >
      <el-row>
        <el-col :span="24">
          <el-form-item :label="t('mall.trade.brokerage.rejectReason')" prop="auditReason">
            <el-input v-model="formData.auditReason" type="textarea" :placeholder="t('mall.trade.brokerage.rejectReasonPlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">{{ t('common.ok') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import * as BrokerageWithdrawApi from '@/api/mall/trade/brokerage/withdraw'

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formData = ref({
  id: undefined,
  auditReason: undefined
})
const formRules = reactive({
  auditReason: [{ required: true, message: t('mall.trade.brokerage.rejectReasonRequired'), trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (id: number) => {
  dialogVisible.value = true
  resetForm()
  formData.value.id = id
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
    const data = formData.value as unknown as BrokerageWithdrawApi.BrokerageWithdrawVO
    await BrokerageWithdrawApi.rejectBrokerageWithdraw(data)
    message.success(t('mall.trade.brokerage.rejectSuccess'))
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
    auditReason: undefined
  }
  formRef.value?.resetFields()
}
</script>

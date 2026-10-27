<template>
  <Dialog v-model="dialogVisible" :title="t('mall.trade.brokerage.updatePromoterTitle')" width="500">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="auto"
    >
      <el-form-item :label="t('mall.trade.brokerage.promoter')" prop="bindUserId">
        <el-input
          v-model="formData.bindUserId"
          :placeholder="t('mall.trade.brokerage.promoterIdPlaceholder2')"
          v-loading="formLoading"
        >
          <template #append>
            <el-button @click="handleGetUser"><Icon icon="ep:search" class="mr-5px" /></el-button>
          </template>
        </el-input>
      </el-form-item>
    </el-form>
    <!-- 展示上级推广人的信息 -->
    <el-descriptions v-if="bindUser" :column="1" border>
      <el-descriptions-item :label="t('mall.trade.brokerage.avatar')">
        <el-avatar :src="bindUser.avatar" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.brokerage.nickname')">{{ bindUser.nickname }}</el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.brokerage.promoterQualification')">
        <el-tag v-if="bindUser.brokerageEnabled">{{ t('mall.trade.brokerage.yes') }}</el-tag>
        <el-tag v-else type="info">{{ t('mall.trade.brokerage.no') }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.brokerage.promoterQualificationTime')">
        {{ formatDate(bindUser.brokerageTime) }}
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">{{ t('common.ok') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import * as BrokerageUserApi from '@/api/mall/trade/brokerage/user'
import { formatDate } from '@/utils/formatTime'

/** 修改分销用户 */
defineOptions({ name: 'BrokerageUserUpdateForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formData = ref()
const formRef = ref() // 表单 Ref
const formRules = reactive({
  bindUserId: [{ required: true, message: t('mall.trade.brokerage.promoterRequired2'), trigger: 'blur' }]
})

/** 打开弹窗 */
const open = async (row: BrokerageUserApi.BrokerageUserVO) => {
  resetForm()
  // 设置数据
  formData.value.id = row.id
  formData.value.bindUserId = row.bindUserId
  // 反显上级推广人
  if (row.bindUserId) {
    await handleGetUser()
  }
  dialogVisible.value = true
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
/** 修改上级推广人 */
const submitForm = async () => {
  if (formLoading.value) return
  // 校验表单
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 未查找到合适的上级
  if (!bindUser.value) {
    message.error(t('mall.trade.brokerage.queryPromoterFirst'))
    return
  }

  // 提交请求
  formLoading.value = true
  try {
    // 发起修改
    await BrokerageUserApi.updateBindUser(formData.value)
    message.success(t('common.updateSuccess'))
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
    id: undefined,
    bindUserId: undefined
  }
  formRef.value?.resetFields()
  bindUser.value = undefined
}

/** 查询推广员 */
const bindUser = ref<BrokerageUserApi.BrokerageUserVO>()
const handleGetUser = async () => {
  if (formData.value.bindUserId == formData.value.id) {
    message.error(t('mall.trade.brokerage.cantBindSelf'))
    return
  }
  formLoading.value = true
  bindUser.value = await BrokerageUserApi.getBrokerageUser(formData.value.bindUserId)
  if (!bindUser.value) {
    message.warning(t('mall.trade.brokerage.promoterNotExist'))
  }
  formLoading.value = false
}
</script>

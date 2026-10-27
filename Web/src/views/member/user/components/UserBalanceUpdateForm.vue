<template>
  <Dialog v-model="dialogVisible" :title="t('user.updateBalance')" width="600">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('user.userId')" prop="id">
            <el-input v-model="formData.id" class="!w-240px" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('user.nickname')" prop="nickname">
            <el-input v-model="formData.nickname" class="!w-240px" disabled />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('user.balanceBefore')" prop="balance">
            <el-input :model-value="formData.balance" class="!w-240px" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('user.balanceChangeType')" prop="changeType">
            <el-radio-group v-model="formData.changeType">
              <el-radio :label="1">{{ t('user.pointIncrease') }}</el-radio>
              <el-radio :label="-1">{{ t('user.pointDecrease') }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('user.balanceChange')" prop="changeBalance">
            <el-input-number
              v-model="formData.changeBalance"
              :min="0"
              :precision="2"
              :step="0.1"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('user.balanceResult')">
            <el-input :model-value="balanceResult" class="!w-240px" disabled />
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
import * as UserApi from '@/api/member/user'
import * as WalletApi from '@/api/pay/wallet/balance'
import { convertToInteger, formatToFraction } from '@/utils'
import { useI18n } from '@/hooks/web/useI18n'

/** 修改用户余额表单 */
defineOptions({ name: 'UpdateBalanceForm' })

const { t } = useI18n('member') // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formData = ref({
  id: undefined,
  nickname: undefined,
  balance: '0',
  changeBalance: 0,
  changeType: 1
})
const formRules = reactive({
  changeBalance: [{ required: true, message: t('user.balanceChangeRequired'), trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (id?: number) => {
  dialogVisible.value = true
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      const user = await UserApi.getUser(id)
      const wallet = await WalletApi.getWallet({ userId: user.id || 0 })
      formData.value.id = user.id
      formData.value.nickname = user.nickname
      formData.value.balance = formatToFraction(wallet.balance)
      formData.value.changeType = 1 // 默认增加余额
      formData.value.changeBalance = 0 // 变动余额默认0
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

  if (formData.value.changeBalance <= 0) {
    message.error(t('user.balanceChangeMin'))
    return
  }
  if (convertToInteger(balanceResult.value) < 0) {
    message.error(t('user.balanceResultMin'))
    return
  }

  // 提交请求
  formLoading.value = true
  try {
    await WalletApi.updateWalletBalance({
      userId: formData.value.id,
      balance: convertToInteger(formData.value.changeBalance) * formData.value.changeType
    })

    message.success(t('common.updateSuccess'))
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
    nickname: undefined,
    balance: '0',
    changeBalance: 0,
    changeType: 1
  }
  formRef.value?.resetFields()
}

/** 变动后的余额 */
const balanceResult = computed(() =>
  formatToFraction(
    convertToInteger(formData.value.balance) +
      convertToInteger(formData.value.changeBalance) * formData.value.changeType
  )
)
</script>
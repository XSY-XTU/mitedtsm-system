<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="800px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="auto"
      v-loading="formLoading"
    >
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('demo.withdraw.subject')" prop="subject">
            <el-input v-model="formData.subject" :placeholder="t('demo.withdraw.subjectPlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('demo.withdraw.type')" prop="type">
            <el-radio-group v-model="formData.type">
              <el-radio :value="1">{{ t('demo.withdraw.typeAlipay') }}</el-radio>
              <el-radio :value="2">{{ t('demo.withdraw.typeWxBalance') }}</el-radio>
              <el-radio :value="3">{{ t('demo.withdraw.typeWallet') }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('demo.withdraw.price')" prop="price">
            <el-input-number
              v-model="formData.price"
              :min="0.01"
              :precision="2"
              :step="0.01"
              :placeholder="t('demo.withdraw.pricePlaceholder')"
              style="width: 200px"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('demo.withdraw.userAccount')" prop="userAccount">
            <el-input v-model="formData.userAccount" :placeholder="getAccountPlaceholder()" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('demo.withdraw.userName')" prop="userName">
            <el-input v-model="formData.userName" :placeholder="t('demo.withdraw.userNamePlaceholder')" />
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
import * as DemoWithdrawApi from '@/api/pay/demo/withdraw/index'
import { yuanToFen } from '@/utils'
const { t } = useI18n('pay') // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref<DemoWithdrawApi.PayDemoWithdrawVO>({
  subject: '',
  price: 0,
  type: 1,
  userName: '',
  userAccount: ''
})
const formRules = reactive({
  subject: [{ required: true, message: t('demo.withdraw.subjectRequired'), trigger: 'blur' }],
  price: [{ required: true, message: t('demo.withdraw.priceRequired'), trigger: 'blur' }],
  type: [{ required: true, message: t('demo.withdraw.typeRequired'), trigger: 'change' }],
  userAccount: [{ required: true, message: t('demo.withdraw.userAccountRequired'), trigger: 'blur' }],
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
}
/** 关闭弹窗 */
const close = async () => {
  dialogVisible.value = false
  resetForm()
}
defineExpose({ open, close }) // 提供 open， close 方法，用于打开, 关闭弹窗

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
    const data = { ...formData.value }
    data.price = yuanToFen(data.price)
    if (formType.value === 'create') {
      await DemoWithdrawApi.createDemoWithdraw(data)
      message.success(t('common.createSuccess'))
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
    subject: '',
    price: 0,
    type: 1,
    userName: '',
    userAccount: ''
  }
  formRef.value?.resetFields()
}

/** 根据提现类型获取账号输入框的占位符文本 */
const getAccountPlaceholder = () => {
  if (formData.value.type === 1) {
    return t('demo.withdraw.alipayAccountPlaceholder')
  } else if (formData.value.type === 2) {
    return t('demo.withdraw.wxOpenidPlaceholder')
  } else if (formData.value.type === 3) {
    return t('demo.withdraw.walletIdPlaceholder')
  }
  return t('demo.withdraw.userAccountPlaceholder')
}
</script>
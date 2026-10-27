<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="rules"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('account.name')" prop="name">
            <el-input v-model="formData.name" :placeholder="t('account.namePlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('account.wechatAccount')" prop="account">
            <template #label>
              <span>
                <el-tooltip
                  :content="t('account.wechatAccountTip')"
                  placement="top"
                >
                  <Icon icon="ep:question-filled" style="vertical-align: middle" />
                </el-tooltip>
                {{ t('account.wechatAccount') }}
              </span>
            </template>
            <el-input v-model="formData.account" :placeholder="t('account.wechatAccountPlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="appId" prop="appId">
            <template #label>
              <span>
                <el-tooltip
                  :content="t('account.appIdTip')"
                  placement="top"
                >
                  <Icon icon="ep:question-filled" style="vertical-align: middle" />
                </el-tooltip>
                appId
              </span>
            </template>
            <el-input v-model="formData.appId" :placeholder="t('account.appIdPlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="appSecret" prop="appSecret">
            <template #label>
              <span>
                <el-tooltip
                  :content="t('account.appSecretTip')"
                  placement="top"
                >
                  <Icon icon="ep:question-filled" style="vertical-align: middle" />
                </el-tooltip>
                appSecret
              </span>
            </template>
            <el-input v-model="formData.appSecret" :placeholder="t('account.appSecretPlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="token" prop="token">
            <el-input v-model="formData.token" :placeholder="t('account.tokenPlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('account.aesKey')" prop="aesKey">
            <el-input v-model="formData.aesKey" :placeholder="t('account.aesKeyPlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item :label="t('account.remark')" prop="remark">
            <el-input v-model="formData.remark" :placeholder="t('common.inputText')" type="textarea" />
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
import * as AccountApi from '@/api/mp/account'

defineOptions({ name: 'MpAccountForm' })

const { t } = useI18n('mp') // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  name: '',
  account: '',
  appId: '',
  appSecret: '',
  token: '',
  aesKey: '',
  remark: ''
})
const rules = reactive({
  name: [{ required: true, message: t('account.nameRequired'), trigger: 'blur' }],
  account: [{ required: true, message: t('account.accountRequired'), trigger: 'blur' }],
  appId: [{ required: true, message: t('account.appIdRequired'), trigger: 'blur' }],
  appSecret: [{ required: true, message: t('account.appSecretRequired'), trigger: 'blur' }],
  token: [{ required: true, message: t('account.tokenRequired'), trigger: 'blur' }]
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
      formData.value = await AccountApi.getAccount(id)
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
    const data = formData.value
    if (formType.value === 'create') {
      await AccountApi.createAccount(data)
      message.success(t('common.createSuccess'))
    } else {
      await AccountApi.updateAccount(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 表单重置 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    name: '',
    account: '',
    appId: '',
    appSecret: '',
    token: '',
    aesKey: '',
    remark: ''
  }
  formRef.value?.resetFields()
}
</script>

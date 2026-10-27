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
          <el-form-item :label="t('system.mail.mail')" prop="mail">
            <el-input v-model="formData.mail" :placeholder="t('system.mail.mailPlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('system.mail.username')" prop="username">
            <el-input v-model="formData.username" :placeholder="t('system.mail.usernamePlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('system.mail.password')" prop="password">
            <el-input
              v-model="formData.password"
              :placeholder="t('system.mail.passwordPlaceholder')"
              type="password"
              show-password
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('system.mail.host')" prop="host">
            <el-input v-model="formData.host" :placeholder="t('system.mail.hostPlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('system.mail.port')" prop="port">
            <el-input-number
              v-model="formData.port"
              :placeholder="t('system.mail.portPlaceholder')"
              :min="1"
              :max="65535"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('system.mail.sslEnable')">
            <el-radio-group v-model="formData.sslEnable">
              <el-radio
                v-for="dict in getBoolDictOptions(DICT_TYPE.INFRA_BOOLEAN_STRING)"
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
        <el-col :span="12">
          <el-form-item :label="t('system.mail.starttlsEnable')">
            <el-radio-group v-model="formData.starttlsEnable">
              <el-radio
                v-for="dict in getBoolDictOptions(DICT_TYPE.INFRA_BOOLEAN_STRING)"
                :key="dict.value"
                :value="dict.value"
              >
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
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
import { DICT_TYPE, getBoolDictOptions } from '@/utils/dict'
import * as MailAccountApi from '@/api/system/mail/account'

defineOptions({ name: 'SystemMailAccountForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  mail: '',
  username: '',
  password: '',
  host: '',
  port: 465,
  sslEnable: true,
  starttlsEnable: false
})
const formRules = reactive({
  mail: [
    { required: true, message: t('system.mail.mailRequired'), trigger: 'blur' },
    { type: 'email', message: t('system.mail.mailFormat'), trigger: ['blur', 'change'] }
  ],
  username: [{ required: true, message: t('system.mail.usernameRequired'), trigger: 'blur' }],
  password: [{ required: true, message: t('system.mail.passwordRequired'), trigger: 'blur' }],
  host: [{ required: true, message: t('system.mail.hostRequired'), trigger: 'blur' }],
  port: [{ required: true, message: t('system.mail.portRequired'), trigger: 'blur' }],
  sslEnable: [{ required: true, message: t('system.mail.sslEnableRequired'), trigger: 'blur' }],
  starttlsEnable: [{ required: true, message: t('system.mail.starttlsEnableRequired'), trigger: 'blur' }]
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
      formData.value = await MailAccountApi.getMailAccount(id)
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
    const data = formData.value as MailAccountApi.MailAccountVO
    if (formType.value === 'create') {
      await MailAccountApi.createMailAccount(data)
      message.success(t('common.createSuccess'))
    } else {
      await MailAccountApi.updateMailAccount(data)
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
    mail: '',
    username: '',
    password: '',
    host: '',
    port: 465,
    sslEnable: true,
    starttlsEnable: false
  }
  formRef.value?.resetFields()
}
</script>
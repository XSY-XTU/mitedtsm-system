<template>
  <Dialog v-model="dialogVisible" :title="t('system.smsTemplate.test')">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="auto"
    >
      <el-row>
        <el-col :span="24">
          <el-form-item :label="t('system.smsTemplate.content')" prop="content">
            <el-input
              v-model="formData.content"
              :placeholder="t('system.smsTemplate.contentPlaceholder')"
              readonly
              type="textarea"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('system.smsLog.mobile')" prop="mobile">
            <el-input v-model="formData.mobile" :placeholder="t('system.smsTemplate.mobilePlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col
          v-for="param in formData.params"
          :key="param"
          :span="12"
        >
          <el-form-item
            :label="t('system.smsTemplate.paramLabel', { param: param })"
            :prop="'templateParams.' + param"
          >
            <el-input
              v-model="formData.templateParams[param]"
              :placeholder="t('system.smsTemplate.paramPlaceholder', { param: param })"
            />
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
import * as SmsTemplateApi from '@/api/system/sms/smsTemplate'

defineOptions({ name: 'SystemSmsTemplateSendForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用

// 发送短信表单相关
const formData = ref({
  content: '',
  params: {},
  mobile: '',
  templateCode: '',
  templateParams: new Map()
})
const formRules = reactive({
  mobile: [{ required: true, message: t('system.smsTemplate.mobileRequired'), trigger: 'blur' }],
  templateCode: [{ required: true, message: t('system.smsTemplate.templateCodeRequired'), trigger: 'blur' }],
  templateParams: {}
})
const formRef = ref() // 表单 Ref

const open = async (id: number) => {
  dialogVisible.value = true
  resetForm()
  // 设置数据
  formLoading.value = true
  try {
    const data = await SmsTemplateApi.getSmsTemplate(id)
    // 设置动态表单
    formData.value.content = data.content
    formData.value.params = data.params
    formData.value.templateCode = data.code
    formData.value.templateParams = data.params.reduce((obj, item) => {
      obj[item] = '' // 给每个动态属性赋值，避免无法读取
      return obj
    }, {})
    formRules.templateParams = data.params.reduce((obj, item) => {
      obj[item] = { required: true, message: t('system.smsTemplate.paramRequired', { param: item }), trigger: 'blur' }
      return obj
    }, {})
  } finally {
    formLoading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const submitForm = async () => {
  // 校验表单
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as SmsTemplateApi.SendSmsReqVO
    const logId = await SmsTemplateApi.sendSms(data)
    if (logId) {
      message.success(t('system.smsTemplate.sendSuccess', { logId: logId }))
    }
    dialogVisible.value = false
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    content: '',
    params: {},
    mobile: '',
    templateCode: '',
    templateParams: new Map()
  }
  formRef.value?.resetFields()
}
</script>

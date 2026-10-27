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
          <el-form-item :label="t('system.smsTemplate.channelId')" prop="channelId">
            <el-select v-model="formData.channelId" :placeholder="t('system.smsTemplate.channelIdPlaceholder')">
              <el-option
                v-for="channel in channelList"
                :key="channel.id"
                :label="
                  channel.signature +
                  `【 ${getDictLabel(DICT_TYPE.SYSTEM_SMS_CHANNEL_CODE, channel.code)}】`
                "
                :value="channel.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('system.smsTemplate.type')" prop="type">
            <el-select v-model="formData.type" :placeholder="t('system.smsTemplate.typePlaceholder')">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.SYSTEM_SMS_TEMPLATE_TYPE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('system.smsTemplate.code')" prop="code">
            <el-input v-model="formData.code" :placeholder="t('system.smsTemplate.codePlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('system.smsTemplate.name')" prop="name">
            <el-input v-model="formData.name" :placeholder="t('system.smsTemplate.namePlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('system.smsTemplate.status')" prop="status">
            <el-radio-group v-model="formData.status">
              <el-radio
                v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
                :key="dict.value"
                :value="dict.value"
              >
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('system.smsTemplate.apiTemplateId')" prop="apiTemplateId">
            <el-input v-model="formData.apiTemplateId" :placeholder="t('system.smsTemplate.apiTemplateIdPlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item :label="t('system.smsTemplate.content')" prop="content">
            <el-input v-model="formData.content" :placeholder="t('system.smsTemplate.contentPlaceholder')" type="textarea" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item :label="t('system.smsTemplate.remark')" prop="remark">
            <el-input v-model="formData.remark" :placeholder="t('system.smsTemplate.remarkPlaceholder')" />
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
import { DICT_TYPE, getDictLabel, getIntDictOptions } from '@/utils/dict'
import * as SmsTemplateApi from '@/api/system/sms/smsTemplate'
import * as SmsChannelApi from '@/api/system/sms/smsChannel'
import { CommonStatusEnum } from '@/utils/constants'

defineOptions({ name: 'SystemSmsTemplateForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型
const formData = ref<SmsTemplateApi.SmsTemplateVO>({
  id: undefined,
  type: undefined,
  status: CommonStatusEnum.ENABLE,
  code: '',
  name: '',
  content: '',
  remark: '',
  apiTemplateId: '',
  channelId: undefined
})
const formRules = reactive({
  type: [{ required: true, message: t('system.smsTemplate.typeRequired'), trigger: 'change' }],
  status: [{ required: true, message: t('system.smsTemplate.statusRequired'), trigger: 'blur' }],
  code: [{ required: true, message: t('system.smsTemplate.codeRequired'), trigger: 'blur' }],
  name: [{ required: true, message: t('system.smsTemplate.nameRequired'), trigger: 'blur' }],
  content: [{ required: true, message: t('system.smsTemplate.contentRequired'), trigger: 'blur' }],
  apiTemplateId: [{ required: true, message: t('system.smsTemplate.apiTemplateIdRequired'), trigger: 'blur' }],
  channelId: [{ required: true, message: t('system.smsTemplate.channelIdRequired'), trigger: 'change' }]
})
const formRef = ref() // 表单 Ref
const channelList = ref<SmsChannelApi.SmsChannelVO[]>([]) // 短信渠道列表

const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await SmsTemplateApi.getSmsTemplate(id)
    } finally {
      formLoading.value = false
    }
  }
  // 加载渠道列表
  channelList.value = await SmsChannelApi.getSimpleSmsChannelList()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  formLoading.value = true
  try {
    const data = formData.value as SmsTemplateApi.SmsTemplateVO
    if (formType.value === 'create') {
      await SmsTemplateApi.createSmsTemplate(data)
      message.success(t('common.createSuccess'))
    } else {
      await SmsTemplateApi.updateSmsTemplate(data)
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
    type: undefined,
    status: CommonStatusEnum.ENABLE,
    code: '',
    name: '',
    content: '',
    remark: '',
    apiTemplateId: '',
    channelId: undefined
  }
  formRef.value?.resetFields()
}
</script>

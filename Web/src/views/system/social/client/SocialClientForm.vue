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
          <el-form-item :label="t('system.social.appName')" prop="name">
            <el-input v-model="formData.name" :placeholder="t('system.social.appNamePlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('system.social.socialPlatform')" prop="socialType">
            <el-radio-group v-model="formData.socialType">
              <el-radio
                v-for="dict in getIntDictOptions(DICT_TYPE.SYSTEM_SOCIAL_TYPE)"
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
          <el-form-item :label="t('system.social.userType')" prop="userType">
            <el-radio-group v-model="formData.userType">
              <el-radio
                v-for="dict in getIntDictOptions(DICT_TYPE.USER_TYPE)"
                :key="dict.value"
                :value="dict.value"
              >
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('system.social.status')" prop="status">
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
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('system.social.clientId')" prop="clientId">
            <el-input v-model="formData.clientId" :placeholder="t('system.social.clientIdPlaceholderTip')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('system.social.clientSecret')" prop="clientSecret">
            <el-input
              v-model="formData.clientSecret"
              :placeholder="t('system.social.clientSecretPlaceholderTip')"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="formData!.socialType === 30">
        <el-col :span="12">
          <el-form-item :label="t('system.social.agentId')" prop="agentId">
            <el-input v-model="formData.agentId" :placeholder="t('system.social.agentIdPlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="formData!.socialType === 40">
        <el-col :span="12">
          <el-form-item :label="t('system.social.publicKey')" prop="publicKey">
            <el-input v-model="formData.publicKey" :placeholder="t('system.social.publicKeyPlaceholder')" />
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
import * as SocialClientApi from '@/api/system/social/client'

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  name: undefined,
  socialType: undefined,
  userType: undefined,
  clientId: undefined,
  clientSecret: undefined,
  publicKey: undefined,
  agentId: undefined,
  status: 0
})
const formRules = reactive({
  name: [{ required: true, message: t('system.social.appNameRequired'), trigger: 'blur' }],
  socialType: [{ required: true, message: t('system.social.socialPlatformRequired'), trigger: 'blur' }],
  userType: [{ required: true, message: t('system.social.userTypeRequired'), trigger: 'blur' }],
  clientId: [{ required: true, message: t('system.social.clientIdRequired'), trigger: 'blur' }],
  clientSecret: [{ required: true, message: t('system.social.clientSecretRequired'), trigger: 'blur' }],
  status: [{ required: true, message: t('system.social.statusRequired'), trigger: 'blur' }]
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
      formData.value = await SocialClientApi.getSocialClient(id)
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
    const data = formData.value as unknown as SocialClientApi.SocialClientVO
    if (formType.value === 'create') {
      await SocialClientApi.createSocialClient(data)
      message.success(t('common.createSuccess'))
    } else {
      await SocialClientApi.updateSocialClient(data)
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
    socialType: undefined,
    userType: undefined,
    clientId: undefined,
    clientSecret: undefined,
    publicKey: undefined,
    agentId: undefined,
    status: 0
  }
  formRef.value?.resetFields()
}
</script>
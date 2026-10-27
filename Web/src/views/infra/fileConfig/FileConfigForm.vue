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
          <el-form-item :label="t('fileConfig.name')" prop="name">
            <el-input v-model="formData.name" :placeholder="t('fileConfig.namePlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('fileConfig.storage')" prop="storage">
            <el-select
              v-model="formData.storage"
              :disabled="formData.id !== undefined"
              :placeholder="t('common.selectPlaceholder')"
            >
              <el-option
                v-for="dict in getDictOptions(DICT_TYPE.INFRA_FILE_STORAGE)"
                :key="dict.value"
                :label="dict.label"
                :value="parseInt(dict.value)"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item :label="t('common.remark')" prop="remark">
            <el-input v-model="formData.remark" :placeholder="t('common.inputPlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <!-- DB -->
      <!-- Local / FTP / SFTP -->
      <el-row v-if="formData.storage >= 10 && formData.storage <= 12">
        <el-col :span="12">
          <el-form-item
            :label="t('fileConfig.basePath')"
            prop="config.basePath"
          >
            <el-input v-model="formData.config.basePath" :placeholder="t('fileConfig.basePathPlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="formData.storage >= 11 && formData.storage <= 12">
        <el-col :span="12">
          <el-form-item
            :label="t('fileConfig.host')"
            prop="config.host"
          >
            <el-input v-model="formData.config.host" :placeholder="t('fileConfig.hostPlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            :label="t('fileConfig.port')"
            prop="config.port"
          >
            <el-input-number v-model="formData.config.port" :min="0" :placeholder="t('fileConfig.portPlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="formData.storage >= 11 && formData.storage <= 12">
        <el-col :span="12">
          <el-form-item
            :label="t('fileConfig.username')"
            prop="config.username"
          >
            <el-input v-model="formData.config.username" :placeholder="t('fileConfig.usernamePlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            :label="t('fileConfig.password')"
            prop="config.password"
          >
            <el-input v-model="formData.config.password" :placeholder="t('fileConfig.passwordPlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="formData.storage === 11">
        <el-col :span="12">
          <el-form-item :label="t('fileConfig.mode')" prop="config.mode">
            <el-radio-group v-model="formData.config.mode">
              <el-radio key="Active" value="Active">{{ t('fileConfig.activeMode') }}</el-radio>
              <el-radio key="Passive" value="Passive">{{ t('fileConfig.passiveMode') }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <!-- S3 -->
      <el-row v-if="formData.storage === 20">
        <el-col :span="12">
          <el-form-item :label="t('fileConfig.endpoint')" prop="config.endpoint">
            <el-input v-model="formData.config.endpoint" :placeholder="t('fileConfig.endpointPlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('fileConfig.bucket')" prop="config.bucket">
            <el-input v-model="formData.config.bucket" :placeholder="t('fileConfig.bucketPlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="formData.storage === 20">
        <el-col :span="12">
          <el-form-item :label="t('fileConfig.accessKey')" prop="config.accessKey">
            <el-input v-model="formData.config.accessKey" :placeholder="t('fileConfig.accessKeyPlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('fileConfig.accessSecret')" prop="config.accessSecret">
            <el-input v-model="formData.config.accessSecret" :placeholder="t('fileConfig.accessSecretPlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="formData.storage === 20">
        <el-col :span="12">
          <el-form-item
            :label="t('fileConfig.pathStyle')"
            prop="config.enablePathStyleAccess"
          >
            <el-radio-group v-model="formData.config.enablePathStyleAccess">
              <el-radio key="true" :value="true">{{ t('common.enable') }}</el-radio>
              <el-radio key="false" :value="false">{{ t('common.disable') }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            :label="t('fileConfig.publicAccess')"
            prop="config.enablePublicAccess"
          >
            <el-radio-group v-model="formData.config.enablePublicAccess">
              <el-radio key="true" :value="true">{{ t('fileConfig.public') }}</el-radio>
              <el-radio key="false" :value="false">{{ t('fileConfig.private') }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="formData.storage === 20">
        <el-col :span="12">
          <el-form-item :label="t('fileConfig.region')">
            <!-- 选填，无需参数校验 -->
            <el-input v-model="formData.config.region" :placeholder="t('fileConfig.regionPlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('fileConfig.domain')">
            <!-- 无需参数校验，所以去掉 prop -->
            <el-input v-model="formData.config.domain" :placeholder="t('fileConfig.domainPlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <!-- 通用 -->
      <el-row v-if="formData.storage && formData.storage !== 20">
        <el-col :span="12">
          <el-form-item :label="t('fileConfig.domain')" prop="config.domain">
            <el-input v-model="formData.config.domain" :placeholder="t('fileConfig.domainPlaceholder')" />
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
import { useI18n } from '@/hooks/web/useI18n'
import { DICT_TYPE, getDictOptions } from '@/utils/dict'
import * as FileConfigApi from '@/api/infra/fileConfig'
import { FormRules } from 'element-plus'

defineOptions({ name: 'InfraFileConfigForm' })

const { t } = useI18n('infra')
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  name: '',
  storage: 0,
  remark: '',
  config: {} as FileConfigApi.FileClientConfig
})
const formRules = reactive<FormRules>({
  name: [{ required: true, message: t('fileConfig.nameRequired'), trigger: 'blur' }],
  storage: [{ required: true, message: t('fileConfig.storageRequired'), trigger: 'change' }],
  config: {
    basePath: [{ required: true, message: t('fileConfig.basePathRequired'), trigger: 'blur' }],
    host: [{ required: true, message: t('fileConfig.hostRequired'), trigger: 'blur' }],
    port: [{ required: true, message: t('fileConfig.portRequired'), trigger: 'blur' }],
    username: [{ required: true, message: t('fileConfig.usernameRequired'), trigger: 'blur' }],
    password: [{ required: true, message: t('fileConfig.passwordRequired'), trigger: 'blur' }],
    mode: [{ required: true, message: t('fileConfig.modeRequired'), trigger: 'change' }],
    endpoint: [{ required: true, message: t('fileConfig.endpointRequired'), trigger: 'blur' }],
    bucket: [{ required: true, message: t('fileConfig.bucketRequired'), trigger: 'blur' }],
    accessKey: [{ required: true, message: t('fileConfig.accessKeyRequired'), trigger: 'blur' }],
    accessSecret: [{ required: true, message: t('fileConfig.accessSecretRequired'), trigger: 'blur' }],
    enablePathStyleAccess: [
      { required: true, message: t('fileConfig.pathStyleRequired'), trigger: 'change' }
    ],
    enablePublicAccess: [{ required: true, message: t('fileConfig.publicAccessRequired'), trigger: 'change' }],
    domain: [{ required: true, message: t('fileConfig.domainRequired'), trigger: 'blur' }]
  } as FormRules
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
      formData.value = await FileConfigApi.getFileConfig(id)
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
    const data = formData.value as unknown as FileConfigApi.FileConfigVO
    if (formType.value === 'create') {
      await FileConfigApi.createFileConfig(data)
      message.success(t('common.createSuccess'))
    } else {
      await FileConfigApi.updateFileConfig(data)
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
    name: '',
    storage: undefined!,
    remark: '',
    config: {} as FileConfigApi.FileClientConfig
  }
  formRef.value?.resetFields()
}
</script>

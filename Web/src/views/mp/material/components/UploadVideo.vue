<template>
  <el-dialog :title="t('material.createVideo')" v-model="showDialog" width="600px">
    <el-upload
      :action="UPLOAD_URL"
      :headers="HEADERS"
      multiple
      :limit="1"
      :file-list="fileList"
      :data="uploadData"
      :before-upload="beforeVideoUpload"
      :on-error="onUploadError"
      :on-success="onUploadSuccess"
      ref="uploadVideoRef"
      :auto-upload="false"
      class="mb-5"
    >
      <template #trigger>
        <el-button type="primary" plain>{{ t('material.selectVideo') }}</el-button>
      </template>
      <template #tip>
        <span class="el-upload__tip" style="margin-left: 10px"
          >{{ t('material.videoTip') }}</span
        >
      </template>
    </el-upload>
    <el-divider />
    <el-form :model="uploadData" :rules="uploadRules" ref="uploadFormRef">
      <el-form-item :label="t('material.videoTitle')" prop="title">
        <el-input
          v-model="uploadData.title"
          :placeholder="t('material.videoTitlePlaceholder')"
        />
      </el-form-item>
      <el-form-item :label="t('material.videoIntroduction')" prop="introduction">
        <el-input
          :rows="3"
          type="textarea"
          v-model="uploadData.introduction"
          :placeholder="t('material.videoIntroductionPlaceholder')"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="showDialog = false">{{ t('common.cancel') }}</el-button>
      <el-button type="primary" @click="submitVideo">{{ t('common.ok') }}</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import type {
  FormInstance,
  FormRules,
  UploadInstance,
  UploadProps,
  UploadUserFile
} from 'element-plus'
import { HEADERS, UploadData, UPLOAD_URL, UploadType, beforeVideoUpload } from './upload'

const message = useMessage()
const { t } = useI18n('mp') // 国际化

const accountId = inject<number>('accountId')

const uploadRules: FormRules = {
  title: [{ required: true, message: t('material.videoTitleRequired'), trigger: 'blur' }],
  introduction: [{ required: true, message: t('material.videoIntroductionRequired'), trigger: 'blur' }]
}

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})
const emit = defineEmits<{
  (e: 'update:modelValue', v: boolean)
  (e: 'uploaded', v: void)
}>()

const showDialog = computed<boolean>({
  get() {
    return props.modelValue
  },
  set(val) {
    emit('update:modelValue', val)
  }
})

const fileList = ref<UploadUserFile[]>([])

const uploadData: UploadData = reactive({
  type: UploadType.Video,
  title: '',
  introduction: '',
  accountId: accountId!
})

const uploadFormRef = ref<FormInstance | null>(null)
const uploadVideoRef = ref<UploadInstance | null>(null)

const submitVideo = () => {
  uploadFormRef.value?.validate((valid) => {
    if (!valid) {
      return
    }
    uploadVideoRef.value?.submit()
  })
}

/** 上传成功处理 */
const onUploadSuccess: UploadProps['onSuccess'] = (res: any) => {
  if (res.code !== 0) {
    message.error(t('material.uploadError') + '：' + res.msg)
    return false
  }

  // 清空上传时的各种数据
  fileList.value = []
  uploadData.title = ''
  uploadData.introduction = ''

  showDialog.value = false
  message.notifySuccess(t('common.uploadSuccess'))
  emit('uploaded')
}

/** 上传失败处理 */
const onUploadError = (err: Error) => message.error(t('common.uploadFailed') + `: ${err.message}`)
</script>

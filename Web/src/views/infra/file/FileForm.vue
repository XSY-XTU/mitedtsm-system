<template>
  <Dialog v-model="dialogVisible" :title="t('largeFile.title')" width="600px" :before-close="handleBeforeClose">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="auto">
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('file.directory')" prop="directory">
            <el-input v-model="formData.directory" :placeholder="t('file.directoryPlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('largeFile.chunkSize')" prop="chunkSize">
            <el-select v-model="formData.chunkSize" style="width: 100%">
              <el-option label="1 MB" :value="1 * 1024 * 1024" />
              <el-option label="2 MB" :value="2 * 1024 * 1024" />
              <el-option label="5 MB" :value="5 * 1024 * 1024" />
              <el-option label="10 MB" :value="10 * 1024 * 1024" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item :label="t('largeFile.selectFile')">
            <el-upload
              ref="uploadRef"
              v-model:file-list="fileList"
              :auto-upload="false"
              :limit="1"
              :on-change="handleFileChange"
              :on-exceed="handleExceed"
              drag
            >
              <Icon icon="ep:upload-filled" :size="40" />
              <div class="el-upload__text">{{ t('largeFile.dragTip') }} <em>{{ t('largeFile.clickUpload') }}</em></div>
              <template #tip>
                <div class="el-upload__tip">
                  {{ t('largeFile.uploadTip') }}<br />
                  <span class="text-warning">{{ t('largeFile.resumeTip') }}</span>
                </div>
              </template>
            </el-upload>
          </el-form-item>
        </el-col>
      </el-row>
      <!-- 上传进度 -->
      <el-row v-if="uploading">
        <el-col :span="24">
          <el-form-item :label="t('largeFile.uploadProgress')">
            <div style="width: 100%">
              <el-progress
                :percentage="uploadProgress"
                :status="uploadStatus"
                :stroke-width="20"
                :text-inside="true"
              />
              <div class="mt-2 text-sm text-gray-500">
                {{ uploadProgressText }}
              </div>
            </div>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button
        :disabled="formLoading || uploading"
        type="primary"
        @click="submitFileForm"
      >
        {{ t('common.ok') }}
      </el-button>
      <el-button :disabled="uploading" @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import { useI18n } from '@/hooks/web/useI18n'
import * as LargeFileApi from '@/api/infra/largeFile'

defineOptions({ name: 'InfraFileForm' })

const { t } = useI18n('infra')
const message = useMessage()

const dialogVisible = ref(false)
const formLoading = ref(false)
const uploading = ref(false)
const fileList = ref([])
const uploadRef = ref()
const formRef = ref()

const formData = ref({
  directory: '',
  chunkSize: 5 * 1024 * 1024 // 默认 5MB
})

const formRules = {
  chunkSize: [{ required: true, message: t('largeFile.chunkSizeRequired'), trigger: 'change' }]
}

// 上传进度相关
const uploadProgress = ref(0)
const uploadStatus = ref<'success' | 'warning' | 'exception' | undefined>(undefined)
const uploadProgressText = ref('')
const currentFile = ref<File | null>(null)
const fileId = ref('')
const totalChunks = ref(0)
const uploadedChunks = ref(0)
const aborted = ref(false) // 中断标志

/** 打开弹窗 */
const open = async () => {
  dialogVisible.value = true
  resetForm()
}
defineExpose({ open })

/** 处理上传的文件发生变化 */
const handleFileChange = (file: any) => {
  currentFile.value = file.raw
}

/** 提交表单 */
const submitFileForm = async () => {
  if (!currentFile.value) {
    message.error(t('largeFile.selectFileRequired'))
    return
  }

  uploading.value = true
  uploadProgress.value = 0
  uploadStatus.value = undefined
  uploadProgressText.value = t('largeFile.preparing')

  try {
    const file = currentFile.value
    const chunkSize = formData.value.chunkSize
    const total = Math.ceil(file.size / chunkSize)

    // 1. 检查文件上传状态
    uploadProgressText.value = t('largeFile.checkingFile')
    const checkResult = await LargeFileApi.checkFileUpload({
      filename: file.name,
      fileSize: file.size,
      chunkSize: chunkSize,
      totalChunks: total,
      contentType: file.type || 'application/octet-stream'
    })

    fileId.value = checkResult.fileId
    totalChunks.value = total
    uploadedChunks.value = checkResult.uploadedChunks.length

    // 2. 如果不需要上传（已上传完成）
    if (!checkResult.needUpload) {
      uploadProgress.value = 100
      uploadStatus.value = 'success'
      uploadProgressText.value = t('largeFile.instantUpload')
      handleSuccess()
      return
    }

    // 3. 计算需要上传的分片
    const needUploadChunks = []
    for (let i = 0; i < total; i++) {
      if (!checkResult.uploadedChunks.includes(i)) {
        needUploadChunks.push(i)
      }
    }

    uploadProgressText.value = t('largeFile.startUpload', { total: total, uploaded: checkResult.uploadedChunks.length, remaining: needUploadChunks.length })

    // 4. 并行上传分片（每次最多3个并发）
    const concurrency = 3
    let completedChunks = checkResult.uploadedChunks.length

    for (let i = 0; i < needUploadChunks.length; i += concurrency) {
      // 检查中断标志，停止上传
      if (aborted.value) {
        break
      }
      const batch = needUploadChunks.slice(i, i + concurrency)
      await Promise.all(
        batch.map(async (chunkIndex) => {
          const start = chunkIndex * chunkSize
          const end = Math.min(start + chunkSize, file.size)
          const chunk = file.slice(start, end)

          await LargeFileApi.uploadFileChunk({
            file: new File([chunk], file.name, { type: file.type || 'application/octet-stream' }),
            fileId: fileId.value,
            chunkIndex: chunkIndex,
            totalChunks: total
          })

          completedChunks++
          uploadProgress.value = Math.round((completedChunks / total) * 100)
          uploadProgressText.value = t('largeFile.uploading', { current: completedChunks, total: total })
        })
      )
    }

    // 检查是否被中断，如果中断则不继续合并
    if (aborted.value) {
      uploadStatus.value = 'warning'
      uploadProgressText.value = t('largeFile.uploadInterrupted')
      return
    }

    // 5. 合并分片（异步执行，前端轮询状态）
    uploadProgressText.value = t('largeFile.submittingMerge')
    uploadStatus.value = 'warning' // 合并阶段进度条显示黄色
    await LargeFileApi.mergeFileChunks({
      fileId: fileId.value,
      filename: file.name,
      totalChunks: total,
      directory: formData.value.directory || undefined
    })

    // 6. 轮询任务状态，显示合并进度
    uploadProgressText.value = t('largeFile.merging', { current: 0, total: total })
    const pollInterval = 1000 // 1秒轮询一次
    let pollCount = 0
    const maxPollCount = 300 // 最多轮询5分钟

    while (pollCount < maxPollCount) {
      // 检查中断标志
      if (aborted.value) {
        uploadStatus.value = 'warning'
        uploadProgressText.value = t('largeFile.mergeInterrupted')
        return
      }

      await new Promise((resolve) => setTimeout(resolve, pollInterval))
      pollCount++

      try {
        const task = await LargeFileApi.getFileUploadTaskByFileId(fileId.value)
        if (!task) {
          continue
        }

        // 更新合并进度
        const mergedChunks = task.mergedChunks || 0
        uploadProgress.value = Math.round((mergedChunks / total) * 100)
        uploadProgressText.value = t('largeFile.merging', { current: mergedChunks, total: total })

        // 检查状态（字符串状态）
        if (task.status === 'completed') {
          // 已完成 - 刷新列表、关闭弹窗、提示
          uploadProgress.value = 100
          uploadStatus.value = 'success'
          uploadProgressText.value = t('largeFile.uploadComplete')
          emit('success') // 先刷新列表
          uploading.value = false // 解除按钮禁用
          dialogVisible.value = false // 关闭弹窗
          message.success(t('largeFile.uploadSuccessMsg'))
          return
        } else if (task.status === 'merging_failed') {
          // 合并失败
          uploadStatus.value = 'exception'
          uploadProgressText.value = t('largeFile.mergeFailed', { error: task.errorMessage || t('common.unknown') })
          message.error(t('largeFile.mergeFailed', { error: task.errorMessage || t('common.unknown') }))
          return
        }
      } catch (e) {
        console.error('轮询任务状态失败:', e)
      }
    }

    // 超时
    uploadStatus.value = 'exception'
    uploadProgressText.value = t('largeFile.mergeTimeout')
    message.error(t('largeFile.mergeTimeoutMsg'))
  } catch (e: any) {
    uploadStatus.value = 'exception'
    uploadProgressText.value = t('largeFile.uploadFailed', { error: e.message || t('common.unknown') })
    message.error(t('largeFile.uploadFailed', { error: e.message || t('common.unknown') }))
  } finally {
    uploading.value = false
  }
}

/** 上传成功处理 */
const emit = defineEmits(['success'])
const handleSuccess = () => {
  setTimeout(() => {
    dialogVisible.value = false
    message.success(t('common.createSuccess'))
    emit('success')
  }, 500)
}

/** 弹窗关闭前确认 */
const handleBeforeClose = async (done: () => void) => {
  if (uploading.value) {
    try {
      await message.confirm(t('largeFile.confirmCancel'))
      // 设置中断标志，停止上传循环
      aborted.value = true
      // 调用中断接口
      if (fileId.value) {
        await LargeFileApi.interruptFileUpload(fileId.value)
      }
      done()
    } catch {
      // 用户取消关闭
    }
  } else {
    done()
  }
}

/** 重置表单 */
const resetForm = () => {
  formLoading.value = false
  uploading.value = false
  uploadProgress.value = 0
  uploadStatus.value = undefined
  uploadProgressText.value = ''
  currentFile.value = null
  fileId.value = ''
  totalChunks.value = 0
  uploadedChunks.value = 0
  aborted.value = false // 清除中断标志
  fileList.value = []
  formData.value = {
    directory: '',
    chunkSize: 5 * 1024 * 1024
  }
}

/** 文件数超出提示 */
const handleExceed = (): void => {
  message.error(t('largeFile.exceedLimit'))
}
</script>

<style scoped>
.el-upload__tip {
  color: #909399;
  font-size: 12px;
  margin-top: 7px;
}
</style>
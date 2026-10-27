<template>
  <ContentWrap>
    <!-- 搜索 -->
    <el-form
      class="-mb-15px"
      label-width="auto"
      :model="queryParams"
      ref="queryFormRef"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('infra.largeFile.filename')" prop="filename">
            <el-input
              v-model="queryParams.filename"
              class="!w-240px"
              clearable
              :placeholder="t('common.inputText')"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('infra.largeFile.status')" prop="status">
            <el-select
              v-model="queryParams.status"
              class="!w-240px"
              clearable
              :placeholder="t('common.selectText')"
            >
              <el-option :label="t('infra.largeFile.statusUploading')" value="uploading" />
              <el-option :label="t('infra.largeFile.statusUploadFailed')" value="uploading_failed" />
              <el-option :label="t('infra.largeFile.statusMerging')" value="merging" />
              <el-option :label="t('infra.largeFile.statusMergeFailed')" value="merging_failed" />
              <el-option :label="t('infra.largeFile.statusCompleted')" value="completed" />
              <el-option :label="t('infra.largeFile.statusInterrupted')" value="interrupted" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item>
            <el-button @click="handleQuery">
              <Icon class="mr-5px" icon="ep:search" />
              {{ t('common.query') }}
            </el-button>
            <el-button @click="resetQuery">
              <Icon class="mr-5px" icon="ep:refresh" />
              {{ t('common.reset') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <!-- 操作按钮 -->
    <div class="mb-10px">
      <el-button type="primary" @click="openUploadDialog">
        <Icon class="mr-5px" icon="ep:upload-filled" />
        {{ t('infra.largeFile.selectFile') }}
      </el-button>
    </div>

    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :table-layout="'auto'">
      <el-table-column align="center" :label="t('infra.largeFile.taskId')" prop="id" min-width="80" />
      <el-table-column align="center" :label="t('infra.largeFile.filename')" prop="filename" min-width="200" />
      <el-table-column align="center" :label="t('infra.largeFile.fileSize')" prop="fileSize" min-width="120">
        <template #default="scope">
          {{ formatFileSize(scope.row.fileSize) }}
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('infra.largeFile.chunkSize')" prop="chunkSize" min-width="100">
        <template #default="scope">
          {{ formatFileSize(scope.row.chunkSize) }}
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('infra.largeFile.progress')" min-width="200">
        <template #default="scope">
          <el-progress
            :percentage="scope.row.progress"
            :status="getProgressStatus(scope.row.status)"
            :stroke-width="15"
            :text-inside="true"
          />
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('infra.largeFile.status')" prop="statusName" min-width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ scope.row.statusName }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('infra.largeFile.createTime')"
        prop="createTime"
        min-width="180"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('infra.largeFile.updateTime')"
        prop="updateTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('infra.largeFile.errorMessage')" prop="errorMessage" min-width="150">
        <template #default="scope">
          <el-tooltip
            v-if="scope.row.errorMessage"
            :content="scope.row.errorMessage"
            placement="top"
          >
            <span class="text-red-500 truncate max-w-[150px] inline-block">
              {{ scope.row.errorMessage }}
            </span>
          </el-tooltip>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column align="center" fixed="right" :label="t('common.operation')" min-width="150">
        <template #default="scope">
          <el-button
            v-if="scope.row.status === 'completed'"
            v-hasPermi="['infra:large-file:query']"
            link
            type="primary"
            @click="handleDownload(scope.row)"
          >
            {{ t('infra.largeFile.download') }}
          </el-button>
          <el-button
            v-hasPermi="['infra:large-file:update']"
            v-if="scope.row.status === 'uploading_failed' || scope.row.status === 'merging_failed' || scope.row.status === 'interrupted'"
            link
            type="primary"
            @click="handleReset(scope.row.id)"
          >
            {{ t('infra.largeFile.retry') }}
          </el-button>
          <el-button
            v-hasPermi="['infra:large-file:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            {{ t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 上传对话框 -->
  <el-dialog
    v-model="uploadDialogVisible"
    :title="t('infra.largeFile.selectFile')"
    width="500px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    @close="handleUploadDialogClose"
  >
    <!-- 文件选择区域 -->
    <div v-if="!uploading">
      <el-form label-width="120px">
        <el-form-item :label="t('infra.largeFile.selectFile')">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
            :on-exceed="handleExceed"
            :file-list="fileList"
            drag
            class="w-full"
          >
            <Icon class="el-icon--upload" icon="ep:upload-filled" />
            <div class="el-upload__text">
              {{ t('infra.largeFile.dragTip') }}<em>{{ t('infra.largeFile.clickUpload') }}</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                {{ t('infra.largeFile.uploadTip') }}
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item :label="t('infra.largeFile.chunkSize')">
          <el-select v-model="selectedChunkSize" class="!w-full">
            <el-option label="1 MB" :value="1 * 1024 * 1024" />
            <el-option label="2 MB" :value="2 * 1024 * 1024" />
            <el-option label="5 MB" :value="5 * 1024 * 1024" />
            <el-option label="10 MB" :value="10 * 1024 * 1024" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="selectedFile" :label="t('infra.largeFile.fileSize')">
          <span>{{ selectedFile.name }} ({{ formatFileSize(selectedFile.size) }})</span>
        </el-form-item>
      </el-form>
    </div>

    <!-- 上传进度区域 -->
    <div v-else class="upload-progress">
      <div class="mb-20px">
        <div class="mb-10px flex justify-between">
          <span>{{ selectedFile?.name }}</span>
          <span>{{ Math.round(uploadProgress) }}%</span>
        </div>
        <el-progress
          :percentage="uploadProgress"
          :status="uploadStatus"
          :stroke-width="20"
          :text-inside="true"
        />
      </div>
      <div class="text-center text-gray-500 text-sm">
        {{ uploadStatusText }}
      </div>
    </div>

    <template #footer>
      <el-button v-if="!uploading" @click="uploadDialogVisible = false">
        {{ t('common.cancel') }}
      </el-button>
      <el-button v-if="!uploading" type="primary" :disabled="!selectedFile" @click="startUpload">
        {{ t('common.confirm') }}
      </el-button>
      <el-button v-if="uploading" type="danger" @click="cancelUpload">
        {{ t('common.cancel') }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import * as LargeFileApi from '@/api/infra/largeFile'
import { dateFormatter } from '@/utils/formatTime'
import type { UploadFile } from 'element-plus'

defineOptions({ name: 'InfraLargeFile' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const list = ref([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  filename: undefined,
  status: undefined
})
const queryFormRef = ref()

// ========== 上传相关 ==========
const uploadDialogVisible = ref(false)
const uploadRef = ref()
const selectedFile = ref<File | null>(null)
const fileList = ref<UploadFile[]>([])
const selectedChunkSize = ref(5 * 1024 * 1024) // 默认 5MB
const uploading = ref(false)
const uploadProgress = ref(0)
const uploadStatus = ref<'success' | 'warning' | 'exception' | undefined>(undefined)
const uploadStatusText = ref('')

// 上传控制
let currentFileId = ''
let cancelRequested = false
let totalChunks = 0
let uploadedChunks = 0

/** 打开上传对话框 */
const openUploadDialog = () => {
  uploadDialogVisible.value = true
  selectedFile.value = null
  fileList.value = []
  uploading.value = false
  uploadProgress.value = 0
  uploadStatus.value = undefined
  uploadStatusText.value = ''
}

/** 关闭上传对话框 */
const handleUploadDialogClose = () => {
  if (uploading.value) {
    cancelUpload()
  }
}

/** 文件选择变化 */
const handleFileChange = (file: UploadFile) => {
  if (file.raw) {
    selectedFile.value = file.raw
    fileList.value = [file]
  }
}

/** 文件超出限制 */
const handleExceed = () => {
  message.error(t('infra.largeFile.exceedLimit'))
}

/** 开始上传 */
const startUpload = async () => {
  if (!selectedFile.value) {
    message.error(t('infra.largeFile.selectFileRequired'))
    return
  }

  const file = selectedFile.value
  const chunkSize = selectedChunkSize.value
  totalChunks = Math.ceil(file.size / chunkSize)
  uploadedChunks = 0
  cancelRequested = false

  uploading.value = true
  uploadProgress.value = 0
  uploadStatus.value = undefined

  try {
    // 1. 检查文件上传状态（断点续传）
    uploadStatusText.value = t('infra.largeFile.checkingFile')
    const checkResult = await LargeFileApi.checkFileUpload({
      filename: file.name,
      fileSize: file.size,
      chunkSize: chunkSize,
      totalChunks: totalChunks,
      contentType: file.type || 'application/octet-stream'
    })

    currentFileId = checkResult.fileId
    const existingChunks = new Set(checkResult.uploadedChunks || [])

    // 如果所有分片都已上传，直接合并
    if (existingChunks.size >= totalChunks) {
      uploadStatusText.value = t('infra.largeFile.instantUpload')
      await mergeFile(file.name, currentFileId, totalChunks)
      return
    }

    // 显示断点续传提示
    if (existingChunks.size > 0) {
      message.success(t('infra.largeFile.resumeTip'))
    }

    // 2. 上传分片
    uploadStatusText.value = t('infra.largeFile.preparing')
    uploadedChunks = existingChunks.size

    for (let i = 0; i < totalChunks; i++) {
      // 检查是否取消
      if (cancelRequested) {
        uploadStatusText.value = t('infra.largeFile.uploadInterrupted')
        uploadStatus.value = 'warning'
        return
      }

      // 跳过已上传的分片
      if (existingChunks.has(i)) {
        continue
      }

      // 切片
      const start = i * chunkSize
      const end = Math.min(start + chunkSize, file.size)
      const chunk = file.slice(start, end)

      // 上传分片
      uploadStatusText.value = t('infra.largeFile.uploading')
        .replace('{current}', String(i + 1))
        .replace('{total}', String(totalChunks))

      await LargeFileApi.uploadFileChunk({
        file: chunk,
        fileId: currentFileId,
        chunkIndex: i,
        totalChunks: totalChunks
      })

      uploadedChunks++
      uploadProgress.value = Math.round((uploadedChunks / totalChunks) * 100)
    }

    // 3. 合并文件
    await mergeFile(file.name, currentFileId, totalChunks)

  } catch (error: any) {
    console.error('Upload failed:', error)
    uploadStatus.value = 'exception'
    uploadStatusText.value = t('infra.largeFile.uploadFailed').replace('{error}', error.message || 'Unknown error')
    message.error(uploadStatusText.value)
  }
}

/** 合并文件 */
const mergeFile = async (filename: string, fileId: string, chunks: number) => {
  try {
    uploadStatusText.value = t('infra.largeFile.submittingMerge')
    await LargeFileApi.mergeFileChunks({
      fileId: fileId,
      filename: filename,
      totalChunks: chunks
    })

    // 轮询任务状态，等待合并完成
    await pollTaskStatus(fileId)

  } catch (error: any) {
    console.error('Merge failed:', error)
    uploadStatus.value = 'exception'
    uploadStatusText.value = t('infra.largeFile.mergeFailed').replace('{error}', error.message || 'Unknown error')
    message.error(uploadStatusText.value)
  }
}

/** 轮询任务状态 */
const pollTaskStatus = async (fileId: string) => {
  const maxAttempts = 120 // 最多等待 2 分钟
  let attempts = 0

  while (attempts < maxAttempts) {
    if (cancelRequested) {
      uploadStatusText.value = t('infra.largeFile.mergeInterrupted')
      uploadStatus.value = 'warning'
      return
    }

    try {
      const task = await LargeFileApi.getFileUploadTaskByFileId(fileId)
      if (!task) break

      // 更新合并进度
      if (task.mergedChunks > 0 && task.totalChunks > 0) {
        uploadProgress.value = Math.round((task.mergedChunks / task.totalChunks) * 100)
        uploadStatusText.value = t('infra.largeFile.merging')
          .replace('{current}', String(task.mergedChunks))
          .replace('{total}', String(task.totalChunks))
      }

      // 检查任务状态
      if (task.status === 'completed') {
        uploadProgress.value = 100
        uploadStatus.value = 'success'
        uploadStatusText.value = t('infra.largeFile.uploadComplete')
        uploading.value = false
        message.success(t('infra.largeFile.uploadSuccessMsg'))

        // 关闭对话框并刷新列表
        setTimeout(() => {
          uploadDialogVisible.value = false
          getList()
        }, 1000)
        return
      }

      if (task.status === 'merging_failed') {
        uploadStatus.value = 'exception'
        uploadStatusText.value = t('infra.largeFile.mergeFailed').replace('{error}', task.errorMessage || 'Unknown')
        message.error(uploadStatusText.value)
        return
      }

    } catch (error) {
      console.error('Poll task status failed:', error)
    }

    // 等待 1 秒后继续轮询
    await new Promise(resolve => setTimeout(resolve, 1000))
    attempts++
  }

  // 超时
  uploadStatus.value = 'warning'
  uploadStatusText.value = t('infra.largeFile.mergeTimeout')
  message.warning(t('infra.largeFile.mergeTimeoutMsg'))
}

/** 取消上传 */
const cancelUpload = async () => {
  try {
    await message.confirm(t('infra.largeFile.confirmCancel'))
    cancelRequested = true
    
    if (currentFileId) {
      await LargeFileApi.interruptFileUpload(currentFileId)
    }
    
    uploadStatusText.value = t('infra.largeFile.uploadInterrupted')
    uploadStatus.value = 'warning'
    uploading.value = false
    
    // 刷新列表
    getList()
  } catch {
    // 用户取消确认
  }
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await LargeFileApi.getFileUploadTaskPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields()
  handleQuery()
}

/** 重试任务 */
const handleReset = async (id: number) => {
  try {
    await message.confirm(t('infra.largeFile.retryConfirm'))
    await LargeFileApi.resetFileUploadTask(id)
    message.success(t('infra.largeFile.retrySuccess'))
    getList()
  } catch {}
}

/** 下载文件 */
const handleDownload = (row: any) => {
  LargeFileApi.downloadLargeFile(row.id)
}

/** 删除任务 */
const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await LargeFileApi.deleteFileUploadTask(id)
    message.success(t('common.delSuccess'))
    getList()
  } catch {}
}

/** 格式化文件大小 */
const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

/** 获取状态标签类型 */
const getStatusType = (status: string): 'success' | 'warning' | 'danger' | 'info' => {
  switch (status) {
    case 'uploading':
      return 'warning'
    case 'uploading_failed':
      return 'danger'
    case 'merging':
      return 'warning'
    case 'merging_failed':
      return 'danger'
    case 'completed':
      return 'success'
    case 'interrupted':
      return 'info'
    default:
      return 'info'
  }
}

/** 获取进度条状态 */
const getProgressStatus = (
  status: string
): 'success' | 'warning' | 'exception' | undefined => {
  switch (status) {
    case 'uploading_failed':
    case 'merging_failed':
      return 'exception'
    case 'completed':
      return 'success'
    case 'interrupted':
      return 'warning'
    default:
      return undefined
  }
}

/** 初始化 */
onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.upload-progress {
  padding: 20px;
}
</style>
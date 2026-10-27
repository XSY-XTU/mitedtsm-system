import request from '@/config/axios'

// ========== 大文件上传相关 API ==========

// 文件上传检查 Response VO
export interface LargeFileCheckRespVO {
  fileId: string
  uploadedChunks: number[]
  needUpload: boolean
  message: string
}

// 文件上传任务 Response VO
export interface LargeFileRespVO {
  id: number
  fileId: string
  filename: string
  fileSize: number
  chunkSize: number
  totalChunks: number
  uploadedChunks: number
  mergedChunks: number
  progress: number
  status: string
  statusName: string
  configId: number
  path: string
  contentType: string
  errorMessage: string
  createTime: Date
  updateTime: Date
}

// 检查文件上传状态（断点续传）
export const checkFileUpload = (data: {
  filename: string
  fileSize: number
  chunkSize: number
  totalChunks: number
  contentType?: string
}) => {
  return request.post<LargeFileCheckRespVO>({ url: '/infra/large-file/check', data })
}

// 上传文件分片
export const uploadFileChunk = (
  data: {
    file: File
    fileId: string
    chunkIndex: number
    totalChunks: number
  },
  onUploadProgress?: Function
) => {
  const formData = new FormData()
  formData.append('file', data.file)
  formData.append('fileId', data.fileId)
  formData.append('chunkIndex', data.chunkIndex.toString())
  formData.append('totalChunks', data.totalChunks.toString())
  return request.upload({ url: '/infra/large-file/chunk', data: formData, onUploadProgress })
}

// 合并文件分片
export const mergeFileChunks = (data: {
  fileId: string
  filename: string
  totalChunks: number
  directory?: string
}) => {
  return request.post<string>({ url: '/infra/large-file/merge', data })
}

// 获取上传任务分页
export const getFileUploadTaskPage = (params: PageParam) => {
  return request.get({ url: '/infra/large-file/task/page', params })
}

// 获取上传任务详情
export const getFileUploadTask = (id: number) => {
  return request.get<LargeFileRespVO>({ url: '/infra/large-file/task/get', params: { id } })
}

// 根据 fileId 获取上传任务详情
export const getFileUploadTaskByFileId = (fileId: string) => {
  return request.get<LargeFileRespVO>({
    url: '/infra/large-file/task/get-by-fileId',
    params: { fileId }
  })
}

// 删除上传任务
export const deleteFileUploadTask = (id: number) => {
  return request.delete({ url: '/infra/large-file/task/delete?id=' + id })
}

// 重置失败任务状态
export const resetFileUploadTask = (id: number) => {
  return request.post({ url: '/infra/large-file/task/reset?id=' + id })
}

// 获取任务的已上传分片列表
export const getUploadedChunks = (fileId: string) => {
  return request.get<number[]>({ url: '/infra/large-file/task/chunks', params: { fileId } })
}

// 中断上传任务
export const interruptFileUpload = (fileId: string) => {
  return request.post({ url: '/infra/large-file/task/interrupt?fileId=' + fileId })
}

// 下载已完成的文件
export const downloadLargeFile = (id: number) => {
  return request.download({ url: '/infra/large-file/task/download?id=' + id })
}

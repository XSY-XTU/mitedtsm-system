<template>
  <div>
    <el-row>
      <el-input v-model="reply.title" class="input-margin-bottom" :placeholder="t('common.inputTitle')" />
      <el-input class="input-margin-bottom" v-model="reply.description" :placeholder="t('common.inputDescription')" />
      <el-row class="ope-row" justify="center">
        <WxVideoPlayer v-if="reply.url" :url="reply.url" />
      </el-row>
      <el-col>
        <el-row style="text-align: center" align="middle">
          <!-- 选择素材 -->
          <el-col :span="12">
            <el-button type="success" @click="showDialog = true">
              {{ t('material.selectFromLibrary') }} <Icon icon="ep:circle-check" />
            </el-button>
            <el-dialog
              :title="t('common.selectVideo')"
              v-model="showDialog"
              width="90%"
              append-to-body
              destroy-on-close
            >
              <WxMaterialSelect
                type="video"
                :account-id="reply.accountId"
                @select-material="selectMaterial"
              />
            </el-dialog>
          </el-col>
          <!-- 文件上传 -->
          <el-col :span="12">
            <el-upload
              :action="UPLOAD_URL"
              :headers="HEADERS"
              multiple
              :limit="1"
              :file-list="fileList"
              :data="uploadData"
              :before-upload="beforeVideoUpload"
              :on-success="onUploadSuccess"
            >
              <el-button type="primary">{{ t('common.newVideo') }} <Icon icon="ep:upload" /></el-button>
            </el-upload>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts" setup>
import WxVideoPlayer from '@/views/mp/components/wx-video-play'
import WxMaterialSelect from '@/views/mp/components/wx-material-select'
import type { UploadRawFile } from 'element-plus'
import { UploadType, useBeforeUpload } from '@/views/mp/hooks/useUpload'
import { getAccessToken } from '@/utils/auth'
import { Reply } from './types'

const message = useMessage()
const { t } = useI18n('mp') // 国际化

const UPLOAD_URL = import.meta.env.VITE_BASE_URL + '/admin-api/mp/material/upload-temporary'
const HEADERS = { Authorization: 'Bearer ' + getAccessToken() }

const props = defineProps<{
  modelValue: Reply
}>()
const emit = defineEmits<{
  (e: 'update:modelValue', v: Reply)
}>()
const reply = computed<Reply>({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const showDialog = ref(false)
const fileList = ref([])
const uploadData = reactive({
  accountId: reply.value.accountId,
  type: 'video',
  title: '',
  introduction: ''
})

const beforeVideoUpload = (rawFile: UploadRawFile) => useBeforeUpload(UploadType.Video, 10)(rawFile)

const onUploadSuccess = (res: any) => {
  if (res.code !== 0) {
    message.error(t('common.uploadError') + res.msg)
    return false
  }

  // 清空上传时的各种数据
  fileList.value = []
  uploadData.title = ''
  uploadData.introduction = ''

  selectMaterial(res.data)
}

/** 选择素材后设置 */
const selectMaterial = (item: any) => {
  showDialog.value = false

  reply.value.mediaId = item.mediaId
  reply.value.url = item.url
  reply.value.name = item.name

  // title、introduction：从 item 到 tempObjItem，因为素材里有 title、introduction
  if (item.title) {
    reply.value.title = item.title || ''
  }
  if (item.introduction) {
    reply.value.description = item.introduction || ''
  }
}
</script>

<style lang="scss" scoped>
.input-margin-bottom {
  margin-bottom: 2%;
}

.ope-row {
  width: 100%;
  padding-top: 10px;
  text-align: center;
}
</style>

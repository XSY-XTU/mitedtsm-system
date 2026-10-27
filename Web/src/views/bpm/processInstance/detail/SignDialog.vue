<template>
  <el-dialog v-model="signDialogVisible" :title="t('approval.sign')" width="935">
    <div class="position-relative">
      <Vue3Signature class="b b-solid b-gray" ref="signature" w="900px" h="400px" />
      <el-button
        class="pos-absolute bottom-20px right-10px"
        type="primary"
        text
        size="small"
        @click="signature.clear()"
      >
        <Icon icon="ep:delete" class="mr-5px" />
        {{ t('common.clear') }}
      </el-button>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="signDialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" @click="submit"> {{ t('common.submit') }} </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import Vue3Signature from 'vue3-signature'
import * as FileApi from '@/api/infra/file'
import download from '@/utils/download'

const message = useMessage() // 消息弹窗
const { t } = useI18n('bpm') // 国际化
const signDialogVisible = ref(false)
const signature = ref()

const open = async () => {
  signDialogVisible.value = true
}
defineExpose({ open })

const emits = defineEmits(['success'])
const submit = async () => {
  message.success(t('approval.signUploading'))
  const res = await FileApi.updateFile({
    file: download.base64ToFile(signature.value.save('image/png'), t('approval.sign'))
  })
  emits('success', res.data)
  signDialogVisible.value = false
}
</script>

<style scoped></style>

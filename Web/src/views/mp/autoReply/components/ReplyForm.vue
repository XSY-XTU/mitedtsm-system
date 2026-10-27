<template>
  <div>
    <el-form ref="formRef" :model="replyForm" :rules="rules" label-width="auto">
      <el-form-item :label="t('message.messageType')" prop="requestMessageType" v-if="msgType === MsgType.Message">
        <el-select v-model="replyForm.requestMessageType" :placeholder="t('common.selectText')">
          <template v-for="dict in getDictOptions(DICT_TYPE.MP_MESSAGE_TYPE)" :key="dict.value">
            <el-option
              v-if="RequestMessageTypes.includes(dict.value)"
              :label="dict.label"
              :value="dict.value"
            />
          </template>
        </el-select>
      </el-form-item>
      <el-form-item :label="t('autoReply.requestMatch')" prop="requestMatch" v-if="msgType === MsgType.Keyword">
        <el-select v-model="replyForm.requestMatch" :placeholder="t('autoReply.selectMatchType')" clearable>
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.MP_AUTO_REPLY_REQUEST_MATCH)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('autoReply.requestKeyword')" prop="requestKeyword" v-if="msgType === MsgType.Keyword">
        <el-input v-model="replyForm.requestKeyword" :placeholder="t('common.inputText')" clearable />
      </el-form-item>
      <el-form-item :label="t('autoReply.responseContent')">
        <WxReplySelect v-model="reply" />
      </el-form-item>
    </el-form>
  </div>
</template>

<script lang="ts" setup>
import WxReplySelect, { type Reply } from '@/views/mp/components/wx-reply'
import type { FormInstance } from 'element-plus'
import { MsgType } from './types'
import { DICT_TYPE, getDictOptions, getIntDictOptions } from '@/utils/dict'

defineOptions({ name: 'ReplyForm' })

const { t } = useI18n('mp') // 国际化

const props = defineProps<{
  modelValue: any
  reply: Reply
  msgType: MsgType
}>()

const emit = defineEmits<{
  (e: 'update:reply', v: Reply)
  (e: 'update:modelValue', v: any)
}>()

const reply = computed<Reply>({
  get: () => props.reply,
  set: (val) => emit('update:reply', val)
})

const replyForm = computed<any>({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const formRef = ref<FormInstance | null>(null) // 表单 ref

const RequestMessageTypes = ['text', 'image', 'voice', 'video', 'shortvideo', 'location', 'link'] // 允许选择的请求消息类型

// 表单校验
const rules = {
  requestKeyword: [{ required: true, message: t('autoReply.keywordRequired'), trigger: 'blur' }],
  requestMatch: [{ required: true, message: t('autoReply.matchRequired'), trigger: 'blur' }]
}

defineExpose({
  resetFields: () => formRef.value?.resetFields(),
  validate: async () => formRef.value?.validate()
})
</script>

<style scoped></style>

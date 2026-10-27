<template>
  <Dialog :title="t('messageTemplate.sendTitle')" v-model="dialogVisible" width="600px">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="auto">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('messageTemplate.templateNo')">
            <el-input v-model="formData.id" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('messageTemplate.templateTitle')">
            <el-input v-model="templateTitle" disabled />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('messageTemplate.user')" prop="userId">
            <el-select
              v-model="formData.userId"
              filterable
              remote
              reserve-keyword
              :placeholder="t('messageTemplate.searchUserPlaceholder')"
              :remote-method="searchUser"
              :loading="userLoading"
              class="!w-full"
            >
              <el-option
                v-for="user in userList"
                :key="user.id"
                :label="user.nickname || user.openid"
                :value="user.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('messageTemplate.templateData')" prop="data">
            <el-input
              v-model="formData.data"
              type="textarea"
              :rows="4"
              :placeholder="t('messageTemplate.templateDataPlaceholder')"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('messageTemplate.jumpUrl')" prop="url">
            <el-input v-model="formData.url" :placeholder="t('messageTemplate.jumpUrlPlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('messageTemplate.miniAppId')" prop="miniProgramAppId">
            <el-input v-model="formData.miniProgramAppId" :placeholder="t('messageTemplate.miniAppIdPlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('messageTemplate.miniAppPagePath')" prop="miniProgramPagePath">
            <el-input v-model="formData.miniProgramPagePath" :placeholder="t('messageTemplate.miniAppPagePathPlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button type="primary" @click="submitForm" :loading="loading">{{ t('common.send') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { MessageTemplateApi, MsgTemplateVO, MsgTemplateSendVO } from '@/api/mp/messageTemplate'
import * as MpUserApi from '@/api/mp/user'

const message = useMessage() // 消息弹窗
const { t } = useI18n('mp') // 国际化

const dialogVisible = ref(false) // 弹窗是否展示
const loading = ref(false) // 提交加载中
const templateTitle = ref('') // 模板标题
const accountId = ref<number>() // 公众号账号编号

const formRef = ref() // 表单 Ref
const formData = ref<MsgTemplateSendVO>({
  id: undefined!,
  userId: undefined!,
  data: '',
  url: '',
  miniProgramAppId: '',
  miniProgramPagePath: ''
})
const formRules = reactive({
  userId: [{ required: true, message: t('messageTemplate.selectUserRequired'), trigger: 'change' }]
})

// 用户搜索相关
const userLoading = ref(false)
const userList = ref<any[]>([])

/** 搜索用户 */
const searchUser = async (query: string) => {
  if (!accountId.value) {
    return
  }
  userLoading.value = true
  try {
    const data = await MpUserApi.getUserPage({
      pageNo: 1,
      pageSize: 20,
      accountId: accountId.value,
      nickname: query || undefined
    })
    userList.value = data.list || []
  } finally {
    userLoading.value = false
  }
}

/** 打开弹窗 */
const open = async (row: MsgTemplateVO) => {
  resetForm()
  dialogVisible.value = true
  // 设置表单数据
  formData.value.id = row.id
  accountId.value = row.accountId
  templateTitle.value = row.title
  // 加载用户列表
  await searchUser('')
}
defineExpose({ open }) // 暴露 open 方法

/** 提交表单 */
const submitForm = async () => {
  // 校验表单
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  loading.value = true
  try {
    const sendData: MsgTemplateSendVO = {
      ...formData.value
    }
    // 如果填写了小程序信息，需要拼接成 miniprogram 字段
    if (sendData.miniProgramAppId && sendData.miniProgramPagePath) {
      sendData.miniprogram = JSON.stringify({
        appid: sendData.miniProgramAppId,
        pagepath: sendData.miniProgramPagePath
      })
    }
    // 如果填写了 data 字段
    if (sendData.data) {
      try {
        sendData.data = JSON.parse(sendData.data)
      } catch (e) {
        message.error(t('messageTemplate.templateDataInvalid'))
        return
      }
    }
    await MessageTemplateApi.sendMessageTemplate(sendData)
    message.success(t('messageTemplate.sendSuccess'))
    dialogVisible.value = false
  } finally {
    loading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined!,
    userId: undefined!,
    data: '',
    url: '',
    miniProgramAppId: '',
    miniProgramPagePath: ''
  }
  userList.value = []
  formRef.value?.resetFields()
}
</script>

<template>
  <doc-alert title="模版消息" url="https://doc.iocoder.cn/mp/message-template/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('account.title')" prop="accountId">
            <WxAccountSelect @change="onAccountChanged" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button
              type="success"
              plain
              @click="handleSync"
              :loading="syncLoading"
              v-hasPermi="['mp:message-template:sync']"
              :disabled="queryParams.accountId === -1"
            >
              <Icon icon="ep:refresh" class="mr-5px" /> {{ t('messageTemplate.sync') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true" :table-layout="'auto'">
      <el-table-column :label="t('messageTemplate.templateId')" align="center" prop="templateId" min-width="200"  fixed="right" />
      <el-table-column :label="t('messageTemplate.templateTitle')" align="center" prop="title" min-width="150" />
      <el-table-column :label="t('messageTemplate.templateContent')" align="center" prop="content" />
      <el-table-column :label="t('messageTemplate.templateExample')" align="center" prop="example" min-width="200" />
      <el-table-column :label="t('messageTemplate.primaryIndustry')" align="center" prop="primaryIndustry" min-width="120" />
      <el-table-column :label="t('messageTemplate.deputyIndustry')" align="center" prop="deputyIndustry" min-width="120" />
      <el-table-column :label="t('common.status')" align="center" prop="status" />
      <el-table-column :label="t('common.operation')" align="center" min-width="160">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="handleSend(scope.row)"
            v-hasPermi="['mp:message-template:send']"
          >
            {{ t('messageTemplate.send') }}
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['mp:message-template:delete']"
          >
            {{ t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </ContentWrap>

  <!-- 表单弹窗：发送消息 -->
  <MessageTemplateSendForm ref="sendFormRef" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import { MessageTemplateApi, MsgTemplateVO } from '@/api/mp/messageTemplate'
import MessageTemplateSendForm from './MessageTemplateSendForm.vue'
import WxAccountSelect from '@/views/mp/components/wx-account-select'

/** 公众号消息模板列表 */
defineOptions({ name: 'MpMessageTemplate' })

const message = useMessage() // 消息弹窗
const { t } = useI18n('mp') // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<MsgTemplateVO[]>([]) // 列表的数据
const queryParams = reactive({
  accountId: -1
})
const queryFormRef = ref() // 搜索的表单
const syncLoading = ref(false) // 同步模板的加载中

/** 公众号选择变化 */
const onAccountChanged = (accountId: number) => {
  queryParams.accountId = accountId
  getList()
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await MessageTemplateApi.getMessageTemplateList(queryParams)
    if (data) {
      list.value = data
    }
  } finally {
    loading.value = false
  }
}

/** 同步操作 */
const handleSync = async () => {
  try {
    await message.confirm(t('messageTemplate.confirmSync'))
    syncLoading.value = true
    await MessageTemplateApi.syncMessageTemplate(queryParams.accountId)
    message.success(t('messageTemplate.syncSuccess'))
    await getList()
  } finally {
    syncLoading.value = false
  }
}

/** 发送消息操作 */
const sendFormRef = ref()
const handleSend = (row: MsgTemplateVO) => {
  sendFormRef.value.open(row)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await MessageTemplateApi.deleteMessageTemplate(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}
</script>

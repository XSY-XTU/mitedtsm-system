<template>
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
        <el-col :span="8">
          <el-form-item :label="t('message.messageType')" prop="type">
            <el-select v-model="queryParams.type" :placeholder="t('message.selectMessageType')" class="!w-240px">
              <el-option
                v-for="dict in getStrDictOptions(DICT_TYPE.MP_MESSAGE_TYPE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('user.openid')" prop="openid">
            <el-input
              v-model="queryParams.openid"
              :placeholder="t('user.openidPlaceholder')"
              clearable
              :v-on="handleQuery"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('common.createTime')" prop="createTime">
            <el-date-picker
              v-model="queryParams.createTime"
              style="width: 240px"
              value-format="YYYY-MM-DD HH:mm:ss"
              type="daterange"
              range-separator="-"
              :start-placeholder="t('common.startTime')"
              :end-placeholder="t('common.endTime')"
              :default-time="['00:00:00', '23:59:59']"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button @click="handleQuery">
              <Icon icon="ep:search" class="mr-5px" />
              {{ t('common.query') }}
            </el-button>
            <el-button @click="resetQuery">
              <Icon icon="ep:refresh" class="mr-5px" />
              {{ t('common.reset') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <MessageTable :list="list" :loading="loading" @send="handleSend" />
    <Pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 发送消息的弹窗 -->
  <el-dialog
    :title="t('message.fanMessageList')"
    v-model="messageBox.show"
    @click="messageBox.show = true"
    width="50%"
    destroy-on-close
  >
    <WxMsg :user-id="messageBox.userId" />
  </el-dialog>
</template>
<script lang="ts" setup>
import * as MpMessageApi from '@/api/mp/message'
import WxMsg from '@/views/mp/components/wx-msg'
import WxAccountSelect from '@/views/mp/components/wx-account-select'
import MessageTable from './MessageTable.vue'
import { DICT_TYPE, getStrDictOptions } from '@/utils/dict'
import { MsgType } from '@/views/mp/components/wx-msg/types'
import type { FormInstance } from 'element-plus'

defineOptions({ name: 'MpMessage' })

const { t } = useI18n('mp') // 国际化

const loading = ref(false)
const total = ref(0) // 数据的总页数
const list = ref<any[]>([]) // 当前页的列表数据

// 搜索参数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  openid: '',
  accountId: -1,
  type: MsgType.Text,
  createTime: []
})
const queryFormRef = ref<FormInstance | null>(null) // 搜索的表单

// 消息对话框
const messageBox = reactive({
  show: false,
  userId: 0
})

/** 侦听accountId */
const onAccountChanged = (id: number) => {
  queryParams.accountId = id
  queryParams.pageNo = 1
  handleQuery()
}

/** 查询列表 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const getList = async () => {
  try {
    loading.value = true
    const data = await MpMessageApi.getMessagePage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 重置按钮操作 */
const resetQuery = async () => {
  // 暂存 accountId，并在 reset 后恢复
  const accountId = queryParams.accountId
  queryFormRef.value?.resetFields()
  queryParams.accountId = accountId
  handleQuery()
}

/** 打开消息发送窗口 */
const handleSend = async (userId: number) => {
  messageBox.userId = userId
  messageBox.show = true
}
</script>

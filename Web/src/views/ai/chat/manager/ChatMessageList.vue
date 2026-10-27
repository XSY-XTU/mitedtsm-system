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
          <el-form-item :label="tAi('chat.manager.conversationId')" prop="conversationId">
            <el-input
              v-model="queryParams.conversationId"
              :placeholder="tAi('chat.manager.conversationIdPlaceholder')"
              clearable
              @keyup.enter="handleQuery"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="tAi('mindMap.userId')" prop="userId">
            <el-select
              v-model="queryParams.userId"
              clearable
              :placeholder="tAi('mindMap.userIdPlaceholder')"
              class="!w-240px"
            >
              <el-option
                v-for="item in userList"
                :key="item.id"
                :label="item.nickname"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="tAi('mindMap.task.createTime')" prop="createTime">
            <el-date-picker
              v-model="queryParams.createTime"
              value-format="YYYY-MM-DD HH:mm:ss"
              type="daterange"
              :start-placeholder="tAi('mindMap.startDate')"
              :end-placeholder="tAi('mindMap.endDate')"
              :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> {{ tAi('mindMap.search') }}</el-button>
            <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> {{ tAi('mindMap.reset') }}</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true" :table-layout="'auto'">
      <el-table-column :label="tAi('chat.manager.messageId')" align="center" prop="id" min-width="180" fixed="left" />
      <el-table-column
        :label="tAi('chat.manager.conversationId')"
        align="center"
        prop="conversationId"
        min-width="180"
        fixed="left"
      />
      <el-table-column :label="tAi('mindMap.user')" align="center" prop="userId" min-width="180">
        <template #default="scope">
          <span>{{ userList.find((item) => item.id === scope.row.userId)?.nickname }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="tAi('chat.role.name')" align="center" prop="roleName" min-width="180" />
      <el-table-column :label="tAi('chat.message.type')" align="center" prop="type" min-width="100" />
      <el-table-column :label="tAi('chat.model.modelId')" align="center" prop="model" min-width="180" />
      <el-table-column :label="tAi('chat.message.content')" align="center" prop="content" min-width="300" />
      <el-table-column
        :label="tAi('mindMap.task.createTime')"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        min-width="180"
      />
      <el-table-column :label="tAi('chat.manager.replyId')" align="center" prop="replyId" min-width="180" />
      <el-table-column :label="tAi('chat.manager.useContext')" align="center" prop="useContext" min-width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="scope.row.useContext" />
        </template>
      </el-table-column>
      <el-table-column :label="tAi('mindMap.operation')" align="center" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['ai:chat-message:delete']"
          >
            {{ tAi('mindMap.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import { ChatMessageApi, ChatMessageVO } from '@/api/ai/chat/message'
import * as UserApi from '@/api/system/user'
import { DICT_TYPE } from '@/utils/dict'

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const tAi = useI18n('ai').t

const loading = ref(true) // 列表的加载中
const list = ref<ChatMessageVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  conversationId: undefined,
  userId: undefined,
  content: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const userList = ref<UserApi.UserVO[]>([]) // 用户列表

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ChatMessageApi.getChatMessagePage(queryParams)
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
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await ChatMessageApi.deleteChatMessageByAdmin(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 初始化 **/
onMounted(async () => {
  getList()
  // 获得用户列表
  userList.value = await UserApi.getSimpleUserList()
})
</script>
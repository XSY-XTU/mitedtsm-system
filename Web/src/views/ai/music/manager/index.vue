<template>
  <doc-alert :title="tAi('music.title')" url="https://doc.iocoder.cn/ai/music/" />

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
          <el-form-item :label="tAi('music.manager.musicName')" prop="title">
            <el-input
              v-model="queryParams.title"
              :placeholder="tAi('music.manager.musicNamePlaceholder')"
              clearable
              @keyup.enter="handleQuery"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="tAi('music.manager.musicStatus')" prop="status">
            <el-select
              v-model="queryParams.status"
              :placeholder="tAi('tool.statusPlaceholder')"
              clearable
              class="!w-240px"
            >
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.AI_MUSIC_STATUS)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="tAi('music.task.generateMode')" prop="generateMode">
            <el-select
              v-model="queryParams.generateMode"
              :placeholder="tAi('tool.statusPlaceholder')"
              clearable
              class="!w-240px"
            >
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.AI_GENERATE_MODE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
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
        <el-col :span="8">
          <el-form-item :label="tAi('music.manager.publicStatus')" prop="publicStatus">
            <el-select
              v-model="queryParams.publicStatus"
              :placeholder="tAi('music.manager.publicStatusPlaceholder')"
              clearable
              class="!w-240px"
            >
              <el-option
                v-for="dict in getBoolDictOptions(DICT_TYPE.INFRA_BOOLEAN_STRING)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
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
      <el-table-column :label="tAi('mindMap.id')" align="center" prop="id" min-width="180" fixed="left" />
      <el-table-column :label="tAi('music.manager.musicName')" align="center" prop="title" min-width="180" fixed="left" />
      <el-table-column :label="tAi('mindMap.user')" align="center" prop="userId" min-width="180">
        <template #default="scope">
          <span>{{ userList.find((item) => item.id === scope.row.userId)?.nickname }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="tAi('music.manager.musicStatus')" align="center" prop="status" min-width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.AI_MUSIC_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column :label="tAi('mindMap.model')" align="center" prop="model" min-width="180" />
      <el-table-column :label="tAi('music.manager.content')" align="center" min-width="180">
        <template #default="{ row }">
          <el-link
            v-if="row.audioUrl?.length > 0"
            type="primary"
            :href="row.audioUrl"
            target="_blank"
          >
            {{ tAi('music.manager.music') }}
          </el-link>
          <el-link
            v-if="row.videoUrl?.length > 0"
            type="primary"
            :href="row.videoUrl"
            target="_blank"
            class="!pl-5px"
          >
            {{ tAi('music.manager.video') }}
          </el-link>
          <el-link
            v-if="row.imageUrl?.length > 0"
            type="primary"
            :href="row.imageUrl"
            target="_blank"
            class="!pl-5px"
          >
            {{ tAi('music.manager.cover') }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column :label="tAi('music.manager.duration')" align="center" prop="duration" min-width="100" />
      <el-table-column :label="tAi('image.prompt')" align="center" prop="prompt" min-width="180" />
      <el-table-column :label="tAi('music.manager.lyric')" align="center" prop="lyric" min-width="180" />
      <el-table-column :label="tAi('common.description')" align="center" prop="gptDescriptionPrompt" min-width="180" />
      <el-table-column :label="tAi('music.task.generateMode')" align="center" prop="generateMode" min-width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.AI_GENERATE_MODE" :value="scope.row.generateMode" />
        </template>
      </el-table-column>
      <el-table-column :label="tAi('music.manager.styleTags')" align="center" prop="tags" min-width="180">
        <template #default="scope">
          <el-tag v-for="tag in scope.row.tags" :key="tag" round class="ml-2px">
            {{ tag }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="tAi('music.manager.publicStatus')" align="center" prop="publicStatus">
        <template #default="scope">
          <el-switch
            v-model="scope.row.publicStatus"
            :active-value="true"
            :inactive-value="false"
            @change="handleUpdatePublicStatusChange(scope.row)"
            :disabled="scope.row.status !== AiMusicStatusEnum.SUCCESS"
          />
        </template>
      </el-table-column>
      <el-table-column :label="tAi('music.manager.taskId')" align="center" prop="taskId" min-width="180" />
      <el-table-column :label="tAi('mindMap.errorMessage')" align="center" prop="errorMessage" />
      <el-table-column
        :label="tAi('mindMap.task.createTime')"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        min-width="180"
      />
      <el-table-column :label="tAi('mindMap.operation')" align="center" min-width="100" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['ai:music:delete']"
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
import { getIntDictOptions, getBoolDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import { MusicApi, MusicVO } from '@/api/ai/music'
import * as UserApi from '@/api/system/user'
import { AiMusicStatusEnum } from '@/views/ai/utils/constants'

/** AI 音乐 列表 */
defineOptions({ name: 'AiMusicManager' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const tAi = useI18n('ai').t

const loading = ref(true) // 列表的加载中
const list = ref<MusicVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userId: undefined,
  title: undefined,
  status: undefined,
  generateMode: undefined,
  createTime: [],
  publicStatus: undefined
})
const queryFormRef = ref() // 搜索的表单
const userList = ref<UserApi.UserVO[]>([]) // 用户列表

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await MusicApi.getMusicPage(queryParams)
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
    await MusicApi.deleteMusic(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 修改是否发布 */
const handleUpdatePublicStatusChange = async (row: MusicVO) => {
  try {
    // 修改状态的二次确认
    const text = row.publicStatus ? tAi('music.manager.public') : tAi('music.manager.private')
    await message.confirm(tAi('music.manager.publicConfirm').replace('{text}', text))
    // 发起修改状态
    await MusicApi.updateMusic({
      id: row.id,
      publicStatus: row.publicStatus
    })
    await getList()
  } catch {
    row.publicStatus = !row.publicStatus
  }
}

/** 初始化 **/
onMounted(async () => {
  getList()
  // 获得用户列表
  userList.value = await UserApi.getSimpleUserList()
})
</script>
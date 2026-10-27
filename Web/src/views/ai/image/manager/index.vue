<template>
  <doc-alert :title="tAi('image.title')" url="https://doc.iocoder.cn/ai/image/" />

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
          <el-form-item :label="tAi('image.platformLabel')" prop="platform">
            <el-select v-model="queryParams.status" :placeholder="tAi('chat.apiKey.platformPlaceholder')" clearable class="!w-240px">
              <el-option
                v-for="dict in getStrDictOptions(DICT_TYPE.AI_PLATFORM)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="tAi('image.manager.paintStatus')" prop="status">
            <el-select
              v-model="queryParams.status"
              :placeholder="tAi('tool.statusPlaceholder')"
              clearable
              class="!w-240px"
            >
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.AI_IMAGE_STATUS)"
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
          <el-form-item :label="tAi('image.manager.publicStatus')" prop="publicStatus">
            <el-select
              v-model="queryParams.publicStatus"
              :placeholder="tAi('image.manager.publicStatusPlaceholder')"
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
      <el-table-column :label="tAi('mindMap.id')" align="center" prop="id" min-width="180" fixed="left" />
      <el-table-column :label="tAi('image.manager.picUrl')" align="center" prop="picUrl" min-width="110" fixed="left">
        <template #default="{ row }">
          <el-image
            class="h-80px w-80px"
            lazy
            :src="row.picUrl"
            :preview-src-list="[row.picUrl]"
            preview-teleported
            fit="cover"
            v-if="row.picUrl?.length > 0"
          />
        </template>
      </el-table-column>
      <el-table-column :label="tAi('mindMap.user')" align="center" prop="userId" min-width="180">
        <template #default="scope">
          <span>{{ userList.find((item) => item.id === scope.row.userId)?.nickname }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="tAi('image.platformLabel')" align="center" prop="platform" min-width="120">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.AI_PLATFORM" :value="scope.row.platform" />
        </template>
      </el-table-column>
      <el-table-column :label="tAi('image.modelLabel')" align="center" prop="model" min-width="180" />
      <el-table-column :label="tAi('image.manager.paintStatus')" align="center" prop="status" min-width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.AI_IMAGE_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column :label="tAi('image.manager.publicStatus')" align="center" prop="publicStatus">
        <template #default="scope">
          <el-switch
            v-model="scope.row.publicStatus"
            :active-value="true"
            :inactive-value="false"
            @change="handleUpdatePublicStatusChange(scope.row)"
            :disabled="scope.row.status !== AiImageStatusEnum.SUCCESS"
          />
        </template>
      </el-table-column>
      <el-table-column :label="tAi('image.prompt')" align="center" prop="prompt" min-width="180" />
      <el-table-column
        :label="tAi('mindMap.task.createTime')"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        min-width="180"
      />
      <el-table-column :label="tAi('image.imageWidth')" align="center" prop="width" />
      <el-table-column :label="tAi('image.imageHeight')" align="center" prop="height" />
      <el-table-column :label="tAi('mindMap.errorMessage')" align="center" prop="errorMessage" />
      <el-table-column :label="tAi('image.manager.taskId')" align="center" prop="taskId" />
      <el-table-column :label="tAi('mindMap.operation')" align="center" min-width="100" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['ai:image:delete']"
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
import { getIntDictOptions, DICT_TYPE, getStrDictOptions, getBoolDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import { ImageApi, ImageVO } from '@/api/ai/image'
import * as UserApi from '@/api/system/user'
import { AiImageStatusEnum } from '@/views/ai/utils/constants'

/** AI 绘画 列表 */
defineOptions({ name: 'AiImageManager' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const tAi = useI18n('ai').t

const loading = ref(true) // 列表的加载中
const list = ref<ImageVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userId: undefined,
  platform: undefined,
  status: undefined,
  publicStatus: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const userList = ref<UserApi.UserVO[]>([]) // 用户列表

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ImageApi.getImagePage(queryParams)
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
    await ImageApi.deleteImage(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 修改是否发布 */
const handleUpdatePublicStatusChange = async (row: ImageVO) => {
  try {
    // 修改状态的二次确认
    const text = row.publicStatus ? tAi('image.manager.public') : tAi('image.manager.private')
    await message.confirm(tAi('image.manager.publicConfirm').replace('{text}', text))
    // 发起修改状态
    await ImageApi.updateImage({
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
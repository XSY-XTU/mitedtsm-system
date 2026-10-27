<template>
  <ContentWrap :title="t('ota.task.taskManagement')" class="mb-20px">
    <!-- 搜索栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
      @submit.prevent
    >
      <el-form-item>
        <el-button type="primary" @click="openTaskForm" v-hasPermi="['iot:ota-task:create']">
          <Icon icon="ep:plus" class="mr-5px" /> {{ t('common.add') }}
        </el-button>
      </el-form-item>
      <el-form-item class="float-right">
        <el-input
          v-model="queryParams.name"
          :placeholder="t('ota.task.namePlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
    </el-form>

    <!-- 任务列表 -->
    <el-table
      v-loading="taskLoading"
      :data="taskList"
      :stripe="true"
      :show-overflow-tooltip="true"
      class="mt-15px"
     :table-layout="'auto'">
      <el-table-column :label="t('ota.task.id')" align="center" prop="id" min-width="80" />
      <el-table-column :label="t('ota.task.name')" align="center" prop="name" />
      <el-table-column :label="t('ota.task.deviceScope')" align="center" prop="deviceScope">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.IOT_OTA_TASK_DEVICE_SCOPE" :value="scope.row.deviceScope" />
        </template>
      </el-table-column>
      <el-table-column :label="t('ota.task.upgradeProgress')" align="center">
        <template #default="scope">
          {{ scope.row.deviceSuccessCount }}/{{ scope.row.deviceTotalCount }}
        </template>
      </el-table-column>
      <el-table-column
        :label="t('ota.firmware.createTime')"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
      />
      <el-table-column :label="t('ota.task.description')" align="center" prop="description" show-overflow-tooltip />
      <el-table-column :label="t('ota.task.status')" align="center" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.IOT_OTA_TASK_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column :label="t('common.operation')" align="center" min-width="150" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="handleTaskDetail(scope.row.id)"> {{ t('common.detail') }} </el-button>
          <el-button
            v-if="scope.row.status === IoTOtaTaskStatusEnum.IN_PROGRESS.value"
            link
            type="danger"
            @click="handleCancelTask(scope.row.id)"
            v-hasPermi="['iot:ota-task:cancel']"
          >
            {{ t('common.cancel') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <Pagination
      :total="taskTotal"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getTaskList"
    />

    <!-- 新增任务弹窗 -->
    <OtaTaskForm
      ref="taskFormRef"
      :firmware-id="firmwareId"
      :product-id="productId"
      @success="handleTaskCreateSuccess"
    />

    <!-- 任务详情弹窗 -->
    <OtaTaskDetail ref="taskDetailRef" @success="refresh" />
  </ContentWrap>
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import { IoTOtaTaskApi, OtaTask } from '@/api/iot/ota/task'
import { DICT_TYPE } from '@/utils/dict'
import { IoTOtaTaskStatusEnum } from '@/views/iot/utils/constants'
import OtaTaskForm from './OtaTaskForm.vue'
import OtaTaskDetail from './OtaTaskDetail.vue'

/** IoT OTA 任务列表 */
defineOptions({ name: 'OtaTaskList' })

const props = defineProps<{
  firmwareId: number
  productId: number
}>()

const { t } = useI18n('iot') // 国际化
const message = useMessage() // 消息弹窗

// 任务列表
const taskLoading = ref(false)
const taskList = ref<OtaTask[]>([])
const taskTotal = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: '',
  firmwareId: props.firmwareId
})
const queryFormRef = ref() // 查询表单引用
const taskFormRef = ref() // 任务表单引用
const taskDetailRef = ref() // 任务详情引用

/** 获取任务列表 */
const getTaskList = async () => {
  taskLoading.value = true
  try {
    const data = await IoTOtaTaskApi.getOtaTaskPage(queryParams)
    taskList.value = data.list
    taskTotal.value = data.total
  } finally {
    taskLoading.value = false
  }
}

/** 搜索 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getTaskList()
}

/** 打开任务表单 */
const openTaskForm = () => {
  taskFormRef.value?.open()
}

/** 处理任务创建成功 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const handleTaskCreateSuccess = () => {
  getTaskList()
  emit('success')
}

/** 查看任务详情 */
const handleTaskDetail = (id: number) => {
  taskDetailRef.value?.open(id)
}

/** 取消任务 */
const handleCancelTask = async (id: number) => {
  try {
    await message.confirm(t('ota.task.cancelConfirm'))
    await IoTOtaTaskApi.cancelOtaTask(id)
    message.success(t('ota.task.cancelSuccess'))
    // 刷新数据
    await refresh()
  } catch (error) {
    console.error('取消任务失败', error)
  }
}

/** 刷新数据 */
const refresh = async () => {
  await getTaskList()
  emit('success')
}

/** 初始化 */
onMounted(() => {
  getTaskList()
})
</script>

<template>
  <doc-alert title="审批接入（业务表单）" url="https://doc.iocoder.cn/bpm/use-business-form/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      class="-mb-15px"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('oa.leave.type')" prop="type">
            <el-select
              v-model="queryParams.type"
              class="!w-240px"
              clearable
              :placeholder="t('oa.leave.typePlaceholder')"
            >
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.BPM_OA_LEAVE_TYPE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('oa.leave.applyTime')" prop="createTime">
            <el-date-picker
              v-model="queryParams.createTime"
              :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
              class="!w-240px"
              :end-placeholder="t('instance.endDate')"
              :start-placeholder="t('instance.startDate')"
              type="daterange"
              value-format="YYYY-MM-DD HH:mm:ss"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('oa.leave.approvalResult')" prop="status">
            <el-select
              v-model="queryParams.status"
              class="!w-240px"
              clearable
              :placeholder="t('oa.leave.approvalResultPlaceholder')"
            >
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.BPM_PROCESS_INSTANCE_STATUS)"
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
          <el-form-item :label="t('oa.leave.reason')" prop="reason">
            <el-input
              v-model="queryParams.reason"
              class="!w-240px"
              clearable
              :placeholder="t('oa.leave.reasonPlaceholder')"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button @click="handleQuery">
              <Icon class="mr-5px" icon="ep:search" />
              {{ t('common.search') }}
            </el-button>
            <el-button @click="resetQuery">
              <Icon class="mr-5px" icon="ep:refresh" />
              {{ t('common.reset') }}
            </el-button>
            <el-button plain type="primary" @click="handleCreate()">
              <Icon class="mr-5px" icon="ep:plus" />
              {{ t('oa.leave.createLeave') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :table-layout="'auto'">
      <el-table-column align="center" :label="t('oa.leave.applyId')" prop="id" />
      <el-table-column align="center" :label="t('common.status')" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.BPM_PROCESS_INSTANCE_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('oa.leave.startTime')"
        prop="startTime"
        min-width="180"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('oa.leave.endTime')"
        prop="endTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('oa.leave.type')" prop="type">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.BPM_OA_LEAVE_TYPE" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('oa.leave.reason')" prop="reason" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('oa.leave.applyTime')"
        prop="createTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('common.operation')" min-width="200">
        <template #default="scope">
          <el-button
            v-hasPermi="['bpm:oa-leave:query']"
            link
            type="primary"
            @click="handleDetail(scope.row)"
          >
            {{ t('common.detail') }}
          </el-button>
          <el-button
            v-hasPermi="['bpm:oa-leave:query']"
            link
            type="primary"
            @click="handleProcessDetail(scope.row)"
          >
            {{ t('oa.leave.progress') }}
          </el-button>
          <el-button
            v-if="scope.row.result === 1"
            v-hasPermi="['bpm:oa-leave:create']"
            link
            type="danger"
            @click="cancelLeave(scope.row)"
          >
            {{ t('approval.cancel') }}
          </el-button>
          <el-button
            v-if="scope.row.status !== 1"
            v-hasPermi="['bpm:oa-leave:create']"
            link
            type="primary"
            @click="handleReCreate(scope.row)"
          >
            {{ t('instance.restart') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>
</template>
<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as LeaveApi from '@/api/bpm/leave'
import * as ProcessInstanceApi from '@/api/bpm/processInstance'

defineOptions({ name: 'BpmOALeave' })

const message = useMessage() // 消息弹窗
const router = useRouter() // 路由
const { t } = useI18n('bpm') // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  type: undefined,
  status: undefined,
  reason: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await LeaveApi.getLeavePage(queryParams)
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

/** 添加操作 */
const handleCreate = () => {
  router.push({ name: 'OALeaveCreate' })
}

/** 重新发起操作 */
const handleReCreate = (row: LeaveApi.LeaveVO) => {
  router.push({
    name: 'OALeaveCreate',
    query: {
      id: row.id
    }
  })
}

/** 详情操作 */
const handleDetail = (row: LeaveApi.LeaveVO) => {
  router.push({
    name: 'OALeaveDetail',
    query: {
      id: row.id
    }
  })
}

/** 取消请假操作 */
const cancelLeave = async (row) => {
  // 二次确认
  const { value } = await ElMessageBox.prompt(t('instance.cancelReason'), t('instance.cancelTitle'), {
    confirmButtonText: t('common.ok'),
    cancelButtonText: t('common.cancel'),
    inputPattern: /^[\s\S]*.*\S[\s\S]*$/, // 判断非空，且非空格
    inputErrorMessage: t('instance.cancelReasonRequired')
  })
  // 发起取消
  await ProcessInstanceApi.cancelProcessInstanceByStartUser(row.id, value)
  message.success(t('instance.cancelSuccess'))
  // 刷新列表
  await getList()
}

/** 审批进度 */
const handleProcessDetail = (row) => {
  router.push({
    name: 'BpmProcessInstanceDetail',
    query: {
      id: row.processInstanceId
    }
  })
}

watch(
  () => router.currentRoute.value,
  () => {
    getList()
  }
)

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

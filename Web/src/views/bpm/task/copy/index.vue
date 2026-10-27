<!-- 工作流 - 抄送我的流程 -->
<template>
  <doc-alert
    title="审批转办、委派、抄送"
    url="https://doc.iocoder.cn/bpm/task-delegation-and-cc/"
  />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form ref="queryFormRef" class="-mb-15px" label-width="auto">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('process.instance.name')" prop="name">
            <el-input
              v-model="queryParams.processInstanceName"
              @keyup.enter="handleQuery"
              class="!w-240px"
              clearable
              :placeholder="t('process.instance.namePlaceholder')"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('process.task.createTime')" prop="createTime">
            <el-date-picker
              v-model="queryParams.createTime"
              :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
              class="!w-240px"
              :end-placeholder="t('common.endTime')"
              :start-placeholder="t('common.startTime')"
              type="daterange"
              value-format="YYYY-MM-DD HH:mm:ss"
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
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :table-layout="'auto'">
      <!-- TODO 芋艿：增加摘要 -->
      <el-table-column align="center" :label="t('process.task.processName')" prop="processInstanceName" min-width="180" />
      <el-table-column :label="t('process.instance.summary')" prop="summary" min-width="180">
        <template #default="scope">
          <div class="flex flex-col" v-if="scope.row.summary && scope.row.summary.length > 0">
            <div v-for="(item, index) in scope.row.summary" :key="index">
              <el-text type="info"> {{ item.key }} : {{ item.value }} </el-text>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        :label="t('process.instance.initiator')"
        prop="startUser.nickname"
        min-width="100"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('process.instance.initiatorTime')"
        prop="processInstanceStartTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('process.task.currentTask')" prop="activityName" min-width="180" />
      <el-table-column align="center" :label="t('process.task.assignee')" min-width="100">
        <template #default="scope"> {{ scope.row.createUser?.nickname || t('common.system') }} </template>
      </el-table-column>
      <el-table-column align="center" :label="t('process.task.approvalOpinion')" prop="reason" min-width="150" />
      <el-table-column
        align="center"
        :label="t('process.task.createTime')"
        prop="createTime"
        min-width="180"
        :formatter="dateFormatter"
      />
      <el-table-column align="center" :label="t('common.operation')" fixed="right" min-width="150">
        <template #default="scope">
          <el-button link type="primary" @click="handleAudit(scope.row)">{{ t('process.instance.detail') }}</el-button>
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
import { dateFormatter } from '@/utils/formatTime'
import * as ProcessInstanceApi from '@/api/bpm/processInstance'

defineOptions({ name: 'BpmProcessInstanceCopy' })

const { push } = useRouter() // 路由
const { t } = useI18n('bpm') // 国际化

const loading = ref(false) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  processInstanceId: '',
  processInstanceName: '',
  createTime: []
})
const queryFormRef = ref() // 搜索的表单

/** 查询任务列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ProcessInstanceApi.getProcessInstanceCopyPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 处理审批按钮 */
const handleAudit = (row: any) => {
  const query = {
    id: row.processInstanceId,
    activityId: undefined
  }
  if (row.activityId) {
    query.activityId = row.activityId
  }
  push({
    name: 'BpmProcessInstanceDetail',
    query: query
  })
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

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

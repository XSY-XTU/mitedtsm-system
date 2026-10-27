<template>
  <doc-alert :title="t('apiLog.systemLog')" url="https://doc.iocoder.cn/system-log/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      label-width="auto"
      :model="queryParams"
      ref="queryFormRef"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('apiLog.userId')" prop="userId">
        <el-input
          v-model="queryParams.userId"
          :placeholder="t('apiLog.userIdPlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('apiLog.userType')" prop="userType">
        <el-select
          v-model="queryParams.userType"
          :placeholder="t('common.selectPlaceholder')"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.USER_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('apiLog.applicationName')" prop="applicationName">
        <el-input
          v-model="queryParams.applicationName"
          :placeholder="t('apiLog.applicationNamePlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('apiLog.requestTime')" prop="beginTime">
        <el-date-picker
          v-model="queryParams.beginTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          :start-placeholder="t('common.startTime')"
          :end-placeholder="t('common.endTime')"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-240px"
        />
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('apiLog.duration')" prop="duration">
        <el-input
          v-model="queryParams.duration"
          :placeholder="t('apiLog.durationPlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('apiLog.resultCode')" prop="resultCode">
        <el-input
          v-model="queryParams.resultCode"
          :placeholder="t('apiLog.resultCodePlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> {{ t('common.query') }}</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> {{ t('common.reset') }}</el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['infra:api-access-log:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> {{ t('common.export') }}
        </el-button>
      </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :table-layout="'auto'">
      <el-table-column :label="t('apiLog.id')" align="center" prop="id" min-width="100" fix="right" />
      <el-table-column :label="t('apiLog.userId')" align="center" prop="userId" />
      <el-table-column :label="t('apiLog.userType')" align="center" prop="userType">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.USER_TYPE" :value="scope.row.userType" />
        </template>
      </el-table-column>
      <el-table-column :label="t('apiLog.applicationName')" align="center" prop="applicationName" min-width="150" />
      <el-table-column :label="t('apiLog.requestMethod')" align="center" prop="requestMethod" min-width="80" />
      <el-table-column :label="t('apiLog.requestUrl')" align="center" prop="requestUrl" min-width="500" />
      <el-table-column :label="t('apiLog.requestTime')" align="center" prop="beginTime" min-width="180">
        <template #default="scope">
          <span>{{ formatDate(scope.row.beginTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="t('apiLog.duration')" align="center" prop="duration" min-width="180">
        <template #default="scope"> {{ scope.row.duration }} ms </template>
      </el-table-column>
      <el-table-column :label="t('apiLog.result')" align="center" prop="status">
        <template #default="scope">
          {{ scope.row.resultCode === 0 ? t('apiLog.success') : t('apiLog.fail') + '(' + scope.row.resultMsg + ')' }}
        </template>
      </el-table-column>
      <el-table-column :label="t('apiLog.operateModule')" align="center" prop="operateModule" min-width="180" />
      <el-table-column :label="t('apiLog.operateName')" align="center" prop="operateName" min-width="180" />
      <el-table-column :label="t('apiLog.operateType')" align="center" prop="operateType">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.INFRA_OPERATE_TYPE" :value="scope.row.operateType" />
        </template>
      </el-table-column>
      <el-table-column :label="t('common.operation')" align="center" fixed="right" min-width="150">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openDetail(scope.row)"
            v-hasPermi="['infra:api-access-log:query']"
          >
            {{ t('common.detail') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗：详情 -->
  <ApiAccessLogDetail ref="detailRef" />
</template>
<script lang="ts" setup>
import { useI18n } from '@/hooks/web/useI18n'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import download from '@/utils/download'
import { formatDate } from '@/utils/formatTime'
import * as ApiAccessLogApi from '@/api/infra/apiAccessLog'
import ApiAccessLogDetail from './ApiAccessLogDetail.vue'

defineOptions({ name: 'InfraApiAccessLog' })

const { t } = useI18n('infra')
const message = useMessage() // 消息弹窗

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userId: null,
  userType: null,
  applicationName: null,
  requestUrl: null,
  duration: null,
  resultCode: null,
  beginTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ApiAccessLogApi.getApiAccessLogPage(queryParams)
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

/** 详情操作 */
const detailRef = ref()
const openDetail = (data: ApiAccessLogApi.ApiAccessLogVO) => {
  detailRef.value.open(data)
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await ApiAccessLogApi.exportApiAccessLog(queryParams)
    download.excel(data, t('common.exportFilename.apiAccessLog') + '.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

<template>
  <doc-alert title="【回款】回款管理、回款计划" url="https://doc.iocoder.cn/crm/receivable/" />
  <doc-alert title="【通用】数据权限" url="https://doc.iocoder.cn/crm/permission/" />

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
          <el-form-item :label="t('receivablePlan.customerName')" prop="customerId">
            <el-select
              v-model="queryParams.customerId"
              class="!w-240px"
              :placeholder="t('customer.ownerUserPlaceholder')"
              @keyup.enter="handleQuery"
            >
              <el-option
                v-for="item in customerList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('receivablePlan.contractNo')" prop="contractNo">
            <el-input
              v-model="queryParams.contractNo"
              class="!w-240px"
              clearable
              :placeholder="t('receivablePlan.contractNoPlaceholder')"
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
            <el-button
              v-hasPermi="['crm:receivable-plan:create']"
              plain
              type="primary"
              @click="openForm('create')"
            >
              <Icon class="mr-5px" icon="ep:plus" />
              {{ t('action.add') }}
            </el-button>
            <el-button
              v-hasPermi="['crm:receivable-plan:export']"
              :loading="exportLoading"
              plain
              type="success"
              @click="handleExport"
            >
              <Icon class="mr-5px" icon="ep:download" />
              {{ t('common.export') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 统计卡片 -->
  <ContentWrap>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic :value="overdueStats.overdueCount">
            <template #title>
              <span style="color: #f56c6c">{{ t('receivablePlan.overdueCount') }}</span>
            </template>
          </el-statistic>
          <div class="text-right text-12px" style="color: #f56c6c">
            {{ t('receivablePlan.overdueTotalPrice') }}: {{ erpPriceInputFormatter(overdueStats.overdueTotalPrice) }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic :value="overdueStats.pendingCount">
            <template #title>
              <span style="color: #e6a23c">{{ t('receivablePlan.pendingCount') }}</span>
            </template>
          </el-statistic>
          <div class="text-right text-12px" style="color: #e6a23c">
            {{ t('receivablePlan.pendingTotalPrice') }}: {{ erpPriceInputFormatter(overdueStats.pendingTotalPrice) }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic :value="summaryStats.totalCount">
            <template #title>
              <span style="color: #409eff">{{ t('receivablePlan.totalCount') }}</span>
            </template>
          </el-statistic>
          <div class="text-right text-12px" style="color: #409eff">
            {{ t('receivablePlan.totalPrice') }}: {{ erpPriceInputFormatter(summaryStats.totalPrice) }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic :value="summaryStats.completionRate + '%'">
            <template #title>
              <span style="color: #67c23a">{{ t('receivablePlan.completionRate') }}</span>
            </template>
          </el-statistic>
          <div class="text-right text-12px" style="color: #67c23a">
            {{ t('receivablePlan.receivedCount') }}: {{ summaryStats.receivedCount }}
          </div>
        </el-card>
      </el-col>
    </el-row>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-tabs v-model="activeName" @tab-click="handleTabClick">
      <el-tab-pane :label="t('customer.myResponsible')" name="1" />
      <el-tab-pane :label="t('customer.subordinateResponsible')" name="3" />
    </el-tabs>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true" :table-layout="'auto'">
      <el-table-column align="center" fixed="left" :label="t('receivablePlan.customerName')" prop="customerName" min-width="150">
        <template #default="scope">
          <el-link
            :underline="false"
            type="primary"
            @click="openCustomerDetail(scope.row.customerId)"
          >
            {{ scope.row.customerName }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('receivablePlan.contractNo')" prop="contractNo" min-width="200" />
      <el-table-column align="center" :label="t('receivablePlan.period')" prop="period">
        <template #default="scope">
          <el-link :underline="false" type="primary" @click="openDetail(scope.row.id)">
            {{ scope.row.period }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        :label="t('receivablePlan.price') + '（元）'"
        prop="price"
        min-width="160"
        :formatter="erpPriceTableColumnFormatter"
      />
      <el-table-column
        :formatter="dateFormatter2"
        align="center"
        :label="t('receivablePlan.returnTime')"
        prop="returnTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('receivablePlan.remindDays')" prop="remindDays" min-width="150" />
      <el-table-column
        align="center"
        :label="t('receivablePlan.remindTime')"
        prop="remindTime"
        min-width="180"
        :formatter="dateFormatter2"
      />
      <el-table-column align="center" :label="t('receivablePlan.returnType')" prop="returnType" min-width="130">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_RECEIVABLE_RETURN_TYPE" :value="scope.row.returnType" />
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('receivablePlan.remark')" prop="remark" />
      <el-table-column :label="t('receivablePlan.ownerUserName')" prop="ownerUserName" min-width="120" />
      <el-table-column
        align="center"
        :label="t('receivablePlan.receivablePrice') + '（元）'"
        prop="receivable.price"
        min-width="160"
      >
        <template #default="scope">
          <el-text v-if="scope.row.receivable">
            {{ erpPriceInputFormatter(scope.row.receivable.price) }}
          </el-text>
          <el-text v-else>{{ erpPriceInputFormatter(0) }}</el-text>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        :label="t('receivable.returnTime')"
        prop="receivable.returnTime"
        min-width="180"
        :formatter="dateFormatter2"
      />
      <el-table-column
        align="center"
        :label="t('receivablePlan.unreceivedPrice') + '（元）'"
        prop="receivable.price"
        min-width="160"
      >
        <template #default="scope">
          <el-text v-if="scope.row.receivable">
            {{ erpPriceInputFormatter(scope.row.price - scope.row.receivable.price) }}
          </el-text>
          <el-text v-else>{{ erpPriceInputFormatter(scope.row.price) }}</el-text>
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('receivablePlan.updateTime')"
        prop="updateTime"
        min-width="180"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('receivablePlan.createTime')"
        prop="createTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('receivablePlan.creatorName')" prop="creatorName" min-width="100" />
      <el-table-column align="center" fixed="right" :label="t('common.action')" min-width="180">
        <template #default="scope">
          <el-button
            v-hasPermi="['crm:receivable:create']"
            link
            type="success"
            @click="openReceivableForm(scope.row)"
            :disabled="scope.row.receivableId"
          >
            {{ t('receivablePlan.createReceivable') }}
          </el-button>
          <el-button
            v-hasPermi="['crm:receivable-plan:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            {{ t('common.edit') }}
          </el-button>
          <el-button
            v-hasPermi="['crm:receivable-plan:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            {{ t('common.delete') }}
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

  <!-- 表单弹窗：添加/修改 -->
  <ReceivablePlanForm ref="formRef" @success="getList" />
  <ReceivableForm ref="receivableFormRef" @success="getList" />
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { dateFormatter, dateFormatter2 } from '@/utils/formatTime'
import download from '@/utils/download'
import * as ReceivablePlanApi from '@/api/crm/receivable/plan'
import ReceivablePlanForm from './ReceivablePlanForm.vue'
import * as CustomerApi from '@/api/crm/customer'
import { erpPriceInputFormatter, erpPriceTableColumnFormatter } from '@/utils'
import { TabsPaneContext } from 'element-plus'
import ReceivableForm from '@/views/crm/receivable/ReceivableForm.vue'

defineOptions({ name: 'ReceivablePlan' })

const message = useMessage() // 消息弹窗
const { t } = useI18n('crm') // 国际化
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  sceneType: '1', // 默认与 activeName 相等
  customerId: undefined,
  contractNo: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中
const activeName = ref('1') // 列表 tab
const customerList = ref<CustomerApi.CustomerVO[]>([]) // 客户列表

// 统计数据
const overdueStats = reactive({
  overdueCount: 0,
  overdueTotalPrice: 0,
  pendingCount: 0,
  pendingTotalPrice: 0
})
const summaryStats = reactive({
  totalCount: 0,
  totalPrice: 0,
  receivedCount: 0,
  receivedPrice: 0,
  completionRate: 0
})

/** 加载统计数据 */
const loadStats = async () => {
  try {
    const overdueData = await ReceivablePlanApi.getReceivablePlanOverdueSummary()
    Object.assign(overdueStats, overdueData)
    const summaryData = await ReceivablePlanApi.getReceivablePlanSummary()
    Object.assign(summaryStats, summaryData)
  } catch {}
}

/** tab 切换 */
const handleTabClick = (tab: TabsPaneContext) => {
  queryParams.sceneType = tab.paneName
  handleQuery()
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ReceivablePlanApi.getReceivablePlanPage(queryParams)
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

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 创建回款操作 */
const receivableFormRef = ref()
const openReceivableForm = (row: ReceivablePlanApi.ReceivablePlanVO) => {
  receivableFormRef.value.open('create', undefined, row)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await ReceivablePlanApi.deleteReceivablePlan(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await ReceivablePlanApi.exportReceivablePlan(queryParams)
    download.excel(data, t('receivablePlan.exportFileName') + '.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 打开详情 */
const { push } = useRouter()
const openDetail = (id: number) => {
  push({ name: 'CrmReceivablePlanDetail', params: { id } })
}

/** 打开客户详情 */
const openCustomerDetail = (id: number) => {
  push({ name: 'CrmCustomerDetail', params: { id } })
}

/** 初始化 **/
onMounted(async () => {
  await Promise.all([getList(), loadStats()])
  // 获得客户列表
  customerList.value = await CustomerApi.getCustomerSimpleList()
})
</script>

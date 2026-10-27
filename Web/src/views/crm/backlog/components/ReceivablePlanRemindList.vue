<!-- 待回款提醒 -->
<template>
  <ContentWrap>
    <div class="pb-5 text-xl">{{ t('backlog.receivablePlanRemind') }}</div>
    <!-- 搜索工作区 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item :label="t('backlog.remindType')" prop="remindType">
        <el-select
          v-model="queryParams.remindType"
          class="!w-240px"
          :placeholder="t('common.status')"
          @change="handleQuery"
        >
          <el-option
            v-for="(option, index) in RECEIVABLE_REMIND_TYPE"
            :label="option.label"
            :value="option.value"
            :key="index"
          />
        </el-select>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true" :table-layout="'auto'">
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
        :label="t('receivablePlan.price')"
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
        :label="t('receivablePlan.receivablePrice')"
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
        :label="t('statistics.customer.dealCycleByUser')"
        prop="receivable.returnTime"
        min-width="180"
        :formatter="dateFormatter2"
      />
      <el-table-column
        align="center"
        :label="t('receivablePlan.receivablePrice')"
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
        :label="t('common.updateTime')"
        prop="updateTime"
        min-width="180"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('common.createTime')"
        prop="createTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('common.creator')" prop="creatorName" min-width="100" />
      <el-table-column align="center" fixed="right" :label="t('common.action')" min-width="180">
        <template #default="scope">
          <el-button
            v-hasPermi="['crm:receivable:create']"
            link
            type="success"
            @click="openReceivableForm(scope.row)"
            :disabled="scope.row.receivableId"
          >
            {{ t('backlog.createReceivable') }}
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

  <!-- 表单弹窗：添加/修改 -->
  <ReceivableForm ref="receivableFormRef" @success="getList" />
</template>

<script setup lang="ts">
import { DICT_TYPE } from '@/utils/dict'
import { dateFormatter, dateFormatter2 } from '@/utils/formatTime'
import * as ReceivablePlanApi from '@/api/crm/receivable/plan'
import { RECEIVABLE_REMIND_TYPE } from './common'
import { erpPriceInputFormatter, erpPriceTableColumnFormatter } from '@/utils'
import ReceivableForm from '@/views/crm/receivable/ReceivableForm.vue'

defineOptions({ name: 'ReceivablePlanRemindList' })

const { t } = useI18n('crm') // 国际化
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  remindType: 1
})
const queryFormRef = ref() // 搜索的表单
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

/** 创建回款操作 */
const receivableFormRef = ref()
const openReceivableForm = (row: ReceivablePlanApi.ReceivablePlanVO) => {
  receivableFormRef.value.open('create', undefined, row)
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

/** 激活时 */
onActivated(async () => {
  await getList()
})

/** 初始化 **/
onMounted(async () => {
  await getList()
})
</script>

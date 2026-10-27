<!-- 待审核合同 -->
<template>
  <ContentWrap>
    <div class="pb-5 text-xl">{{ t('backlog.contractAudit') }}</div>
    <!-- 搜索工作区 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item :label="t('contract.auditStatus')" prop="auditStatus">
        <el-select
          v-model="queryParams.auditStatus"
          class="!w-240px"
          :placeholder="t('common.status')"
          @change="handleQuery"
        >
          <el-option
            v-for="(option, index) in AUDIT_STATUS"
            :label="option.label"
            :value="option.value"
            :key="index"
          />
        </el-select>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true" :table-layout="'auto'">
      <el-table-column align="center" fixed="left" :label="t('contract.no')" prop="no" min-width="180" />
      <el-table-column align="center" fixed="left" :label="t('contract.name')" prop="name" min-width="160">
        <template #default="scope">
          <el-link :underline="false" type="primary" @click="openDetail(scope.row.id)">
            {{ scope.row.name }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('contract.customerName')" prop="customerName" min-width="120">
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
      <el-table-column align="center" :label="t('contract.businessName')" prop="businessName" min-width="130">
        <template #default="scope">
          <el-link
            :underline="false"
            type="primary"
            @click="openBusinessDetail(scope.row.businessId)"
          >
            {{ scope.row.businessName }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        :label="t('contract.totalPrice')"
        prop="totalPrice"
        min-width="140"
        :formatter="erpPriceTableColumnFormatter"
      />
      <el-table-column
        align="center"
        :label="t('contract.orderDate')"
        prop="orderDate"
        min-width="120"
        :formatter="dateFormatter2"
      />
      <el-table-column
        align="center"
        :label="t('contract.startTime')"
        prop="startTime"
        min-width="120"
        :formatter="dateFormatter2"
      />
      <el-table-column
        align="center"
        :label="t('contract.endTime')"
        prop="endTime"
        min-width="120"
        :formatter="dateFormatter2"
      />
      <el-table-column align="center" :label="t('contract.signContactName')" prop="contactName" min-width="130">
        <template #default="scope">
          <el-link
            :underline="false"
            type="primary"
            @click="openContactDetail(scope.row.signContactId)"
          >
            {{ scope.row.signContactName }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('contract.signUserName')" prop="signUserName" min-width="130" />
      <el-table-column align="center" :label="t('contract.remark')" prop="remark" min-width="200" />
      <el-table-column
        align="center"
        :label="t('contract.totalReceivablePrice')"
        prop="totalReceivablePrice"
        min-width="140"
        :formatter="erpPriceTableColumnFormatter"
      />
      <el-table-column
        align="center"
        :label="t('contract.unreceivablePrice')"
        prop="totalReceivablePrice"
        min-width="140"
        :formatter="erpPriceTableColumnFormatter"
      >
        <template #default="scope">
          {{ erpPriceInputFormatter(scope.row.totalPrice - scope.row.totalReceivablePrice) }}
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('contract.contactLastTime')"
        prop="contactLastTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('contract.ownerUserName')" prop="ownerUserName" min-width="120" />
      <el-table-column align="center" :label="t('contract.ownerUserDeptName')" prop="ownerUserDeptName" min-width="100" />
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
      <el-table-column align="center" :label="t('common.creator')" prop="creatorName" min-width="120" />
      <el-table-column align="center" fixed="right" :label="t('contract.auditStatus')" prop="auditStatus" min-width="120">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_AUDIT_STATUS" :value="scope.row.auditStatus" />
        </template>
      </el-table-column>
      <el-table-column fixed="right" :label="t('common.action')" min-width="150">
        <template #default="scope">
          <el-button
            link
            v-hasPermi="['crm:contract:update']"
            type="primary"
            @click="handleProcessDetail(scope.row)"
          >
            {{ t('backlog.viewApproval') }}
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

<script setup lang="ts" name="CheckContract">
import { dateFormatter, dateFormatter2 } from '@/utils/formatTime'
import * as ContractApi from '@/api/crm/contract'
import { DICT_TYPE } from '@/utils/dict'
import { AUDIT_STATUS } from './common'
import { erpPriceInputFormatter, erpPriceTableColumnFormatter } from '@/utils'

const { t } = useI18n('crm') // 国际化
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  sceneType: 1, // 我负责的
  auditStatus: 10
})
const queryFormRef = ref() // 搜索的表单
/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ContractApi.getContractPage(queryParams)
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

/** 查看审批 */
const handleProcessDetail = (row: ContractApi.ContractVO) => {
  push({ name: 'BpmProcessInstanceDetail', query: { id: row.processInstanceId } })
}

/** 打开合同详情 */
const { push } = useRouter()
const openDetail = (id: number) => {
  push({ name: 'CrmContractDetail', params: { id } })
}

/** 打开客户详情 */
const openCustomerDetail = (id: number) => {
  push({ name: 'CrmCustomerDetail', params: { id } })
}

/** 打开联系人详情 */
const openContactDetail = (id: number) => {
  push({ name: 'CrmContactDetail', params: { id } })
}

/** 打开商机详情 */
const openBusinessDetail = (id: number) => {
  push({ name: 'CrmBusinessDetail', params: { id } })
}

/** 激活时 */
onActivated(async () => {
  await getList()
})

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

<style scoped></style>
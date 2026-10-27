<template>
  <doc-alert title="【合同】合同管理、合同提取" url="https://doc.iocoder.cn/crm/contract/" />
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
          <el-form-item :label="t('crm.contract.no')" prop="no">
            <el-input
              v-model="queryParams.no"
              class="!w-240px"
              clearable
              :placeholder="t('crm.contract.noPlaceholder')"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('crm.contract.name')" prop="name">
            <el-input
              v-model="queryParams.name"
              class="!w-240px"
              clearable
              :placeholder="t('crm.contract.namePlaceholder')"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('crm.contract.customerId')" prop="customerId">
            <el-select
              v-model="queryParams.customerId"
              class="!w-240px"
              clearable
              lable-key="name"
              :placeholder="t('crm.contract.customerIdPlaceholder')"
              value-key="id"
              @keyup.enter="handleQuery"
            >
              <el-option
                v-for="item in customerList"
                :key="item.id"
                :label="item.name"
                :value="item.id!"
              />
            </el-select>
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
            <el-button v-hasPermi="['crm:contract:create']" type="primary" @click="openForm('create')">
              <Icon class="mr-5px" icon="ep:plus" />
              {{ t('common.add') }}
            </el-button>
            <el-button
              v-hasPermi="['crm:contract:export']"
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

  <!-- 列表 -->
  <ContentWrap>
    <el-tabs v-model="activeName" @tab-click="handleTabClick">
      <el-tab-pane :label="t('crm.customer.myResponsible')" name="1" />
      <el-tab-pane :label="t('crm.customer.myInvolved')" name="2" />
      <el-tab-pane :label="t('crm.customer.subordinateResponsible')" name="3" />
    </el-tabs>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true" :table-layout="'auto'">
      <el-table-column align="center" fixed="left" :label="t('crm.contract.no')" prop="no" min-width="180" />
      <el-table-column align="center" fixed="left" :label="t('crm.contract.name')" prop="name" min-width="160">
        <template #default="scope">
          <el-link :underline="false" type="primary" @click="openDetail(scope.row.id)">
            {{ scope.row.name }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('crm.contract.customerName')" prop="customerName" min-width="120">
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
      <el-table-column align="center" :label="t('crm.contract.businessName')" prop="businessName" min-width="130">
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
        :label="t('crm.contract.totalPrice') + '（元）'"
        prop="totalPrice"
        min-width="140"
        :formatter="erpPriceTableColumnFormatter"
      />
      <el-table-column
        align="center"
        :label="t('crm.contract.orderDate')"
        prop="orderDate"
        min-width="120"
        :formatter="dateFormatter2"
      />
      <el-table-column
        align="center"
        :label="t('crm.contract.startTime')"
        prop="startTime"
        min-width="120"
        :formatter="dateFormatter2"
      />
      <el-table-column
        align="center"
        :label="t('crm.contract.endTime')"
        prop="endTime"
        min-width="120"
        :formatter="dateFormatter2"
      />
      <el-table-column align="center" :label="t('crm.contract.contactName')" prop="contactName" min-width="130">
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
      <el-table-column align="center" :label="t('crm.contract.signUserName')" prop="signUserName" min-width="130" />
      <el-table-column align="center" :label="t('crm.contract.remark')" prop="remark" min-width="200" />
      <el-table-column
        align="center"
        :label="t('crm.contract.totalReceivablePrice') + '（元）'"
        prop="totalReceivablePrice"
        min-width="140"
        :formatter="erpPriceTableColumnFormatter"
      />
      <el-table-column
        align="center"
        :label="t('crm.contract.unreceivablePrice') + '（元）'"
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
        :label="t('crm.contract.contactLastTime')"
        prop="contactLastTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('crm.contract.ownerUserName')" prop="ownerUserName" min-width="120" />
      <el-table-column align="center" :label="t('crm.contract.ownerUserDeptName')" prop="ownerUserDeptName" min-width="100" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('crm.contract.updateTime')"
        prop="updateTime"
        min-width="180"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('crm.contract.createTime')"
        prop="createTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('crm.contract.creatorName')" prop="creatorName" min-width="120" />
      <el-table-column align="center" fixed="right" :label="t('crm.contract.auditStatus')" prop="auditStatus" min-width="120">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_AUDIT_STATUS" :value="scope.row.auditStatus" />
        </template>
      </el-table-column>
      <el-table-column fixed="right" :label="t('common.action')" min-width="250">
        <template #default="scope">
          <el-button
            v-if="scope.row.auditStatus === 0"
            v-hasPermi="['crm:contract:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            {{ t('common.edit') }}
          </el-button>
          <el-button
            v-if="scope.row.auditStatus === 0"
            v-hasPermi="['crm:contract:update']"
            link
            type="primary"
            @click="handleSubmit(scope.row)"
          >
            {{ t('crm.contract.submitAudit') }}
          </el-button>
          <el-button
            v-else
            link
            v-hasPermi="['crm:contract:update']"
            type="primary"
            @click="handleProcessDetail(scope.row)"
          >
            {{ t('crm.contract.viewApproval') }}
          </el-button>
          <el-button
            v-hasPermi="['crm:contract:query']"
            link
            type="primary"
            @click="openDetail(scope.row.id)"
          >
            {{ t('common.detail') }}
          </el-button>
          <el-button
            v-hasPermi="['crm:contract:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            {{ t('common.del') }}
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
  <ContractForm ref="formRef" @success="getList" />
</template>
<script lang="ts" setup>
import { dateFormatter, dateFormatter2 } from '@/utils/formatTime'
import download from '@/utils/download'
import * as ContractApi from '@/api/crm/contract'
import ContractForm from './ContractForm.vue'
import { DICT_TYPE } from '@/utils/dict'
import { erpPriceInputFormatter, erpPriceTableColumnFormatter } from '@/utils'
import * as CustomerApi from '@/api/crm/customer'
import { TabsPaneContext } from 'element-plus'

defineOptions({ name: 'CrmContract' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  sceneType: '1', // 默认与 activeName 相等
  name: null,
  customerId: null,
  orderDate: [],
  no: null
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中
const activeName = ref('1') // 列表 tab
const customerList = ref<CustomerApi.CustomerVO[]>([]) // 客户列表

/** tab 切换 */
const handleTabClick = (tab: TabsPaneContext) => {
  queryParams.sceneType = tab.paneName
  handleQuery()
}

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

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await ContractApi.deleteContract(id)
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
    const data = await ContractApi.exportContract(queryParams)
    download.excel(data, '合同.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 提交审核 **/
const handleSubmit = async (row: ContractApi.ContractVO) => {
  await message.confirm(t('crm.contract.submitAuditConfirm', { name: row.name }))
  await ContractApi.submitContract(row.id)
  message.success(t('crm.contract.submitAuditSuccess'))
  await getList()
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

/** 初始化 **/
onMounted(async () => {
  await getList()
  customerList.value = await CustomerApi.getCustomerSimpleList()
})
</script>

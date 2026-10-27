<template>
  <doc-alert
    title="【财务】采购付款、销售收款"
    url="https://doc.iocoder.cn/sale/finance-payment-receipt/"
  />

  <ContentWrap>
    <!-- 搜索工作区 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item :label="t('finance.receipt.no')" prop="no">
        <el-input
          v-model="queryParams.no"
          :placeholder="t('finance.receipt.noPlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item :label="t('finance.receipt.receiptTime')" prop="receiptTime">
        <el-date-picker
          v-model="queryParams.receiptTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          :start-placeholder="t('common.startTime')"
          :end-placeholder="t('common.endTime')"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item :label="t('finance.receipt.customerId')" prop="customerId">
        <el-select
          v-model="queryParams.customerId"
          clearable
          filterable
          :placeholder="t('finance.receipt.selectCustomer')"
          class="!w-240px"
        >
          <el-option
            v-for="item in customerList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('finance.receipt.creator')" prop="creator">
        <el-select
          v-model="queryParams.creator"
          clearable
          filterable
          :placeholder="t('finance.receipt.selectCreator')"
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
      <el-form-item :label="t('finance.receipt.financeUserId')" prop="financeUserId">
        <el-select
          v-model="queryParams.financeUserId"
          clearable
          filterable
          :placeholder="t('finance.receipt.selectFinanceUser')"
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
      <el-form-item :label="t('finance.receipt.accountId')" prop="accountId">
        <el-select
          v-model="queryParams.accountId"
          clearable
          filterable
          :placeholder="t('finance.receipt.selectAccount')"
          class="!w-240px"
        >
          <el-option
            v-for="item in accountList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('common.status')" prop="status">
        <el-select v-model="queryParams.status" :placeholder="t('finance.receipt.selectStatus')" clearable class="!w-240px">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.ERP_AUDIT_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('common.remark')" prop="remark">
        <el-input
          v-model="queryParams.remark"
          :placeholder="t('finance.receipt.remarkPlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item :label="t('finance.receipt.bizNo')" prop="bizNo">
        <el-input
          v-model="queryParams.bizNo"
          :placeholder="t('finance.receipt.bizNoPlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> {{ t('common.search') }}</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> {{ t('common.reset') }}</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['erp:finance-receipt:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> {{ t('common.add') }}
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['erp:finance-receipt:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> {{ t('common.export') }}
        </el-button>
        <el-button
          type="danger"
          plain
          @click="handleDelete(selectionList.map((item) => item.id))"
          v-hasPermi="['erp:finance-receipt:delete']"
          :disabled="selectionList.length === 0"
        >
          <Icon icon="ep:delete" class="mr-5px" /> {{ t('common.delete') }}
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table
      v-loading="loading"
      :data="list"
      :stripe="true"
      :show-overflow-tooltip="true"
      @selection-change="handleSelectionChange"
     :table-layout="'auto'">
      <el-table-column width="30" :label="t('finance.receipt.select')" type="selection" />
      <el-table-column min-width="180" :label="t('finance.receipt.no')" align="center" prop="no" />
      <el-table-column :label="t('finance.receipt.customerName')" align="center" prop="customerName" />
      <el-table-column
        :label="t('finance.receipt.receiptTime')"
        align="center"
        prop="receiptTime"
        :formatter="dateFormatter2"
        width="120px" />
      <el-table-column :label="t('finance.receipt.creatorName')" align="center" prop="creatorName" />
      <el-table-column :label="t('finance.receipt.financeUserName')" align="center" prop="financeUserName" />
      <el-table-column :label="t('finance.receipt.accountName')" align="center" prop="accountName" />
      <el-table-column
        :label="t('finance.receipt.totalReceipt')"
        align="center"
        prop="totalPrice"
        :formatter="erpPriceTableColumnFormatter" />
      <el-table-column
        :label="t('finance.receipt.discountPrice')"
        align="center"
        prop="discountPrice"
        :formatter="erpPriceTableColumnFormatter" />
      <el-table-column
        :label="t('finance.receipt.receiptPrice')"
        align="center"
        prop="receiptPrice"
        :formatter="erpPriceTableColumnFormatter" />
      <el-table-column :label="t('common.status')" align="center" fixed="right" min-width="90" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.ERP_AUDIT_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column :label="t('common.operation')" align="center" fixed="right" min-width="220">
        <template #default="scope">
          <el-button
            link
            @click="openForm('detail', scope.row.id)"
            v-hasPermi="['erp:finance-receipt:query']"
          >
            {{ t('common.detail') }}
          </el-button>
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['erp:finance-receipt:update']"
            :disabled="scope.row.status === 20"
          >
            {{ t('common.edit') }}
          </el-button>
          <el-button
            link
            type="primary"
            @click="handleUpdateStatus(scope.row.id, 20)"
            v-hasPermi="['erp:finance-receipt:update-status']"
            v-if="scope.row.status === 10"
          >
            {{ t('finance.receipt.approve') }}
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleUpdateStatus(scope.row.id, 10)"
            v-hasPermi="['erp:finance-receipt:update-status']"
            v-else
          >
            {{ t('finance.receipt.unapprove') }}
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete([scope.row.id])"
            v-hasPermi="['erp:finance-receipt:delete']"
          >
            {{ t('common.delete') }}
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
  <FinanceReceiptForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter2 } from '@/utils/formatTime'
import download from '@/utils/download'
import { FinanceReceiptApi, FinanceReceiptVO } from '@/api/erp/finance/receipt'
import FinanceReceiptForm from './FinanceReceiptForm.vue'
import { UserVO } from '@/api/system/user'
import * as UserApi from '@/api/system/user'
import { erpPriceTableColumnFormatter } from '@/utils'
import { AccountApi, AccountVO } from '@/api/erp/finance/account'
import { CustomerApi, CustomerVO } from '@/api/erp/sale/customer'

/** ERP 收款单列表 */
defineOptions({ name: 'ErpPurchaseOrder' })

const message = useMessage() // 消息弹窗
const { t } = useI18n('erp') // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<FinanceReceiptVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  no: undefined,
  receiptTime: [],
  customerId: undefined,
  creator: undefined,
  financeUserId: undefined,
  accountId: undefined,
  status: undefined,
  remark: undefined,
  bizNo: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中
const customerList = ref<CustomerVO[]>([]) // 客户列表
const userList = ref<UserVO[]>([]) // 用户列表
const accountList = ref<AccountVO[]>([]) // 账户列表

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await FinanceReceiptApi.getFinanceReceiptPage(queryParams)
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
const handleDelete = async (ids: number[]) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await FinanceReceiptApi.deleteFinanceReceipt(ids)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
    selectionList.value = selectionList.value.filter((item) => !ids.includes(item.id))
  } catch {}
}

/** 审批/反审批操作 */
const handleUpdateStatus = async (id: number, status: number) => {
  try {
    // 审批的二次确认
    const action = status === 20 ? t('finance.receipt.approve') : t('finance.receipt.unapprove')
    await message.confirm(t('finance.receipt.approveConfirm', { action }))
    // 发起审批
    await FinanceReceiptApi.updateFinanceReceiptStatus(id, status)
    message.success(t('finance.receipt.approveSuccess', { action }))
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
    const data = await FinanceReceiptApi.exportFinanceReceipt(queryParams)
    download.excel(data, t('finance.receipt.exportFilename') + '.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 选中操作 */
const selectionList = ref<FinanceReceiptVO[]>([])
const handleSelectionChange = (rows: FinanceReceiptVO[]) => {
  selectionList.value = rows
}

/** 初始化 **/
onMounted(async () => {
  await getList()
  // 加载客户、用户、账户
  customerList.value = await CustomerApi.getCustomerSimpleList()
  userList.value = await UserApi.getSimpleUserList()
  accountList.value = await AccountApi.getAccountSimpleList()
})
// TODO 芋艿：可优化功能：列表界面，支持导入
// TODO 芋艿：可优化功能：详情界面，支持打印
</script>
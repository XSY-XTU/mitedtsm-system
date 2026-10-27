<template>
  <doc-alert title="【交易】分销返佣" url="https://doc.iocoder.cn/mall/trade-brokerage/" />

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
          <el-form-item :label="t('mall.trade.brokerage.userId')" prop="userId">
            <el-input
              v-model="queryParams.userId"
              :placeholder="t('mall.trade.brokerage.userIdPlaceholder')"
              clearable
              @keyup.enter="handleQuery"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('mall.trade.brokerage.withdrawBank')" prop="bankName">
            <el-select
              v-model="queryParams.bankName"
              :placeholder="t('mall.trade.brokerage.withdrawBankPlaceholder')"
              clearable
              class="!w-240px"
            >
              <el-option
                v-for="dict in getStrDictOptions(DICT_TYPE.BROKERAGE_BANK_NAME)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('common.status')" prop="status">
            <el-select v-model="queryParams.status" :placeholder="t('common.selectText')" clearable class="!w-240px">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.BROKERAGE_WITHDRAW_STATUS)"
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
          <el-form-item :label="t('mall.trade.brokerage.applyTime')" prop="createTime">
            <el-date-picker
              v-model="queryParams.createTime"
              value-format="YYYY-MM-DD HH:mm:ss"
              type="daterange"
              :start-placeholder="t('common.startTime')"
              :end-placeholder="t('common.endTime')"
              :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
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
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true" :table-layout="'auto'">
      <el-table-column :label="t('common.index')" align="left" prop="id" min-width="60px" />
      <el-table-column :label="t('mall.trade.brokerage.userInfo')" align="left" min-width="120px">
        <template #default="scope">
          <div>{{ t('common.index') }}：{{ scope.row.userId }}</div>
          <div>{{ t('mall.trade.brokerage.nickname') }}：{{ scope.row.userNickname }}</div>
        </template>
      </el-table-column>
      <el-table-column :label="t('mall.trade.brokerage.withdrawAmount')" align="left" prop="price" min-width="80px">
        <template #default="scope">
          <div>{{ t('mall.trade.brokerage.amount') }}：￥{{ fenToYuan(scope.row.price) }}</div>
          <div>{{ t('mall.trade.brokerage.fee') }}：￥{{ fenToYuan(scope.row.feePrice) }}</div>
        </template>
      </el-table-column>
      <el-table-column :label="t('mall.trade.brokerage.withdrawMethod')" align="left" prop="type" min-width="80px">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.BROKERAGE_WITHDRAW_TYPE" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column :label="t('mall.trade.brokerage.withdrawInfo')" align="left" min-width="120px">
        <template #default="scope">
          <div v-if="scope.row.type === BrokerageWithdrawTypeEnum.WALLET.type">-</div>
          <div v-else>
            <div v-if="scope.row.userAccount">{{ t('mall.trade.brokerage.account') }}：{{ scope.row.userAccount }}</div>
            <div v-if="scope.row.userName">{{ t('mall.trade.brokerage.realName') }}：{{ scope.row.userName }}</div>
          </div>
          <template v-if="scope.row.type === BrokerageWithdrawTypeEnum.BANK.type">
            <div>
              {{ t('mall.trade.brokerage.bankName') }}：
              <dict-tag :type="DICT_TYPE.BROKERAGE_BANK_NAME" :value="scope.row.bankName" />
            </div>
            <div>{{ t('mall.trade.brokerage.bankAddress') }}：{{ scope.row.bankAddress }}</div>
          </template>
          <div v-if="scope.row.qrCodeUrl" class="mt-2">
            <div>{{ t('mall.trade.brokerage.qrCode') }}：</div>
            <el-image
              :src="scope.row.qrCodeUrl"
              class="h-40px w-40px"
              :preview-src-list="[scope.row.qrCodeUrl]"
              preview-teleported
            />
          </div>
        </template>
      </el-table-column>
      <el-table-column
        :label="t('mall.trade.brokerage.applyTime')"
        align="left"
        prop="createTime"
        :formatter="dateFormatter"
        min-width="180"
      />
      <el-table-column :label="t('common.remark')" align="left" prop="remark" />
      <el-table-column :label="t('common.status')" align="left" prop="status" min-width="120px">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.BROKERAGE_WITHDRAW_STATUS" :value="scope.row.status" />
          <div v-if="scope.row.auditTime" class="text-xs">
            {{ t('common.createTime') }}：{{ formatDate(scope.row.auditTime) }}
          </div>
          <div v-if="scope.row.auditReason" class="text-xs">
            {{ t('mall.trade.brokerage.auditReason') }}：{{ scope.row.auditReason }}
          </div>
          <!-- 提现失败原因 -->
          <div v-if="scope.row.transferErrorMsg" class="text-xs text-red-500">
            {{ t('mall.trade.brokerage.transferFailReason') }}：{{ scope.row.transferErrorMsg }}
          </div>
        </template>
      </el-table-column>
      <el-table-column :label="t('common.operation')" align="left" min-width="150" fixed="right">
        <template #default="scope">
          <template
            v-if="
              scope.row.status === BrokerageWithdrawStatusEnum.AUDITING.status &&
              !scope.row.payTransferId
            "
          >
            <el-button
              link
              type="primary"
              @click="handleApprove(scope.row.id)"
              v-hasPermi="['trade:brokerage-withdraw:audit']"
            >
              {{ t('mall.trade.brokerage.approve') }}
            </el-button>
            <el-button
              link
              type="danger"
              @click="openForm(scope.row.id)"
              v-hasPermi="['trade:brokerage-withdraw:audit']"
            >
              {{ t('mall.trade.brokerage.reject') }}
            </el-button>
          </template>
          <template v-if="scope.row.status === BrokerageWithdrawStatusEnum.WITHDRAW_FAIL.status">
            <el-button
              link
              type="warning"
              @click="handleRetryTransfer(scope.row.id)"
              v-hasPermi="['trade:brokerage-withdraw:audit']"
            >
              {{ t('mall.trade.brokerage.retryTransfer') }}
            </el-button>
          </template>
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
  <BrokerageWithdrawRejectForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions, getStrDictOptions } from '@/utils/dict'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import * as BrokerageWithdrawApi from '@/api/mall/trade/brokerage/withdraw'
import BrokerageWithdrawRejectForm from './BrokerageWithdrawRejectForm.vue'
import { BrokerageWithdrawStatusEnum, BrokerageWithdrawTypeEnum } from '@/utils/constants'
import { fenToYuan } from '@/utils'

defineOptions({ name: 'BrokerageWithdraw' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userId: null,
  type: undefined,
  userName: null,
  userAccount: null,
  bankName: undefined,
  status: undefined,
  auditReason: null,
  auditTime: [],
  remark: null,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await BrokerageWithdrawApi.getBrokerageWithdrawPage(queryParams)
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
const openForm = (id: number) => {
  formRef.value.open(id)
}

/** 审核通过 */
const handleApprove = async (id: number) => {
  try {
    loading.value = true
    await message.confirm(t('mall.trade.brokerage.approveConfirm'))
    await BrokerageWithdrawApi.approveBrokerageWithdraw(id)
    await message.success(t('common.success'))
    await getList()
  } finally {
    loading.value = false
  }
}

/** 重新转账 */
const handleRetryTransfer = async (id: number) => {
  try {
    loading.value = true
    await message.confirm(t('mall.trade.brokerage.retryTransferConfirm'))
    await BrokerageWithdrawApi.approveBrokerageWithdraw(id)
    await message.success(t('common.success'))
    await getList()
  } finally {
    loading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

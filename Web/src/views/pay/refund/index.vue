<template>
  <doc-alert title="支付宝、微信退款接入" url="https://doc.iocoder.cn/pay/refund-demo/" />

  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form
      class="-mb-15px"
      label-width="auto"
      :model="queryParams"
      ref="queryFormRef"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('order.appId')" prop="appId">
        <el-select
          v-model="queryParams.appId"
          clearable
          :placeholder="t('common.selectText')"
          class="!w-240px"
        >
          <el-option v-for="item in appList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('refund.channelCode')" prop="channelCode">
        <el-select
          v-model="queryParams.channelCode"
          :placeholder="t('common.selectText')"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.PAY_CHANNEL_CODE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('order.merchantOrderId')" prop="merchantOrderId">
        <el-input
          v-model="queryParams.merchantOrderId"
          :placeholder="t('order.merchantOrderIdPlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('refund.merchantRefundId')" prop="merchantRefundId">
        <el-input
          v-model="queryParams.merchantRefundId"
          :placeholder="t('refund.merchantRefundIdPlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('order.channelOrderNo')" prop="channelOrderNo">
        <el-input
          v-model="queryParams.channelOrderNo"
          :placeholder="t('refund.channelOrderNoPlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('refund.channelRefundNo')" prop="channelRefundNo">
        <el-input
          v-model="queryParams.channelRefundNo"
          :placeholder="t('refund.channelRefundNoPlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('refund.status')" prop="status">
        <el-select
          v-model="queryParams.status"
          :placeholder="t('refund.statusPlaceholder')"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.PAY_REFUND_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('order.createTime')" prop="createTime">
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
        <el-button @click="handleQuery"> <Icon icon="ep:search" class="mr-5px" /> {{ t('common.search') }} </el-button>
        <el-button @click="resetQuery"> <Icon icon="ep:refresh" class="mr-5px" /> {{ t('common.reset') }} </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['system:tenant:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> {{ t('action.export') }}
        </el-button>
      </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :table-layout="'auto'">
      <el-table-column :label="t('refund.id')" align="center" prop="id" />
      <el-table-column
        :label="t('refund.createTime')"
        align="center"
        prop="createTime"
        min-width="170"
        :formatter="dateFormatter"
      />
      <el-table-column :label="t('refund.payPrice')" align="center" prop="payPrice" min-width="100">
        <template #default="scope">
          ￥{{ parseFloat(scope.row.payPrice / 100).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column :label="t('refund.price')" align="center" prop="refundPrice" min-width="100">
        <template #default="scope">
          ￥{{ parseFloat(scope.row.refundPrice / 100).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column :label="t('refund.no')" align="left" min-width="300">
        <template #default="scope">
          <p class="order-font">
            <el-tag size="small">{{ t('refund.merchantTag') }}</el-tag> {{ scope.row.merchantRefundId }}
          </p>
          <p class="order-font">
            <el-tag size="small" type="warning">{{ t('refund.refundTag') }}</el-tag> {{ scope.row.no }}
          </p>
          <p class="order-font" v-if="scope.row.channelRefundNo">
            <el-tag size="small" type="success">{{ t('refund.channelTag') }}</el-tag> {{ scope.row.channelRefundNo }}
          </p>
        </template>
      </el-table-column>
      <el-table-column :label="t('order.no')" align="left" min-width="300">
        <template #default="scope">
          <p class="order-font">
            <el-tag size="small">{{ t('order.merchantTag') }}</el-tag> {{ scope.row.merchantOrderId }}
          </p>
          <p class="order-font">
            <el-tag size="small" type="success">{{ t('order.channelTag') }}</el-tag> {{ scope.row.channelOrderNo }}
          </p>
        </template>
      </el-table-column>
      <el-table-column :label="t('refund.status')" align="center" prop="status" min-width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PAY_REFUND_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column :label="t('refund.channelCode')" align="center" min-width="140">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PAY_CHANNEL_CODE" :value="scope.row.channelCode" />
        </template>
      </el-table-column>
      <el-table-column
        :label="t('refund.successTime')"
        align="center"
        prop="successTime"
        min-width="180"
        :formatter="dateFormatter"
      />
      <el-table-column :label="t('order.appName')" align="center" prop="successTime" min-width="100">
        <template #default="scope">
          <span>{{ scope.row.appName }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="t('common.action')" align="center" fixed="right" min-width="150">
        <template #default="scope">
          <el-button
            type="primary"
            link
            @click="openDetail(scope.row.id)"
            v-hasPermi="['pay:order:query']"
          >
            {{ t('common.detail') }}
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

  <!-- 表单弹窗：预览 -->
  <RefundDetail ref="detailRef" @success="getList" />
</template>
<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions, getStrDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as RefundApi from '@/api/pay/refund'
import * as AppApi from '@/api/pay/app'
import RefundDetail from './RefundDetail.vue'
import download from '@/utils/download'
import { useI18n } from '@/hooks/web/useI18n'

const { t } = useI18n('pay')

defineOptions({ name: 'PayRefund' })

const message = useMessage() // 消息弹窗

const loading = ref(false) // 列表遮罩层
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  merchantId: undefined,
  appId: undefined,
  channelCode: undefined,
  merchantOrderId: undefined,
  merchantRefundId: undefined,
  status: undefined,
  payPrice: undefined,
  refundPrice: undefined,
  channelOrderNo: undefined,
  channelRefundNo: undefined,
  createTime: [],
  successTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出等待
const appList = ref([]) // 支付应用列表集合

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await RefundApi.getRefundPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields()
  handleQuery()
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await RefundApi.exportRefund(queryParams)
    download.excel(data, 'pay-refund.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 预览详情 */
const detailRef = ref()
const openDetail = (id: number) => {
  detailRef.value.open(id)
}

/** 初始化 **/
onMounted(async () => {
  await getList()
  appList.value = await AppApi.getAppList()
})
</script>
<style>
.order-font {
  padding: 2px 0;
  font-size: 12px;
}
</style>
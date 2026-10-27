<template>
  <doc-alert title="支付宝支付接入" url="https://doc.iocoder.cn/pay/alipay-pay-demo/" />
  <doc-alert title="微信公众号支付接入" url="https://doc.iocoder.cn/pay/wx-pub-pay-demo/" />
  <doc-alert title="微信小程序支付接入" url="https://doc.iocoder.cn/pay/wx-lite-pay-demo/" />

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
          clearable
          v-model="queryParams.appId"
          :placeholder="t('common.selectText')"
          class="!w-240px"
        >
          <el-option v-for="item in appList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('channel.title')" prop="channelCode">
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
          <el-form-item :label="t('order.no')" prop="no">
        <el-input
          v-model="queryParams.no"
          :placeholder="t('order.noPlaceholder')"
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
          :placeholder="t('order.channelOrderNoPlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('order.status')" prop="status">
        <el-select
          v-model="queryParams.status"
          :placeholder="t('order.statusPlaceholder')"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.PAY_ORDER_STATUS)"
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
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> {{ t('common.search') }}</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> {{ t('common.reset') }}</el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['pay:order:export']"
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
      <el-table-column :label="t('order.id')" align="center" prop="id" min-width="80" />
      <el-table-column
        :label="t('order.createTime')"
        align="center"
        prop="createTime"
        min-width="180"
        :formatter="dateFormatter"
      />
      <el-table-column :label="t('order.price')" align="center" prop="price" min-width="100">
        <template #default="scope"> ￥{{ parseFloat(scope.row.price / 100).toFixed(2) }} </template>
      </el-table-column>
      <el-table-column :label="t('order.refundPrice')" align="center" prop="refundPrice" min-width="100">
        <template #default="scope">
          ￥{{ parseFloat(scope.row.refundPrice / 100).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column :label="t('order.feePrice')" align="center" prop="channelFeePrice" min-width="100">
        <template #default="scope">
          ￥{{ parseFloat(scope.row.channelFeePrice / 100).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column :label="t('order.no')" align="left" min-width="300">
        <template #default="scope">
          <p class="order-font">
            <el-tag size="small"> {{ t('order.merchantTag') }}</el-tag> {{ scope.row.merchantOrderId }}
          </p>
          <p class="order-font" v-if="scope.row.no">
            <el-tag size="small" type="warning">{{ t('order.payTag') }}</el-tag> {{ scope.row.no }}
          </p>
          <p class="order-font" v-if="scope.row.channelOrderNo">
            <el-tag size="small" type="success">{{ t('order.channelTag') }}</el-tag> {{ scope.row.channelOrderNo }}
          </p>
        </template>
      </el-table-column>
      <el-table-column :label="t('order.status')" align="center" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PAY_ORDER_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column :label="t('channel.code')" align="center" prop="channelCode" min-width="140">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PAY_CHANNEL_CODE" :value="scope.row.channelCode" />
        </template>
      </el-table-column>
      <el-table-column
        :label="t('order.successTime')"
        align="center"
        prop="successTime"
        min-width="180"
        :formatter="dateFormatter"
      />
      <el-table-column :label="t('order.appName')" align="center" prop="appName" min-width="100" />
      <el-table-column :label="t('order.subject')" align="center" prop="subject" min-width="180" />
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
  <OrderDetail ref="detailRef" @success="getList" />
</template>
<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions, getStrDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as OrderApi from '@/api/pay/order'
import OrderDetail from './OrderDetail.vue'
import download from '@/utils/download'
import { getAppList } from '@/api/pay/app'
import { useI18n } from '@/hooks/web/useI18n'

const { t } = useI18n('pay')

defineOptions({ name: 'PayOrder' })

const message = useMessage() // 消息弹窗

const loading = ref(false) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  appId: null,
  channelCode: null,
  merchantOrderId: null,
  channelOrderNo: null,
  no: null,
  status: null,
  createTime: []
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
    const data = await OrderApi.getOrderPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await OrderApi.exportOrder(queryParams)
    download.excel(data, 'pay-order.xls')
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
  appList.value = await getAppList()
})
</script>
<style>
.order-font {
  padding: 2px 0;
  font-size: 12px;
}
</style>
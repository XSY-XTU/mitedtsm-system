<template>
  <doc-alert title="【交易】交易订单" url="https://doc.iocoder.cn/mall/trade-order/" />
  <doc-alert title="【交易】购物车" url="https://doc.iocoder.cn/mall/trade-cart/" />

  <!-- 搜索 -->
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      class="-mb-15px"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('common.createTime')" prop="createTime">
            <el-date-picker
              v-model="queryParams.createTime"
              :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
              class="!w-280px"
              :end-placeholder="t('common.customTime')"
              :start-placeholder="t('common.customTime')"
              type="daterange"
              value-format="YYYY-MM-DD HH:mm:ss"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('order.pickUpStore')" prop="pickUpStoreIds">
            <el-select
              v-model="queryParams.pickUpStoreIds"
              class="!w-280px"
              :placeholder="t('common.all')"
              @change="handleQuery"
            >
              <el-option
                v-for="item in pickUpStoreList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('order.aggregateSearch')">
            <el-input
              v-show="true"
              v-model="queryParams[queryType.queryParam]"
              class="!w-280px"
              clearable
              :placeholder="t('common.inputText')"
              :type="queryType.queryParam === 'userId' ? 'number' : 'text'"
            >
              <template #prepend>
                <el-select
                  v-model="queryType.queryParam"
                  class="!w-110px"
                  :placeholder="t('common.all')"
                  @change="inputChangeSelect"
                >
                  <el-option
                    v-for="dict in dynamicSearchList"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  />
                </el-select>
              </template>
            </el-input>
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
              @click="handlePickup"
              type="success"
              plain
              v-hasPermi="['trade:order:pick-up']"
              :disabled="isUse"
            >
              <Icon class="mr-5px" icon="ep:check" />
              {{ t('order.pickUp') }}
            </el-button>
            <el-button type="primary" @click="connectToSerialPort" :disabled="serialPort || isUse">
              {{ t('order.connectScanner') }}
            </el-button>
            <el-button type="danger" @click="cutPort" :disabled="!serialPort || isUse">
              {{ t('order.disconnectScanner') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 统计卡片 -->
  <el-row :gutter="16" class="summary">
    <el-col :sm="6" :xs="12" v-loading="loading">
      <SummaryCard
        :title="t('mall.trade.order.orderCount')"
        icon="icon-park-outline:transaction-order"
        icon-color="bg-blue-100"
        icon-bg-color="text-blue-500"
        :value="summary?.orderCount || 0"
      />
    </el-col>
    <el-col :sm="6" :xs="12" v-loading="loading">
      <SummaryCard
        :title="t('mall.trade.order.orderAmount')"
        icon="streamline:money-cash-file-dollar-common-money-currency-cash-file"
        icon-color="bg-purple-100"
        icon-bg-color="text-purple-500"
        prefix="¥"
        :decimals="2"
        :value="fenToYuan(summary?.orderPayPrice || 0)"
      />
    </el-col>
    <el-col :sm="6" :xs="12" v-loading="loading">
      <SummaryCard
        :title="t('mall.trade.order.refundCount')"
        icon="heroicons:receipt-refund"
        icon-color="bg-yellow-100"
        icon-bg-color="text-yellow-500"
        :value="summary?.afterSaleCount || 0"
      />
    </el-col>
    <el-col :sm="6" :xs="12" v-loading="loading">
      <SummaryCard
        :title="t('mall.trade.order.refundAmount')"
        icon="ri:refund-2-line"
        icon-color="bg-green-100"
        icon-bg-color="text-green-500"
        prefix="¥"
        :decimals="2"
        :value="fenToYuan(summary?.afterSalePrice || 0)"
      />
    </el-col>
  </el-row>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :table-layout="'auto'">
      <el-table-column :label="t('order.no')" align="center" prop="no" min-width="180" />
      <el-table-column :label="t('order.productInfo')" align="center" prop="user.nickname" min-width="80" />
      <el-table-column
        :label="t('order.promoterUser')"
        align="center"
        prop="brokerageUser.nickname"
        min-width="100"
      />
      <el-table-column :label="t('order.productInfo')" align="center" prop="spuName" min-width="300">
        <template #default="{ row }">
          <div class="flex items-center" v-for="item in row.items" :key="item.id">
            <el-image
              :src="item.picUrl"
              class="mr-10px h-30px w-30px flex-shrink-0"
              :preview-src-list="[item.picUrl]"
              preview-teleported
            />
            <span class="mr-10px">{{ item.spuName }}</span>
            <div class="flex flex-col flex-wrap gap-1">
              <el-tag
                v-for="property in item.properties"
                :key="property.propertyId"
                class="mr-10px"
              >
                {{ property.propertyName }}: {{ property.valueName }}
              </el-tag>
              <span>{{ floatToFixed2(item.price) }} {{ t('order.yuan') }} x {{ item.count }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        :label="t('order.payPrice') + '(' + t('order.yuan') + ')'"
        align="center"
        prop="payPrice"
        min-width="110"
        :formatter="fenToYuanFormat"
      />
      <el-table-column :label="t('order.verifier')" align="center" prop="storeStaffName" min-width="70" />
      <el-table-column :label="t('order.verifyStore')" align="center" prop="pickUpStoreId" min-width="80">
        <template #default="{ row }">
          {{ pickUpStoreList.find((p) => p.id === row.pickUpStoreId)?.name }}
        </template>
      </el-table-column>
      <el-table-column :label="t('order.paymentStatus')" align="center" prop="payStatus" min-width="80">
        <template #default="{ row }">
          <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="row.payStatus || false" />
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('order.status')" prop="status" min-width="120">
        <template #default="{ row }">
          <dict-tag :type="DICT_TYPE.TRADE_ORDER_STATUS" :value="row.status" />
        </template>
      </el-table-column>
      <el-table-column
        :label="t('order.createTime')"
        align="center"
        prop="createTime"
        min-width="170"
        :formatter="dateFormatter"
      />
    </el-table>
    <!-- 分页 -->
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 各种操作的弹窗 -->
  <OrderPickUpForm ref="pickUpForm" @success="getList" />
</template>

<script lang="ts" setup>
import type { FormInstance } from 'element-plus'
import * as TradeOrderApi from '@/api/mall/trade/order'
import * as PickUpStoreApi from '@/api/mall/trade/delivery/pickUpStore'
import { DICT_TYPE } from '@/utils/dict'
import { fenToYuan, floatToFixed2 } from '@/utils'
import { fenToYuanFormat } from '@/utils/formatter'
import SummaryCard from '@/components/SummaryCard/index.vue'
import { dateFormatter } from '@/utils/formatTime'
import { DeliveryTypeEnum } from '@/utils/constants'
import { TradeOrderSummaryRespVO } from '@/api/mall/trade/order'
import { DeliveryPickUpStoreVO } from '@/api/mall/trade/delivery/pickUpStore'
import OrderPickUpForm from '@/views/mall/trade/order/form/OrderPickUpForm.vue'
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/modules/user'
const message = useMessage() // 消息弹窗
const { t } = useI18n('mall.trade') // 国际化

const port = ref('')
const ports = ref([])
const reader = ref('')

defineOptions({ name: 'PickUpOrder' })

const loading = ref(true) // 列表的加载中
const total = ref(2) // 列表的总页数
const list = ref<TradeOrderApi.OrderVO[]>([]) // 列表的数据
const queryFormRef = ref<FormInstance>() // 搜索的表单
const INIT_QUERY_PARAMS = {
  // 页数
  pageNo: 1,
  // 每页显示数量
  pageSize: 10,
  // 创建时间
  createTime: undefined,
  // 配送方式
  deliveryType: DeliveryTypeEnum.PICK_UP.type,
  // 自提门店
  pickUpStoreIds: -1
} // 初始表单参数

const queryParams = ref({ ...INIT_QUERY_PARAMS }) // 表单搜索
const queryType = reactive({ queryParam: 'no' }) // 订单搜索类型 queryParam
const summary = ref<TradeOrderSummaryRespVO>() // 订单统计数据

const serialPort = ref(false) // 是否连接扫码枪
const isUse = ref(true) // 是否可核销

// 订单聚合搜索 select 类型配置（动态搜索）
const dynamicSearchList = ref([
  { value: 'no', label: t('mall.trade.order.no') },
  { value: 'userId', label: t('mall.trade.order.userUid') },
  { value: 'userNickname', label: t('mall.trade.order.userNickname') },
  { value: 'userMobile', label: t('mall.trade.order.userPhone') }
])
/**
 * 聚合搜索切换查询对象时触发
 * @param val
 */
const inputChangeSelect = (val: string) => {
  dynamicSearchList.value
    .filter((item) => item.value !== val)
    ?.forEach((item) => {
      // 清除集合搜索无用属性
      if (queryParams.value.hasOwnProperty(item.value)) {
        delete queryParams.value[item.value]
      }
    })
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    // 统计
    summary.value = await TradeOrderApi.getOrderSummary(unref(queryParams))
    // 分页
    const data = await TradeOrderApi.getOrderPage(unref(queryParams))
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = async () => {
  queryParams.value.pageNo = 1
  await getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields()
  queryParams.value = { ...INIT_QUERY_PARAMS }
  if (pickUpStoreList.value.length > 0) {
    queryParams.value.pickUpStoreIds = pickUpStoreList.value[0].id
  }
  handleQuery()
}

/** 自提门店精简列表 */
const pickUpStoreList = ref<DeliveryPickUpStoreVO[]>([])
const getPickUpStoreList = async () => {
  pickUpStoreList.value = await PickUpStoreApi.getSimpleDeliveryPickUpStoreList()
  // 移除自己无法核销的门店
  const userId = useUserStore().getUser.id
  pickUpStoreList.value = pickUpStoreList.value.filter((item) =>
    item.verifyUserIds?.includes(userId)
  )
}

/** 显示核销表单 */
const pickUpForm = ref()
const handlePickup = () => {
  pickUpForm.value.open()
}

/** 连接扫码枪 */
const connectToSerialPort = async () => {
  try {
    // 判断浏览器支持串口通信
    if (
      'serial' in navigator &&
      navigator.serial != null &&
      typeof navigator.serial === 'object' &&
      'requestPort' in navigator.serial
    ) {
      // 提示用户选择一个串口
      port.value = await navigator.serial.requestPort()
    } else {
      message.error(t('mall.trade.order.scannerNotSupported'))
      return
    }

    // 获取用户之前授予该网站访问权限的所有串口
    ports.value = await navigator.serial.getPorts()

    // console.log(port.value, ports.value);
    // console.log(port.value)
    // 等待串口打开
    await port.value.open({ baudRate: 9600, dataBits: 8, stopBits: 2 })

    // console.log(typeof port.value);
    message.success(t('mall.trade.order.scannerConnectSuccess'))
    serialPort.value = true
    // readData(port.value);
    readData()
  } catch (error) {
    // 处理连接串口出错的情况
    console.log('Error connecting to serial port:', error)
  }
}

/** 监听扫码枪输入 */
const readData = async () => {
  reader.value = port.value.readable.getReader()
  let data = '' //扫码数据
  // 监听来自串口的数据
  while (true) {
    const { value, done } = await reader.value.read()
    if (done) {
      // 允许稍后关闭串口
      reader.value.releaseLock()
      break
    }
    // 获取发送的数据
    const serialData = new TextDecoder().decode(value)
    data = `${data}${serialData}`
    if (serialData.includes('\r')) {
      //读取结束
      let codeData = data.replace('\r', '')
      data = '' //清空下次读取不会叠加
      console.log(`二维码数据：${codeData}`)
      //处理拿到数据逻辑
      pickUpForm.value.open(codeData)
    }
  }
}

/** 断开扫码枪 */
const cutPort = async () => {
  if (port.value !== '') {
    await reader.value.cancel()
    await port.value.close()
    port.value = ''
    console.log('断开扫码枪连接')
    message.success(t('mall.trade.order.scannerDisconnectSuccess'))
    serialPort.value = false
  } else {
    message.warning(t('mall.trade.order.scannerNotConnected'))
  }
}

/** 初始化 **/
onMounted(async () => {
  await getPickUpStoreList()
  if (pickUpStoreList.value.length === 0) {
    message.error(t('mall.trade.order.noPickUpStore'))
    loading.value = false
    isUse.value = true
    return
  }

  // 查询
  queryParams.value.pickUpStoreIds = pickUpStoreList.value[0].id
  isUse.value = false
  await getList()
})
</script>
<style lang="scss" scoped>
:deep(.order-table-col > .cell) {
  padding: 0;
}

.summary {
  .el-col {
    margin-bottom: 1rem;
  }
}
</style>

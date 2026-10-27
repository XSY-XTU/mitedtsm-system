<template>
  <el-table-column class-name="order-table-col">
    <template #header>
      <div class="flex items-center" style="width: 100%">
        <div :style="{ width: orderTableHeadWidthList[0] + 'px' }" class="flex justify-center">
          {{ t('order.productInfo') }}
        </div>
        <div :style="{ width: orderTableHeadWidthList[1] + 'px' }" class="flex justify-center">
          {{ t('order.priceCount') }}
        </div>
        <div :style="{ width: orderTableHeadWidthList[2] + 'px' }" class="flex justify-center">
          {{ t('order.afterSaleStatus') }}
        </div>
        <div :style="{ width: orderTableHeadWidthList[3] + 'px' }" class="flex justify-center">
          {{ t('order.payPrice') }}({{ t('order.yuan') }})
        </div>
        <div :style="{ width: orderTableHeadWidthList[4] + 'px' }" class="flex justify-center">
          {{ t('order.buyerReceiver') }}
        </div>
        <div :style="{ width: orderTableHeadWidthList[5] + 'px' }" class="flex justify-center">
          {{ t('order.deliveryType') }}
        </div>
        <div :style="{ width: orderTableHeadWidthList[6] + 'px' }" class="flex justify-center">
          {{ t('order.status') }}
        </div>
        <div :style="{ width: orderTableHeadWidthList[7] + 'px' }" class="flex justify-center">
          {{ t('common.operation') }}
        </div>
      </div>
    </template>
    <template #default="scope">
      <el-table
        :ref="setOrderTableRef"
        :border="true"
        :data="scope.row.items"
        :header-cell-style="headerStyle"
        :span-method="spanMethod"
        style="width: 100%"
      >
        <el-table-column min-width="300" prop="spuName">
          <template #header>
            <div
              class="h-[35px] flex items-center -mx-[10px] px-[20px]"
              style="background-color: var(--app-content-bg-color)"
            >
              <span class="mr-20px">{{ t('order.no') }}：{{ scope.row.no }} </span>
              <span class="mr-20px">{{ t('order.createTime') }}：{{ formatDate(scope.row.createTime) }}</span>
              <span>{{ t('order.orderSource') }}：</span>
              <dict-tag :type="DICT_TYPE.TERMINAL" :value="scope.row.terminal" class="mr-20px"  fixed="right" />
              <span>{{ t('order.paymentMethod') }}：</span>
              <dict-tag
                v-if="scope.row.payChannelCode"
                :type="DICT_TYPE.PAY_CHANNEL_CODE"
                :value="scope.row.payChannelCode"
                class="mr-20px"
              />
              <span v-else class="mr-20px">{{ t('order.unpaid') }}</span>
              <span v-if="scope.row.payTime" class="mr-20px">
                {{ t('order.payTime') }}：{{ formatDate(scope.row.payTime) }}
              </span>
              <span>{{ t('order.orderType') }}：</span>
              <dict-tag :type="DICT_TYPE.TRADE_ORDER_TYPE" :value="scope.row.type" />
            </div>
          </template>
          <template #default="{ row }">
            <div class="flex flex-wrap">
              <div class="mb-[10px] mr-[10px] flex items-start">
                <div class="mr-[10px]">
                  <el-image
                    :src="row.picUrl"
                    class="!h-[45px] !w-[45px]"
                    fit="contain"
                    @click="imagePreview(row.picUrl)"
                  >
                    <template #error>
                      <div class="image-slot">
                        <icon icon="ep:picture" />
                      </div>
                    </template>
                  </el-image>
                </div>
                <ElTooltip :content="row.spuName" placement="top">
                  <span class="overflow-ellipsis max-h-[45px] overflow-hidden">
                    {{ row.spuName }}
                  </span>
                </ElTooltip>
              </div>
              <el-tag
                v-for="property in row.properties"
                :key="property.propertyId"
                class="mb-[10px] mr-[10px]"
              >
                {{ property.propertyName }}: {{ property.valueName }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column :label="t('order.productOriginalPrice') + '*' + t('order.count')" prop="price" min-width="150">
          <template #default="{ row }">
            {{ floatToFixed2(row.price) }} {{ t('order.yuan') }} / {{ row.count }}
          </template>
        </el-table-column>
        <el-table-column :label="t('order.afterSaleStatus')" prop="afterSaleStatus" min-width="120">
          <template #default="{ row }">
            <dict-tag
              :type="DICT_TYPE.TRADE_ORDER_ITEM_AFTER_SALE_STATUS"
              :value="row.afterSaleStatus"
            />
          </template>
        </el-table-column>
        <el-table-column align="center" :label="t('order.actualPayment')" min-width="120" prop="payPrice">
          <template #default>
            {{ floatToFixed2(scope.row.payPrice) + t('order.yuan') }}
          </template>
        </el-table-column>
        <el-table-column :label="t('order.buyerReceiver')" min-width="160">
          <template #default>
            <!-- 快递发货  -->
            <div
              v-if="scope.row.deliveryType === DeliveryTypeEnum.EXPRESS.type"
              class="flex flex-col"
            >
              <span>{{ t('order.buyer') }}：{{ scope.row.user?.nickname }}</span>
              <span>
                {{ t('order.consignee') }}：{{ scope.row.receiverName }} {{ scope.row.receiverMobile }}
                {{ scope.row.receiverAreaName }} {{ scope.row.receiverDetailAddress }}
              </span>
            </div>
            <!-- 自提  -->
            <div
              v-if="scope.row.deliveryType === DeliveryTypeEnum.PICK_UP.type"
              class="flex flex-col"
            >
              <span>
                {{ t('order.storeName') }}：
                {{ pickUpStoreList.find((p) => p.id === scope.row.pickUpStoreId)?.name }}
              </span>
              <span>
                {{ t('order.storePhone') }}：
                {{ pickUpStoreList.find((p) => p.id === scope.row.pickUpStoreId)?.phone }}
              </span>
              <span>
                {{ t('order.storeAddress') }}:
                {{ pickUpStoreList.find((p) => p.id === scope.row.pickUpStoreId)?.detailAddress }}
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column align="center" :label="t('order.deliveryType')" min-width="120">
          <template #default>
            <dict-tag :type="DICT_TYPE.TRADE_DELIVERY_TYPE" :value="scope.row.deliveryType" />
          </template>
        </el-table-column>
        <el-table-column align="center" :label="t('order.status')" min-width="120">
          <template #default>
            <dict-tag :type="DICT_TYPE.TRADE_ORDER_STATUS" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column align="center" fixed="right" :label="t('common.operation')" min-width="160">
          <template #default>
            <slot :row="scope.row"></slot>
          </template>
        </el-table-column>
      </el-table>
    </template>
  </el-table-column>
</template>
<script lang="ts" setup>
import { nextTick, onMounted, onUnmounted, watch } from 'vue'
import { DICT_TYPE } from '@/utils/dict'
import { DeliveryTypeEnum } from '@/utils/constants'
import { formatDate } from '@/utils/formatTime'
import { floatToFixed2 } from '@/utils'
import * as TradeOrderApi from '@/api/mall/trade/order'
import { OrderVO } from '@/api/mall/trade/order'
import type { TableColumnCtx, TableInstance } from 'element-plus'
import { createImageViewer } from '@/components/ImageViewer'
import type { DeliveryPickUpStoreVO } from '@/api/mall/trade/delivery/pickUpStore'

defineOptions({ name: 'OrderTableColumn' })

const { t } = useI18n('mall.trade') // 国际化

const props = defineProps<{
  list: OrderVO[]
  pickUpStoreList: DeliveryPickUpStoreVO[]
}>()

const headerStyle = ({ row, columnIndex }: any) => {
  // 表头第一行第一列占 8
  if (columnIndex === 0) {
    row[columnIndex].colSpan = 8
  } else {
    // 其余的不要
    row[columnIndex].colSpan = 0
    return {
      display: 'none'
    }
  }
}

interface SpanMethodProps {
  row: TradeOrderApi.OrderItemRespVO
  column: TableColumnCtx<TradeOrderApi.OrderItemRespVO>
  rowIndex: number
  columnIndex: number
}

type spanMethodResp = number[] | { rowspan: number; colspan: number } | undefined
const spanMethod = ({ row, rowIndex, columnIndex }: SpanMethodProps): spanMethodResp => {
  const len = props.list.find(
    (order) => order.items?.findIndex((item) => item.id === row.id) !== -1
  )?.items?.length
  // 要合并的列，从零开始
  const colIndex = [3, 4, 5, 6, 7]
  if (colIndex.includes(columnIndex)) {
    // 除了第一行其余的不要
    if (rowIndex !== 0) {
      return {
        rowspan: 0,
        colspan: 0
      }
    }
    // 动态合并行
    return {
      rowspan: len!,
      colspan: 1
    }
  }
}

const orderTableHeadWidthList = ref([300, 150, 120, 120, 160, 120, 120, 160]) // 头部 col 宽度初始化
let isFirstTable = false // 标记是否已处理第一个表格
let firstTableInstance: TableInstance | null = null

/** 解决 ref 在 v-for 中的获取问题*/
const setOrderTableRef = async (el: TableInstance) => {
  if (!el) return
  // 只处理第一个表格实例
  if (!isFirstTable) {
    isFirstTable = true
    firstTableInstance = el
    // 使用 nextTick 确保 DOM 已完全渲染
    await nextTick()
    tableHeadWidthAuto(el)
  }
}

/** 头部宽度自适应 */
const tableHeadWidthAuto = (el: TableInstance) => {
  if (!el) return
  const columns = el.store.states.columns.value
  if (columns.length === 0) {
    return
  }
  columns.forEach((col: TableColumnCtx<TableInstance>, index: number) => {
    if (col.realWidth) {
      orderTableHeadWidthList.value[index] = col.realWidth
    }
  })
}

/** 监听窗口大小变化，重新计算表头宽度 */
const handleResize = async () => {
  if (firstTableInstance) {
    await nextTick()
    tableHeadWidthAuto(firstTableInstance!)
  }
}

/** 监听列表数据变化，重新计算表头宽度 */
watch(
  () => props.list,
  async () => {
    // 数据变化后，等待 DOM 更新完成再重新计算宽度
    await nextTick()
    if (firstTableInstance) {
      // 延迟一小段时间，确保表格已完全渲染
      await new Promise((resolve) => setTimeout(resolve, 100))
      tableHeadWidthAuto(firstTableInstance!)
    }
  },
  { deep: true }
)

onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})

/** 商品图预览 */
const imagePreview = (imgUrl: string) => {
  createImageViewer({
    urlList: [imgUrl]
  })
}
</script>
<style lang="scss" scoped>
:deep(.order-table-col > .cell) {
  padding: 0;
}
</style>

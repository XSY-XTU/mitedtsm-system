<template>
  <ContentWrap>
    <!-- 订单信息 -->
    <el-descriptions :title="t('mall.trade.order.orderInfo')">
      <el-descriptions-item :label="t('mall.trade.order.no') + ': '">{{ formData.no }}</el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.order.buyer') + ': '">{{ formData?.user?.nickname }}</el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.order.orderType') + ': '">
        <dict-tag :type="DICT_TYPE.TRADE_ORDER_TYPE" :value="formData.type!" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.order.orderSource') + ': '">
        <dict-tag :type="DICT_TYPE.TERMINAL" :value="formData.terminal!" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.order.buyerMessage') + ': '">{{ formData.userRemark }}</el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.order.merchantRemark') + ': '">{{ formData.remark }}</el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.order.payOrderId') + ': '">{{ formData.payOrderId }}</el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.order.paymentMethod') + ': '">
        <dict-tag :type="DICT_TYPE.PAY_CHANNEL_CODE" :value="formData.payChannelCode!" />
      </el-descriptions-item>
      <el-descriptions-item v-if="formData.brokerageUser" :label="t('mall.trade.order.promoterUser') + ': '">
        {{ formData.brokerageUser?.nickname }}
      </el-descriptions-item>
    </el-descriptions>

    <!-- 订单状�?-->
    <el-descriptions :column="1" :title="t('mall.trade.order.orderStatusInfo')">
      <el-descriptions-item :label="t('mall.trade.order.status') + ': '">
        <dict-tag :type="DICT_TYPE.TRADE_ORDER_STATUS" :value="formData.status!" />
      </el-descriptions-item>
      <el-descriptions-item v-hasPermi="['trade:order:update']" label-class-name="no-colon">
        <el-button
          v-if="formData.status! === TradeOrderStatusEnum.UNPAID.status"
          type="primary"
          @click="updatePrice"
        >
          {{ t('mall.trade.order.adjustPrice') }}
        </el-button>
        <el-button type="primary" @click="remark">{{ t('mall.trade.order.remark') }}</el-button>
        <!-- 待发�?-->
        <template v-if="formData.status! === TradeOrderStatusEnum.UNDELIVERED.status">
          <!-- 快递发�?-->
          <el-button
            v-if="formData.deliveryType === DeliveryTypeEnum.EXPRESS.type"
            type="primary"
            @click="delivery"
          >
            {{ t('mall.trade.order.delivery') }}
          </el-button>
          <el-button
            v-if="formData.deliveryType === DeliveryTypeEnum.EXPRESS.type"
            type="primary"
            @click="updateAddress"
          >
            {{ t('mall.trade.order.updateAddress') }}
          </el-button>
          <!-- 到店自提 -->
          <el-button
            v-if="formData.deliveryType === DeliveryTypeEnum.PICK_UP.type && showPickUp"
            type="primary"
            @click="handlePickUp"
          >
            {{ t('mall.trade.order.pickUp') }}
          </el-button>
        </template>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label><span style="color: red">{{ t('mall.trade.order.reminder') + ': ' }}</span></template>
        {{ t('mall.trade.order.reminderContent') }}<br />
        {{ t('mall.trade.order.reminderContent2') }} <br />
        {{ t('mall.trade.order.reminderContent3') }}
      </el-descriptions-item>
    </el-descriptions>

    <!-- 商品信息 -->
    <el-descriptions :title="t('mall.trade.order.productInfo')">
      <el-descriptions-item labelClassName="no-colon">
        <el-row :gutter="20">
          <el-col :span="15">
            <el-table :data="formData.items" border :table-layout="'auto'">
              <el-table-column :label="t('mall.trade.order.product')" prop="spuName" width="auto">
                <template #default="{ row }">
                  {{ row.spuName }}
                  <el-tag v-for="property in row.properties" :key="property.propertyId">
                    {{ property.propertyName }}: {{ property.valueName }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column :label="t('mall.trade.order.productOriginalPrice')" prop="price" min-width="150">
                <template #default="{ row }">{{ fenToYuan(row.price) }}{{ t('mall.trade.order.yuan') }}</template>
              </el-table-column>
              <el-table-column :label="t('mall.trade.order.count')" prop="count" min-width="100" />
              <el-table-column :label="t('mall.trade.order.total')" prop="payPrice" min-width="150">
                <template #default="{ row }">{{ fenToYuan(row.payPrice) }}{{ t('mall.trade.order.yuan') }}</template>
              </el-table-column>
              <el-table-column :label="t('mall.trade.order.afterSaleStatus')" prop="afterSaleStatus" min-width="120">
                <template #default="{ row }">
                  <dict-tag
                    :type="DICT_TYPE.TRADE_ORDER_ITEM_AFTER_SALE_STATUS"
                    :value="row.afterSaleStatus"
                  />
                </template>
              </el-table-column>
            </el-table>
          </el-col>
          <el-col :span="10" />
        </el-row>
      </el-descriptions-item>
    </el-descriptions>
    <el-descriptions :column="4">
      <!-- 第一�?-->
      <el-descriptions-item :label="t('mall.trade.order.productTotalPrice') + ': '">
        {{ fenToYuan(formData.totalPrice!) }} {{ t('mall.trade.order.yuan') }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.order.freightAmount') + ': '">
        {{ fenToYuan(formData.deliveryPrice!) }} {{ t('mall.trade.order.yuan') }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.order.orderAdjustPrice') + ': '">
        {{ fenToYuan(formData.adjustPrice!) }} {{ t('mall.trade.order.yuan') }}
      </el-descriptions-item>
      <el-descriptions-item v-for="item in 1" :key="item" label-class-name="no-colon" />
      <!-- 第二�?-->
      <el-descriptions-item>
        <template #label><span style="color: red">{{ t('mall.trade.order.couponDiscount') + ': ' }}</span></template>
        {{ fenToYuan(formData.couponPrice!) }} {{ t('mall.trade.order.yuan') }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label><span style="color: red">{{ t('mall.trade.order.vipDiscount') + ': ' }}</span></template>
        {{ fenToYuan(formData.vipPrice!) }} {{ t('mall.trade.order.yuan') }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label><span style="color: red">{{ t('mall.trade.order.activityDiscount') + ': ' }}</span></template>
        {{ fenToYuan(formData.discountPrice!) }} {{ t('mall.trade.order.yuan') }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label><span style="color: red">{{ t('mall.trade.order.pointDeduction') + ': ' }}</span></template>
        {{ fenToYuan(formData.pointPrice!) }} {{ t('mall.trade.order.yuan') }}
      </el-descriptions-item>
      <!-- 第三�?-->
      <el-descriptions-item v-for="item in 3" :key="item" label-class-name="no-colon" />
      <el-descriptions-item :label="t('mall.trade.order.payableAmount') + ': '">
        {{ fenToYuan(formData.payPrice!) }} {{ t('mall.trade.order.yuan') }}
      </el-descriptions-item>
    </el-descriptions>

    <!-- 物流信息 -->
    <el-descriptions :column="4" :title="t('mall.trade.order.receivingInfo')">
      <el-descriptions-item :label="t('mall.trade.order.deliveryType') + ': '">
        <dict-tag :type="DICT_TYPE.TRADE_DELIVERY_TYPE" :value="formData.deliveryType!" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.order.consignee') + ': '">{{ formData.receiverName }}</el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.order.contactPhone') + ': '">{{ formData.receiverMobile }}</el-descriptions-item>
      <!-- 快递配�?-->
      <div v-if="formData.deliveryType === DeliveryTypeEnum.EXPRESS.type">
        <el-descriptions-item v-if="formData.receiverDetailAddress" :label="t('mall.trade.order.receivingAddress') + ': '">
          {{ formData.receiverAreaName }} {{ formData.receiverDetailAddress }}
          <el-link
            v-clipboard:copy="formData.receiverAreaName + ' ' + formData.receiverDetailAddress"
            v-clipboard:success="clipboardSuccess"
            icon="ep:document-copy"
            type="primary"
          />
        </el-descriptions-item>
        <el-descriptions-item v-if="formData.logisticsId" :label="t('mall.trade.order.logisticsCompany') + ': '">
          {{ deliveryExpressList.find((item) => item.id === formData.logisticsId)?.name }}
        </el-descriptions-item>
        <el-descriptions-item v-if="formData.logisticsId" :label="t('mall.trade.order.trackingNo') + ': '">
          {{ formData.logisticsNo }}
        </el-descriptions-item>
        <el-descriptions-item v-if="formatDate.deliveryTime" :label="t('mall.trade.order.deliveryTime') + ': '">
          {{ formatDate(formData.deliveryTime) }}
        </el-descriptions-item>
        <el-descriptions-item v-for="item in 2" :key="item" label-class-name="no-colon" />
        <el-descriptions-item v-if="expressTrackList.length > 0" :label="t('mall.trade.order.logisticsDetail') + ': '">
          <el-timeline>
            <el-timeline-item
              v-for="(express, index) in expressTrackList"
              :key="index"
              :timestamp="formatDate(express.time)"
            >
              {{ express.content }}
            </el-timeline-item>
          </el-timeline>
        </el-descriptions-item>
      </div>
      <!-- 自提门店 -->
      <div v-if="formData.deliveryType === DeliveryTypeEnum.PICK_UP.type">
        <el-descriptions-item v-if="formData.pickUpStoreId" :label="t('mall.trade.delivery.storeTitle') + ': '">
          {{ pickUpStore?.name }}
        </el-descriptions-item>
      </div>
    </el-descriptions>

    <!-- 订单日志 -->
    <el-descriptions :title="t('mall.trade.order.orderLog')">
      <el-descriptions-item labelClassName="no-colon">
        <el-timeline>
          <el-timeline-item
            v-for="(log, index) in formData.logs"
            :key="index"
            :timestamp="formatDate(log.createTime!)"
            placement="top"
          >
            <div class="el-timeline-right-content">
              {{ log.content }}
            </div>
            <template #dot>
              <span
                :style="{ backgroundColor: getUserTypeColor(log.userType!) }"
                class="dot-node-style"
              >
                {{ getDictLabel(DICT_TYPE.USER_TYPE, log.userType)[0] }}
              </span>
            </template>
          </el-timeline-item>
        </el-timeline>
      </el-descriptions-item>
    </el-descriptions>
  </ContentWrap>

  <!-- 各种操作的弹�?-->
  <OrderDeliveryForm ref="deliveryFormRef" @success="getDetail" />
  <OrderUpdateRemarkForm ref="updateRemarkForm" @success="getDetail" />
  <OrderUpdateAddressForm ref="updateAddressFormRef" @success="getDetail" />
  <OrderUpdatePriceForm ref="updatePriceFormRef" @success="getDetail" />
</template>
<script lang="ts" setup>
import * as TradeOrderApi from '@/api/mall/trade/order'
import { fenToYuan } from '@/utils'
import { formatDate } from '@/utils/formatTime'
import { DICT_TYPE, getDictLabel, getDictObj } from '@/utils/dict'
import OrderUpdateRemarkForm from '@/views/mall/trade/order/form/OrderUpdateRemarkForm.vue'
import OrderDeliveryForm from '@/views/mall/trade/order/form/OrderDeliveryForm.vue'
import OrderUpdateAddressForm from '@/views/mall/trade/order/form/OrderUpdateAddressForm.vue'
import OrderUpdatePriceForm from '@/views/mall/trade/order/form/OrderUpdatePriceForm.vue'
import * as DeliveryExpressApi from '@/api/mall/trade/delivery/express'
import { useTagsViewStore } from '@/store/modules/tagsView'
import { DeliveryTypeEnum, TradeOrderStatusEnum } from '@/utils/constants'
import * as DeliveryPickUpStoreApi from '@/api/mall/trade/delivery/pickUpStore'
import { propTypes } from '@/utils/propTypes'

defineOptions({ name: 'TradeOrderDetail' })

const { t } = useI18n() // 国际�?

const message = useMessage() // 消息弹窗

/** 获得 userType 颜色 */
const getUserTypeColor = (type: number) => {
  const dict = getDictObj(DICT_TYPE.USER_TYPE, type)
  switch (dict?.colorType) {
    case 'success':
      return '#67C23A'
    case 'info':
      return '#909399'
    case 'warning':
      return '#E6A23C'
    case 'danger':
      return '#F56C6C'
  }
  return '#409EFF'
}

// 订单详情
const formData = ref<TradeOrderApi.OrderVO>({
  logs: []
})

/** 各种操作 */
const updateRemarkForm = ref() // 订单备注表单 Ref
const remark = () => {
  updateRemarkForm.value?.open(formData.value)
}
const deliveryFormRef = ref() // 发货表单 Ref
const delivery = () => {
  deliveryFormRef.value?.open(formData.value)
}
const updateAddressFormRef = ref() // 收货地址表单 Ref
const updateAddress = () => {
  updateAddressFormRef.value?.open(formData.value)
}
const updatePriceFormRef = ref() // 订单调价表单 Ref
const updatePrice = () => {
  updatePriceFormRef.value?.open(formData.value)
}

/** 核销 */
const handlePickUp = async () => {
  try {
    // 二次确认
    await message.confirm(t('common.confirmTitle'))
    // 提交
    await TradeOrderApi.pickUpOrder(formData.value.id!)
    message.success(t('mall.trade.order.pickUpSuccess'))
    // 刷新列表
    await getDetail()
  } catch {}
}

/** 获得详情 */
const { params } = useRoute() // 查询参数
const props = defineProps({
  id: propTypes.number.def(undefined), // 订单ID
  showPickUp: propTypes.bool.def(true) // 显示核销按钮
})
const id = (params.id || props.id) as unknown as number
const getDetail = async () => {
  if (id) {
    const res = (await TradeOrderApi.getOrder(id)) as TradeOrderApi.OrderVO
    // 没有表单信息则关闭页面返�?
    if (!res) {
      message.error(t('mall.trade.order.detail') + t('common.notExist'))
      close()
    }
    formData.value = res
  }
}

/** 关闭 tag */
const { delView } = useTagsViewStore() // 视图操作
const { push, currentRoute } = useRouter() // 路由
const close = () => {
  delView(unref(currentRoute))
  push({ name: 'TradeOrder' })
}

/** 复制 */
const clipboardSuccess = () => {
  message.success(t('common.copySuccess'))
}

/** 初始�?**/
const deliveryExpressList = ref([]) // 物流公司
const expressTrackList = ref([]) // 物流详情
const pickUpStore = ref({}) // 自提门店
onMounted(async () => {
  await getDetail()
  // 如果配送方式为快递，则查询物流公�?
  if (formData.value.deliveryType === DeliveryTypeEnum.EXPRESS.type) {
    deliveryExpressList.value = await DeliveryExpressApi.getSimpleDeliveryExpressList()
    if (formData.value.logisticsId) {
      expressTrackList.value = await TradeOrderApi.getExpressTrackList(formData.value.id!)
    }
  } else if (formData.value.deliveryType === DeliveryTypeEnum.PICK_UP.type) {
    if (formData.value.pickUpStoreId) {
      pickUpStore.value = await DeliveryPickUpStoreApi.getDeliveryPickUpStore(formData.value.pickUpStoreId)
    }
  }
})
</script>
<style lang="scss" scoped>
:deep(.el-descriptions) {
  &:not(:nth-child(1)) {
    margin-top: 20px;
  }

  .el-descriptions__title {
    display: flex;
    align-items: center;

    &::before {
      display: inline-block;
      width: 3px;
      height: 20px;
      margin-right: 10px;
      background-color: #409eff;
      content: '';
    }
  }

  .el-descriptions-item__container {
    margin: 0 10px;

    .no-colon {
      margin: 0;

      &::after {
        content: '';
      }
    }
  }
}

// 时间线样式调�?
:deep(.el-timeline) {
  margin: 10px 0 0 160px;

  .el-timeline-item__wrapper {
    position: relative;
    top: -20px;

    .el-timeline-item__timestamp {
      position: absolute !important;
      top: 10px;
      left: -150px;
    }
  }

  .el-timeline-right-content {
    display: flex;
    align-items: center;
    min-height: 30px;
    padding: 10px;
    border-radius: var(--el-card-border-radius);
    background-color: var(--app-content-bg-color);

    &::before {
      position: absolute;
      top: 10px;
      left: 13px; /* 将伪元素水平居中 */
      border-color: transparent var(--app-content-bg-color) transparent transparent; /* 尖角颜色，左侧朝�?*/
      border-style: solid;
      border-width: 8px; /* 调整尖角大小 */
      content: ''; /* 必须设置 content 属�?*/
    }
  }

  .dot-node-style {
    position: absolute;
    left: -5px;
    display: flex;
    width: 20px;
    height: 20px;
    font-size: 10px;
    color: #fff;
    border-radius: 50%;
    justify-content: center;
    align-items: center;
  }
}
</style>

<template>
  <ContentWrap>
    <!-- 订单信息 -->
    <el-descriptions :title="t('mall.trade.afterSale.orderInfo')">
      <el-descriptions-item :label="t('mall.trade.afterSale.orderNo') + ': '">{{ formData.orderNo }}</el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.order.deliveryType') + ': '">
        <dict-tag :type="DICT_TYPE.TRADE_DELIVERY_TYPE" :value="formData.order.deliveryType" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.order.orderType') + ': '">
        <dict-tag :type="DICT_TYPE.TRADE_ORDER_TYPE" :value="formData.order.type" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.order.consignee') + ': '">
        {{ formData.order.receiverName }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.order.buyerMessage') + ': '">
        {{ formData.order.userRemark }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.order.orderSource') + ': '">
        <dict-tag :type="DICT_TYPE.TERMINAL" :value="formData.order.terminal" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.order.contactPhone') + ': '">
        {{ formData.order.receiverMobile }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.order.merchantRemark') + ': '">{{ formData.order.remark }}</el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.order.payOrderId') + ': '">
        {{ formData.order.payOrderId }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.order.paymentMethod') + ': '">
        <dict-tag :type="DICT_TYPE.PAY_CHANNEL_CODE" :value="formData.order.payChannelCode" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.afterSale.buyer') + ': '">{{ formData?.user?.nickname }}</el-descriptions-item>
    </el-descriptions>

    <!-- 售后信息 -->
    <el-descriptions :title="t('mall.trade.afterSale.afterSaleInfo')">
      <el-descriptions-item :label="t('mall.trade.afterSale.refundNo') + ': '">{{ formData.no }}</el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.afterSale.applyTime') + ': '">
        {{ formatDate(formData.auditTime) }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.afterSale.type') + ': '">
        <dict-tag :type="DICT_TYPE.TRADE_AFTER_SALE_TYPE" :value="formData.type" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.afterSale.way') + ': '">
        <dict-tag :type="DICT_TYPE.TRADE_AFTER_SALE_WAY" :value="formData.way" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.afterSale.refundPrice') + ': '">
        {{ fenToYuan(formData.refundPrice) }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.afterSale.refundReason') + ': '">{{ formData.applyReason }}</el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.afterSale.additionalDesc') + ': '">
        {{ formData.applyDescription }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('mall.trade.afterSale.evidenceImages') + ': '">
        <el-image
          v-for="(item, index) in formData.applyPicUrls"
          :key="index"
          :src="item.url"
          class="mr-10px h-60px w-60px"
          @click="imagePreview(formData.applyPicUrls)"
        />
      </el-descriptions-item>
    </el-descriptions>

    <!-- 退款状�?-->
    <el-descriptions :column="1" :title="t('mall.trade.afterSale.refundStatus')">
      <el-descriptions-item :label="t('mall.trade.afterSale.refundStatus') + ': '">
        <dict-tag :type="DICT_TYPE.TRADE_AFTER_SALE_STATUS" :value="formData.status" />
      </el-descriptions-item>
      <el-descriptions-item label-class-name="no-colon">
        <el-button v-if="formData.status === 10" type="primary" @click="agree">{{ t('mall.trade.afterSale.agreeAfterSale') }}</el-button>
        <el-button v-if="formData.status === 10" type="primary" @click="disagree">
          {{ t('mall.trade.afterSale.disagreeAfterSale') }}
        </el-button>
        <el-button v-if="formData.status === 30" type="primary" @click="receive">
          {{ t('mall.trade.afterSale.confirmReceive') }}
        </el-button>
        <el-button v-if="formData.status === 30" type="primary" @click="refuse">{{ t('mall.trade.afterSale.refuseReceive') }}</el-button>
        <el-button v-if="formData.status === 40" type="primary" @click="refund">{{ t('mall.trade.afterSale.confirmRefund') }}</el-button>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label><span style="color: red">{{ t('mall.trade.order.reminder') }}: </span></template>
        {{ t('mall.trade.afterSale.afterSaleReminder') }}<br />
        {{ t('mall.trade.afterSale.afterSaleReminder2') }}<br />
        {{ t('mall.trade.afterSale.afterSaleReminder3') }}
      </el-descriptions-item>
    </el-descriptions>

    <!-- 商品信息 -->
    <el-descriptions :title="t('mall.trade.afterSale.productInfo')">
      <el-descriptions-item labelClassName="no-colon">
        <el-row :gutter="20">
          <el-col :span="15">
            <el-table v-if="formData.orderItem" :data="[formData.orderItem]" border :table-layout="'auto'">
              <el-table-column :label="t('mall.trade.order.product')" prop="spuName" width="auto">
                <template #default="{ row }">
                  {{ row.spuName }}
                  <el-tag
                    v-for="property in row.properties"
                    :key="property.propertyId"
                    class="mr-10px"
                  >
                    {{ property.propertyName }}: {{ property.valueName }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column :label="t('mall.trade.order.productOriginalPrice')" prop="price" min-width="150">
                <template #default="{ row }">{{ fenToYuan(row.price) }} {{ t('mall.trade.order.yuan') }}</template>
              </el-table-column>
              <el-table-column :label="t('mall.trade.order.count')" prop="count" min-width="100" />
              <el-table-column :label="t('mall.trade.order.total')" prop="payPrice" min-width="150">
                <template #default="{ row }">{{ fenToYuan(row.payPrice) }} {{ t('mall.trade.order.yuan') }}</template>
              </el-table-column>
            </el-table>
          </el-col>
          <el-col :span="10" />
        </el-row>
      </el-descriptions-item>
    </el-descriptions>

    <!-- 操作日志 -->
    <el-descriptions :title="t('mall.trade.afterSale.afterSaleLog')">
      <el-descriptions-item labelClassName="no-colon">
        <el-timeline>
          <el-timeline-item
            v-for="saleLog in formData.logs"
            :key="saleLog.id"
            :timestamp="formatDate(saleLog.createTime)"
            placement="top"
          >
            <div class="el-timeline-right-content">
              <span>{{ saleLog.content }}</span>
            </div>
            <template #dot>
              <span
                :style="{ backgroundColor: getUserTypeColor(saleLog.userType) }"
                class="dot-node-style"
              >
                {{ getDictLabel(DICT_TYPE.USER_TYPE, saleLog.userType)[0] || t('common.system') }}
              </span>
            </template>
          </el-timeline-item>
        </el-timeline>
      </el-descriptions-item>
    </el-descriptions>
  </ContentWrap>

  <!-- 各种操作的弹�?-->
  <UpdateAuditReasonForm ref="updateAuditReasonFormRef" @success="getDetail" />
</template>
<script lang="ts" setup>
import * as AfterSaleApi from '@/api/mall/trade/afterSale/index'
import { fenToYuan } from '@/utils'
import { DICT_TYPE, getDictLabel, getDictObj } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import UpdateAuditReasonForm from '@/views/mall/trade/afterSale/form/AfterSaleDisagreeForm.vue'
import { createImageViewer } from '@/components/ImageViewer'
import { isArray } from '@/utils/is'
import { useTagsViewStore } from '@/store/modules/tagsView'

defineOptions({ name: 'TradeAfterSaleDetail' })

const { t } = useI18n() // 国际�?
const message = useMessage() // 消息弹窗
const { params } = useRoute() // 查询参数
const { push, currentRoute } = useRouter() // 路由
const formData = ref({
  order: {},
  logs: []
})
const updateAuditReasonFormRef = ref() // 拒绝售后表单 Ref

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

/** 获得详情 */
const getDetail = async () => {
  const id = params.id as unknown as number
  if (id) {
    const res = await AfterSaleApi.getAfterSale(id)
    // 没有表单信息则关闭页面返�?
    if (res == null) {
      message.notifyError(t('mall.trade.afterSale.notExist'))
      close()
    }
    formData.value = res
  }
}

/** 同意售后 */
const agree = async () => {
  try {
    // 二次确认
    await message.confirm(t('mall.trade.afterSale.agreeConfirm'))
    await AfterSaleApi.agree(formData.value.id)
    // 提示成功
    message.success(t('common.success'))
    await getDetail()
  } catch {}
}

/** 拒绝售后 */
const disagree = async () => {
  updateAuditReasonFormRef.value?.open(formData.value)
}

/** 确认收货 */
const receive = async () => {
  try {
    // 二次确认
    await message.confirm(t('mall.trade.afterSale.receiveConfirm'))
    await AfterSaleApi.receive(formData.value.id)
    // 提示成功
    message.success(t('common.success'))
    await getDetail()
  } catch {}
}

/** 拒绝收货 */
const refuse = async () => {
  try {
    // 二次确认
    await message.confirm(t('mall.trade.afterSale.refuseConfirm'))
    await AfterSaleApi.refuse(formData.value.id)
    // 提示成功
    message.success(t('common.success'))
    await getDetail()
  } catch {}
}

/** 确认退�?*/
const refund = async () => {
  try {
    // 二次确认
    await message.confirm(t('mall.trade.afterSale.refundConfirm'))
    await AfterSaleApi.refund(formData.value.id)
    // 提示成功
    message.success(t('common.success'))
    await getDetail()
  } catch {}
}

/** 图片预览 */
const imagePreview = (args) => {
  const urlList = []
  if (isArray(args)) {
    args.forEach((item) => {
      urlList.push(item.url)
    })
  } else {
    urlList.push(args)
  }
  createImageViewer({
    urlList
  })
}
const { delView } = useTagsViewStore() // 视图操作
/** 关闭 tag */
const close = () => {
  delView(unref(currentRoute))
  push({ name: 'TradeAfterSale' })
}
onMounted(async () => {
  await getDetail()
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
    background-color: var(--app-content-bg-color);

    &::before {
      position: absolute;
      top: 10px;
      left: 13px;
      border-color: transparent var(--app-content-bg-color) transparent transparent; /* 尖角颜色，左侧朝�?*/
      border-style: solid;
      border-width: 8px; /* 调整尖角大小 */
      content: '';
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

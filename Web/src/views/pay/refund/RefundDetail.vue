<template>
  <Dialog v-model="dialogVisible" :title="t('refund.detailTitle')" width="700px">
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item :label="t('refund.merchantRefundId')">
        <el-tag size="small">{{ refundDetail.merchantRefundId }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="t('refund.channelRefundNo')">
        <el-tag type="success" size="small" v-if="refundDetail.channelRefundNo">{{
          refundDetail.channelRefundNo
        }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="t('order.merchantOrderId')">
        <el-tag size="small">{{ refundDetail.merchantOrderId }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="t('order.channelOrderNo')">
        <el-tag type="success" size="small">{{ refundDetail.channelOrderNo }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="t('order.appId')">{{ refundDetail.appId }}</el-descriptions-item>
      <el-descriptions-item :label="t('order.appName')">{{ refundDetail.appName }}</el-descriptions-item>
      <el-descriptions-item :label="t('refund.payPrice')">
        <el-tag type="success" size="small">
          ￥{{ (refundDetail.payPrice / 100.0).toFixed(2) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="t('refund.price')">
        <el-tag size="mini" type="danger">
          ￥{{ (refundDetail.refundPrice / 100.0).toFixed(2) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="t('refund.status')">
        <dict-tag :type="DICT_TYPE.PAY_REFUND_STATUS" :value="refundDetail.status" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('refund.successTime')">
        {{ formatDate(refundDetail.successTime) }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('refund.createTime')">
        {{ formatDate(refundDetail.createTime) }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('notify.updateTime')">
        {{ formatDate(refundDetail.updateTime) }}
      </el-descriptions-item>
    </el-descriptions>
    <!-- 分割线 -->
    <el-divider />
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item :label="t('refund.channelCode')">
        <dict-tag :type="DICT_TYPE.PAY_CHANNEL_CODE" :value="refundDetail.channelCode" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('refund.reason')">{{ refundDetail.reason }}</el-descriptions-item>
      <el-descriptions-item :label="t('refund.userIp')">{{ refundDetail.userIp }}</el-descriptions-item>
      <el-descriptions-item :label="t('channel.notifyUrl')">{{ refundDetail.notifyUrl }}</el-descriptions-item>
    </el-descriptions>
    <!-- 分割线 -->
    <el-divider />
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item :label="t('refund.channelErrorCode')">
        {{ refundDetail.channelErrorCode }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('refund.channelErrorMsg')">
        {{ refundDetail.channelErrorMsg }}
      </el-descriptions-item>
    </el-descriptions>
    <el-descriptions :column="1" label-class-name="desc-label" direction="vertical" border>
      <el-descriptions-item :label="t('refund.extension')">
        <el-text style="white-space: pre-wrap; word-break: break-word">
          {{ refundDetail.channelNotifyData }}
        </el-text>
      </el-descriptions-item>
    </el-descriptions>
  </Dialog>
</template>
<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import * as RefundApi from '@/api/pay/refund'
import { useI18n } from '@/hooks/web/useI18n'

const { t } = useI18n('pay')

defineOptions({ name: 'PayRefundDetail' })

const dialogVisible = ref(false) // 弹窗的是否展示
const detailLoading = ref(false) // 表单的加载中
const refundDetail = ref({})

/** 打开弹窗 */
const open = async (id: number) => {
  dialogVisible.value = true
  // 设置数据
  detailLoading.value = true
  try {
    refundDetail.value = await RefundApi.getRefund(id)
  } finally {
    detailLoading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗
</script>
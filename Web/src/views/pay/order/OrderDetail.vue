<template>
  <Dialog v-model="dialogVisible" :title="t('order.detailTitle')" width="700px">
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item :label="t('order.merchantOrderId')">
        <el-tag size="small">{{ detailData.merchantOrderId }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="t('order.no')">
        <el-tag type="warning" size="small" v-if="detailData.no">{{ detailData.no }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="t('order.appId')">{{ detailData.appId }}</el-descriptions-item>
      <el-descriptions-item :label="t('order.appName')">{{ detailData.appName }}</el-descriptions-item>
      <el-descriptions-item :label="t('order.status')">
        <dict-tag :type="DICT_TYPE.PAY_ORDER_STATUS" :value="detailData.status" size="small" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('order.price')">
        <el-tag type="success" size="small">￥{{ ((detailData.price || 0) / 100.0).toFixed(2) }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="t('order.channelFeePrice')">
        <el-tag type="warning" size="small">
          ￥{{ ((detailData.channelFeePrice || 0) / 100.0).toFixed(2) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="t('order.channelFeeRate')">
        {{ (detailData.channelFeeRate || 0).toFixed(2) }}%
      </el-descriptions-item>
      <el-descriptions-item :label="t('order.successTime')">
        {{ formatDate(detailData.successTime) }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('order.expireTime')">
        {{ formatDate(detailData.expireTime) }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('order.createTime')">
        {{ formatDate(detailData.createTime) }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('notify.updateTime')">
        {{ formatDate(detailData.updateTime) }}
      </el-descriptions-item>
    </el-descriptions>
    <!-- 分割线 -->
    <el-divider />
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item :label="t('order.subject')">{{ detailData.subject }}</el-descriptions-item>
      <el-descriptions-item :label="t('order.body')">{{ detailData.body }}</el-descriptions-item>
      <el-descriptions-item :label="t('channel.code')">
        <dict-tag :type="DICT_TYPE.PAY_CHANNEL_CODE" :value="detailData.channelCode" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('order.userIp')">{{ detailData.userIp }}</el-descriptions-item>
      <el-descriptions-item :label="t('order.channelOrderNo')">
        <el-tag size="mini" type="success" v-if="detailData.channelOrderNo">
          {{ detailData.channelOrderNo }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="t('order.channelUserId')">{{ detailData.channelUserId }}</el-descriptions-item>
      <el-descriptions-item :label="t('order.refundPrice')">
        <el-tag size="mini" type="danger">
          ￥{{ ((detailData.refundPrice || 0) / 100.0).toFixed(2) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="t('channel.notifyUrl')">{{ detailData.notifyUrl }}</el-descriptions-item>
    </el-descriptions>
    <!-- 分割线 -->
    <el-divider />
    <el-descriptions :column="1" label-class-name="desc-label" direction="vertical" border>
      <el-descriptions-item :label="t('order.extension')">
        <el-text style="white-space: pre-wrap; word-break: break-word">
          {{ detailData.extension.channelNotifyData }}
        </el-text>
      </el-descriptions-item>
    </el-descriptions>
  </Dialog>
</template>
<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import * as OrderApi from '@/api/pay/order'
import { formatDate } from '@/utils/formatTime'
import { useI18n } from '@/hooks/web/useI18n'

const { t } = useI18n('pay')

defineOptions({ name: 'PayOrderDetail' })

const dialogVisible = ref(false) // 弹窗的是否展示
const detailLoading = ref(false) // 表单的加载中
const detailData = ref({
  extension: {}
})

/** 打开弹窗 */
const open = async (id: number) => {
  dialogVisible.value = true
  // 设置数据
  detailLoading.value = true
  try {
    detailData.value = await OrderApi.getOrderDetail(id)
    if (!detailData.value.extension) {
      detailData.value.extension = {}
    }
  } finally {
    detailLoading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗
</script>
<style>
.tag-purple {
  color: #722ed1;
  background: #f9f0ff;
  border-color: #d3adf7;
}

.tag-pink {
  color: #eb2f96;
  background: #fff0f6;
  border-color: #ffadd2;
}
</style>
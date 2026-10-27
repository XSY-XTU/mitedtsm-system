<template>
  <Dialog v-model="dialogVisible" :title="t('transfer.detailTitle')" width="700px">
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item :label="t('transfer.merchantTransferId')">
        <el-tag size="small">{{ detailData.merchantTransferId }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="t('transfer.no')">
        <el-tag type="warning" size="small" v-if="detailData.no">{{ detailData.no }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="t('order.appId')">{{ detailData.appId }}</el-descriptions-item>
      <el-descriptions-item :label="t('transfer.status')">
        <dict-tag :type="DICT_TYPE.PAY_TRANSFER_STATUS" :value="detailData.status" size="small" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('transfer.price')">
        <el-tag type="success" size="small">￥{{ (detailData.price / 100.0).toFixed(2) }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="t('transfer.successTime')">
        {{ formatDate(detailData.successTime) }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('transfer.createTime')">
        {{ formatDate(detailData.createTime) }}
      </el-descriptions-item>
    </el-descriptions>
    <!-- 分割线 -->
    <el-divider />
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item :label="t('transfer.userName')">{{ detailData.userName }}</el-descriptions-item>
      <el-descriptions-item :label="t('transfer.userAccount')">{{ detailData.userAccount }}</el-descriptions-item>
      <el-descriptions-item :label="t('channel.code')">
        <dict-tag :type="DICT_TYPE.PAY_CHANNEL_CODE" :value="detailData.channelCode" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('transfer.userIp')">{{ detailData.userIp }}</el-descriptions-item>
      <el-descriptions-item :label="t('transfer.channelTransferNo')">
        <el-tag size="mini" type="success" v-if="detailData.channelTransferNo">
          {{ detailData.channelTransferNo }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="t('channel.notifyUrl')">{{ detailData.notifyUrl }}</el-descriptions-item>
    </el-descriptions>
    <el-divider />
    <el-descriptions :column="1" label-class-name="desc-label" direction="vertical" border>
      <el-descriptions-item :label="t('transfer.channelNotifyData')">
        <el-text style="white-space: pre-wrap; word-break: break-word">
          {{ detailData.channelNotifyData }}
        </el-text>
      </el-descriptions-item>
    </el-descriptions>
    <el-divider />
    <div style="text-align: right">
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </div>
  </Dialog>
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import * as TransferApi from '@/api/pay/transfer'
import { formatDate } from '@/utils/formatTime'
import { useI18n } from '@/hooks/web/useI18n'

const { t } = useI18n('pay')

defineOptions({ name: 'PayTransferDetail' })

const dialogVisible = ref(false) // 弹窗的是否展示
const detailLoading = ref(false) // 表单的加载中
const detailData = ref({})

/** 打开弹窗 */
const open = async (id: number) => {
  dialogVisible.value = true
  // 设置数据
  detailLoading.value = true
  try {
    detailData.value = await TransferApi.getTransfer(id)
  } finally {
    detailLoading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗
</script>

<style scoped></style>
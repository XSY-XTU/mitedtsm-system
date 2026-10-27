<template>
  <Dialog v-model="dialogVisible" :title="t('notify.detailTitle')" width="50%">
    <el-descriptions :column="2">
      <el-descriptions-item :label="t('notify.status')" :span="2">
        <dict-tag :type="DICT_TYPE.PAY_NOTIFY_STATUS" :value="detailData.status" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('notify.merchantOrderId')" :span="2">
        <el-tag>{{ detailData.merchantOrderId }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="t('notify.merchantRefundId')" :span="2" v-if="detailData.merchantRefundId">
        <el-tag>{{ detailData.merchantRefundId }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="t('notify.merchantTransferId')" :span="2" v-if="detailData.merchantTransferId">
        <el-tag>{{ detailData.merchantTransferId }}</el-tag>
      </el-descriptions-item>

      <el-descriptions-item :label="t('notify.appId')">{{ detailData.appId }}</el-descriptions-item>
      <el-descriptions-item :label="t('notify.appName')">{{ detailData.appName }}</el-descriptions-item>

      <el-descriptions-item :label="t('notify.dataId')">{{ detailData.dataId }}</el-descriptions-item>
      <el-descriptions-item :label="t('notify.type')">
        <dict-tag :type="DICT_TYPE.PAY_NOTIFY_TYPE" :value="detailData.type" />
      </el-descriptions-item>

      <el-descriptions-item :label="t('notify.notifyTimes')">{{ detailData.notifyTimes }}</el-descriptions-item>
      <el-descriptions-item :label="t('notify.maxNotifyTimes')">
        {{ detailData.maxNotifyTimes }}
      </el-descriptions-item>

      <el-descriptions-item :label="t('notify.lastExecuteTime')">
        {{ formatDate(detailData.lastExecuteTime) }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('notify.nextNotifyTime')">
        {{ formatDate(detailData.nextNotifyTime) }}
      </el-descriptions-item>

      <el-descriptions-item :label="t('notify.createTime')">
        {{ formatDate(detailData.createTime) }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('notify.updateTime')">
        {{ formatDate(detailData.updateTime) }}
      </el-descriptions-item>
    </el-descriptions>

    <!-- 分割线 -->
    <el-divider />

    <el-descriptions :column="1" direction="vertical" border>
      <el-descriptions-item :label="t('notify.log')">
        <el-table :data="detailData.logs" :table-layout="'auto'">
          <el-table-column :label="t('notify.logId')" align="center" prop="id" />
          <el-table-column :label="t('notify.status')" align="center" prop="status">
            <template #default="scope">
              <dict-tag :type="DICT_TYPE.PAY_NOTIFY_STATUS" :value="scope.row.status" />
            </template>
          </el-table-column>
          <el-table-column :label="t('notify.notifyTimes')" align="center" prop="notifyTimes" />
          <el-table-column :label="t('notify.logTime')" align="center" prop="lastExecuteTime" min-width="180">
            <template #default="scope">
              <span>{{ formatDate(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="t('notify.response')" align="center" prop="response" />
        </el-table>
      </el-descriptions-item>
    </el-descriptions>
  </Dialog>
</template>
<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import * as PayNotifyApi from '@/api/pay/notify'
import { formatDate } from '@/utils/formatTime'
import { useI18n } from '@/hooks/web/useI18n'

const { t } = useI18n('pay')

defineOptions({ name: 'PayNotifyDetail' })

const dialogVisible = ref(false) // 弹窗的是否展示
const detailLoading = ref(false) // 表单的加载中
const detailData = ref({})

/** 打开弹窗 */
const open = async (id: number) => {
  dialogVisible.value = true
  // 设置数据
  detailLoading.value = true
  try {
    detailData.value = await PayNotifyApi.getNotifyTaskDetail(id)
  } finally {
    detailLoading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗
</script>
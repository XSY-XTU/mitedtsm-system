<template>
  <Dialog v-model="dialogVisible" :max-height="500" :scroll="true" :title="t('common.detail')" width="800">
    <el-descriptions :column="1" border>
      <el-descriptions-item :label="t('apiLog.id')" min-width="120">
        {{ detailData.id }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('apiLog.traceId')">
        {{ detailData.traceId }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('apiLog.applicationName')">
        {{ detailData.applicationName }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('apiLog.userInfo')">
        {{ detailData.userId }}
        <dict-tag :type="DICT_TYPE.USER_TYPE" :value="detailData.userType" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('apiLog.userIp')">
        {{ detailData.userIp }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('apiLog.userAgent')">
        {{ detailData.userAgent }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('apiLog.requestInfo')">
        {{ detailData.requestMethod }} {{ detailData.requestUrl }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('apiLog.requestParams')">
        {{ detailData.requestParams }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('apiLog.responseBody')">
        {{ detailData.responseBody }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('apiLog.requestTime')">
        {{ formatDate(detailData.beginTime) }} ~ {{ formatDate(detailData.endTime) }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('apiLog.duration')">{{ detailData.duration }} ms</el-descriptions-item>
      <el-descriptions-item :label="t('apiLog.result')">
        <div v-if="detailData.resultCode === 0">{{ t('apiLog.normal') }}</div>
        <div v-else-if="detailData.resultCode > 0">
          {{ t('apiLog.fail') }} | {{ detailData.resultCode }} | {{ detailData.resultMsg }}
        </div>
      </el-descriptions-item>
      <el-descriptions-item :label="t('apiLog.operateModule')">
        {{ detailData.operateModule }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('apiLog.operateName')">
        {{ detailData.operateName }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('apiLog.operateType')">
        <dict-tag :type="DICT_TYPE.INFRA_OPERATE_TYPE" :value="detailData.operateType" />
      </el-descriptions-item>
    </el-descriptions>
  </Dialog>
</template>

<script lang="ts" setup>
import { useI18n } from '@/hooks/web/useI18n'
import { DICT_TYPE } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import * as ApiAccessLog from '@/api/infra/apiAccessLog'

defineOptions({ name: 'ApiAccessLogDetail' })

const { t } = useI18n('infra')

const dialogVisible = ref(false) // 弹窗的是否展示
const detailLoading = ref(false) // 表单地加载中
const detailData = ref({} as ApiAccessLog.ApiAccessLogVO) // 详情数据

/** 打开弹窗 */
const open = async (data: ApiAccessLog.ApiAccessLogVO) => {
  dialogVisible.value = true
  // 设置数据
  detailLoading.value = true
  try {
    detailData.value = data
  } finally {
    detailLoading.value = false
  }
}

defineExpose({ open }) // 提供 open 方法，用于打开弹窗
</script>

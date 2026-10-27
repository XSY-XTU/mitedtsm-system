<template>
  <Dialog v-model="dialogVisible" :title="t('job.detail')" width="700px">
    <el-descriptions :column="1" border>
      <el-descriptions-item :label="t('job.id')" min-width="60">
        {{ detailData.id }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('job.name')">
        {{ detailData.name }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('job.status')">
        <dict-tag :type="DICT_TYPE.INFRA_JOB_STATUS" :value="detailData.status" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('job.handlerName')">
        {{ detailData.handlerName }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('job.handlerParams')">
        {{ detailData.handlerParam }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('job.cronExpression')">
        {{ detailData.cronExpression }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('job.retryCount')">
        {{ detailData.retryCount }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('job.retryInterval')">
        {{ detailData.retryInterval + ' ' + t('job.log.ms') }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('job.monitorTimeout')">
        {{ detailData.monitorTimeout > 0 ? detailData.monitorTimeout + ' ' + t('job.log.ms') : t('job.notEnabled') }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('job.nextExecuteTime')">
        <el-timeline>
          <el-timeline-item
            v-for="(nextTime, index) in nextTimes"
            :key="index"
            :timestamp="formatDate(nextTime)"
          >
            {{ t('job.log.executeIndex') }} {{ index + 1 }}
          </el-timeline-item>
        </el-timeline>
      </el-descriptions-item>
    </el-descriptions>
  </Dialog>
</template>
<script lang="ts" setup>
import { useI18n } from '@/hooks/web/useI18n'
import { DICT_TYPE } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import * as JobApi from '@/api/infra/job'

defineOptions({ name: 'InfraJobDetail' })

const { t } = useI18n('infra')

const dialogVisible = ref(false) // 弹窗的是否展示
const detailLoading = ref(false) // 表单的加载中
const detailData = ref({} as JobApi.JobVO) // 详情数据
const nextTimes = ref([]) // 下一轮执行时间的数组

/** 打开弹窗 */
const open = async (id: number) => {
  dialogVisible.value = true
  // 查看，设置数据
  if (id) {
    detailLoading.value = true
    try {
      detailData.value = await JobApi.getJob(id)
      // 获取下一次执行时间
      nextTimes.value = await JobApi.getJobNextTimes(id)
    } finally {
      detailLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗
</script>

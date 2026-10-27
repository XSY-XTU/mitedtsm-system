<template>
  <div>
    <div class="flex items-start justify-between">
      <div>
        <el-col>
          <el-row>
            <span class="text-xl font-bold">{{ t('receivablePlan.period') }} {{ receivablePlan.period }}</span>
          </el-row>
        </el-col>
      </div>
      <div>
        <!-- 右上：按钮 -->
        <slot></slot>
      </div>
    </div>
  </div>
  <ContentWrap class="mt-10px">
    <el-descriptions :column="5" direction="vertical">
      <el-descriptions-item :label="t('receivablePlan.customerName')">
        {{ receivablePlan.customerName }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('receivablePlan.contractNo')">{{ receivablePlan.contractNo }}</el-descriptions-item>
      <el-descriptions-item :label="t('receivablePlan.price')">
        {{ erpPriceInputFormatter(receivablePlan.price) }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('receivablePlan.returnTime')">
        {{ formatDate(receivablePlan.returnTime) }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('receivablePlan.receivablePrice')">
        <el-text v-if="receivablePlan.receivable">
          {{ erpPriceInputFormatter(receivablePlan.receivable.price) }}
        </el-text>
        <el-text v-else>{{ erpPriceInputFormatter(0) }}</el-text>
      </el-descriptions-item>
    </el-descriptions>
  </ContentWrap>
</template>
<script lang="ts" setup>
import * as ReceivablePlanApi from '@/api/crm/receivable/plan'
import { formatDate } from '@/utils/formatTime'
import { erpPriceInputFormatter } from '@/utils'

const { t } = useI18n('crm') // 国际化
const { receivablePlan } = defineProps<{ receivablePlan: ReceivablePlanApi.ReceivablePlanVO }>()
</script>
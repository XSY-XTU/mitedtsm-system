<template>
  <ContentWrap>
    <el-collapse v-model="activeNames">
      <el-collapse-item name="basicInfo">
        <template #title>
          <span class="text-base font-bold">{{ t('customer.basicInfoTab') }}</span>
        </template>
        <el-descriptions :column="4">
          <el-descriptions-item :label="t('receivablePlan.period')">{{ receivablePlan.period }}</el-descriptions-item>
          <el-descriptions-item :label="t('receivablePlan.customerName')">
            {{ receivablePlan.customerName }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('receivablePlan.contractNo')">
            {{ receivablePlan.contractNo }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('receivablePlan.price')">
            {{ erpPriceInputFormatter(receivablePlan.price) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('receivablePlan.returnTime')">
            {{ formatDate(receivablePlan.returnTime, 'YYYY-MM-DD') }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('receivablePlan.returnType')">
            <dict-tag
              :type="DICT_TYPE.CRM_RECEIVABLE_RETURN_TYPE"
              :value="receivablePlan.returnType"
            />
          </el-descriptions-item>
          <el-descriptions-item :label="t('receivablePlan.remindDays')">
            {{ receivablePlan.remindDays }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('receivablePlan.remark')">{{ receivablePlan.remark }}</el-descriptions-item>
          <el-descriptions-item :label="t('receivablePlan.receivablePrice')">
            <el-text v-if="receivablePlan.receivable">
              {{ erpPriceInputFormatter(receivablePlan.receivable.price) }}
            </el-text>
            <el-text v-else>{{ erpPriceInputFormatter(0) }}</el-text>
          </el-descriptions-item>
          <el-descriptions-item :label="t('receivable.price')">
            <el-text v-if="receivablePlan.receivable">
              {{ erpPriceInputFormatter(receivablePlan.price - receivablePlan.receivable.price) }}
            </el-text>
            <el-text v-else>{{ erpPriceInputFormatter(receivablePlan.price) }}</el-text>
          </el-descriptions-item>
          <el-descriptions-item :label="t('receivable.returnTime')">
            {{ formatDate(receivablePlan.receivable?.returnTime, 'YYYY-MM-DD') }}
          </el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
      <el-collapse-item name="systemInfo">
        <template #title>
          <span class="text-base font-bold">{{ t('receivable.systemInfo') }}</span>
        </template>
        <el-descriptions :column="4">
          <el-descriptions-item :label="t('receivablePlan.ownerUserName')">
            {{ receivablePlan.ownerUserName }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('receivable.creatorName')">
            {{ receivablePlan.creatorName }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('receivablePlan.createTime')">
            {{ formatDate(receivablePlan.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('receivablePlan.updateTime')">
            {{ formatDate(receivablePlan.updateTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
    </el-collapse>
  </ContentWrap>
</template>
<script setup lang="ts">
import * as ReceivablePlanApi from '@/api/crm/receivable/plan'
import { DICT_TYPE } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import { erpPriceInputFormatter } from '@/utils'

const { t } = useI18n('crm') // 国际化

const { receivablePlan } = defineProps<{
  receivablePlan: ReceivablePlanApi.ReceivablePlanVO
}>()

// 展示的折叠面板
const activeNames = ref(['basicInfo', 'systemInfo'])
</script>
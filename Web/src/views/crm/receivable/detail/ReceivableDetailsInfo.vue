<template>
  <ContentWrap>
    <el-collapse v-model="activeNames">
      <el-collapse-item name="basicInfo">
        <template #title>
          <span class="text-base font-bold">{{ t('customer.basicInfoTab') }}</span>
        </template>
        <el-descriptions :column="4">
          <el-descriptions-item :label="t('receivable.no')">{{ receivable.no }}</el-descriptions-item>
          <el-descriptions-item :label="t('receivable.customerName')">
            {{ receivable.customerName }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('receivable.contractNo')">
            {{ receivable.contract?.no }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('receivable.returnTime')">
            {{ formatDate(receivable.returnTime, 'YYYY-MM-DD') }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('receivable.price')">
            {{ erpPriceInputFormatter(receivable.price) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('receivable.returnType')">
            <dict-tag :type="DICT_TYPE.CRM_RECEIVABLE_RETURN_TYPE" :value="receivable.returnType" />
          </el-descriptions-item>
          <el-descriptions-item :label="t('receivable.remark')">{{ receivable.remark }}</el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
      <el-collapse-item name="systemInfo">
        <template #title>
          <span class="text-base font-bold">{{ t('receivable.systemInfo') }}</span>
        </template>
        <el-descriptions :column="4">
          <el-descriptions-item :label="t('receivable.ownerUserName')">
            {{ receivable.ownerUserName }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('receivable.creatorName')">
            {{ receivable.creatorName }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('receivable.createTime')">
            {{ formatDate(receivable.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('receivable.updateTime')">
            {{ formatDate(receivable.updateTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
    </el-collapse>
  </ContentWrap>
</template>
<script setup lang="ts">
import * as ReceivableApi from '@/api/crm/receivable'
import { DICT_TYPE } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import { erpPriceInputFormatter } from '@/utils'

const { t } = useI18n('crm') // 国际化

const { receivable } = defineProps<{
  receivable: ReceivableApi.ReceivableVO
}>()

// 展示的折叠面板
const activeNames = ref(['basicInfo', 'systemInfo'])
</script>
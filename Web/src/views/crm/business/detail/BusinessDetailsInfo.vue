<template>
  <ContentWrap>
    <el-collapse v-model="activeNames">
      <el-collapse-item name="basicInfo">
        <template #title>
          <span class="text-base font-bold">{{ t('crm.customer.basicInfoTab') }}</span>
        </template>
        <el-descriptions :column="4">
          <el-descriptions-item :label="t('crm.business.name')">{{ business.name }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.business.customerName')">{{ business.customerName }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.business.price') + '（元）'">
            {{ erpPriceInputFormatter(business.totalPrice) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.business.dealTime')">
            {{ formatDate(business.dealTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.business.contactNextTime')">
            {{ formatDate(business.contactNextTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.business.statusTypeId')">
            {{ business.statusTypeName }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.business.statusName')">{{ business.statusName }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.business.remark')">{{ business.remark }}</el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
      <el-collapse-item name="systemInfo">
        <template #title>
          <span class="text-base font-bold">{{ t('common.systemInfo') }}</span>
        </template>
        <el-descriptions :column="4">
          <el-descriptions-item :label="t('crm.business.ownerUserName')">{{ business.ownerUserName }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.business.contactLastTime')">
            {{ formatDate(business.contactLastTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="''">&nbsp;</el-descriptions-item>
          <el-descriptions-item :label="''">&nbsp;</el-descriptions-item>
          <el-descriptions-item :label="t('crm.business.creatorName')">{{ business.creatorName }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.business.createTime')">
            {{ formatDate(business.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.business.updateTime')">
            {{ formatDate(business.updateTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
    </el-collapse>
  </ContentWrap>
</template>
<script setup lang="ts">
import * as BusinessApi from '@/api/crm/business'
import { formatDate } from '@/utils/formatTime'
import { erpPriceInputFormatter } from '@/utils'

const { t } = useI18n() // 国际化

const { business } = defineProps<{
  business: BusinessApi.BusinessVO
}>()

// 展示的折叠面板
const activeNames = ref(['basicInfo', 'systemInfo'])
</script>
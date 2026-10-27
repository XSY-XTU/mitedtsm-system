<template>
  <div>
    <div class="flex items-start justify-between">
      <div>
        <el-col>
          <el-row>
            <span class="text-xl font-bold">{{ business.name }}</span>
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
      <el-descriptions-item :label="t('crm.business.customerName')">{{ business.customerName }}</el-descriptions-item>
      <el-descriptions-item :label="t('crm.business.price') + '（元）'">
        {{ erpPriceInputFormatter(business.totalPrice) }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('crm.business.statusTypeName')">{{ business.statusTypeName }}</el-descriptions-item>
      <el-descriptions-item :label="t('crm.business.ownerUserName')">{{ business.ownerUserName }}</el-descriptions-item>
      <el-descriptions-item :label="t('crm.business.createTime')">
        {{ formatDate(business.createTime) }}
      </el-descriptions-item>
    </el-descriptions>
  </ContentWrap>
</template>
<script lang="ts" setup>
import * as BusinessApi from '@/api/crm/business'
import { formatDate } from '@/utils/formatTime'
import { erpPriceInputFormatter } from '@/utils'

const { t } = useI18n() // 国际化
const { business } = defineProps<{ business: BusinessApi.BusinessVO }>()
</script>
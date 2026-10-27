<!-- 合同详情组件 -->
<template>
  <ContentWrap>
    <el-collapse v-model="activeNames">
      <el-collapse-item name="contractInfo">
        <template #title>
          <span class="text-base font-bold">{{ t('crm.customer.basicInfoTab') }}</span>
        </template>
        <el-descriptions :column="4">
          <el-descriptions-item :label="t('crm.contract.no')">{{ contract.no }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.contract.name')">{{ contract.name }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.contract.customerName')">{{ contract.customerName }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.contract.businessName')">{{ contract.businessName }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.contract.totalPrice') + '（元）'">
            {{ erpPriceInputFormatter(contract.totalPrice) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.contract.orderDate')">
            {{ formatDate(contract.orderDate) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.contract.startTime')">
            {{ formatDate(contract.startTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.contract.endTime')">
            {{ formatDate(contract.endTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.contract.signContactId')">
            {{ contract.signContactName }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.contract.signUserId')">
            {{ contract.signUserName }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.contract.remark')">
            {{ contract.remark }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.contract.auditStatus')">
            <dict-tag :type="DICT_TYPE.CRM_AUDIT_STATUS" :value="contract.auditStatus" />
          </el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
      <el-collapse-item name="systemInfo">
        <template #title>
          <span class="text-base font-bold">{{ t('common.systemInfo') }}</span>
        </template>
        <el-descriptions :column="4">
          <el-descriptions-item :label="t('crm.contract.ownerUserName')">{{ contract.ownerUserName }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.contract.contactLastTime')">
            {{ formatDate(contract.contactLastTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="''">&nbsp;</el-descriptions-item>
          <el-descriptions-item :label="''">&nbsp;</el-descriptions-item>
          <el-descriptions-item :label="t('crm.contract.creatorName')">{{ contract.creatorName }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.contract.createTime')">
            {{ formatDate(contract.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.contract.updateTime')">
            {{ formatDate(contract.updateTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
    </el-collapse>
  </ContentWrap>
</template>
<script lang="ts" setup>
import * as ContractApi from '@/api/crm/contract'
import { formatDate } from '@/utils/formatTime'
import { DICT_TYPE } from '@/utils/dict'
import { erpPriceInputFormatter } from '@/utils'

const { t } = useI18n() // 国际化

defineOptions({ name: 'ContractDetailsInfo' })
defineProps<{
  contract: ContractApi.ContractVO
}>()

// 展示的折叠面板
const activeNames = ref(['contractInfo', 'systemInfo'])
</script>
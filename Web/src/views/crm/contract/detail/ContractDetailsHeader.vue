<!-- 合同详情头部组件-->
<template>
  <div>
    <div class="flex items-start justify-between">
      <div>
        <el-col>
          <el-row>
            <span class="text-xl font-bold">{{ contract.name }}</span>
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
      <el-descriptions-item :label="t('crm.contract.customerName')">
        {{ contract.customerName }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('crm.contract.totalPrice') + '（元）'">
        {{ erpPriceInputFormatter(contract.totalPrice) }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('crm.contract.orderDate')">
        {{ formatDate(contract.orderDate) }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('crm.contract.totalReceivablePrice') + '（元）'">
        {{ erpPriceInputFormatter(contract.totalReceivablePrice) }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('crm.contract.ownerUserName')">
        {{ contract.ownerUserName }}
      </el-descriptions-item>
    </el-descriptions>
  </ContentWrap>
</template>
<script lang="ts" setup>
import * as ContractApi from '@/api/crm/contract'
import { formatDate } from '@/utils/formatTime'
import { erpPriceInputFormatter } from '@/utils'

const { t } = useI18n() // 国际化

defineOptions({ name: 'ContractDetailsHeader' })
defineProps<{ contract: ContractApi.ContractVO }>()
</script>
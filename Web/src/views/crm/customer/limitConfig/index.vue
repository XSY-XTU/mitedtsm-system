<template>
  <doc-alert title="【客户】客户管理、公海客户" url="https://doc.iocoder.cn/crm/customer/" />
  <doc-alert title="【通用】数据权限" url="https://doc.iocoder.cn/crm/permission/" />

  <!-- 列表 -->
  <ContentWrap>
    <el-tabs>
      <el-tab-pane :label="t('limitConfig.quantityLimitTab')">
        <CustomerLimitConfigList :confType="LimitConfType.CUSTOMER_QUANTITY_LIMIT" />
      </el-tab-pane>
      <el-tab-pane :label="t('limitConfig.lockLimitTab')">
        <CustomerLimitConfigList :confType="LimitConfType.CUSTOMER_LOCK_LIMIT" />
      </el-tab-pane>
    </el-tabs>
  </ContentWrap>
</template>
<script setup lang="ts">
import CustomerLimitConfigList from './CustomerLimitConfigList.vue'
import { LimitConfType } from '@/api/crm/customer/limitConfig'

defineOptions({ name: 'CrmCustomerLimitConfig' })
const { t } = useI18n('crm.customer') // 国际化
</script>
<template>
  <ContentWrap>
    <el-collapse v-model="activeNames" class="">
      <el-collapse-item name="basicInfo">
        <template #title>
          <span class="text-base font-bold">{{ t('basicInfoTab') }}</span>
        </template>
        <el-descriptions :column="4">
          <el-descriptions-item :label="t('name')">
            {{ customer.name }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('source')">
            <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_SOURCE" :value="customer.source" />
          </el-descriptions-item>
          <el-descriptions-item :label="t('mobile')">{{ customer.mobile }}</el-descriptions-item>
          <el-descriptions-item :label="t('telephone')">{{ customer.telephone }}</el-descriptions-item>
          <el-descriptions-item :label="t('email')">{{ customer.email }}</el-descriptions-item>
          <el-descriptions-item :label="t('areaId')">
            {{ customer.areaName }} {{ customer.detailAddress }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('qq')">{{ customer.qq }}</el-descriptions-item>
          <el-descriptions-item :label="t('wechat')">{{ customer.wechat }}</el-descriptions-item>
          <el-descriptions-item :label="t('industryId')">
            <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_INDUSTRY" :value="customer.industryId" />
          </el-descriptions-item>
          <el-descriptions-item :label="t('level')">
            <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_LEVEL" :value="customer.level" />
          </el-descriptions-item>
          <el-descriptions-item :label="t('contactNextTime')">
            {{ formatDate(customer.contactNextTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('remark')">{{ customer.remark }}</el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
      <el-collapse-item name="systemInfo">
        <template #title>
          <span class="text-base font-bold">{{ t('common.systemInfo') }}</span>
        </template>
        <el-descriptions :column="4">
          <el-descriptions-item :label="t('ownerUserId')">{{ customer.ownerUserName }}</el-descriptions-item>
          <el-descriptions-item :label="t('lastContactContent')">
            {{ customer.contactLastContent }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('lastContactTime')">
            {{ formatDate(customer.contactLastTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="''">&nbsp;</el-descriptions-item>
          <el-descriptions-item :label="t('common.creator')">{{ customer.creatorName }}</el-descriptions-item>
          <el-descriptions-item :label="t('common.createTime')">
            {{ formatDate(customer.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('common.updateTime')">
            {{ formatDate(customer.updateTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
    </el-collapse>
  </ContentWrap>
</template>
<script lang="ts" setup>
import * as CustomerApi from '@/api/crm/customer'
import { DICT_TYPE } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'

defineOptions({ name: 'CrmCustomerDetailsInfo' })
const { t } = useI18n('crm.customer') // 国际化
const { customer } = defineProps<{
  customer: CustomerApi.CustomerVO // 客户明细
}>()

const activeNames = ref(['basicInfo', 'systemInfo']) // 展示的折叠面板
</script>
<style lang="scss" scoped></style>
<template>
  <ContentWrap>
    <el-collapse v-model="activeNames" class="">
      <el-collapse-item name="basicInfo">
        <template #title>
          <span class="text-base font-bold">{{ t('customer.basicInfoTab') }}</span>
        </template>
        <el-descriptions :column="4">
          <el-descriptions-item :label="t('clue.name')">
            {{ clue.name }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('customer.source')">
            <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_SOURCE" :value="clue.source" />
          </el-descriptions-item>
          <el-descriptions-item :label="t('customer.mobile')">{{ clue.mobile }}</el-descriptions-item>
          <el-descriptions-item :label="t('customer.telephone')">{{ clue.telephone }}</el-descriptions-item>
          <el-descriptions-item :label="t('customer.email')">{{ clue.email }}</el-descriptions-item>
          <el-descriptions-item :label="t('customer.detailAddress')">
            {{ clue.areaName }} {{ clue.detailAddress }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('customer.qq')">{{ clue.qq }}</el-descriptions-item>
          <el-descriptions-item :label="t('customer.wechat')">{{ clue.wechat }}</el-descriptions-item>
          <el-descriptions-item :label="t('customer.industryId')">
            <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_INDUSTRY" :value="clue.industryId" />
          </el-descriptions-item>
          <el-descriptions-item :label="t('customer.level')">
            <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_LEVEL" :value="clue.level" />
          </el-descriptions-item>
          <el-descriptions-item :label="t('clue.contactNextTime')">
            {{ formatDate(clue.contactNextTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('clue.remark')">{{ clue.remark }}</el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
      <el-collapse-item name="systemInfo">
        <template #title>
          <span class="text-base font-bold">{{ t('receivable.systemInfo') }}</span>
        </template>
        <el-descriptions :column="4">
          <el-descriptions-item :label="t('clue.ownerUserName')">{{ clue.ownerUserName }}</el-descriptions-item>
          <el-descriptions-item :label="t('clue.contactLastContent')">
            {{ clue.contactLastContent }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('clue.contactLastTime')">
            {{ formatDate(clue.contactLastTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="''">&nbsp;</el-descriptions-item>
          <el-descriptions-item :label="t('clue.creatorName')">{{ clue.creatorName }}</el-descriptions-item>
          <el-descriptions-item :label="t('clue.createTime')">
            {{ formatDate(clue.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('clue.updateTime')">
            {{ formatDate(clue.updateTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
    </el-collapse>
  </ContentWrap>
</template>
<script lang="ts" setup>
import * as ClueApi from '@/api/crm/clue'
import { DICT_TYPE } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'

defineOptions({ name: 'CrmClueDetailsInfo' })

const { t } = useI18n('crm') // 国际化

const { clue } = defineProps<{
  clue: ClueApi.ClueVO // 线索明细
}>()

const activeNames = ref(['basicInfo', 'systemInfo']) // 展示的折叠面板
</script>
<style lang="scss" scoped></style>
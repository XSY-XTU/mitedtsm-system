<template>
  <ContentWrap>
    <el-collapse v-model="activeNames">
      <el-collapse-item name="basicInfo">
        <template #title>
          <span class="text-base font-bold">{{ t('crm.customer.basicInfoTab') }}</span>
        </template>
        <el-descriptions :column="4">
          <el-descriptions-item :label="t('crm.contact.name')">{{ contact.name }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.contact.customerName')">{{ contact.customerName }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.contact.mobile')">{{ contact.mobile }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.contact.telephone')">{{ contact.telephone }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.contact.email')">{{ contact.email }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.contact.qq')">{{ contact.qq }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.contact.wechat')">{{ contact.wechat }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.contact.areaId')">
            {{ contact.areaName }} {{ contact.detailAddress }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.contact.post')">{{ contact.post }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.contact.parentName')">{{ contact.parentName }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.contact.master')">
            <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="contact.master" />
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.contact.sex')">
            <dict-tag :type="DICT_TYPE.SYSTEM_USER_SEX" :value="contact.sex" />
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.contact.contactNextTime')">
            {{ formatDate(contact.contactNextTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.contact.remark')">{{ contact.remark }}</el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
      <el-collapse-item name="systemInfo">
        <template #title>
          <span class="text-base font-bold">{{ t('common.systemInfo') }}</span>
        </template>
        <el-descriptions :column="4">
          <el-descriptions-item :label="t('crm.contact.ownerUserName')">{{ contact.ownerUserName }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.contact.contactLastContent')">
            {{ contact.contactLastContent }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.contact.contactLastTime')">
            {{ formatDate(contact.contactLastTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="''">&nbsp;</el-descriptions-item>
          <el-descriptions-item :label="t('crm.contact.creatorName')">{{ contact.creatorName }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.contact.createTime')">
            {{ formatDate(contact.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.contact.updateTime')">
            {{ formatDate(contact.updateTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
    </el-collapse>
  </ContentWrap>
</template>
<script setup lang="ts">
import * as ContactApi from '@/api/crm/contact'
import { DICT_TYPE } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'

const { t } = useI18n() // 国际化

const { contact } = defineProps<{
  contact: ContactApi.ContactVO
}>()

// 展示的折叠面板
const activeNames = ref(['basicInfo', 'systemInfo'])
</script>
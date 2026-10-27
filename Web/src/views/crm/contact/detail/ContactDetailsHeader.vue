<template>
  <div>
    <div class="flex items-start justify-between">
      <div>
        <el-col>
          <el-row>
            <span class="text-xl font-bold">{{ contact.name }}</span>
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
      <el-descriptions-item :label="t('crm.contact.customerName')">{{ contact.customerName }}</el-descriptions-item>
      <el-descriptions-item :label="t('crm.contact.post')">{{ contact.post }}</el-descriptions-item>
      <el-descriptions-item :label="t('crm.contact.mobile')">{{ contact.mobile }}</el-descriptions-item>
      <el-descriptions-item :label="t('crm.contact.createTime')">
        {{ formatDate(contact.createTime) }}
      </el-descriptions-item>
    </el-descriptions>
  </ContentWrap>
</template>
<script lang="ts" setup>
import * as ContactApi from '@/api/crm/contact'
import { formatDate } from '@/utils/formatTime'

const { t } = useI18n() // 国际化
const { contact } = defineProps<{ contact: ContactApi.ContactVO }>()
</script>
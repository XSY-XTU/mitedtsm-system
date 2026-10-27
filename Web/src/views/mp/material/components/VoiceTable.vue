<template>
  <el-table :data="props.list" stripe border v-loading="props.loading" style="margin-top: 10px" :table-layout="'auto'">
    <el-table-column :label="t('common.id')" align="center" prop="mediaId" />
    <el-table-column :label="t('material.fileName')" align="center" prop="name" />
    <el-table-column :label="t('material.typeVoice')" align="center">
      <template #default="scope">
        <WxVoicePlayer v-if="scope.row.url" :url="scope.row.url" />
      </template>
    </el-table-column>
    <el-table-column
      :label="t('material.uploadTime')"
      align="center"
      prop="createTime"
      :formatter="dateFormatter"
      min-width="180"
    >
      <template #default="scope">
        <span>{{ scope.row.createTime }}</span>
      </template>
    </el-table-column>
    <el-table-column :label="t('common.operation')" align="center" class-name="small-padding fixed-width">
      <template #default="scope">
        <el-button type="primary" link @click="emit('delete', scope.row.id)">
          <Icon icon="ep:download"  fixed="right" />{{ t('common.download') }}
        </el-button>
        <el-button
          type="primary"
          link
          @click="emit('delete', scope.row.id)"
          v-hasPermi="['mp:material:delete']"
        >
          <Icon icon="ep:delete" />{{ t('common.delete') }}
        </el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script lang="ts" setup>
import WxVoicePlayer from '@/views/mp/components/wx-voice-play'
import { dateFormatter } from '@/utils/formatTime'

const { t } = useI18n('mp') // 国际化

const props = defineProps<{
  list: any[]
  loading: boolean
}>()

const emit = defineEmits<{
  (e: 'delete', v: number)
}>()
</script>

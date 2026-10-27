<template>
  <el-table :data="props.list" stripe border v-loading="props.loading" style="margin-top: 10px" :table-layout="'auto'">
    <el-table-column :label="t('common.id')" align="center" prop="mediaId" />
    <el-table-column :label="t('material.fileName')" align="center" prop="name" />
    <el-table-column :label="t('material.videoTitle')" align="center" prop="title" />
    <el-table-column :label="t('material.videoIntroduction')" align="center" prop="introduction" />
    <el-table-column :label="t('material.typeVideo')" align="center">
      <template #default="scope">
        <WxVideoPlayer v-if="scope.row.url" :url="scope.row.url" />
      </template>
    </el-table-column>
    <el-table-column
      :label="t('material.uploadTime')"
      align="center"
      :formatter="dateFormatter"
      prop="createTime"
      min-width="180"
    >
      <template #default="scope">
        <span>{{ scope.row.createTime }}</span>
      </template>
    </el-table-column>
    <el-table-column :label="t('common.operation')" align="center" fixed="right" min-width="150">
      <template #default="scope">
        <el-button type="primary" link @click="handleDownload(scope.row.url)">
          <Icon icon="ep:download" />{{ t('common.download') }}
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
import WxVideoPlayer from '@/views/mp/components/wx-video-play'
import { dateFormatter } from '@/utils/formatTime'

const { t } = useI18n('mp') // 国际化

const props = defineProps<{
  list: any[]
  loading: boolean
}>()

const emit = defineEmits<{
  (e: 'delete', v: number)
  (e: 'download', v: string)
}>()

// 下载文件
const handleDownload = (url: string) => {
  window.open(url, '_blank')
}
</script>

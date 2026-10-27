<template>
  <div>
    <div v-if="item.event === 'subscribe'">
      <el-tag type="success">{{ t('message.eventSubscribe') }}</el-tag>
    </div>
    <div v-else-if="item.event === 'unsubscribe'">
      <el-tag type="danger">{{ t('message.eventUnsubscribe') }}</el-tag>
    </div>
    <div v-else-if="item.event === 'CLICK'">
      <el-tag>{{ t('message.eventClickMenu') }}</el-tag>
      【{{ item.eventKey }}】
    </div>
    <div v-else-if="item.event === 'VIEW'">
      <el-tag>{{ t('message.eventClickMenuLink') }}</el-tag>
      【{{ item.eventKey }}】
    </div>
    <div v-else-if="item.event === 'scancode_waitmsg'">
      <el-tag>{{ t('message.eventScanResult') }}</el-tag>
      【{{ item.eventKey }}】
    </div>
    <div v-else-if="item.event === 'scancode_push'">
      <el-tag>{{ t('message.eventScanResult') }}</el-tag>
      【{{ item.eventKey }}】
    </div>
    <div v-else-if="item.event === 'pic_sysphoto'">
      <el-tag>{{ t('message.eventSysPhoto') }}</el-tag>
    </div>
    <div v-else-if="item.event === 'pic_photo_or_album'">
      <el-tag>{{ t('message.eventPhotoOrAlbum') }}</el-tag>
    </div>
    <div v-else-if="item.event === 'pic_weixin'">
      <el-tag>{{ t('message.eventWeixinAlbum') }}</el-tag>
    </div>
    <div v-else-if="item.event === 'location_select'">
      <el-tag>{{ t('message.eventLocationSelect') }}</el-tag>
    </div>
    <div v-else-if="item.event === 'SCAN'">
      <el-tag>{{ t('message.eventScan') }}</el-tag>
    </div>
    <div v-else>
      <el-tag type="danger">{{ t('message.eventUnknown') }}</el-tag>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useI18n } from '@/hooks/web/useI18n'

const { t } = useI18n('mp')

const props = defineProps<{
  item: any
}>()

const item = ref(props.item)
</script>

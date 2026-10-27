<template>
  <div>
    <el-table v-loading="props.loading" :data="props.list" :table-layout="'auto'">
      <el-table-column
        :label="t('message.sendTime')"
        align="center"
        prop="createTime"
        min-width="180"
        :formatter="dateFormatter"
      />
      <el-table-column :label="t('message.messageType')" align="center" prop="type" min-width="80" />
      <el-table-column :label="t('message.sendFrom')" align="center" prop="sendFrom" min-width="80">
        <template #default="scope">
          <el-tag v-if="scope.row.sendFrom === 1" type="success">{{ t('message.sendFromUser') }}</el-tag>
          <el-tag v-else type="info">{{ t('message.sendFromMp') }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="t('user.openid')" align="center" prop="openid" min-width="300" />
      <el-table-column :label="t('message.content')" prop="content">
        <template #default="scope">
          <!-- 【事件】区域 -->
          <div v-if="scope.row.type === MsgType.Event && scope.row.event === 'subscribe'">
            <el-tag type="success">{{ t('message.eventSubscribe') }}</el-tag>
          </div>
          <div v-else-if="scope.row.type === MsgType.Event && scope.row.event === 'unsubscribe'">
            <el-tag type="danger">{{ t('message.eventUnsubscribe') }}</el-tag>
          </div>
          <div v-else-if="scope.row.type === MsgType.Event && scope.row.event === 'CLICK'">
            <el-tag>{{ t('message.eventClickMenu') }}</el-tag>
            【{{ scope.row.eventKey }}】
          </div>
          <div v-else-if="scope.row.type === MsgType.Event && scope.row.event === 'VIEW'">
            <el-tag>{{ t('message.eventClickMenuLink') }}</el-tag>
            【{{ scope.row.eventKey }}】
          </div>
          <div
            v-else-if="scope.row.type === MsgType.Event && scope.row.event === 'scancode_waitmsg'"
          >
            <el-tag>{{ t('message.eventScanResult') }}</el-tag>
            【{{ scope.row.eventKey }}】
          </div>
          <div v-else-if="scope.row.type === MsgType.Event && scope.row.event === 'scancode_push'">
            <el-tag>{{ t('message.eventScanResult') }}</el-tag>
            【{{ scope.row.eventKey }}】
          </div>
          <div v-else-if="scope.row.type === MsgType.Event && scope.row.event === 'pic_sysphoto'">
            <el-tag>{{ t('message.eventSysPhoto') }}</el-tag>
          </div>
          <div
            v-else-if="scope.row.type === MsgType.Event && scope.row.event === 'pic_photo_or_album'"
          >
            <el-tag>{{ t('message.eventPhotoOrAlbum') }}</el-tag>
          </div>
          <div v-else-if="scope.row.type === MsgType.Event && scope.row.event === 'pic_weixin'">
            <el-tag>{{ t('message.eventWeixinAlbum') }}</el-tag>
          </div>
          <div
            v-else-if="scope.row.type === MsgType.Event && scope.row.event === 'location_select'"
          >
            <el-tag>{{ t('message.eventLocationSelect') }}</el-tag>
          </div>
          <div v-else-if="scope.row.type === MsgType.Event && scope.row.event === 'SCAN'">
            <el-tag type="success">{{ t('message.eventScan') }}</el-tag>
          </div>
          <div v-else-if="scope.row.type === MsgType.Event">
            <el-tag type="danger">{{ t('message.eventUnknown') }}</el-tag>
          </div>
          <!-- 【消息】区域 -->
          <div v-else-if="scope.row.type === MsgType.Text">{{ scope.row.content }}</div>
          <div v-else-if="scope.row.type === MsgType.Voice">
            <wx-voice-player :url="scope.row.mediaUrl" :content="scope.row.recognition" />
          </div>
          <div v-else-if="scope.row.type === MsgType.Image">
            <a target="_blank" :href="scope.row.mediaUrl">
              <img :src="scope.row.mediaUrl" style="width: 100px" />
            </a>
          </div>
          <div v-else-if="scope.row.type === MsgType.Video || scope.row.type === 'shortvideo'">
            <wx-video-player :url="scope.row.mediaUrl" style="margin-top: 10px" />
          </div>
          <div v-else-if="scope.row.type === MsgType.Link">
            <el-tag>{{ t('message.linkTag') }}</el-tag>
            ：
            <a :href="scope.row.url" target="_blank">{{ scope.row.title }}</a>
          </div>
          <div v-else-if="scope.row.type === MsgType.Location">
            <WxLocation
              :label="scope.row.label"
              :location-y="scope.row.locationY"
              :location-x="scope.row.locationX"
            />
          </div>
          <div v-else-if="scope.row.type === MsgType.Music">
            <WxMusic
              :title="scope.row.title"
              :description="scope.row.description"
              :thumb-media-url="scope.row.thumbMediaUrl"
              :music-url="scope.row.musicUrl"
              :hq-music-url="scope.row.hqMusicUrl"
            />
          </div>
          <div v-else-if="scope.row.type === MsgType.News">
            <WxNews :articles="scope.row.articles" />
          </div>
          <div v-else>
            <el-tag type="danger">{{ t('message.typeUnknown') }}</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column :label="t('common.operation')" align="center" class-name="small-padding fixed-width" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="emit('send', scope.row.userId)"
            v-hasPermi="['mp:message:send']"
          >
            {{ t('message.message') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
  </div>
</template>

<script lang="ts" setup>
import WxVideoPlayer from '@/views/mp/components/wx-video-play'
import WxVoicePlayer from '@/views/mp/components/wx-voice-play'
import WxLocation from '@/views/mp/components/wx-location'
import WxMusic from '@/views/mp/components/wx-music'
import WxNews from '@/views/mp/components/wx-news'
import { dateFormatter } from '@/utils/formatTime'
import { MsgType } from '@/views/mp/components/wx-msg/types'

const { t } = useI18n('mp') // 国际化

const props = defineProps({
  list: {
    type: Array,
    required: true
  },
  loading: {
    type: Boolean,
    required: true
  }
})

const emit = defineEmits<{ (e: 'send', v: number) }>()
</script>

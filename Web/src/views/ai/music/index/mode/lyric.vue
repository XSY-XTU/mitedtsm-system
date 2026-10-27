<template>
  <div class="">
    <Title :title="t('music.lyricsTitle')" :desc="t('music.lyricsHint')">
      <el-input
        v-model="formData.lyric"
        :autosize="{ minRows: 6, maxRows: 6}"
        resize="none"
        type="textarea"
        maxlength="1200"
        show-word-limit
        :placeholder="t('music.lyricsPlaceholder')"
      />
    </Title>

    <Title :title="t('music.musicStyle')">
      <el-space class="flex-wrap">
        <el-tag v-for="tag in tags" :key="tag" round class="mb-8px">{{tag}}</el-tag>
      </el-space>

      <el-button
        :type="showCustom ? 'primary': 'default'"
        round
        size="small"
        class="mb-6px"
        @click="showCustom = !showCustom"
      >{{ t('music.customStyle') }}
      </el-button>
    </Title>

    <Title v-show="showCustom" :desc="t('music.musicStyleHint')" class="-mt-12px">
      <el-input
        v-model="formData.style"
        :autosize="{ minRows: 4, maxRows: 4}"
        resize="none"
        type="textarea"
        maxlength="256"
        show-word-limit
        :placeholder="t('music.musicStylePlaceholder')"
      />
    </Title>

    <Title :title="t('music.songName')">
      <el-input v-model="formData.name" :placeholder="t('music.songNamePlaceholder')"/>
    </Title>

    <Title :title="t('music.version')">
      <el-select v-model="formData.version" :placeholder="t('music.pleaseSelect')">
        <el-option
          v-for="item in [{
            value: '3',
            label: 'V3'
          }, {
            value: '2',
            label: 'V2'
          }]"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
    </Title>
  </div>
</template>

<script lang="ts" setup>
import Title from '../title/index.vue'
defineOptions({ name: 'Lyric' })

const { t } = useI18n('ai')

const tags = ['rock', 'punk', 'jazz', 'soul', 'country', 'kidsmusic', 'pop']

const showCustom = ref(false)

const formData = reactive({
  lyric: '',
  style: '',
  name: '',
  version: ''
})

defineExpose({
  formData
})
</script>

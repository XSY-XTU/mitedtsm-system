<template>
  <div>
    <Title :title="t('music.musicDescTitle')" :desc="t('music.musicDescHint')">
      <el-input
        v-model="formData.desc"
        :autosize="{ minRows: 6, maxRows: 6}"
        resize="none"
        type="textarea"
        maxlength="1200"
        show-word-limit
        :placeholder="t('music.musicDescPlaceholder')"
      />
    </Title>

    <Title :title="t('music.instrumental')" :desc="t('music.instrumentalHint')">
      <template #extra>
        <el-switch v-model="formData.pure" size="small"/>
      </template>
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

defineOptions({ name: 'Desc' })

const { t } = useI18n('ai')

const formData = reactive({
  desc: '',
  pure: false,
  version: '3'
})

defineExpose({
  formData
})

</script>

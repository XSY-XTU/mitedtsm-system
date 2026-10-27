<template>
  <el-form-item :label="t('sinkConfig.requestUrl')" prop="config.url">
    <el-input v-model="urlPath" :placeholder="t('sinkConfig.requestUrlPlaceholder')">
      <template #prepend>
        <el-select v-model="urlPrefix" placeholder="Select" style="width: 115px">
          <!--suppress HttpUrlsUsage -->
          <el-option label="http://" value="http://" />
          <el-option label="https://" value="https://" />
        </el-select>
      </template>
    </el-input>
  </el-form-item>
  <el-form-item :label="t('sinkConfig.requestMethod')" prop="config.method">
    <el-select v-model="config.method" :placeholder="t('sinkConfig.requestMethodPlaceholder')">
      <el-option label="GET" value="GET" />
      <el-option label="POST" value="POST" />
      <el-option label="PUT" value="PUT" />
      <el-option label="DELETE" value="DELETE" />
    </el-select>
  </el-form-item>
  <el-form-item :label="t('sinkConfig.requestHeaders')" prop="config.headers">
    <key-value-editor v-model="config.headers" :add-button-text="t('sinkConfig.addHeader')" />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.requestParams')" prop="config.query">
    <key-value-editor v-model="config.query" :add-button-text="t('sinkConfig.addParam')" />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.requestBody')" prop="config.body">
    <el-input v-model="config.body" :placeholder="t('sinkConfig.requestBodyPlaceholder')" type="textarea" />
  </el-form-item>
</template>

<script lang="ts" setup>
import { HttpConfig, IotDataSinkTypeEnum } from '@/api/iot/rule/data/sink'
import { useVModel } from '@vueuse/core'
import { isEmpty } from '@/utils/is'
import KeyValueEditor from './components/KeyValueEditor.vue'

defineOptions({ name: 'HttpConfigForm' })

const { t } = useI18n('iot')

const props = defineProps<{
  modelValue: any
}>()
const emit = defineEmits(['update:modelValue'])
const config = useVModel(props, 'modelValue', emit) as Ref<HttpConfig>

// noinspection HttpUrlsUsage
/** URL处理 */
const urlPrefix = ref('http://')
const urlPath = ref('')
const fullUrl = computed(() => {
  return urlPath.value ? urlPrefix.value + urlPath.value : ''
})

/** 监听 URL 变化 */
watch([urlPrefix, urlPath], () => {
  config.value.url = fullUrl.value
})

/** 组件初始化 */
onMounted(() => {
  if (!isEmpty(config.value)) {
    // 初始化 URL
    if (config.value.url) {
      if (config.value.url.startsWith('https://')) {
        urlPrefix.value = 'https://'
        urlPath.value = config.value.url.substring(8)
      } else if (config.value.url.startsWith('http://')) {
        urlPrefix.value = 'http://'
        urlPath.value = config.value.url.substring(7)
      } else {
        urlPath.value = config.value.url
      }
    }
    return
  }

  config.value = {
    type: IotDataSinkTypeEnum.HTTP + '', // 序列化成对应类型时使用
    url: '',
    method: 'POST',
    headers: {},
    query: {},
    body: ''
  }
})
</script>

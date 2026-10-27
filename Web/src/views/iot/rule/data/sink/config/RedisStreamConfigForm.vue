<template>
  <el-form-item :label="t('sinkConfig.host')" prop="config.host">
    <el-input v-model="config.host" :placeholder="t('sinkConfig.hostPlaceholder')" />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.port')" prop="config.port">
    <el-input-number
      v-model="config.port"
      :max="65535"
      :min="1"
      controls-position="right"
      :placeholder="t('sinkConfig.portPlaceholder')"
    />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.password')" prop="config.password">
    <el-input v-model="config.password" :placeholder="t('sinkConfig.passwordPlaceholder')" show-password type="password" />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.database')" prop="config.database">
    <el-input-number
      v-model="config.database"
      :max="15"
      :min="0"
      controls-position="right"
      :placeholder="t('sinkConfig.databasePlaceholder')"
    />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.topic')" prop="config.topic">
    <el-input v-model="config.topic" :placeholder="t('sinkConfig.topicPlaceholder')" />
  </el-form-item>
</template>
<script lang="ts" setup>
import { IotDataSinkTypeEnum, RedisStreamMQConfig } from '@/api/iot/rule/data/sink'
import { useVModel } from '@vueuse/core'
import { isEmpty } from '@/utils/is'

defineOptions({ name: 'RedisStreamMQConfigForm' })

const { t } = useI18n('iot')

const props = defineProps<{
  modelValue: any
}>()
const emit = defineEmits(['update:modelValue'])
const config = useVModel(props, 'modelValue', emit) as Ref<RedisStreamMQConfig>

/** 组件初始化 */
onMounted(() => {
  if (!isEmpty(config.value)) {
    return
  }
  config.value = {
    type: IotDataSinkTypeEnum.REDIS_STREAM + '', // 序列化成对应类型时使用
    host: '',
    port: 6379,
    password: '',
    database: 0,
    topic: ''
  }
})
</script>

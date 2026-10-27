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
  <el-form-item :label="t('sinkConfig.virtualHost')" prop="config.virtualHost">
    <el-input v-model="config.virtualHost" :placeholder="t('sinkConfig.virtualHostPlaceholder')" />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.username')" prop="config.username">
    <el-input v-model="config.username" :placeholder="t('sinkConfig.usernamePlaceholder')" />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.password')" prop="config.password">
    <el-input v-model="config.password" :placeholder="t('sinkConfig.passwordPlaceholder')" show-password type="password" />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.exchange')" prop="config.exchange">
    <el-input v-model="config.exchange" :placeholder="t('sinkConfig.exchangePlaceholder')" />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.routingKey')" prop="config.routingKey">
    <el-input v-model="config.routingKey" :placeholder="t('sinkConfig.routingKeyPlaceholder')" />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.queue')" prop="config.queue">
    <el-input v-model="config.queue" :placeholder="t('sinkConfig.queuePlaceholder')" />
  </el-form-item>
</template>
<script lang="ts" setup>
import { IotDataSinkTypeEnum, RabbitMQConfig } from '@/api/iot/rule/data/sink'
import { useVModel } from '@vueuse/core'
import { isEmpty } from '@/utils/is'

defineOptions({ name: 'RabbitMQConfigForm' })

const { t } = useI18n('iot')

const props = defineProps<{
  modelValue: any
}>()
const emit = defineEmits(['update:modelValue'])
const config = useVModel(props, 'modelValue', emit) as Ref<RabbitMQConfig>

/** 组件初始化 */
onMounted(() => {
  if (!isEmpty(config.value)) {
    return
  }
  config.value = {
    type: IotDataSinkTypeEnum.RABBITMQ + '', // 序列化成对应类型时使用
    host: '',
    port: 5672,
    virtualHost: '/',
    username: '',
    password: '',
    exchange: '',
    routingKey: '',
    queue: ''
  }
})
</script>

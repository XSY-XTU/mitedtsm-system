<template>
  <el-form-item :label="t('sinkConfig.wsServerUrl')" prop="config.serverUrl">
    <el-input
      v-model="config.serverUrl"
      :placeholder="t('sinkConfig.wsServerUrlPlaceholder')"
    />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.connectTimeout')" prop="config.connectTimeoutMs">
    <el-input-number
      v-model="config.connectTimeoutMs"
      :min="1000"
      :step="1000"
      controls-position="right"
    />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.sendTimeout')" prop="config.sendTimeoutMs">
    <el-input-number
      v-model="config.sendTimeoutMs"
      :min="1000"
      :step="1000"
      controls-position="right"
    />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.heartbeatInterval')" prop="config.heartbeatIntervalMs">
    <el-input-number
      v-model="config.heartbeatIntervalMs"
      :min="0"
      :step="1000"
      controls-position="right"
      :placeholder="t('sinkConfig.heartbeatIntervalPlaceholder')"
    />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.heartbeatMessage')" prop="config.heartbeatMessage">
    <el-input v-model="config.heartbeatMessage" :placeholder="t('sinkConfig.heartbeatMessagePlaceholder')" />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.subprotocols')" prop="config.subprotocols">
    <el-input v-model="config.subprotocols" :placeholder="t('sinkConfig.subprotocolsPlaceholder')" />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.customHeaders')" prop="config.customHeaders">
    <el-input
      v-model="config.customHeaders"
      type="textarea"
      :placeholder="t('sinkConfig.customHeadersPlaceholder')"
    />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.verifySslCert')" prop="config.verifySslCert">
    <el-switch v-model="config.verifySslCert" />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.dataFormat')" prop="config.dataFormat">
    <el-select v-model="config.dataFormat" :placeholder="t('sinkConfig.dataFormatPlaceholder')">
      <el-option label="JSON" value="JSON" />
      <el-option label="TEXT" value="TEXT" />
    </el-select>
  </el-form-item>
  <el-form-item :label="t('sinkConfig.reconnectInterval')" prop="config.reconnectIntervalMs">
    <el-input-number
      v-model="config.reconnectIntervalMs"
      :min="1000"
      :step="1000"
      controls-position="right"
    />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.maxReconnectAttempts')" prop="config.maxReconnectAttempts">
    <el-input-number v-model="config.maxReconnectAttempts" :min="0" controls-position="right" />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.enableCompression')" prop="config.enableCompression">
    <el-switch v-model="config.enableCompression" />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.sendRetryCount')" prop="config.sendRetryCount">
    <el-input-number v-model="config.sendRetryCount" :min="0" controls-position="right" />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.sendRetryInterval')" prop="config.sendRetryIntervalMs">
    <el-input-number
      v-model="config.sendRetryIntervalMs"
      :min="100"
      :step="500"
      controls-position="right"
    />
  </el-form-item>
</template>
<script lang="ts" setup>
import { IotDataSinkTypeEnum, WebSocketConfig } from '@/api/iot/rule/data/sink'
import { useVModel } from '@vueuse/core'
import { isEmpty } from '@/utils/is'

defineOptions({ name: 'WebSocketConfigForm' })

const { t } = useI18n('iot')

const props = defineProps<{
  modelValue: any
}>()
const emit = defineEmits(['update:modelValue'])
const config = useVModel(props, 'modelValue', emit) as Ref<WebSocketConfig>

/** 组件初始化 */
onMounted(() => {
  if (!isEmpty(config.value)) {
    return
  }
  config.value = {
    type: IotDataSinkTypeEnum.WEBSOCKET + '',
    serverUrl: '',
    connectTimeoutMs: 5000,
    sendTimeoutMs: 10000,
    heartbeatIntervalMs: 30000,
    heartbeatMessage: '{"type":"heartbeat"}',
    subprotocols: '',
    customHeaders: '',
    verifySslCert: true,
    dataFormat: 'JSON',
    reconnectIntervalMs: 5000,
    maxReconnectAttempts: 3,
    enableCompression: false,
    sendRetryCount: 1,
    sendRetryIntervalMs: 1000
  }
})
</script>

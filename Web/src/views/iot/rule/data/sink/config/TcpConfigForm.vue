<template>
  <el-form-item :label="t('sinkConfig.tcpServerAddress')" prop="config.host">
    <el-input v-model="config.host" :placeholder="t('sinkConfig.tcpServerAddressPlaceholder')" />
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
  <el-form-item :label="t('sinkConfig.connectTimeout')" prop="config.connectTimeoutMs">
    <el-input-number
      v-model="config.connectTimeoutMs"
      :min="1000"
      :step="1000"
      controls-position="right"
      :placeholder="t('sinkConfig.connectTimeoutPlaceholder')"
    />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.readTimeout')" prop="config.readTimeoutMs">
    <el-input-number
      v-model="config.readTimeoutMs"
      :min="1000"
      :step="1000"
      controls-position="right"
      :placeholder="t('sinkConfig.readTimeoutPlaceholder')"
    />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.enableSsl')" prop="config.ssl">
    <el-switch v-model="config.ssl" />
  </el-form-item>
  <el-form-item v-if="config.ssl" :label="t('sinkConfig.sslCertPath')" prop="config.sslCertPath">
    <el-input v-model="config.sslCertPath" :placeholder="t('sinkConfig.sslCertPathPlaceholder')" />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.dataFormat')" prop="config.dataFormat">
    <el-select v-model="config.dataFormat" :placeholder="t('sinkConfig.dataFormatPlaceholder')">
      <el-option label="JSON" value="JSON" />
      <el-option label="BINARY" value="BINARY" />
    </el-select>
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
  <el-form-item :label="t('sinkConfig.reconnectInterval')" prop="config.reconnectIntervalMs">
    <el-input-number
      v-model="config.reconnectIntervalMs"
      :min="1000"
      :step="1000"
      controls-position="right"
      :placeholder="t('sinkConfig.reconnectIntervalPlaceholder')"
    />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.maxReconnectAttempts')" prop="config.maxReconnectAttempts">
    <el-input-number
      v-model="config.maxReconnectAttempts"
      :min="0"
      controls-position="right"
      :placeholder="t('sinkConfig.maxReconnectAttemptsPlaceholder')"
    />
  </el-form-item>
</template>
<script lang="ts" setup>
import { IotDataSinkTypeEnum, TcpConfig } from '@/api/iot/rule/data/sink'
import { useVModel } from '@vueuse/core'
import { isEmpty } from '@/utils/is'

defineOptions({ name: 'TcpConfigForm' })

const { t } = useI18n('iot')

const props = defineProps<{
  modelValue: any
}>()
const emit = defineEmits(['update:modelValue'])
const config = useVModel(props, 'modelValue', emit) as Ref<TcpConfig>

/** 组件初始化 */
onMounted(() => {
  if (!isEmpty(config.value)) {
    return
  }
  config.value = {
    type: IotDataSinkTypeEnum.TCP + '',
    host: '',
    port: 8080,
    connectTimeoutMs: 5000,
    readTimeoutMs: 10000,
    ssl: false,
    sslCertPath: '',
    dataFormat: 'JSON',
    heartbeatIntervalMs: 30000,
    reconnectIntervalMs: 5000,
    maxReconnectAttempts: 3
  }
})
</script>

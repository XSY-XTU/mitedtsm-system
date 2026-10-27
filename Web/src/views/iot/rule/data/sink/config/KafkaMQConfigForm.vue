<template>
  <el-form-item :label="t('sinkConfig.bootstrapServers')" prop="config.bootstrapServers">
    <el-input v-model="config.bootstrapServers" :placeholder="t('sinkConfig.bootstrapServersPlaceholder')" />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.username')" prop="config.username">
    <el-input v-model="config.username" :placeholder="t('sinkConfig.usernamePlaceholder')" />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.password')" prop="config.password">
    <el-input v-model="config.password" :placeholder="t('sinkConfig.passwordPlaceholder')" show-password type="password" />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.enableSsl')" prop="config.ssl">
    <el-switch v-model="config.ssl" />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.topic')" prop="config.topic">
    <el-input v-model="config.topic" :placeholder="t('sinkConfig.topicPlaceholder')" />
  </el-form-item>
</template>
<script lang="ts" setup>
import { IotDataSinkTypeEnum, KafkaMQConfig } from '@/api/iot/rule/data/sink'
import { useVModel } from '@vueuse/core'
import { isEmpty } from '@/utils/is'

defineOptions({ name: 'KafkaMQConfigForm' })

const { t } = useI18n('iot')

const props = defineProps<{
  modelValue: any
}>()
const emit = defineEmits(['update:modelValue'])
const config = useVModel(props, 'modelValue', emit) as Ref<KafkaMQConfig>

/** 组件初始化 */
onMounted(() => {
  if (!isEmpty(config.value)) {
    return
  }
  config.value = {
    type: IotDataSinkTypeEnum.KAFKA + '', // 序列化成对应类型时使用
    bootstrapServers: '',
    username: '',
    password: '',
    ssl: false,
    topic: ''
  }
})
</script>

<template>
  <el-form-item :label="t('sinkConfig.nameServer')" prop="config.nameServer">
    <el-input
      v-model="config.nameServer"
      :placeholder="t('sinkConfig.nameServerPlaceholder')"
    />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.accessKey')" prop="config.accessKey">
    <el-input v-model="config.accessKey" :placeholder="t('sinkConfig.accessKeyPlaceholder')" />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.secretKey')" prop="config.secretKey">
    <el-input
      v-model="config.secretKey"
      :placeholder="t('sinkConfig.secretKeyPlaceholder')"
      show-password
      type="password"
    />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.consumerGroup')" prop="config.group">
    <el-input v-model="config.group" :placeholder="t('sinkConfig.consumerGroupPlaceholder')" />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.topic')" prop="config.topic">
    <el-input v-model="config.topic" :placeholder="t('sinkConfig.topicPlaceholder')" />
  </el-form-item>
  <el-form-item :label="t('sinkConfig.tags')" prop="config.tags">
    <el-input v-model="config.tags" :placeholder="t('sinkConfig.tagsPlaceholder')" />
  </el-form-item>
</template>
<script lang="ts" setup>
import { IotDataSinkTypeEnum, RocketMQConfig } from '@/api/iot/rule/data/sink'
import { useVModel } from '@vueuse/core'
import { isEmpty } from '@/utils/is'

defineOptions({ name: 'RocketMQConfigForm' })

const { t } = useI18n('iot')

const props = defineProps<{
  modelValue: any
}>()
const emit = defineEmits(['update:modelValue'])
const config = useVModel(props, 'modelValue', emit) as Ref<RocketMQConfig>

/** 组件初始化 */
onMounted(() => {
  if (!isEmpty(config.value)) {
    return
  }
  config.value = {
    type: IotDataSinkTypeEnum.ROCKETMQ + '', // 序列化成对应类型时使用
    nameServer: '',
    accessKey: '',
    secretKey: '',
    group: '',
    topic: '',
    tags: ''
  }
})
</script>

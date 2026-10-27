<!-- 产品的物模型表单（event 项） -->
<template>
  <el-form-item
    :rules="[{ required: true, message: t('thing.model.eventTypeRequired'), trigger: 'change' }]"
    :label="t('thing.model.eventType')"
    prop="event.type"
  >
    <el-radio-group v-model="thingModelEvent.type">
      <el-radio
        v-for="eventType in Object.values(IoTThingModelEventTypeEnum)"
        :key="eventType.value"
        :value="eventType.value"
      >
        {{ eventType.label }}
      </el-radio>
    </el-radio-group>
  </el-form-item>
  <el-form-item :label="t('thing.model.outputParams')">
    <ThingModelInputOutputParam
      v-model="thingModelEvent.outputParams"
      :direction="IoTThingModelParamDirectionEnum.OUTPUT"
    />
  </el-form-item>
</template>

<script lang="ts" setup>
import ThingModelInputOutputParam from './ThingModelInputOutputParam.vue'
import { useVModel } from '@vueuse/core'
import { ThingModelEvent } from '@/api/iot/thingmodel'
import { isEmpty } from '@/utils/is'
import {
  IoTThingModelEventTypeEnum,
  IoTThingModelParamDirectionEnum
} from '@/views/iot/utils/constants'

/** IoT 物模型事件 */
defineOptions({ name: 'ThingModelEvent' })

const { t } = useI18n('iot') // 国际化

const props = defineProps<{ modelValue: any; isStructDataSpecs?: boolean }>()
const emits = defineEmits(['update:modelValue'])
const thingModelEvent = useVModel(props, 'modelValue', emits) as Ref<ThingModelEvent>

// 默认选中，INFO 信息
watch(
  () => thingModelEvent.value.type,
  (val: string) =>
    isEmpty(val) && (thingModelEvent.value.type = IoTThingModelEventTypeEnum.INFO.value),
  { immediate: true }
)
</script>

<style lang="scss" scoped>
:deep(.el-form-item) {
  .el-form-item {
    margin-bottom: 0;
  }
}
</style>

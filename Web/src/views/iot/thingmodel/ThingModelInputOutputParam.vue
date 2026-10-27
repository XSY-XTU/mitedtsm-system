<!-- 产品的物模型表单（event、service 项里的参数） -->
<template>
  <div
    v-for="(item, index) in thingModelParams"
    :key="index"
    class="w-1/1 param-item flex justify-between px-10px mb-10px"
  >
    <span>{{ t('iot.thingModel.paramName') }}：{{ item.name }}</span>
    <div class="btn">
      <el-button link type="primary" @click="openParamForm(item)">{{ t('common.edit') }}</el-button>
      <el-divider direction="vertical" />
      <el-button link type="danger" @click="deleteParamItem(index)">{{ t('common.delete') }}</el-button>
    </div>
  </div>
  <el-button link type="primary" @click="openParamForm(null)">+{{ t('common.add') }}</el-button>

  <!-- param 表单 -->
  <Dialog v-model="dialogVisible" :title="t('iot.thingModel.addParam')" append-to-body>
    <el-form
      ref="paramFormRef"
      v-loading="formLoading"
      :model="formData"
      :rules="ThingModelFormRules"
      label-width="100px"
    >
      <el-form-item :label="t('iot.thingModel.paramName')" prop="name">
        <el-input v-model="formData.name" :placeholder="t('iot.thingModel.paramNamePlaceholder')" />
      </el-form-item>
      <el-form-item :label="t('iot.thingModel.identifier')" prop="identifier">
        <el-input v-model="formData.identifier" :placeholder="t('iot.thingModel.identifierPlaceholder')" />
      </el-form-item>
      <!-- 属性配置 -->
      <ThingModelProperty v-model="formData.property" is-params />
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">{{ t('common.confirm') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import { useVModel } from '@vueuse/core'
import ThingModelProperty from './ThingModelProperty.vue'
import { isEmpty } from '@/utils/is'
import { IoTDataSpecsDataTypeEnum } from '@/views/iot/utils/constants'
import { ThingModelFormRules } from '@/api/iot/thingmodel'

/** 输入输出参数配置组件 */
defineOptions({ name: 'ThingModelInputOutputParam' })

const { t } = useI18n() // 国际化
const props = defineProps<{ modelValue: any; direction: string }>()
const emits = defineEmits(['update:modelValue'])
const thingModelParams = useVModel(props, 'modelValue', emits) as Ref<any[]>
const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const paramFormRef = ref() // 表单 ref
const formData = ref<any>({
  dataType: IoTDataSpecsDataTypeEnum.INT,
  property: {
    dataType: IoTDataSpecsDataTypeEnum.INT,
    dataSpecs: {
      dataType: IoTDataSpecsDataTypeEnum.INT
    }
  }
})

/** 打开 param 表单 */
const openParamForm = (val: any) => {
  dialogVisible.value = true
  resetForm()
  if (isEmpty(val)) {
    return
  }
  // 编辑时回显数据
  formData.value = {
    identifier: val.identifier,
    name: val.name,
    description: val.description,
    property: {
      dataType: val.dataType,
      dataSpecs: val.dataSpecs,
      dataSpecsList: val.dataSpecsList
    }
  }
}

/** 删除 param 项 */
const deleteParamItem = (index: number) => {
  thingModelParams.value.splice(index, 1)
}

/** 添加参数 */
const submitForm = async () => {
  // 初始化参数列表
  if (isEmpty(thingModelParams.value)) {
    thingModelParams.value = []
  }
  // 校验参数
  await paramFormRef.value.validate()
  try {
    // 构建数据对象
    const data = unref(formData)
    const item = {
      identifier: data.identifier,
      name: data.name,
      description: data.description,
      dataType: data.property.dataType,
      paraOrder: 0, // TODO @puhui999: 先写死默认看看后续
      direction: props.direction,
      dataSpecs:
        !!data.property.dataSpecs && Object.keys(data.property.dataSpecs).length > 1
          ? data.property.dataSpecs
          : undefined,
      dataSpecsList: isEmpty(data.property.dataSpecsList) ? undefined : data.property.dataSpecsList
    }

    // 新增或修改同 identifier 的参数
    const existingIndex = thingModelParams.value.findIndex(
      (spec) => spec.identifier === data.identifier
    )
    if (existingIndex > -1) {
      thingModelParams.value[existingIndex] = item
    } else {
      thingModelParams.value.push(item)
    }
  } finally {
    dialogVisible.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    dataType: IoTDataSpecsDataTypeEnum.INT,
    property: {
      dataType: IoTDataSpecsDataTypeEnum.INT,
      dataSpecs: {
        dataType: IoTDataSpecsDataTypeEnum.INT
      }
    }
  }
  paramFormRef.value?.resetFields()
}
</script>

<style lang="scss" scoped>
.param-item {
  background-color: #e4f2fd;
}
</style>

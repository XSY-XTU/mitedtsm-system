<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="auto"
      v-loading="formLoading"
    >
      <el-form-item :label="t('processExpression.name')" prop="name">
        <el-input v-model="formData.name" :placeholder="t('processExpression.namePlaceholder')" />
      </el-form-item>
      <el-form-item :label="t('processExpression.status')" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio
            v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
            :key="dict.value"
            :value="dict.value"
          >
            {{ dict.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item :label="t('processExpression.expression')" prop="expression">
        <el-input type="textarea" v-model="formData.expression" :placeholder="t('processExpression.expressionPlaceholder')" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">{{ t('common.confirm') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { ProcessExpressionApi, ProcessExpressionVO } from '@/api/bpm/processExpression'
import { CommonStatusEnum } from '@/utils/constants'

/** BPM 流程表达式表单 */
defineOptions({ name: 'ProcessExpressionForm' })

const { t } = useI18n('bpm') // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  name: undefined,
  status: undefined,
  expression: undefined
})
const formRules = reactive({
  name: [{ required: true, message: t('processExpression.nameRequired'), trigger: 'blur' }],
  status: [{ required: true, message: t('processExpression.statusRequired'), trigger: 'blur' }],
  expression: [{ required: true, message: t('processExpression.expressionRequired'), trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await ProcessExpressionApi.getProcessExpression(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as ProcessExpressionVO
    if (formType.value === 'create') {
      await ProcessExpressionApi.createProcessExpression(data)
      message.success(t('common.createSuccess'))
    } else {
      await ProcessExpressionApi.updateProcessExpression(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    name: undefined,
    status: CommonStatusEnum.ENABLE,
    expression: undefined
  }
  formRef.value?.resetFields()
}
</script>

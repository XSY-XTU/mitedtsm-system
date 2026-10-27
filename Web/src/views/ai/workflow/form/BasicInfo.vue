<template>
  <el-form ref="formRef" :model="modelData" :rules="formRules" label-width="140px">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item :label="tAi('workflow.processCode')" prop="code">
          <el-input v-model="modelData.code" :placeholder="tAi('workflow.processCodePlaceholder')" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item :label="tAi('workflow.processName')" prop="name">
          <el-input v-model="modelData.name" :placeholder="tAi('workflow.processNamePlaceholder')" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item :label="tAi('workflow.status')" prop="status">
          <el-select v-model="modelData.status" :placeholder="tAi('workflow.statusPlaceholder')">
            <el-option
              v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item :label="tAi('workflow.remark')" prop="remark">
          <el-input v-model="modelData.remark" :rows="2" type="textarea" :placeholder="tAi('workflow.remarkPlaceholder')" />
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>
<script lang="ts" setup>
import { FormRules } from 'element-plus'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'

const { t } = useI18n()
const tAi = useI18n('ai').t

const modelData = defineModel<any>()

const formRef = ref() // 表单 Ref
const formRules = reactive<FormRules>({
  code: [{ required: true, message: tAi('workflow.processCodeRequired'), trigger: 'blur' }],
  name: [{ required: true, message: tAi('workflow.processNameRequired'), trigger: 'blur' }],
  status: [{ required: true, message: tAi('workflow.statusRequired'), trigger: 'change' }]
})

/** 表单校验 */
const validate = async () => {
  await formRef.value?.validate()
}
defineExpose({
  validate
})
</script>
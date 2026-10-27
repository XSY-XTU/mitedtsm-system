<template>
  <el-form
    ref="formRef"
    :model="formData"
    :rules="formRules"
    label-width="auto"
    v-loading="formLoading"
  >
    <el-row>
      <el-col :span="12">
        <el-form-item :label="t('codegenDemo.name')" prop="name">
          <el-input v-model="formData.name" :placeholder="t('codegenDemo.namePlaceholder')" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item :label="t('codegenDemo.demo03.teacher')" prop="teacher">
          <el-input v-model="formData.teacher" :placeholder="t('codegenDemo.demo03.teacherPlaceholder')" />
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>
<script setup lang="ts">
import { Demo03StudentApi } from '@/api/infra/demo/demo03/normal'

const { t } = useI18n('infra') // 国际化

const props = defineProps<{
  studentId: number // 学生编号（主表的关联字段）
}>()
const formLoading = ref(false) // 表单的加载中
const formData = ref({})
const formRules = reactive({
  studentId: [{ required: true, message: t('codegenDemo.demo03.studentIdRequired'), trigger: 'blur' }],
  name: [{ required: true, message: t('codegenDemo.nameRequired'), trigger: 'blur' }],
  teacher: [{ required: true, message: t('codegenDemo.demo03.teacherRequired'), trigger: 'blur' }],
})
const formRef = ref() // 表单 Ref

/** 监听主表的关联字段的变化，加载对应的子表数据 */
watch(
  () => props.studentId,
  async (val) => {
    // 1. 重置表单
    formData.value = {
      id: undefined,
      studentId: undefined,
      name: undefined,
      teacher: undefined
    }
    // 2. val 非空，则加载数据
    if (!val) {
      return
    }
    try {
      formLoading.value = true
      const data = await Demo03StudentApi.getDemo03GradeByStudentId(val)
      if (!data) {
        return
      }
      formData.value = data
    } finally {
      formLoading.value = false
    }
  },
  { immediate: true }
)

/** 表单校验 */
const validate = () => {
  return formRef.value.validate()
}

/** 表单值 */
const getData = () => {
  return formData.value
}

defineExpose({ validate, getData })
</script>

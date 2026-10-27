<template>
  <el-form
    ref="formRef"
    :model="formData"
    :rules="formRules"
    v-loading="formLoading"
    label-width="0px"
    :inline-message="true"
  >
    <el-table :data="formData" class="-mt-10px" :table-layout="'auto'">
      <el-table-column :label="t('common.index')" type="index" width="100" />
      <el-table-column :label="t('codegenDemo.demo03.courseName')" min-width="150">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.name`" :rules="formRules.name" class="mb-0px!">
            <el-input v-model="row.name" :placeholder="t('codegenDemo.demo03.courseNamePlaceholder')" />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column :label="t('codegenDemo.demo03.score')" min-width="150">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.score`" :rules="formRules.score" class="mb-0px!">
            <el-input v-model="row.score" :placeholder="t('codegenDemo.demo03.scorePlaceholder')" />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column align="center" fixed="right" :label="t('common.action')" min-width="150">
        <template #default="{ $index }">
          <el-button @click="handleDelete($index)" link>—</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-form>
  <el-row justify="center" class="mt-3">
    <el-button @click="handleAdd" round>+ {{ t('codegenDemo.demo03.addCourse') }}</el-button>
  </el-row>
</template>
<script setup lang="ts">
import { Demo03StudentApi } from '@/api/infra/demo/demo03/inner'

const { t } = useI18n('infra') // 国际化

const props = defineProps<{
  studentId: number // 学生编号（主表的关联字段）
}>()
const formLoading = ref(false) // 表单的加载中
const formData = ref<any[]>([])
const formRules = reactive({
  studentId: [{ required: true, message: t('codegenDemo.demo03.studentIdRequired'), trigger: 'blur' }],
  name: [{ required: true, message: t('codegenDemo.demo03.courseNameRequired'), trigger: 'blur' }],
  score: [{ required: true, message: t('codegenDemo.demo03.scoreRequired'), trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

/** 监听主表的关联字段的变化，加载对应的子表数据 */
watch(
  () => props.studentId,
  async (val) => {
    // 1. 重置表单
    formData.value = []
    // 2. val 非空，则加载数据
    if (!val) {
      return
    }
    try {
      formLoading.value = true
      formData.value = await Demo03StudentApi.getDemo03CourseListByStudentId(val)
    } finally {
      formLoading.value = false
    }
  },
  { immediate: true }
)

/** 新增按钮操作 */
const handleAdd = () => {
  const row = {
    id: undefined,
    studentId: undefined,
    name: undefined,
    score: undefined
  }
  row.studentId = props.studentId as any
  formData.value.push(row)
}

/** 删除按钮操作 */
const handleDelete = (index) => {
  formData.value.splice(index, 1)
}

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

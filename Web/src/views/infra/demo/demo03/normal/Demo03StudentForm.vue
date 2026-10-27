<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
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
          <el-form-item :label="t('common.sex')" prop="sex">
            <el-radio-group v-model="formData.sex">
              <el-radio
                v-for="dict in getIntDictOptions(DICT_TYPE.SYSTEM_USER_SEX)"
                :key="dict.value"
                :value="dict.value"
              >
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('codegenDemo.demo03.birthday')" prop="birthday">
            <el-date-picker
              v-model="formData.birthday"
              type="date"
              value-format="x"
              :placeholder="t('codegenDemo.demo03.birthdayPlaceholder')"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item :label="t('codegenDemo.demo03.description')" prop="description">
            <Editor v-model="formData.description" height="150px" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <!-- 子表的表单 -->
    <el-tabs v-model="subTabsName">
      <el-tab-pane :label="t('codegenDemo.demo03.course')" name="demo03Course">
        <Demo03CourseForm ref="demo03CourseFormRef" :student-id="formData.id" />
      </el-tab-pane>
      <el-tab-pane :label="t('codegenDemo.demo03.grade')" name="demo03Grade">
        <Demo03GradeForm ref="demo03GradeFormRef" :student-id="formData.id" />
      </el-tab-pane>
    </el-tabs>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">{{ t('common.ok') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { Demo03StudentApi, Demo03Student } from '@/api/infra/demo/demo03/normal'
import Demo03CourseForm from './components/Demo03CourseForm.vue'
import Demo03GradeForm from './components/Demo03GradeForm.vue'

/** 学生 表单 */
defineOptions({ name: 'Demo03StudentForm' })

const { t } = useI18n('infra') // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  name: undefined,
  sex: undefined,
  birthday: undefined,
  description: undefined,
})
const formRules = reactive({
  name: [{ required: true, message: t('codegenDemo.nameRequired'), trigger: 'blur' }],
  sex: [{ required: true, message: t('common.sexRequired'), trigger: 'blur' }],
  birthday: [{ required: true, message: t('codegenDemo.demo03.birthdayRequired'), trigger: 'blur' }],
  description: [{ required: true, message: t('codegenDemo.demo03.descriptionRequired'), trigger: 'blur' }],
})
const formRef = ref() // 表单 Ref

/** 子表的表单 */
const subTabsName = ref('demo03Course')
const demo03CourseFormRef = ref()
const demo03GradeFormRef = ref()

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
      formData.value = await Demo03StudentApi.getDemo03Student(id)
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
  // 校验子表单
  try {
    await demo03CourseFormRef.value.validate()
  } catch (e) {
    subTabsName.value = 'demo03Course'
    return
  }
  try {
    await demo03GradeFormRef.value.validate()
  } catch (e) {
    subTabsName.value = 'demo03Grade'
    return
  }
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as Demo03Student
    // 拼接子表的数据
    data.demo03Courses = demo03CourseFormRef.value.getData()
    data.demo03Grade = demo03GradeFormRef.value.getData()
    if (formType.value === 'create') {
      await Demo03StudentApi.createDemo03Student(data)
      message.success(t('common.createSuccess'))
    } else {
      await Demo03StudentApi.updateDemo03Student(data)
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
    sex: undefined,
    birthday: undefined,
    description: undefined,
  }
  formRef.value?.resetFields()
}
</script>

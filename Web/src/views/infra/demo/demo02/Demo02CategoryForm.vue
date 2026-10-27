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
          <el-form-item :label="t('codegenDemo.parentId')" prop="parentId">
            <el-tree-select
              v-model="formData.parentId"
              :data="demo02CategoryTree"
              :props="defaultProps"
              check-strictly
              default-expand-all
              :placeholder="t('common.selectText')"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">{{ t('common.ok') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import * as Demo02CategoryApi from '@/api/infra/demo/demo02'
import { defaultProps, handleTree } from '@/utils/tree'

const { t } = useI18n('infra') // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  name: undefined,
  parentId: undefined
})
const formRules = reactive({
  name: [{ required: true, message: t('codegenDemo.nameRequired'), trigger: 'blur' }],
  parentId: [{ required: true, message: t('codegenDemo.parentIdRequired'), trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref
const demo02CategoryTree = ref() // 树形结构

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
      formData.value = await Demo02CategoryApi.getDemo02Category(id)
    } finally {
      formLoading.value = false
    }
  }
  await getDemo02CategoryTree()
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
    const data = formData.value as unknown as Demo02CategoryApi.Demo02CategoryVO
    if (formType.value === 'create') {
      await Demo02CategoryApi.createDemo02Category(data)
      message.success(t('common.createSuccess'))
    } else {
      await Demo02CategoryApi.updateDemo02Category(data)
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
    parentId: undefined
  }
  formRef.value?.resetFields()
}

/** 获得示例分类树 */
const getDemo02CategoryTree = async () => {
  demo02CategoryTree.value = []
  const data = await Demo02CategoryApi.getDemo02CategoryList()
  const root: Tree = { id: 0, name: t('codegenDemo.demo02.topCategory'), children: [] }
  root.children = handleTree(data, 'id', 'parentId')
  demo02CategoryTree.value.push(root)
}
</script>
<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="auto"
      v-loading="formLoading"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('parentIdLabel')" prop="parentId">
            <el-tree-select
              v-model="formData.parentId"
              :data="productCategoryTree"
              :props="defaultProps"
              check-strictly
              default-expand-all
              :placeholder="t('selectParentId')"
              class="w-1/1"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('nameLabel')" prop="name">
            <el-input v-model="formData.name" :placeholder="t('namePlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('codeLabel')" prop="code">
            <el-input v-model="formData.code" :placeholder="t('codeLabel')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('sort')" prop="sort">
            <el-input v-model="formData.sort" :placeholder="t('sortPlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item :label="t('status')" prop="status">
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
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { ProductCategoryApi, ProductCategoryVO } from '@/api/erp/product/category'
import { defaultProps, handleTree } from '@/utils/tree'
import { CommonStatusEnum } from '@/utils/constants'

/** ERP 产品分类 表单 */
defineOptions({ name: 'ProductCategoryForm' })

const { t } = useI18n('erp.product.category') // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  parentId: undefined,
  name: undefined,
  code: undefined,
  sort: undefined,
  status: CommonStatusEnum.ENABLE
})
const formRules = reactive({
  parentId: [{ required: true, message: t('parentIdLabel') + t('common.required'), trigger: 'blur' }],
  name: [{ required: true, message: t('nameRequired'), trigger: 'blur' }],
  code: [{ required: true, message: t('codeRequired'), trigger: 'blur' }],
  sort: [{ required: true, message: t('sortRequired'), trigger: 'blur' }],
  status: [{ required: true, message: t('statusRequired'), trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref
const productCategoryTree = ref() // 树形结构

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
      formData.value = await ProductCategoryApi.getProductCategory(id)
    } finally {
      formLoading.value = false
    }
  }
  await getProductCategoryTree()
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
    const data = formData.value as unknown as ProductCategoryVO
    if (formType.value === 'create') {
      await ProductCategoryApi.createProductCategory(data)
      message.success(t('common.createSuccess'))
    } else {
      await ProductCategoryApi.updateProductCategory(data)
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
    parentId: undefined,
    name: undefined,
    code: undefined,
    sort: undefined,
    status: CommonStatusEnum.ENABLE
  }
  formRef.value?.resetFields()
}

/** 获得产品分类树 */
const getProductCategoryTree = async () => {
  productCategoryTree.value = []
  const data = await ProductCategoryApi.getProductCategoryList()
  const root: Tree = { id: 0, name: t('topCategory'), children: [] }
  root.children = handleTree(data, 'id', 'parentId')
  productCategoryTree.value.push(root)
}
</script>
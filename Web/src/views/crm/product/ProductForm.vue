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
          <el-form-item :label="t('name')" prop="name">
            <el-input v-model="formData.name" :placeholder="t('namePlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('ownerUserId')" prop="ownerUserId">
            <el-select
              v-model="formData.ownerUserId"
              :placeholder="t('customer.ownerUserPlaceholder')"
              :disabled="formData.id"
              class="w-1/1"
            >
              <el-option
                v-for="user in userList"
                :key="user.id"
                :label="user.nickname"
                :value="user.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('categoryId')" prop="categoryId">
            <el-cascader
              v-model="formData.categoryId"
              :options="productCategoryList"
              :props="defaultProps"
              class="w-1/1"
              clearable
              :placeholder="t('category.namePlaceholder')"
              filterable
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('unit')" prop="unit">
            <el-select v-model="formData.unit" class="w-1/1" :placeholder="t('common.selectPlaceholder')">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.CRM_PRODUCT_UNIT)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('no')" prop="no">
            <el-input v-model="formData.no" :placeholder="t('noPlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('price')" prop="price">
            <el-input-number
              v-model="formData.price"
              :placeholder="t('price')"
              :min="0"
              :precision="2"
              :step="0.1"
              class="w-full!"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('description')" prop="description">
            <el-input v-model="formData.description" :placeholder="t('description')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('status')" prop="status">
            <el-select v-model="formData.status" :placeholder="t('common.selectPlaceholder')" class="w-1/1">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.CRM_PRODUCT_STATUS)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">{{ t('dialog.confirm') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('dialog.cancel') }}</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import * as ProductApi from '@/api/crm/product'
import * as ProductCategoryApi from '@/api/crm/product/category'
import { defaultProps, handleTree } from '@/utils/tree'
import { getSimpleUserList, UserVO } from '@/api/system/user'
import { useUserStore } from '@/store/modules/user'

defineOptions({ name: 'CrmProductForm' })

const { t } = useI18n('crm.product') // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const userId = useUserStore().getUser.id // 当前登录的编号
const formData = ref({
  id: undefined,
  name: undefined,
  no: undefined,
  unit: undefined,
  price: Number(undefined),
  status: undefined,
  categoryId: undefined,
  description: undefined,
  ownerUserId: -1
})
const formRules = reactive({
  name: [{ required: true, message: t('nameRequired'), trigger: 'blur' }],
  no: [{ required: true, message: t('noRequired'), trigger: 'blur' }],
  status: [{ required: true, message: t('statusRequired'), trigger: 'change' }],
  categoryId: [{ required: true, message: t('categoryIdRequired'), trigger: 'blur' }],
  ownerUserId: [{ required: true, message: t('ownerUserRequired'), trigger: 'blur' }],
  price: [{ required: true, message: t('priceRequired'), trigger: 'blur' }]
})

const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type, { scope: 'common' })
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await ProductApi.getProduct(id)
    } finally {
      formLoading.value = false
    }
  } else {
    formData.value.ownerUserId = userId
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as ProductApi.ProductVO
    if (formType.value === 'create') {
      await ProductApi.createProduct(data)
      message.success(t('common.createSuccess'))
    } else {
      await ProductApi.updateProduct(data)
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
    no: undefined,
    unit: undefined,
    price: Number(undefined),
    status: undefined,
    categoryId: undefined,
    description: undefined,
    ownerUserId: -1
  }
  formRef.value?.resetFields()
}

/** 初始化 */
const productCategoryList = ref<any[]>([]) // 产品分类树
const userList = ref<UserVO[]>([]) // 系统用户
onMounted(async () => {
  // 产品分类树
  const data = await ProductCategoryApi.getProductCategoryList({})
  productCategoryList.value = handleTree(data, 'id', 'parentId')
  // 系统用户列表
  userList.value = await getSimpleUserList()
})
</script>

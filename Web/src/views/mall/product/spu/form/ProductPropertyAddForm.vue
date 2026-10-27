<!-- 商品发布 - 库存价格 - 添加属性 -->
<template>
  <Dialog v-model="dialogVisible" :title="t('property.addProperty')">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="auto"
      @keydown.enter.prevent="submitForm"
    >
      <el-form-item :label="t('property.propertyName')" prop="name">
        <el-select
          v-model="formData.name"
          :reserve-keyword="false"
          allow-create
          class="!w-360px"
          default-first-option
          filterable
          :placeholder="t('property.addPropertyPlaceholder')"
        >
          <el-option
            v-for="item in attributeOptions"
            :key="item.id"
            :label="item.name"
            :value="item.name"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">{{ t('common.confirm') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import * as PropertyApi from '@/api/mall/product/property'

defineOptions({ name: 'ProductPropertyForm' })

const { t } = useI18n('mall.product') // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const formData = ref({
  name: ''
})
const formRules = reactive({
  name: [{ required: true, message: t('property.nameRequired'), trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref
const attributeList = ref([]) // 商品属性列表
const attributeOptions = ref([] as PropertyApi.PropertyVO[]) // 商品属性名称下拉框
const props = defineProps({
  propertyList: {
    type: Array,
    default: () => {}
  }
})

watch(
  () => props.propertyList, // 解决 props 无法直接修改父组件的问题
  (data) => {
    if (!data) return
    attributeList.value = data
  },
  {
    deep: true,
    immediate: true
  }
)

/** 打开弹窗 */
const open = async () => {
  dialogVisible.value = true
  resetForm()
  // 加载列表
  await getAttributeOptions()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const submitForm = async () => {
  // 1.1 重复添加校验
  for (const attrItem of attributeList.value) {
    if (attrItem.name === formData.value.name) {
      return message.error(t('property.propertyExists'))
    }
  }
  // 1.2 校验表单
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return

  // 2.1 情况一：属性名已存在，则直接使用并结束
  const existProperty = attributeOptions.value.find((item) => item.name === formData.value.name)
  if (existProperty) {
    // 添加到属性列表
    attributeList.value.push({
      id: existProperty.id,
      ...formData.value,
      values: []
    })
    // 关闭弹窗
    dialogVisible.value = false
    return
  }

  // 2.2 情况二：如果是不存在的属性，则需要执行新增
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as PropertyApi.PropertyVO
    const propertyId = await PropertyApi.createProperty(data)
    // 添加到属性列表
    attributeList.value.push({
      id: propertyId,
      ...formData.value,
      values: []
    })
    // 关闭弹窗
    message.success(t('common.createSuccess'))
    dialogVisible.value = false
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    name: ''
  }
  formRef.value?.resetFields()
}

/** 获取商品属性下拉选项 */
const getAttributeOptions = async () => {
  formLoading.value = true
  try {
    attributeOptions.value = await PropertyApi.getPropertySimpleList()
  } finally {
    formLoading.value = false
  }
}
</script>

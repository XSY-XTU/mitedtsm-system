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
          <el-form-item label="ProductKey" prop="productKey">
            <el-input
              v-model="formData.productKey"
              :placeholder="t('common.inputProductKey')"
              :readonly="formType === 'update'"
            >
              <template #append>
                <el-button @click="generateProductKey" :disabled="formType === 'update'">
                  {{ t('common.regenProductKey') }}
                </el-button>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('common.productName')" prop="name">
            <el-input v-model="formData.name" :placeholder="t('common.inputProductName')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('common.productCategory')" prop="categoryId">
            <el-select v-model="formData.categoryId" :placeholder="t('common.selectProductCategory')" clearable>
              <el-option
                v-for="category in categoryList"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('common.deviceType')" prop="deviceType">
            <el-radio-group v-model="formData.deviceType" :disabled="formType === 'update'">
              <el-radio
                v-for="dict in getIntDictOptions(DICT_TYPE.IOT_PRODUCT_DEVICE_TYPE)"
                :key="dict.value"
                :label="dict.value"
              >
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row
        v-if="[DeviceTypeEnum.DEVICE, DeviceTypeEnum.GATEWAY].includes(formData.deviceType!)"
      >
        <el-col :span="12">
          <el-form-item :label="t('common.networkType')" prop="netType">
            <el-select v-model="formData.netType" :placeholder="t('common.selectNetworkType')">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.IOT_NET_TYPE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('common.protocolType')" prop="protocolType">
            <el-select v-model="formData.protocolType" :placeholder="t('common.selectProtocolType')">
              <el-option
                v-for="dict in getStrDictOptions(DICT_TYPE.IOT_PROTOCOL_TYPE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item prop="serializeType">
            <template #label>
              <el-tooltip
                :content="t('common.iotDefaultFilename')"
                placement="top"
              >
                <span>
                  {{ t('common.serializeType') }}
                  <Icon icon="ep:question-filled" class="ml-2px" />
                </span>
              </el-tooltip>
            </template>
            <el-select v-model="formData.serializeType" :placeholder="t('common.selectSerializeType')">
              <el-option
                v-for="dict in getStrDictOptions(DICT_TYPE.IOT_SERIALIZE_TYPE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-collapse>
        <el-collapse-item :title="t('common.moreConfig')">
          <el-form-item :label="t('common.dynamicRegister')" prop="registerEnabled">
            <template #label>
              <el-tooltip
                :content="t('common.dynamicRegisterTip')"
                placement="top"
              >
                <span>
                  {{ t('common.dynamicRegister') }}
                  <Icon icon="ep:question-filled" class="ml-2px" />
                </span>
              </el-tooltip>
            </template>
            <el-switch v-model="formData.registerEnabled" />
          </el-form-item>
          <el-form-item :label="t('common.productIcon')" prop="icon">
            <UploadImg v-model="formData.icon" :height="'80px'" :width="'80px'" />
          </el-form-item>
          <el-form-item :label="t('common.productPicture')" prop="picUrl">
            <UploadImg v-model="formData.picUrl" :height="'120px'" :width="'120px'" />
          </el-form-item>
          <el-form-item :label="t('common.description')" prop="description">
            <el-input type="textarea" v-model="formData.description" :placeholder="t('common.inputProductDesc')" />
          </el-form-item>
        </el-collapse-item>
      </el-collapse>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">{{ t('common.ok') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import {
  ProductApi,
  ProductVO,
  ProtocolTypeEnum,
  SerializeTypeEnum,
  DeviceTypeEnum
} from '@/api/iot/product/product'
import { DICT_TYPE, getIntDictOptions, getStrDictOptions } from '@/utils/dict'
import { ProductCategoryApi, ProductCategoryVO } from '@/api/iot/product/category'
import { UploadImg } from '@/components/UploadFile'
import { generateRandomStr } from '@/utils'

defineOptions({ name: 'IoTProductForm' })

const { t } = useI18n('iot')
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const formData = ref({
  id: undefined,
  name: undefined,
  productKey: '',
  categoryId: undefined,
  icon: undefined,
  picUrl: undefined,
  description: undefined,
  deviceType: undefined,
  netType: undefined,
  protocolType: ProtocolTypeEnum.MQTT,
  serializeType: SerializeTypeEnum.JSON,
  registerEnabled: false
})
const formRules = reactive({
  productKey: [{ required: true, message: t('common.productKeyRequired'), trigger: 'blur' }],
  name: [{ required: true, message: t('common.productNameRequired'), trigger: 'blur' }],
  categoryId: [{ required: true, message: t('common.productCategoryRequired'), trigger: 'change' }],
  deviceType: [{ required: true, message: t('common.deviceTypeRequired'), trigger: 'change' }],
  netType: [
    {
      required: true,
      message: t('common.networkTypeRequired'),
      trigger: 'change'
    }
  ],
  protocolType: [{ required: true, message: t('common.protocolTypeRequired'), trigger: 'change' }],
  serializeType: [{ required: true, message: t('common.serializeTypeRequired'), trigger: 'change' }]
})
const formRef = ref()
const categoryList = ref<ProductCategoryVO[]>([]) // 产品分类列表

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  if (id) {
    formLoading.value = true
    try {
      formData.value = await ProductApi.getProduct(id)
    } finally {
      formLoading.value = false
    }
  } else {
    // 新增时，生成随机 productKey
    generateProductKey()
  }
  // 加载分类列表
  categoryList.value = await ProductCategoryApi.getSimpleProductCategoryList()
}
defineExpose({ open, close: () => (dialogVisible.value = false) })

/** 提交表单 */
const emit = defineEmits(['success'])
const submitForm = async () => {
  await formRef.value.validate()
  formLoading.value = true
  try {
    const data = formData.value as unknown as ProductVO
    if (formType.value === 'create') {
      await ProductApi.createProduct(data)
      message.success(t('common.createSuccess'))
    } else {
      await ProductApi.updateProduct(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false // 确保关闭弹框
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
    productKey: '',
    categoryId: undefined,
    icon: undefined,
    picUrl: undefined,
    description: undefined,
    deviceType: undefined,
    netType: undefined,
    protocolType: ProtocolTypeEnum.MQTT,
    serializeType: SerializeTypeEnum.JSON,
    registerEnabled: false
  }
  formRef.value?.resetFields()
}

/** 生成 ProductKey */
const generateProductKey = () => {
  formData.value.productKey = generateRandomStr(16)
}
</script>
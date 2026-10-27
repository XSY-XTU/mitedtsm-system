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
          <el-form-item :label="t('sale.customer.name')" prop="name">
            <el-input v-model="formData.name" :placeholder="t('sale.customer.namePlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('sale.customer.contact')" prop="contact">
            <el-input v-model="formData.contact" :placeholder="t('sale.customer.contactPlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('sale.customer.mobile')" prop="mobile">
            <el-input v-model="formData.mobile" :placeholder="t('sale.customer.mobilePlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('sale.customer.telephone')" prop="telephone">
            <el-input v-model="formData.telephone" :placeholder="t('sale.customer.telephonePlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('sale.customer.email')" prop="email">
            <el-input v-model="formData.email" :placeholder="t('sale.customer.emailPlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('sale.customer.fax')" prop="fax">
            <el-input v-model="formData.fax" :placeholder="t('sale.customer.faxPlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('sale.customer.status')" prop="status">
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
        <el-col :span="12">
          <el-form-item :label="t('sale.customer.sort')" prop="sort">
            <el-input-number
              v-model="formData.sort"
              :placeholder="t('sale.customer.sortPlaceholder')"
              class="!w-1/1"
              :precision="0"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('sale.customer.taxNo')" prop="taxNo">
            <el-input v-model="formData.taxNo" :placeholder="t('sale.customer.taxNoPlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('sale.customer.taxPercent')" prop="taxPercent">
            <el-input-number
              v-model="formData.taxPercent"
              :min="0"
              :precision="2"
              :placeholder="t('sale.customer.taxPercentPlaceholder')"
              class="!w-1/1"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('sale.customer.bankName')" prop="bankName">
            <el-input v-model="formData.bankName" :placeholder="t('sale.customer.bankNamePlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('sale.customer.bankAccount')" prop="bankAccount">
            <el-input v-model="formData.bankAccount" :placeholder="t('sale.customer.bankAccountPlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('sale.customer.bankAddress')" prop="bankAddress">
            <el-input v-model="formData.bankAddress" :placeholder="t('sale.customer.bankAddressPlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item :label="t('sale.customer.remark')" prop="remark">
            <el-input type="textarea" v-model="formData.remark" :placeholder="t('sale.customer.remarkPlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">{{ t('common.confirm') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { CustomerApi, CustomerVO } from '@/api/erp/sale/customer'
import { CommonStatusEnum } from '@/utils/constants'

/** ERP 客户 表单 */
defineOptions({ name: 'CustomerForm' })

const { t } = useI18n('erp') // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  name: undefined,
  contact: undefined,
  mobile: undefined,
  telephone: undefined,
  email: undefined,
  fax: undefined,
  remark: undefined,
  status: undefined,
  sort: undefined,
  taxNo: undefined,
  taxPercent: undefined,
  bankName: undefined,
  bankAccount: undefined,
  bankAddress: undefined
})
const formRules = reactive({
  name: [{ required: true, message: t('sale.customer.nameRequired'), trigger: 'blur' }],
  status: [{ required: true, message: t('sale.customer.statusRequired'), trigger: 'blur' }],
  sort: [{ required: true, message: t('sale.customer.sortRequired'), trigger: 'blur' }]
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
      formData.value = await CustomerApi.getCustomer(id)
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
    const data = formData.value as unknown as CustomerVO
    if (formType.value === 'create') {
      await CustomerApi.createCustomer(data)
      message.success(t('common.createSuccess'))
    } else {
      await CustomerApi.updateCustomer(data)
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
    contact: undefined,
    mobile: undefined,
    telephone: undefined,
    email: undefined,
    fax: undefined,
    remark: undefined,
    status: CommonStatusEnum.ENABLE,
    sort: undefined,
    taxNo: undefined,
    taxPercent: undefined,
    bankName: undefined,
    bankAccount: undefined,
    bankAddress: undefined
  }
  formRef.value?.resetFields()
}
</script>

<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="1080">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="auto"
      v-loading="formLoading"
      :disabled="disabled"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('no')" prop="no">
            <el-input disabled v-model="formData.no" :placeholder="t('autoGenerate')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('paymentTime')" prop="paymentTime">
            <el-date-picker
              v-model="formData.paymentTime"
              type="date"
              value-format="x"
              :placeholder="t('selectPaymentTime')"
              class="!w-1/1"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('supplierId')" prop="supplierId">
            <el-select
              v-model="formData.supplierId"
              clearable
              filterable
              :placeholder="t('selectSupplier')"
              class="!w-1/1"
            >
              <el-option
                v-for="item in supplierList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('financeUser')" prop="financeUserId">
            <el-select
              v-model="formData.financeUserId"
              clearable
              filterable
              :placeholder="t('selectFinanceUser')"
              class="!w-1/1"
            >
              <el-option
                v-for="item in userList"
                :key="item.id"
                :label="item.nickname"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('remark')" prop="remark">
            <el-input
              type="textarea"
              v-model="formData.remark"
              :rows="1"
              :placeholder="t('remarkPlaceholder')"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('attachment')" prop="fileUrl">
            <UploadFile :is-show-tip="false" v-model="formData.fileUrl" :limit="1" />
          </el-form-item>
        </el-col>
      </el-row>
      <!-- 子表的表单 -->
      <ContentWrap>
        <el-tabs v-model="subTabsName" class="-mt-15px -mb-10px">
          <el-tab-pane :label="t('itemList')" name="item">
            <FinancePaymentItemForm
              ref="itemFormRef"
              :supplier-id="formData.supplierId"
              :items="formData.items"
              :disabled="disabled"
            />
          </el-tab-pane>
        </el-tabs>
      </ContentWrap>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('paymentAccount')" prop="accountId">
            <el-select
              v-model="formData.accountId"
              clearable
              filterable
              :placeholder="t('selectAccount')"
              class="!w-1/1"
            >
              <el-option
                v-for="item in accountList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('totalPayment')" prop="totalPrice">
            <el-input disabled v-model="formData.totalPrice" :formatter="erpPriceInputFormatter" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('discountAmount')" prop="discountPrice">
            <el-input-number
              v-model="formData.discountPrice"
              controls-position="right"
              :precision="2"
              :placeholder="t('discountAmount')"
              class="!w-1/1"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('actualPayment')">
            <el-input
              disabled
              v-model="formData.paymentPrice"
              :formatter="erpPriceInputFormatter"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading" v-if="!disabled">
        {{ t('common.ok') }}
      </el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { FinancePaymentApi, FinancePaymentVO } from '@/api/erp/finance/payment'
import FinancePaymentItemForm from './components/FinancePaymentItemForm.vue'
import { SupplierApi, SupplierVO } from '@/api/erp/purchase/supplier'
import { erpPriceInputFormatter, erpPriceMultiply } from '@/utils'
import * as UserApi from '@/api/system/user'
import { AccountApi, AccountVO } from '@/api/erp/finance/account'

/** ERP 付款单表单 */
defineOptions({ name: 'FinancePaymentForm' })

const { t } = useI18n('erp.finance.payment') // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改；detail - 详情
const formData = ref({
  id: undefined,
  supplierId: undefined,
  accountId: undefined,
  financeUserId: undefined,
  paymentTime: undefined,
  remark: undefined,
  fileUrl: '',
  totalPrice: 0,
  discountPrice: 0,
  paymentPrice: 0,
  items: [],
  no: undefined // 订单单号，后端返回
})
const formRules = reactive({
  supplierId: [{ required: true, message: t('supplierRequired'), trigger: 'blur' }],
  paymentTime: [{ required: true, message: t('paymentTimeRequired'), trigger: 'blur' }]
})
const disabled = computed(() => formType.value === 'detail')
const formRef = ref() // 表单 Ref
const supplierList = ref<SupplierVO[]>([]) // 供应商列表
const accountList = ref<AccountVO[]>([]) // 账户列表
const userList = ref<UserApi.UserVO[]>([]) // 用户列表

/** 子表的表单 */
const subTabsName = ref('item')
const itemFormRef = ref()

/** 计算 discountPrice、totalPrice 价格 */
watch(
  () => formData.value,
  (val) => {
    if (!val) {
      return
    }
    const totalPrice = val.items.reduce((prev, curr) => prev + curr.paymentPrice, 0)
    formData.value.totalPrice = totalPrice
    formData.value.paymentPrice = totalPrice - val.discountPrice
  },
  { deep: true }
)

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
      formData.value = await FinancePaymentApi.getFinancePayment(id)
    } finally {
      formLoading.value = false
    }
  }
  // 加载供应商列表
  supplierList.value = await SupplierApi.getSupplierSimpleList()
  // 加载用户列表
  userList.value = await UserApi.getSimpleUserList()
  // 加载账户列表
  accountList.value = await AccountApi.getAccountSimpleList()
  const defaultAccount = accountList.value.find((item) => item.defaultStatus)
  if (defaultAccount) {
    formData.value.accountId = defaultAccount.id
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  await itemFormRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as FinancePaymentVO
    if (formType.value === 'create') {
      await FinancePaymentApi.createFinancePayment(data)
      message.success(t('common.createSuccess'))
    } else {
      await FinancePaymentApi.updateFinancePayment(data)
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
    supplierId: undefined,
    accountId: undefined,
    financeUserId: undefined,
    paymentTime: undefined,
    remark: undefined,
    fileUrl: undefined,
    totalPrice: 0,
    discountPrice: 0,
    paymentPrice: 0,
    items: [],
    no: undefined
  }
  formRef.value?.resetFields()
}
</script>
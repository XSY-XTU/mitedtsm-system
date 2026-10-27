<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="1280">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('crm.contract.no')" prop="no">
            <el-input disabled v-model="formData.no" :placeholder="t('crm.contract.noAutoGenerate')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('crm.contract.name')" prop="name">
            <el-input v-model="formData.name" :placeholder="t('crm.contract.namePlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('crm.contract.ownerUserId')" prop="ownerUserId">
            <el-select
              v-model="formData.ownerUserId"
              :disabled="formType !== 'create'"
              class="w-1/1"
            >
              <el-option
                v-for="item in userOptions"
                :key="item.id"
                :label="item.nickname"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('crm.contract.customerName')" prop="customerId">
            <el-select
              v-model="formData.customerId"
              :placeholder="t('crm.contract.customerIdPlaceholder')"
              class="w-1/1"
              @change="handleCustomerChange"
            >
              <el-option
                v-for="item in customerList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('crm.contract.businessName')" prop="businessId">
            <el-select
              @change="handleBusinessChange"
              :disabled="!formData.customerId"
              v-model="formData.businessId"
              class="w-1/1"
            >
              <el-option
                v-for="item in getBusinessOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id!"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('crm.contract.orderDate')" prop="orderDate">
            <el-date-picker
              v-model="formData.orderDate"
              :placeholder="t('crm.contract.orderDatePlaceholder')"
              type="date"
              value-format="x"
              class="!w-1/1"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('crm.contract.startTime')" prop="startTime">
            <el-date-picker
              v-model="formData.startTime"
              :placeholder="t('crm.contract.startTimePlaceholder')"
              type="date"
              value-format="x"
              class="!w-1/1"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('crm.contract.endTime')" prop="endTime">
            <el-date-picker
              v-model="formData.endTime"
              :placeholder="t('crm.contract.endTimePlaceholder')"
              type="date"
              value-format="x"
              class="!w-1/1"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('crm.contract.signUserId')" prop="signUserId">
            <el-select v-model="formData.signUserId" class="w-1/1">
              <el-option
                v-for="item in userOptions"
                :key="item.id"
                :label="item.nickname"
                :value="item.id!"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('crm.contract.signContactId')" prop="signContactId">
            <el-select
              v-model="formData.signContactId"
              :disabled="!formData.customerId"
              class="w-1/1"
            >
              <el-option
                v-for="item in getContactOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('crm.contract.remark')" prop="remark">
            <el-input v-model="formData.remark" :placeholder="t('crm.contract.remarkPlaceholder')" type="textarea" />
          </el-form-item>
        </el-col>
      </el-row>
      <!-- 子表的表单 -->
      <ContentWrap>
        <el-tabs v-model="subTabsName" class="-mt-15px -mb-10px">
          <el-tab-pane :label="t('crm.contract.productList')" name="product">
            <ContractProductForm
              ref="productFormRef"
              :products="formData.products"
              :disabled="disabled"
            />
          </el-tab-pane>
        </el-tabs>
      </ContentWrap>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('crm.contract.totalProductPrice')" prop="totalProductPrice">
            <el-input
              disabled
              v-model="formData.totalProductPrice"
              :formatter="erpPriceInputFormatter"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('crm.contract.discountPercent')" prop="discountPercent">
            <el-input-number
              v-model="formData.discountPercent"
              :placeholder="t('crm.contract.discountPercent')"
              controls-position="right"
              :min="0"
              :precision="2"
              class="!w-1/1"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('crm.contract.discountAmount')" prop="totalPrice">
            <el-input
              disabled
              v-model="formData.totalPrice"
              :placeholder="t('crm.contract.namePlaceholder')"
              :formatter="erpPriceInputFormatter"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">{{ t('crm.contract.save') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import * as CustomerApi from '@/api/crm/customer'
import * as ContractApi from '@/api/crm/contract'
import * as UserApi from '@/api/system/user'
import * as ContactApi from '@/api/crm/contact'
import * as BusinessApi from '@/api/crm/business'
import { erpPriceMultiply, erpPriceInputFormatter } from '@/utils'
import { useUserStore } from '@/store/modules/user'
import ContractProductForm from '@/views/crm/contract/components/ContractProductForm.vue'

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  no: undefined,
  name: undefined,
  customerId: undefined,
  businessId: undefined,
  orderDate: undefined,
  startTime: undefined,
  endTime: undefined,
  signUserId: undefined,
  signContactId: undefined,
  ownerUserId: undefined,
  discountPercent: 0,
  totalProductPrice: undefined,
  remark: undefined,
  products: []
})
const formRules = reactive({
  name: [{ required: true, message: t('crm.contract.nameRequired'), trigger: 'blur' }],
  customerId: [{ required: true, message: t('crm.contract.customerIdRequired'), trigger: 'blur' }],
  orderDate: [{ required: true, message: t('crm.contract.orderDateRequired'), trigger: 'blur' }],
  ownerUserId: [{ required: true, message: t('crm.contract.ownerUserRequired'), trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref
const userOptions = ref<UserApi.UserVO[]>([]) // 用户列表
const customerList = ref([]) // 客户列表的数据
const businessList = ref<BusinessApi.BusinessVO[]>([])
const contactList = ref<ContactApi.ContactVO[]>([])

/** 子表的表单 */
const subTabsName = ref('product')
const productFormRef = ref()

/** 计算 discountPrice、totalPrice 价格 */
watch(
  () => formData.value,
  (val) => {
    if (!val) {
      return
    }
    const totalProductPrice = val.products.reduce((prev, curr) => prev + curr.totalPrice, 0)
    const discountPrice =
      val.discountPercent != null
        ? erpPriceMultiply(totalProductPrice, val.discountPercent / 100.0)
        : 0
    const totalPrice = totalProductPrice - discountPrice
    // 赋值
    formData.value.totalProductPrice = totalProductPrice
    formData.value.totalPrice = totalPrice
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
      formData.value = await ContractApi.getContract(id)
    } finally {
      formLoading.value = false
    }
  }
  // 获得客户列表
  customerList.value = await CustomerApi.getCustomerSimpleList()
  // 获得用户列表
  userOptions.value = await UserApi.getSimpleUserList()
  // 默认新建时选中自己
  if (formType.value === 'create') {
    formData.value.ownerUserId = useUserStore().getUser.id
  }
  // 获取联系人
  contactList.value = await ContactApi.getSimpleContactList()
  // 获得商机列表
  businessList.value = await BusinessApi.getSimpleBusinessList()
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
  productFormRef.value.validate()
  try {
    const data = unref(formData.value) as unknown as ContractApi.ContractVO
    if (formType.value === 'create') {
      await ContractApi.createContract(data)
      message.success(t('common.createSuccess'))
    } else {
      await ContractApi.updateContract(data)
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
    no: undefined,
    name: undefined,
    customerId: undefined,
    businessId: undefined,
    orderDate: undefined,
    startTime: undefined,
    endTime: undefined,
    signUserId: undefined,
    signContactId: undefined,
    ownerUserId: undefined,
    discountPercent: 0,
    totalProductPrice: undefined,
    remark: undefined,
    products: []
  }
  formRef.value?.resetFields()
}

/** 处理切换客户 */
const handleCustomerChange = () => {
  formData.value.businessId = undefined
  formData.value.signContactId = undefined
  formData.value.products = []
}

/** 处理商机变化 */
const handleBusinessChange = async (businessId: number) => {
  const business = await BusinessApi.getBusiness(businessId)
  business.products.forEach((item) => {
    item.contractPrice = item.businessPrice
  })
  formData.value.products = business.products
}

/** 动态获取客户联系人 */
const getContactOptions = computed(() =>
  contactList.value.filter((item) => item.customerId == formData.value.customerId)
)
/** 动态获取商机 */
const getBusinessOptions = computed(() =>
  businessList.value.filter((item) => item.customerId == formData.value.customerId)
)
</script>
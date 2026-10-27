<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="auto"
    >
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('receivablePlan.period')" prop="period">
            <el-input v-model="formData.period" disabled :placeholder="t('contract.noAutoGenerate')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('receivable.ownerUserId')" prop="ownerUserId">
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
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('receivable.customerName')" prop="customerId">
            <el-select
              v-model="formData.customerId"
              :disabled="formType !== 'create'"
              class="w-1/1"
              filterable
              :placeholder="t('customer.ownerUserPlaceholder')"
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
        <el-col :span="12">
          <el-form-item :label="t('receivable.contractName')" prop="contractId">
            <el-select
              v-model="formData.contractId"
              :disabled="formType !== 'create' || !formData.customerId"
              class="w-1/1"
              filterable
              :placeholder="t('contract.namePlaceholder')"
            >
              <el-option
                v-for="data in contractList"
                :key="data.id"
                :label="data.name"
                :value="data.id!"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('receivablePlan.price')" prop="price">
            <el-input-number
              v-model="formData.price"
              :min="0.01"
              :precision="2"
              class="!w-100%"
              controls-position="right"
              :placeholder="t('receivablePlan.pricePlaceholder')"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('receivablePlan.returnTime')" prop="returnTime">
            <el-date-picker
              v-model="formData.returnTime"
              :placeholder="t('receivablePlan.returnTime')"
              type="date"
              value-format="x"
              class="!w-100%"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('receivablePlan.remindDays')" prop="remindDays">
            <el-input-number
              v-model="formData.remindDays"
              class="!w-100%"
              controls-position="right"
              :placeholder="t('receivablePlan.remindDays')"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('receivable.returnType')" prop="returnType">
            <el-select v-model="formData.returnType" class="w-1/1" :placeholder="t('common.selectPlaceholder')">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.CRM_RECEIVABLE_RETURN_TYPE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item :label="t('receivablePlan.remark')" prop="remark">
            <el-input v-model="formData.remark" :placeholder="t('customer.remarkPlaceholder')" type="textarea" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">{{ t('dialog.confirm') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('dialog.cancel') }}</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import * as ReceivablePlanApi from '@/api/crm/receivable/plan'
import * as UserApi from '@/api/system/user'
import * as CustomerApi from '@/api/crm/customer'
import * as ContractApi from '@/api/crm/contract'
import { useUserStore } from '@/store/modules/user'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { cloneDeep } from 'lodash-es'

const { t } = useI18n('crm') // 国际化
const message = useMessage() // 消息弹窗
const userOptions = ref<UserApi.UserVO[]>([]) // 用户列表
const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref<ReceivablePlanApi.ReceivablePlanVO>({} as ReceivablePlanApi.ReceivablePlanVO)
const formRules = reactive({
  price: [{ required: true, message: t('receivablePlan.pricePlaceholder'), trigger: 'blur' }],
  returnTime: [{ required: true, message: t('receivablePlan.returnTime'), trigger: 'blur' }],
  customerId: [{ required: true, message: t('customer.nameRequired'), trigger: 'blur' }],
  contractId: [{ required: true, message: t('receivable.contractIdRequired'), trigger: 'blur' }],
  ownerUserId: [{ required: true, message: t('customer.ownerUserRequired'), trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref
const customerList = ref<CustomerApi.CustomerVO[]>([]) // 客户列表
const contractList = ref<ContractApi.ContractVO[]>([]) // 合同列表

/** 打开弹窗 */
const open = async (type: string, id?: number, customerId?: number, contractId?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type, { scope: 'common' })
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      const data = await ReceivablePlanApi.getReceivablePlan(id)
      formData.value = cloneDeep(data)
      await handleCustomerChange(data.customerId!)
      formData.value.contractId = data?.contractId
    } finally {
      formLoading.value = false
    }
  }
  // 获得用户列表
  userOptions.value = await UserApi.getSimpleUserList()
  // 获得客户列表
  customerList.value = await CustomerApi.getCustomerSimpleList()
  // 默认新建时选中自己
  if (formType.value === 'create') {
    formData.value.ownerUserId = useUserStore().getUser.id
  }
  // 设置 customerId 和 contractId 默认值
  if (customerId) {
    formData.value.customerId = customerId
    await handleCustomerChange(customerId)
  }
  if (contractId) {
    formData.value.contractId = contractId
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
    const data = formData.value as unknown as ReceivablePlanApi.ReceivablePlanVO
    if (formType.value === 'create') {
      await ReceivablePlanApi.createReceivablePlan(data)
      message.success(t('common.createSuccess'))
    } else {
      await ReceivablePlanApi.updateReceivablePlan(data)
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
  formData.value = {} as ReceivablePlanApi.ReceivablePlanVO
  formRef.value?.resetFields()
}

/** 处理切换客户 */
const handleCustomerChange = async (customerId: number) => {
  // 重置合同编号
  formData.value.contractId = undefined
  // 获得合同列表
  if (customerId) {
    contractList.value = []
    contractList.value = await ContractApi.getContractSimpleList(customerId)
  }
}
</script>
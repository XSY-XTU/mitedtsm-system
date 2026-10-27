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
          <el-form-item :label="t('limitConfig.ruleUserGroup')" prop="userIds">
            <el-select multiple filterable v-model="formData.userIds" class="w-1/1">
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
          <el-form-item :label="t('limitConfig.ruleDeptGroup')" prop="deptIds">
            <el-tree-select
              v-model="formData.deptIds"
              :data="deptTree"
              :props="defaultProps"
              multiple
              filterable
              check-strictly
              node-key="id"
              :placeholder="t('limitConfig.ruleDeptPlaceholder')"
              class="w-1/1"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item
            :label="
              formData.type === LimitConfType.CUSTOMER_QUANTITY_LIMIT
                ? t('limitConfig.customerQuantityLimit')
                : t('limitConfig.customerLockLimit')
            "
            prop="maxCount"
          >
            <el-input-number v-model="formData.maxCount" :placeholder="t('limitConfig.maxCountPlaceholder')" class="w-1/1" />
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="formData.type === LimitConfType.CUSTOMER_QUANTITY_LIMIT">
          <el-form-item :label="t('limitConfig.dealCountEnabled')" prop="dealCountEnabled">
            <el-switch v-model="formData.dealCountEnabled" />
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
import * as CustomerLimitConfigApi from '@/api/crm/customer/limitConfig'
import * as DeptApi from '@/api/system/dept'
import { defaultProps, handleTree } from '@/utils/tree'
import * as UserApi from '@/api/system/user'
import { cloneDeep } from 'lodash-es'
import { LimitConfType } from '@/api/crm/customer/limitConfig'

const { t } = useI18n('crm.customer') // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  type: LimitConfType.CUSTOMER_LOCK_LIMIT, // 给个默认值，避免 IDE 报错
  userIds: undefined,
  deptIds: undefined,
  maxCount: undefined,
  dealCountEnabled: false
})
const formRules = reactive({
  type: [{ required: true, message: t('limitConfig.ruleTypeRequired'), trigger: 'change' }],
  maxCount: [{ required: true, message: t('limitConfig.maxCountRequired'), trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref
const deptTree = ref() // 部门树形结构
const userOptions = ref<UserApi.UserVO[]>([]) // 用户列表

/** 打开弹窗 */
const open = async (type: string, limitConfType: LimitConfType, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await CustomerLimitConfigApi.getCustomerLimitConfig(id)
    } finally {
      formLoading.value = false
    }
  } else {
    formData.value.type = limitConfType
  }
  // 获得部门树
  deptTree.value = handleTree(await DeptApi.getSimpleDeptList())
  // 获得用户
  userOptions.value = await UserApi.getSimpleUserList()
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
    const data = formData.value as unknown as CustomerLimitConfigApi.CustomerLimitConfigVO
    if (formType.value === 'create') {
      await CustomerLimitConfigApi.createCustomerLimitConfig(data)
      message.success(t('common.createSuccess'))
    } else {
      await CustomerLimitConfigApi.updateCustomerLimitConfig(data)
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
    type: LimitConfType.CUSTOMER_LOCK_LIMIT,
    userIds: undefined,
    deptIds: undefined,
    maxCount: undefined,
    dealCountEnabled: false
  }
  formRef.value?.resetFields()
}
</script>
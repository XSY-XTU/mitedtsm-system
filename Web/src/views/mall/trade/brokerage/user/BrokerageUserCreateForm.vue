<template>
  <Dialog v-model="dialogVisible" :title="t('mall.trade.brokerage.createDistributor')" width="800">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="12" :xs="24">
          <el-form-item :label="t('mall.trade.brokerage.distributor')" prop="userId">
            <el-input
              v-model="formData.userId"
              v-loading="formLoading"
              :placeholder="t('mall.trade.brokerage.distributorIdPlaceholder')"
            >
              <template #append>
                <el-button @click="handleGetUser(formData.userId, t('mall.trade.brokerage.distributor'))">
                  <Icon class="mr-5px" icon="ep:search" />
                </el-button>
              </template>
            </el-input>
          </el-form-item>
          <!-- 展示分销员的信息 -->
          <el-descriptions v-if="userInfo.user" :column="1" border>
            <el-descriptions-item :label="t('mall.trade.brokerage.avatar')">
              <el-avatar :src="userInfo.user?.avatar" />
            </el-descriptions-item>
            <el-descriptions-item :label="t('mall.trade.brokerage.nickname')">{{ userInfo.user?.nickname }}</el-descriptions-item>
          </el-descriptions>
        </el-col>

        <el-col :span="12" :xs="24">
          <el-form-item :label="t('mall.trade.brokerage.superiorPromoter')" prop="bindUserId">
            <el-input
              v-model="formData.bindUserId"
              v-loading="formLoading"
              :placeholder="t('mall.trade.brokerage.promoterIdPlaceholder2')"
            >
              <template #append>
                <el-button @click="handleGetUser(formData.bindUserId, t('mall.trade.brokerage.promoter'))">
                  <Icon class="mr-5px" icon="ep:search" />
                </el-button>
              </template>
            </el-input>
          </el-form-item>
          <!-- 展示上级推广人的信息 -->
          <el-descriptions v-if="userInfo.bindUser" :column="1" border>
            <el-descriptions-item :label="t('mall.trade.brokerage.avatar')">
              <el-avatar :src="userInfo.bindUser?.avatar" />
            </el-descriptions-item>
            <el-descriptions-item :label="t('mall.trade.brokerage.nickname')"
              >{{ userInfo.bindUser?.nickname }}
            </el-descriptions-item>
            <el-descriptions-item :label="t('mall.trade.brokerage.promoterQualification')">
              <el-tag v-if="userInfo.bindUser?.brokerageEnabled">{{ t('mall.trade.brokerage.yes') }}</el-tag>
              <el-tag v-else type="info">{{ t('mall.trade.brokerage.no') }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item :label="t('mall.trade.brokerage.promoterQualificationTime')">
              {{ formatDate(userInfo.bindUser?.brokerageTime) }}
            </el-descriptions-item>
          </el-descriptions>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">{{ t('common.ok') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import * as BrokerageUserApi from '@/api/mall/trade/brokerage/user'
import * as UserApi from '@/api/member/user'
import { formatDate } from '@/utils/formatTime'

defineOptions({ name: 'BrokerageUserCreateForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formData = ref({
  userId: undefined,
  bindUserId: undefined
})
const formRef = ref() // 表单 Ref
const formRules = reactive({
  userId: [{ required: true, message: t('mall.trade.brokerage.distributorRequired'), trigger: 'blur' }],
  bindUserId: [{ required: true, message: t('mall.trade.brokerage.promoterRequired'), trigger: 'blur' }]
})

/** 打开弹窗 */
const open = async () => {
  resetForm()
  dialogVisible.value = true
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
/** 创建分销员 */
const submitForm = async () => {
  if (formLoading.value) return
  // 校验表单
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return

  // 提交请求
  formLoading.value = true
  try {
    // 发起修改
    await BrokerageUserApi.createBrokerageUser(formData.value)
    message.success(t('common.createSuccess'))
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formRef.value?.resetFields()
  formData.value = {
    userId: undefined,
    bindUserId: undefined
  }

  userInfo.bindUser = undefined
  userInfo.user = undefined
}

/** 查询推广员和分销员 */
const userInfo = reactive<{
  bindUser: BrokerageUserApi.BrokerageUserVO | undefined
  user: BrokerageUserApi.BrokerageUserVO | undefined
}>({
  bindUser: undefined,
  user: undefined
})
const handleGetUser = async (id: any, userType: string) => {
  if (!id) {
    message.warning(t('mall.trade.brokerage.inputIdFirst', { type: userType }))
    return
  }
  if (userType === t('mall.trade.brokerage.promoter') && formData.value.bindUserId == formData.value.userId) {
    message.error(t('mall.trade.brokerage.cantBindSelf'))
    return
  }
  const user =
    userType === t('mall.trade.brokerage.promoter') ? await BrokerageUserApi.getBrokerageUser(id) : await UserApi.getUser(id)
  userType === t('mall.trade.brokerage.promoter') ? (userInfo.bindUser = user) : (userInfo.user = user)
  if (!user) {
    message.warning(t('mall.trade.brokerage.distributorNotExist'))
  }
}
</script>

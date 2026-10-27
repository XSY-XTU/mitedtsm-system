<template>
  <Dialog v-model="dialogVisible" :title="t('user.updatePoint')" width="600">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('user.userId')" prop="id">
            <el-input v-model="formData.id" class="!w-240px" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('user.nickname')" prop="nickname">
            <el-input v-model="formData.nickname" class="!w-240px" disabled />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('user.pointBefore')" prop="point">
            <el-input-number v-model="formData.point" class="!w-240px" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('user.pointChangeType')" prop="changeType">
            <el-radio-group v-model="formData.changeType">
              <el-radio :value="1">{{ t('user.pointIncrease') }}</el-radio>
              <el-radio :value="-1">{{ t('user.pointDecrease') }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('user.pointChange')" prop="changePoint">
            <el-input-number v-model="formData.changePoint" :min="0" :precision="0" class="!w-240px" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('user.pointResult')">
            <el-input-number v-model="pointResult" class="!w-240px" disabled />
          </el-form-item>
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
import * as UserApi from '@/api/member/user'
import { useI18n } from '@/hooks/web/useI18n'

/** 修改用户积分表单 */
defineOptions({ name: 'UpdatePointForm' })

const { t } = useI18n('member') // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formData = ref({
  id: undefined,
  nickname: undefined,
  point: 0,
  changePoint: 0,
  changeType: 1
})
const formRules = reactive({
  changePoint: [{ required: true, message: t('user.pointChangeRequired'), trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (id?: number) => {
  dialogVisible.value = true
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await UserApi.getUser(id)
      formData.value.changeType = 1 // 默认增加积分
      formData.value.changePoint = 0 // 变动积分默认0
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
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return

  if (formData.value.changePoint < 1) {
    message.error(t('user.pointChangeMin'))
    return
  }
  if (pointResult.value < 0) {
    message.error(t('user.pointResultMin'))
    return
  }

  // 提交请求
  formLoading.value = true
  try {
    await UserApi.updateUserPoint({
      id: formData.value.id,
      point: formData.value.changePoint * formData.value.changeType
    })

    message.success(t('common.updateSuccess'))
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
    nickname: undefined,
    point: 0,
    changePoint: 0,
    changeType: 1
  }
  formRef.value?.resetFields()
}

/** 变动后的积分 */
const pointResult = computed(
  () => formData.value.point + formData.value.changePoint * formData.value.changeType
)
</script>
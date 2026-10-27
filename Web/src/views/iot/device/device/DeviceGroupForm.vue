<template>
  <Dialog :title="t('device.group.addToGroup')" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="auto"
      v-loading="formLoading"
    >
      <el-row>
        <el-col :span="24">
          <el-form-item :label="t('device.device.group')" prop="groupIds">
            <el-select v-model="formData.groupIds" :placeholder="t('device.group.selectGroup')" multiple clearable>
              <el-option
                v-for="group in deviceGroups"
                :key="group.id"
                :label="group.name"
                :value="group.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">{{ t('common.ok') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { DeviceApi } from '@/api/iot/device/device'
import { DeviceGroupApi } from '@/api/iot/device/group'

defineOptions({ name: 'IoTDeviceGroupForm' })

const { t } = useI18n('iot') // 国际化
const message = useMessage() // 消息窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const formData = ref({
  ids: [] as number[],
  groupIds: [] as number[]
})
const formRules = reactive({
  groupIds: [{ required: true, message: t('device.group.nameRequired'), trigger: 'change' }]
})
const formRef = ref() // 表单 Ref
const deviceGroups = ref<any[]>([]) // 设备分组列表

/** 打开弹窗 */
const open = async (ids: number[]) => {
  dialogVisible.value = true
  resetForm()
  formData.value.ids = ids

  // 加载设备分组列表
  try {
    deviceGroups.value = await DeviceGroupApi.getSimpleDeviceGroupList()
  } catch (error) {
    console.error('Failed to load device group list:', error)
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
    await DeviceApi.updateDeviceGroup(formData.value)
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
    ids: [],
    groupIds: []
  }
  formRef.value?.resetFields()
}
</script>
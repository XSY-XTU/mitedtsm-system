<template>
  <el-dialog v-model="dialogVisible" :title="t('ota.task.createTask')" width="800px" append-to-body>
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="auto"
      v-loading="formLoading"
    >
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('ota.task.name')" prop="name">
            <el-input v-model="formData.name" :placeholder="t('ota.task.namePlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('ota.task.deviceScope')" prop="deviceScope">
            <el-select v-model="formData.deviceScope" :placeholder="t('ota.task.selectUpgradeScope')" class="w-full">
              <el-option
                v-for="item in Object.values(IoTOtaTaskDeviceScopeEnum)"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item :label="t('ota.task.description')" prop="description">
            <el-input
              v-model="formData.description"
              type="textarea"
              :rows="3"
              :placeholder="t('ota.task.descriptionPlaceholder')"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row
        v-if="formData.deviceScope === IoTOtaTaskDeviceScopeEnum.SELECT.value"
      >
        <el-col :span="24">
          <el-form-item :label="t('ota.task.selectDevices')" prop="deviceIds">
            <el-select
              v-model="formData.deviceIds"
              multiple
              :placeholder="t('ota.task.selectDevice')"
              class="w-full"
              filterable
              reserve-keyword
            >
              <el-option
                v-for="device in devices"
                :key="device.id"
                :label="
                  device.nickname ? `${device.deviceName} (${device.nickname})` : device.deviceName
                "
                :value="device.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">{{ t('common.confirm') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { IoTOtaTaskApi, OtaTask } from '@/api/iot/ota/task'
import { IoTOtaTaskDeviceScopeEnum } from '@/views/iot/utils/constants'
import { DeviceApi, DeviceVO } from '@/api/iot/device/device'

/** IoT OTA 升级任务表单 */
defineOptions({ name: 'OtaTaskForm' })

const props = defineProps<{
  firmwareId: number
  productId: number
}>()

const { t } = useI18n('iot') // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中：修改时的数据加载
const formData = ref<OtaTask>({
  name: '',
  deviceScope: IoTOtaTaskDeviceScopeEnum.ALL.value,
  firmwareId: props.firmwareId,
  description: '',
  deviceIds: []
})
const formRef = ref() // 表单 Ref
const formRules = {
  name: [{ required: true, message: t('ota.task.inputTaskName'), trigger: 'blur' }],
  deviceScope: [{ required: true, message: t('ota.task.selectUpgradeScope'), trigger: 'change' }],
  deviceIds: [{ required: true, message: t('ota.task.selectAtLeastOneDevice'), trigger: 'change' }]
}
const devices = ref<DeviceVO[]>([]) // 设备选择相关

/** 打开弹窗 */
const open = async () => {
  dialogVisible.value = true
  resetForm()
  // 加载设备列表
  devices.value = (await DeviceApi.getDeviceListByProductId(props.productId)) || []
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
    await IoTOtaTaskApi.createOtaTask(formData.value)
    message.success(t('ota.task.createSuccess'))
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
    name: '',
    deviceScope: IoTOtaTaskDeviceScopeEnum.ALL.value,
    firmwareId: props.firmwareId,
    description: '',
    deviceIds: []
  }
  formRef.value?.resetFields()
}
</script>

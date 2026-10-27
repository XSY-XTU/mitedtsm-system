<!-- Modbus 连接配置弹窗 -->
<template>
  <Dialog :title="t('modbus.config')" v-model="dialogVisible" width="600px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="auto"
      v-loading="formLoading"
    >
      <!-- Client 模式专有字段：IP、端口、超时、重试 -->
      <template v-if="isClient">
        <el-row>
          <el-col :span="12">
            <el-form-item label="IP" prop="ip">
              <el-input v-model="formData.ip" :placeholder="t('common.inputPlaceholder')" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="t('modbus.port')" prop="port">
              <el-input-number
                v-model="formData.port"
                :placeholder="t('common.inputPlaceholder')"
                :min="1"
                :max="65535"
                controls-position="right"
                class="!w-full"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </template>
      <!-- 公共字段：从站地址 -->
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('modbus.slaveId')" prop="slaveId">
            <el-input-number
              v-model="formData.slaveId"
              :min="1"
              :max="247"
              controls-position="right"
              :placeholder="t('common.inputPlaceholder')"
              class="!w-full"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <!-- Client 模式专有字段：超时、重试 -->
      <template v-if="isClient">
        <el-row>
          <el-col :span="12">
            <el-form-item label="Timeout(ms)" prop="timeout">
              <el-input-number
                v-model="formData.timeout"
                :min="1000"
                :step="1000"
                controls-position="right"
                :placeholder="t('common.inputPlaceholder')"
                class="!w-full"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Retry(ms)" prop="retryInterval">
              <el-input-number
                v-model="formData.retryInterval"
                :min="1000"
                :step="1000"
                controls-position="right"
                :placeholder="t('common.inputPlaceholder')"
                class="!w-full"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </template>
      <!-- Server 模式专有字段：模式、帧格式 -->
      <template v-if="isServer">
        <el-row>
          <el-col :span="12">
            <el-form-item :label="t('common.type')" prop="mode">
              <el-radio-group v-model="formData.mode">
                <el-radio
                  v-for="dict in getIntDictOptions(DICT_TYPE.IOT_MODBUS_MODE)"
                  :key="dict.value"
                  :label="dict.value"
                >
                  {{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Frame" prop="frameFormat">
              <el-radio-group v-model="formData.frameFormat">
                <el-radio
                  v-for="dict in getIntDictOptions(DICT_TYPE.IOT_MODBUS_FRAME_FORMAT)"
                  :key="dict.value"
                  :label="dict.value"
                >
                  {{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </template>
      <!-- 公共字段：状态 -->
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('common.status')" prop="status">
            <el-radio-group v-model="formData.status">
              <el-radio
                v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
                :key="dict.value"
                :label="dict.value"
              >
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
      <el-button type="primary" @click="submitForm" :loading="formLoading">{{ t('common.ok') }}</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import { DeviceModbusConfigApi, DeviceModbusConfigVO } from '@/api/iot/device/modbus/config'
import { ProtocolTypeEnum } from '@/api/iot/product/product'
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { CommonStatusEnum } from '@/utils/constants'
import { ModbusModeEnum, ModbusFrameFormatEnum } from '@/views/iot/utils/constants'

defineOptions({ name: 'DeviceModbusConfigForm' })

const props = defineProps<{
  deviceId: number
  protocolType: string
}>()

const emit = defineEmits<{
  (e: 'success'): void
}>()

const message = useMessage()
const { t } = useI18n('iot') // 国际化
const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单提交 loading 状态
const isClient = computed(() => props.protocolType === ProtocolTypeEnum.MODBUS_TCP_CLIENT) // 是否为 Client 模式
const isServer = computed(() => props.protocolType === ProtocolTypeEnum.MODBUS_TCP_SERVER) // 是否为 Server 模式
const formData = ref<DeviceModbusConfigVO>({
  deviceId: props.deviceId,
  ip: '',
  port: 502,
  slaveId: 1,
  timeout: 3000,
  retryInterval: 10000,
  mode: ModbusModeEnum.POLLING,
  frameFormat: ModbusFrameFormatEnum.MODBUS_TCP,
  status: CommonStatusEnum.ENABLE
})
const formRules = computed(() => {
  const rules: Record<string, any[]> = {
    slaveId: [{ required: true, message: t('common.required'), trigger: 'blur' }]
  }
  if (isClient.value) {
    rules.ip = [{ required: true, message: t('common.required'), trigger: 'blur' }]
    rules.port = [{ required: true, message: t('common.required'), trigger: 'blur' }]
    rules.timeout = [{ required: true, message: t('common.required'), trigger: 'blur' }]
    rules.retryInterval = [{ required: true, message: t('common.required'), trigger: 'blur' }]
  }
  if (isServer.value) {
    rules.mode = [{ required: true, message: t('common.required'), trigger: 'change' }]
    rules.frameFormat = [{ required: true, message: t('common.required'), trigger: 'change' }]
  }
  return rules
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (data?: DeviceModbusConfigVO) => {
  dialogVisible.value = true
  resetForm()
  // 编辑模式
  if (data && data.id) {
    formData.value = { ...data }
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    deviceId: props.deviceId,
    ip: '',
    port: 502,
    slaveId: 1,
    timeout: 3000,
    retryInterval: 10000,
    mode: ModbusModeEnum.POLLING,
    frameFormat: ModbusFrameFormatEnum.MODBUS_TCP,
    status: CommonStatusEnum.ENABLE
  }
  formRef.value?.resetFields()
}

/** 提交表单 */
const submitForm = async () => {
  // 校验表单
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    formData.value.deviceId = props.deviceId
    await DeviceModbusConfigApi.saveModbusConfig(formData.value)
    message.success(t('common.success'))
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 暴露方法 */
defineExpose({ open })
</script>
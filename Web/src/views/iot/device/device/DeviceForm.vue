<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="auto"
      v-loading="formLoading"
    >
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('common.product')" prop="productId">
            <el-select
              v-model="formData.productId"
              :placeholder="t('common.selectProduct')"
              :disabled="formType === 'update'"
              clearable
              @change="handleProductChange"
            >
              <el-option
                v-for="product in products"
                :key="product.id"
                :label="product.name"
                :value="product.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="DeviceName" prop="deviceName">
            <el-input
              v-model="formData.deviceName"
              :placeholder="t('common.inputDeviceName')"
              :disabled="formType === 'update'"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-collapse>
        <el-collapse-item :title="t('common.moreConfig')">
          <el-form-item :label="t('device.device.nickname')" prop="nickname">
            <el-input v-model="formData.nickname" :placeholder="t('common.inputNickname')" />
          </el-form-item>
          <el-form-item :label="t('device.device.picUrl')" prop="picUrl">
            <UploadImg v-model="formData.picUrl" :height="'120px'" :width="'120px'" />
          </el-form-item>
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
          <el-form-item :label="t('device.device.serialNumber')" prop="serialNumber">
            <el-input v-model="formData.serialNumber" :placeholder="t('common.inputSerialNumber')" />
          </el-form-item>
          <el-form-item :label="t('device.device.longitude')" prop="longitude">
            <div class="flex items-center gap-2 w-full">
              <el-input v-model="formData.longitude" :placeholder="t('device.device.longitude')" class="flex-1">
                <template #prepend>{{ t('device.device.longitude') }}</template>
              </el-input>
              <el-input v-model="formData.latitude" :placeholder="t('device.device.latitude')" class="flex-1">
                <template #prepend>{{ t('device.device.latitude') }}</template>
              </el-input>
              <el-button type="primary" @click="openMapDialog">{{ t('device.device.coordinatePick') }}</el-button>
            </div>
          </el-form-item>
        </el-collapse-item>
      </el-collapse>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">{{ t('common.ok') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
  <!-- 地图选择弹窗 -->
  <MapDialog ref="mapDialogRef" @confirm="handleMapConfirm" />
</template>
<script setup lang="ts">
import { DeviceApi, DeviceVO } from '@/api/iot/device/device'
import { DeviceGroupApi } from '@/api/iot/device/group'
import { DeviceTypeEnum, ProductApi, ProductVO } from '@/api/iot/product/product'
import { UploadImg } from '@/components/UploadFile'
import { MapDialog } from '@/components/Map'
import { ref } from 'vue'

/** IoT 设备表单 */
defineOptions({ name: 'IoTDeviceForm' })

const { t } = useI18n('iot') // 国际化
const message = useMessage() // 消息窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const mapDialogRef = ref() // 地图弹窗 Ref

const formData = ref({
  id: undefined,
  productId: undefined,
  deviceName: undefined,
  nickname: undefined,
  picUrl: undefined,
  deviceType: undefined as number | undefined,
  serialNumber: undefined,
  longitude: undefined as number | string | undefined,
  latitude: undefined as number | string | undefined,
  groupIds: [] as number[]
})

/** 打开地图选择弹窗 */
const openMapDialog = () => {
  mapDialogRef.value?.open(
    formData.value.longitude ? Number(formData.value.longitude) : undefined,
    formData.value.latitude ? Number(formData.value.latitude) : undefined
  )
}

/** 处理地图选择确认 */
const handleMapConfirm = (data: { longitude: string; latitude: string; address: string }) => {
  formData.value.longitude = data.longitude
  formData.value.latitude = data.latitude
}

const formRules = reactive({
  productId: [{ required: true, message: t('common.productRequired'), trigger: 'blur' }],
  deviceName: [
    { required: true, message: t('common.deviceNameRequired'), trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9_.\-:@]{4,32}$/,
      message: t('common.deviceNameFormat'),
      trigger: 'blur'
    }
  ],
  nickname: [
    {
      validator: (_rule, value: any, callback) => {
        if (value === undefined || value === null) {
          callback()
          return
        }
        const length = value.replace(/[\u4e00-\u9fa5\u3040-\u30ff]/g, 'aa').length
        if (length < 4 || length > 64) {
          callback(new Error(t('device.device.nameLengthLimit')))
        } else if (!/^[\u4e00-\u9fa5\u3040-\u30ff_a-zA-Z0-9]+$/.test(value)) {
          callback(new Error(t('device.device.nameCharLimit')))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  serialNumber: [
    {
      pattern: /^[a-zA-Z0-9-_]+$/,
      message: t('device.device.serialNumberFormat'),
      trigger: 'blur'
    }
  ],
  longitude: [
    {
      validator: (_rule: any, value: any, callback: any) => {
        if (value !== undefined && value !== null && value !== '') {
          const num = Number(value)
          if (isNaN(num)) {
            callback(new Error(t('device.device.longitudeMustBeNumber')))
            return
          }
          if (num < -180 || num > 180) {
            callback(new Error(t('device.device.longitudeRange')))
            return
          }
          if (!formData.value.latitude && formData.value.latitude !== 0) {
            callback(new Error(t('device.device.fillBothLongitudeLatitude')))
            return
          }
        }
        callback()
      },
      trigger: 'blur'
    }
  ],
  latitude: [
    {
      validator: (_rule: any, value: any, callback: any) => {
        if (value !== undefined && value !== null && value !== '') {
          const num = Number(value)
          if (isNaN(num)) {
            callback(new Error(t('device.device.latitudeMustBeNumber')))
            return
          }
          if (num < -90 || num > 90) {
            callback(new Error(t('device.device.latitudeRange')))
            return
          }
          if (!formData.value.longitude && formData.value.longitude !== 0) {
            callback(new Error(t('device.device.fillBothLatitudeLongitude')))
            return
          }
        }
        callback()
      },
      trigger: 'blur'
    }
  ]
})
const formRef = ref() // 表单 Ref
const products = ref<ProductVO[]>([]) // 产品列表
const deviceGroups = ref<any[]>([])

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
      formData.value = await DeviceApi.getDevice(id)
    } finally {
      formLoading.value = false
    }
  }

  // 加载产品列表
  products.value = await ProductApi.getSimpleProductList()
  // 加载设备分组列表
  deviceGroups.value = await DeviceGroupApi.getSimpleDeviceGroupList()
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
    const data = formData.value as unknown as DeviceVO
    if (formType.value === 'create') {
      await DeviceApi.createDevice(data)
      message.success(t('common.createSuccess'))
    } else {
      await DeviceApi.updateDevice(data)
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
    productId: undefined,
    deviceName: undefined,
    nickname: undefined,
    picUrl: undefined,
    deviceType: undefined,
    serialNumber: undefined,
    longitude: undefined,
    latitude: undefined,
    groupIds: []
  }
  formRef.value?.resetFields()
}

/** 产品选择变化 */
const handleProductChange = (productId: number) => {
  if (!productId) {
    formData.value.deviceType = undefined
    return
  }
  const product = products.value?.find((item) => item.id === productId)
  formData.value.deviceType = product?.deviceType
}
</script>
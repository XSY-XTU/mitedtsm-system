<template>
  <el-form
    ref="formRef"
    :model="formData"
    :rules="formRules"
    label-width="0px"
    :inline-message="true"
  >
    <el-table :data="formData" class="-mt-10px" :table-layout="'auto'">
      <el-table-column :label="t('rule.data.product')" min-width="150">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.productId`" :rules="formRules.productId" class="mb-0px!">
            <el-select
              v-model="row.productId"
              :placeholder="t('rule.data.selectProduct')"
              @change="handleProductChange(row, $index)"
              clearable
              filterable
              style="width: 100%"
            >
              <el-option
                v-for="product in productList"
                :key="product.id"
                :label="product.name"
                :value="product.id"
              />
            </el-select>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column :label="t('rule.data.device')" min-width="150">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.deviceId`" :rules="formRules.deviceId" class="mb-0px!">
            <el-select
              v-model="row.deviceId"
              :placeholder="t('rule.data.selectDevice')"
              clearable
              filterable
              style="width: 100%"
            >
              <el-option :label="t('rule.data.allDevices')" :value="0" />
              <el-option
                v-for="device in getFilteredDevices(row.productId)"
                :key="device.id"
                :label="device.deviceName"
                :value="device.id"
              />
            </el-select>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column :label="t('rule.data.message')" min-width="150">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.method`" :rules="formRules.method" class="mb-0px!">
            <el-select
              v-model="row.method"
              :placeholder="t('rule.data.selectMessage')"
              @change="handleMethodChange(row, $index)"
              clearable
              filterable
              style="width: 100%"
            >
              <el-option
                v-for="method in upstreamMethods"
                :key="method.method"
                :label="method.name"
                :value="method.method"
              />
            </el-select>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column :label="t('rule.data.identifier')" min-width="200">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.identifier`" class="mb-0px!">
            <el-select
              v-if="shouldShowIdentifierSelect(row)"
              v-model="row.identifier"
              :placeholder="t('rule.data.selectIdentifier')"
              clearable
              filterable
              style="width: 100%"
              v-loading="row.identifierLoading"
            >
              <el-option
                v-for="item in getThingModelOptions(row)"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column align="center" fixed="right" :label="t('rule.data.operation')" min-width="60">
        <template #default="{ $index }">
          <el-button @click="handleDelete($index)" link type="danger">—</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-row justify="center" class="mt-3">
      <el-button @click="handleAdd" type="primary" plain round>+ {{ t('rule.data.addDataSource') }}</el-button>
    </el-row>
  </el-form>
</template>

<script setup lang="ts">
import { ProductApi } from '@/api/iot/product/product'
import { DeviceApi } from '@/api/iot/device/device'
import { ThingModelApi } from '@/api/iot/thingmodel'
import { IotDeviceMessageMethodEnum, IoTThingModelTypeEnum } from '@/views/iot/utils/constants'

const { t } = useI18n('iot')

const formData = ref<any[]>([])
const productList = ref<any[]>([]) // 产品列表
const deviceList = ref<any[]>([]) // 设备列表
const thingModelCache = ref<Map<number, any[]>>(new Map()) // 缓存物模型数据，key 为 productId

const formRules = reactive({
  productId: [{ required: true, message: t('rule.data.productRequired'), trigger: 'change' }],
  deviceId: [{ required: true, message: t('rule.data.deviceRequired'), trigger: 'change' }],
  method: [{ required: true, message: t('rule.data.methodRequired'), trigger: 'change' }]
})
const formRef = ref() // 表单 Ref

// 获取上行消息方法列表
const upstreamMethods = computed(() => {
  return Object.values(IotDeviceMessageMethodEnum).filter((item) => item.upstream)
})

/** 根据产品 ID 过滤设备 */
const getFilteredDevices = (productId: number) => {
  if (!productId) return []
  return deviceList.value.filter((device: any) => device.productId === productId)
}

/** 判断是否需要显示标识符选择器 */
const shouldShowIdentifierSelect = (row: any) => {
  return [
    IotDeviceMessageMethodEnum.EVENT_POST.method,
    IotDeviceMessageMethodEnum.PROPERTY_POST.method
  ].includes(row.method)
}

/** 获取物模型选项 */
const getThingModelOptions = (row: any) => {
  if (!row.productId || !shouldShowIdentifierSelect(row)) {
    return []
  }
  const thingModels: any[] = thingModelCache.value.get(row.productId) || []
  let filteredModels: any[] = []
  if (row.method === IotDeviceMessageMethodEnum.EVENT_POST.method) {
    filteredModels = thingModels.filter((item: any) => item.type === IoTThingModelTypeEnum.EVENT)
  } else if (row.method === IotDeviceMessageMethodEnum.PROPERTY_POST.method) {
    filteredModels = thingModels.filter((item: any) => item.type === IoTThingModelTypeEnum.PROPERTY)
  }
  return filteredModels.map((item: any) => ({
    label: `${item.name} (${item.identifier})`,
    value: item.identifier
  }))
}

/** 加载产品列表 */
const loadProductList = async () => {
  try {
    productList.value = await ProductApi.getSimpleProductList()
  } catch (error) {
    console.error('加载产品列表失败:', error)
  }
}

/** 加载设备列表 */
const loadDeviceList = async () => {
  try {
    deviceList.value = await DeviceApi.getSimpleDeviceList()
  } catch (error) {
    console.error('加载设备列表失败:', error)
  }
}

/** 加载物模型数据 */
const loadThingModel = async (productId: number) => {
  // 已缓存，无需重复加载
  if (thingModelCache.value.has(productId)) {
    return
  }
  try {
    const thingModels = await ThingModelApi.getThingModelList({ productId })
    thingModelCache.value.set(productId, thingModels)
  } catch (error) {
    console.error('加载物模型失败:', error)
  }
}

/** 产品变化时处理 */
const handleProductChange = async (row: any, _index: number) => {
  row.deviceId = 0
  row.method = undefined
  row.identifier = undefined
  row.identifierLoading = false
}

/** 消息方法变化时处理 */
const handleMethodChange = async (row: any, _index: number) => {
  // 清空标识符
  row.identifier = undefined
  // 如果需要加载物模型数据
  if (shouldShowIdentifierSelect(row) && row.productId) {
    row.identifierLoading = true
    await loadThingModel(row.productId)
    row.identifierLoading = false
  }
}

/** 新增按钮操作 */
const handleAdd = () => {
  const row = {
    productId: undefined,
    deviceId: undefined,
    method: undefined,
    identifier: undefined,
    identifierLoading: false
  }
  formData.value.push(row)
}

/** 删除按钮操作 */
const handleDelete = (index: number) => {
  formData.value.splice(index, 1)
}

/** 表单校验 */
const validate = () => {
  return formRef.value.validate()
}

/** 表单值 */
const getData = () => {
  return formData.value
}

/** 设置表单值 */
const setData = (data: any[]) => {
  // 确保每个项都有必要的字段
  formData.value = (data || []).map((item) => ({
    ...item,
    identifierLoading: false
  }))
  // 为已有数据预加载物模型
  data?.forEach(async (item) => {
    if (item.productId && shouldShowIdentifierSelect(item)) {
      await loadThingModel(item.productId)
    }
  })
}

/** 初始化 */
onMounted(async () => {
  await Promise.all([loadProductList(), loadDeviceList()])
})

defineExpose({ validate, getData, setData })
</script>

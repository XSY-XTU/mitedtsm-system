<!-- Modbus 配置 -->
<template>
  <div>
    <!-- 连接配置区域 -->
    <ContentWrap>
      <div class="flex items-center justify-between mb-4">
        <span class="text-lg font-medium">{{ t('modbus.config') }}</span>
        <el-button type="primary" @click="handleEditConfig" v-hasPermi="['iot:device:create']">
          {{ t('common.edit') }}
        </el-button>
      </div>

      <!-- 详情展示 -->
      <el-descriptions :column="3" border direction="horizontal">
        <!-- Client 模式专有字段 -->
        <template v-if="isClient">
          <el-descriptions-item label="IP">
            {{ modbusConfig.ip || '-' }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('modbus.port')">
            {{ modbusConfig.port || '-' }}
          </el-descriptions-item>
        </template>
        <!-- 公共字段 -->
        <el-descriptions-item :label="t('modbus.slaveId')">
          {{ modbusConfig.slaveId || '-' }}
        </el-descriptions-item>
        <!-- Client 模式专有字段 -->
        <template v-if="isClient">
          <el-descriptions-item :label="t('modbus.connectionType')">
            {{ modbusConfig.timeout ? `${modbusConfig.timeout} ms` : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="Retry">
            {{ modbusConfig.retryInterval ? `${modbusConfig.retryInterval} ms` : '-' }}
          </el-descriptions-item>
        </template>
        <!-- Server 模式专有字段 -->
        <template v-if="isServer">
          <el-descriptions-item :label="t('common.type')">
            <dict-tag :type="DICT_TYPE.IOT_MODBUS_MODE" :value="modbusConfig.mode" />
          </el-descriptions-item>
          <el-descriptions-item label="Frame">
            <dict-tag :type="DICT_TYPE.IOT_MODBUS_FRAME_FORMAT" :value="modbusConfig.frameFormat" />
          </el-descriptions-item>
        </template>
        <!-- 公共字段 -->
        <el-descriptions-item :label="t('common.status')">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="modbusConfig.status" />
        </el-descriptions-item>
      </el-descriptions>
    </ContentWrap>

    <!-- 点位配置区域 -->
    <ContentWrap class="mt-4">
      <div class="flex items-center justify-between mb-4">
        <span class="text-lg font-medium">{{ t('modbus.pointConfig') }}</span>
        <el-button type="primary" @click="handleAddPoint" v-hasPermi="['iot:device:create']">
          <Icon icon="ep:plus" class="mr-1" />
          {{ t('modbus.addPoint') }}
        </el-button>
      </div>

      <!-- 搜索栏 -->
      <el-form :model="queryParams" :inline="true" class="-mb-15px">
        <el-form-item :label="t('thing.model.name')" prop="name">
          <el-input
            v-model="queryParams.name"
            :placeholder="t('thing.model.namePlaceholder')"
            clearable
            class="!w-200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item :label="t('thing.model.identifier')" prop="identifier">
          <el-input
            v-model="queryParams.identifier"
            :placeholder="t('thing.model.identifierPlaceholder')"
            clearable
            class="!w-200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button @click="handleQuery">
            <Icon icon="ep:search" class="mr-5px" />
            {{ t('common.search') }}
          </el-button>
          <el-button @click="resetQuery">
            <Icon icon="ep:refresh" class="mr-5px" />
            {{ t('common.reset') }}
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 点位列表 -->
      <el-table v-loading="pointLoading" :data="pointList" :stripe="true" class="mt-4" :table-layout="'auto'">
        <el-table-column :label="t('thing.model.name')" align="center" prop="name" min-width="100" />
        <el-table-column :label="t('thing.model.identifier')" align="center" prop="identifier" min-width="100">
          <template #default="scope">
            <el-tag size="small" type="primary">{{ scope.row.identifier }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('modbus.functionCode')" align="center" prop="functionCode" min-width="140">
          <template #default="scope">
            {{ formatFunctionCode(scope.row.functionCode) }}
          </template>
        </el-table-column>
        <el-table-column :label="t('modbus.registerAddress')" align="center" prop="registerAddress" min-width="100">
          <template #default="scope">
            {{ formatRegisterAddress(scope.row.registerAddress) }}
          </template>
        </el-table-column>
        <el-table-column label="Count" align="center" prop="registerCount" min-width="90" />
        <el-table-column :label="t('thing.model.dataType')" align="center" prop="rawDataType" min-width="90">
          <template #default="scope">
            <el-tag size="small" type="info">{{ scope.row.rawDataType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Byte Order" align="center" prop="byteOrder" min-width="80" />
        <el-table-column label="Scale" align="center" prop="scale" min-width="80" />
        <el-table-column :label="t('modbus.pollInterval')" align="center" prop="pollInterval" min-width="90">
          <template #default="scope"> {{ scope.row.pollInterval }} ms </template>
        </el-table-column>
        <el-table-column :label="t('common.status')" align="center" prop="status" min-width="80">
          <template #default="scope">
            <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column :label="t('common.operation')" align="center" fixed="right" min-width="150">
          <template #default="scope">
            <el-button
              link
              type="primary"
              @click="handleEditPoint(scope.row)"
              v-hasPermi="['iot:device:update']"
            >
              {{ t('common.edit') }}
            </el-button>
            <el-button
              link
              type="danger"
              @click="handleDeletePoint(scope.row.id, scope.row.name)"
              v-hasPermi="['iot:device:delete']"
            >
              {{ t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <Pagination
        :total="total"
        v-model:page="queryParams.pageNo"
        v-model:limit="queryParams.pageSize"
        @pagination="getPointPage"
      />
    </ContentWrap>

    <!-- 连接配置弹窗 -->
    <DeviceModbusConfigForm
      ref="configFormRef"
      :device-id="device.id"
      :protocol-type="product.protocolType"
      @success="getModbusConfig"
    />

    <!-- 点位表单弹窗 -->
    <DeviceModbusPointForm
      ref="pointFormRef"
      :device-id="device.id"
      :thing-model-list="thingModelList"
      @success="getPointPage"
    />
  </div>
</template>

<script lang="ts" setup>
import { DeviceVO } from '@/api/iot/device/device'
import { ProductVO, ProtocolTypeEnum } from '@/api/iot/product/product'
import { ThingModelData } from '@/api/iot/thingmodel'
import { DeviceModbusConfigApi, DeviceModbusConfigVO } from '@/api/iot/device/modbus/config'
import { DeviceModbusPointApi, DeviceModbusPointVO } from '@/api/iot/device/modbus/point'
import { ModbusFunctionCodeOptions } from '@/views/iot/utils/constants'
import { DICT_TYPE } from '@/utils/dict'
import DeviceModbusConfigForm from './DeviceModbusConfigForm.vue'
import DeviceModbusPointForm from './DeviceModbusPointForm.vue'

defineOptions({ name: 'DeviceModbusConfig' })

const props = defineProps<{
  device: DeviceVO
  product: ProductVO
  thingModelList: ThingModelData[]
}>()

const message = useMessage()
const { t } = useI18n('iot') // 国际化

// ======================= 连接配置 =======================
const isClient = computed(() => props.product.protocolType === ProtocolTypeEnum.MODBUS_TCP_CLIENT) // 是否为 Client 模式
const isServer = computed(() => props.product.protocolType === ProtocolTypeEnum.MODBUS_TCP_SERVER) // 是否为 Server 模式
const modbusConfig = ref<DeviceModbusConfigVO>({} as DeviceModbusConfigVO)

/** 获取连接配置 */
const getModbusConfig = async () => {
  modbusConfig.value = await DeviceModbusConfigApi.getModbusConfig(props.device.id)
}

/** 编辑连接配置 */
const configFormRef = ref()
const handleEditConfig = () => {
  configFormRef.value?.open(modbusConfig.value)
}

// ======================= 点位配置 =======================
const pointLoading = ref(false)
const pointList = ref<DeviceModbusPointVO[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  deviceId: props.device.id,
  name: undefined as string | undefined,
  identifier: undefined as string | undefined
})

/** 获取点位分页 */
const getPointPage = async () => {
  pointLoading.value = true
  try {
    const data = await DeviceModbusPointApi.getModbusPointPage(queryParams)
    pointList.value = data.list
    total.value = data.total
  } finally {
    pointLoading.value = false
  }
}

/** 搜索 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getPointPage()
}

/** 重置搜索 */
const resetQuery = () => {
  queryParams.name = undefined
  queryParams.identifier = undefined
  handleQuery()
}

/** 格式化功能码 */
const formatFunctionCode = (code: number) => {
  const option = ModbusFunctionCodeOptions.find((item) => item.value === code)
  return option ? option.label : `${code}`
}

/** 格式化寄存器地址为十六进制 */
const formatRegisterAddress = (address: number) => {
  return '0x' + address.toString(16).toUpperCase().padStart(4, '0')
}

/** 新增点位 */
const pointFormRef = ref()
const handleAddPoint = () => {
  pointFormRef.value?.open('create')
}

/** 编辑点位 */
const handleEditPoint = (row: DeviceModbusPointVO) => {
  pointFormRef.value?.open('update', row.id)
}

/** 删除点位 */
const handleDeletePoint = async (id: number, name: string) => {
  try {
    // 删除的二次确认
    await message.delConfirm(t('common.confirmOperation'))
    // 发起删除
    await DeviceModbusPointApi.deleteModbusPoint(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getPointPage()
  } catch {}
}

/** 初始化 */
onMounted(async () => {
  await getModbusConfig()
  await getPointPage()
})
</script>
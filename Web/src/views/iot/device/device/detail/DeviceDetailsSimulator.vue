<!-- 模拟设备 -->
<template>
  <ContentWrap>
    <el-row :gutter="20">
      <!-- 左侧指令调试区域 -->
      <el-col :span="12">
        <el-tabs v-model="activeTab" type="border-card">
          <!-- 上行指令调试 -->
          <el-tab-pane :label="t('simulator.upstreamDebug')" name="upstream">
            <el-tabs v-if="activeTab === 'upstream'" v-model="upstreamTab">
              <!-- 属性上报 -->
              <el-tab-pane :label="t('simulator.propertyReport')" :name="IotDeviceMessageMethodEnum.PROPERTY_POST.method">
                <ContentWrap>
                  <el-table :data="propertyList" :show-overflow-tooltip="true" :stripe="true" :table-layout="'auto'">
                    <el-table-column
                      fixed="left"
                      align="center"
                      :label="t('thing.model.name')"
                      prop="name"
                      min-width="120"
                    />
                    <el-table-column
                      fixed="left"
                      align="center"
                      :label="t('thing.model.identifier')"
                      prop="identifier"
                      min-width="120"
                    />
                    <el-table-column align="center" :label="t('thing.model.dataType')" min-width="100">
                      <template #default="{ row }">
                        {{ row.property?.dataType ?? '-' }}
                      </template>
                    </el-table-column>
                    <el-table-column align="left" :label="t('thing.model.dataDefinition')" min-width="200">
                      <template #default="{ row }">
                        <DataDefinition :data="row" />
                      </template>
                    </el-table-column>
                    <el-table-column fixed="right" align="center" :label="t('thing.model.value')" min-width="150">
                      <template #default="scope">
                        <el-input
                          :model-value="getFormValue(scope.row.identifier)"
                          @update:model-value="setFormValue(scope.row.identifier, $event)"
                          :placeholder="t('simulator.inputValue')"
                          size="small"
                        />
                      </template>
                    </el-table-column>
                  </el-table>
                  <div class="flex justify-between items-center mt-4">
                    <span class="text-sm text-gray-600">
                      {{ t('simulator.setPropertyThenSend') }}
                    </span>
                    <el-button type="primary" @click="handlePropertyPost">{{ t('simulator.sendPropertyReport') }}</el-button>
                  </div>
                </ContentWrap>
              </el-tab-pane>

              <!-- 事件上报 -->
              <el-tab-pane :label="t('simulator.eventReport')" :name="IotDeviceMessageMethodEnum.EVENT_POST.method">
                <ContentWrap>
                  <el-table :data="eventList" :show-overflow-tooltip="true" :stripe="true" :table-layout="'auto'">
                    <el-table-column
                      fixed="left"
                      align="center"
                      :label="t('thing.model.name')"
                      prop="name"
                      min-width="120"
                    />
                    <el-table-column
                      fixed="left"
                      align="center"
                      :label="t('thing.model.identifier')"
                      prop="identifier"
                      min-width="120"
                    />
                    <el-table-column align="center" :label="t('thing.model.dataType')" min-width="100">
                      <template #default="{ row }">
                        {{ row.event?.dataType ?? '-' }}
                      </template>
                    </el-table-column>
                    <el-table-column align="left" :label="t('thing.model.dataDefinition')" min-width="200">
                      <template #default="{ row }">
                        <DataDefinition :data="row" />
                      </template>
                    </el-table-column>
                    <el-table-column align="center" :label="t('thing.model.value')" min-width="200">
                      <template #default="scope">
                        <el-input
                          :model-value="getFormValue(scope.row.identifier)"
                          @update:model-value="setFormValue(scope.row.identifier, $event)"
                          type="textarea"
                          :rows="3"
                          :placeholder="t('simulator.inputJsonValue')"
                          size="small"
                        />
                      </template>
                    </el-table-column>
                    <el-table-column fixed="right" align="center" :label="t('common.operation')" min-width="150">
                      <template #default="scope">
                        <el-button type="primary" size="small" @click="handleEventPost(scope.row)">
                          {{ t('simulator.reportEvent') }}
                        </el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                </ContentWrap>
              </el-tab-pane>

              <!-- 状态变更 -->
              <el-tab-pane :label="t('simulator.stateChange')" :name="IotDeviceMessageMethodEnum.STATE_UPDATE.method">
                <ContentWrap>
                  <div class="flex gap-4">
                    <el-button type="primary" @click="handleDeviceState(DeviceStateEnum.ONLINE)">
                      {{ t('simulator.deviceOnline') }}
                    </el-button>
                    <el-button type="danger" @click="handleDeviceState(DeviceStateEnum.OFFLINE)">
                      {{ t('simulator.deviceOffline') }}
                    </el-button>
                  </div>
                </ContentWrap>
              </el-tab-pane>
            </el-tabs>
          </el-tab-pane>

          <!-- 下行指令调试 -->
          <el-tab-pane :label="t('simulator.downstreamDebug')" name="downstream">
            <el-tabs v-if="activeTab === 'downstream'" v-model="downstreamTab">
              <!-- 属性调试 -->
              <el-tab-pane :label="t('simulator.propertySet')" :name="IotDeviceMessageMethodEnum.PROPERTY_SET.method">
                <ContentWrap>
                  <el-table :data="propertyList" :show-overflow-tooltip="true" :stripe="true" :table-layout="'auto'">
                    <el-table-column
                      fixed="left"
                      align="center"
                      :label="t('thing.model.name')"
                      prop="name"
                      min-width="120"
                    />
                    <el-table-column
                      fixed="left"
                      align="center"
                      :label="t('thing.model.identifier')"
                      prop="identifier"
                      min-width="120"
                    />
                    <el-table-column align="center" :label="t('thing.model.dataType')" min-width="100">
                      <template #default="{ row }">
                        {{ row.property?.dataType ?? '-' }}
                      </template>
                    </el-table-column>
                    <el-table-column align="left" :label="t('thing.model.dataDefinition')" min-width="200">
                      <template #default="{ row }">
                        <DataDefinition :data="row" />
                      </template>
                    </el-table-column>
                    <el-table-column fixed="right" align="center" :label="t('thing.model.value')" min-width="150">
                      <template #default="scope">
                        <el-input
                          :model-value="getFormValue(scope.row.identifier)"
                          @update:model-value="setFormValue(scope.row.identifier, $event)"
                          :placeholder="t('simulator.inputValue')"
                          size="small"
                        />
                      </template>
                    </el-table-column>
                  </el-table>
                  <div class="flex justify-between items-center mt-4">
                    <span class="text-sm text-gray-600">
                      {{ t('simulator.setPropertyThenSendSet') }}
                    </span>
                    <el-button type="primary" @click="handlePropertySet">{{ t('simulator.sendPropertySet') }}</el-button>
                  </div>
                </ContentWrap>
              </el-tab-pane>

              <!-- 服务调用 -->
              <el-tab-pane
                :label="t('simulator.deviceServiceCall')"
                :name="IotDeviceMessageMethodEnum.SERVICE_INVOKE.method"
              >
                <ContentWrap>
                  <el-table :data="serviceList" :show-overflow-tooltip="true" :stripe="true" :table-layout="'auto'">
                    <el-table-column
                      fixed="left"
                      align="center"
                      :label="t('thinkModelFunction.service.name')"
                      prop="name"
                      min-width="120"
                    />
                    <el-table-column
                      fixed="left"
                      align="center"
                      :label="t('thing.model.identifier')"
                      prop="identifier"
                      min-width="120"
                    />
                    <el-table-column align="left" :label="t('common.inputParams')" min-width="200">
                      <template #default="{ row }">
                        <DataDefinition :data="row" />
                      </template>
                    </el-table-column>
                    <el-table-column align="center" :label="t('thing.model.parameterValue')" min-width="200">
                      <template #default="scope">
                        <el-input
                          :model-value="getFormValue(scope.row.identifier)"
                          @update:model-value="setFormValue(scope.row.identifier, $event)"
                          type="textarea"
                          :rows="3"
                          :placeholder="t('simulator.inputServiceJson')"
                          size="small"
                        />
                      </template>
                    </el-table-column>
                    <el-table-column fixed="right" align="center" :label="t('common.operation')" min-width="150">
                      <template #default="scope">
                        <el-button
                          type="primary"
                          size="small"
                          @click="handleServiceInvoke(scope.row)"
                        >
                          {{ t('simulator.serviceCall') }}
                        </el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                </ContentWrap>
              </el-tab-pane>
            </el-tabs>
          </el-tab-pane>
        </el-tabs>
      </el-col>

      <!-- 右侧设备日志区域 -->
      <el-col :span="12">
        <ContentWrap :title="t('simulator.deviceMessage')">
          <DeviceDetailsMessage ref="deviceMessageRef" :device-id="device.id" />
        </ContentWrap>
      </el-col>
    </el-row>
  </ContentWrap>
</template>

<script lang="ts" setup>
import { ProductVO } from '@/api/iot/product/product'
import { ThingModelData } from '@/api/iot/thingmodel'
import { DeviceApi, DeviceVO } from '@/api/iot/device/device'
import DeviceDetailsMessage from './DeviceDetailsMessage.vue'
import { DataDefinition } from '@/views/iot/thingmodel/components'
import {
  DeviceStateEnum,
  IotDeviceMessageMethodEnum,
  IoTThingModelTypeEnum
} from '@/views/iot/utils/constants'

const props = defineProps<{
  product: ProductVO
  device: DeviceVO
  thingModelList: ThingModelData[]
}>()

const message = useMessage() // 消息弹窗
const { t } = useI18n('iot') // 国际化
const activeTab = ref('upstream') // 上行upstream、下行downstream
const upstreamTab = ref(IotDeviceMessageMethodEnum.PROPERTY_POST.method) // 上行子标签
const downstreamTab = ref(IotDeviceMessageMethodEnum.PROPERTY_SET.method) // 下行子标签
const deviceMessageRef = ref() // 设备消息组件引用
const deviceMessageRefreshDelay = 2000 // 延迟 N 秒，保证模拟上行的消息被处理

// 表单数据：存储用户输入的模拟值
const formData = ref<Record<string, string>>({})

// 根据类型过滤物模型数据
const getFilteredThingModelList = (type: number) => {
  return props.thingModelList.filter((item) => item.type === type)
}
const propertyList = computed(() => getFilteredThingModelList(IoTThingModelTypeEnum.PROPERTY))
const eventList = computed(() => getFilteredThingModelList(IoTThingModelTypeEnum.EVENT))
const serviceList = computed(() => getFilteredThingModelList(IoTThingModelTypeEnum.SERVICE))

/** 获取表单值的辅助函数 */
const getFormValue = (identifier: string | number | undefined) => {
  if (!identifier) return ''
  return formData.value[String(identifier)] || ''
}
/** 设置表单值的辅助函数 */
const setFormValue = (identifier: string | number | undefined, value: string) => {
  if (!identifier) return
  formData.value[String(identifier)] = value
}

/** 模拟属性上报 */
const handlePropertyPost = async () => {
  const data: Record<string, any> = {}
  propertyList.value.forEach((item) => {
    const value = getFormValue(item.identifier)
    if (value && item.identifier) {
      data[String(item.identifier)] = value
    }
  })
  if (Object.keys(data).length === 0) {
    message.warning(t('simulator.pleaseSetOneProperty'))
    return
  }

  try {
    await DeviceApi.sendDeviceMessage({
      deviceId: props.device.id,
      method: IotDeviceMessageMethodEnum.PROPERTY_POST.method,
      params: data
    })
    message.success(t('simulator.propertyReportSuccess'))
    deviceMessageRef.value.refresh(deviceMessageRefreshDelay)
  } catch (error) {
    message.error(t('simulator.propertyReportFailed'))
  }
}

/** 模拟事件上报 */
const handleEventPost = async (eventItem: ThingModelData) => {
  const value = getFormValue(eventItem.identifier)
  if (!value) {
    message.warning(t('simulator.pleaseInputEventParams'))
    return
  }
  let eventParams: any
  try {
    eventParams = JSON.parse(value)
  } catch {
    message.error(t('simulator.jsonFormatError'))
    return
  }

  try {
    await DeviceApi.sendDeviceMessage({
      deviceId: props.device.id,
      method: IotDeviceMessageMethodEnum.EVENT_POST.method,
      params: {
        identifier: String(eventItem.identifier),
        value: eventParams,
        time: Date.now()
      }
    })
    message.success(t('simulator.eventReportSuccess', { name: String(eventItem.name) }))
    deviceMessageRef.value.refresh(deviceMessageRefreshDelay)
  } catch (error) {
    message.error(t('simulator.eventReportFailed', { name: String(eventItem.name) }))
  }
}

/** 模拟设备状态 */
const handleDeviceState = async (state: number) => {
  try {
    await DeviceApi.sendDeviceMessage({
      deviceId: props.device.id,
      method: IotDeviceMessageMethodEnum.STATE_UPDATE.method,
      params: {
        state: state
      }
    })
    message.success(state === DeviceStateEnum.ONLINE ? t('simulator.deviceOnlineSuccess') : t('simulator.deviceOfflineSuccess'))
    deviceMessageRef.value.refresh(deviceMessageRefreshDelay)
  } catch (error) {
    message.error(state === DeviceStateEnum.ONLINE ? t('simulator.deviceOnlineFailed') : t('simulator.deviceOfflineFailed'))
  }
}

/** 模拟属性设置 */
const handlePropertySet = async () => {
  const data: Record<string, any> = {}
  propertyList.value.forEach((item) => {
    const value = getFormValue(item.identifier)
    if (value && item.identifier) {
      data[String(item.identifier)] = value
    }
  })
  if (Object.keys(data).length === 0) {
    message.warning(t('simulator.pleaseSetOneProperty'))
    return
  }

  try {
    await DeviceApi.sendDeviceMessage({
      deviceId: props.device.id,
      method: IotDeviceMessageMethodEnum.PROPERTY_SET.method,
      params: data
    })
    message.success(t('simulator.propertySetSuccess'))
    deviceMessageRef.value.refresh(deviceMessageRefreshDelay)
  } catch (error) {
    message.error(t('simulator.propertySetFailed'))
  }
}

/** 模拟服务调用 */
const handleServiceInvoke = async (serviceItem: ThingModelData) => {
  const value = getFormValue(serviceItem.identifier)
  if (!value) {
    message.warning(t('simulator.pleaseInputServiceParams'))
    return
  }
  let serviceParams: any
  try {
    serviceParams = JSON.parse(value)
  } catch {
    message.error(t('simulator.serviceJsonFormatError'))
    return
  }

  try {
    await DeviceApi.sendDeviceMessage({
      deviceId: props.device.id,
      method: IotDeviceMessageMethodEnum.SERVICE_INVOKE.method,
      params: {
        identifier: String(serviceItem.identifier),
        inputParams: serviceParams
      }
    })
    message.success(t('simulator.serviceCallSuccess', { name: String(serviceItem.name) }))
    deviceMessageRef.value.refresh(deviceMessageRefreshDelay)
  } catch (error) {
    message.error(t('simulator.serviceCallFailed', { name: String(serviceItem.name) }))
  }
}
</script>
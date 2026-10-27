<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('alert.config.title')" prop="configId">
            <el-select
              v-model="queryParams.configId"
              :placeholder="t('common.selectPlaceholder')"
              clearable
              filterable
              class="!w-240px"
            >
              <el-option
                v-for="config in alertConfigList"
                :key="config.id"
                :label="config.name"
                :value="config.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('alert.config.level')" prop="configLevel">
            <el-select
              v-model="queryParams.configLevel"
              :placeholder="t('common.selectPlaceholder')"
              clearable
              class="!w-240px"
            >
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.IOT_ALERT_LEVEL)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('common.product')" prop="productId">
            <el-select
              v-model="queryParams.productId"
              :placeholder="t('common.selectProduct')"
              clearable
              filterable
              @change="handleProductChange"
              class="!w-240px"
            >
              <el-option
                v-for="product in productList"
                :key="product.id"
                :label="product.name"
                :value="product.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('device.device.title')" prop="deviceId">
            <el-select
              v-model="queryParams.deviceId"
              :placeholder="t('common.selectPlaceholder')"
              clearable
              filterable
              class="!w-240px"
            >
              <el-option
                v-for="device in filteredDeviceList"
                :key="device.id"
                :label="device.deviceName"
                :value="device.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('alert.record.handleStatus')" prop="processStatus">
            <el-select
              v-model="queryParams.processStatus"
              :placeholder="t('common.selectPlaceholder')"
              clearable
              class="!w-240px"
            >
              <el-option
                v-for="dict in getBoolDictOptions(DICT_TYPE.INFRA_BOOLEAN_STRING)"
                :key="String(dict.value)"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('common.createTime')" prop="createTime">
            <el-date-picker
              v-model="queryParams.createTime"
              value-format="YYYY-MM-DD HH:mm:ss"
              type="daterange"
              :start-placeholder="t('common.startTime')"
              :end-placeholder="t('common.endTime')"
              :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
              class="!w-220px"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> {{ t('common.search') }}</el-button>
            <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> {{ t('common.reset') }}</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table
      row-key="id"
      v-loading="loading"
      :data="list"
      :stripe="true"
      :show-overflow-tooltip="true"
     :table-layout="'auto'">
      <el-table-column :label="t('common.id')" align="center" prop="id" />
      <el-table-column :label="t('alert.record.alertName')" align="center" prop="configName" />
      <el-table-column :label="t('alert.record.alertLevel')" align="center" prop="configLevel">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.IOT_ALERT_LEVEL" :value="scope.row.configLevel" />
        </template>
      </el-table-column>
      <el-table-column :label="t('common.productName')" align="center" prop="productId">
        <template #default="scope">
          {{ getProductName(scope.row.productId) }}
        </template>
      </el-table-column>
      <el-table-column :label="t('alert.record.deviceName')" align="center" prop="deviceId">
        <template #default="scope">
          {{ getDeviceName(scope.row.deviceId) }}
        </template>
      </el-table-column>
      <el-table-column align="center" prop="deviceMessage">
        <template #header>
          {{ t('alert.record.deviceName') + ' ' + t('common.log') }}
        </template>
        <template #default="scope">
          <el-popover
            placement="top-start"
            :min-width="600"
            trigger="hover"
            v-if="scope.row.deviceMessage"
          >
            <template #reference>
              <el-button link type="primary">
                <Icon icon="ep:view" class="mr-5px" />
                {{ t('common.view') }}
              </el-button>
            </template>
            <pre>{{ scope.row.deviceMessage }}</pre>
          </el-popover>
          <span v-else class="text-gray-400">-</span>
        </template>
      </el-table-column>
      <el-table-column :label="t('alert.record.handleStatus')" align="center" prop="processStatus">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="scope.row.processStatus" />
        </template>
      </el-table-column>
      <el-table-column :label="t('alert.record.handleRemark')" align="center" prop="processRemark"  fixed="right" />
      <el-table-column
        :label="t('common.createTime')"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        min-width="180"
      />
      <el-table-column :label="t('common.operation')" align="center" min-width="120px">
        <template #default="scope">
          <el-button
            v-if="!scope.row.processStatus"
            link
            type="primary"
            @click="handleProcess(scope.row)"
            v-hasPermi="['iot:alert-record:process']"
          >
            {{ t('alert.record.handleAlert') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import { AlertRecordApi, AlertRecord } from '@/api/iot/alert/record'
import { AlertConfigApi, AlertConfig } from '@/api/iot/alert/config'
import { ProductApi, ProductVO } from '@/api/iot/product/product'
import { DeviceApi, DeviceVO } from '@/api/iot/device/device'
import { DICT_TYPE, getIntDictOptions, getBoolDictOptions } from '@/utils/dict'

/** IoT 告警记录列表 */
defineOptions({ name: 'IotAlertRecord' })

const message = useMessage() // 消息弹窗
const { t } = useI18n('iot') // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<AlertRecord[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const alertConfigList = ref<AlertConfig[]>([]) // 告警配置列表
const productList = ref<ProductVO[]>([]) // 产品列表
const deviceList = ref<DeviceVO[]>([]) // 设备列表
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  configId: undefined as number | undefined,
  configLevel: undefined as number | undefined,
  productId: undefined as number | undefined,
  deviceId: undefined as number | undefined,
  processStatus: undefined as boolean | undefined,
  createTime: [] as string[]
})
const queryFormRef = ref() // 搜索的表单

/** 根据选择的产品 ID，筛选设备列表 */
const filteredDeviceList = computed(() => {
  if (!queryParams.productId) {
    return deviceList.value
  }
  return deviceList.value.filter((device) => device.productId === queryParams.productId)
})

/** 根据产品 ID 获取产品名称 */
const getProductName = (productId: number) => {
  if (!productId) {
    return `-`
  }
  const product = productList.value.find((p) => p.id === productId)
  return product ? product.name : `...`
}

/** 根据设备 ID 获取设备名称 */
const getDeviceName = (deviceId: number) => {
  if (!deviceId) {
    return `-`
  }
  const device = deviceList.value.find((d) => d.id === deviceId)
  return device ? device.deviceName : `...`
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await AlertRecordApi.getAlertRecordPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 产品变更处理 */
const handleProductChange = () => {
  queryParams.deviceId = undefined // 清空设备选择
}

/** 处理告警记录 */
const handleProcess = async (row: AlertRecord) => {
  try {
    const { value: processRemark } = await ElMessageBox.prompt(t('common.inputPlaceholder'), t('alert.record.handleAlert'), {
      confirmButtonText: t('common.ok'),
      cancelButtonText: t('common.cancel')
    })
    await AlertRecordApi.processAlertRecord(row.id, processRemark)
    message.success(t('common.success'))
    await getList()
  } catch (error) {}
}

/** 初始化 **/
onMounted(async () => {
  await getList()
  alertConfigList.value = await AlertConfigApi.getSimpleAlertConfigList()
  productList.value = await ProductApi.getSimpleProductList()
  deviceList.value = await DeviceApi.getSimpleDeviceList()
})
</script>
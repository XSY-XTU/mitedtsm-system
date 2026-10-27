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
          <el-form-item :label="t('ota.firmware.name')" prop="name">
            <el-input
              v-model="queryParams.name"
              :placeholder="t('ota.firmware.namePlaceholder')"
              clearable
              @keyup.enter="handleQuery"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('ota.firmware.product')" prop="productId">
            <el-select
              v-model="queryParams.productId"
              :placeholder="t('ota.firmware.selectProduct')"
              clearable
              @keyup.enter="handleQuery"
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
        <el-col :span="8">
          <el-form-item :label="t('ota.firmware.createTime')" prop="createTime">
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
            <el-button
              type="primary"
              plain
              @click="openForm('create')"
              v-hasPermi="['iot:ota-firmware:create']"
            >
              <Icon icon="ep:plus" class="mr-5px" /> {{ t('common.add') }}
            </el-button>
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
      <el-table-column :label="t('ota.firmware.id')" align="center" prop="id" />
      <el-table-column :label="t('ota.firmware.name')" align="center" prop="name" />
      <el-table-column :label="t('ota.firmware.firmwareVersion')" align="center" prop="description" />
      <el-table-column :label="t('ota.firmware.version')" align="center" prop="version" />
      <el-table-column :label="t('ota.firmware.productId')" align="center" prop="productId">
        <template #default="scope">
          <el-link
            @click="openProductDetail(scope.row.productId)"
            v-if="getProductName(scope.row.productId)"
          >
            {{ getProductName(scope.row.productId) }}
          </el-link>
          <span v-else>{{ t('ota.firmware.loading') }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="t('ota.firmware.fileUrl')" align="center" prop="fileUrl">
        <template #default="scope">
          <el-link :href="scope.row.fileUrl" target="_blank" download>
            <Icon icon="ep:download" class="mr-5px" />
            {{ t('ota.firmware.downloadFirmware') }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column
        :label="t('ota.firmware.createTime')"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        min-width="180"
       fixed="right" />
      <el-table-column :label="t('common.operation')" align="center" min-width="180px">
        <template #default="scope">
          <el-button
            link
            @click="openFirmwareDetail(scope.row.id)"
            v-hasPermi="['iot:ota-firmware:query']"
          >
            {{ t('common.detail') }}
          </el-button>
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['iot:ota-firmware:update']"
          >
            {{ t('common.edit') }}
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['iot:ota-firmware:delete']"
          >
            {{ t('common.del') }}
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

  <!-- 表单弹窗：添加/修改 -->
  <OtaFirmwareForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import { IoTOtaFirmwareApi, IoTOtaFirmware } from '@/api/iot/ota/firmware'
import { ProductApi, ProductVO } from '@/api/iot/product/product'
import OtaFirmwareForm from './OtaFirmwareForm.vue'

/** IoT OTA 固件列表 */
defineOptions({ name: 'IoTOtaFirmware' })

const message = useMessage() // 消息弹窗
const { t } = useI18n('iot') // 国际化
const { push } = useRouter() // 路由

const loading = ref(true) // 列表的加载中
const list = ref<IoTOtaFirmware[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const productList = ref<ProductVO[]>([]) // 产品列表
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  productId: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await IoTOtaFirmwareApi.getOtaFirmwarePage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 根据产品编号，获取产品名称 */
const getProductName = (productId: number) => {
  const product = productList.value.find((p) => p.id === productId)
  return product?.name || ''
}

/** 打开产品详情 */
const openProductDetail = (productId: number) => {
  push({ name: 'IoTProductDetail', params: { id: productId } })
}

/** 打开固件详情 */
const openFirmwareDetail = (firmwareId: number) => {
  push({ name: 'IoTOtaFirmwareDetail', params: { id: firmwareId } })
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

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await IoTOtaFirmwareApi.deleteOtaFirmware(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 初始化 **/
onMounted(async () => {
  productList.value = await ProductApi.getSimpleProductList()
  getList()
})
</script>

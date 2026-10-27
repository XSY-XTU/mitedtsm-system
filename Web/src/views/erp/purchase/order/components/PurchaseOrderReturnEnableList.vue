<!-- 可退货的订单列表 -->
<template>
  <Dialog
    :title="t('selectOrderReturn')"
    v-model="dialogVisible"
    :appendToBody="true"
    :scroll="true"
    width="1080"
  >
    <ContentWrap>
      <!-- 搜索工作栏 -->
      <el-form
        class="-mb-15px"
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="68px"
      >
        <el-form-item :label="t('no')" prop="no">
          <el-input
            v-model="queryParams.no"
            :placeholder="t('noPlaceholder')"
            clearable
            @keyup.enter="handleQuery"
            class="!w-160px"
          />
        </el-form-item>
        <el-form-item :label="t('product')" prop="productId">
          <el-select
            v-model="queryParams.productId"
            clearable
            filterable
            :placeholder="t('selectProduct')"
            class="!w-160px"
          >
            <el-option
              v-for="item in productList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('orderTime')" prop="orderTime">
          <el-date-picker
            v-model="queryParams.orderTime"
            value-format="YYYY-MM-DD HH:mm:ss"
            type="daterange"
            :start-placeholder="t('startDate')"
            :end-placeholder="t('endDate')"
            :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
            class="!w-160px"
          />
        </el-form-item>
        <el-form-item>
          <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> {{ t('common.search') }}</el-button>
          <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> {{ t('common.reset') }}</el-button>
        </el-form-item>
      </el-form>
    </ContentWrap>

    <ContentWrap>
      <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true" :table-layout="'auto'">
        <el-table-column align="center" min-width="65">
          <template #default="scope">
            <el-radio
              :value="scope.row.id"
              v-model="currentRowValue"
              @change="handleCurrentChange(scope.row)"
            >
              &nbsp;
            </el-radio>
          </template>
        </el-table-column>
        <el-table-column min-width="180" :label="t('no')" align="center" prop="no" />
        <el-table-column :label="t('supplierId')" align="center" prop="supplierName" />
        <el-table-column :label="t('productInfo')" align="center" prop="productNames" min-width="200" />
        <el-table-column
          :label="t('orderTime')"
          align="center"
          prop="orderTime"
          :formatter="dateFormatter2"
          min-width="120"
        />
        <el-table-column :label="t('creator')" align="center" prop="creatorName" />
        <el-table-column
          :label="t('totalCount')"
          align="center"
          prop="totalCount"
          :formatter="erpCountTableColumnFormatter"
        />
        <el-table-column
          :label="t('inCount')"
          align="center"
          prop="inCount"
          :formatter="erpCountTableColumnFormatter"
        />
        <el-table-column
          :label="t('returnCount')"
          align="center"
          prop="returnCount"
          :formatter="erpCountTableColumnFormatter"
        />
        <el-table-column
          :label="t('totalProductPrice')"
          align="center"
          prop="totalProductPrice"
          :formatter="erpPriceTableColumnFormatter"
        />
        <el-table-column
          :label="t('totalPrice')"
          align="center"
          prop="totalPrice"
          :formatter="erpPriceTableColumnFormatter"
        />
      </el-table>
      <!-- 分页 -->
      <Pagination
        v-model:limit="queryParams.pageSize"
        v-model:page="queryParams.pageNo"
        :total="total"
        @pagination="getList"
      />
    </ContentWrap>
    <template #footer>
      <el-button :disabled="!currentRow" type="primary" @click="submitForm">{{ t('common.confirm') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import { ElTable } from 'element-plus'
import { PurchaseOrderApi, PurchaseOrderVO } from '@/api/erp/purchase/order'
import { dateFormatter2 } from '@/utils/formatTime'
import { erpCountTableColumnFormatter, erpPriceTableColumnFormatter } from '@/utils'
import { ProductApi, ProductVO } from '@/api/erp/product/product'

defineOptions({ name: 'PurchaseOrderReturnEnableList' })

const { t } = useI18n('erp.purchase.order') // 国际化

const list = ref<PurchaseOrderVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const loading = ref(false) // 列表的加载中
const dialogVisible = ref(false) // 弹窗的是否展示
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  no: undefined,
  productId: undefined,
  orderTime: [],
  returnEnable: true
})
const queryFormRef = ref() // 搜索的表单
const productList = ref<ProductVO[]>([]) // 产品列表

/** 选中行 */
const currentRowValue = ref(undefined) // 选中行的 value
const currentRow = ref(undefined) // 选中行
const handleCurrentChange = (row) => {
  currentRow.value = row
}

/** 打开弹窗 */
const open = async () => {
  dialogVisible.value = true
  await nextTick() // 等待，避免 queryFormRef 为空
  // 加载可退货的订单列表
  await resetQuery()
  // 加载产品列表
  productList.value = await ProductApi.getProductSimpleList()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交选择 */
const emits = defineEmits<{
  (e: 'success', value: PurchaseOrderVO): void
}>()
const submitForm = () => {
  try {
    emits('success', currentRow.value)
  } finally {
    // 关闭弹窗
    dialogVisible.value = false
  }
}

/** 加载列表  */
const getList = async () => {
  loading.value = true
  try {
    const data = await PurchaseOrderApi.getPurchaseOrderPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  currentRowValue.value = undefined
  currentRow.value = undefined
  getList()
}
</script>
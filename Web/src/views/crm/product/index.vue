<template>
  <doc-alert title="【产品】产品管理、产品分类" url="https://doc.iocoder.cn/crm/product/" />

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
          <el-form-item :label="t('name')" prop="name">
            <el-input
              v-model="queryParams.name"
              :placeholder="t('namePlaceholder')"
              clearable
              @keyup.enter="handleQuery"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('status')" prop="status">
            <el-select v-model="queryParams.status" :placeholder="t('common.selectPlaceholder')" clearable class="!w-240px">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.CRM_PRODUCT_STATUS)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button @click="handleQuery"> <Icon icon="ep:search" class="mr-5px" /> {{ t('common.search') }} </el-button>
            <el-button @click="resetQuery"> <Icon icon="ep:refresh" class="mr-5px" /> {{ t('common.reset') }} </el-button>
            <el-button type="primary" @click="openForm('create')" v-hasPermi="['crm:product:create']">
              <Icon icon="ep:plus" class="mr-5px" /> {{ t('action.add') }}
            </el-button>
            <el-button
              type="success"
              plain
              @click="handleExport"
              :loading="exportLoading"
              v-hasPermi="['crm:product:export']"
            >
              <Icon icon="ep:download" class="mr-5px" />
              {{ t('common.export') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true" :table-layout="'auto'">
      <el-table-column :label="t('name')" align="center" prop="name" min-width="160">
        <template #default="scope">
          <el-link :underline="false" type="primary" @click="openDetail(scope.row.id)">
            {{ scope.row.name }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column :label="t('categoryName')" align="center" prop="categoryName" min-width="160" />
      <el-table-column :label="t('unit')" align="center" prop="unit">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_PRODUCT_UNIT" :value="scope.row.unit" />
        </template>
      </el-table-column>
      <el-table-column :label="t('no')" align="center" prop="no" />
      <el-table-column
        :label="t('price') + '（元）'"
        align="center"
        prop="price"
        :formatter="erpPriceTableColumnFormatter"
        min-width="100"
      />
      <el-table-column :label="t('description')" align="center" prop="description" min-width="150" />
      <el-table-column :label="t('status')" align="center" prop="status" min-width="120">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_PRODUCT_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column :label="t('ownerUserName')" align="center" prop="ownerUserName" min-width="120" />
      <el-table-column
        :label="t('updateTime')"
        align="center"
        prop="updateTime"
        :formatter="dateFormatter"
        min-width="180"
      />
      <el-table-column :label="t('creatorName')" align="center" prop="creatorName" min-width="120" />
      <el-table-column
        :label="t('createTime')"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        min-width="180"
      />
      <el-table-column :label="t('common.action')" align="center" fixed="right" min-width="160">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['crm:product:update']"
          >
            {{ t('common.edit') }}
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['crm:product:delete']"
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
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <ProductForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import * as ProductApi from '@/api/crm/product'
import ProductForm from './ProductForm.vue'
import { erpPriceTableColumnFormatter } from '@/utils'

defineOptions({ name: 'CrmProduct' })

const message = useMessage() // 消息弹窗
const { t } = useI18n('crm.product') // 国际化
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  status: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ProductApi.getProductPage(queryParams)
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

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 打开详情 */
const { currentRoute, push } = useRouter()
const openDetail = (id: number) => {
  push({ name: 'CrmProductDetail', params: { id } })
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await ProductApi.deleteProduct(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await ProductApi.exportProduct(queryParams)
    download.excel(data, t('exportFileName') + '.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 激活时 */
onActivated(() => {
  getList()
})

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

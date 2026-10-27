<!-- 商品中心 - 商品列表  -->
<template>
  <doc-alert title="【商品】商品 SPU 与 SKU" url="https://doc.iocoder.cn/mall/product-spu-sku/" />

  <!-- 搜索工作区 -->
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      class="-mb-15px"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('spu.name')" prop="name">
            <el-input
              v-model="queryParams.name"
              class="!w-240px"
              clearable
              :placeholder="t('spu.namePlaceholder')"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('spu.categoryId')" prop="categoryId">
            <el-cascader
              v-model="queryParams.categoryId"
              :options="categoryList"
              :props="defaultProps"
              class="w-1/1"
              clearable
              filterable
              :placeholder="t('spu.categoryIdPlaceholder')"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('common.createTime')" prop="createTime">
            <el-date-picker
              v-model="queryParams.createTime"
              :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
              class="!w-240px"
              :end-placeholder="t('common.endTime')"
              :start-placeholder="t('common.startTime')"
              type="daterange"
              value-format="YYYY-MM-DD HH:mm:ss"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button @click="handleQuery">
              <Icon class="mr-5px" icon="ep:search" />
              {{ t('common.search') }}
            </el-button>
            <el-button @click="resetQuery">
              <Icon class="mr-5px" icon="ep:refresh" />
              {{ t('common.reset') }}
            </el-button>
            <el-button
              v-hasPermi="['product:spu:create']"
              plain
              type="primary"
              @click="openForm(undefined)"
            >
              <Icon class="mr-5px" icon="ep:plus" />
              {{ t('common.add') }}
            </el-button>
            <el-button
              v-hasPermi="['product:spu:export']"
              :loading="exportLoading"
              plain
              type="success"
              @click="handleExport"
            >
              <Icon class="mr-5px" icon="ep:download" />
              {{ t('common.export') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-tabs v-model="queryParams.tabType" @tab-click="handleTabClick">
      <el-tab-pane
        v-for="item in tabsData"
        :key="item.type"
        :label="item.name + '(' + item.count + ')'"
        :name="item.type"
      />
    </el-tabs>
    <el-table v-loading="loading" :data="list" :table-layout="'auto'">
      <el-table-column type="expand">
        <template #default="{ row }">
          <el-form class="spu-table-expand" label-position="left">
            <el-row>
              <el-col :span="24">
                <el-row>
                  <el-col :span="8">
                    <el-form-item :label="t('spu.categoryId') + ':'">
                      <span>{{ formatCategoryName(row.categoryId) }}</span>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item :label="t('spu.marketPrice') + ':'">
                      <span>{{ fenToYuan(row.marketPrice) }}</span>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item :label="t('spu.costPrice') + ':'">
                      <span>{{ fenToYuan(row.costPrice) }}</span>
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <el-row>
                  <el-col :span="8">
                    <el-form-item :label="t('spu.browseCount') + ':'">
                      <span>{{ row.browseCount }}</span>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item :label="t('spu.virtualSalesCount') + ':'">
                      <span>{{ row.virtualSalesCount }}</span>
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column :label="t('spu.id')" min-width="140" prop="id" />
      <el-table-column :label="t('spu.productInfo')" min-width="300">
        <template #default="{ row }">
          <div class="flex">
            <el-image
              fit="cover"
              :src="row.picUrl"
              class="flex-none w-50px h-50px"
              @click="imagePreview(row.picUrl)"
            />
            <div class="ml-4 overflow-hidden">
              <el-tooltip effect="dark" :content="row.name" placement="top">
                <div>
                  {{ row.name }}
                </div>
              </el-tooltip>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('spu.price')" min-width="160" prop="price">
        <template #default="{ row }"> ¥ {{ fenToYuan(row.price) }}</template>
      </el-table-column>
      <el-table-column align="center" :label="t('spu.salesCount')" min-width="90" prop="salesCount" />
      <el-table-column align="center" :label="t('spu.stock')" min-width="90" prop="stock" />
      <el-table-column align="center" :label="t('spu.sort')" min-width="70" prop="sort" />
      <el-table-column align="center" :label="t('spu.salesStatus')" min-width="80">
        <template #default="{ row }">
          <template v-if="row.status >= 0">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              :active-text="t('spu.statusEnable')"
              :inactive-text="t('spu.statusDisable')"
              inline-prompt
              @change="handleStatusChange(row)"
            />
          </template>
          <template v-else>
            <el-tag type="info">{{ t('spu.statusRecycle') }}</el-tag>
          </template>
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('common.createTime')"
        prop="createTime"
        min-width="180"
      />
      <el-table-column align="center" fixed="right" :label="t('common.operation')" min-width="200">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row.id)"> {{ t('common.detail') }} </el-button>
          <el-button
            v-hasPermi="['product:spu:update']"
            link
            type="primary"
            @click="openForm(row.id)"
          >
            {{ t('common.edit') }}
          </el-button>
          <template v-if="queryParams.tabType === 4">
            <el-button
              v-hasPermi="['product:spu:delete']"
              link
              type="danger"
              @click="handleDelete(row.id)"
            >
              {{ t('common.delete') }}
            </el-button>
            <el-button
              v-hasPermi="['product:spu:update']"
              link
              type="primary"
              @click="handleStatus02Change(row, ProductSpuStatusEnum.DISABLE.status)"
            >
              {{ t('common.restore') }}
            </el-button>
          </template>
          <template v-else>
            <el-button
              v-hasPermi="['product:spu:update']"
              link
              type="danger"
              @click="handleStatus02Change(row, ProductSpuStatusEnum.RECYCLE.status)"
            >
              {{ t('common.recycle') }}
            </el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>
</template>
<script lang="ts" setup>
import { TabsPaneContext } from 'element-plus'
import { createImageViewer } from '@/components/ImageViewer'
import { dateFormatter } from '@/utils/formatTime'
import { defaultProps, handleTree, treeToString } from '@/utils/tree'
import { ProductSpuStatusEnum } from '@/utils/constants'
import { fenToYuan } from '@/utils'
import download from '@/utils/download'
import * as ProductSpuApi from '@/api/mall/product/spu'
import * as ProductCategoryApi from '@/api/mall/product/category'

defineOptions({ name: 'ProductSpu' })

const message = useMessage() // 消息弹窗
const route = useRoute() // 路由
const { t } = useI18n('mall.product') // 国际化
const { push } = useRouter() // 路由跳转

const loading = ref(false) // 列表的加载中
const exportLoading = ref(false) // 导出的加载中
const total = ref(0) // 列表的总页数
const list = ref<ProductSpuApi.Spu[]>([]) // 列表的数据
// tabs 数据
const tabsData = ref([
  {
    name: t('spu.tabOnSale'),
    type: 0,
    count: 0
  },
  {
    name: t('spu.tabInWarehouse'),
    type: 1,
    count: 0
  },
  {
    name: t('spu.tabSoldOut'),
    type: 2,
    count: 0
  },
  {
    name: t('spu.tabStockWarning'),
    type: 3,
    count: 0
  },
  {
    name: t('spu.tabRecycleBin'),
    type: 4,
    count: 0
  }
])

const queryParams = ref({
  pageNo: 1,
  pageSize: 10,
  tabType: 0,
  name: '',
  categoryId: undefined,
  createTime: undefined
}) // 查询参数
const queryFormRef = ref() // 搜索的表单Ref

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ProductSpuApi.getSpuPage(queryParams.value)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 切换 Tab */
const handleTabClick = (tab: TabsPaneContext) => {
  queryParams.value.tabType = tab.paneName as number
  getList()
}

/** 获得每个 Tab 的数量 */
const getTabsCount = async () => {
  const res = await ProductSpuApi.getTabsCount()
  for (let objName in res) {
    tabsData.value[Number(objName)].count = res[objName]
  }
}

/** 添加到仓 / 回收站的状态 */
const handleStatus02Change = async (row: any, newStatus: number) => {
  try {
    // 二次确认
    const text = newStatus === ProductSpuStatusEnum.RECYCLE.status ? t('spu.addToRecycleBin') : t('spu.restoreToWarehouse')
    await message.confirm(t('common.confirmText', { text: `"${row.name}"${text}` }))
    // 发起修改
    await ProductSpuApi.updateStatus({ id: row.id, status: newStatus })
    message.success(t('common.operateSuccess', { text }))
    // 刷新 tabs 数据
    await getTabsCount()
    // 刷新列表
    await getList()
  } catch {}
}

/** 更新上架/下架状态 */
const handleStatusChange = async (row: any) => {
  try {
    // 二次确认
    const text = row.status ? t('spu.statusEnable') : t('spu.statusDisable')
    await message.confirm(t('common.confirmText', { text: `${text}"${row.name}"` }))
    // 发起修改
    await ProductSpuApi.updateStatus({ id: row.id, status: row.status })
    message.success(t('common.operateSuccess', { text }))
    // 刷新 tabs 数据
    await getTabsCount()
    // 刷新列表
    await getList()
  } catch {
    // 异常时，需要重置回之前的
    row.status =
      row.status === ProductSpuStatusEnum.DISABLE.status
        ? ProductSpuStatusEnum.ENABLE.status
        : ProductSpuStatusEnum.DISABLE.status
  }
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await ProductSpuApi.deleteSpu(id)
    message.success(t('common.delSuccess'))
    // 刷新tabs数据
    await getTabsCount()
    // 刷新列表
    await getList()
  } catch {}
}

/** 商品图预览 */
const imagePreview = (imgUrl: string) => {
  createImageViewer({
    urlList: [imgUrl]
  })
}

/** 搜索按钮操作 */
const handleQuery = () => {
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 新增或修改 */
const openForm = (id?: number) => {
  // 修改
  if (typeof id === 'number') {
    push({ name: 'ProductSpuEdit', params: { id } })
    return
  }
  // 新增
  push({ name: 'ProductSpuAdd' })
}

/** 查看商品详情 */
const openDetail = (id: number) => {
  push({ name: 'ProductSpuDetail', params: { id } })
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await ProductSpuApi.exportSpu(queryParams.value)
    download.excel(data, t('spu.exportFile') + '.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 获取分类的节点的完整结构 */
const categoryList = ref() // 分类
const formatCategoryName = (categoryId: number) => {
  return treeToString(categoryList.value, categoryId)
}

/** 激活时 */
onActivated(() => {
  getList()
})

/** 初始化 **/
onMounted(async () => {
  // 解析路由categoryId
  if (route.query.categoryId) {
    queryParams.value.categoryId = route.query.categoryId
  }
  // 获得商品信息
  await getTabsCount()
  await getList()
  // 获得分类
  const data = await ProductCategoryApi.getCategoryList({})
  categoryList.value = handleTree(data, 'id', 'parentId')
})
</script>
<style lang="scss" scoped>
.spu-table-expand {
  padding-left: 42px;

  :deep(.el-form-item__label) {
    width: 82px;
    font-weight: bold;
    color: #99a9bf;
  }
}
</style>

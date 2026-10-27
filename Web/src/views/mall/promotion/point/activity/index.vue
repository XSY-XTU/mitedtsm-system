<template>
  <doc-alert title="【营销】积分商城活动" url="https://doc.iocoder.cn/mall/promotion-point/" />

  <ContentWrap>
    <!-- 搜索工作区 -->
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      class="-mb-15px"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('status')" prop="status">
            <el-select
              v-model="queryParams.status"
              class="!w-240px"
              clearable
              :placeholder="t('statusPlaceholder')"
            >
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
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
            <el-button @click="handleQuery">
              <Icon class="mr-5px" icon="ep:search" />
              {{ $t('common.search') }}
            </el-button>
            <el-button @click="resetQuery">
              <Icon class="mr-5px" icon="ep:refresh" />
              {{ $t('common.reset') }}
            </el-button>
            <el-button
              v-hasPermi="['promotion:point-activity:create']"
              plain
              type="primary"
              @click="openForm('create')"
            >
              <Icon class="mr-5px" icon="ep:plus" />
              {{ $t('action.create') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true" :table-layout="'auto'">
      <el-table-column :label="t('activityId')" min-width="80" prop="id" />
      <el-table-column :label="t('picUrl')" min-width="80" prop="spuName">
        <template #default="scope">
          <el-image
            :preview-src-list="[scope.row.picUrl]"
            :src="scope.row.picUrl"
            class="h-40px w-40px"
            preview-teleported
          />
        </template>
      </el-table-column>
      <el-table-column :label="t('spuName')" min-width="300" prop="spuName" />
      <el-table-column
        :formatter="fenToYuanFormat"
        :label="t('marketPrice')"
        min-width="100"
        prop="marketPrice"
      />
      <el-table-column align="center" :label="t('status')" min-width="100" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('stock')" min-width="80" prop="stock" />
      <el-table-column align="center" :label="t('totalStock')" min-width="80" prop="totalStock" />
      <el-table-column align="center" :label="t('redeemedQuantity')" min-width="100" prop="redeemedQuantity">
        <template #default="{ row }">
          {{ getRedeemedQuantity(row) }}
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('createTime')"
        prop="createTime"
        min-width="180"
      />
      <el-table-column align="center" fixed="right" :label="$t('common.operation')" min-width="150">
        <template #default="scope">
          <el-button
            v-hasPermi="['promotion:point-activity:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            {{ $t('action.edit') }}
          </el-button>
          <el-button
            v-if="scope.row.status === 0"
            v-hasPermi="['promotion:point-activity:close']"
            link
            type="danger"
            @click="handleClose(scope.row.id)"
          >
            {{ t('close') }}
          </el-button>
          <el-button
            v-else
            v-hasPermi="['promotion:point-activity:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            {{ $t('action.delete') }}
          </el-button>
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

  <!-- 表单弹窗：添加/修改 -->
  <PointActivityForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import PointActivityForm from './PointActivityForm.vue'
import { fenToYuanFormat } from '@/utils/formatter'
import { PointActivityApi } from '@/api/mall/promotion/point'

defineOptions({ name: 'PointActivity' })

const message = useMessage() // 消息弹窗
const { t } = useI18n('mall.promotion.point') // 国际化
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: null,
  status: null
})
const queryFormRef = ref() // 搜索的表单
const getRedeemedQuantity = computed(() => (row: any) => (row.totalStock || 0) - (row.stock || 0)) // 获得商品已兑换数量
/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await PointActivityApi.getPointActivityPage(queryParams)
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

/** 关闭按钮操作 */
const handleClose = async (id: number) => {
  try {
    // 关闭的二次确认
    await message.confirm(t('closeConfirm'))
    // 发起关闭
    await PointActivityApi.closePointActivity(id)
    message.success(t('closeSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await PointActivityApi.deletePointActivity(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 初始化 **/
onMounted(async () => {
  await getList()
})
</script>

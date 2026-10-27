<template>
  <doc-alert title="【营销】拼团活动" url="https://doc.iocoder.cn/mall/promotion-combination/" />

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
          <el-form-item :label="t('combination.name')" prop="name">
            <el-input
              v-model="queryParams.name"
              class="!w-240px"
              clearable
              :placeholder="t('combination.namePlaceholder')"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('combination.status')" prop="status">
            <el-select
              v-model="queryParams.status"
              class="!w-240px"
              clearable
              :placeholder="t('combination.statusPlaceholder')"
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
              {{ t('common.query') }}
            </el-button>
            <el-button @click="resetQuery">
              <Icon class="mr-5px" icon="ep:refresh" />
              {{ t('common.reset') }}
            </el-button>
            <el-button
              v-hasPermi="['promotion:combination-activity:create']"
              plain
              type="primary"
              @click="openForm('create')"
            >
              <Icon class="mr-5px" icon="ep:plus" />
              {{ t('common.add') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true" :table-layout="'auto'">
      <el-table-column :label="t('combination.activityId')" min-width="80" prop="id" />
      <el-table-column :label="t('combination.name')" min-width="140" prop="name" />
      <el-table-column :label="t('combination.activeTime')" min-width="210">
        <template #default="scope">
          {{ formatDate(scope.row.startTime, 'YYYY-MM-DD') }}
          ~ {{ formatDate(scope.row.endTime, 'YYYY-MM-DD') }}
        </template>
      </el-table-column>
      <el-table-column :label="t('combination.picUrl')" min-width="80" prop="spuName">
        <template #default="scope">
          <el-image
            :preview-src-list="[scope.row.picUrl]"
            :src="scope.row.picUrl"
            class="h-40px w-40px"
            preview-teleported
          />
        </template>
      </el-table-column>
      <el-table-column :label="t('combination.spuName')" min-width="300" prop="spuName" />
      <el-table-column
        :formatter="fenToYuanFormat"
        :label="t('combination.marketPrice')"
        min-width="100"
        prop="marketPrice"
      />
      <el-table-column :label="t('combination.combinationPrice')" min-width="100" prop="seckillPrice">
        <template #default="scope">
          {{ formatCombinationPrice(scope.row.products) }}
        </template>
      </el-table-column>
      <el-table-column :label="t('combination.groupCount')" min-width="100" prop="groupCount" />
      <el-table-column :label="t('combination.groupSuccessCount')" min-width="100" prop="groupSuccessCount" />
      <el-table-column :label="t('combination.recordCount')" min-width="100" prop="recordCount" />
      <el-table-column align="center" :label="t('combination.status')" min-width="100" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('common.createTime')"
        prop="createTime"
        min-width="180"
      />
      <el-table-column align="center" fixed="right" :label="t('common.action')" min-width="150">
        <template #default="scope">
          <el-button
            v-hasPermi="['promotion:combination-activity:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            {{ t('common.edit') }}
          </el-button>
          <el-button
            v-if="scope.row.status === 0"
            v-hasPermi="['promotion:combination-activity:close']"
            link
            type="danger"
            @click="handleClose(scope.row.id)"
          >
            {{ t('combination.close') }}
          </el-button>
          <el-button
            v-else
            v-hasPermi="['promotion:combination-activity:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            {{ t('common.del') }}
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
  <CombinationActivityForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import * as CombinationActivityApi from '@/api/mall/promotion/combination/combinationActivity'
import CombinationActivityForm from './CombinationActivityForm.vue'
import { fenToYuanFormat } from '@/utils/formatter'
import { fenToYuan } from '@/utils'

defineOptions({ name: 'PromotionBargainActivity' })

const message = useMessage() // 消息弹窗
const { t } = useI18n('mall.promotion.combination') // 国际化
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
/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await CombinationActivityApi.getCombinationActivityPage(queryParams)
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
    await message.confirm(t('combination.closeConfirm'))
    // 发起关闭
    await CombinationActivityApi.closeCombinationActivity(id)
    message.success(t('combination.closeSuccess'))
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
    await CombinationActivityApi.deleteCombinationActivity(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

const formatCombinationPrice = (products) => {
  const combinationPrice = Math.min(...products.map((item) => item.combinationPrice))
  return `￥${fenToYuan(combinationPrice)}`
}

/** 初始化 **/
onMounted(async () => {
  await getList()
})
</script>

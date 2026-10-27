<template>
  <Dialog v-model="dialogVisible" :appendToBody="true" :title="t('selectActivity')" width="70%">
    <ContentWrap>
      <!-- 搜索工作�?-->
      <el-form
        ref="queryFormRef"
        :inline="true"
        :model="queryParams"
        class="-mb-15px"
        label-width="68px"
      >
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
        <el-form-item>
          <el-button @click="handleQuery">
            <Icon class="mr-5px" icon="ep:search" />
            {{ $t('common.search') }}
          </el-button>
          <el-button @click="resetQuery">
            <Icon class="mr-5px" icon="ep:refresh" />
            {{ $t('common.reset') }}
          </el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="list" show-overflow-tooltip :table-layout="'auto'">
        <!-- 1. 多选模式（不能使用type="selection"，Element会忽略Header插槽�?-->
        <el-table-column v-if="multiple" min-width="55">
          <template #header>
            <el-checkbox
              v-model="isCheckAll"
              :indeterminate="isIndeterminate"
              @change="handleCheckAll"
            />
          </template>
          <template #default="{ row }">
            <el-checkbox
              v-model="checkedStatus[row.id]"
              @change="(checked: boolean) => handleCheckOne(checked, row, true)"
            />
          </template>
        </el-table-column>
        <!-- 2. 单选模�?-->
        <el-table-column v-else label="#" min-width="55">
          <template #default="{ row }">
            <el-radio
              v-model="selectedActivityId"
              :value="row.id"
              @change="handleSingleSelected(row)"
            >
              <!-- 空格不能省略，是为了让单选框不显示label，如果不指定label不会有选中的效�?-->
              &nbsp;
            </el-radio>
          </template>
        </el-table-column>
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
      </el-table>
      <!-- 分页 -->
      <Pagination
        v-model:limit="queryParams.pageSize"
        v-model:page="queryParams.pageNo"
        :total="total"
        @pagination="getList"
      />
    </ContentWrap>
    <template v-if="multiple" #footer>
      <el-button type="primary" @click="handleEmitChange">{{ $t('common.confirm') }}</el-button>
      <el-button @click="dialogVisible = false">{{ $t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import { propTypes } from '@/utils/propTypes'
import { CHANGE_EVENT } from 'element-plus'
import { PointActivityApi, PointActivityVO } from '@/api/mall/promotion/point'
import { fenToYuanFormat } from '@/utils/formatter'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'

/**
 * 活动表格选择对话�? * 1. 单选模式：
 *    1.1 点击表格左侧的单选框时，结束选择，并关闭对话�? *    1.2 再次打开时，保持选中状�? * 2. 多选模式：
 *    2.1 点击表格左侧的多选框时，记录选中的活�? *    2.2 切换分页时，保持活动的选中状�? *    2.3 点击右下角的确定按钮时，结束选择，关闭对话框
 *    2.4 再次打开时，保持选中状�? */
defineOptions({ name: 'PointTableSelect' })

const { t } = useI18n('mall.promotion.point') // 国际�?
defineProps({
  // 多选模�?  multiple: propTypes.bool.def(false)
})

// 列表的总页�?const total = ref(0)
// 列表的数�?const list = ref<PointActivityVO[]>([])
// 列表的加载中
const loading = ref(false)
// 弹窗的是否展�?const dialogVisible = ref(false)
// 查询参数
const queryParams = ref({
  pageNo: 1,
  pageSize: 10,
  name: null,
  status: undefined
})
const getRedeemedQuantity = computed(() => (row: any) => (row.totalStock || 0) - (row.stock || 0)) // 获得商品已兑换数�?/** 打开弹窗 */
const open = (pointList?: PointActivityVO[]) => {
  // 重置
  checkedActivities.value = []
  checkedStatus.value = {}
  isCheckAll.value = false
  isIndeterminate.value = false

  // 处理已选中
  if (pointList && pointList.length > 0) {
    checkedActivities.value = [...pointList]
    checkedStatus.value = Object.fromEntries(pointList.map((activityVO) => [activityVO.id, true]))
  }

  dialogVisible.value = true
  resetQuery()
}
// 提供 open 方法，用于打开弹窗
defineExpose({ open })

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await PointActivityApi.getPointActivityPage(queryParams.value)
    list.value = data.list
    total.value = data.total
    // checkbox绑定undefined会有问题，需要给一个bool值
    list.value.forEach(
      (activityVO) =>
        (checkedStatus.value[activityVO.id] = checkedStatus.value[activityVO.id] || false)
    )
    // 计算全选框状态
    calculateIsCheckAll()
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryParams.value = {
    pageNo: 1,
    pageSize: 10,
    name: null,
    status: undefined
  }
  getList()
}

// 是否全�?const isCheckAll = ref(false)
// 全选框是否处于中间状态：不是全部选中 && 任意一个选中
const isIndeterminate = ref(false)
// 选中的活�?const checkedActivities = ref<PointActivityVO[]>([])
// 选中状态：key为活动ID，value为是否选中
const checkedStatus = ref<Record<string, boolean>>({})

// 选中的活�?activityId
const selectedActivityId = ref()
/** 单选中时触�?*/
const handleSingleSelected = (pointActivityVO: PointActivityVO) => {
  emits(CHANGE_EVENT, pointActivityVO)
  // 关闭弹窗
  dialogVisible.value = false
  // 记住上次选择的ID
  selectedActivityId.value = pointActivityVO.id
}

/** 多选完�?*/
const handleEmitChange = () => {
  // 关闭弹窗
  dialogVisible.value = false
  emits(CHANGE_EVENT, [...checkedActivities.value])
}

/** 确认选择时的触发事件 */
const emits = defineEmits<{
  (e: CHANGE_EVENT, v: PointActivityVO | PointActivityVO[] | any): void
}>()

/** 全�?全不�?*/
const handleCheckAll = (checked: boolean) => {
  isCheckAll.value = checked
  isIndeterminate.value = false

  list.value.forEach((pointActivity) => handleCheckOne(checked, pointActivity, false))
}

/**
 * 选中一�? * @param checked 是否选中
 * @param pointActivity 活动
 * @param isCalcCheckAll 是否计算全�? */
const handleCheckOne = (
  checked: boolean,
  pointActivity: PointActivityVO,
  isCalcCheckAll: boolean
) => {
  if (checked) {
    checkedActivities.value.push(pointActivity)
    checkedStatus.value[pointActivity.id] = true
  } else {
    const index = findCheckedIndex(pointActivity)
    if (index > -1) {
      checkedActivities.value.splice(index, 1)
      checkedStatus.value[pointActivity.id] = false
      isCheckAll.value = false
    }
  }

  // 计算全选框状态
  if (isCalcCheckAll) {
    calculateIsCheckAll()
  }
}

// 查找活动在已选中活动列表中的索引
const findCheckedIndex = (activityVO: PointActivityVO) =>
  checkedActivities.value.findIndex((item) => item.id === activityVO.id)

// 计算全选框状态
const calculateIsCheckAll = () => {
  isCheckAll.value = list.value.every((activityVO) => checkedStatus.value[activityVO.id])
  // 计算中间状态：不是全部选中 && 任意一个选中
  isIndeterminate.value =
    !isCheckAll.value && list.value.some((activityVO) => checkedStatus.value[activityVO.id])
}
</script>

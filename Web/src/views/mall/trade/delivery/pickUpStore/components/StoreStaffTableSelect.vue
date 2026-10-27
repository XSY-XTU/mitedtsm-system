<!-- TODO 芋艿：这块后续抽个独立的组件出来 -->
<template>
  <Dialog :title="t('delivery.selectStaffTitle')" v-model="dialogVisible" width="60%">
    <el-row :gutter="20">
      <!-- 左侧部门�?-->
      <el-col :span="4" :xs="24">
        <ContentWrap class="h-1/1">
          <DeptTree @node-click="handleDeptNodeClick" />
        </ContentWrap>
      </el-col>
      <el-col :span="20" :xs="24">
        <!-- 搜索 -->
        <ContentWrap>
          <el-form
            class="-mb-15px"
            :model="queryParams"
            ref="queryFormRef"
            :inline="true"
            label-width="68px"
          >
            <el-form-item :label="t('delivery.username')" prop="username">
              <el-input
                v-model="queryParams.username"
                :placeholder="t('delivery.usernamePlaceholder')"
                clearable
                @keyup.enter="handleQuery"
                class="!w-240px"
              />
            </el-form-item>
            <el-form-item :label="t('delivery.mobile')" prop="mobile">
              <el-input
                v-model="queryParams.mobile"
                :placeholder="t('delivery.mobilePlaceholder')"
                clearable
                @keyup.enter="handleQuery"
                class="!w-240px"
              />
            </el-form-item>
            <el-form-item :label="t('common.status')" prop="status">
              <el-select
                v-model="queryParams.status"
                :placeholder="t('common.selectText')"
                clearable
                class="!w-240px"
              >
                <el-option
                  v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item :label="t('common.createTime')" prop="createTime">
              <el-date-picker
                v-model="queryParams.createTime"
                value-format="YYYY-MM-DD HH:mm:ss"
                type="datetimerange"
                :start-placeholder="t('common.startTime')"
                :end-placeholder="t('common.endTime')"
                class="!w-240px"
              />
            </el-form-item>
            <el-form-item>
              <el-button @click="handleQuery"><Icon icon="ep:search" />{{ t('common.search') }}</el-button>
              <el-button @click="resetQuery"><Icon icon="ep:refresh" />{{ t('common.reset') }}</el-button>
            </el-form-item>
          </el-form>
        </ContentWrap>
        <ContentWrap>
          <el-table v-loading="loading" :data="list" :table-layout="'auto'">
            <el-table-column min-width="55">
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
            <el-table-column :label="t('common.id')" align="center" key="id" prop="id" />
            <el-table-column
              :label="t('delivery.username')"
              align="center"
              prop="username"
              :show-overflow-tooltip="true"
            />
            <el-table-column
              :label="t('delivery.userNickname')"
              align="center"
              prop="nickname"
              :show-overflow-tooltip="true"
            />
            <el-table-column
              :label="t('delivery.dept')"
              align="center"
              key="deptName"
              prop="deptName"
              :show-overflow-tooltip="true"
            />
            <el-table-column :label="t('delivery.mobile')" align="center" prop="mobile" min-width="120" />
            <el-table-column :label="t('common.status')" key="status">
              <template #default="scope">
                <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
              </template>
            </el-table-column>
            <el-table-column
              :label="t('common.createTime')"
              align="center"
              prop="createTime"
              :formatter="dateFormatter"
              min-width="180"
            />
          </el-table>
          <Pagination
            :total="total"
            v-model:page="queryParams.pageNo"
            v-model:limit="queryParams.pageSize"
            @pagination="getList"
          />
        </ContentWrap>
      </el-col>
    </el-row>
    <template #footer>
      <el-button type="primary" @click="handleEmitChange">{{ t('common.ok') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as UserApi from '@/api/system/user'
import DeptTree from '@/views/system/user/DeptTree.vue'

const { t } = useI18n('mall.trade') // 国际�?
// 是否全�?const isCheckAll = ref(false)
// 全选框是否处于中间状态：不是全部选中 && 任意一个选中
const isIndeterminate = ref(false)
// 选中的活�?const checkedUsers = ref([])
// 选中状态：key为用户ID，value为是否选中
const checkedStatus = ref<Record<string, boolean>>({})

const dialogVisible = ref(false)
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页�?const list = ref([]) // 列表的数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  username: undefined,
  mobile: undefined,
  status: undefined,
  deptId: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表�?
/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await UserApi.getUserPage(queryParams)
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
  queryFormRef.value?.resetFields()
  handleQuery()
}

/** 处理部门被点�?*/
const handleDeptNodeClick = async (row) => {
  queryParams.deptId = row.id
  await getList()
}

/** 打开弹窗 */
const open = async () => {
  dialogVisible.value = true
  loading.value = true
  try {
    await getList()
  } finally {
    loading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 全�?全不�?*/
const handleCheckAll = (checked: boolean) => {
  isCheckAll.value = checked
  isIndeterminate.value = false

  list.value.forEach((combinationActivity) => handleCheckOne(checked, combinationActivity, false))
}

/**
 * 选中一�? * @param checked 是否选中
 * @param combinationActivity 活动
 * @param isCalcCheckAll 是否计算全�? */
const handleCheckOne = (checked: boolean, combinationActivity, isCalcCheckAll: boolean) => {
  if (checked) {
    checkedUsers.value.push(combinationActivity as never)
    checkedStatus.value[combinationActivity.id] = true
  } else {
    const index = findCheckedIndex(combinationActivity)
    if (index > -1) {
      checkedUsers.value.splice(index, 1)
      checkedStatus.value[combinationActivity.id] = false
      isCheckAll.value = false
    }
  }

  // 计算全选框状态
  if (isCalcCheckAll) {
    calculateIsCheckAll()
  }
}

// 查找活动在已选中活动列表中的索引
const findCheckedIndex = (user) => checkedUsers.value.findIndex((item) => item.id === user.id)

// 计算全选框状态
const calculateIsCheckAll = () => {
  isCheckAll.value = list.value.every((user) => checkedStatus.value[user.id])
  // 计算中间状态：不是全部选中 && 任意一个选中
  isIndeterminate.value =
    !isCheckAll.value && list.value.some((user) => checkedStatus.value[user.id])
}

/** 多选完成 */
const handleEmitChange = () => {
  // 关闭弹窗
  dialogVisible.value = false
  emits('change', [...checkedUsers.value])
}

/** 确认选择时的触发事件 */
const emits = defineEmits<{
  change: [CombinationActivityApi: any]
}>()
</script>

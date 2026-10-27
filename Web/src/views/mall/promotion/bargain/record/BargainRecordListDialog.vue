<template>
  <Dialog v-model="dialogVisible" :title="t('mall.promotion.bargain.recordTitle')">
    <!-- 列表 -->
    <ContentWrap>
      <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true" :table-layout="'auto'">
        <el-table-column :label="t('mall.promotion.bargain.userId')" prop="userId" min-width="80px" />
        <el-table-column :label="t('mall.promotion.bargain.avatar')" prop="avatar" min-width="80px">
          <template #default="scope">
            <el-avatar :src="scope.row.avatar" />
          </template>
        </el-table-column>
        <el-table-column :label="t('mall.promotion.bargain.nickname')" prop="nickname" min-width="100px" />
        <el-table-column
          :label="t('mall.promotion.bargain.reducePrice')"
          prop="reducePrice"
          min-width="100px"
          :formatter="fenToYuanFormat"
        />
        <el-table-column
          :label="t('mall.promotion.bargain.helpTime')"
          align="center"
          prop="createTime"
          :formatter="dateFormatter"
          min-width="180"
        />
      </el-table>
      <!-- 分页 -->
      <Pagination
        :total="total"
        v-model:page="queryParams.pageNo"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </ContentWrap>
  </Dialog>
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import * as BargainHelpApi from '@/api/mall/promotion/bargain/bargainHelp'
import { fenToYuanFormat } from '@/utils/formatter'

/** 助力列表 */
defineOptions({ name: 'BargainRecordListDialog' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  recordId: undefined
})
const queryFormRef = ref() // 搜索的表单
/** 打开弹窗 */
const dialogVisible = ref(false) // 弹窗的是否展示
const open = async (recordId: any) => {
  dialogVisible.value = true
  queryParams.recordId = recordId
  resetQuery()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await BargainHelpApi.getBargainHelpPage(queryParams)
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
</script>

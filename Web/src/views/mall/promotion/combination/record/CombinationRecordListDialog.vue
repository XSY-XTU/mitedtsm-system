<template>
  <Dialog v-model="dialogVisible" :title="t('combination.recordList')" width="950">
    <!-- 列表 -->
    <ContentWrap>
      <el-table v-loading="loading" :data="list" :table-layout="'auto'">
        <el-table-column align="center" :label="t('combination.id')" prop="id" min-width="50" />
        <el-table-column align="center" :label="t('combination.avatar')" prop="avatar" min-width="80">
          <template #default="scope">
            <el-avatar :src="scope.row.avatar" />
          </template>
        </el-table-column>
        <el-table-column align="center" :label="t('combination.nickname')" prop="nickname" min-width="100" />
        <el-table-column align="center" :label="t('combination.headId')" prop="headId" min-width="100">
          <template #default="{ row }: { row: CombinationRecordApi.CombinationRecordVO }">
            <el-tag> {{ row.headId === 0 ? t('combination.headLeader') : t('combination.headMember') }} </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          :formatter="dateFormatter"
          align="center"
          :label="t('combination.createTimeLabel')"
          prop="createTime"
          min-width="180"
        />
        <el-table-column
          :formatter="dateFormatter"
          align="center"
          :label="t('combination.endTimeLabel')"
          prop="endTime"
          min-width="180"
        />
        <el-table-column align="center" :label="t('combination.statusLabel')" prop="status" min-width="150">
          <template #default="scope">
            <dict-tag
              :type="DICT_TYPE.PROMOTION_COMBINATION_RECORD_STATUS"
              :value="scope.row.status"
            />
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
  </Dialog>
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as CombinationRecordApi from '@/api/mall/promotion/combination/combinationRecord'
import { DICT_TYPE } from '@/utils/dict'

/** 助力列表 */
defineOptions({ name: 'CombinationRecordListDialog' })

const { t } = useI18n('mall.promotion.combination') // 国际化
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  headId: undefined
})

/** 打开弹窗 */
const dialogVisible = ref(false) // 弹窗的是否展示
const open = async (headId: any) => {
  dialogVisible.value = true
  queryParams.headId = headId
  await getList()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await CombinationRecordApi.getCombinationRecordPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}
</script>

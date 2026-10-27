<template>
  <doc-alert title="【营销】拼团活动" url="https://doc.iocoder.cn/mall/promotion-combination/" />

  <!-- 统计信息展示 -->
  <el-row :gutter="12">
    <el-col :span="6">
      <ContentWrap class="h-[110px] pb-0!">
        <div class="flex items-center">
          <div
            class="h-[50px] w-[50px] flex items-center justify-center"
            style="color: rgb(24 144 255); background-color: rgb(24 144 255 / 10%)"
          >
            <Icon :size="23" icon="fa:user-times" />
          </div>
          <div class="ml-[20px]">
            <div class="mb-8px text-14px text-gray-400">{{ t('combination.userCountUnit') }}</div>
            <CountTo
              :duration="2600"
              :end-val="recordSummary.userCount"
              :start-val="0"
              class="text-20px"
            />
          </div>
        </div>
      </ContentWrap>
    </el-col>
    <el-col :span="6">
      <ContentWrap class="h-[110px]">
        <div class="flex items-center">
          <div
            class="h-[50px] w-[50px] flex items-center justify-center"
            style="color: rgb(162 119 255); background-color: rgb(162 119 255 / 10%)"
          >
            <Icon :size="23" icon="fa:user-plus" />
          </div>
          <div class="ml-[20px]">
            <div class="mb-8px text-14px text-gray-400">{{ t('combination.successCountUnit') }}</div>
            <CountTo
              :duration="2600"
              :end-val="recordSummary.successCount"
              :start-val="0"
              class="text-20px"
            />
          </div>
        </div>
      </ContentWrap>
    </el-col>
    <el-col :span="6">
      <ContentWrap class="h-[110px]">
        <div class="flex items-center">
          <div
            class="h-[50px] w-[50px] flex items-center justify-center"
            style="color: rgb(162 119 255); background-color: rgb(162 119 255 / 10%)"
          >
            <Icon :size="23" icon="fa:user-plus" />
          </div>
          <div class="ml-[20px]">
            <div class="mb-8px text-14px text-gray-400">{{ t('combination.virtualGroupCountUnit') }}</div>
            <CountTo
              :duration="2600"
              :end-val="recordSummary.virtualGroupCount"
              :start-val="0"
              class="text-20px"
            />
          </div>
        </div>
      </ContentWrap>
    </el-col>
  </el-row>

  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      class="-mb-15px"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('common.createTime')" prop="createTime">
            <el-date-picker
              v-model="queryParams.createTime"
              :shortcuts="defaultShortcuts"
              class="!w-240px"
              :end-placeholder="t('common.endTime')"
              :start-placeholder="t('common.startTime')"
              type="daterange"
              value-format="YYYY-MM-DD HH:mm:ss"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('combination.statusLabel')" prop="status">
            <el-select v-model="queryParams.status" class="!w-240px" clearable :placeholder="t('combination.allStatus')">
              <el-option
                v-for="(dict, index) in getIntDictOptions(
                  DICT_TYPE.PROMOTION_COMBINATION_RECORD_STATUS
                )"
                :key="index"
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
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 分页列表数据展示 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="pageList" :table-layout="'auto'">
      <el-table-column align="center" :label="t('combination.id')" prop="id" min-width="50" />
      <el-table-column align="center" :label="t('combination.avatar')" prop="avatar" min-width="80">
        <template #default="scope">
          <el-avatar :src="scope.row.avatar" />
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('combination.nickname')" prop="nickname" min-width="100" />
      <el-table-column align="center" :label="t('combination.headId')" prop="headId" min-width="100">
        <template #default="{ row }: { row: CombinationRecordApi.CombinationRecordVO }">
          {{
            row.headId ? pageList.find((item) => item.id === row.headId)?.nickname : row.nickname
          }}
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('combination.startTimeLabel')"
        prop="startTime"
        min-width="180"
      />
      <el-table-column
        align="center"
        :label="t('combination.spuNameLabel')"
        prop="type"
        show-overflow-tooltip
        min-width="300"
      >
        <template #default="{ row }">
          <el-image
            :src="row.picUrl"
            class="mr-5px h-30px w-30px align-middle"
            @click="imagePreview(row.picUrl)"
          />
          <span class="align-middle">{{ row.spuName }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('combination.userSize')" prop="userSize" min-width="100" />
      <el-table-column align="center" :label="t('combination.userCountLabel')" prop="userCount" min-width="100" />
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
      <el-table-column align="center" fixed="right" :label="t('common.action')">
        <template #default="scope">
          <el-button
            v-hasPermi="['promotion:combination-record:query']"
            link
            type="primary"
            @click="openRecordListDialog(scope.row)"
          >
            {{ t('combination.viewRecord') }}
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

  <!-- 表单弹窗 -->
  <CombinationRecordListDialog ref="combinationRecordListRef" />
</template>
<script lang="ts" setup>
import CombinationRecordListDialog from './CombinationRecordListDialog.vue'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter, defaultShortcuts } from '@/utils/formatTime'
import { createImageViewer } from '@/components/ImageViewer'
import * as CombinationRecordApi from '@/api/mall/promotion/combination/combinationRecord'

defineOptions({ name: 'PromotionCombinationRecord' })

const { t } = useI18n('mall.promotion.combination') // 国际化
const queryParams = ref({
  status: undefined, // 拼团状态
  createTime: undefined, // 创建时间
  pageSize: 10,
  pageNo: 1
})
const queryFormRef = ref() // 搜索的表单
const combinationRecordListRef = ref() // 查询表单 Ref
const loading = ref(true) // 列表的加载中
const total = ref(0) // 总记录数
const pageList = ref<CombinationRecordApi.CombinationRecordVO[]>([]) // 分页数据
/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await CombinationRecordApi.getCombinationRecordPage(queryParams.value)
    pageList.value = data.list as CombinationRecordApi.CombinationRecordVO[]
    total.value = data.total
  } finally {
    loading.value = false
  }
}
// 拼团统计数据
const recordSummary = ref({
  successCount: 0,
  userCount: 0,
  virtualGroupCount: 0
})
/** 获得拼团记录统计信息 */
const getSummary = async () => {
  recordSummary.value = await CombinationRecordApi.getCombinationRecordSummary()
}

/** 查看拼团详情 */
const openRecordListDialog = (row: CombinationRecordApi.CombinationRecordVO) => {
  combinationRecordListRef.value?.open(row.headId || row.id) // 多表达式的原因，团长时 headId 为空，就是自身的情况
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 商品图预览 */
const imagePreview = (imgUrl: string) => {
  createImageViewer({
    urlList: [imgUrl]
  })
}

/** 初始化 **/
onMounted(async () => {
  await getSummary()
  await getList()
})
</script>

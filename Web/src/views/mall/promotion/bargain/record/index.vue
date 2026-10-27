<template>
  <doc-alert title="【营销】砍价活动" url="https://doc.iocoder.cn/mall/promotion-bargain/" />

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
          <el-form-item :label="t('mall.promotion.bargain.recordStatus')" prop="status">
            <el-select
              v-model="queryParams.status"
              :placeholder="t('mall.promotion.bargain.recordStatusPlaceholder')"
              clearable
              class="!w-240px"
            >
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.PROMOTION_BARGAIN_RECORD_STATUS)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('common.createTime')" prop="createTime">
            <el-date-picker
              v-model="queryParams.createTime"
              value-format="YYYY-MM-DD HH:mm:ss"
              type="daterange"
              :start-placeholder="t('common.startTime')"
              :end-placeholder="t('common.endTime')"
              :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> {{ t('common.query') }}</el-button>
            <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> {{ t('common.reset') }}</el-button>
            <el-button
              type="primary"
              plain
              @click="openForm('create')"
              v-hasPermi="['promotion:bargain-record:create']"
            >
              <Icon icon="ep:plus" class="mr-5px" /> {{ t('action.add') }}
            </el-button>
            <el-button
              type="success"
              plain
              @click="handleExport"
              :loading="exportLoading"
              v-hasPermi="['promotion:bargain-record:export']"
            >
              <Icon icon="ep:download" class="mr-5px" /> {{ t('action.export') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true" :table-layout="'auto'">
      <el-table-column :label="t('mall.promotion.bargain.recordId')" min-width="50" prop="id" />
      <el-table-column :label="t('mall.promotion.bargain.initiatorUser')" min-width="120">
        <template #default="scope">
          <el-image
            :src="scope.row.avatar"
            class="h-20px w-20px"
            :preview-src-list="[scope.row.avatar]"
            preview-teleported
          />
          {{ scope.row.nickname }}
        </template>
      </el-table-column>
      <el-table-column
        :label="t('mall.promotion.bargain.initiatorTime')"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        min-width="180"
      />
      <el-table-column :label="t('mall.promotion.bargain.bargainActivity')" min-width="150" prop="activity.name" />
      <el-table-column
        :label="t('mall.promotion.bargain.minPrice')"
        min-width="100"
        prop="activity.bargainMinPrice"
        :formatter="fenToYuanFormat"
      />
      <el-table-column
        :label="t('mall.promotion.bargain.currentPrice')"
        min-width="100"
        prop="bargainPrice"
        :formatter="fenToYuanFormat"
      />
      <el-table-column :label="t('mall.promotion.bargain.totalBargainCount')" min-width="100" prop="activity.helpMaxCount" />
      <el-table-column :label="t('mall.promotion.bargain.remainBargainCount')" min-width="100" prop="helpCount" />
      <el-table-column :label="t('mall.promotion.bargain.recordStatus')" align="center" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PROMOTION_BARGAIN_RECORD_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        :label="t('mall.promotion.bargain.endTime')"
        align="center"
        prop="endTime"
        :formatter="dateFormatter"
        min-width="180"
       fixed="right" />
      <el-table-column :label="t('mall.promotion.bargain.orderId')" align="center" prop="orderId" />
      <el-table-column :label="t('common.operation')" align="center">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openRecordListDialog(scope.row.id)"
            v-hasPermi="['promotion:bargain-help:query']"
          >
            {{ t('mall.promotion.bargain.help') }}
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

  <!-- 表单弹窗 -->
  <BargainRecordListDialog ref="recordListDialogRef" />
</template>

<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as BargainRecordApi from '@/api/mall/promotion/bargain/bargainRecord'
import { fenToYuanFormat } from '@/utils/formatter'
import BargainRecordListDialog from './BargainRecordListDialog.vue'

defineOptions({ name: 'PromotionBargainRecord' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  status: null,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await BargainRecordApi.getBargainRecordPage(queryParams)
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

/** 打开[助力]弹窗 */
const recordListDialogRef = ref()
const openRecordListDialog = (id?: number) => {
  recordListDialogRef.value.open(id)
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

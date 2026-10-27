<template>
  <doc-alert title="【交易】分销返佣" url="https://doc.iocoder.cn/mall/trade-brokerage/" />

  <ContentWrap>
    <!-- 搜索工作�?-->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('mall.trade.brokerage.userId')" prop="userId">
            <el-input
              v-model="queryParams.userId"
              :placeholder="t('mall.trade.brokerage.userId')"
              clearable
              @keyup.enter="handleQuery"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('mall.trade.brokerage.bizType')" prop="bizType">
            <el-select
              v-model="queryParams.bizType"
              :placeholder="t('mall.trade.brokerage.bizTypePlaceholder')"
              clearable
              class="!w-240px"
            >
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.BROKERAGE_RECORD_BIZ_TYPE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('common.status')" prop="status">
            <el-select v-model="queryParams.status" :placeholder="t('common.selectText')" clearable class="!w-240px">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.BROKERAGE_RECORD_STATUS)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
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
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true" :table-layout="'auto'">
      <el-table-column :label="t('common.index')" align="center" prop="id" min-width="60" />
      <el-table-column :label="t('mall.trade.brokerage.userId')" align="center" prop="userId" min-width="80" />
      <el-table-column :label="t('mall.trade.brokerage.avatar')" align="center" prop="userAvatar" min-width="70">
        <template #default="scope">
          <el-avatar :src="scope.row.userAvatar" />
        </template>
      </el-table-column>
      <el-table-column :label="t('mall.trade.brokerage.nickname')" align="center" prop="userNickname" min-width="80px" />
      <el-table-column :label="t('mall.trade.brokerage.bizType')" align="center" prop="bizType" min-width="85">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.BROKERAGE_RECORD_BIZ_TYPE" :value="scope.row.bizType" />
        </template>
      </el-table-column>
      <el-table-column :label="t('mall.trade.brokerage.bizId')" align="center" prop="bizId" min-width="80" />
      <el-table-column :label="t('mall.trade.brokerage.recordTitle_label')" align="center" prop="title" min-width="110" />
      <el-table-column
        :label="t('mall.trade.brokerage.price')"
        align="center"
        prop="price"
        min-width="60"
        :formatter="fenToYuanFormat"
      />
      <el-table-column :label="t('mall.trade.brokerage.description')" align="center" prop="description" min-width="120" />
      <el-table-column :label="t('common.status')" align="center" prop="status" min-width="85">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.BROKERAGE_RECORD_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        :label="t('mall.trade.brokerage.unfreezeTime')"
        align="center"
        prop="unfreezeTime"
        :formatter="dateFormatter"
        min-width="180"
      />
      <el-table-column
        :label="t('common.createTime')"
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
</template>

<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as BrokerageRecordApi from '@/api/mall/trade/brokerage/record'
import { fenToYuanFormat } from '@/utils/formatter'

defineOptions({ name: 'TradeBrokerageRecord' })

const { t } = useI18n() // 国际�?
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userId: null,
  bizType: null,
  price: null,
  status: null,
  createTime: []
})
const queryFormRef = ref() // 搜索的表�?
/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await BrokerageRecordApi.getBrokerageRecordPage(queryParams)
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

/** 初始�?**/
onMounted(() => {
  getList()
})
</script>

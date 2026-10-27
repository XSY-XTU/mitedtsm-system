<template>
  <Dialog v-model="dialogVisible" :title="t('mall.trade.brokerage.promoterOrderListTitle')" width="75%">
    <ContentWrap>
      <!-- 搜索工作�?-->
      <el-form
        ref="queryFormRef"
        :inline="true"
        :model="queryParams"
        class="-mb-15px"
        label-width="85px"
      >
        <el-form-item :label="t('mall.trade.brokerage.userType')" prop="sourceUserLevel">
          <el-radio-group v-model="queryParams.sourceUserLevel" @change="handleQuery">
            <el-radio-button :value="0">{{ t('mall.trade.brokerage.all') }}</el-radio-button>
            <el-radio-button :value="1">{{ t('mall.trade.brokerage.firstLevelPromoter') }}</el-radio-button>
            <el-radio-button :value="2">{{ t('mall.trade.brokerage.secondLevelPromoter') }}</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="t('common.status')" prop="status">
          <el-select
            v-model="queryParams.status"
            class="!w-240px"
            clearable
            :placeholder="t('common.selectText')"
          >
            <el-option
              v-for="dict in getIntDictOptions(DICT_TYPE.BROKERAGE_RECORD_STATUS)"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('mall.trade.brokerage.bindTimeRange')" prop="createTime">
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
      </el-form>
    </ContentWrap>

    <!-- 列表 -->
    <ContentWrap>
      <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true" :table-layout="'auto'">
        <el-table-column align="center" :label="t('mall.trade.order.no')" min-width="80px" prop="bizId" />
        <el-table-column align="center" :label="t('mall.trade.brokerage.userId')" min-width="80px" prop="sourceUserId" />
        <el-table-column align="center" :label="t('mall.trade.brokerage.avatar')" prop="sourceUserAvatar" min-width="70">
          <template #default="scope">
            <el-avatar :src="scope.row.sourceUserAvatar" />
          </template>
        </el-table-column>
        <el-table-column align="center" :label="t('mall.trade.brokerage.nickname')" min-width="80px" prop="sourceUserNickname" />
        <el-table-column
          :formatter="fenToYuanFormat"
          align="center"
          :label="t('mall.trade.brokerage.commission')"
          min-width="100px"
          prop="price"
        />
        <el-table-column align="center" :label="t('common.status')" min-width="85" prop="status">
          <template #default="scope">
            <dict-tag :type="DICT_TYPE.BROKERAGE_RECORD_STATUS" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column
          :formatter="dateFormatter"
          align="center"
          :label="t('common.createTime')"
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
  </Dialog>
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as BrokerageRecordApi from '@/api/mall/trade/brokerage/record'
import { BrokerageRecordBizTypeEnum } from '@/utils/constants'
import { fenToYuanFormat } from '@/utils/formatter'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'

/** 推广订单列表 */
defineOptions({ name: 'BrokerageOrderListDialog' })

const { t } = useI18n() // 国际�?
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userId: null,
  bizType: BrokerageRecordBizTypeEnum.ORDER.type,
  sourceUserLevel: 0,
  createTime: [],
  status: null
})
const queryFormRef = ref() // 搜索的表单

/** 打开弹窗 */
const dialogVisible = ref(false) // 弹窗的是否展示
const open = async (userId: any) => {
  dialogVisible.value = true
  queryParams.userId = userId
  resetQuery()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    // 处理全部的情况
    const data = await BrokerageRecordApi.getBrokerageRecordPage({
      ...queryParams,
      sourceUserLevel: queryParams.sourceUserLevel === 0 ? undefined : queryParams.sourceUserLevel
    })
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

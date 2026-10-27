<template>
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true" :table-layout="'auto'">
      <el-table-column align="center" :label="t('balanceRecord.id')" prop="id" />
      <el-table-column align="center" :label="t('balanceRecord.title')" prop="title" />
      <el-table-column align="center" :label="t('balanceRecord.price')" prop="price">
        <template #default="{ row }"> {{ fenToYuan(row.price) }} {{ t('balanceRecord.unit') }}</template>
      </el-table-column>
      <el-table-column align="center" :label="t('balanceRecord.balance')" prop="balance">
        <template #default="{ row }"> {{ fenToYuan(row.balance) }} {{ t('balanceRecord.unit') }}</template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('balanceRecord.tradeTime')"
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
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as WalletTransactionApi from '@/api/pay/wallet/transaction'
import { fenToYuan } from '@/utils'
import { useI18n } from '@/hooks/web/useI18n'

defineOptions({ name: 'UserBalanceList' })

const { t } = useI18n('member')

const props = defineProps({
  walletId: {
    type: Number,
    required: false
  }
})

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  walletId: null
})
const list = ref([]) // 列表的数据
/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    queryParams.walletId = props.walletId as any
    const data = await WalletTransactionApi.getWalletTransactionPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}
/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      label-width="auto"
      :model="queryParams"
      ref="queryFormRef"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('wallet.balance.userId')" prop="userId">
        <el-input
          v-model="queryParams.userId"
          :placeholder="t('common.inputText')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('wallet.balance.userType')" prop="userType">
        <el-select
          v-model="queryParams.userType"
          :placeholder="t('common.selectText')"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.USER_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('notify.createTime')" prop="createTime">
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
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> {{ t('common.search') }}</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> {{ t('common.reset') }}</el-button>
      </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true" :table-layout="'auto'">
      <el-table-column :label="t('wallet.balance.id')" align="center" prop="id" />
      <el-table-column :label="t('wallet.balance.userId')" align="center" prop="userId" />
      <el-table-column :label="t('wallet.balance.userType')" align="center" prop="userType">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.USER_TYPE" :value="scope.row.userType" />
        </template>
      </el-table-column>
      <el-table-column :label="t('wallet.balance.balance')" align="center" prop="balance">
        <template #default="{ row }"> {{ fenToYuan(row.balance) }} {{ t('wallet.balance.unit') }}</template>
      </el-table-column>
      <el-table-column :label="t('wallet.balance.totalExpense')" align="center" prop="totalExpense">
        <template #default="{ row }"> {{ fenToYuan(row.totalExpense) }} {{ t('wallet.balance.unit') }}</template>
      </el-table-column>
      <el-table-column :label="t('wallet.balance.totalRecharge')" align="center" prop="totalRecharge">
        <template #default="{ row }"> {{ fenToYuan(row.totalRecharge) }} {{ t('wallet.balance.unit') }}</template>
      </el-table-column>
      <el-table-column :label="t('wallet.balance.freezePrice')" align="center" prop="freezePrice">
        <template #default="{ row }"> {{ fenToYuan(row.freezePrice) }} {{ t('wallet.balance.unit') }}</template>
      </el-table-column>
      <el-table-column
        :label="t('wallet.transaction.createTime')"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        min-width="180"
       fixed="right" />
      <el-table-column :label="t('common.action')" align="center">
        <template #default="scope">
          <el-button link type="primary" @click="openForm(scope.row.id)">{{ t('common.detail') }}</el-button>
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

  <!-- 弹窗 -->
  <WalletForm ref="formRef" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { fenToYuan } from '@/utils'
import * as WalletApi from '@/api/pay/wallet/balance'
import WalletForm from './WalletForm.vue'
import { useI18n } from '@/hooks/web/useI18n'

const { t } = useI18n('pay')

defineOptions({ name: 'WalletBalance' })

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userId: null,
  userType: null,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await WalletApi.getWalletPage(queryParams)
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
const openForm = (id?: number) => {
  formRef.value.open(id)
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
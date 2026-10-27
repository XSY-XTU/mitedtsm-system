<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      label-width="auto"
    >
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> {{ t('common.search') }}</el-button>
            <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> {{ t('common.reset') }}</el-button>
            <el-button type="primary" plain @click="openForm('create')">
              <Icon icon="ep:plus" />{{ t('demo.withdraw.createButton') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :table-layout="'auto'">
      <el-table-column :label="t('common.action')" align="center" min-width="150" fixed="right">
        <template #default="scope">
          <el-button
            v-if="scope.row.status === 0 && !scope.row.payTransferId"
            type="primary"
            link
            @click="handleTransfer(scope.row.id)"
          >
            {{ t('demo.withdraw.transferButton') }}
          </el-button>
          <el-button
            v-else-if="scope.row.status === 20"
            type="warning"
            link
            @click="handleTransfer(scope.row.id)"
          >
            {{ t('demo.withdraw.retransferButton') }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column :label="t('demo.withdraw.id')" align="center" prop="id" min-width="100" />
      <el-table-column :label="t('demo.withdraw.subject')" align="center" prop="subject" min-width="120" />
      <el-table-column :label="t('demo.withdraw.type')" align="center" prop="type" min-width="90">
        <template #default="scope">
          <el-tag v-if="scope.row.type === 1">{{ t('demo.withdraw.typeAlipay') }}</el-tag>
          <el-tag v-else-if="scope.row.type === 2">{{ t('demo.withdraw.typeWxBalance') }}</el-tag>
          <el-tag v-else-if="scope.row.type === 3">{{ t('demo.withdraw.typeWallet') }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="t('demo.withdraw.price')" align="center" prop="price" min-width="120">
        <template #default="scope">
          <span>￥{{ (scope.row.price / 100.0).toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="t('demo.withdraw.userName')" align="center" prop="userName" min-width="150" />
      <el-table-column :label="t('demo.withdraw.userAccount')" align="center" prop="userAccount" min-width="250" />
      <el-table-column :label="t('demo.withdraw.status')" align="center" prop="status" min-width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0 && !scope.row.payTransferId" type="warning">
            {{ t('demo.withdraw.statusWait') }}
          </el-tag>
          <el-tag v-else-if="scope.row.status === 0 && scope.row.payTransferId" type="info">
            {{ t('demo.withdraw.statusTransferring') }}
          </el-tag>
          <el-tag v-else-if="scope.row.status === 10" type="success">{{ t('demo.withdraw.statusSuccess') }}</el-tag>
          <el-tag v-else-if="scope.row.status === 20" type="danger">{{ t('demo.withdraw.statusFail') }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="t('demo.withdraw.payTransferId')" align="center" prop="payTransferId" min-width="120" />
      <el-table-column :label="t('demo.withdraw.transferChannelCode')" align="center" prop="transferChannelCode" min-width="180">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PAY_CHANNEL_CODE" :value="scope.row.transferChannelCode" />
        </template>
      </el-table-column>
      <el-table-column
        :label="t('demo.withdraw.transferTime')"
        align="center"
        prop="transferTime"
        :formatter="dateFormatter"
        min-width="180"
      />
      <el-table-column
        :label="t('demo.withdraw.transferErrorMsg')"
        align="center"
        prop="transferErrorMsg"
        min-width="200"
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

  <!-- 表单弹窗：添加/修改 -->
  <DemoWithdrawForm ref="demoFormRef" @success="getList" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import * as DemoWithdrawApi from '@/api/pay/demo/withdraw'
import DemoWithdrawForm from './DemoWithdrawForm.vue'
import { DICT_TYPE } from '@/utils/dict'
import { useMessage } from '@/hooks/web/useMessage'
import { useI18n } from '@/hooks/web/useI18n'

const { t } = useI18n('pay')
const message = useMessage()
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10
})
const queryFormRef = ref() // 搜索的表单

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await DemoWithdrawApi.getDemoWithdrawPage(queryParams)
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

/** 创建示例提现单操作 */
const demoFormRef = ref()
const openForm = (type: string) => {
  demoFormRef.value.open(type)
}

/** 处理转账操作 */
const handleTransfer = async (id: number) => {
  try {
    // 转账操作的二次确认
    await message.confirm(t('demo.withdraw.transferConfirm'))
    // 发起转账
    loading.value = true
    const payTransferId = await DemoWithdrawApi.transferDemoWithdraw(id)
    message.success(t('demo.withdraw.transferSuccess') + payTransferId)
    // 刷新列表
    await getList()
  } finally {
    loading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
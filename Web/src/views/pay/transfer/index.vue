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
          <el-form-item :label="t('transfer.no')" prop="no">
        <el-input
          v-model="queryParams.no"
          :placeholder="t('transfer.noPlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('transfer.channelCode')" prop="channelCode">
        <el-select
          v-model="queryParams.channelCode"
          :placeholder="t('transfer.channelPlaceholder')"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.PAY_CHANNEL_CODE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('transfer.merchantTransferId')" prop="merchantTransferId">
        <el-input
          v-model="queryParams.merchantTransferId"
          :placeholder="t('transfer.merchantTransferIdPlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('transfer.type')" prop="type">
        <el-select v-model="queryParams.type" :placeholder="t('transfer.typePlaceholder')" clearable class="!w-240px">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.PAY_TRANSFER_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('transfer.status')" prop="status">
        <el-select
          v-model="queryParams.status"
          :placeholder="t('transfer.statusPlaceholder')"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.PAY_TRANSFER_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('transfer.userName')" prop="userName">
        <el-input
          v-model="queryParams.userName"
          :placeholder="t('transfer.userNamePlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('transfer.userAccount')" prop="accountNo">
        <el-input
          v-model="queryParams.accountNo"
          :placeholder="t('transfer.userAccountPlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('transfer.channelTransferNo')" prop="channelTransferNo">
        <el-input
          v-model="queryParams.channelTransferNo"
          :placeholder="t('transfer.channelTransferNoPlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('transfer.createTime')" prop="createTime">
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
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['pay:transfer:export']"
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
      <el-table-column :label="t('transfer.id')" align="center" prop="id" />
      <el-table-column
        :label="t('transfer.createTime')"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        min-width="180"
      />
      <el-table-column :label="t('transfer.appName')" align="center" prop="appName" min-width="100" />
      <el-table-column :label="t('transfer.price')" align="center" prop="price">
        <template #default="scope">
          <span>￥{{ (scope.row.price / 100.0).toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="t('transfer.status')" align="center" prop="status" min-width="120">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PAY_TRANSFER_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column :label="t('order.no')" align="left" min-width="300">
        <template #default="scope">
          <p class="transfer-font">
            <el-tag size="small"> {{ t('transfer.merchantTag') }}</el-tag>
            {{ scope.row.merchantTransferId }}
          </p>
          <p class="transfer-font" v-if="scope.row.no">
            <el-tag size="small" type="warning">{{ t('transfer.transferTag') }}</el-tag>
            {{ scope.row.no }}
          </p>
          <p class="transfer-font" v-if="scope.row.channelTransferNo">
            <el-tag size="small" type="success">{{ t('transfer.channelTag') }}</el-tag>
            {{ scope.row.channelTransferNo }}
          </p>
        </template>
      </el-table-column>
      <el-table-column :label="t('transfer.userName')" align="center" prop="userName" min-width="120" />
      <el-table-column :label="t('transfer.userAccount')" align="left" prop="userAccount" min-width="200" />
      <el-table-column :label="t('transfer.subject')" align="center" prop="subject" min-width="120" />
      <el-table-column :label="t('transfer.channelCode')" align="center" prop="channelCode">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PAY_CHANNEL_CODE" :value="scope.row.channelCode" />
        </template>
      </el-table-column>
      <el-table-column
        :label="t('transfer.successTimeTitle')"
        align="center"
        prop="successTime"
        :formatter="dateFormatter"
        min-width="180"
      />
      <el-table-column :label="t('common.action')" align="center" fixed="right" min-width="150">
        <template #default="scope">
          <el-button link type="primary" @click="openDetail(scope.row.id)"> {{ t('common.detail') }} </el-button>
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
    <TransferDetail ref="detailRef" />
  </ContentWrap>
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import * as TransferApi from '@/api/pay/transfer'
import { DICT_TYPE, getStrDictOptions } from '@/utils/dict'
import TransferDetail from './TransferDetail.vue'
import download from '@/utils/download'
import { useI18n } from '@/hooks/web/useI18n'

const { t } = useI18n('pay')

defineOptions({ name: 'PayTransfer' })

const message = useMessage() // 消息弹窗

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  no: null,
  appId: null,
  channelId: null,
  channelCode: null,
  merchantTransferId: null,
  type: null,
  status: null,
  successTime: [],
  price: null,
  subject: null,
  userName: null,
  userAccount: null,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await TransferApi.getTransferPage(queryParams)
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

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await TransferApi.exportTransfer(queryParams)
    download.excel(data, 'pay-transfer.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 添加/修改操作 */
const detailRef = ref()
const openDetail = (id: number) => {
  detailRef.value.open(id)
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

<style scoped>
.transfer-font {
  padding: 2px 0;
  font-size: 12px;
}
</style>
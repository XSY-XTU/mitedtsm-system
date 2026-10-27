<template>
  <doc-alert title="支付功能开启" url="https://doc.iocoder.cn/pay/build/" />

  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form
      class="-mb-15px"
      label-width="auto"
      :model="queryParams"
      ref="queryFormRef"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('notify.appId')" prop="appId">
        <el-select
          v-model="queryParams.appId"
          :placeholder="t('common.selectText')"
          clearable
          filterable
          class="!w-240px"
        >
          <el-option v-for="item in appList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('notify.type')" prop="type">
        <el-select
          v-model="queryParams.type"
          :placeholder="t('common.selectText')"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.PAY_NOTIFY_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('notify.dataId')" prop="dataId">
        <el-input
          v-model="queryParams.dataId"
          :placeholder="t('common.inputText')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('notify.status')" prop="status">
        <el-select
          v-model="queryParams.status"
          :placeholder="t('common.selectText')"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.PAY_NOTIFY_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('notify.merchantOrderId')" prop="merchantOrderId">
        <el-input
          v-model="queryParams.merchantOrderId"
          :placeholder="t('notify.merchantOrderIdPlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('notify.merchantRefundId')" prop="merchantRefundId">
        <el-input
          v-model="queryParams.merchantRefundId"
          :placeholder="t('notify.merchantRefundIdPlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('notify.merchantTransferId')" prop="merchantTransferId">
        <el-input
          v-model="queryParams.merchantTransferId"
          :placeholder="t('notify.merchantTransferIdPlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('notify.createTime')" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          style="width: 240px"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          range-separator="-"
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
    <el-table v-loading="loading" :data="list" :table-layout="'auto'">
      <el-table-column :label="t('notify.id')" align="center" prop="id" />
      <el-table-column :label="t('notify.appId')" align="center" prop="appName" />
      <el-table-column :label="t('notify.merchantInfo')" align="center" prop="merchant">
        <template #default="scope">
          <div v-if="scope.row.merchantOrderId">
            <div>{{ t('notify.merchantOrderId') }}：{{ scope.row.merchantOrderId }}</div>
          </div>
          <div v-if="scope.row.merchantRefundId">
            <div>{{ t('notify.merchantRefundId') }}：{{ scope.row.merchantRefundId }}</div>
          </div>
          <div v-if="scope.row.merchantTransferId">
            <div>{{ t('notify.merchantTransferId') }}：{{ scope.row.merchantTransferId }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column :label="t('notify.type')" align="center" prop="type">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PAY_NOTIFY_TYPE" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column :label="t('notify.dataId')" align="center" prop="dataId" />
      <el-table-column :label="t('notify.status')" align="center" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PAY_NOTIFY_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        :label="t('notify.lastExecuteTime')"
        align="center"
        prop="lastExecuteTime"
        min-width="180"
        :formatter="dateFormatter"
      />
      <el-table-column
        :label="t('notify.nextNotifyTime')"
        align="center"
        prop="nextNotifyTime"
        min-width="180"
        :formatter="dateFormatter"
      />
      <el-table-column :label="t('notify.notifyTimes')" align="center" prop="notifyTimes">
        <template #default="scope">
          <el-tag size="small" type="success">
            {{ scope.row.notifyTimes }} / {{ scope.row.maxNotifyTimes }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="t('common.action')" align="center" class-name="small-padding fixed-width" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openDetail(scope.row.id)"
            v-hasPermi="['pay:notify:query']"
          >
            {{ t('common.detail') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗：预览 -->
  <NotifyDetail ref="detailRef" />
</template>

<script lang="ts" setup>
import * as PayNotifyApi from '@/api/pay/notify'
import * as PayAppApi from '@/api/pay/app'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import NotifyDetail from './NotifyDetail.vue'
import { useI18n } from '@/hooks/web/useI18n'

const { t } = useI18n('pay')

defineOptions({ name: 'PayNotify' })

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref() // 列表的数据
const queryParams = ref({
  pageNo: 1,
  pageSize: 10,
  appId: null,
  type: null,
  dataId: null,
  status: null,
  merchantOrderId: null,
  merchantRefundId: null,
  merchantTransferId: null,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const appList = ref([]) // 支付应用列表集合

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNo = 1
  getList()
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await PayNotifyApi.getNotifyTaskPage(queryParams.value)
    list.value = data.list
    total.value = data.total
    loading.value = false
  } finally {
    loading.value = false
  }
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields()
  handleQuery()
}

/** 详情按钮操作 */
const detailRef = ref()
const openDetail = (id: number) => {
  detailRef.value.open(id)
}

/** 初始化 **/
onMounted(async () => {
  await getList()
  // 获得筛选项
  appList.value = await PayAppApi.getAppList()
})
</script>
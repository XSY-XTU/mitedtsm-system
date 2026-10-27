<template>
  <doc-alert title="【交易】分销返佣" url="https://doc.iocoder.cn/mall/trade-brokerage/" />

  <ContentWrap>
    <!-- 搜索工作区 -->
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      class="-mb-15px"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('mall.trade.brokerage.promoterId')" prop="bindUserId">
            <el-input
              v-model="queryParams.bindUserId"
              class="!w-240px"
              clearable
              :placeholder="t('mall.trade.brokerage.promoterIdPlaceholder')"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('mall.trade.brokerage.promoterQualification')" prop="brokerageEnabled">
            <el-select
              v-model="queryParams.brokerageEnabled"
              class="!w-240px"
              clearable
              :placeholder="t('mall.trade.brokerage.promoterQualificationPlaceholder')"
            >
              <el-option :value="true" :label="t('mall.trade.brokerage.yes')" />
              <el-option :value="false" :label="t('mall.trade.brokerage.no')" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('common.createTime')" prop="createTime">
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
            <el-button
              v-hasPermi="['trade:brokerage-user:create']"
              plain
              type="primary"
              @click="openCreateUserForm"
            >
              <Icon class="mr-5px" icon="ep:plus" />
              {{ t('action.add') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true" :table-layout="'auto'">
      <el-table-column align="center" :label="t('mall.trade.brokerage.userId')" min-width="80px" prop="id" />
      <el-table-column align="center" :label="t('mall.trade.brokerage.avatar')" prop="avatar" min-width="70">
        <template #default="scope">
          <el-avatar :src="scope.row.avatar" />
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('mall.trade.brokerage.nickname')" min-width="80px" prop="nickname" />
      <el-table-column align="center" :label="t('mall.trade.brokerage.promoterCount')" prop="brokerageUserCount" min-width="80" />
      <el-table-column
        align="center"
        :label="t('mall.trade.brokerage.promoterOrderCount')"
        min-width="110px"
        prop="brokerageOrderCount"
      />
      <el-table-column
        :formatter="fenToYuanFormat"
        align="center"
        :label="t('mall.trade.brokerage.promoterOrderAmount')"
        min-width="110px"
        prop="brokerageOrderPrice"
      />
      <el-table-column
        :formatter="fenToYuanFormat"
        align="center"
        :label="t('mall.trade.brokerage.withdrawnAmount')"
        min-width="100px"
        prop="withdrawPrice"
      />
      <el-table-column align="center" :label="t('mall.trade.brokerage.withdrawnCount')" min-width="100px" prop="withdrawCount" />
      <el-table-column
        :formatter="fenToYuanFormat"
        align="center"
        :label="t('mall.trade.brokerage.unwithdrawnAmount')"
        min-width="100px"
        prop="price"
      />
      <el-table-column
        :formatter="fenToYuanFormat"
        align="center"
        :label="t('mall.trade.brokerage.frozenCommission')"
        min-width="100px"
        prop="frozenPrice"
      />
      <el-table-column align="center" :label="t('mall.trade.brokerage.promoterQualification')" min-width="80px" prop="brokerageEnabled">
        <template #default="scope">
          <el-switch
            v-model="scope.row.brokerageEnabled"
            :disabled="!checkPermi(['trade:brokerage-user:update-bind-user'])"
            :active-text="t('mall.trade.brokerage.yes')"
            :inactive-text="t('mall.trade.brokerage.no')"
            inline-prompt
            @change="handleBrokerageEnabledChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('mall.trade.brokerage.promoterTime')"
        prop="brokerageTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('mall.trade.brokerage.superiorPromoterId')" prop="bindUserId" min-width="150" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('mall.trade.brokerage.bindTime')"
        prop="bindUserTime"
        min-width="180"
      />
      <el-table-column align="center" fixed="right" :label="t('common.operation')" min-width="150">
        <template #default="scope">
          <el-dropdown
            v-hasPermi="[
              'trade:brokerage-user:user-query',
              'trade:brokerage-user:order-query',
              'trade:brokerage-user:update-bind-user',
              'trade:brokerage-user:clear-bind-user'
            ]"
            @command="(command) => handleCommand(command, scope.row)"
          >
            <el-button link type="primary">
              <Icon icon="ep:d-arrow-right" />
              {{ t('common.more') }}
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                  v-if="checkPermi(['trade:brokerage-user:user-query'])"
                  command="openBrokerageUserTable"
                >
                  {{ t('mall.trade.brokerage.promoter') }}
                </el-dropdown-item>
                <el-dropdown-item
                  v-if="checkPermi(['trade:brokerage-user:order-query'])"
                  command="openBrokerageOrderTable"
                >
                  {{ t('mall.trade.brokerage.promoterOrder') }}
                </el-dropdown-item>
                <el-dropdown-item
                  v-if="checkPermi(['trade:brokerage-user:update-bind-user'])"
                  command="openUpdateBindUserForm"
                >
                  {{ t('mall.trade.brokerage.updateSuperiorPromoter') }}
                </el-dropdown-item>
                <el-dropdown-item
                  v-if="
                    scope.row.bindUserId && checkPermi(['trade:brokerage-user:clear-bind-user'])
                  "
                  command="handleClearBindUser"
                >
                  {{ t('mall.trade.brokerage.clearSuperiorPromoter') }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
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
  <!-- 修改上级推广人表单 -->
  <BrokerageUserUpdateForm ref="updateFormRef" @success="getList" />
  <!-- 推广人列表 -->
  <BrokerageUserListDialog ref="listDialogRef" />
  <!-- 推广订单列表 -->
  <BrokerageOrderListDialog ref="orderDialogRef" />
  <!-- 创建分销员 -->
  <BrokerageUserCreateForm ref="createFormRef" @success="getList" />
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as BrokerageUserApi from '@/api/mall/trade/brokerage/user'
import { checkPermi } from '@/utils/permission'
import { fenToYuanFormat } from '@/utils/formatter'
import BrokerageUserUpdateForm from '@/views/mall/trade/brokerage/user/BrokerageUserUpdateForm.vue'
import BrokerageUserListDialog from '@/views/mall/trade/brokerage/user/BrokerageUserListDialog.vue'
import BrokerageOrderListDialog from '@/views/mall/trade/brokerage/user/BrokerageOrderListDialog.vue'
import BrokerageUserCreateForm from '@/views/mall/trade/brokerage/user/BrokerageUserCreateForm.vue'

defineOptions({ name: 'TradeBrokerageUser' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  bindUserId: null,
  brokerageEnabled: true,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await BrokerageUserApi.getBrokerageUserPage(queryParams)
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

const handleCommand = (command: string, row: BrokerageUserApi.BrokerageUserVO) => {
  switch (command) {
    case 'openBrokerageUserTable':
      openBrokerageUserTable(row.id)
      break
    case 'openBrokerageOrderTable':
      openBrokerageOrderTable(row.id)
      break
    case 'openUpdateBindUserForm':
      openUpdateBindUserForm(row)
      break
    case 'handleClearBindUser':
      handleClearBindUser(row)
      break
  }
}

/** 打开推广人列表 */
const listDialogRef = ref()
const openBrokerageUserTable = (id: number) => {
  listDialogRef.value.open(id)
}

/** 打开推广订单列表 */
const orderDialogRef = ref()
const openBrokerageOrderTable = (id: number) => {
  orderDialogRef.value.open(id)
}

/** 打开表单：修改上级推广人 */
const updateFormRef = ref()
const openUpdateBindUserForm = (row: BrokerageUserApi.BrokerageUserVO) => {
  updateFormRef.value.open(row)
}

/** 创建分销员 */
const createFormRef = ref<InstanceType<typeof CreateUserForm>>()
const openCreateUserForm = () => {
  createFormRef.value?.open()
}

/** 清除上级推广人 */
const handleClearBindUser = async (row: BrokerageUserApi.BrokerageUserVO) => {
  try {
    // 二次确认
    await message.confirm(t('mall.trade.brokerage.clearConfirm', { nickname: row.nickname }))
    // 发起修改
    await BrokerageUserApi.clearBindUser({ id: row.id })
    message.success(t('mall.trade.brokerage.clearSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 推广资格：开启/关闭 */
const handleBrokerageEnabledChange = async (row: BrokerageUserApi.BrokerageUserVO) => {
  try {
    // 二次确认
    const confirmText = row.brokerageEnabled ? t('mall.trade.brokerage.openConfirm', { nickname: row.nickname }) : t('mall.trade.brokerage.closeConfirm', { nickname: row.nickname })
    await message.confirm(confirmText)
    // 发起修改
    await BrokerageUserApi.updateBrokerageEnabled({ id: row.id, enabled: row.brokerageEnabled })
    message.success(row.brokerageEnabled ? t('mall.trade.brokerage.openSuccess') : t('mall.trade.brokerage.closeSuccess'))
    // 刷新列表
    await getList()
  } catch {
    // 异常时，需要重置回之前的值
    row.brokerageEnabled = !row.brokerageEnabled
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

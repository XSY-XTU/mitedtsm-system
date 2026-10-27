<template>
  <doc-alert title="公众号接入" url="https://doc.iocoder.cn/mp/account/" />

  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('account.name')" prop="name">
            <el-input
              v-model="queryParams.name"
              :placeholder="t('account.namePlaceholder')"
              clearable
              @keyup.enter="handleQuery"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" />{{ t('common.query') }}</el-button>
            <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" />{{ t('common.reset') }}</el-button>
            <el-button type="primary" @click="openForm('create')" v-hasPermi="['mp:account:create']">
              <Icon icon="ep:plus" class="mr-5px" /> {{ t('common.add') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :table-layout="'auto'">
      <el-table-column :label="t('account.name')" align="center" prop="name" />
      <el-table-column :label="t('account.wechatAccount')" align="center" prop="account" min-width="180" />
      <el-table-column label="appId" align="center" prop="appId" min-width="180" />
      <el-table-column :label="t('account.serverUrl')" align="center" prop="appId" min-width="360">
        <template #default="scope">
          {{ 'http://服务端地址/admin-api/mp/open/' + scope.row.appId }}
        </template>
      </el-table-column>
      <el-table-column :label="t('account.qrCode')" align="center" prop="qrCodeUrl">
        <template #default="scope">
          <img
            v-if="scope.row.qrCodeUrl"
            :src="scope.row.qrCodeUrl"
            :alt="t('account.qrCode')"
            style="display: inline-block; height: 100px"
          />
          <el-button
            link
            type="primary"
            @click="handleGenerateQrCode(scope.row)"
            v-hasPermi="['mp:account:qr-code']"
          >
            {{ t('account.generateQrCode') }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column :label="t('account.remark')" align="center" prop="remark"  fixed="right" />
      <el-table-column :label="t('common.operation')" align="center">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['mp:account:update']"
          >
            {{ t('common.edit') }}
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['mp:account:delete']"
          >
            {{ t('common.delete') }}
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleCleanQuota(scope.row)"
            v-hasPermi="['mp:account:clear-quota']"
          >
            {{ t('account.clearQuota') }}
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

  <!-- 对话框(添加 / 修改) -->
  <AccountForm ref="formRef" @success="getList" />
</template>
<script lang="ts" setup>
import * as AccountApi from '@/api/mp/account'
import AccountForm from './AccountForm.vue'

defineOptions({ name: 'MpAccount' })

const message = useMessage() // 消息弹窗
const { t } = useI18n('mp') // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: null,
  account: null,
  appId: null
})
const queryFormRef = ref() // 搜索的表单

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await AccountApi.getAccountPage(queryParams)
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
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 删除按钮操作 */
const handleDelete = async (id) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await AccountApi.deleteAccount(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 生成二维码的按钮操作 */
const handleGenerateQrCode = async (row) => {
  try {
    // 生成二维码的二次确认
    await message.confirm(t('account.confirmGenerateQrCode', { name: row.name }))
    // 发起生成二维码
    await AccountApi.generateAccountQrCode(row.id)
    message.success(t('account.generateQrCodeSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 清空二维码 API 配额的按钮操作 */
const handleCleanQuota = async (row) => {
  try {
    // 清空 API 配额的二次确认
    await message.confirm(t('account.confirmClearQuota', { name: row.name }))
    // 发起清空 API 配额
    await AccountApi.clearAccountQuota(row.id)
    message.success(t('account.clearQuotaSuccess'))
  } catch {}
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

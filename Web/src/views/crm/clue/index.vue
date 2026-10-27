<template>
  <doc-alert title="【线索】线索管理" url="https://doc.iocoder.cn/crm/clue/" />
  <doc-alert title="【通用】数据权限" url="https://doc.iocoder.cn/crm/permission/" />

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
          <el-form-item :label="t('clue.name')" prop="name">
            <el-input
              v-model="queryParams.name"
              :placeholder="t('clue.namePlaceholder')"
              clearable
              @keyup.enter="handleQuery"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('clue.transformStatus')" prop="transformStatus">
            <el-select v-model="queryParams.transformStatus" class="!w-240px">
              <el-option :value="false" :label="t('clue.transformStatusNo')" />
              <el-option :value="true" :label="t('clue.transformStatusYes')" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('customer.mobile')" prop="mobile">
            <el-input
              v-model="queryParams.mobile"
              :placeholder="t('customer.mobilePlaceholder')"
              clearable
              @keyup.enter="handleQuery"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('customer.telephone')" prop="telephone">
            <el-input
              v-model="queryParams.telephone"
              :placeholder="t('customer.telephonePlaceholder')"
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
            <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> {{ t('common.search') }}</el-button>
            <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> {{ t('common.reset') }}</el-button>
            <el-button type="primary" @click="openForm('create')" v-hasPermi="['crm:clue:create']">
              <Icon icon="ep:plus" class="mr-5px" /> {{ t('action.add') }}
            </el-button>
            <el-button
              type="success"
              plain
              @click="handleExport"
              :loading="exportLoading"
              v-hasPermi="['crm:clue:export']"
            >
              <Icon icon="ep:download" class="mr-5px" /> {{ t('common.export') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-tabs v-model="activeName" @tab-click="handleTabClick">
      <el-tab-pane :label="t('customer.myResponsible')" name="1" />
      <el-tab-pane :label="t('customer.myInvolved')" name="2" />
      <el-tab-pane :label="t('customer.subordinateResponsible')" name="3" />
    </el-tabs>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true" :table-layout="'auto'">
      <el-table-column :label="t('clue.name')" align="center" prop="name" fixed="left" min-width="160">
        <template #default="scope">
          <el-link :underline="false" type="primary" @click="openDetail(scope.row.id)">
            {{ scope.row.name }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column :label="t('customer.source')" align="center" prop="source" min-width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_SOURCE" :value="scope.row.source" />
        </template>
      </el-table-column>
      <el-table-column :label="t('customer.mobile')" align="center" prop="mobile" min-width="120" />
      <el-table-column :label="t('customer.telephone')" align="center" prop="telephone" min-width="130" />
      <el-table-column :label="t('customer.email')" align="center" prop="email" min-width="180" />
      <el-table-column :label="t('customer.detailAddress')" align="center" prop="detailAddress" min-width="180" />
      <el-table-column align="center" :label="t('customer.industryId')" prop="industryId" min-width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_INDUSTRY" :value="scope.row.industryId" />
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('customer.level')" prop="level" min-width="135">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_LEVEL" :value="scope.row.level" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('clue.contactNextTime')"
        prop="contactNextTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('clue.remark')" prop="remark" min-width="200" />
      <el-table-column
        :label="t('clue.contactLastTime')"
        align="center"
        prop="contactLastTime"
        :formatter="dateFormatter"
        min-width="180"
      />
      <el-table-column align="center" :label="t('clue.contactLastContent')" prop="contactLastContent" min-width="200" />
      <el-table-column align="center" :label="t('clue.ownerUserName')" prop="ownerUserName" min-width="100" />
      <el-table-column align="center" :label="t('clue.ownerUserDeptName')" prop="ownerUserDeptName" min-width="100" />
      <el-table-column
        :label="t('clue.updateTime')"
        align="center"
        prop="updateTime"
        :formatter="dateFormatter"
        min-width="180"
      />
      <el-table-column
        :label="t('clue.createTime')"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        min-width="180"
      />
      <el-table-column align="center" :label="t('clue.creatorName')" prop="creatorName" min-width="100" />
      <el-table-column :label="t('common.action')" align="center" min-width="150" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['crm:clue:update']"
          >
            {{ t('common.edit') }}
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['crm:clue:delete']"
          >
            {{ t('common.delete') }}
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

  <!-- 表单弹窗：添加/修改 -->
  <ClueForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import * as ClueApi from '@/api/crm/clue'
import ClueForm from './ClueForm.vue'
import { TabsPaneContext } from 'element-plus'

defineOptions({ name: 'CrmClue' })

const message = useMessage() // 消息弹窗
const { t } = useI18n('crm') // 国际化
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  sceneType: '1', // 默认与 activeName 相等
  name: null,
  telephone: null,
  mobile: null,
  transformStatus: false
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中
const activeName = ref('1') // 列表 tab

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ClueApi.getCluePage(queryParams)
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

/** tab 切换 */
const handleTabClick = (tab: TabsPaneContext) => {
  queryParams.sceneType = tab.paneName
  handleQuery()
}

/** 打开线索详情 */
const { push } = useRouter()
const openDetail = (id: number) => {
  push({ name: 'CrmClueDetail', params: { id } })
}

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await ClueApi.deleteClue(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await ClueApi.exportClue(queryParams)
    download.excel(data, t('clue.exportFileName') + '.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

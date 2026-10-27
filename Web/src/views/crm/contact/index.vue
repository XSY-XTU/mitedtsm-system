<template>
  <doc-alert title="【客户】客户管理、公海客户" url="https://doc.iocoder.cn/crm/customer/" />
  <doc-alert title="【通用】数据权限" url="https://doc.iocoder.cn/crm/permission/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      class="-mb-15px"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('crm.contact.customerId')" prop="customerId">
            <el-select
              v-model="queryParams.customerId"
              class="!w-240px"
              clearable
              lable-key="name"
              :placeholder="t('crm.contact.customerIdPlaceholder')"
              value-key="id"
              @keyup.enter="handleQuery"
            >
              <el-option
                v-for="item in customerList"
                :key="item.id"
                :label="item.name"
                :value="item.id!"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('crm.contact.name')" prop="name">
            <el-input
              v-model="queryParams.name"
              class="!w-240px"
              clearable
              :placeholder="t('crm.contact.namePlaceholder')"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('crm.contact.mobile')" prop="mobile">
            <el-input
              v-model="queryParams.mobile"
              class="!w-240px"
              clearable
              :placeholder="t('crm.contact.mobilePlaceholder')"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('crm.contact.telephone')" prop="telephone">
            <el-input
              v-model="queryParams.telephone"
              class="!w-240px"
              clearable
              :placeholder="t('crm.contact.telephonePlaceholder')"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('crm.contact.wechat')" prop="wechat">
            <el-input
              v-model="queryParams.wechat"
              class="!w-240px"
              clearable
              :placeholder="t('crm.contact.wechatPlaceholder')"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('crm.contact.email')" prop="email">
            <el-input
              v-model="queryParams.email"
              class="!w-240px"
              clearable
              :placeholder="t('crm.contact.emailPlaceholder')"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button @click="handleQuery">
              <Icon class="mr-5px" icon="ep:search" />
              {{ t('common.search') }}
            </el-button>
            <el-button @click="resetQuery">
              <Icon class="mr-5px" icon="ep:refresh" />
              {{ t('common.reset') }}
            </el-button>
            <el-button v-hasPermi="['crm:contact:create']" type="primary" @click="openForm('create')">
              <Icon class="mr-5px" icon="ep:plus" />
              {{ t('common.add') }}
            </el-button>
            <el-button
              v-hasPermi="['crm:contact:export']"
              :loading="exportLoading"
              plain
              type="success"
              @click="handleExport"
            >
              <Icon class="mr-5px" icon="ep:download" />
              {{ t('common.export') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-tabs v-model="activeName" @tab-click="handleTabClick">
      <el-tab-pane :label="t('crm.customer.myResponsible')" name="1" />
      <el-tab-pane :label="t('crm.customer.myInvolved')" name="2" />
      <el-tab-pane :label="t('crm.customer.subordinateResponsible')" name="3" />
    </el-tabs>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true" :table-layout="'auto'">
      <el-table-column align="center" fixed="left" :label="t('crm.contact.name')" prop="name" min-width="160">
        <template #default="scope">
          <el-link :underline="false" type="primary" @click="openDetail(scope.row.id)">
            {{ scope.row.name }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" fixed="left" :label="t('crm.contact.customerName')" prop="customerName" min-width="120">
        <template #default="scope">
          <el-link
            :underline="false"
            type="primary"
            @click="openCustomerDetail(scope.row.customerId)"
          >
            {{ scope.row.customerName }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('crm.contact.mobile')" prop="mobile" min-width="120" />
      <el-table-column align="center" :label="t('crm.contact.telephone')" prop="telephone" min-width="130" />
      <el-table-column align="center" :label="t('crm.contact.email')" prop="email" min-width="180" />
      <el-table-column align="center" :label="t('crm.contact.post')" prop="post" min-width="120" />
      <el-table-column align="center" :label="t('crm.contact.detailAddress')" prop="detailAddress" min-width="120" />
      <el-table-column align="center" :label="t('crm.contact.master')" prop="master" min-width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="scope.row.master" />
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('crm.contact.parentName')" prop="parentName" min-width="160">
        <template #default="scope">
          <el-link :underline="false" type="primary" @click="openDetail(scope.row.parentId)">
            {{ scope.row.parentName }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column :label="t('crm.contact.detailAddress')" align="center" prop="detailAddress" min-width="180" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('crm.contact.contactNextTime')"
        prop="contactNextTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('crm.contact.sex')" prop="sex">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.SYSTEM_USER_SEX" :value="scope.row.sex" />
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('crm.contact.remark')" prop="remark" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('crm.contact.contactLastTime')"
        prop="contactLastTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('crm.contact.ownerUserName')" prop="ownerUserName" min-width="120" />
      <el-table-column align="center" :label="t('crm.contact.ownerUserDeptName')" prop="ownerUserDeptName" min-width="100" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('crm.contact.updateTime')"
        prop="updateTime"
        min-width="180"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('crm.contact.createTime')"
        prop="createTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('crm.contact.creatorName')" prop="creatorName" min-width="120" />
      <el-table-column align="center" fixed="right" :label="t('common.action')" min-width="200">
        <template #default="scope">
          <el-button
            v-hasPermi="['crm:contact:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            {{ t('common.edit') }}
          </el-button>
          <el-button
            v-hasPermi="['crm:contact:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            {{ t('common.del') }}
          </el-button>
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

  <!-- 表单弹窗：添加/修改 -->
  <ContactForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import * as ContactApi from '@/api/crm/contact'
import ContactForm from './ContactForm.vue'
import { DICT_TYPE } from '@/utils/dict'
import * as CustomerApi from '@/api/crm/customer'
import { TabsPaneContext } from 'element-plus'

defineOptions({ name: 'CrmContact' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  sceneType: '1', // 默认与 activeName 相等
  mobile: undefined,
  telephone: undefined,
  email: undefined,
  customerId: undefined,
  name: undefined,
  wechat: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中
const activeName = ref('1') // 列表 tab
const customerList = ref<CustomerApi.CustomerVO[]>([]) // 客户列表

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ContactApi.getContactPage(queryParams)
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
    await ContactApi.deleteContact(id)
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
    const data = await ContactApi.exportContact(queryParams)
    download.excel(data, '联系人.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 打开联系人详情 */
const { push } = useRouter()
const openDetail = (id: number) => {
  push({ name: 'CrmContactDetail', params: { id } })
}

/** 打开客户详情 */
const openCustomerDetail = (id: number) => {
  push({ name: 'CrmCustomerDetail', params: { id } })
}

/** 初始化 **/
onMounted(async () => {
  await getList()
  customerList.value = await CustomerApi.getCustomerSimpleList()
})
</script>

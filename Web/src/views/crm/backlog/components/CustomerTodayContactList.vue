<template>
  <ContentWrap>
    <div class="pb-5 text-xl">{{ t('backlog.customerTodayContact') }}</div>
    <!-- 搜索工作区 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item :label="t('common.status')" prop="contactStatus">
        <el-select
          v-model="queryParams.contactStatus"
          class="!w-240px"
          :placeholder="t('common.status')"
          @change="handleQuery"
        >
          <el-option
            v-for="(option, index) in CONTACT_STATUS"
            :label="option.label"
            :value="option.value"
            :key="index"
          />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('backlog.sceneType')" prop="sceneType">
        <el-select
          v-model="queryParams.sceneType"
          class="!w-240px"
          :placeholder="t('backlog.sceneType')"
          @change="handleQuery"
        >
          <el-option
            v-for="(option, index) in SCENE_TYPES"
            :label="option.label"
            :value="option.value"
            :key="index"
          />
        </el-select>
      </el-form-item>
    </el-form>
  </ContentWrap>
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true" :table-layout="'auto'">
      <el-table-column align="center" :label="t('customer.name')" fixed="left" prop="name" min-width="160">
        <template #default="scope">
          <el-link :underline="false" type="primary" @click="openDetail(scope.row.id)">
            {{ scope.row.name }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('customer.source')" prop="source" min-width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_SOURCE" :value="scope.row.source" />
        </template>
      </el-table-column>
      <el-table-column :label="t('customer.mobile')" align="center" prop="mobile" min-width="120" />
      <el-table-column :label="t('customer.telephone')" align="center" prop="telephone" min-width="130" />
      <el-table-column :label="t('customer.email')" align="center" prop="email" min-width="180" />
      <el-table-column align="center" :label="t('customer.level')" prop="level" min-width="135">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_LEVEL" :value="scope.row.level" />
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('customer.industryId')" prop="industryId" min-width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_INDUSTRY" :value="scope.row.industryId" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('customer.contactNextTime')"
        prop="contactNextTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('customer.remark')" prop="remark" min-width="200" />
      <el-table-column align="center" :label="t('customer.lockStatus')" prop="lockStatus">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="scope.row.lockStatus" />
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('customer.dealStatus')" prop="dealStatus">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="scope.row.dealStatus" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('customer.lastContactTime')"
        prop="contactLastTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('customer.lastContactContent')" prop="contactLastContent" min-width="200" />
      <el-table-column :label="t('customer.detailAddress')" align="center" prop="detailAddress" min-width="180" />
      <el-table-column align="center" :label="t('customer.poolDay')" prop="poolDay" min-width="140">
        <template #default="scope"> {{ scope.row.poolDay }} {{ t('customer.dayUnit') }}</template>
      </el-table-column>
      <el-table-column align="center" :label="t('customer.ownerUserId')" prop="ownerUserName" min-width="100" />
      <el-table-column align="center" :label="t('customer.ownerUserDeptName')" prop="ownerUserDeptName" min-width="100" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('common.updateTime')"
        prop="updateTime"
        min-width="180"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('common.createTime')"
        prop="createTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('common.creator')" prop="creatorName" min-width="100" />
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
import * as CustomerApi from '@/api/crm/customer'
import { DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import { CONTACT_STATUS, SCENE_TYPES } from './common'

defineOptions({ name: 'CrmCustomerTodayContactList' })

const { t } = useI18n('crm') // 国际化
const { push } = useRouter()

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = ref({
  pageNo: 1,
  pageSize: 10,
  contactStatus: 1,
  sceneType: 1,
  pool: null // 是否公海数据
})
const queryFormRef = ref() // 搜索的表单
/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await CustomerApi.getCustomerPage(queryParams.value)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNo = 1
  getList()
}

/** 打开客户详情 */
const openDetail = (id: number) => {
  push({ name: 'CrmCustomerDetail', params: { id } })
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

<style lang="scss"></style>

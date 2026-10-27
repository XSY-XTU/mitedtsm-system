<template>
  <ContentWrap>
    <div class="pb-5 text-xl">{{ t('backlog.clueFollow') }}</div>
    <!-- 搜索工作区 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item :label="t('common.status')" prop="followUpStatus">
        <el-select
          v-model="queryParams.followUpStatus"
          class="!w-240px"
          :placeholder="t('common.status')"
          @change="handleQuery"
        >
          <el-option
            v-for="(option, index) in FOLLOWUP_STATUS"
            :label="option.label"
            :value="option.value"
            :key="index"
          />
        </el-select>
      </el-form-item>
    </el-form>
  </ContentWrap>
  <!-- 列表 -->
  <ContentWrap>
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
    </el-table>
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>
</template>
<script setup lang="ts">
import * as ClueApi from '@/api/crm/clue'
import { DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import { FOLLOWUP_STATUS } from './common'

defineOptions({ name: 'CrmClueFollowList' })

const { t } = useI18n('crm') // 国际化
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  followUpStatus: false,
  transformStatus: false
})
const queryFormRef = ref() // 搜索的表单
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

/** 打开线索详情 */
const { push } = useRouter()
const openDetail = (id: number) => {
  push({ name: 'CrmClueDetail', params: { id } })
}

/** 激活时 */
onActivated(async () => {
  await getList()
})

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
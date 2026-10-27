<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item :label="t('experience.record.bizType')" prop="bizType">
        <el-select
          v-model="queryParams.bizType"
          :placeholder="t('experience.record.bizTypePlaceholder')"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.MEMBER_EXPERIENCE_BIZ_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('experience.record.title')" prop="title">
        <el-input
          v-model="queryParams.title"
          :placeholder="t('experience.record.titlePlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item :label="t('common.createTime')" prop="createTime">
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
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> {{ t('common.query') }}</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> {{ t('common.reset') }}</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true" :table-layout="'auto'">
      <el-table-column :label="t('experience.record.id')" align="center" prop="id" min-width="150" />
      <el-table-column
        :label="t('experience.record.earnTime')"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
      />
      <el-table-column :label="t('experience.record.experienceLabel')" align="center" prop="experience" min-width="150">
        <template #default="scope">
          <el-tag v-if="scope.row.experience > 0" class="ml-2" type="success" effect="dark">
            +{{ scope.row.experience }}
          </el-tag>
          <el-tag v-else class="ml-2" type="danger" effect="dark">
            {{ scope.row.experience }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="t('experience.record.totalExperienceLabel')" align="center" prop="totalExperience" min-width="150">
        <template #default="scope">
          <el-tag class="ml-2" effect="dark">
            {{ scope.row.totalExperience }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="t('experience.record.title')" align="center" prop="title" min-width="150" />
      <el-table-column :label="t('experience.record.description')" align="center" prop="description" />
      <el-table-column :label="t('experience.record.bizId')" align="center" prop="bizId" min-width="150" />
      <el-table-column :label="t('experience.record.bizType')" align="center" prop="bizType" min-width="150">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.MEMBER_EXPERIENCE_BIZ_TYPE" :value="scope.row.bizType" />
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
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import * as ExperienceRecordApi from '@/api/member/experience-record/index'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { useI18n } from '@/hooks/web/useI18n'

defineOptions({ name: 'UserExperienceRecordList' })

const { t } = useI18n('member')

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userId: null,
  bizId: null,
  bizType: null,
  title: null,
  description: null,
  experience: null,
  totalExperience: null,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ExperienceRecordApi.getExperienceRecordPage(queryParams)
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

const { userId } = defineProps({
  userId: {
    type: Number,
    required: true
  }
})
/** 初始化 **/
onMounted(() => {
  queryParams.userId = userId
  getList()
})
</script>
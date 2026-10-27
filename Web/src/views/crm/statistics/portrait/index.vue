<!-- 数据统计 - 客户画像 -->
<template>
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
          <el-form-item :label="t('timeRange')" prop="orderDate">
            <el-date-picker
              v-model="queryParams.times"
              :shortcuts="defaultShortcuts"
              class="!w-240px"
              :end-placeholder="t('common.endTime')"
              :start-placeholder="t('common.startTime')"
              type="daterange"
              value-format="YYYY-MM-DD HH:mm:ss"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('dept')" prop="deptId">
            <el-tree-select
              v-model="queryParams.deptId"
              :data="deptList"
              :props="defaultProps"
              check-strictly
              class="!w-240px"
              node-key="id"
              :placeholder="t('dept')"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button @click="handleQuery">
              <Icon class="mr-5px" icon="ep:search" />
              {{ t('search') }}
            </el-button>
            <el-button @click="resetQuery">
              <Icon class="mr-5px" icon="ep:refresh" />
              {{ t('reset') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 客户画像统计 -->
  <el-col>
    <el-tabs v-model="activeTab">
      <!-- 城市分布分析 -->
      <el-tab-pane :label="t('portrait.area')" name="customerArea" lazy>
        <PortraitCustomerArea ref="customerAreaRef" :query-params="queryParams" />
      </el-tab-pane>
      <!-- 客户行业分析 -->
      <el-tab-pane :label="t('portrait.industry')" name="customerIndustry" lazy>
        <PortraitCustomerIndustry ref="customerIndustryRef" :query-params="queryParams" />
      </el-tab-pane>
      <!-- 客户级别分析 -->
      <el-tab-pane :label="t('portrait.level')" name="customerLevel" lazy>
        <PortraitCustomerLevel ref="customerLevelRef" :query-params="queryParams" />
      </el-tab-pane>
      <!-- 客户来源分析 -->
      <el-tab-pane :label="t('portrait.source')" name="customerSource" lazy>
        <PortraitCustomerSource ref="customerSourceRef" :query-params="queryParams" />
      </el-tab-pane>
    </el-tabs>
  </el-col>
</template>

<script lang="ts" setup>
import PortraitCustomerArea from './components/PortraitCustomerArea.vue'
import PortraitCustomerIndustry from './components/PortraitCustomerIndustry.vue'
import PortraitCustomerLevel from './components/PortraitCustomerLevel.vue'
import PortraitCustomerSource from './components/PortraitCustomerSource.vue'
import { defaultProps, handleTree } from '@/utils/tree'
import * as DeptApi from '@/api/system/dept'
import { beginOfDay, defaultShortcuts, endOfDay, formatDate } from '@/utils/formatTime'
import { useUserStore } from '@/store/modules/user'

import { useI18n } from '@/hooks/web/useI18n'

defineOptions({ name: 'CrmStatisticsPortrait' })

const { t } = useI18n('crm.statistics') // 国际化

const queryParams = reactive({
  deptId: useUserStore().getUser.deptId,
  times: [
    // 默认显示最近一周的数据
    formatDate(beginOfDay(new Date(new Date().getTime() - 3600 * 1000 * 24 * 7))),
    formatDate(endOfDay(new Date(new Date().getTime() - 3600 * 1000 * 24)))
  ]
})

const queryFormRef = ref() // 搜索的表单
const deptList = ref<Tree[]>([]) // 树形结构
const activeTab = ref('customerArea')
const customerAreaRef = ref() // 城市分布分析
const customerIndustryRef = ref() // 客户行业分析
const customerLevelRef = ref() // 客户级别分析
const customerSourceRef = ref() // 客户来源分析

/** 搜索按钮操作 */
const handleQuery = () => {
  switch (activeTab.value) {
    case 'customerArea': // 城市分布分析
      customerAreaRef.value?.loadData?.()
      break
    case 'customerIndustry': // 客户行业分析
      customerIndustryRef.value?.loadData?.()
      break
    case 'customerLevel': // 客户级别分析
      customerLevelRef.value?.loadData?.()
      break
    case 'customerSource': // 客户来源分析
      customerSourceRef.value?.loadData?.()
      break
  }
}

// 当 activeTab 改变时，刷新当前活动的 tab
watch(activeTab, () => {
  handleQuery()
})

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

// 加载部门树
onMounted(async () => {
  deptList.value = handleTree(await DeptApi.getSimpleDeptList())
})
</script>
<style lang="scss" scoped></style>
<!-- 成交周期分析 -->
<template>
  <!-- Echarts-->
  <el-card shadow="never">
    <el-skeleton :loading="loading" animated>
      <Echart :height="500" :options="echartsOption" />
    </el-skeleton>
  </el-card>

  <!-- 统计列表 -->
  <el-card shadow="never" class="mt-16px">
    <el-table v-loading="loading" :data="list" :table-layout="'auto'">
      <el-table-column :label="t('customer.index')" align="center" type="index" width="80" />
      <el-table-column :label="t('customer.ownerUserName')" align="center" prop="ownerUserName" min-width="200" />
      <el-table-column
        :label="t('customer.customerDealCycle')"
        align="center"
        prop="customerDealCycle"
        min-width="200"
      />
      <el-table-column :label="t('customer.customerDealCount')" align="center" prop="customerDealCount" min-width="200" />
    </el-table>
  </el-card>
</template>
<script setup lang="ts">
import {
  StatisticsCustomerApi,
  CrmStatisticsCustomerDealCycleByDateRespVO,
  CrmStatisticsCustomerSummaryByDateRespVO
} from '@/api/crm/statistics/customer'
import { EChartsOption } from 'echarts'

defineOptions({ name: 'CustomerDealCycleByUser' })

const { t } = useI18n('crm.statistics') // 国际
const props = defineProps<{ queryParams: any }>() // 搜索参数

const loading = ref(false) // 加载
const list = ref<CrmStatisticsCustomerDealCycleByDateRespVO[]>([]) // 列表的数
/** 柱状图配置：纵向 */
const echartsOption = reactive<EChartsOption>({
  grid: {
    left: 20,
    right: 40, // X 轴右侧显示完
    bottom: 20,
    containLabel: true
  },
  legend: {},
  series: [
    {
      name: t('customer.dealCycleDay'),
      type: 'bar',
      data: [],
      yAxisIndex: 0
    },
    {
      name: t('customer.customerDealCount'),
      type: 'bar',
      data: [],
      yAxisIndex: 1
    }
  ],
  toolbox: {
    feature: {
      dataZoom: {
        xAxisIndex: false // 数据区域缩放：Y 轴不缩放
      },
      brush: {
        type: ['lineX', 'clear'] // 区域缩放按钮、还原按
        },
      saveAsImage: { show: true, name: t('customer.dealCycleByUser') } // 保存为图
      }
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    }
  },
  yAxis: [
    {
      type: 'value',
      name: t('customer.dealCycleDay'),
      min: 0,
      minInterval: 1 // 显示整数刻度
    },
    {
      type: 'value',
      name: t('customer.customerDealCount'),
      min: 0,
      minInterval: 1, // 显示整数刻度
      splitLine: {
        lineStyle: {
          type: 'dotted', // 右侧网格线虚 减少混乱
          opacity: 0.7
        }
      }
    }
  ],
  xAxis: {
    type: 'category',
    name: t('customer.date'),
    data: []
  }
}) as EChartsOption

/** 获取数据并填充图*/
const fetchAndFill = async () => {
  // 1. 加载统计数据
  const customerDealCycleByDate = await StatisticsCustomerApi.getCustomerDealCycleByDate(
    props.queryParams
  )
  const customerSummaryByDate = await StatisticsCustomerApi.getCustomerSummaryByDate(
    props.queryParams
  )
  const customerDealCycleByUser = await StatisticsCustomerApi.getCustomerDealCycleByUser(
    props.queryParams
  )
  // 2.1 更新 Echarts 数据
  if (echartsOption.xAxis && echartsOption.xAxis['data']) {
    echartsOption.xAxis['data'] = customerDealCycleByDate.map(
      (s: CrmStatisticsCustomerDealCycleByDateRespVO) => s.time
    )
  }
  if (echartsOption.series && echartsOption.series[0] && echartsOption.series[0]['data']) {
    echartsOption.series[0]['data'] = customerDealCycleByDate.map(
      (s: CrmStatisticsCustomerDealCycleByDateRespVO) => s.customerDealCycle
    )
  }
  if (echartsOption.series && echartsOption.series[1] && echartsOption.series[1]['data']) {
    echartsOption.series[1]['data'] = customerSummaryByDate.map(
      (s: CrmStatisticsCustomerSummaryByDateRespVO) => s.customerDealCount
    )
  }
  // 2.2 更新列表数据
  list.value = customerDealCycleByUser
}

/** 获取统计数据 */
const loadData = async () => {
  loading.value = true
  try {
    await fetchAndFill()
  } finally {
    loading.value = false
  }
}
defineExpose({ loadData })

/** 初始*/
onMounted(() => {
  loadData()
})
</script>

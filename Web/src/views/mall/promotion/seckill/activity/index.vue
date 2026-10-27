<template>
  <doc-alert title="【营销】秒杀活动" url="https://doc.iocoder.cn/mall/promotion-seckill/" />

  <ContentWrap>
    <!-- 搜索工作区 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('mall.promotion.seckill.name')" prop="name">
            <el-input
              v-model="queryParams.name"
              :placeholder="t('mall.promotion.seckill.namePlaceholder')"
              clearable
              @keyup.enter="handleQuery"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('mall.promotion.seckill.status')" prop="status">
            <el-select
              v-model="queryParams.status"
              :placeholder="t('mall.promotion.seckill.statusPlaceholder')"
              clearable
              class="!w-240px"
            >
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> {{ t('common.query') }}</el-button>
            <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> {{ t('common.reset') }}</el-button>
            <el-button
              type="primary"
              plain
              @click="openForm('create')"
              v-hasPermi="['promotion:seckill-activity:create']"
            >
              <Icon icon="ep:plus" class="mr-5px" /> {{ t('mall.promotion.seckill.add') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true" :table-layout="'auto'">
      <el-table-column :label="t('mall.promotion.seckill.activityId')" prop="id" min-width="80" />
      <el-table-column :label="t('mall.promotion.seckill.name')" prop="name" min-width="140" />
      <el-table-column
        :label="t('mall.promotion.seckill.configIds')"
        prop="configIds"
        min-width="220"
        :show-overflow-tooltip="false"
      >
        <template #default="scope">
          <el-tag v-for="(configId, index) in scope.row.configIds" :key="index" class="mr-5px">
            {{ formatConfigNames(configId) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="t('mall.promotion.seckill.activeTime')" min-width="210">
        <template #default="scope">
          {{ formatDate(scope.row.startTime, 'YYYY-MM-DD') }}
          ~ {{ formatDate(scope.row.endTime, 'YYYY-MM-DD') }}
        </template>
      </el-table-column>
      <el-table-column :label="t('mall.promotion.seckill.picUrl')" prop="spuName" min-width="80">
        <template #default="scope">
          <el-image
            :src="scope.row.picUrl"
            class="h-40px w-40px"
            :preview-src-list="[scope.row.picUrl]"
            preview-teleported
          />
        </template>
      </el-table-column>
      <el-table-column :label="t('mall.promotion.seckill.spuName')" prop="spuName" min-width="300" />
      <el-table-column
        :label="t('mall.promotion.seckill.marketPrice')"
        prop="marketPrice"
        min-width="100"
        :formatter="fenToYuanFormat"
      />
      <el-table-column :label="t('mall.promotion.seckill.marketPrice')" prop="marketPrice" min-width="100" />
      <el-table-column :label="t('mall.promotion.seckill.seckillPrice')" prop="seckillPrice" min-width="100">
        <template #default="scope">
          {{ formatSeckillPrice(scope.row.products) }}
        </template>
      </el-table-column>
      <el-table-column :label="t('mall.promotion.seckill.status')" align="center" prop="status" min-width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column :label="t('mall.promotion.seckill.stock')" align="center" prop="stock" min-width="80" />
      <el-table-column :label="t('mall.promotion.seckill.totalStock')" align="center" prop="totalStock" min-width="80" />
      <el-table-column
        :label="t('common.createTime')"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        min-width="180"
      />
      <el-table-column :label="t('common.operation')" align="center" min-width="150" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['promotion:seckill-activity:update']"
          >
            {{ t('action.edit') }}
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleClose(scope.row.id)"
            v-if="scope.row.status === 0"
            v-hasPermi="['promotion:seckill-activity:close']"
          >
            {{ t('mall.promotion.seckill.close') }}
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-else
            v-hasPermi="['promotion:seckill-activity:delete']"
          >
            {{ t('action.del') }}
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
  <SeckillActivityForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as SeckillActivityApi from '@/api/mall/promotion/seckill/seckillActivity'
import { SeckillConfigApi } from '@/api/mall/promotion/seckill/seckillConfig'
import SeckillActivityForm from './SeckillActivityForm.vue'
import { formatDate } from '@/utils/formatTime'
import { fenToYuanFormat } from '@/utils/formatter'
import { fenToYuan } from '@/utils'

defineOptions({ name: 'SeckillActivity' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: null,
  status: null
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await SeckillActivityApi.getSeckillActivityPage(queryParams)
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

/** 关闭按钮操作 */
const handleClose = async (id: number) => {
  try {
    // 关闭的二次确认
    await message.confirm(t('mall.promotion.seckill.closeConfirm'))
    // 发起关闭
    await SeckillActivityApi.closeSeckillActivity(id)
    message.success(t('mall.promotion.seckill.closeSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await SeckillActivityApi.deleteSeckillActivity(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

const configList = ref([]) // 时段配置精简列表
const formatConfigNames = (configId) => {
  const config = configList.value.find((item) => item.id === configId)
  return config != null ? `${config.name}[${config.startTime} ~ ${config.endTime}]` : ''
}

const formatSeckillPrice = (products) => {
  const seckillPrice = Math.min(...products.map((item) => item.seckillPrice))
  return `￥${fenToYuan(seckillPrice)}`
}

/** 初始化 **/
onMounted(async () => {
  await getList()
  // 获得秒杀时间段
  configList.value = await SeckillConfigApi.getSimpleSeckillConfigList()
})
</script>

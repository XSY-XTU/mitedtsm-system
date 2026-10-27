<template>
  <doc-alert title="【营销】限时折扣" url="https://doc.iocoder.cn/mall/promotion-discount/" />

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
          <el-form-item :label="t('mall.promotion.discount.name')" prop="name">
            <el-input
              v-model="queryParams.name"
              :placeholder="t('mall.promotion.discount.namePlaceholder')"
              clearable
              @keyup.enter="handleQuery"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('mall.promotion.discount.status')" prop="status">
            <el-select
              v-model="queryParams.status"
              :placeholder="t('mall.promotion.discount.statusPlaceholder')"
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
        <el-col :span="8">
          <el-form-item :label="t('mall.promotion.discount.activeTime')" prop="activeTime">
            <el-date-picker
              v-model="queryParams.activeTime"
              value-format="YYYY-MM-DD HH:mm:ss"
              type="daterange"
              :start-placeholder="t('mall.promotion.discount.startTimePlaceholder')"
              :end-placeholder="t('mall.promotion.discount.endTimePlaceholder')"
              :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
              class="!w-240px"
            />
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
              v-hasPermi="['promotion:discount-activity:create']"
            >
              <Icon icon="ep:plus" class="mr-5px" /> {{ t('mall.promotion.discount.addActivity') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>
  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true" :table-layout="'auto'">
      <el-table-column :label="t('mall.promotion.discount.activityId')" prop="id" min-width="80" />
      <el-table-column :label="t('mall.promotion.discount.name')" prop="name" min-width="140" />
      <el-table-column :label="t('mall.promotion.discount.activeTime')" min-width="210">
        <template #default="scope">
          {{ formatDate(scope.row.startTime, 'YYYY-MM-DD') }}
          ~ {{ formatDate(scope.row.endTime, 'YYYY-MM-DD') }}
        </template>
      </el-table-column>
<!--      <el-table-column label="商品图片" prop="spuName" min-width="80">-->
<!--        <template #default="scope">-->
<!--          <el-image-->
<!--            :src="scope.row.picUrl"-->
<!--            class="h-40px w-40px"-->
<!--            :preview-src-list="[scope.row.picUrl]"-->
<!--            preview-teleported-->
<!--          />-->
<!--        </template>-->
<!--      </el-table-column>-->
<!--      <el-table-column label="商品标题" prop="spuName" min-width="300" />-->
      <el-table-column :label="t('mall.promotion.discount.status')" align="center" prop="status" min-width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
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
            v-hasPermi="['promotion:discount-activity:update']"
          >
            {{ t('action.edit') }}
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleClose(scope.row.id)"
            v-if="scope.row.status === 0"
            v-hasPermi="['promotion:discount-activity:close']"
          >
            {{ t('mall.promotion.discount.close') }}
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-else
            v-hasPermi="['promotion:discount-activity:delete']"
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
  <DiscountActivityForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as DiscountActivity from '@/api/mall/promotion/discount/discountActivity'
import DiscountActivityForm from './DiscountActivityForm.vue'
import { formatDate } from '@/utils/formatTime'
import { fenToYuanFormat } from '@/utils/formatter'
import { fenToYuan } from '@/utils'

defineOptions({ name: 'DiscountActivity' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  activeTime: null,
  name: null,
  status: null
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await DiscountActivity.getDiscountActivityPage(queryParams)
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
    await message.confirm(t('mall.promotion.discount.closeConfirm'))
    // 发起关闭
    await DiscountActivity.closeDiscountActivity(id)
    message.success(t('mall.promotion.discount.closeSuccess'))
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
    await DiscountActivity.deleteDiscountActivity(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

const configList = ref([]) // 时段配置精简列表
// const formatConfigNames = (configId) => {
//   const config = configList.value.find((item) => item.id === configId)
//   return config != null ? `${config.name}[${config.startTime} ~ ${config.endTime}]` : ''
// }

const formatSeckillPrice = (products) => {
  // const seckillPrice = Math.min(...products.map((item) => item.seckillPrice))
  console.log(products)
  const seckillPrice = 200
  return `￥${fenToYuan(seckillPrice)}`
}

/** 初始化 **/
onMounted(async () => {
  await getList()
})
</script>

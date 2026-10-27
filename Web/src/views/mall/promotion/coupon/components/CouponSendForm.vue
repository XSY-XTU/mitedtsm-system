<template>
  <Dialog v-model="dialogVisible" :appendToBody="true" :title="t('mall.promotion.coupon.sendCoupon')" width="70%">
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="auto"
    >
      <el-form-item :label="t('mall.promotion.coupon.name')" prop="name">
        <el-input
          v-model="queryParams.name"
          class="!w-240px"
          :placeholder="t('mall.promotion.coupon.namePlaceholder')"
          clearable
          @keyup="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon class="mr-5px" icon="ep:search" />
          {{ t('common.query') }}
        </el-button>
        <el-button @click="resetQuery">
          <Icon class="mr-5px" icon="ep:refresh" />
          {{ t('common.reset') }}
        </el-button>
      </el-form-item>
    </el-form>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list" show-overflow-tooltip :table-layout="'auto'">
      <el-table-column align="center" :label="t('mall.promotion.coupon.name')" prop="name" min-width="60" />
      <el-table-column
        :label="t('mall.promotion.coupon.discountAmountLabel')"
        align="center"
        prop="discount"
        :formatter="discountFormat"
        min-width="60"
      />
      <el-table-column
        align="center"
        :label="t('mall.promotion.coupon.minConsumption')"
        prop="usePrice"
        min-width="60"
        :formatter="usePriceFormat"
      />
      <el-table-column
        align="center"
        :label="t('mall.promotion.coupon.validityPeriod')"
        prop="validityType"
        min-width="140"
        :formatter="validityTypeFormat"
      />
      <el-table-column
        align="center"
        :label="t('mall.promotion.coupon.remainedCount')"
        min-width="60"
        :formatter="remainedCountFormat"
      />
      <el-table-column :label="t('common.operation')" align="center" min-width="150" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="primary"
            :disabled="sendLoading"
            :loading="sendLoading"
            @click="handleSendCoupon(scope.row.id)"
            v-hasPermi="['promotion:coupon:send']"
          >
            {{ t('mall.promotion.coupon.send') }}
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
    <div class="clear-both"></div>
  </Dialog>
</template>
<script lang="ts" setup>
import * as CouponTemplateApi from '@/api/mall/promotion/coupon/couponTemplate'
import * as CouponApi from '@/api/mall/promotion/coupon/coupon'
import {
  discountFormat,
  remainedCountFormat,
  usePriceFormat,
  validityTypeFormat
} from '@/views/mall/promotion/coupon/formatter'
import { CouponTemplateTakeTypeEnum } from '@/utils/constants'

defineOptions({ name: 'PromotionCouponSendForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗
const total = ref(0) // 列表的总页数
const list = ref<any[]>([]) // 列表的数据
const loading = ref(false) // 列表的加载中
const sendLoading = ref(false) // 发送按钮的加载中
const dialogVisible = ref(false) // 弹窗的是否展示
const queryParams = ref({
  pageNo: 1,
  pageSize: 10,
  name: null,
  canTakeTypes: [CouponTemplateTakeTypeEnum.ADMIN.type]
}) // 查询参数
const queryFormRef = ref() // 搜索的表单
// 领取人的编号列表
let userIds: number[] = []

/** 打开弹窗 */
const open = (ids: number[]) => {
  userIds = ids
  // 打开时重置查询，防止发送列表剩余数量未更新的问题
  resetQuery()

  dialogVisible.value = true
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await CouponTemplateApi.getCouponTemplatePage(queryParams.value)
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

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef?.value?.resetFields()
  handleQuery()
}

/** 发送操作 **/
const handleSendCoupon = async (templateId: number) => {
  try {
    sendLoading.value = true
    await CouponApi.sendCoupon({ templateId, userIds })
    // 提示
    message.success(t('mall.promotion.coupon.sendSuccess'))
    dialogVisible.value = false
  } finally {
    sendLoading.value = false
  }
}
</script>

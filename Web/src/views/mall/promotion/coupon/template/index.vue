<template>
  <doc-alert title="【营销】优惠劵" url="https://doc.iocoder.cn/mall/promotion-coupon/" />

  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      class="-mb-15px"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('mall.promotion.coupon.name')" prop="name">
            <el-input
              v-model="queryParams.name"
              class="!w-240px"
              clearable
              :placeholder="t('mall.promotion.coupon.namePlaceholder')"
              @keyup="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('mall.promotion.coupon.discountType')" prop="discountType">
            <el-select
              v-model="queryParams.discountType"
              class="!w-240px"
              clearable
              :placeholder="t('mall.promotion.coupon.discountTypePlaceholder')"
            >
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.PROMOTION_DISCOUNT_TYPE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('mall.promotion.coupon.status')" prop="status">
            <el-select
              v-model="queryParams.status"
              class="!w-240px"
              clearable
              :placeholder="t('mall.promotion.coupon.statusPlaceholder')"
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
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('common.createTime')" prop="createTime">
            <el-date-picker
              v-model="queryParams.createTime"
              :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
              class="!w-240px"
              :end-placeholder="t('common.endTime')"
              :start-placeholder="t('common.startTime')"
              type="daterange"
              value-format="YYYY-MM-DD HH:mm:ss"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button @click="handleQuery">
              <Icon class="mr-5px" icon="ep:search" />
              {{ t('common.query') }}
            </el-button>
            <el-button @click="resetQuery">
              <Icon class="mr-5px" icon="ep:refresh" />
              {{ t('common.reset') }}
            </el-button>
            <el-button
              v-hasPermi="['promotion:coupon-template:create']"
              plain
              type="primary"
              @click="openForm('create')"
            >
              <Icon class="mr-5px" icon="ep:plus" />
              {{ t('action.add') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :table-layout="'auto'">
      <el-table-column :label="t('mall.promotion.coupon.name')" min-width="140" prop="name" />
      <el-table-column :label="t('common.type')" min-width="130" prop="productScope">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PROMOTION_PRODUCT_SCOPE" :value="scope.row.productScope" />
        </template>
      </el-table-column>
      <el-table-column :label="t('mall.promotion.coupon.discount')" min-width="110" prop="discount">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PROMOTION_DISCOUNT_TYPE" :value="scope.row.discountType" />
          <div>{{ discountFormat(scope.row) }}</div>
        </template>
      </el-table-column>
      <el-table-column :label="t('mall.promotion.coupon.takeType')" min-width="100" prop="takeType">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PROMOTION_COUPON_TAKE_TYPE" :value="scope.row.takeType" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="validityTypeFormat"
        align="center"
        :label="t('mall.promotion.coupon.useTime')"
        prop="validityType"
        min-width="185"
      />
      <el-table-column
        :formatter="totalCountFormat"
        align="center"
        :label="t('mall.promotion.coupon.totalCount')"
        prop="totalCount"
      />
      <el-table-column
        :formatter="remainedCountFormat"
        align="center"
        :label="t('mall.promotion.coupon.remainedCount')"
        prop="totalCount"
      />
      <el-table-column
        :formatter="takeLimitCountFormat"
        align="center"
        :label="t('mall.promotion.coupon.takeLimitCount')"
        prop="takeLimitCount"
      />
      <el-table-column align="center" :label="t('common.status')" prop="status">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="0"
            :inactive-value="1"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('common.createTime')"
        prop="createTime"
        min-width="180"
      />
      <el-table-column
        align="center"
        class-name="small-padding fixed-width"
        fixed="right"
        :label="t('common.operation')"
        min-width="120"
      >
        <template #default="scope">
          <el-button
            v-hasPermi="['promotion:coupon-template:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            {{ t('action.edit') }}
          </el-button>
          <el-button
            v-hasPermi="['promotion:coupon-template:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            {{ t('action.del') }}
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
  <CouponTemplateForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import * as CouponTemplateApi from '@/api/mall/promotion/coupon/couponTemplate'
import { CommonStatusEnum } from '@/utils/constants'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import CouponTemplateForm from './CouponTemplateForm.vue'
import {
  discountFormat,
  remainedCountFormat,
  takeLimitCountFormat,
  totalCountFormat,
  validityTypeFormat
} from '@/views/mall/promotion/coupon/formatter'

defineOptions({ name: 'PromotionCouponTemplate' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 字典表格数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: null,
  status: null,
  discountType: null,
  type: null,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    // 执行查询
    const data = await CouponTemplateApi.getCouponTemplatePage(queryParams)
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
  queryFormRef?.value?.resetFields()
  handleQuery()
}

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 优惠劵模板状态修改 */
const handleStatusChange = async (row: any) => {
  // 此时，row 已经变成目标状态了，所以可以直接提交请求和提示
  let text = row.status === CommonStatusEnum.ENABLE ? t('common.enable') : t('common.disable')

  try {
    await message.confirm(t('mall.promotion.coupon.enableConfirm'))
    await CouponTemplateApi.updateCouponTemplateStatus(row.id, row.status)
    message.success(row.status === CommonStatusEnum.ENABLE ? t('mall.promotion.coupon.enableSuccess') : t('mall.promotion.coupon.disableSuccess'))
  } catch {
    // 异常时，需要将 row.status 状态重置回之前值
    row.status =
      row.status === CommonStatusEnum.ENABLE ? CommonStatusEnum.DISABLE : CommonStatusEnum.ENABLE
  }
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.confirm(t('mall.promotion.coupon.deleteConfirm', { id }))
    // 发起删除
    await CouponTemplateApi.deleteCouponTemplate(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

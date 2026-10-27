<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="65%">
    <!-- 搜索工作-->
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
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </ContentWrap>

    <!-- 列表 -->
    <ContentWrap>
      <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange" :table-layout="'auto'">
        <el-table-column type="selection" width="55" />
        <el-table-column :label="t('mall.promotion.coupon.name')" min-width="140" prop="name" />
        <el-table-column :label="t('common.type')" min-width="80" prop="productScope">
          <template #default="scope">
            <dict-tag :type="DICT_TYPE.PROMOTION_PRODUCT_SCOPE" :value="scope.row.productScope" />
          </template>
        </el-table-column>
        <el-table-column :label="t('mall.promotion.coupon.discount')" min-width="100" prop="discount">
          <template #default="scope">
            <dict-tag :type="DICT_TYPE.PROMOTION_DISCOUNT_TYPE" :value="scope.row.discountType" />
            {{ discountFormat(scope.row) }}
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
        <el-table-column align="center" :label="t('mall.promotion.coupon.totalCount')" prop="totalCount" />
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
            <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
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
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">{{ t('common.ok') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import {
  discountFormat,
  remainedCountFormat,
  takeLimitCountFormat,
  validityTypeFormat
} from '@/views/mall/promotion/coupon/formatter'
import * as CouponTemplateApi from '@/api/mall/promotion/coupon/couponTemplate'
import { CouponTemplateTakeTypeEnum } from '@/utils/constants'

defineOptions({ name: 'CouponSelect' })

const { t } = useI18n() // 国际
const props = defineProps<{
  multipleSelection?: CouponTemplateApi.CouponTemplateVO[]
  takeType: number // 领取方式
}>()
const emit = defineEmits<{
  (e: 'update:multipleSelection', v: CouponTemplateApi.CouponTemplateVO[]): void
  (e: 'change', v: CouponTemplateApi.CouponTemplateVO[]): void
}>()
const dialogVisible = ref(false) // 弹窗的是否展
const dialogTitle = ref(t('mall.promotion.coupon.selectCoupon')) // 弹窗的标
const formLoading = ref(false) // 表单的加载中）修改时的数据加载；2）提交的按钮禁用
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页
const list = ref([]) // 字典表格数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: null,
  discountType: null,
  canTakeTypes: [CouponTemplateTakeTypeEnum.USER.type] // 只获得直接领取的
  })
const queryFormRef = ref() // 搜索的表
const selectedCouponList = ref<CouponTemplateApi.CouponTemplateVO[]>([]) // 选择的数
/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    // 执行查询
    queryParams.canTakeTypes = [props.takeType] as any
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

/** 打开弹窗 */
const open = async () => {
  dialogVisible.value = true
  resetQuery()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const handleSelectionChange = (val: CouponTemplateApi.CouponTemplateVO[]) => {
  if (props.multipleSelection) {
    emit('update:multipleSelection', val)
    return
  }
  selectedCouponList.value = val
}

const submitForm = () => {
  dialogVisible.value = false
  emit('change', selectedCouponList.value)
}
</script>

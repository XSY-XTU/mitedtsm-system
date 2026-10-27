<template>
  <doc-alert title="【营销】优惠劵" url="https://doc.iocoder.cn/mall/promotion-coupon/" />

  <!-- 搜索工作区 -->
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      class="-mb-15px"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('mall.promotion.coupon.nickname')" prop="nickname">
            <el-input
              v-model="queryParams.nickname"
              class="!w-240px"
              :placeholder="t('mall.promotion.coupon.nicknamePlaceholder')"
              clearable
              @keyup="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('mall.promotion.coupon.createTime')" prop="createTime">
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
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button @click="handleQuery"> <Icon icon="ep:search" class="mr-5px" />{{ t('common.query') }} </el-button>
            <el-button @click="resetQuery"> <Icon icon="ep:refresh" class="mr-5px" />{{ t('common.reset') }} </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <!-- Tab 选项：真正的内容（Tab） -->
    <el-tabs v-model="activeTab" type="card" @tab-change="onTabChange">
      <el-tab-pane
        v-for="tab in statusTabs"
        :key="tab.value"
        :label="tab.label"
        :name="tab.value"
      />
    </el-tabs>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list" :table-layout="'auto'">
      <el-table-column :label="t('mall.promotion.coupon.nickname')" align="center" min-width="100" prop="nickname" />
      <el-table-column :label="t('mall.promotion.coupon.name')" align="center" min-width="140" prop="name" />
      <el-table-column :label="t('common.type')" align="center" prop="discountType">
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
      <el-table-column :label="t('mall.promotion.coupon.takeType')" align="center" prop="takeType">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PROMOTION_COUPON_TAKE_TYPE" :value="scope.row.takeType" />
        </template>
      </el-table-column>
      <el-table-column :label="t('common.status')" align="center" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PROMOTION_COUPON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        :label="t('mall.promotion.coupon.createTime')"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        min-width="180"
       fixed="right" />
      <el-table-column
        :label="t('mall.promotion.coupon.useTime')"
        align="center"
        prop="useTime"
        :formatter="dateFormatter"
        min-width="180"
      />
      <el-table-column :label="t('common.operation')" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button
            v-hasPermi="['promotion:coupon:delete']"
            type="danger"
            link
            @click="handleDelete(scope.row.id)"
          >
            {{ t('mall.promotion.coupon.recycle') }}
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
</template>

<script setup lang="ts" name="PromotionCoupon">
import { deleteCoupon, getCouponPage } from '@/api/mall/promotion/coupon/coupon'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import { discountFormat } from '@/views/mall/promotion/coupon/formatter'

defineOptions({ name: 'PromotionCoupon' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 字典表格数据
// 查询参数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  createTime: [],
  status: undefined,
  nickname: undefined
})
const queryFormRef = ref() // 搜索的表单
const activeTab = ref('all') // Tab 筛选
const statusTabs = reactive([
  {
    label: t('mall.promotion.coupon.all'),
    value: 'all'
  }
])

/** 查询列表 */
const getList = async () => {
  loading.value = true
  // 执行查询
  try {
    const data = await getCouponPage(queryParams)
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
  queryFormRef.value?.resetFields()
  handleQuery()
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 二次确认
    await message.confirm(t('mall.promotion.coupon.recycleConfirm'))
    // 发起删除
    await deleteCoupon(id)
    message.notifySuccess(t('mall.promotion.coupon.recycleSuccess'))
    // 重新加载列表
    await getList()
  } catch {}
}

/** tab 切换 */
const onTabChange = (tabName) => {
  queryParams.status = tabName === 'all' ? undefined : tabName
  getList()
}

/** 初始化 **/
onMounted(() => {
  getList()
  // 设置 statuses 过滤
  for (const dict of getIntDictOptions(DICT_TYPE.PROMOTION_COUPON_STATUS)) {
    statusTabs.push({
      label: dict.label,
      value: dict.value as string
    })
  }
})
</script>

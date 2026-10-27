<template>
  <doc-alert title="支付宝支付接入" url="https://doc.iocoder.cn/pay/alipay-pay-demo/" />
  <doc-alert title="支付宝、微信退款接入" url="https://doc.iocoder.cn/pay/refund-demo/" />
  <doc-alert title="微信公众号支付接入" url="https://doc.iocoder.cn/pay/wx-pub-pay-demo/" />
  <doc-alert title="微信小程序支付接入" url="https://doc.iocoder.cn/pay/wx-lite-pay-demo/" />

  <!-- 操作工具栏 -->
  <el-row :gutter="10" class="mb8">
    <el-col :span="1.5">
      <el-button type="primary" plain @click="openForm"><Icon icon="ep:plus" />{{ t('demo.order.createOrder') }}</el-button>
    </el-col>
  </el-row>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :table-layout="'auto'">
      <el-table-column :label="t('demo.order.id')" align="center" prop="id" />
      <el-table-column :label="t('demo.order.userId')" align="center" prop="userId" />
      <el-table-column :label="t('demo.order.spuName')" align="center" prop="spuName" />
      <el-table-column :label="t('demo.order.price')" align="center" prop="price">
        <template #default="scope">
          <span>￥{{ (scope.row.price / 100.0).toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="t('demo.order.refundPrice')" align="center" prop="refundPrice">
        <template #default="scope">
          <span>￥{{ (scope.row.refundPrice / 100.0).toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column
        :label="t('common.createTime')"
        align="center"
        prop="createTime"
        min-width="180"
        :formatter="dateFormatter"
      />
      <el-table-column :label="t('demo.order.payOrderId')" align="center" prop="payOrderId" />
      <el-table-column :label="t('demo.order.payStatus')" align="center" prop="payStatus">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="scope.row.payStatus" />
        </template>
      </el-table-column>
      <el-table-column
        :label="t('demo.order.payTime')"
        align="center"
        prop="payTime"
        min-width="180"
        :formatter="dateFormatter"
      />
      <el-table-column :label="t('demo.order.refundTime')" align="center" prop="refundTime" min-width="180">
        <template #default="scope">
          <span v-if="scope.row.refundTime">{{ formatDate(scope.row.refundTime) }}</span>
          <span v-else-if="scope.row.payRefundId">{{ t('demo.order.refunding') }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="t('common.action')" align="center" class-name="small-padding fixed-width" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="handlePay(scope.row)" v-if="!scope.row.payStatus">
            {{ t('demo.order.goToPay') }}
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleRefund(scope.row)"
            v-if="scope.row.payStatus && !scope.row.payRefundId"
          >
            {{ t('demo.order.initiateRefund') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 对话框(添加 / 修改) -->
  <Dialog :title="t('demo.order.createOrder')" v-model="dialogVisible" width="500px">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="80px"
    >
      <el-form-item :label="t('demo.order.selectProduct')" prop="spuId">
        <el-select
          v-model="formData.spuId"
          :placeholder="t('demo.order.selectProductPlaceholder')"
          clearable
          style="width: 380px"
        >
          <el-option v-for="item in spus" :key="item.id" :label="item.name" :value="item.id">
            <span style="float: left">{{ item.name }}</span>
            <span style="float: right; font-size: 13px; color: #8492a6">
              ￥{{ (item.price / 100.0).toFixed(2) }}
            </span>
          </el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">{{ t('common.ok') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup name="PayDemoOrder">
import * as PayDemoApi from '@/api/pay/demo/order'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import { DICT_TYPE } from '@/utils/dict'

const { t } = useI18n('pay') // 国际化
const router = useRouter() // 路由对象
const message = useMessage() // 消息弹窗

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
// 查询条件
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10
})

const formRef = ref()

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await PayDemoApi.getDemoOrderPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 支付按钮操作 */
const handlePay = (row: any) => {
  router.push({
    name: 'PayCashier',
    query: {
      id: row.payOrderId,
      returnUrl: encodeURIComponent('/pay/demo/order?id=' + row.id)
    }
  })
}

/** 退款按钮操作 */
const handleRefund = async (row: any) => {
  const id = row.id
  try {
    await message.confirm(t('demo.order.refundConfirm', { id }))
    await PayDemoApi.refundDemoOrder(id)
    await getList()
    message.success(t('demo.order.refundSuccess'))
  } catch {}
}

// ========== 弹窗 ==========

// 商品数组
const spus = ref([
  {
    id: 1,
    name: t('demo.order.products.huaweiPhone'),
    price: 1
  },
  {
    id: 2,
    name: t('demo.order.products.xiaomiTV'),
    price: 10
  },
  {
    id: 3,
    name: t('demo.order.products.appleWatch'),
    price: 100
  },
  {
    id: 4,
    name: t('demo.order.products.asusLaptop'),
    price: 1000
  },
  {
    id: 5,
    name: t('demo.order.products.nioCar'),
    price: 200000
  }
])

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const formData = ref<any>({}) // 表单数据
const formRules = {
  spuId: [{ required: true, message: t('demo.order.spuIdRequired'), trigger: 'blur' }]
}

/** 表单重置 */
const reset = () => {
  formData.value = {
    spuId: undefined
  }
  formRef.value?.resetFields()
}

/** 新增按钮操作 */
const openForm = () => {
  reset()
  dialogVisible.value = true
}

/** 提交按钮 */
const submitForm = async () => {
  // 校验表单
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    await PayDemoApi.createDemoOrder(formData.value)
    message.success(t('common.createSuccess'))
    dialogVisible.value = false
  } finally {
    formLoading.value = false
    getList()
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
<template>
  <el-card shadow="never">
    <template #header>
      <CardTitle :title="t('operationData')" />
    </template>
    <div class="flex flex-row flex-wrap items-center gap-8 p-4">
      <div
        v-for="item in data"
        :key="item.name"
        class="h-20 w-20% flex flex-col cursor-pointer items-center justify-center gap-2"
        @click="handleClick(item.routerName)"
      >
        <CountTo
          :decimals="item.decimals"
          :end-val="item.value"
          :prefix="item.prefix"
          class="text-3xl"
        />
        <span class="text-center">{{ item.name }}</span>
      </div>
    </div>
  </el-card>
</template>
<script lang="ts" setup>
import * as ProductSpuApi from '@/api/mall/product/spu'
import * as TradeStatisticsApi from '@/api/mall/statistics/trade'
import * as PayStatisticsApi from '@/api/mall/statistics/pay'
import { CardTitle } from '@/components/Card'
import { useI18n } from '@/hooks/web/useI18n'

/** 运营数据卡片 */
defineOptions({ name: 'OperationDataCard' })

const { t } = useI18n('mall.home')
const router = useRouter() // 路由

/** 数据 */
const data = reactive({
  orderUndelivered: { name: t('pendingOrder'), value: 9, routerName: 'TradeOrder' },
  orderAfterSaleApply: { name: t('refundOrder'), value: 4, routerName: 'TradeAfterSale' },
  orderWaitePickUp: { name: t('pickUpOrder'), value: 0, routerName: 'TradeOrder' },
  productAlertStock: { name: t('stockWarning'), value: 0, routerName: 'ProductSpu' },
  productForSale: { name: t('productForSale'), value: 0, routerName: 'ProductSpu' },
  productInWarehouse: { name: t('productInWarehouse'), value: 0, routerName: 'ProductSpu' },
  withdrawAuditing: { name: t('withdrawAuditing'), value: 0, routerName: 'TradeBrokerageWithdraw' },
  rechargePrice: {
    name: t('rechargePrice'),
    value: 0.0,
    prefix: '￥',
    decimals: 2,
    routerName: 'PayWalletRecharge'
  }
})

/** 查询订单数据 */
const getOrderData = async () => {
  const orderCount = await TradeStatisticsApi.getOrderCount()
  if (orderCount.undelivered != null) {
    data.orderUndelivered.value = orderCount.undelivered
  }
  if (orderCount.afterSaleApply != null) {
    data.orderAfterSaleApply.value = orderCount.afterSaleApply
  }
  if (orderCount.pickUp != null) {
    data.orderWaitePickUp.value = orderCount.pickUp
  }
  if (orderCount.auditingWithdraw != null) {
    data.withdrawAuditing.value = orderCount.auditingWithdraw
  }
}

/** 查询商品数据 */
const getProductData = async () => {
  const productCount = await ProductSpuApi.getTabsCount()
  data.productForSale.value = productCount['0']
  data.productInWarehouse.value = productCount['1']
  data.productAlertStock.value = productCount['3']
}

/** 查询钱包充值数据 */
const getWalletRechargeData = async () => {
  const paySummary = await PayStatisticsApi.getWalletRechargePrice()
  data.rechargePrice.value = paySummary.rechargePrice
}

/**
 * 跳转到对应页面
 *
 * @param routerName 路由页面组件的名称
 */
const handleClick = (routerName: string) => {
  router.push({ name: routerName })
}

/** 激活时 */
onActivated(() => {
  getOrderData()
  getProductData()
  getWalletRechargeData()
})

/** 初始化 **/
onMounted(() => {
  getOrderData()
  getProductData()
  getWalletRechargeData()
})
</script>

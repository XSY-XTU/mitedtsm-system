<template>
  <ContentWrap>
    <el-table :data="business.products" :stripe="true" :show-overflow-tooltip="true" :table-layout="'auto'">
      <el-table-column
        align="center"
        :label="t('crm.product.name')"
        fixed="left"
        prop="productName"
        min-width="160"
      >
        <template #default="scope">
          {{ scope.row.productName }}
        </template>
      </el-table-column>
      <el-table-column :label="t('crm.product.no')" align="center" prop="productNo" min-width="120" />
      <el-table-column align="center" :label="t('crm.product.unit')" prop="productUnit" min-width="160">
        <template #default="{ row }">
          <dict-tag :type="DICT_TYPE.CRM_PRODUCT_UNIT" :value="row.productUnit" />
        </template>
      </el-table-column>
      <el-table-column
        :label="t('crm.product.price') + '（元）'"
        align="center"
        prop="productPrice"
        min-width="140"
        :formatter="erpPriceTableColumnFormatter"
      />
      <el-table-column
        :label="t('crm.business.price') + '（元）'"
        align="center"
        prop="businessPrice"
        min-width="140"
        :formatter="erpPriceTableColumnFormatter"
      />
      <el-table-column
        align="center"
        :label="t('crm.business.count')"
        prop="count"
        min-width="100px"
        :formatter="erpPriceTableColumnFormatter"
      />
      <el-table-column
        :label="t('crm.business.total') + '（元）'"
        align="center"
        prop="totalPrice"
        min-width="140"
        :formatter="erpPriceTableColumnFormatter"
      />
    </el-table>
    <el-row class="mt-10px" justify="end">
      <el-col :span="3"> {{ t('crm.business.discountPercent') }}：{{ erpPriceInputFormatter(business.discountPercent) }}% </el-col>
      <el-col :span="4">
        {{ t('crm.business.totalProductPrice') }}：{{ erpPriceInputFormatter(business.totalProductPrice) }} 元
      </el-col>
    </el-row>
  </ContentWrap>
</template>
<script setup lang="ts">
import * as BusinessApi from '@/api/crm/business'
import { erpPriceInputFormatter, erpPriceTableColumnFormatter } from '@/utils'
import { DICT_TYPE } from '@/utils/dict'

const { t } = useI18n() // 国际化
const { business } = defineProps<{
  business: BusinessApi.BusinessVO
}>()
</script>
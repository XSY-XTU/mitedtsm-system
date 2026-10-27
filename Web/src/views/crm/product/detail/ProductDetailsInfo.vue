<template>
  <ContentWrap>
    <el-collapse v-model="activeNames">
      <el-collapse-item name="basicInfo">
        <template #title>
          <span class="text-base font-bold">{{ t('basicInfoTab') }}</span>
        </template>
        <el-descriptions :column="4">
          <el-descriptions-item :label="t('name')">{{ product.name }}</el-descriptions-item>
          <el-descriptions-item :label="t('no')">{{ product.no }}</el-descriptions-item>
          <el-descriptions-item :label="t('price')">
            {{ erpPriceInputFormatter(product.price) }} 元
          </el-descriptions-item>
          <el-descriptions-item :label="t('description')">{{ product.description }}</el-descriptions-item>
          <el-descriptions-item :label="t('categoryName')">{{ product.categoryName }}</el-descriptions-item>
          <el-descriptions-item :label="t('status')">
            <dict-tag :type="DICT_TYPE.CRM_PRODUCT_STATUS" :value="product.status" />
          </el-descriptions-item>
          <el-descriptions-item :label="t('unit')">
            <dict-tag :type="DICT_TYPE.CRM_PRODUCT_UNIT" :value="product.unit" />
          </el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
    </el-collapse>
  </ContentWrap>
</template>
<script setup lang="ts">
import { DICT_TYPE } from '@/utils/dict'
import * as ProductApi from '@/api/crm/product'
import { erpPriceInputFormatter } from '@/utils'

const { t } = useI18n('crm.product') // 国际化

const { product } = defineProps<{
  product: ProductApi.ProductVO
}>()

// 展示的折叠面板
const activeNames = ref(['basicInfo'])
</script>

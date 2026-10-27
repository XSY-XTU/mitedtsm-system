<template>
  <el-table :data="formData" :show-overflow-tooltip="true" :stripe="true" height="120" :table-layout="'auto'">
    <el-table-column :label="t('business.name')" fixed="left" align="center" prop="name" />
    <el-table-column
      :label="t('business.price')"
      align="center"
      prop="totalPrice"
      :formatter="erpPriceTableColumnFormatter"
    />
    <el-table-column :label="t('business.customerName')" align="center" prop="customerName" />
    <el-table-column :label="t('business.statusTypeName')" align="center" prop="statusTypeName" />
    <el-table-column :label="t('business.statusName')" align="center" prop="statusName" />
    <el-table-column align="center" fixed="right" :label="t('common.action')" min-width="150">
      <template #default="{ $index }">
        <el-button link type="danger" @click="handleDelete($index)"> {{ t('common.remove') }}</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script lang="ts" setup>
import { erpPriceTableColumnFormatter } from '@/utils'

const { t } = useI18n('crm') // 国际化

const props = defineProps<{
  businesses: undefined
}>()
const formData = ref([])

/** 初始化商机列表 */
watch(
  () => props.businesses,
  async (val) => {
    formData.value = val
  },
  { immediate: true }
)

/** 删除按钮操作 */
const handleDelete = (index: number) => {
  formData.value.splice(index, 1)
}
</script>
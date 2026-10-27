<template>
  <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true" :table-layout="'auto'">
    <el-table-column :label="t('address.id')" align="center" prop="id" min-width="150" />
    <el-table-column :label="t('address.receiverName')" align="center" prop="name" min-width="150" />
    <el-table-column :label="t('address.mobile')" align="center" prop="mobile" min-width="150" />
    <el-table-column :label="t('address.areaId')" align="center" prop="areaId" min-width="150" />
    <el-table-column :label="t('address.detailAddress')" align="center" prop="detailAddress" />
    <el-table-column :label="t('address.isDefault')" align="center" prop="defaultStatus" min-width="150">
      <template #default="scope">
        <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="Number(scope.row.defaultStatus)" />
      </template>
    </el-table-column>
    <el-table-column
      :label="t('common.createTime')"
      align="center"
      prop="createTime"
      :formatter="dateFormatter"
      min-width="180"
    />
  </el-table>
</template>
<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as AddressApi from '@/api/member/address'
import { useI18n } from '@/hooks/web/useI18n'

const { t } = useI18n('member')

const { userId }: { userId: number } = defineProps({
  userId: {
    type: Number,
    required: true
  }
})

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    list.value = await AddressApi.getAddressList({ userId })
  } finally {
    loading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

<style scoped lang="scss"></style>
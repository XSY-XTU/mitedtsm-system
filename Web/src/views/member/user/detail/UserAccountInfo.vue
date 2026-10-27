<template>
  <el-descriptions :class="{ 'kefu-descriptions': column === 1 }" :column="column">
    <el-descriptions-item>
      <template #label>
        <descriptions-item-label icon="svg-icon:member_level" :label="t('detail.currentLevel')" />
      </template>
      {{ user.levelName || t('detail.empty') }}
    </el-descriptions-item>
    <el-descriptions-item>
      <template #label>
        <descriptions-item-label icon="ep:suitcase" :label="t('detail.growthValue')" />
      </template>
      {{ user.experience || 0 }}
    </el-descriptions-item>
    <el-descriptions-item>
      <template #label>
        <descriptions-item-label icon="ep:coin" :label="t('detail.currentPoint')" />
      </template>
      {{ user.point || 0 }}
    </el-descriptions-item>
    <el-descriptions-item>
      <template #label>
        <descriptions-item-label icon="ep:coin" :label="t('detail.totalPointLabel')" />
      </template>
      {{ user.totalPoint || 0 }}
    </el-descriptions-item>
    <el-descriptions-item>
      <template #label>
        <descriptions-item-label icon="svg-icon:member_balance" :label="t('detail.currentBalance')" />
      </template>
      {{ fenToYuan(wallet.balance || 0) }}
    </el-descriptions-item>
    <el-descriptions-item>
      <template #label>
        <descriptions-item-label icon="svg-icon:member_expenditure_balance" :label="t('detail.expenditureAmount')" />
      </template>
      {{ fenToYuan(wallet.totalExpense || 0) }}
    </el-descriptions-item>
    <el-descriptions-item>
      <template #label>
        <descriptions-item-label icon="svg-icon:member_recharge_balance" :label="t('detail.rechargeAmount')" />
      </template>
      {{ fenToYuan(wallet.totalRecharge || 0) }}
    </el-descriptions-item>
  </el-descriptions>
</template>
<script lang="ts" setup>
import { DescriptionsItemLabel } from '@/components/Descriptions'
import * as UserApi from '@/api/member/user'
import * as WalletApi from '@/api/pay/wallet/balance'
import { fenToYuan } from '@/utils'
import { useI18n } from '@/hooks/web/useI18n'

const { t } = useI18n('member')

withDefaults(defineProps<{ user: UserApi.UserVO; wallet: WalletApi.WalletVO; column?: number }>(), {
  column: 2
}) // 用户信息
</script>
<style lang="scss" scoped>
.cell-item {
  display: inline;
}

.cell-item::after {
  content: ':';
}

.kefu-descriptions {
  ::v-deep(.el-descriptions__cell) {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .el-descriptions__label {
      width: 120px;
      display: block;
      text-align: left;
    }

    .el-descriptions__content {
      flex: 1;
      text-align: end;
    }
  }
}
</style>
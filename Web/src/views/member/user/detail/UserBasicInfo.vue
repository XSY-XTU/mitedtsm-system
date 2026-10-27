<template>
  <el-card shadow="never">
    <template #header>
      <slot name="header"></slot>
    </template>
    <el-row v-if="mode === 'member'">
      <el-col :span="4">
        <ElAvatar :size="140" :src="user.avatar || undefined" shape="square" />
      </el-col>
      <el-col :span="20">
        <el-descriptions :column="2">
          <el-descriptions-item>
            <template #label>
              <descriptions-item-label icon="ep:user" :label="t('detail.username')" />
            </template>
            {{ user.name || t('detail.empty') }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <descriptions-item-label icon="ep:user" :label="t('user.nickname')" />
            </template>
            {{ user.nickname }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('user.mobile')">
            <template #label>
              <descriptions-item-label icon="ep:phone" :label="t('user.mobile')" />
            </template>
            {{ user.mobile }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <descriptions-item-label icon="fa:mars-double" :label="t('user.gender')" />
            </template>
            <dict-tag :type="DICT_TYPE.SYSTEM_USER_SEX" :value="user.sex" />
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <descriptions-item-label icon="ep:location" :label="t('user.area')" />
            </template>
            {{ user.areaName }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <descriptions-item-label icon="ep:position" :label="t('user.registerIp')" />
            </template>
            {{ user.registerIp }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <descriptions-item-label icon="fa:birthday-cake" :label="t('user.birthday')" />
            </template>
            {{ user.birthday ? formatDate(user.birthday as any) : t('detail.empty') }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <descriptions-item-label icon="ep:calendar" :label="t('user.registerTime')" />
            </template>
            {{ user.createTime ? formatDate(user.createTime as any) : t('detail.empty') }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <descriptions-item-label icon="ep:calendar" :label="t('user.loginTime')" />
            </template>
            {{ user.loginDate ? formatDate(user.loginDate as any) : t('detail.empty') }}
          </el-descriptions-item>
        </el-descriptions>
      </el-col>
    </el-row>
    <template v-if="mode === 'kefu'">
      <ElAvatar :size="140" :src="user.avatar || undefined" shape="square" />
      <el-descriptions :column="1" class="kefu-descriptions">
        <el-descriptions-item>
          <template #label>
            <descriptions-item-label icon="ep:user" :label="t('detail.username')" />
          </template>
          {{ user.name || t('detail.empty') }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <descriptions-item-label icon="ep:user" :label="t('user.nickname')" />
          </template>
          {{ user.nickname }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <descriptions-item-label icon="ep:phone" :label="t('user.mobile')" />
          </template>
          {{ user.mobile }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <descriptions-item-label icon="fa:mars-double" :label="t('user.gender')" />
          </template>
          <dict-tag :type="DICT_TYPE.SYSTEM_USER_SEX" :value="user.sex" />
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <descriptions-item-label icon="ep:location" :label="t('user.area')" />
          </template>
          {{ user.areaName }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <descriptions-item-label icon="ep:position" :label="t('user.registerIp')" />
          </template>
          {{ user.registerIp }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <descriptions-item-label icon="fa:birthday-cake" :label="t('user.birthday')" />
          </template>
          {{ user.birthday ? formatDate(user.birthday as any) : t('detail.empty') }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <descriptions-item-label icon="ep:calendar" :label="t('user.registerTime')" />
          </template>
          {{ user.createTime ? formatDate(user.createTime as any) : t('detail.empty') }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <descriptions-item-label icon="ep:calendar" :label="t('user.loginTime')" />
          </template>
          {{ user.loginDate ? formatDate(user.loginDate as any) : t('detail.empty') }}
        </el-descriptions-item>
      </el-descriptions>
    </template>
  </el-card>
</template>
<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import * as UserApi from '@/api/member/user'
import { DescriptionsItemLabel } from '@/components/Descriptions/index'
import { useI18n } from '@/hooks/web/useI18n'

const { t } = useI18n('member')

withDefaults(defineProps<{ user: UserApi.UserVO; mode?: string }>(), {
  mode: 'member'
})
</script>
<style lang="scss" scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

::v-deep(.kefu-descriptions) {
  .el-descriptions__cell {
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
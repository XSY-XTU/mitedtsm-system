<template>
  <doc-alert title="会员手册（功能开启）" url="https://doc.iocoder.cn/member/build/" />

  <ContentWrap>
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="auto"
      v-loading="formLoading"
    >
      <el-form-item label="hideId" v-show="false">
        <el-input v-model="formData.id" />
      </el-form-item>

      <el-tabs>
        <el-tab-pane :label="t('config.point')">
          <el-form-item :label="t('config.pointDeductEnable')" prop="pointTradeDeductEnable">
            <el-switch v-model="formData.pointTradeDeductEnable" style="user-select: none" />
            <el-text class="w-full" size="small" type="info">{{ t('config.pointDeductEnableTip') }}</el-text>
          </el-form-item>
          <el-form-item :label="t('config.pointDeductUnitPrice')" prop="pointTradeDeductUnitPrice">
            <el-input-number
              v-model="computedPointTradeDeductUnitPrice"
              :placeholder="t('config.pointDeductUnitPricePlaceholder')"
              :precision="2"
            />
            <el-text class="w-full" size="small" type="info">
              {{ t('config.pointDeductUnitPriceTip') }}
            </el-text>
          </el-form-item>
          <el-form-item :label="t('config.pointDeductMaxPrice')" prop="pointTradeDeductMaxPrice">
            <el-input-number
              v-model="formData.pointTradeDeductMaxPrice"
              :placeholder="t('config.pointDeductMaxPricePlaceholder')"
            />
            <el-text class="w-full" size="small" type="info">
              {{ t('config.pointDeductMaxPriceTip') }}
            </el-text>
          </el-form-item>
          <el-form-item :label="t('config.pointGivePoint')" prop="pointTradeGivePoint">
            <el-input-number
              v-model="formData.pointTradeGivePoint"
              :placeholder="t('config.pointGivePointPlaceholder')"
            />
            <el-text class="w-full" size="small" type="info">
              {{ t('config.pointGivePointTip') }}
            </el-text>
          </el-form-item>
        </el-tab-pane>
      </el-tabs>

      <el-form-item>
        <el-button type="primary" @click="onSubmit">{{ t('common.save') }}</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>
</template>
<script lang="ts" setup>
import * as ConfigApi from '@/api/member/config'
import { useI18n } from '@/hooks/web/useI18n'

defineOptions({ name: 'MemberConfig' })

const { t } = useI18n('member') // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formData = ref({
  id: undefined,
  pointTradeDeductEnable: true,
  pointTradeDeductUnitPrice: 0,
  pointTradeDeductMaxPrice: 0,
  pointTradeGivePoint: 0
})

// 创建一个计算属性，用于将 pointTradeDeductUnitPrice 显示为带两位小数的形式
const computedPointTradeDeductUnitPrice = computed({
  get: () => (formData.value.pointTradeDeductUnitPrice / 100).toFixed(2),
  set: (newValue: number) => {
    formData.value.pointTradeDeductUnitPrice = Math.round(newValue * 100)
  }
})

const formRules = reactive({})
const formRef = ref() // 表单 Ref

/** 修改积分配置 */
const onSubmit = async () => {
  // 校验表单
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as ConfigApi.ConfigVO
    await ConfigApi.saveConfig(data)
    message.success(t('common.updateSuccess'))
    dialogVisible.value = false
  } finally {
    formLoading.value = false
  }
}

/** 获得积分配置 */
const getConfig = async () => {
  try {
    const data = await ConfigApi.getConfig()
    if (data === null) {
      return
    }
    formData.value = data
  } finally {
  }
}

onMounted(() => {
  getConfig()
})
</script>

<style scoped>
:deep(.el-form-item__label) {
  white-space: nowrap;
}
</style>

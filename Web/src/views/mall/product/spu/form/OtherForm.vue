<!-- 商品发布 - 其它设置 -->
<template>
  <el-form ref="formRef" :model="formData" :rules="rules" label-width="auto" :disabled="isDetail">
    <el-row>
      <el-col :span="12">
        <el-form-item :label="t('other.productSort')" prop="sort">
          <el-input-number
            v-model="formData.sort"
            :min="0"
            :placeholder="t('other.sortPlaceholder')"
            class="w-80!"
          />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item :label="t('spu.giveIntegral')" prop="giveIntegral">
          <el-input-number
            v-model="formData.giveIntegral"
            :min="0"
            :placeholder="t('other.giveIntegralPlaceholder')"
            class="w-80!"
          />
        </el-form-item>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="12">
        <el-form-item :label="t('spu.virtualSalesCount')" prop="virtualSalesCount">
          <el-input-number
            v-model="formData.virtualSalesCount"
            :min="0"
            :placeholder="t('other.virtualSalesCountPlaceholder')"
            class="w-80!"
          />
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>
<script lang="ts" setup>
import type { Spu } from '@/api/mall/product/spu'
import { PropType } from 'vue'
import { propTypes } from '@/utils/propTypes'
import { copyValueToTarget } from '@/utils'

defineOptions({ name: 'ProductOtherForm' })

const { t } = useI18n('mall.product') // 国际化
const message = useMessage() // 消息弹窗

const props = defineProps({
  propFormData: {
    type: Object as PropType<Spu>,
    default: () => {}
  },
  isDetail: propTypes.bool.def(false) // 是否作为详情组件
})

const formRef = ref() // 表单Ref
// 表单数据
const formData = ref<Spu>({
  sort: 0, // 商品排序
  giveIntegral: 0, // 赠送积分
  virtualSalesCount: 0 // 虚拟销量
})
// 表单规则
const rules = reactive({
  sort: [required],
  giveIntegral: [required],
  virtualSalesCount: [required]
})

/** 将传进来的值赋值给 formData */
watch(
  () => props.propFormData,
  (data) => {
    if (!data) {
      return
    }
    copyValueToTarget(formData.value, data)
  },
  {
    immediate: true
  }
)

/** 表单校验 */
const emit = defineEmits(['update:activeName'])
const validate = async () => {
  if (!formRef) return
  try {
    await unref(formRef)?.validate()
    // 校验通过更新数据
    Object.assign(props.propFormData, formData.value)
  } catch (e) {
    message.error(t('other.otherFormIncomplete'))
    emit('update:activeName', 'other')
    throw e // 目的截断之后的校验
  }
}
defineExpose({ validate })
</script>

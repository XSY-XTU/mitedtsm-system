<template>
  <ComponentContainerProperty v-model="formData.style">
    <!-- 表单 -->
    <el-form label-width="80px" :model="formData" class="m-t-8px">
      <el-card :header="t('mall.promotion.diy.searchBarProperty.hotKeywords')" class="property-group" shadow="never">
        <Draggable v-model="formData.hotKeywords" :empty-item="''" :min="0">
          <template #default="{ index }">
            <el-input v-model="formData.hotKeywords[index]" :placeholder="t('mall.promotion.diy.searchBarProperty.hotKeywordsPlaceholder')" />
          </template>
        </Draggable>
      </el-card>
      <el-card :header="t('mall.promotion.diy.searchBarProperty.searchStyle')" class="property-group" shadow="never">
        <el-form-item :label="t('mall.promotion.diy.searchBarProperty.frameStyle')">
          <el-radio-group v-model="formData!.borderRadius">
            <el-tooltip :content="t('mall.promotion.diy.searchBarProperty.frameSquare')" placement="top">
              <el-radio-button :value="0">
                <Icon icon="tabler:input-search" />
              </el-radio-button>
            </el-tooltip>
            <el-tooltip :content="t('mall.promotion.diy.searchBarProperty.frameRound')" placement="top">
              <el-radio-button :value="10">
                <Icon icon="iconoir:input-search" />
              </el-radio-button>
            </el-tooltip>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="t('mall.promotion.diy.searchBarProperty.placeholder')" prop="placeholder">
          <el-input v-model="formData.placeholder" />
        </el-form-item>
        <el-form-item :label="t('mall.promotion.diy.searchBarProperty.textPosition')" prop="placeholderPosition">
          <el-radio-group v-model="formData!.placeholderPosition">
            <el-tooltip :content="t('mall.promotion.diy.searchBarProperty.textLeft')" placement="top">
              <el-radio-button value="left">
                <Icon icon="ant-design:align-left-outlined" />
              </el-radio-button>
            </el-tooltip>
            <el-tooltip :content="t('mall.promotion.diy.searchBarProperty.textCenter')" placement="top">
              <el-radio-button value="center">
                <Icon icon="ant-design:align-center-outlined" />
              </el-radio-button>
            </el-tooltip>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="t('mall.promotion.diy.searchBarProperty.showScan')" prop="showScan">
          <el-switch v-model="formData!.showScan" />
        </el-form-item>
        <el-form-item :label="t('mall.promotion.diy.searchBarProperty.frameHeight')" prop="height">
          <el-slider v-model="formData!.height" :max="50" :min="28" show-input input-size="small" />
        </el-form-item>
        <el-form-item :label="t('mall.promotion.diy.searchBarProperty.frameColor')" prop="backgroundColor">
          <ColorInput v-model="formData.backgroundColor" />
        </el-form-item>
        <el-form-item class="lef" :label="t('mall.promotion.diy.searchBarProperty.textColor')" prop="textColor">
          <ColorInput v-model="formData.textColor" />
        </el-form-item>
      </el-card>
    </el-form>
  </ComponentContainerProperty>
</template>

<script setup lang="ts">
import { useI18n } from '@/hooks/web/useI18n'
import { useVModel } from '@vueuse/core'
import { SearchProperty } from '@/components/DiyEditor/components/mobile/SearchBar/config'
import { isString } from '@/utils/is'

/** 搜索框属性面板 */
defineOptions({ name: 'SearchProperty' })

const { t } = useI18n('mall.promotion.diy')

const props = defineProps<{ modelValue: SearchProperty }>()
const emit = defineEmits(['update:modelValue'])
const formData = useVModel(props, 'modelValue', emit)

// 监听热词数组变化
watch(
  () => formData.value.hotKeywords,
  (newVal) => {
    // 找到非字符串项的索引
    const nonStringIndex = newVal.findIndex((item) => !isString(item))
    if (nonStringIndex !== -1) {
      formData.value.hotKeywords[nonStringIndex] = ''
    }
  },
  { deep: true, flush: 'post' }
)
</script>

<style scoped lang="scss"></style>

<template>
  <ComponentContainerProperty v-model="formData.style">
    <el-form label-width="85px" :model="formData" :rules="rules">
      <el-card :header="t('mall.promotion.diy.titleBarProperty.style')" class="property-group" shadow="never">
        <el-form-item :label="t('mall.promotion.diy.titleBarProperty.bgImage')" prop="bgImgUrl">
          <UploadImg v-model="formData.bgImgUrl" width="100%" height="40px">
            <template #tip>{{ t('mall.promotion.diy.titleBarProperty.imageSizeTip') }}</template>
          </UploadImg>
        </el-form-item>
        <el-form-item :label="t('mall.promotion.diy.titleBarProperty.titlePosition')" prop="textAlign">
          <el-radio-group v-model="formData!.textAlign">
            <el-tooltip :content="t('mall.promotion.diy.titleBarProperty.textLeft')" placement="top">
              <el-radio-button value="left">
                <Icon icon="ant-design:align-left-outlined" />
              </el-radio-button>
            </el-tooltip>
            <el-tooltip :content="t('mall.promotion.diy.titleBarProperty.textCenter')" placement="top">
              <el-radio-button value="center">
                <Icon icon="ant-design:align-center-outlined" />
              </el-radio-button>
            </el-tooltip>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="t('mall.promotion.diy.titleBarProperty.offset')" prop="marginLeft" label-width="70px">
          <el-slider
            v-model="formData.marginLeft"
            :max="100"
            :min="0"
            show-input
            input-size="small"
          />
        </el-form-item>
        <el-form-item :label="t('mall.promotion.diy.titleBarProperty.height')" prop="height" label-width="70px">
          <el-slider
            v-model="formData.height"
            :max="200"
            :min="20"
            show-input
            input-size="small"
          />
        </el-form-item>
      </el-card>
      <el-card :header="t('mall.promotion.diy.titleBarProperty.mainTitle')" class="property-group" shadow="never">
        <el-form-item :label="t('mall.promotion.diy.titleBarProperty.text')" prop="title" label-width="40px">
          <InputWithColor
            v-model="formData.title"
            v-model:color="formData.titleColor"
            show-word-limit
            maxlength="20"
          />
        </el-form-item>
        <el-form-item :label="t('mall.promotion.diy.titleBarProperty.size')" prop="titleSize" label-width="40px">
          <el-slider
            v-model="formData.titleSize"
            :max="60"
            :min="10"
            show-input
            input-size="small"
          />
        </el-form-item>
        <el-form-item :label="t('mall.promotion.diy.titleBarProperty.weight')" prop="titleWeight" label-width="40px">
          <el-slider
            v-model="formData.titleWeight"
            :min="100"
            :max="900"
            :step="100"
            show-input
            input-size="small"
          />
        </el-form-item>
      </el-card>
      <el-card :header="t('mall.promotion.diy.titleBarProperty.subtitle')" class="property-group" shadow="never">
        <el-form-item :label="t('mall.promotion.diy.titleBarProperty.text')" prop="description" label-width="40px">
          <InputWithColor
            v-model="formData.description"
            v-model:color="formData.descriptionColor"
            show-word-limit
            maxlength="50"
          />
        </el-form-item>
        <el-form-item :label="t('mall.promotion.diy.titleBarProperty.size')" prop="descriptionSize" label-width="40px">
          <el-slider
            v-model="formData.descriptionSize"
            :max="60"
            :min="10"
            show-input
            input-size="small"
          />
        </el-form-item>
        <el-form-item :label="t('mall.promotion.diy.titleBarProperty.weight')" prop="descriptionWeight" label-width="40px">
          <el-slider
            v-model="formData.descriptionWeight"
            :min="100"
            :max="900"
            :step="100"
            show-input
            input-size="small"
          />
        </el-form-item>
      </el-card>
      <el-card :header="t('mall.promotion.diy.titleBarProperty.viewMore')" class="property-group" shadow="never">
        <el-form-item :label="t('mall.promotion.diy.titleBarProperty.show')" prop="more.show">
          <el-checkbox v-model="formData.more.show" />
        </el-form-item>
        <!-- 更多按钮的 样式选择 -->
        <template v-if="formData.more.show">
          <el-form-item :label="t('mall.promotion.diy.titleBarProperty.moreStyle')" prop="more.type">
            <el-radio-group v-model="formData.more.type">
              <el-radio value="text">{{ t('mall.promotion.diy.titleBarProperty.moreStyleText') }}</el-radio>
              <el-radio value="icon">{{ t('mall.promotion.diy.titleBarProperty.moreStyleIcon') }}</el-radio>
              <el-radio value="all">{{ t('mall.promotion.diy.titleBarProperty.moreStyleAll') }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item :label="t('mall.promotion.diy.titleBarProperty.moreText')" prop="more.text" v-show="formData.more.type !== 'icon'">
            <el-input v-model="formData.more.text" />
          </el-form-item>
          <el-form-item :label="t('mall.promotion.diy.titleBarProperty.jumpLink')" prop="more.url">
            <AppLinkInput v-model="formData.more.url" />
          </el-form-item>
        </template>
      </el-card>
    </el-form>
  </ComponentContainerProperty>
</template>
<script setup lang="ts">
import { useI18n } from '@/hooks/web/useI18n'
import { TitleBarProperty } from './config'
import { useVModel } from '@vueuse/core'
// 导航栏属性面板
defineOptions({ name: 'TitleBarProperty' })

const { t } = useI18n('mall.promotion.diy')

const props = defineProps<{ modelValue: TitleBarProperty }>()
const emit = defineEmits(['update:modelValue'])
const formData = useVModel(props, 'modelValue', emit)

// 表单校验
const rules = {}
</script>

<style scoped lang="scss"></style>

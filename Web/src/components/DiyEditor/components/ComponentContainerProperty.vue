<template>
  <el-tabs stretch>
    <!-- 每个组件的自定义内容 -->
    <el-tab-pane :label="t('editor.property.content')" v-if="$slots.default">
      <slot></slot>
    </el-tab-pane>

    <!-- 每个组件的通用内容 -->
    <el-tab-pane :label="t('editor.property.style')" lazy>
      <el-card :header="t('editor.property.componentStyle')" class="property-group">
        <el-form :model="formData" label-width="80px">
          <el-form-item :label="t('editor.property.componentBackground')" prop="bgType">
            <el-radio-group v-model="formData.bgType">
              <el-radio value="color">{{ t('editor.property.bgColor') }}</el-radio>
              <el-radio value="img">{{ t('editor.property.bgImg') }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item :label="t('editor.property.selectColor')" prop="bgColor" v-if="formData.bgType === 'color'">
            <ColorInput v-model="formData.bgColor" />
          </el-form-item>
          <el-form-item :label="t('editor.property.uploadImage')" prop="bgImg" v-else>
            <UploadImg v-model="formData.bgImg" :limit="1">
              <template #tip>{{ t('editor.property.imageWidthTip') }}</template>
            </UploadImg>
          </el-form-item>
          <el-tree :data="treeData" :expand-on-click-node="false" default-expand-all>
            <template #default="{ node, data }">
              <el-form-item
                :label="data.label"
                :prop="data.prop"
                :label-width="node.level === 1 ? '80px' : '62px'"
                class="w-full m-b-0!"
              >
                <el-slider
                  v-model="formData[data.prop]"
                  :max="100"
                  :min="0"
                  show-input
                  input-size="small"
                  :show-input-controls="false"
                  @input="handleSliderChange(data.prop)"
                />
              </el-form-item>
            </template>
          </el-tree>
          <slot name="style" :style="formData"></slot>
        </el-form>
      </el-card>
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
import { ComponentStyle } from '@/components/DiyEditor/util'
import { useVModel } from '@vueuse/core'
import { useI18n } from '@/hooks/web/useI18n'

/**
 * 组件容器属性：目前右边部分
 * 用于包裹组件，为组件提供 背景、外边距、内边距、边框等样式
 */
defineOptions({ name: 'ComponentContainer' })

const { t } = useI18n('mall.promotion.diy')

const props = defineProps<{ modelValue: ComponentStyle }>()
const emit = defineEmits(['update:modelValue'])
const formData = useVModel(props, 'modelValue', emit)

const treeData = computed(() => [
  {
    label: t('editor.property.outerMargin'),
    prop: 'margin',
    children: [
      {
        label: t('editor.property.top'),
        prop: 'marginTop'
      },
      {
        label: t('editor.property.right'),
        prop: 'marginRight'
      },
      {
        label: t('editor.property.bottom'),
        prop: 'marginBottom'
      },
      {
        label: t('editor.property.left'),
        prop: 'marginLeft'
      }
    ]
  },
  {
    label: t('editor.property.innerPadding'),
    prop: 'padding',
    children: [
      {
        label: t('editor.property.top'),
        prop: 'paddingTop'
      },
      {
        label: t('editor.property.right'),
        prop: 'paddingRight'
      },
      {
        label: t('editor.property.bottom'),
        prop: 'paddingBottom'
      },
      {
        label: t('editor.property.left'),
        prop: 'paddingLeft'
      }
    ]
  },
  {
    label: t('editor.property.borderRadius'),
    prop: 'borderRadius',
    children: [
      {
        label: t('editor.property.topLeft'),
        prop: 'borderTopLeftRadius'
      },
      {
        label: t('editor.property.topRight'),
        prop: 'borderTopRightRadius'
      },
      {
        label: t('editor.property.bottomRight'),
        prop: 'borderBottomRightRadius'
      },
      {
        label: t('editor.property.bottomLeft'),
        prop: 'borderBottomLeftRadius'
      }
    ]
  }
])

const handleSliderChange = (prop: string) => {
  switch (prop) {
    case 'margin':
      formData.value.marginTop = formData.value.margin
      formData.value.marginRight = formData.value.margin
      formData.value.marginBottom = formData.value.margin
      formData.value.marginLeft = formData.value.margin
      break
    case 'padding':
      formData.value.paddingTop = formData.value.padding
      formData.value.paddingRight = formData.value.padding
      formData.value.paddingBottom = formData.value.padding
      formData.value.paddingLeft = formData.value.padding
      break
    case 'borderRadius':
      formData.value.borderTopLeftRadius = formData.value.borderRadius
      formData.value.borderTopRightRadius = formData.value.borderRadius
      formData.value.borderBottomRightRadius = formData.value.borderRadius
      formData.value.borderBottomLeftRadius = formData.value.borderRadius
      break
  }
}
</script>

<style scoped lang="scss">
:deep(.el-slider__runway) {
  margin-right: 16px;
}

:deep(.el-input-number) {
  width: 50px;
}
</style>

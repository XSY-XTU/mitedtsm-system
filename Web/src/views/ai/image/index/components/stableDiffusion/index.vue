<!-- stableDiffusion -->
<template>
  <div class="prompt">
    <el-text tag="b">{{ t('image.prompt') }}</el-text>
    <el-text tag="p">{{ t('image.promptHint') }}</el-text>
    <el-input
      v-model="prompt"
      maxlength="1024"
      :rows="5"
      class="w-100% mt-15px"
      input-style="border-radius: 7px;"
      :placeholder="t('image.promptPlaceholder')"
      show-word-limit
      type="textarea"
    />
  </div>
  <div class="flex flex-col mt-30px">
    <div>
      <el-text tag="b">{{ t('image.randomHotWords') }}</el-text>
    </div>
    <el-space wrap class="flex flex-row flex-wrap justify-start mt-15px">
      <el-button
        round
        class="m-0"
        :type="selectHotWord === hotWord ? 'primary' : 'default'"
        v-for="hotWord in ImageHotEnglishWords"
        :key="hotWord"
        @click="handleHotWordClick(hotWord)"
      >
        {{ hotWord }}
      </el-button>
    </el-space>
  </div>
  <div class="mt-30px">
    <div>
      <el-text tag="b">{{ t('image.samplingMethod') }}</el-text>
    </div>
    <el-space wrap class="mt-15px w-full">
      <el-select v-model="sampler" placeholder="Select" size="large" class="!w-350px">
        <el-option
          v-for="item in StableDiffusionSamplers"
          :key="item.key"
          :label="item.name"
          :value="item.key"
        />
      </el-select>
    </el-space>
  </div>
  <div class="mt-30px">
    <div>
      <el-text tag="b">{{ t('image.clip') }}</el-text>
    </div>
    <el-space wrap class="mt-15px w-full">
      <el-select v-model="clipGuidancePreset" placeholder="Select" size="large" class="!w-350px">
        <el-option
          v-for="item in StableDiffusionClipGuidancePresets"
          :key="item.key"
          :label="item.name"
          :value="item.key"
        />
      </el-select>
    </el-space>
  </div>
  <div class="mt-30px">
    <div>
      <el-text tag="b">{{ t('image.style') }}</el-text>
    </div>
    <el-space wrap class="mt-15px w-full">
      <el-select v-model="stylePreset" placeholder="Select" size="large" class="!w-350px">
        <el-option
          v-for="item in StableDiffusionStylePresets"
          :key="item.key"
          :label="item.name"
          :value="item.key"
        />
      </el-select>
    </el-space>
  </div>
  <div class="mt-30px">
    <div>
      <el-text tag="b">{{ t('image.imageSize') }}</el-text>
    </div>
    <el-space wrap class="mt-15px w-full">
      <el-input v-model="width" class="w-170px" :placeholder="t('image.imageWidth')" />
      <el-input v-model="height" class="w-170px" :placeholder="t('image.imageHeight')" />
    </el-space>
  </div>
  <div class="mt-30px">
    <div>
      <el-text tag="b">{{ t('image.iterationSteps') }}</el-text>
    </div>
    <el-space wrap class="mt-15px w-full">
      <el-input
        v-model="steps"
        type="number"
        size="large"
        class="!w-350px"
        placeholder="Please input"
      />
    </el-space>
  </div>
  <div class="mt-30px">
    <div>
      <el-text tag="b">{{ t('image.guidanceScale') }}</el-text>
    </div>
    <el-space wrap class="mt-15px w-full">
      <el-input
        v-model="scale"
        type="number"
        size="large"
        class="!w-350px"
        placeholder="Please input"
      />
    </el-space>
  </div>
  <div class="mt-30px">
    <div>
      <el-text tag="b">{{ t('image.seed') }}</el-text>
    </div>
    <el-space wrap class="mt-15px w-full">
      <el-input
        v-model="seed"
        type="number"
        size="large"
        class="!w-350px"
        placeholder="Please input"
      />
    </el-space>
  </div>
  <div class="flex justify-center mt-50px">
    <el-button
      type="primary"
      size="large"
      round
      :loading="drawIn"
      :disabled="prompt.length === 0"
      @click="handleGenerateImage"
    >
      {{ drawIn ? t('image.generating') : t('image.generateContent') }}
    </el-button>
  </div>
</template>
<script setup lang="ts">
import { ImageApi, ImageDrawReqVO, ImageVO } from '@/api/ai/image'
import { hasChinese } from '@/views/ai/utils/utils'
import {
  AiPlatformEnum,
  ImageHotEnglishWords,
  StableDiffusionClipGuidancePresets,
  StableDiffusionSamplers,
  StableDiffusionStylePresets
} from '@/views/ai/utils/constants'
import { ModelVO } from '@/api/ai/model/model'

const { t } = useI18n('ai') // 国际化
const message = useMessage() // 消息弹窗

// 接收父组件传入的模型列表
const props = defineProps({
  models: {
    type: Array<ModelVO>,
    default: () => [] as ModelVO[]
  }
})
const emits = defineEmits(['onDrawStart', 'onDrawComplete']) // 定义 emits

// 定义属性
const drawIn = ref<boolean>(false) // 生成中
const selectHotWord = ref<string>('') // 选中的热词
// 表单
const prompt = ref<string>('') // 提示词
const width = ref<number>(512) // 图片宽度
const height = ref<number>(512) // 图片高度
const sampler = ref<string>('DDIM') // 采样方法
const steps = ref<number>(20) // 迭代步数
const seed = ref<number>(42) // 控制生成图像的随机性
const scale = ref<number>(7.5) // 引导系数
const clipGuidancePreset = ref<string>('NONE') // 文本提示相匹配的图像(clip_guidance_preset) 简称 CLIP
const stylePreset = ref<string>('3d-model') // 风格

/** 选择热词 */
const handleHotWordClick = async (hotWord: string) => {
  // 情况一：取消选中
  if (selectHotWord.value == hotWord) {
    selectHotWord.value = ''
    return
  }

  // 情况二：选中
  selectHotWord.value = hotWord // 选中
  prompt.value = hotWord // 替换提示词
}

/** 图片生成 */
const handleGenerateImage = async () => {
  // 从 models 中查找匹配的模型
  const selectModel = 'stable-diffusion-v1-6'
  const matchedModel = props.models.find(
    (item) => item.model === selectModel && item.platform === AiPlatformEnum.STABLE_DIFFUSION
  )
  if (!matchedModel) {
    message.error(t('image.modelUnavailable'))
    return
  }

  // 二次确认
  if (hasChinese(prompt.value)) {
    message.alert(t('image.chineseNotSupported'))
    return
  }
  await message.confirm(t('image.confirmGenerate'))

  try {
    // 加载中
    drawIn.value = true
    // 回调
    emits('onDrawStart', AiPlatformEnum.STABLE_DIFFUSION)
    // 发送请求
    const form = {
      modelId: matchedModel.id,
      prompt: prompt.value, // 提示词
      width: width.value, // 图片宽度
      height: height.value, // 图片高度
      options: {
        seed: seed.value, // 随机种子
        steps: steps.value, // 图片生成步数
        scale: scale.value, // 引导系数
        sampler: sampler.value, // 采样算法
        clipGuidancePreset: clipGuidancePreset.value, // 文本提示相匹配的图像 CLIP
        stylePreset: stylePreset.value // 风格
      }
    } as ImageDrawReqVO
    await ImageApi.drawImage(form)
  } finally {
    // 回调
    emits('onDrawComplete', AiPlatformEnum.STABLE_DIFFUSION)
    // 加载结束
    drawIn.value = false
  }
}

/** 填充值 */
const settingValues = async (detail: ImageVO) => {
  prompt.value = detail.prompt
  width.value = detail.width
  height.value = detail.height
  seed.value = detail.options?.seed
  steps.value = detail.options?.steps
  scale.value = detail.options?.scale
  sampler.value = detail.options?.sampler
  clipGuidancePreset.value = detail.options?.clipGuidancePreset
  stylePreset.value = detail.options?.stylePreset
}

/** 暴露组件方法 */
defineExpose({ settingValues })
</script>
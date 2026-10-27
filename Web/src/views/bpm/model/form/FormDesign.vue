<template>
  <el-form ref="formRef" :model="modelData" :rules="rules" label-width="120px" class="mt-20px">
    <el-form-item :label="t('process.model.formType')" prop="formType" class="mb-20px">
      <el-radio-group v-model="modelData.formType">
        <el-radio
          v-for="dict in getIntDictOptions(DICT_TYPE.BPM_MODEL_FORM_TYPE)"
          :key="dict.value"
          :value="dict.value"
        >
          {{ dict.label }}
        </el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item v-if="modelData.formType === BpmModelFormType.NORMAL" :label="t('process.model.formId')" prop="formId">
      <el-select v-model="modelData.formId" clearable style="width: 100%">
        <el-option v-for="form in formList" :key="form.id" :label="form.name" :value="form.id" />
      </el-select>
    </el-form-item>
    <el-form-item v-if="modelData.formType === BpmModelFormType.CUSTOM" :label="t('process.model.formSubmitRoute')" prop="formCustomCreatePath">
      <el-input
        v-model="modelData.formCustomCreatePath"
        :placeholder="t('process.model.formSubmitRoute')"
        style="width: 330px"
      />
      <el-tooltip
        class="item"
        :content="t('process.model.formSubmitRouteTip')"
        effect="light"
        placement="top"
      >
        <Icon icon="ep:question" class="ml-5px" />
      </el-tooltip>
    </el-form-item>
    <el-form-item v-if="modelData.formType === BpmModelFormType.CUSTOM" :label="t('process.model.formViewPath')" prop="formCustomViewPath">
      <el-input
        v-model="modelData.formCustomViewPath"
        :placeholder="t('process.model.formViewPath')"
        style="width: 330px"
      />
      <el-tooltip
        class="item"
        :content="t('process.model.formViewPathTip')"
        effect="light"
        placement="top"
      >
        <Icon icon="ep:question" class="ml-5px" />
      </el-tooltip>
    </el-form-item>
    <!-- 表单预览 -->
    <div
      v-if="modelData.formType === BpmModelFormType.NORMAL && modelData.formId && formPreview.rule.length > 0"
      class="mt-20px"
    >
      <div class="flex items-center mb-15px">
        <div class="h-15px w-4px bg-[#1890ff] mr-10px"></div>
        <span class="text-15px font-bold">{{ t('process.model.formPreview') }}</span>
      </div>
      <form-create
        v-model="formPreview.formData"
        :rule="formPreview.rule"
        :option="formPreview.option"
      />
    </div>
  </el-form>
</template>

<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import * as FormApi from '@/api/bpm/form'
import { setConfAndFields2 } from '@/utils/formCreate'
import { BpmModelFormType } from '@/utils/constants'

const props = defineProps({
  formList: {
    type: Array,
    required: true
  }
})

const { t } = useI18n('bpm') // 国际化
const formRef = ref()

// 创建本地数据副本
const modelData = defineModel<any>()

// 表单预览数据
const formPreview = ref({
  formData: {},
  rule: [],
  option: {
    submitBtn: false,
    resetBtn: false,
    formData: {}
  }
})

// 监听表单ID变化，加载表单数据
watch(
  () => modelData.value.formId,
  async (newFormId) => {
    if (newFormId && modelData.value.formType === BpmModelFormType.NORMAL) {
      const data = await FormApi.getForm(newFormId)
      setConfAndFields2(formPreview.value, data.conf, data.fields)
      // 设置只读
      formPreview.value.rule.forEach((item: any) => {
        item.props = { ...item.props, disabled: true }
      })
    } else {
      formPreview.value.rule = []
    }
  },
  { immediate: true }
)

const rules = {
  formType: [{ required: true, message: t('process.model.formType') + t('common.notEmpty'), trigger: 'blur' }],
  formId: [{ required: true, message: t('process.model.formId') + t('common.notEmpty'), trigger: 'blur' }],
  formCustomCreatePath: [{ required: true, message: t('process.model.formSubmitRoute') + t('common.notEmpty'), trigger: 'blur' }],
  formCustomViewPath: [{ required: true, message: t('process.model.formViewPath') + t('common.notEmpty'), trigger: 'blur' }]
}

/** 表单校验 */
const validate = async () => {
  await formRef.value?.validate()
}

defineExpose({
  validate
})
</script>

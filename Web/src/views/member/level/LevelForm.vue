<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="800">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="auto"
      v-loading="formLoading"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('level.name')" prop="name">
            <el-input v-model="formData.name" :placeholder="t('level.namePlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('level.level')" prop="level">
            <el-input-number
              v-model="formData.level"
              :min="0"
              :precision="0"
              :placeholder="t('level.levelPlaceholder')"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('level.experience')" prop="experience">
            <el-input-number
              v-model="formData.experience"
              :min="0"
              :precision="0"
              :placeholder="t('level.experiencePlaceholder')"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('level.discountPercent')" prop="discountPercent">
            <el-input-number
              v-model="formData.discountPercent"
              :min="0"
              :max="100"
              :precision="0"
              :placeholder="t('level.discountPercentPlaceholder')"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('level.icon')">
            <UploadImg v-model="formData.icon" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('level.bgUrl')">
            <UploadImg v-model="formData.backgroundUrl" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('level.status')" prop="status">
            <el-radio-group v-model="formData.status">
              <el-radio
                v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
                :key="dict.value"
                :value="dict.value"
              >
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">{{ t('common.ok') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import * as LevelApi from '@/api/member/level'
import { CommonStatusEnum } from '@/utils/constants'
import { useI18n } from '@/hooks/web/useI18n'

/** 会员等级表单 **/
defineOptions({ name: 'MemberLevelForm' })

const { t } = useI18n('member') // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  name: undefined,
  experience: undefined,
  level: undefined,
  discountPercent: undefined,
  icon: undefined,
  backgroundUrl: undefined,
  status: CommonStatusEnum.ENABLE
})
const formRules = reactive({
  name: [{ required: true, message: t('level.nameRequired'), trigger: 'blur' }],
  experience: [{ required: true, message: t('level.experienceRequired'), trigger: 'blur' }],
  level: [{ required: true, message: t('level.levelRequired'), trigger: 'blur' }],
  discountPercent: [{ required: true, message: t('level.discountPercentRequired'), trigger: 'blur' }],
  status: [{ required: true, message: t('level.statusRequired'), trigger: 'change' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await LevelApi.getLevel(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as LevelApi.LevelVO
    if (formType.value === 'create') {
      await LevelApi.createLevel(data)
      message.success(t('common.createSuccess'))
    } else {
      await LevelApi.updateLevel(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    name: undefined,
    experience: undefined,
    level: undefined,
    discountPercent: undefined,
    icon: undefined,
    backgroundUrl: undefined,
    status: CommonStatusEnum.ENABLE
  }
  formRef.value?.resetFields()
}
</script>

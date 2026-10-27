<template>
  <el-drawer
    v-model="drawerVisible"
    :title="drawerTitle"
    size="80%"
    direction="rtl"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    @close="handleClose"
  >
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="auto">
      <!-- 基础信息配置 -->
      <BasicInfoSection v-model="formData" :rules="formRules" />
      <!-- 触发器配置 -->
      <TriggerSection v-model:triggers="formData.triggers" />
      <!-- 执行器配置 -->
      <ActionSection v-model:actions="formData.actions" />
    </el-form>
    <template #footer>
      <div class="drawer-footer">
        <el-button :disabled="submitLoading" type="primary" @click="handleSubmit">
          <Icon icon="ep:check" />
          {{ t('common.ok') }}
        </el-button>
        <el-button @click="handleClose">
          <Icon icon="ep:close" />
          {{ t('common.cancel') }}
        </el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import BasicInfoSection from './sections/BasicInfoSection.vue'
import TriggerSection from './sections/TriggerSection.vue'
import ActionSection from './sections/ActionSection.vue'
import { IotSceneRule } from '@/api/iot/rule/scene'
import { RuleSceneApi } from '@/api/iot/rule/scene'
import {
  IotRuleSceneTriggerTypeEnum,
  IotRuleSceneActionTypeEnum,
  isDeviceTrigger
} from '@/views/iot/utils/constants'
import { ElMessage } from 'element-plus'
import { CommonStatusEnum } from '@/utils/constants'

/** IoT 场景联动规则表单 - 主表单组件 */
defineOptions({ name: 'RuleSceneForm' })

/** 组件属性定义 */
const props = defineProps<{
  /** 抽屉显示状态 */
  modelValue: boolean
  /** 编辑的场景联动规则数据 */
  ruleScene?: IotSceneRule
}>()

/** 组件事件定义 */
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'success'): void
}>()

const { t } = useI18n('iot') // 国际化
const drawerVisible = useVModel(props, 'modelValue', emit) // 抽屉显示状态

/**
 * 创建默认的表单数据
 * @returns 默认表单数据对象
 */
const createDefaultFormData = (): IotSceneRule => {
  return {
    name: '',
    description: '',
    status: CommonStatusEnum.ENABLE, // 默认启用状态
    triggers: [
      {
        type: IotRuleSceneTriggerTypeEnum.DEVICE_PROPERTY_POST,
        productId: undefined,
        deviceId: undefined,
        identifier: undefined,
        operator: undefined,
        value: undefined,
        cronExpression: undefined,
        conditionGroups: [] // 空的条件组数组
      }
    ],
    actions: []
  }
}

const formRef = ref() // 表单引用
const formData = ref<IotSceneRule>(createDefaultFormData()) // 表单数据

/**
 * 触发器校验器
 * @param _rule 校验规则（未使用）
 * @param value 校验值
 * @param callback 回调函数
 */
const validateTriggers = (_rule: any, value: any, callback: any) => {
  if (!value || !Array.isArray(value) || value.length === 0) {
    callback(new Error(t('rule.scene.needOneTrigger')))
    return
  }

  for (let i = 0; i < value.length; i++) {
    const trigger = value[i]

    // 校验触发器类型
    if (!trigger.type) {
      callback(new Error(`${t('rule.scene.trigger')} ${i + 1}: ${t('rule.scene.triggerTypeRequired')}`))
      return
    }

    // 校验设备触发器
    if (isDeviceTrigger(trigger.type)) {
      if (!trigger.productId) {
        callback(new Error(`${t('rule.scene.trigger')} ${i + 1}: ${t('rule.scene.productRequired')}`))
        return
      }
      if (!trigger.deviceId) {
        callback(new Error(`${t('rule.scene.trigger')} ${i + 1}: ${t('rule.scene.deviceRequired')}`))
        return
      }
      if (!trigger.identifier) {
        callback(new Error(`${t('rule.scene.trigger')} ${i + 1}: ${t('rule.scene.identifierRequired')}`))
        return
      }
      if (!trigger.operator) {
        callback(new Error(`${t('rule.scene.trigger')} ${i + 1}: ${t('rule.scene.operatorRequired')}`))
        return
      }
      if (trigger.value === undefined || trigger.value === null || trigger.value === '') {
        callback(new Error(`${t('rule.scene.trigger')} ${i + 1}: ${t('rule.scene.valueRequired')}`))
        return
      }
    }

    // 校验定时触发器
    if (trigger.type === IotRuleSceneTriggerTypeEnum.TIMER) {
      if (!trigger.cronExpression) {
        callback(new Error(`${t('rule.scene.trigger')} ${i + 1}: ${t('rule.scene.cronExpressionRequired')}`))
        return
      }
    }
  }

  callback()
}

/**
 * 执行器校验器
 * @param _rule 校验规则（未使用）
 * @param value 校验值
 * @param callback 回调函数
 */
const validateActions = (_rule: any, value: any, callback: any) => {
  if (!value || !Array.isArray(value) || value.length === 0) {
    callback(new Error(t('rule.scene.needOneAction')))
    return
  }

  for (let i = 0; i < value.length; i++) {
    const action = value[i]

    // 校验执行器类型
    if (!action.type) {
      callback(new Error(`${t('rule.scene.action')} ${i + 1}: ${t('rule.scene.actionTypeRequired')}`))
      return
    }

    // 校验设备控制执行器
    if (
      action.type === IotRuleSceneActionTypeEnum.DEVICE_PROPERTY_SET ||
      action.type === IotRuleSceneActionTypeEnum.DEVICE_SERVICE_INVOKE
    ) {
      if (!action.productId) {
        callback(new Error(`${t('rule.scene.action')} ${i + 1}: ${t('rule.scene.productRequired')}`))
        return
      }
      if (!action.deviceId) {
        callback(new Error(`${t('rule.scene.action')} ${i + 1}: ${t('rule.scene.deviceRequired')}`))
        return
      }

      // 服务调用需要验证服务标识符
      if (action.type === IotRuleSceneActionTypeEnum.DEVICE_SERVICE_INVOKE) {
        if (!action.identifier) {
          callback(new Error(`${t('rule.scene.action')} ${i + 1}: ${t('rule.scene.serviceRequired')}`))
          return
        }
      }

      if (!action.params || Object.keys(action.params).length === 0) {
        callback(new Error(`${t('rule.scene.action')} ${i + 1}: ${t('rule.scene.paramsRequired')}`))
        return
      }
    }

    // 校验告警执行器
    if (
      action.type === IotRuleSceneActionTypeEnum.ALERT_TRIGGER ||
      action.type === IotRuleSceneActionTypeEnum.ALERT_RECOVER
    ) {
      if (!action.alertConfigId) {
        callback(new Error(`${t('rule.scene.action')} ${i + 1}: ${t('rule.scene.alertConfigRequired')}`))
        return
      }
    }
  }

  callback()
}

const formRules = reactive({
  name: [
    { required: true, message: t('rule.scene.nameRequired'), trigger: 'blur' },
    { type: 'string', min: 1, max: 50, message: t('rule.scene.nameLengthLimit'), trigger: 'blur' }
  ],
  status: [
    { required: true, message: t('rule.scene.statusRequired'), trigger: 'change' },
    {
      type: 'enum',
      enum: [CommonStatusEnum.ENABLE, CommonStatusEnum.DISABLE],
      message: t('rule.scene.statusMustBeEnableOrDisable'),
      trigger: 'change'
    }
  ],
  description: [
    { type: 'string', max: 200, message: t('rule.scene.descriptionLengthLimit'), trigger: 'blur' }
  ],
  triggers: [{ required: true, validator: validateTriggers, trigger: 'change' }],
  actions: [{ required: true, validator: validateActions, trigger: 'change' }]
}) // 表单校验规则

const submitLoading = ref(false) // 提交加载状态
const isEdit = ref(false) // 是否为编辑模式
const drawerTitle = computed(() => (isEdit.value ? t('rule.scene.editRule') : t('rule.scene.createRule'))) // 抽屉标题

/** 提交表单 */
const handleSubmit = async () => {
  // 校验表单
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return

  // 提交请求
  submitLoading.value = true
  try {
    if (isEdit.value) {
      // 更新场景联动规则
      await RuleSceneApi.updateRuleScene(formData.value)
      ElMessage.success(t('rule.scene.updateSuccess'))
    } else {
      // 创建场景联动规则
      await RuleSceneApi.createRuleScene(formData.value)
      ElMessage.success(t('rule.scene.createSuccess'))
    }

    // 关闭抽屉并触发成功事件
    drawerVisible.value = false
    emit('success')
  } catch (error) {
    console.error(t('rule.scene.saveFailed'), error)
    ElMessage.error(isEdit.value ? t('rule.scene.updateFailed') : t('rule.scene.createFailed'))
  } finally {
    submitLoading.value = false
  }
}

/** 处理抽屉关闭事件 */
const handleClose = () => {
  drawerVisible.value = false
}

/** 初始化表单数据 */
const initFormData = () => {
  if (props.ruleScene) {
    // 编辑模式：数据结构已对齐，直接使用后端数据
    isEdit.value = true
    formData.value = {
      ...props.ruleScene,
      // 确保触发器数组不为空
      triggers: props.ruleScene.triggers?.length
        ? props.ruleScene.triggers
        : [
            {
              type: IotRuleSceneTriggerTypeEnum.DEVICE_PROPERTY_POST,
              productId: undefined,
              deviceId: undefined,
              identifier: undefined,
              operator: undefined,
              value: undefined,
              cronExpression: undefined,
              conditionGroups: []
            }
          ],
      // 确保执行器数组不为空
      actions: props.ruleScene.actions || []
    }
  } else {
    // 新增模式：使用默认数据
    isEdit.value = false
    formData.value = createDefaultFormData()
  }
}

/** 监听抽屉显示 */
watch(drawerVisible, async (visible) => {
  if (visible) {
    initFormData()
    // 重置表单验证状态
    await nextTick()
    formRef.value?.clearValidate()
  }
})

/** 监听编辑数据变化 */
watch(
  () => props.ruleScene,
  () => {
    if (drawerVisible.value) {
      initFormData()
    }
  },
  { deep: true }
)
</script>
<template>
  <div class="panel-tab__content">
    <el-table :data="elementListenersList" size="small" border :table-layout="'auto'">
      <el-table-column :label="t('common.index')" width="50px" type="index" />
      <el-table-column
        :label="t('designer.eventType')"
        min-width="80px"
        show-overflow-tooltip
        :formatter="(row) => listenerEventTypeObject[row.event]"
      />
      <el-table-column label="ID" min-width="80px" prop="id" show-overflow-tooltip />
      <el-table-column
        :label="t('designer.listenerType')"
        min-width="80px"
        show-overflow-tooltip
        :formatter="(row) => listenerTypeObject[row.listenerType]"
      />
      <el-table-column :label="t('common.action')" min-width="150">
        <template #default="scope">
          <el-button size="small" link @click="openListenerForm(scope.row, scope.$index)"
            >{{ t('common.edit') }}</el-button
          >
          <el-divider direction="vertical" />
          <el-button
            size="small"
            link
            style="color: #ff4d4f"
            @click="removeListener(scope.row, scope.$index)"
            >{{ t('common.remove') }}</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <div class="element-drawer__button">
      <XButton
        size="small"
        type="primary"
        preIcon="ep:plus"
        :title="t('designer.addListener')"
        @click="openListenerForm(null)"
      />
      <XButton
        type="success"
        preIcon="ep:select"
        :title="t('designer.selectListener')"
        size="small"
        @click="openProcessListenerDialog"
      />
    </div>

    <!-- 监听器 编辑/创建 部分 -->
    <el-drawer
      v-model="listenerFormModelVisible"
      :title="t('designer.taskListener')"
      :size="`${width}px`"
      append-to-body
      destroy-on-close
    >
      <el-form size="small" :model="listenerForm" label-width="96px" ref="listenerFormRef">
        <el-form-item
          :label="t('designer.eventType')"
          prop="event"
          :rules="{ required: true, trigger: ['blur', 'change'] }"
        >
          <el-select v-model="listenerForm.event">
            <el-option
              v-for="i in Object.keys(listenerEventTypeObject)"
              :key="i"
              :label="listenerEventTypeObject[i]"
              :value="i"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          label="ID"
          prop="id"
          :rules="{ required: true, trigger: ['blur', 'change'] }"
        >
          <el-input v-model="listenerForm.id" clearable />
        </el-form-item>
        <el-form-item
          :label="t('designer.listenerType')"
          prop="listenerType"
          :rules="{ required: true, trigger: ['blur', 'change'] }"
        >
          <el-select v-model="listenerForm.listenerType">
            <el-option
              v-for="i in Object.keys(listenerTypeObject)"
              :key="i"
              :label="listenerTypeObject[i]"
              :value="i"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          v-if="listenerForm.listenerType === 'classListener'"
          :label="t('designer.javaClass')"
          prop="class"
          key="listener-class"
          :rules="{ required: true, trigger: ['blur', 'change'] }"
        >
          <el-input v-model="listenerForm.class" clearable />
        </el-form-item>
        <el-form-item
          v-if="listenerForm.listenerType === 'expressionListener'"
          :label="t('designer.expression')"
          prop="expression"
          key="listener-expression"
          :rules="{ required: true, trigger: ['blur', 'change'] }"
        >
          <el-input v-model="listenerForm.expression" clearable />
        </el-form-item>
        <el-form-item
          v-if="listenerForm.listenerType === 'delegateExpressionListener'"
          :label="t('designer.delegateExpression')"
          prop="delegateExpression"
          key="listener-delegate"
          :rules="{ required: true, trigger: ['blur', 'change'] }"
        >
          <el-input v-model="listenerForm.delegateExpression" clearable />
        </el-form-item>
        <template v-if="listenerForm.listenerType === 'scriptListener'">
          <el-form-item
            :label="t('designer.scriptFormat')"
            prop="scriptFormat"
            key="listener-script-format"
            :rules="{ required: true, trigger: ['blur', 'change'], message: t('designer.scriptFormat') }"
          >
            <el-input v-model="listenerForm.scriptFormat" clearable />
          </el-form-item>
          <el-form-item
            :label="t('designer.scriptType')"
            prop="scriptType"
            key="listener-script-type"
            :rules="{ required: true, trigger: ['blur', 'change'], message: t('designer.scriptType') }"
          >
            <el-select v-model="listenerForm.scriptType">
              <el-option :label="t('designer.inlineScript')" value="inlineScript" />
              <el-option :label="t('designer.externalScript')" value="externalScript" />
            </el-select>
          </el-form-item>
          <el-form-item
            v-if="listenerForm.scriptType === 'inlineScript'"
            :label="t('designer.scriptContent')"
            prop="value"
            key="listener-script"
            :rules="{ required: true, trigger: ['blur', 'change'], message: t('designer.scriptContent') }"
          >
            <el-input v-model="listenerForm.value" clearable />
          </el-form-item>
          <el-form-item
            v-if="listenerForm.scriptType === 'externalScript'"
            :label="t('designer.resourceUrl')"
            prop="resource"
            key="listener-resource"
            :rules="{ required: true, trigger: ['blur', 'change'], message: t('designer.resourceUrl') }"
          >
            <el-input v-model="listenerForm.resource" clearable />
          </el-form-item>
        </template>

        <template v-if="listenerForm.event === 'timeout'">
          <el-form-item :label="t('designer.timerType')" prop="eventDefinitionType" key="eventDefinitionType">
            <el-select v-model="listenerForm.eventDefinitionType">
              <el-option :label="t('designer.time')" value="date" />
              <el-option :label="t('designer.duration')" value="duration" />
              <el-option :label="t('designer.cycle')" value="cycle" />
              <el-option :label="t('designer.none')" value="null" />
            </el-select>
          </el-form-item>
          <el-form-item
            v-if="!!listenerForm.eventDefinitionType && listenerForm.eventDefinitionType !== 'null'"
            :label="t('designer.timerType')"
            prop="eventTimeDefinitions"
            key="eventTimeDefinitions"
            :rules="{ required: true, trigger: ['blur', 'change'], message: t('designer.timerType') }"
          >
            <el-input v-model="listenerForm.eventTimeDefinitions" clearable />
          </el-form-item>
        </template>
      </el-form>

      <el-divider />
      <p class="listener-filed__title">
        <span><Icon icon="ep:menu" />{{ t('designer.injectFields') }}：</span>
        <el-button size="small" type="primary" @click="openListenerFieldForm(null)"
          >{{ t('designer.addField') }}</el-button
        >
      </p>
      <el-table
        :data="fieldsListOfListener"
        size="small"
        max-height="240"
        fit
        border
        style="flex: none"
        :table-layout="'auto'"
      >
        <el-table-column :label="t('common.index')" width="50px" type="index" />
        <el-table-column :label="t('designer.fieldName')" min-width="100px" prop="name" />
        <el-table-column
          :label="t('designer.fieldType')"
          min-width="80px"
          show-overflow-tooltip
          :formatter="(row) => fieldTypeObject[row.fieldType]"
        />
        <el-table-column
          :label="t('designer.fieldValue')"
          min-width="100px"
          show-overflow-tooltip
          :formatter="(row) => row.string || row.expression"
        />
        <el-table-column :label="t('common.action')" min-width="150">
          <template #default="scope">
            <el-button size="small" link @click="openListenerFieldForm(scope.row, scope.$index)"
              >{{ t('common.edit') }}</el-button
            >
            <el-divider direction="vertical" />
            <el-button
              size="small"
              link
              style="color: #ff4d4f"
              @click="removeListenerField(scope.row, scope.$index)"
              >{{ t('common.remove') }}</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <div class="element-drawer__button">
        <el-button size="small" @click="listenerFormModelVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button size="small" type="primary" @click="saveListenerConfig">{{ t('common.save') }}</el-button>
      </div>
    </el-drawer>

    <!-- 注入西段 编辑/创建 部分 -->
    <el-dialog
      :title="t('designer.fieldConfig')"
      v-model="listenerFieldFormModelVisible"
      width="600px"
      append-to-body
      destroy-on-close
    >
      <el-form
        :model="listenerFieldForm"
        size="small"
        label-width="96px"
        ref="listenerFieldFormRef"
        style="height: 136px"
      >
        <el-form-item
          :label="t('designer.fieldName') + '：'"
          prop="name"
          :rules="{ required: true, trigger: ['blur', 'change'] }"
        >
          <el-input v-model="listenerFieldForm.name" clearable />
        </el-form-item>
        <el-form-item
          :label="t('designer.fieldType') + '：'"
          prop="fieldType"
          :rules="{ required: true, trigger: ['blur', 'change'] }"
        >
          <el-select v-model="listenerFieldForm.fieldType">
            <el-option
              v-for="i in Object.keys(fieldTypeObject)"
              :key="i"
              :label="fieldTypeObject[i]"
              :value="i"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          v-if="listenerFieldForm.fieldType === 'string'"
          :label="t('designer.fieldValue') + '：'"
          prop="string"
          key="field-string"
          :rules="{ required: true, trigger: ['blur', 'change'] }"
        >
          <el-input v-model="listenerFieldForm.string" clearable />
        </el-form-item>
        <el-form-item
          v-if="listenerFieldForm.fieldType === 'expression'"
          :label="t('designer.expression') + '：'"
          prop="expression"
          key="field-expression"
          :rules="{ required: true, trigger: ['blur', 'change'] }"
        >
          <el-input v-model="listenerFieldForm.expression" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button size="small" @click="listenerFieldFormModelVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button size="small" type="primary" @click="saveListenerFiled">{{ t('common.confirm') }}</el-button>
      </template>
    </el-dialog>
  </div>

  <!-- 选择弹窗 -->
  <ProcessListenerDialog ref="processListenerDialogRef" @select="selectProcessListener" />
</template>
<script lang="ts" setup>
import { ElMessageBox } from 'element-plus'
import { useI18n } from '@/hooks/web/useI18n'
import { createListenerObject, updateElementExtensions } from '../../utils'
import {
  initListenerForm,
  initListenerType,
  eventType,
  listenerType,
  fieldType,
  initListenerForm2
} from './utilSelf'
import ProcessListenerDialog from '@/components/bpmnProcessDesigner/package/penal/listeners/ProcessListenerDialog.vue'

defineOptions({ name: 'UserTaskListeners' })

const { t } = useI18n('bpm')

const props = defineProps({
  id: String,
  type: String
})
const prefix = inject('prefix')
const width = inject('width')
const elementListenersList = ref<any[]>([])
const listenerEventTypeObject = ref(eventType)
const listenerTypeObject = ref(listenerType)
const listenerFormModelVisible = ref(false)
const listenerForm = ref<any>({})
const fieldTypeObject = ref(fieldType)
const fieldsListOfListener = ref<any[]>([])
const listenerFieldFormModelVisible = ref(false) // 监听器 注入字段表单弹窗 显示状态
const editingListenerIndex = ref(-1) // 监听器所在下标，-1 为新增
const editingListenerFieldIndex = ref(-1) // 字段所在下标，-1 为新增
const listenerFieldForm = ref<any>({}) // 监听器 注入字段 详情表单
const bpmnElementListeners = ref()
const otherExtensionList = ref()
const listenerFormRef = ref()
const listenerFieldFormRef = ref()
const bpmnInstances = () => (window as any)?.bpmnInstances

const resetListenersList = () => {
  const instances = bpmnInstances()
  if (!instances || !instances.bpmnElement) return

  // 直接使用原始BPMN元素，避免Vue响应式代理问题
  const bpmnElement = instances.bpmnElement
  const businessObject = bpmnElement.businessObject

  console.log(bpmnElement, 'bpmnElement - resetListenersList')

  otherExtensionList.value =
    businessObject?.extensionElements?.values?.filter(
      (ex) => ex.$type !== `${prefix}:TaskListener`
    ) ?? [] // 保留非监听器类型的扩展属性，避免移除监听器时清空其他配置（如审批人等）。相关案例：https://gitee.com/mitedtsmcode/mitedtsm-ui-admin-vue3/issues/ICMSYC
  bpmnElementListeners.value =
    businessObject?.extensionElements?.values?.filter(
      (ex) => ex.$type === `${prefix}:TaskListener`
    ) ?? []
  elementListenersList.value = bpmnElementListeners.value.map((listener) =>
    initListenerType(listener)
  )
}
const openListenerForm = (listener, index?) => {
  if (listener) {
    listenerForm.value = initListenerForm(listener)
    editingListenerIndex.value = index
  } else {
    listenerForm.value = {}
    editingListenerIndex.value = -1 // 标记为新增
  }
  if (listener && listener.fields) {
    fieldsListOfListener.value = listener.fields.map((field) => ({
      ...field,
      fieldType: field.string ? 'string' : 'expression'
    }))
  } else {
    fieldsListOfListener.value = []
    listenerForm.value['fields'] = []
  }
  // 打开侧边栏并清楚验证状态
  listenerFormModelVisible.value = true
  nextTick(() => {
    if (listenerFormRef.value) listenerFormRef.value.clearValidate()
  })
}
// 移除监听器
const removeListener = (listener, index?) => {
  console.log(listener, 'listener')
  ElMessageBox.confirm(t('designer.confirmRemoveListener'), t('common.tip'), {
    confirmButtonText: t('common.confirm'),
    cancelButtonText: t('common.cancel')
  })
    .then(() => {
      const instances = bpmnInstances()
      if (!instances || !instances.bpmnElement) return

      bpmnElementListeners.value.splice(index, 1)
      elementListenersList.value.splice(index, 1)
      updateElementExtensions(
        instances.bpmnElement,
        otherExtensionList.value.concat(bpmnElementListeners.value)
      )
    })
    .catch(() => console.info('操作取消'))
}
// 保存监听器
const saveListenerConfig = async () => {
  let validateStatus = await listenerFormRef.value.validate()
  if (!validateStatus) return // 验证不通过直接返回

  const instances = bpmnInstances()
  if (!instances || !instances.bpmnElement) return

  const bpmnElement = instances.bpmnElement
  const listenerObject = createListenerObject(listenerForm.value, true, prefix)

  if (editingListenerIndex.value === -1) {
    bpmnElementListeners.value.push(listenerObject)
    elementListenersList.value.push(listenerForm.value)
  } else {
    bpmnElementListeners.value.splice(editingListenerIndex.value, 1, listenerObject)
    elementListenersList.value.splice(editingListenerIndex.value, 1, listenerForm.value)
  }
  // 保存其他配置
  otherExtensionList.value =
    bpmnElement.businessObject?.extensionElements?.values?.filter(
      (ex) => ex.$type !== `${prefix}:TaskListener`
    ) ?? []
  updateElementExtensions(
    bpmnElement,
    otherExtensionList.value.concat(bpmnElementListeners.value)
  )
  // 4. 隐藏侧边栏
  listenerFormModelVisible.value = false
  listenerForm.value = {}
}
// 打开监听器字段编辑弹窗
const openListenerFieldForm = (field, index?) => {
  listenerFieldForm.value = field ? JSON.parse(JSON.stringify(field)) : {}
  editingListenerFieldIndex.value = field ? index : -1
  listenerFieldFormModelVisible.value = true
  nextTick(() => {
    if (listenerFieldFormRef.value) listenerFieldFormRef.value.clearValidate()
  })
}
// 保存监听器注入字段
const saveListenerFiled = async () => {
  let validateStatus = await listenerFieldFormRef.value.validate()
  if (!validateStatus) return // 验证不通过直接返回
  if (editingListenerFieldIndex.value === -1) {
    fieldsListOfListener.value.push(listenerFieldForm.value)
    listenerForm.value.fields.push(listenerFieldForm.value)
  } else {
    fieldsListOfListener.value.splice(editingListenerFieldIndex.value, 1, listenerFieldForm.value)
    listenerForm.value.fields.splice(editingListenerFieldIndex.value, 1, listenerFieldForm.value)
  }
  listenerFieldFormModelVisible.value = false
  nextTick(() => {
    listenerFieldForm.value = {}
  })
}
// 移除监听器字段
const removeListenerField = (field, index) => {
  console.log(field, 'field')
  ElMessageBox.confirm(t('designer.confirmRemoveField'), t('common.tip'), {
    confirmButtonText: t('common.confirm'),
    cancelButtonText: t('common.cancel')
  })
    .then(() => {
      fieldsListOfListener.value.splice(index, 1)
      listenerForm.value.fields.splice(index, 1)
    })
    .catch(() => console.info('操作取消'))
}

// 打开监听器弹窗
const processListenerDialogRef = ref()
const openProcessListenerDialog = async () => {
  processListenerDialogRef.value.open('task')
}
const selectProcessListener = (listener) => {
  const instances = bpmnInstances()
  if (!instances || !instances.bpmnElement) return

  const bpmnElement = instances.bpmnElement
  const listenerForm = initListenerForm2(listener)
  const listenerObject = createListenerObject(listenerForm, true, prefix)
  bpmnElementListeners.value.push(listenerObject)
  elementListenersList.value.push(listenerForm)

  // 保存其他配置
  otherExtensionList.value =
    bpmnElement.businessObject?.extensionElements?.values?.filter(
      (ex) => ex.$type !== `${prefix}:TaskListener`
    ) ?? []
  updateElementExtensions(
    bpmnElement,
    otherExtensionList.value.concat(bpmnElementListeners.value)
  )
}

watch(
  () => props.id,
  (val) => {
    val &&
      val.length &&
      nextTick(() => {
        resetListenersList()
      })
  },
  { immediate: true }
)
</script>

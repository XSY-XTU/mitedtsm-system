<template>
  <div class="node-wrapper">
    <div class="node-container">
      <div
        class="node-box"
        :class="[
          { 'node-config-error': !currentNode.showText },
          `${useTaskStatusClass(currentNode?.activityStatus)}`
        ]"
      >
        <div class="node-title-container">
          <div class="node-title-icon start-user"
            ><span class="iconfont icon-start-user"></span
          ></div>
          <input
            v-if="!readonly && showInput"
            type="text"
            class="editable-title-input"
            @blur="blurEvent()"
            v-mountedFocus
            v-model="currentNode.name"
            :placeholder="currentNode.name"
          />
          <div v-else class="node-title" @click="clickTitle">
            {{ currentNode.name }}
          </div>
        </div>
        <div class="node-content" @click="nodeClick">
          <div class="node-text" :title="currentNode.showText" v-if="currentNode.showText">
            {{ currentNode.showText }}
          </div>
          <div class="node-text" v-else>
            {{ t('simpleDesigner.configureInitiator') }}
          </div>
          <Icon icon="ep:arrow-right-bold" v-if="!readonly" />
        </div>
      </div>
      <!-- 传递子节点给添加节点组件。会在子节点前面添加节点 -->
      <NodeHandler
        v-if="currentNode"
        v-model:child-node="currentNode.childNode"
        :current-node="currentNode"
      />
    </div>
  </div>
  <StartUserNodeConfig v-if="!readonly && currentNode" ref="nodeSetting" :flow-node="currentNode" />
  <!-- 审批记录 -->
  <el-dialog
    :title="dialogTitle || t('simpleDesigner.approvalRecord')"
    v-model="dialogVisible"
    width="1000px"
    append-to-body
  >
    <el-row>
      <el-table :data="selectTasks" size="small" border header-cell-class-name="table-header-gray" :table-layout="'auto'">
        <el-table-column
          :label="t('simpleDesigner.serialNumber')"
          header-align="center"
          align="center"
          type="index"
          width="50"
        />
        <el-table-column :label="t('simpleDesigner.approverLabel')" min-width="100" align="center">
          <template #default="scope">
            {{ scope.row.assigneeUser?.nickname || scope.row.ownerUser?.nickname }}
          </template>
        </el-table-column>

        <el-table-column :label="t('simpleDesigner.deptLabel')" min-width="100" align="center">
          <template #default="scope">
            {{ scope.row.assigneeUser?.deptName || scope.row.ownerUser?.deptName }}
          </template>
        </el-table-column>
        <el-table-column
          :formatter="dateFormatter"
          align="center"
          :label="t('simpleDesigner.startTimeLabel')"
          prop="createTime"
          min-width="140"
        />
        <el-table-column
          :formatter="dateFormatter"
          align="center"
          :label="t('simpleDesigner.endTimeLabel')"
          prop="endTime"
          min-width="140"
        />
        <el-table-column align="center" :label="t('simpleDesigner.approvalStatusLabel')" prop="status" min-width="90">
          <template #default="scope">
            <dict-tag :type="DICT_TYPE.BPM_TASK_STATUS" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column align="center" :label="t('simpleDesigner.approvalOpinionLabel')" prop="reason" min-width="120" />
        <el-table-column align="center" :label="t('simpleDesigner.durationLabel')" prop="durationInMillis" min-width="100">
          <template #default="scope">
            {{ formatPast2(scope.row.durationInMillis) }}
          </template>
        </el-table-column>
      </el-table>
    </el-row>
  </el-dialog>
</template>
<script setup lang="ts">
import NodeHandler from '../NodeHandler.vue'
import { useWatchNode, useNodeName2, useTaskStatusClass } from '../node'
import { SimpleFlowNode, NodeType } from '../consts'
import StartUserNodeConfig from '../nodes-config/StartUserNodeConfig.vue'
import { dateFormatter, formatPast2 } from '@/utils/formatTime'
import { DICT_TYPE } from '@/utils/dict'

defineOptions({
  name: 'StartEventNode'
})

const { t } = useI18n('bpm') // 国际化

const props = defineProps({
  flowNode: {
    type: Object as () => SimpleFlowNode,
    default: () => null
  }
})
const readonly = inject<Boolean>('readonly') // 是否只读
const tasks = inject<Ref<any[]>>('tasks', ref([]))
// 定义事件，更新父组件。
const emits = defineEmits<{
  'update:modelValue': [node: SimpleFlowNode | undefined]
}>()
// 监控节点变化
const currentNode = useWatchNode(props)
// 节点名称编辑
const { showInput, blurEvent, clickTitle } = useNodeName2(currentNode, NodeType.START_USER_NODE)

const nodeSetting = ref()
//
const nodeClick = () => {
  if (readonly) {
    // 只读模式，弹窗显示任务信息
    if (tasks && tasks.value) {
      dialogTitle.value = currentNode.value.name
      selectTasks.value = tasks.value.filter(
        (item: any) => item?.taskDefinitionKey === currentNode.value.id
      )
      dialogVisible.value = true
    }
  } else {
    // 编辑模式，打开节点配置、把当前节点传递给配置组件
    nodeSetting.value.showStartUserNodeConfig(currentNode.value)
    nodeSetting.value.openDrawer()
  }
}

// 任务的弹窗显示，用于只读模式
const dialogVisible = ref(false) // 弹窗可见性
const dialogTitle = ref<string | undefined>(undefined) // 弹窗标题
const selectTasks = ref<any[] | undefined>([]) // 选中的任务数组
</script>
<style lang="scss" scoped></style>

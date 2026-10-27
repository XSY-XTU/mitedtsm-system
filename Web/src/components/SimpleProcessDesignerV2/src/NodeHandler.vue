<template>
  <div class="node-handler-wrapper">
    <div class="node-handler">
      <el-popover
        trigger="hover"
        v-model:visible="popoverShow"
        placement="right-start"
        width="auto"
        v-if="!readonly"
      >
        <div class="handler-item-wrapper">
          <div class="handler-item" @click="addNode(NodeType.USER_TASK_NODE)">
            <div class="approve handler-item-icon">
              <span class="iconfont icon-approve icon-size"></span>
            </div>
            <div class="handler-item-text">{{ t('simpleDesigner.approver') }}</div>
          </div>
          <div class="handler-item" @click="addNode(NodeType.TRANSACTOR_NODE)">
            <div class="transactor handler-item-icon">
              <span class="iconfont icon-transactor icon-size"></span>
            </div>
            <div class="handler-item-text">{{ t('simpleDesigner.handler') }}</div>
          </div>
          <div class="handler-item" @click="addNode(NodeType.COPY_TASK_NODE)">
            <div class="handler-item-icon copy">
              <span class="iconfont icon-size icon-copy"></span>
            </div>
            <div class="handler-item-text">{{ t('simpleDesigner.copy') }}</div>
          </div>
          <div class="handler-item" @click="addNode(NodeType.CONDITION_BRANCH_NODE)">
            <div class="handler-item-icon condition">
              <span class="iconfont icon-size icon-exclusive"></span>
            </div>
            <div class="handler-item-text">{{ t('simpleDesigner.conditionBranch') }}</div>
          </div>
          <div class="handler-item" @click="addNode(NodeType.PARALLEL_BRANCH_NODE)">
            <div class="handler-item-icon parallel">
              <span class="iconfont icon-size icon-parallel"></span>
            </div>
            <div class="handler-item-text">{{ t('simpleDesigner.parallelBranch') }}</div>
          </div>
          <div class="handler-item" @click="addNode(NodeType.INCLUSIVE_BRANCH_NODE)">
            <div class="handler-item-icon inclusive">
              <span class="iconfont icon-size icon-inclusive"></span>
            </div>
            <div class="handler-item-text">{{ t('simpleDesigner.inclusiveBranch') }}</div>
          </div>
          <div class="handler-item" @click="addNode(NodeType.DELAY_TIMER_NODE)">
            <div class="handler-item-icon delay">
              <span class="iconfont icon-size icon-delay"></span>
            </div>
            <div class="handler-item-text">{{ t('simpleDesigner.delayTimer') }}</div>
          </div>
          <div class="handler-item" @click="addNode(NodeType.ROUTER_BRANCH_NODE)">
            <div class="handler-item-icon router">
              <span class="iconfont icon-size icon-router"></span>
            </div>
            <div class="handler-item-text">{{ t('simpleDesigner.routerBranch') }}</div>
          </div>
          <div class="handler-item" @click="addNode(NodeType.TRIGGER_NODE)">
            <div class="handler-item-icon trigger">
              <span class="iconfont icon-size icon-trigger"></span>
            </div>
            <div class="handler-item-text">{{ t('simpleDesigner.trigger') }}</div>
          </div>
          <div class="handler-item" @click="addNode(NodeType.CHILD_PROCESS_NODE)">
            <div class="handler-item-icon child-process">
              <span class="iconfont icon-size icon-child-process"></span>
            </div>
            <div class="handler-item-text">{{ t('simpleDesigner.childProcess') }}</div>
          </div>
        </div>
        <template #reference>
          <div class="add-icon"><Icon icon="ep:plus" /></div>
        </template>
      </el-popover>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  ApproveMethodType,
  AssignEmptyHandlerType,
  AssignStartUserHandlerType,
  ConditionType,
  NODE_DEFAULT_NAME_KEY,
  NodeType,
  RejectHandlerType,
  SimpleFlowNode,
  DEFAULT_CONDITION_GROUP_VALUE
} from './consts'
import { generateUUID } from '@/utils'
import { cloneDeep } from 'lodash-es'

defineOptions({
  name: 'NodeHandler'
})

const { t } = useI18n('bpm') // 国际化

// 获取节点默认名称
const getNodeDefaultName = (type: number): string => {
  const key = NODE_DEFAULT_NAME_KEY.get(type)
  return key ? t(`simpleDesigner.${key}`) : ''
}

const popoverShow = ref(false)
const props = defineProps({
  childNode: {
    type: Object as () => SimpleFlowNode,
    default: null
  },
  currentNode: {
    type: Object as () => SimpleFlowNode,
    required: true
  }
})
const emits = defineEmits(['update:childNode'])

const readonly = inject<Boolean>('readonly') // 是否只读

const addNode = (type: number) => {
  popoverShow.value = false
  if (type === NodeType.USER_TASK_NODE || type === NodeType.TRANSACTOR_NODE) {
    const id = 'Activity_' + generateUUID()
    const data: SimpleFlowNode = {
      id: id,
      name: getNodeDefaultName(type),
      showText: '',
      type: type,
      approveMethod: ApproveMethodType.SEQUENTIAL_APPROVE,
      // 超时处理
      rejectHandler: {
        type: RejectHandlerType.FINISH_PROCESS
      },
      timeoutHandler: {
        enable: false
      },
      assignEmptyHandler: {
        type: AssignEmptyHandlerType.APPROVE
      },
      assignStartUserHandlerType: AssignStartUserHandlerType.START_USER_AUDIT,
      childNode: props.childNode,
      taskCreateListener: {
        enable: false
      },
      taskAssignListener: {
        enable: false
      },
      taskCompleteListener: {
        enable: false
      }
    }
    emits('update:childNode', data)
  }
  if (type === NodeType.COPY_TASK_NODE) {
    const data: SimpleFlowNode = {
      id: 'Activity_' + generateUUID(),
      name: getNodeDefaultName(NodeType.COPY_TASK_NODE),
      showText: '',
      type: NodeType.COPY_TASK_NODE,
      childNode: props.childNode
    }
    emits('update:childNode', data)
  }
  if (type === NodeType.CONDITION_BRANCH_NODE) {
    const data: SimpleFlowNode = {
      name: t('simpleDesigner.conditionBranch'),
      type: NodeType.CONDITION_BRANCH_NODE,
      id: 'GateWay_' + generateUUID(),
      childNode: props.childNode,
      conditionNodes: [
        {
          id: 'Flow_' + generateUUID(),
          name: t('simpleDesigner.condition1'),
          showText: '',
          type: NodeType.CONDITION_NODE,
          childNode: undefined,
          conditionSetting: {
            defaultFlow: false,
            conditionType: ConditionType.RULE,
            conditionGroups: cloneDeep(DEFAULT_CONDITION_GROUP_VALUE)
          }
        },
        {
          id: 'Flow_' + generateUUID(),
          name: t('simpleDesigner.otherCase'),
          showText: t('simpleDesigner.otherCaseDesc'),
          type: NodeType.CONDITION_NODE,
          childNode: undefined,
          conditionSetting: {
            defaultFlow: true
          }
        }
      ]
    }
    emits('update:childNode', data)
  }
  if (type === NodeType.PARALLEL_BRANCH_NODE) {
    const data: SimpleFlowNode = {
      name: t('simpleDesigner.parallelBranch'),
      type: NodeType.PARALLEL_BRANCH_NODE,
      id: 'GateWay_' + generateUUID(),
      childNode: props.childNode,
      conditionNodes: [
        {
          id: 'Flow_' + generateUUID(),
          name: t('simpleDesigner.parallel1'),
          showText: t('simpleDesigner.parallelDesc'),
          type: NodeType.CONDITION_NODE,
          childNode: undefined
        },
        {
          id: 'Flow_' + generateUUID(),
          name: t('simpleDesigner.parallel2'),
          showText: t('simpleDesigner.parallelDesc'),
          type: NodeType.CONDITION_NODE,
          childNode: undefined
        }
      ]
    }
    emits('update:childNode', data)
  }
  if (type === NodeType.INCLUSIVE_BRANCH_NODE) {
    const data: SimpleFlowNode = {
      name: t('simpleDesigner.inclusiveBranch'),
      type: NodeType.INCLUSIVE_BRANCH_NODE,
      id: 'GateWay_' + generateUUID(),
      childNode: props.childNode,
      conditionNodes: [
        {
          id: 'Flow_' + generateUUID(),
          name: t('simpleDesigner.inclusiveCondition1'),
          showText: '',
          type: NodeType.CONDITION_NODE,
          childNode: undefined,
          conditionSetting: {
            defaultFlow: false,
            conditionType: ConditionType.RULE,
            conditionGroups: cloneDeep(DEFAULT_CONDITION_GROUP_VALUE)
          }
        },
        {
          id: 'Flow_' + generateUUID(),
          name: t('simpleDesigner.otherCase'),
          showText: t('simpleDesigner.otherCaseDesc'),
          type: NodeType.CONDITION_NODE,
          childNode: undefined,
          conditionSetting: {
            defaultFlow: true
          }
        }
      ]
    }
    emits('update:childNode', data)
  }
  if (type === NodeType.DELAY_TIMER_NODE) {
    const data: SimpleFlowNode = {
      id: 'Activity_' + generateUUID(),
      name: getNodeDefaultName(NodeType.DELAY_TIMER_NODE),
      showText: '',
      type: NodeType.DELAY_TIMER_NODE,
      childNode: props.childNode
    }
    emits('update:childNode', data)
  }
  if (type === NodeType.ROUTER_BRANCH_NODE) {
    const data: SimpleFlowNode = {
      id: 'GateWay_' + generateUUID(),
      name: getNodeDefaultName(NodeType.ROUTER_BRANCH_NODE),
      showText: '',
      type: NodeType.ROUTER_BRANCH_NODE,
      childNode: props.childNode
    }
    emits('update:childNode', data)
  }
  if (type === NodeType.TRIGGER_NODE) {
    const data: SimpleFlowNode = {
      id: 'Activity_' + generateUUID(),
      name: getNodeDefaultName(NodeType.TRIGGER_NODE),
      showText: '',
      type: NodeType.TRIGGER_NODE,
      childNode: props.childNode
    }
    emits('update:childNode', data)
  }
  if (type === NodeType.CHILD_PROCESS_NODE) {
    const data: SimpleFlowNode = {
      id: 'Activity_' + generateUUID(),
      name: getNodeDefaultName(NodeType.CHILD_PROCESS_NODE),
      showText: '',
      type: NodeType.CHILD_PROCESS_NODE,
      childNode: props.childNode,
      childProcessSetting: {
        calledProcessDefinitionKey: '',
        calledProcessDefinitionName: '',
        async: false,
        skipStartUserNode: false,
        startUserSetting: {
          type: 1
        },
        timeoutSetting: {
          enable: false
        },
        multiInstanceSetting: {
          enable: false
        }
      }
    }
    emits('update:childNode', data)
  }
}
</script>

<style lang="scss" scoped></style>

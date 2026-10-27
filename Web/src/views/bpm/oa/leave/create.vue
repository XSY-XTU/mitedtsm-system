<template>
  <el-row :gutter="20">
    <el-col :span="16">
      <ContentWrap :title="t('oa.leave.title') + t('common.info')">
        <el-form
          ref="formRef"
          v-loading="formLoading"
          :model="formData"
          :rules="formRules"
          label-width="80px"
        >
          <el-form-item :label="t('oa.leave.type')" prop="type">
            <el-select v-model="formData.type" clearable :placeholder="t('oa.leave.typePlaceholder')">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.BPM_OA_LEAVE_TYPE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item :label="t('oa.leave.startTime')" prop="startTime">
            <el-date-picker
              v-model="formData.startTime"
              clearable
              :placeholder="t('common.selectTime')"
              type="datetime"
              value-format="x"
            />
          </el-form-item>
          <el-form-item :label="t('oa.leave.endTime')" prop="endTime">
            <el-date-picker
              v-model="formData.endTime"
              clearable
              :placeholder="t('common.selectTime')"
              type="datetime"
              value-format="x"
            />
          </el-form-item>
          <el-form-item :label="t('oa.leave.reason')" prop="reason">
            <el-input v-model="formData.reason" :placeholder="t('oa.leave.reasonPlaceholder')" type="textarea" />
          </el-form-item>
          <el-form-item>
            <el-button :disabled="formLoading" type="primary" @click="submitForm">
              {{ t('common.ok') }}
            </el-button>
          </el-form-item>
        </el-form>
      </ContentWrap>
    </el-col>

    <!-- 审批相关：流程信息 -->
    <el-col :span="8">
      <ContentWrap :title="t('process.instance.flowDiagram')" :bodyStyle="{ padding: '0 20px 0' }">
        <ProcessInstanceTimeline
          ref="timelineRef"
          :activity-nodes="activityNodes"
          :show-status-icon="false"
          @select-user-confirm="selectUserConfirm"
        />
      </ContentWrap>
    </el-col>
  </el-row>
</template>
<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import * as LeaveApi from '@/api/bpm/leave'
import { useTagsViewStore } from '@/store/modules/tagsView'

// 审批相关：import
import * as DefinitionApi from '@/api/bpm/definition'
import ProcessInstanceTimeline from '@/views/bpm/processInstance/detail/ProcessInstanceTimeline.vue'
import * as ProcessInstanceApi from '@/api/bpm/processInstance'
import { CandidateStrategy, NodeId } from '@/components/SimpleProcessDesignerV2/src/consts'
import { ApprovalNodeInfo } from '@/api/bpm/processInstance'

defineOptions({ name: 'BpmOALeaveCreate' })

const message = useMessage() // 消息弹窗
const { t } = useI18n('bpm') // 国际化
const { delView } = useTagsViewStore() // 视图操作
const { push, currentRoute } = useRouter() // 路由
const { query } = useRoute() // 查询参数

const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formData = ref({
  type: undefined,
  reason: undefined,
  startTime: undefined,
  endTime: undefined
})
const formRules = reactive({
  type: [{ required: true, message: t('oa.leave.type') + t('common.notEmpty'), trigger: 'blur' }],
  reason: [{ required: true, message: t('oa.leave.reason') + t('common.notEmpty'), trigger: 'change' }],
  startTime: [{ required: true, message: t('oa.leave.startTime') + t('common.notEmpty'), trigger: 'change' }],
  endTime: [{ required: true, message: t('oa.leave.endTime') + t('common.notEmpty'), trigger: 'change' }]
})
const formRef = ref() // 表单 Ref

// 审批相关：变量
const processDefineKey = 'oa_leave' // 流程定义 Key
const startUserSelectTasks = ref([]) // 发起人需要选择审批人的用户任务列表
const startUserSelectAssignees = ref({}) // 发起人选择审批人的数据
const tempStartUserSelectAssignees = ref({}) // 历史发起人选择审批人的数据，用于每次表单变更时，临时保存
const activityNodes = ref<ProcessInstanceApi.ApprovalNodeInfo[]>([]) // 审批节点信息
const processDefinitionId = ref('')

/** 提交表单 */
const submitForm = async () => {
  // 1.1 校验表单
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 1.2 审批相关：校验指定审批人
  if (startUserSelectTasks.value?.length > 0) {
    for (const userTask of startUserSelectTasks.value) {
      if (
        Array.isArray(startUserSelectAssignees.value[userTask.id]) &&
        startUserSelectAssignees.value[userTask.id].length === 0
      ) {
        return message.warning(t('process.instance.selectCandidate', { name: userTask.name }))
      }
    }
  }

  // 2. 提交请求
  formLoading.value = true
  try {
    const data = { ...formData.value } as unknown as LeaveApi.LeaveVO
    // 审批相关：设置指定审批人
    if (startUserSelectTasks.value?.length > 0) {
      data.startUserSelectAssignees = startUserSelectAssignees.value
    }
    await LeaveApi.createLeave(data)
    message.success(t('process.instance.startSuccess'))
    // 关闭当前 Tab
    delView(unref(currentRoute))
    await push({ name: 'BpmOALeave' })
  } finally {
    formLoading.value = false
  }
}

/** 审批相关：获取审批详情 */
const getApprovalDetail = async () => {
  try {
    const data = await ProcessInstanceApi.getApprovalDetail({
      processDefinitionId: processDefinitionId.value,
      // TODO 小北：可以支持 processDefinitionKey 查询
      activityId: NodeId.START_USER_NODE_ID,
      processVariablesStr: JSON.stringify({ day: daysDifference() }) // 解决 GET 无法传递对象的问题，后端 String 再转 JSON
    })

    if (!data) {
      message.error(t('process.instance.queryError'))
      return
    }
    // 获取审批节点，显示 Timeline 的数据
    activityNodes.value = data.activityNodes

    // 获取发起人自选的任务
    startUserSelectTasks.value = data.activityNodes?.filter(
      (node: ApprovalNodeInfo) => CandidateStrategy.START_USER_SELECT === node.candidateStrategy
    )
    // 恢复之前的选择审批人
    if (startUserSelectTasks.value?.length > 0) {
      for (const node of startUserSelectTasks.value) {
        if (
          tempStartUserSelectAssignees.value[node.id] &&
          tempStartUserSelectAssignees.value[node.id].length > 0
        ) {
          startUserSelectAssignees.value[node.id] = tempStartUserSelectAssignees.value[node.id]
        } else {
          startUserSelectAssignees.value[node.id] = []
        }
      }
    }
  } finally {
  }
}

/** 审批相关：选择发起人 */
const selectUserConfirm = (id: string, userList: any[]) => {
  startUserSelectAssignees.value[id] = userList?.map((item: any) => item.id)
}

// 计算天数差
// TODO @小北：可以搞到 formatTime 里面去，然后看看 dayjs 里面有没有现成的方法，或者辅助计算的方法。
const daysDifference = () => {
  const oneDay = 24 * 60 * 60 * 1000 // 一天的毫秒数
  const diffTime = Math.abs(Number(formData.value.endTime) - Number(formData.value.startTime))
  return Math.floor(diffTime / oneDay)
}

/** 获取请假数据，用于重新发起时自动填充 */
const getDetail = async (id: number) => {
  try {
    formLoading.value = true
    const data = await LeaveApi.getLeave(id)
    if (!data) {
      message.error(t('oa.leave.restartFailed') + '：' + t('oa.leave.dataNotFound'))
      return
    }
    formData.value = {
      type: data.type,
      reason: data.reason,
      startTime: data.startTime,
      endTime: data.endTime
    }
  } finally {
    formLoading.value = false
  }
}

/** 初始化 */
onMounted(async () => {
  // TODO @小北：这里可以简化，统一通过 getApprovalDetail 处理么？
  const processDefinitionDetail = await DefinitionApi.getProcessDefinition(
    undefined,
    processDefineKey
  )

  if (!processDefinitionDetail) {
    message.error(t('oa.leave.processModelNotFound'))
    return
  }
  processDefinitionId.value = processDefinitionDetail.id
  startUserSelectTasks.value = processDefinitionDetail.startUserSelectTasks

  // 如果有业务编号，说明是重新发起，需要加载原有数据
  if (query.id) {
    await getDetail(Number(query.id))
  }

  // 审批相关：加载最新的审批详情，主要用于节点预测
  await getApprovalDetail()
})

/** 审批相关：预测流程节点会因为输入的参数值而产生新的预测结果值，所以需重新预测一次, formData.value可改成实际业务中的特定字段 */
watch(
  formData.value,
  (newValue, oldValue) => {
    if (!oldValue) {
      return
    }
    if (newValue && Object.keys(newValue).length > 0) {
      // 记录之前的节点审批人
      tempStartUserSelectAssignees.value = startUserSelectAssignees.value
      startUserSelectAssignees.value = {}
      // 加载最新的审批详情,主要用于节点预测
      getApprovalDetail()
    }
  },
  {
    immediate: true
  }
)
</script>

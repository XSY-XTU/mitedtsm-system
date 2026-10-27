<template>
  <el-drawer
    :append-to-body="true"
    v-model="settingVisible"
    :show-close="false"
    :size="550"
    :before-close="saveConfig"
  >
    <template #header>
      <div class="config-header">
        <input
          v-if="showInput"
          type="text"
          class="config-editable-input"
          @blur="blurEvent()"
          v-mountedFocus
          v-model="nodeName"
          :placeholder="nodeName"
        />
        <div v-else class="node-name">
          {{ nodeName }} <Icon class="ml-1" icon="ep:edit-pen" :size="16" @click="clickIcon()" />
        </div>
        <div class="divide-line"></div>
      </div>
    </template>
    <el-tabs type="border-card" v-model="activeTabName">
      <el-tab-pane :label="t('simpleDesigner.permission')" name="user">
        <el-text
          v-if="
            (!startUserIds || startUserIds.length === 0) &&
            (!startDeptIds || startDeptIds.length === 0)
          "
        >
          {{ t('simpleDesigner.allMemberCanStart') }}
        </el-text>
        <div v-else-if="startUserIds && startUserIds.length > 0">
          <el-text v-if="startUserIds.length == 1">
            {{ getUserNicknames(startUserIds) }} {{ t('simpleDesigner.canStartProcess') }}
          </el-text>
          <el-text v-else>
            <el-tooltip
              class="box-item"
              effect="dark"
              placement="top"
              :content="getUserNicknames(startUserIds)"
            >
              {{ getUserNicknames(startUserIds.slice(0, 2)) }} {{ t('simpleDesigner.gotIt') }}
              {{ startUserIds.length }} {{ t('simpleDesigner.peopleCanStart') }}
            </el-tooltip>
          </el-text>
        </div>
        <div v-else-if="startDeptIds && startDeptIds.length > 0">
          <el-text v-if="startDeptIds.length == 1">
            {{ getDeptNames(startDeptIds) }} {{ t('simpleDesigner.canStartProcess') }}
          </el-text>
          <el-text v-else>
            <el-tooltip
              class="box-item"
              effect="dark"
              placement="top"
              :content="getDeptNames(startDeptIds)"
            >
              {{ getDeptNames(startDeptIds.slice(0, 2)) }} {{ t('simpleDesigner.gotIt') }}
              {{ startDeptIds.length }} {{ t('simpleDesigner.deptCanStart') }}
            </el-tooltip>
          </el-text>
        </div>
      </el-tab-pane>
      <el-tab-pane :label="t('simpleDesigner.fieldPermissionTitle')" name="fields" v-if="formType === 10">
        <div class="field-setting-pane">
          <div class="field-setting-desc">{{ t('simpleDesigner.fieldPermission') }}</div>
          <div class="field-permit-title">
            <div class="setting-title-label first-title"> {{ t('simpleDesigner.fieldName') }} </div>
            <div class="other-titles">
              <span class="setting-title-label cursor-pointer" @click="updatePermission('READ')">
                {{ t('simpleDesigner.readOnly') }}
              </span>
              <span class="setting-title-label cursor-pointer" @click="updatePermission('WRITE')">
                {{ t('simpleDesigner.editable') }}
              </span>
              <span class="setting-title-label cursor-pointer" @click="updatePermission('NONE')">
                {{ t('simpleDesigner.hidden') }}
              </span>
            </div>
          </div>
          <div
            class="field-setting-item"
            v-for="(item, index) in fieldsPermissionConfig"
            :key="index"
          >
            <div class="field-setting-item-label"> {{ item.title }} </div>
            <el-radio-group class="field-setting-item-group" v-model="item.permission">
              <div class="item-radio-wrap">
                <el-radio
                  :value="FieldPermissionType.READ"
                  size="large"
                  :label="FieldPermissionType.READ"
                  ><span></span
                ></el-radio>
              </div>
              <div class="item-radio-wrap">
                <el-radio
                  :value="FieldPermissionType.WRITE"
                  size="large"
                  :label="FieldPermissionType.WRITE"
                  ><span></span
                ></el-radio>
              </div>
              <div class="item-radio-wrap">
                <el-radio
                  :value="FieldPermissionType.NONE"
                  size="large"
                  :label="FieldPermissionType.NONE"
                  ><span></span
                ></el-radio>
              </div>
            </el-radio-group>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
    <template #footer>
      <el-divider />
      <div>
        <el-button type="primary" @click="saveConfig">{{ t('simpleDesigner.confirm') }}</el-button>
        <el-button @click="closeDrawer">{{ t('simpleDesigner.cancel') }}</el-button>
      </div>
    </template>
  </el-drawer>
</template>
<script setup lang="ts">
import { useI18n } from '@/hooks/web/useI18n'
import { SimpleFlowNode, NodeType, FieldPermissionType, START_USER_BUTTON_SETTING } from '../consts'
import { useWatchNode, useDrawer, useNodeName, useFormFieldsPermission } from '../node'
import * as UserApi from '@/api/system/user'
import * as DeptApi from '@/api/system/dept'

const { t } = useI18n('bpm')
defineOptions({
  name: 'StartUserNodeConfig'
})
const props = defineProps({
  flowNode: {
    type: Object as () => SimpleFlowNode,
    required: true
  }
})
// 可发起流程的用户编号
const startUserIds = inject<Ref<any[]>>('startUserIds')
// 可发起流程的部门编号
const startDeptIds = inject<Ref<any[]>>('startDeptIds')
// 用户列表
const userOptions = inject<Ref<UserApi.UserVO[]>>('userList')
// 部门列表
const deptOptions = inject<Ref<DeptApi.DeptVO[]>>('deptList')
// 抽屉配置
const { settingVisible, closeDrawer, openDrawer } = useDrawer()
// 当前节点
const currentNode = useWatchNode(props)
// 节点名称
const { nodeName, showInput, clickIcon, blurEvent } = useNodeName(NodeType.COPY_TASK_NODE)
// 激活的 Tab 标签页
const activeTabName = ref('user')
// 表单字段权限配置
const { formType, fieldsPermissionConfig, getNodeConfigFormFields } = useFormFieldsPermission(
  FieldPermissionType.WRITE
)
const getUserNicknames = (userIds: number[]): string => {
  if (!userIds || userIds.length === 0) {
    return ''
  }
  const nicknames: string[] = []
  userIds.forEach((userId) => {
    const found = userOptions?.value.find((item) => item.id === userId)
    if (found && found.nickname) {
      nicknames.push(found.nickname)
    }
  })
  return nicknames.join(',')
}
const getDeptNames = (deptIds: number[]): string => {
  if (!deptIds || deptIds.length === 0) {
    return ''
  }
  const deptNames: string[] = []
  deptIds.forEach((deptId) => {
    const found = deptOptions?.value.find((item) => item.id === deptId)
    if (found && found.name) {
      deptNames.push(found.name)
    }
  })
  return deptNames.join(',')
}
// 保存配置
const saveConfig = async () => {
  activeTabName.value = 'user'
  currentNode.value.name = nodeName.value!
  currentNode.value.showText = t('simpleDesigner.configured')
  // 设置表单权限
  currentNode.value.fieldsPermission = fieldsPermissionConfig.value
  // 设置发起人的按钮权限
  currentNode.value.buttonsSetting = START_USER_BUTTON_SETTING
  settingVisible.value = false
  return true
}
// 显示发起人节点配置， 由父组件传过来
const showStartUserNodeConfig = (node: SimpleFlowNode) => {
  nodeName.value = node.name
  // 表单字段权限
  getNodeConfigFormFields(node.fieldsPermission)
}

/** 批量更新权限 */
const updatePermission = (type: string) => {
  fieldsPermissionConfig.value.forEach((field) => {
    field.permission =
      type === 'READ'
        ? FieldPermissionType.READ
        : type === 'WRITE'
          ? FieldPermissionType.WRITE
          : FieldPermissionType.NONE
  })
}
defineExpose({ openDrawer, showStartUserNodeConfig }) // 暴露方法给父组件
</script>

<style lang="scss" scoped></style>

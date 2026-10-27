<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="auto"
      v-loading="formLoading"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('user.mobile')" prop="mobile">
            <el-input v-model="formData.mobile" :placeholder="t('user.mobilePlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('user.status')" prop="status">
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
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('user.nickname')" prop="nickname">
            <el-input v-model="formData.nickname" :placeholder="t('user.nicknamePlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('user.avatar')" prop="avatar">
            <UploadImg v-model="formData.avatar" :limit="1" :is-show-tip="false" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('user.realName')" prop="name">
            <el-input v-model="formData.name" :placeholder="t('user.realNamePlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('user.sex')" prop="sex">
            <el-radio-group v-model="formData.sex">
              <el-radio
                v-for="dict in getIntDictOptions(DICT_TYPE.SYSTEM_USER_SEX)"
                :key="dict.value"
                :value="dict.value"
              >
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('user.birthday')" prop="birthday">
            <el-date-picker
              v-model="formData.birthday"
              type="date"
              value-format="x"
              :placeholder="t('user.birthdayPlaceholder')"
              class="w-1/1"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('user.area')" prop="areaId">
            <el-tree-select
              v-model="formData.areaId"
              :data="areaList"
              :props="defaultProps"
              :render-after-expand="true"
              class="w-1/1"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('user.userTag')" prop="tagIds">
            <MemberTagSelect v-model="formData.tagIds" show-add />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('user.userGroup')" prop="groupId">
            <MemberGroupSelect v-model="formData.groupId" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item :label="t('user.memberRemark')" prop="mark">
            <el-input type="textarea" v-model="formData.mark" :placeholder="t('user.memberRemarkPlaceholder')" />
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
import * as UserApi from '@/api/member/user'
import * as AreaApi from '@/api/system/area'
import { defaultProps } from '@/utils/tree'
import MemberTagSelect from '@/views/member/tag/components/MemberTagSelect.vue'
import MemberGroupSelect from '@/views/member/group/components/MemberGroupSelect.vue'
import { useI18n } from '@/hooks/web/useI18n'

const { t } = useI18n('member') // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  mobile: undefined,
  password: undefined,
  status: undefined,
  nickname: undefined,
  avatar: undefined,
  name: undefined,
  sex: undefined,
  areaId: undefined,
  birthday: undefined,
  mark: undefined,
  tagIds: [],
  groupId: undefined
})
const formRules = reactive({
  mobile: [{ required: true, message: t('user.mobilePlaceholder'), trigger: 'blur' }],
  status: [{ required: true, message: t('common.selectPlaceholder'), trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref
const areaList = ref([]) // 地区列表

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
      formData.value = await UserApi.getUser(id)
    } finally {
      formLoading.value = false
    }
  }
  // 获得地区列表
  areaList.value = await AreaApi.getAreaTree()
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
    const data = formData.value as unknown as UserApi.UserVO
    if (formType.value === 'create') {
      // 说明：目前暂时没有新增操作。如果自己业务需要，可以进行扩展
      // await UserApi.createUser(data)
      message.success(t('common.createSuccess'))
    } else {
      await UserApi.updateUser(data)
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
    mobile: undefined,
    password: undefined,
    status: undefined,
    nickname: undefined,
    avatar: undefined,
    name: undefined,
    sex: undefined,
    areaId: undefined,
    birthday: undefined,
    mark: undefined,
    tagIds: [],
    groupId: undefined
  }
  formRef.value?.resetFields()
}
</script>
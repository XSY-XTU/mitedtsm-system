<template>
  <doc-alert title="会员用户、标签、分组" url="https://doc.iocoder.cn/member/user/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      class="-mb-15px"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('user.nickname')" prop="nickname">
            <el-input
              v-model="queryParams.nickname"
              class="!w-240px"
              clearable
              :placeholder="t('user.nicknamePlaceholder')"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('user.mobile')" prop="mobile">
            <el-input
              v-model="queryParams.mobile"
              class="!w-240px"
              clearable
              :placeholder="t('user.mobilePlaceholder')"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('user.registerTime')" prop="createTime">
            <el-date-picker
              v-model="queryParams.createTime"
              :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
              class="!w-240px"
              :end-placeholder="t('common.endTime')"
              :start-placeholder="t('common.startTime')"
              type="daterange"
              value-format="YYYY-MM-DD HH:mm:ss"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('user.loginTime')" prop="loginDate">
            <el-date-picker
              v-model="queryParams.loginDate"
              :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
              class="!w-240px"
              :end-placeholder="t('common.endTime')"
              :start-placeholder="t('common.startTime')"
              type="daterange"
              value-format="YYYY-MM-DD HH:mm:ss"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('user.tag')" prop="tagIds">
            <MemberTagSelect v-model="queryParams.tagIds" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('user.level')" prop="levelId">
            <MemberLevelSelect v-model="queryParams.levelId" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('user.group')" prop="groupId">
            <MemberGroupSelect v-model="queryParams.groupId" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button @click="handleQuery">
              <Icon class="mr-5px" icon="ep:search" />
              {{ t('common.query') }}
            </el-button>
            <el-button @click="resetQuery">
              <Icon class="mr-5px" icon="ep:refresh" />
              {{ t('common.reset') }}
            </el-button>
            <el-button v-hasPermi="['promotion:coupon:send']" @click="openCoupon">{{ t('user.sendCoupon') }}</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table
      v-loading="loading"
      :data="list"
      :show-overflow-tooltip="true"
      :stripe="true"
      @selection-change="handleSelectionChange"
     :table-layout="'auto'">
      <el-table-column type="selection" width="55" />
      <el-table-column align="center" :label="t('user.id')" prop="id" min-width="120" />
      <el-table-column align="center" :label="t('user.avatar')" prop="avatar" min-width="80">
        <template #default="scope">
          <img :src="scope.row.avatar" style="width: 40px" />
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('user.mobile')" prop="mobile" min-width="120" />
      <el-table-column align="center" :label="t('user.nickname')" prop="nickname" min-width="80" />
      <el-table-column align="center" :label="t('user.level')" prop="levelName" min-width="100" />
      <el-table-column align="center" :label="t('user.group')" prop="groupName" min-width="100" />
      <el-table-column
        :show-overflow-tooltip="false"
        align="center"
        :label="t('user.tag')"
        prop="tagNames"
      >
        <template #default="scope">
          <el-tag v-for="(tagName, index) in scope.row.tagNames" :key="index" class="mr-5px">
            {{ tagName }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('user.point')" prop="point" min-width="100" />
      <el-table-column align="center" :label="t('user.status')" prop="status" min-width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('user.loginTime')"
        prop="loginDate"
        min-width="180"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('user.registerTime')"
        prop="createTime"
        min-width="180"
      />
      <el-table-column
        :show-overflow-tooltip="false"
        align="center"
        fixed="right"
        :label="t('common.action')"
        min-width="100"
      >
        <template #default="scope">
          <div class="flex items-center justify-center">
            <el-button link type="primary" @click="openDetail(scope.row.id)">{{ t('common.detail') }}</el-button>
            <el-dropdown
              v-hasPermi="[
                'member:user:update',
                'member:user:update-level',
                'member:user:update-point',
                'pay:wallet:update-balance'
              ]"
              @command="(command) => handleCommand(command, scope.row)"
            >
              <el-button link type="primary">
                <Icon icon="ep:d-arrow-right" />
                {{ t('common.more') }}
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item
                    v-if="checkPermi(['member:user:update'])"
                    command="handleUpdate"
                  >
                    {{ t('common.edit') }}
                  </el-dropdown-item>
                  <el-dropdown-item
                    v-if="checkPermi(['member:user:update-level'])"
                    command="handleUpdateLevel"
                  >
                    {{ t('user.updateLevel') }}
                  </el-dropdown-item>
                  <el-dropdown-item
                    v-if="checkPermi(['member:user:update-point'])"
                    command="handleUpdatePoint"
                  >
                    {{ t('user.updatePoint') }}
                  </el-dropdown-item>
                  <el-dropdown-item
                    v-if="checkPermi(['pay:wallet:update-balance'])"
                    command="handleUpdateBlance"
                  >
                    {{ t('user.updateBalance') }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <UserForm ref="formRef" @success="getList" />
  <!-- 修改用户等级弹窗 -->
  <UserLevelUpdateForm ref="updateLevelFormRef" @success="getList" />
  <!-- 修改用户积分弹窗 -->
  <UserPointUpdateForm ref="updatePointFormRef" @success="getList" />
  <!-- 修改用户余额弹窗 -->
  <UserBalanceUpdateForm ref="UpdateBalanceFormRef" @success="getList" />
  <!-- 发送优惠券弹窗 -->
  <CouponSendForm ref="couponSendFormRef" />
</template>
<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as UserApi from '@/api/member/user'
import { DICT_TYPE } from '@/utils/dict'
import UserForm from './UserForm.vue'
import MemberTagSelect from '@/views/member/tag/components/MemberTagSelect.vue'
import MemberLevelSelect from '@/views/member/level/components/MemberLevelSelect.vue'
import MemberGroupSelect from '@/views/member/group/components/MemberGroupSelect.vue'
import UserLevelUpdateForm from './components/UserLevelUpdateForm.vue'
import UserPointUpdateForm from './components/UserPointUpdateForm.vue'
import UserBalanceUpdateForm from './components/UserBalanceUpdateForm.vue'
import { CouponSendForm } from '@/views/mall/promotion/coupon/components'
import { checkPermi } from '@/utils/permission'
import { useI18n } from '@/hooks/web/useI18n'

defineOptions({ name: 'MemberUser' })

const { t } = useI18n('member')
const message = useMessage() // 消息弹窗

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  nickname: null,
  mobile: null,
  loginDate: [],
  createTime: [],
  tagIds: [],
  levelId: null,
  groupId: null
})
const queryFormRef = ref() // 搜索的表单
const updateLevelFormRef = ref() // 修改会员等级表单
const updatePointFormRef = ref() // 修改会员积分表单
const UpdateBalanceFormRef = ref() // 修改用户余额表单
const selectedIds = ref<number[]>([]) // 表格的选中 ID 数组

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await UserApi.getUserPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 打开会员详情 */
const { push } = useRouter()
const openDetail = (id: number) => {
  push({ name: 'MemberUserDetail', params: { id } })
}

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 表格选中事件 */
const handleSelectionChange = (rows: UserApi.UserVO[]) => {
  selectedIds.value = rows.map((row) => row.id)
}

/** 发送优惠券 */
const couponSendFormRef = ref()
const openCoupon = () => {
  if (selectedIds.value.length === 0) {
    message.warning(t('user.selectUserForCoupon'))
    return
  }
  couponSendFormRef.value.open(selectedIds.value)
}

/** 操作分发 */
const handleCommand = (command: string, row: UserApi.UserVO) => {
  switch (command) {
    case 'handleUpdate':
      openForm('update', row.id)
      break
    case 'handleUpdateLevel':
      updateLevelFormRef.value.open(row.id)
      break
    case 'handleUpdatePoint':
      updatePointFormRef.value.open(row.id)
      break
    case 'handleUpdateBlance':
      UpdateBalanceFormRef.value.open(row.id)
      break
    default:
      break
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

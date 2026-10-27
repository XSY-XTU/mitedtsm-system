<template>
  <doc-alert title="公众号粉丝" url="https://doc.iocoder.cn/mp/user/" />

  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('account.title')" prop="accountId">
            <WxAccountSelect @change="onAccountChanged" :modelValue="queryParams.accountId" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('user.openid')" prop="openid">
            <el-input
              v-model="queryParams.openid"
              :placeholder="t('user.openidPlaceholder')"
              clearable
              @keyup.enter="handleQuery"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('user.nickname')" prop="nickname">
            <el-input
              v-model="queryParams.nickname"
              :placeholder="t('user.nicknamePlaceholder')"
              clearable
              @keyup.enter="handleQuery"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button @click="handleQuery"> <Icon icon="ep:search" />{{ t('common.query') }} </el-button>
            <el-button @click="resetQuery"> <Icon icon="ep:refresh" />{{ t('common.reset') }} </el-button>
            <el-button
              type="success"
              plain
              @click="handleSync"
              v-hasPermi="['mp:user:sync']"
              :disabled="queryParams.accountId === 0"
            >
              <Icon icon="ep:refresh" class="mr-5px" /> {{ t('common.refresh') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange" :table-layout="'auto'">
      <el-table-column type="selection" width="55" v-if="isDialog"/>
      <el-table-column :label="t('common.id')" align="center" prop="id" />
      <el-table-column :label="t('user.openid')" align="center" prop="openid" min-width="260" />
      <el-table-column :label="t('user.avatar')" min-width="80px" prop="headImageUrl">
        <template #default="scope">
          <el-avatar :src="scope.row.headImageUrl"/>
        </template>
      </el-table-column>
      <el-table-column :label="t('user.nickname')" align="center" prop="nickname" />
      <el-table-column :label="t('user.remark')" align="center" prop="remark" />
      <el-table-column :label="t('user.tagIds')" align="center" prop="tagIds" min-width="200">
        <template #default="scope">
          <span v-for="(tagId, index) in scope.row.tagIds" :key="index">
            <el-tag>{{ tagList.find((tag) => tag.tagId === tagId)?.name }} </el-tag>&nbsp;
          </span>
        </template>
      </el-table-column>
      <el-table-column :label="t('user.isSubscribe')" align="center" prop="subscribeStatus">
        <template #default="scope">
          <el-tag v-if="scope.row.subscribeStatus === 0" type="success">{{ t('user.isSubscribeYes') }}</el-tag>
          <el-tag v-else type="danger">{{ t('user.isSubscribeNo') }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        :label="t('user.subscribeTime')"
        align="center"
        prop="subscribeTime"
        min-width="180"
        :formatter="dateFormatter"
       fixed="right" />
      <el-table-column :label="t('common.operation')" align="center">
        <template #default="scope">
          <el-button
            type="primary"
            link
            @click="openForm(scope.row.id)"
            v-hasPermi="['mp:user:update']"
          >
            {{ t('common.edit') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗：修改 -->
  <UserForm ref="formRef" @success="getList" />
</template>
<script lang="ts" setup>
import {dateFormatter} from '@/utils/formatTime'
import * as MpUserApi from '@/api/mp/user'
import * as MpTagApi from '@/api/mp/tag'
import WxAccountSelect from '@/views/mp/components/wx-account-select'
import type {FormInstance} from 'element-plus'
import UserForm from './UserForm.vue'
import {ref} from "vue";

defineOptions({ name: 'MpUser' })

const { t } = useI18n('mp') // 国际化
const message = useMessage() // 消息

const isDialog = ref(false) // 是不是弹窗调用
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref<any[]>([]) // 列表的数据
const multipleSelection = ref<String[]>([])

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  accountId: -1,
  openid: '',
  nickname: ''
})
const queryFormRef = ref<FormInstance | null>(null) // 搜索的表单
const tagList = ref<any[]>([]) // 公众号标签列表

/** 侦听公众号变化 **/
const onAccountChanged = (id: number) => {
  queryParams.accountId = id
  queryParams.pageNo = 1
  getList()
}

/** 查询列表 */
const getList = async () => {
  try {
    multipleSelection.value = []
    loading.value = true
    const data = await MpUserApi.getUserPage(queryParams)
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
  if(isDialog.value){
    emitChange()
  }
}

/** 重置按钮操作 */
const resetQuery = () => {
  const accountId = queryParams.accountId
  queryFormRef.value?.resetFields()
  queryParams.accountId = accountId
  handleQuery()
}

/** 添加/修改操作 */
const formRef = ref<InstanceType<typeof UserForm> | null>(null)
const openForm = (id: number) => {
  formRef.value?.open(id)
}

/** 同步标签 */
const handleSync = async () => {
  try {
    await message.confirm(t('user.confirmSync'))
    await MpUserApi.syncUser(queryParams.accountId)
    message.success(t('user.syncSuccess'))
    await getList()
  } catch {}
}

/** Expose*/
defineExpose({
  open: (accountId: number) => {
    onAccountChanged(accountId)
    isDialog.value = true
  }
});

/** Emits*/
interface Emits {
  (e: 'change', data: {
    multipleSelection: any[]
    total: number
    queryParams: object
  }): void
  // (e: 'select', user: any): void
  // (e: 'cancel'): void
}
const emit = defineEmits<Emits>()
const emitChange = () => {
  emit('change', {multipleSelection: multipleSelection.value, total: total.value, queryParams})
}

const handleSelectionChange = (val: any[]) => {
  multipleSelection.value = val
  if (isDialog.value) {
    emitChange()
  }
}

/** 初始化 */
onMounted(async () => {
  tagList.value = await MpTagApi.getSimpleTagList()
})
</script>

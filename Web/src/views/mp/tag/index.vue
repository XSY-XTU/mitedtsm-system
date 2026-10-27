<template>
  <doc-alert title="公众号标签" url="https://doc.iocoder.cn/mp/tag/" />

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
            <WxAccountSelect @change="onAccountChanged" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button
              type="primary"
              plain
              @click="openForm('create')"
              v-hasPermi="['mp:tag:create']"
              :disabled="queryParams.accountId === 0"
            >
              <Icon icon="ep:plus" class="mr-5px" /> {{ t('common.add') }}
            </el-button>
            <el-button
              type="success"
              plain
              @click="handleSync"
              v-hasPermi="['mp:tag:sync']"
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
    <el-table v-loading="loading" :data="list" :table-layout="'auto'">
      <el-table-column :label="t('common.id')" align="center" prop="id"  fixed="right" />
      <el-table-column :label="t('tag.name')" align="center" prop="name" />
      <el-table-column :label="t('tag.count')" align="center" prop="count" />
      <el-table-column
        :label="t('common.createTime')"
        align="center"
        prop="createTime"
        min-width="180"
        :formatter="dateFormatter"
      />
      <el-table-column :label="t('common.operation')" align="center">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['mp:tag:update']"
          >
            {{ t('common.edit') }}
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['mp:tag:delete']"
          >
            {{ t('common.delete') }}
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

  <!-- 表单弹窗：添加/修改 -->
  <TagForm ref="formRef" @success="getList" />
</template>
<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as MpTagApi from '@/api/mp/tag'
import TagForm from './TagForm.vue'
import WxAccountSelect from '@/views/mp/components/wx-account-select'

defineOptions({ name: 'MpTag' })

const message = useMessage() // 消息弹窗
const { t } = useI18n('mp') // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref<any[]>([]) // 列表的数据

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  accountId: -1
})

const formRef = ref<InstanceType<typeof TagForm> | null>(null)

/** 侦听公众号变化 **/
const onAccountChanged = (id: number) => {
  queryParams.accountId = id
  queryParams.pageNo = 1
  getList()
}

/** 查询列表 */
const getList = async () => {
  try {
    loading.value = true
    const data = await MpTagApi.getTagPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 添加/修改操作 */
const openForm = (type: 'create' | 'update', id?: number) => {
  formRef.value?.open(type, queryParams.accountId, id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await MpTagApi.deleteTag(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {
    //
  }
}

/** 同步操作 */
const handleSync = async () => {
  try {
    await message.confirm(t('tag.confirmSync'))
    await MpTagApi.syncTag(queryParams.accountId as number)
    message.success(t('tag.syncSuccess'))
    await getList()
  } catch {
    //
  }
}
</script>

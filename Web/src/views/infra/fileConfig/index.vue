<template>
  <doc-alert :title="t('file.docTitle')" url="https://doc.iocoder.cn/file/" />

  <!-- 搜索 -->
  <ContentWrap>
    <el-form
      class="-mb-15px"
      label-width="auto"
      :model="queryParams"
      ref="queryFormRef"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('fileConfig.name')" prop="name">
        <el-input
          v-model="queryParams.name"
          :placeholder="t('fileConfig.namePlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('fileConfig.storage')" prop="storage">
        <el-select
          v-model="queryParams.storage"
          :placeholder="t('common.selectPlaceholder')"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.INFRA_FILE_STORAGE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('common.createTime')" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          :start-placeholder="t('common.startTime')"
          :end-placeholder="t('common.endTime')"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-240px"
        />
      </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> {{ t('common.query') }}</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> {{ t('common.reset') }}</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['infra:file-config:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> {{ t('common.add') }}
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="checkedIds.length === 0"
          @click="handleDeleteBatch"
          v-hasPermi="['infra:file-config:delete']"
        >
          <Icon icon="ep:delete" class="mr-5px" /> {{ t('common.batchDelete') }}
        </el-button>
      </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" @selection-change="handleRowCheckboxChange" :table-layout="'auto'">
      <el-table-column type="selection" width="55" />
      <el-table-column :label="t('common.id')" align="center" prop="id" />
      <el-table-column :label="t('fileConfig.name')" align="center" prop="name" />
      <el-table-column :label="t('fileConfig.storage')" align="center" prop="storage">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.INFRA_FILE_STORAGE" :value="scope.row.storage" />
        </template>
      </el-table-column>
      <el-table-column :label="t('common.remark')" align="center" prop="remark" />
      <el-table-column :label="t('fileConfig.primary')" align="center" prop="primary">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="scope.row.master" />
        </template>
      </el-table-column>
      <el-table-column
        :label="t('common.createTime')"
        align="center"
        prop="createTime"
        min-width="180"
        :formatter="dateFormatter"
       fixed="right" />
      <el-table-column :label="t('common.operation')" align="center" min-width="240">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['infra:file-config:update']"
          >
            {{ t('common.edit') }}
          </el-button>
          <el-button
            link
            type="primary"
            :disabled="scope.row.master"
            @click="handleMaster(scope.row.id)"
            v-hasPermi="['infra:file-config:update']"
          >
            {{ t('fileConfig.setPrimary') }}
          </el-button>
          <el-button link type="primary" @click="handleTest(scope.row.id)"> {{ t('fileConfig.test') }} </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['infra:file-config:delete']"
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
  <FileConfigForm ref="formRef" @success="getList" />
</template>
<script lang="ts" setup>
import { useI18n } from '@/hooks/web/useI18n'
import * as FileConfigApi from '@/api/infra/fileConfig'
import FileConfigForm from './FileConfigForm.vue'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'

defineOptions({ name: 'InfraFileConfig' })

const { t } = useI18n('infra')
const message = useMessage() // 消息弹窗

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  storage: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await FileConfigApi.getFileConfigPage(queryParams)
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

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await FileConfigApi.deleteFileConfig(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 批量删除按钮操作 */
const checkedIds = ref<number[]>([])
const handleRowCheckboxChange = (rows) => {
  checkedIds.value = rows.map((row) => row.id)
}

const handleDeleteBatch = async () => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起批量删除
    await FileConfigApi.deleteFileConfigList(checkedIds.value)
    checkedIds.value = []
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 主配置按钮操作 */
const handleMaster = async (id) => {
  try {
    await message.confirm(t('fileConfig.confirmPrimary', { id: id }))
    await FileConfigApi.updateFileConfigMaster(id)
    message.success(t('common.updateSuccess'))
    await getList()
  } catch {}
}

/** 测试按钮操作 */
const handleTest = async (id) => {
  try {
    const response = await FileConfigApi.testFileConfig(id)
    await message.confirm(t('fileConfig.confirmVisit'), t('fileConfig.testSuccess'))
    window.open(response, '_blank')
  } catch {}
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

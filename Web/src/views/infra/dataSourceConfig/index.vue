<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form class="-mb-15px" label-width="auto">
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button
              type="primary"
              plain
              @click="openForm('create')"
              v-hasPermi="['infra:data-source-config:create']"
            >
              <Icon icon="ep:plus" class="mr-5px" /> {{ t('common.add') }}
            </el-button>
            <el-button
              type="danger"
              plain
              :disabled="checkedIds.length === 0"
              @click="handleDeleteBatch"
              v-hasPermi="['infra:data-source-config:delete']"
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
      <el-table-column :label="t('datasource.name')" align="center" prop="name" />
      <el-table-column :label="t('datasource.url')" align="center" prop="url" :show-overflow-tooltip="true" />
      <el-table-column :label="t('datasource.username')" align="center" prop="username" />
      <el-table-column
        :label="t('common.createTime')"
        align="center"
        prop="createTime"
        width="180"
        :formatter="dateFormatter"
      />
      <el-table-column :label="t('common.operation')" align="center">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['infra:data-source-config:update']"
            :disabled="scope.row.id === 0"
          >
            {{ t('common.edit') }}
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['infra:data-source-config:delete']"
            :disabled="scope.row.id === 0"
          >
            {{ t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <DataSourceConfigForm ref="formRef" @success="getList" />
</template>
<script lang="ts" setup>
import { useI18n } from '@/hooks/web/useI18n'
import { dateFormatter } from '@/utils/formatTime'
import * as DataSourceConfigApi from '@/api/infra/dataSourceConfig'
import DataSourceConfigForm from './DataSourceConfigForm.vue'

defineOptions({ name: 'InfraDataSourceConfig' })

const { t } = useI18n('infra')
const message = useMessage() // 消息弹窗

const loading = ref(true) // 列表的加载中
const list = ref([]) // 列表的数据

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    list.value = await DataSourceConfigApi.getDataSourceConfigList()
  } finally {
    loading.value = false
  }
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
    await DataSourceConfigApi.deleteDataSourceConfig(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 批量删除按钮操作 */
const checkedIds = ref<number[]>([])
const handleRowCheckboxChange = (rows: DataSourceConfigApi.DataSourceConfigVO[]) => {
  // 过滤掉id为 0 的主数据源
  checkedIds.value = rows.map((row) => row.id!).filter((id) => id !== 0 && Boolean(id))
}

const handleDeleteBatch = async () => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起批量删除
    await DataSourceConfigApi.deleteDataSourceConfigList(checkedIds.value)
    checkedIds.value = []
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

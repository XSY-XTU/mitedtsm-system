<template>
  <doc-alert title="AI 手册" url="https://doc.iocoder.cn/ai/build/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="tAi('chat.model.modelName')" prop="name">
            <el-input
              v-model="queryParams.name"
              :placeholder="t('common.inputText')"
              clearable
              @keyup.enter="handleQuery"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="tAi('chat.model.modelId')" prop="model">
            <el-input
              v-model="queryParams.model"
              :placeholder="t('common.inputText')"
              clearable
              @keyup.enter="handleQuery"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="tAi('chat.model.platform')" prop="platform">
            <el-input
              v-model="queryParams.platform"
              :placeholder="t('common.inputText')"
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
            <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> {{ t('common.search') }}</el-button>
            <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> {{ t('common.reset') }}</el-button>
            <el-button
              type="primary"
              plain
              @click="openForm('create')"
              v-hasPermi="['ai:model:create']"
            >
              <Icon icon="ep:plus" class="mr-5px" /> {{ t('common.add') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true" :table-layout="'auto'">
      <el-table-column :label="tAi('chat.model.platform')" align="center" prop="platform" min-width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.AI_PLATFORM" :value="scope.row.platform" />
        </template>
      </el-table-column>
      <el-table-column :label="tAi('chat.model.modelType')" align="center" prop="platform" min-width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.AI_MODEL_TYPE" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column :label="tAi('chat.model.modelName')" align="center" prop="name" min-width="180" />
      <el-table-column :label="tAi('chat.model.modelId')" align="center" prop="model" min-width="180" />
      <el-table-column :label="tAi('chat.model.apiKey')" align="center" prop="keyId" min-width="140">
        <template #default="scope">
          <span>{{ apiKeyList.find((item) => item.id === scope.row.keyId)?.name }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="t('common.sort')" align="center" prop="sort" min-width="80" />
      <el-table-column :label="t('common.status')" align="center" prop="status" min-width="80">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column :label="tAi('chat.model.temperature')" align="center" prop="temperature" min-width="80" />
      <el-table-column :label="tAi('chat.model.maxTokens')" align="center" prop="maxTokens" min-width="140" />
      <el-table-column :label="tAi('chat.model.maxContexts')" align="center" prop="maxContexts" min-width="100" />
      <el-table-column :label="t('common.operation')" align="center" min-width="180" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['ai:model:update']"
          >
            {{ t('common.edit') }}
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['ai:model:delete']"
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
  <ModelForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { ModelApi, ModelVO } from '@/api/ai/model/model'
import ModelForm from './ModelForm.vue'
import { DICT_TYPE } from '@/utils/dict'
import { ApiKeyApi, ApiKeyVO } from '@/api/ai/model/apiKey'

/** API 模型列表 */
defineOptions({ name: 'AiModel' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const tAi = useI18n('ai').t

const loading = ref(true) // 列表的加载中
const list = ref<ModelVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  model: undefined,
  platform: undefined
})
const queryFormRef = ref() // 搜索的表单
const apiKeyList = ref([] as ApiKeyVO[]) // API 密钥列表

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ModelApi.getModelPage(queryParams)
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
    await ModelApi.deleteModel(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 初始化 **/
onMounted(async () => {
  await getList()
  // 获得下拉数据
  apiKeyList.value = await ApiKeyApi.getApiKeySimpleList()
})
</script>

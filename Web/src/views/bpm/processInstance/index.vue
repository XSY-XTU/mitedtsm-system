<template>
  <doc-alert title="流程发起、取消、重新发起" url="https://doc.iocoder.cn/bpm/process-instance/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="" prop="name">
        <el-input
          v-model="queryParams.name"
          :placeholder="t('process.instance.namePlaceholder')"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>

      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> {{ t('common.search') }}</el-button>
      </el-form-item>

      <el-form-item label="" prop="category" class="absolute right-[300px]">
        <el-select
          v-model="queryParams.category"
          :placeholder="t('process.instance.selectCategory')"
          clearable
          class="!w-155px"
          @change="handleQuery"
        >
          <el-option
            v-for="category in categoryList"
            :key="category.code"
            :label="category.name"
            :value="category.code"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="" prop="status" class="absolute right-[130px]">
        <el-select
          v-model="queryParams.status"
          :placeholder="t('process.instance.selectStatus')"
          clearable
          class="!w-155px"
          @change="handleQuery"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BPM_PROCESS_INSTANCE_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>

      <!-- 高级筛选 -->
      <el-form-item class="absolute right-0">
        <el-popover
          :visible="showPopover"
          persistent
          :width="400"
          :show-arrow="false"
          placement="bottom-end"
        >
          <template #reference>
            <el-button @click="showPopover = !showPopover">
              <Icon icon="ep:plus" class="mr-5px" />{{ t('process.instance.advancedFilter') }}
            </el-button>
          </template>
          <el-form-item
            :label="t('process.instance.belongProcess')"
            class="font-bold"
            label-position="top"
            prop="processDefinitionKey"
          >
            <el-select
              v-model="queryParams.processDefinitionKey"
              :placeholder="t('process.instance.selectProcessDefinition')"
              clearable
              class="!w-390px"
              @change="handleQuery"
            >
              <el-option
                v-for="item in processDefinitionList"
                :key="item.key"
                :label="item.name"
                :value="item.key"
              />
            </el-select>
          </el-form-item>
          <el-form-item :label="t('process.instance.initiatorTime')" class="font-bold" label-position="top" prop="createTime">
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
          <el-form-item class="font-bold" label-position="top">
            <div class="flex justify-end w-full">
              <el-button @click="resetQuery">{{ t('process.instance.clearQuery') }}</el-button>
              <el-button @click="showPopover = false">{{ t('process.instance.cancelQuery') }}</el-button>
              <el-button type="primary" @click="handleQuery">{{ t('process.instance.confirmQuery') }}</el-button>
            </div>
          </el-form-item>
        </el-popover>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :table-layout="'auto'">
      <el-table-column :label="t('process.instance.name')" align="center" prop="name" min-width="200px" fixed="left" />
      <el-table-column :label="t('process.instance.summary')" prop="summary" min-width="180" fixed="left">
        <template #default="scope">
          <div class="flex flex-col" v-if="scope.row.summary && scope.row.summary.length > 0">
            <div v-for="(item, index) in scope.row.summary" :key="index">
              <el-text type="info"> {{ item.key }} : {{ item.value }} </el-text>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        :label="t('process.instance.category')"
        align="center"
        prop="categoryName"
        min-width="100"
        fixed="left"
      />
      <el-table-column :label="t('process.instance.processStatus')" prop="status" min-width="200">
        <template #default="scope">
          <!-- 审批中状态 -->
          <template
            v-if="
              scope.row.status === BpmProcessInstanceStatus.RUNNING && scope.row.tasks?.length > 0
            "
          >
            <!-- 单人审批 -->
            <template v-if="scope.row.tasks.length === 1">
              <span>
                <el-button link type="primary" @click="handleDetail(scope.row)">
                  {{ scope.row.tasks[0].assigneeUser?.nickname }}
                </el-button>
                ({{ scope.row.tasks[0].name }}) {{ t('process.instance.approving') }}
              </span>
            </template>
            <!-- 多人审批 -->
            <template v-else>
              <span>
                <el-button link type="primary" @click="handleDetail(scope.row)">
                  {{ scope.row.tasks[0].assigneeUser?.nickname }}
                </el-button>
                {{ t('process.instance.peopleApproving', { count: scope.row.tasks.length }) }} ({{ scope.row.tasks[0].name }})
              </span>
            </template>
          </template>
          <!-- 非审批中状态 -->
          <template v-else>
            <dict-tag :type="DICT_TYPE.BPM_PROCESS_INSTANCE_STATUS" :value="scope.row.status" />
          </template>
        </template>
      </el-table-column>
      <el-table-column
        :label="t('process.instance.initiatorTime')"
        align="center"
        prop="startTime"
        min-width="180"
        :formatter="dateFormatter"
      />
      <el-table-column
        :label="t('process.instance.endTime')"
        align="center"
        prop="endTime"
        min-width="180"
        :formatter="dateFormatter"
      />
      <el-table-column :label="t('common.operation')" align="center" fixed="right" min-width="180">
        <template #default="scope">
          <el-button
            link
            type="primary"
            v-hasPermi="['bpm:process-instance:cancel']"
            @click="handleDetail(scope.row)"
          >
            {{ t('process.instance.detail') }}
          </el-button>
          <el-button
            link
            type="primary"
            v-if="scope.row.status === 1"
            v-hasPermi="['bpm:process-instance:query']"
            @click="handleCancel(scope.row)"
          >
            {{ t('process.instance.cancelProcess') }}
          </el-button>
          <el-button link type="primary" v-else @click="handleCreate(scope.row)">
            {{ t('process.instance.restart') }}
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
</template>
<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import { ElMessageBox } from 'element-plus'
import * as ProcessInstanceApi from '@/api/bpm/processInstance'
import { CategoryApi, CategoryVO } from '@/api/bpm/category'
import { ProcessInstanceVO } from '@/api/bpm/processInstance'
import * as DefinitionApi from '@/api/bpm/definition'
import { BpmProcessInstanceStatus } from '@/utils/constants'

defineOptions({ name: 'BpmProcessInstanceMy' })

const router = useRouter() // 路由
const message = useMessage() // 消息弹窗
const { t } = useI18n('bpm') // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const processDefinitionList = ref<any[]>([]) // 流程定义列表
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: '',
  processDefinitionKey: undefined,
  category: undefined,
  status: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const categoryList = ref<CategoryVO[]>([]) // 流程分类列表
const showPopover = ref(false) // 高级筛选是否展示

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ProcessInstanceApi.getProcessInstanceMyPage(queryParams)
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

/** 发起流程操作 **/
const handleCreate = async (row?: ProcessInstanceVO) => {
  if (row?.id) {
    const processDefinitionDetail = await DefinitionApi.getProcessDefinition(
      row.processDefinitionId
    )
    // 如果是【业务表单】，跳转到对应的发起界面
    if (processDefinitionDetail.formType === 20) {
      await router.push({
        path: processDefinitionDetail.formCustomCreatePath,
        query: {
          id: row.businessKey
        }
      })
    } else if (processDefinitionDetail.formType === 10) {
      //如果是【流程表单】，跳转到流程发起界面
      await router.push({
        name: 'BpmProcessInstanceCreate',
        query: { processInstanceId: row.id }
      })
    }
  }
}

/** 查看详情 */
const handleDetail = (row: ProcessInstanceVO) => {
  router.push({
    name: 'BpmProcessInstanceDetail',
    query: {
      id: row.id
    }
  })
}

/** 取消按钮操作 */
const handleCancel = async (row: ProcessInstanceVO) => {
  // 二次确认
  const { value } = await ElMessageBox.prompt(t('process.instance.cancelReason'), t('process.instance.cancelTitle'), {
    confirmButtonText: t('common.ok'),
    cancelButtonText: t('common.cancel'),
    inputPattern: /^[\s\S]*.*\S[\s\S]*$/, // 判断非空，且非空格
    inputErrorMessage: t('process.instance.cancelReasonRequired')
  })
  // 发起取消
  await ProcessInstanceApi.cancelProcessInstanceByStartUser(row.id, value)
  message.success(t('process.instance.cancelSuccess'))
  // 刷新列表
  await getList()
}

/** 激活时 **/
onActivated(() => {
  getList()
})

/** 初始化 **/
onMounted(async () => {
  await getList()
  categoryList.value = await CategoryApi.getCategorySimpleList()
  // 获取流程定义列表
  processDefinitionList.value = await DefinitionApi.getSimpleProcessDefinitionList()
})
</script>
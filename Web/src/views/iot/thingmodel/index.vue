<!-- 产品的物模型列表 -->
<template>
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
          <el-form-item :label="t('thing.model.type')" prop="name">
            <el-select
              v-model="queryParams.type"
              class="!w-240px"
              clearable
              :placeholder="t('common.selectPlaceholder')"
              @change="handleQuery"
            >
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.IOT_THING_MODEL_TYPE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button
              v-hasPermi="[`iot:thing-model:create`]"
              plain
              type="primary"
              @click="openForm('create')"
            >
              <Icon class="mr-5px" icon="ep:plus" />
              {{ t('thing.addFunction') }}
            </el-button>
            <el-button v-hasPermi="[`iot:thing-model:query`]" plain type="success" @click="openTSL">
              TSL
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-tabs>
      <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true" :table-layout="'auto'">
        <el-table-column align="center" :label="t('thing.model.type')" prop="type">
          <template #default="scope">
            <dict-tag :type="DICT_TYPE.IOT_THING_MODEL_TYPE" :value="scope.row.type" />
          </template>
        </el-table-column>
        <el-table-column align="center" :label="t('thing.model.name')" prop="name" />
        <el-table-column align="center" :label="t('thing.model.identifier')" prop="identifier" />
        <el-table-column align="center" :label="t('thing.model.dataType')" prop="identifier">
          <template #default="{ row }">
            {{ getDataTypeOptionsLabel(row.property?.dataType) ?? '-' }}
          </template>
        </el-table-column>
        <el-table-column align="left" :label="t('thing.model.dataDefinition')" prop="identifier">
          <template #default="{ row }">
            <DataDefinition :data="row" />
          </template>
        </el-table-column>
        <el-table-column align="center" :label="t('common.operation')" fixed="right">
          <template #default="scope">
            <el-button
              v-hasPermi="[`iot:thing-model:update`]"
              link
              type="primary"
              @click="openForm('update', scope.row.id)"
            >
              {{ t('common.edit') }}
            </el-button>
            <el-button
              v-hasPermi="['iot:thing-model:delete']"
              link
              type="danger"
              @click="handleDelete(scope.row.id)"
            >
              {{ t('common.delete') }}
            </el-button>
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
    </el-tabs>
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <ThingModelForm ref="formRef" @success="getList" />
  <ThingModelTSL ref="tslRef" />
</template>
<script lang="ts" setup>
import { ThingModelApi, ThingModelData } from '@/api/iot/thingmodel'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import ThingModelForm from './ThingModelForm.vue'
import ThingModelTSL from './ThingModelTSL.vue'
import { ProductVO } from '@/api/iot/product/product'
import { getDataTypeOptionsLabel, IOT_PROVIDE_KEY } from '@/views/iot/utils/constants'
import { DataDefinition } from './components'

defineOptions({ name: 'IoTThingModel' })

const { t } = useI18n('iot') // 国际化
const message = useMessage() // 消息弹窗

const loading = ref(true) // 列表的加载中
const list = ref<ThingModelData[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  type: undefined,
  productId: -1
})

const queryFormRef = ref() // 搜索的表单
const product = inject<Ref<ProductVO>>(IOT_PROVIDE_KEY.PRODUCT) // 注入产品信息

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    queryParams.productId = product?.value?.id || -1
    const data = await ThingModelApi.getThingModelPage(queryParams)
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

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 展示物模型 TSL */
const tslRef = ref()
const openTSL = () => {
  tslRef.value?.open()
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await ThingModelApi.deleteThingModel(id)
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

<template>
  <doc-alert title="会员等级、积分、签到" url="https://doc.iocoder.cn/member/level/" />

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
          <el-form-item :label="t('level.name')" prop="name">
            <el-input
              v-model="queryParams.name"
              :placeholder="t('level.namePlaceholder')"
              clearable
              @keyup.enter="handleQuery"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('level.status')" prop="status">
            <el-select v-model="queryParams.status" :placeholder="t('common.selectPlaceholder')" clearable class="!w-240px">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
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
            <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> {{ t('common.query') }}</el-button>
            <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> {{ t('common.reset') }}</el-button>
            <el-button type="primary" @click="openForm('create')" v-hasPermi="['member:level:create']">
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
      <el-table-column :label="t('level.id')" align="center" prop="id" min-width="60" />
      <el-table-column :label="t('level.icon')" align="center" prop="icon" min-width="80">
        <template #default="scope">
          <el-image
            :src="scope.row.icon"
            class="h-30px w-30px"
            :preview-src-list="[scope.row.icon]"
          />
        </template>
      </el-table-column>
      <el-table-column :label="t('level.backgroundUrl')" align="center" prop="backgroundUrl" min-width="100">
        <template #default="scope">
          <el-image
            :src="scope.row.backgroundUrl"
            class="h-30px w-30px"
            :preview-src-list="[scope.row.backgroundUrl]"
          />
        </template>
      </el-table-column>
      <el-table-column :label="t('level.name')" align="center" prop="name" min-width="100" />
      <el-table-column :label="t('level.level')" align="center" prop="level" min-width="60" />
      <el-table-column :label="t('level.experience')" align="center" prop="experience" min-width="80" />
      <el-table-column :label="t('level.discountPercentUnit')" align="center" prop="discountPercent" min-width="110" />
      <el-table-column :label="t('level.status')" align="center" prop="status" min-width="70">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        :label="t('common.createTime')"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        min-width="170"
      />
      <el-table-column :label="t('common.action')" align="center" min-width="110px" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['member:level:update']"
          >
            {{ t('common.edit') }}
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['member:level:delete']"
          >
            {{ t('common.del') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <LevelForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as LevelApi from '@/api/member/level'
import LevelForm from './LevelForm.vue'
import { useI18n } from '@/hooks/web/useI18n'

/** 会员等级管理 **/
defineOptions({ name: 'MemberLevel' })

const message = useMessage() // 消息弹窗
const { t } = useI18n('member') // 国际化

const loading = ref(true) // 列表的加载中
const list = ref([]) // 列表的数据
const queryParams = reactive({
  name: null,
  status: null
})
const queryFormRef = ref() // 搜索的表单

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    list.value = await LevelApi.getLevelList(queryParams)
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
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
    await LevelApi.deleteLevel(id)
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

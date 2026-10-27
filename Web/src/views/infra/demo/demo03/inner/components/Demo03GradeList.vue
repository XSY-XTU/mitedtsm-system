<template>
  <!-- 列表 -->
  <ContentWrap>
    <el-table
      row-key="id"
      v-loading="loading"
      :data="list"
      :stripe="true"
      :show-overflow-tooltip="true"
      :table-layout="'auto'"
    >
      <el-table-column :label="t('common.id')" align="center" prop="id" />
      <el-table-column :label="t('codegenDemo.name')" align="center" prop="name" />
      <el-table-column :label="t('codegenDemo.demo03.teacher')" align="center" prop="teacher" />
      <el-table-column
        :label="t('common.createTime')"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        min-width="180"
      />
    </el-table>
  </ContentWrap>
</template>
<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import { Demo03StudentApi } from '@/api/infra/demo/demo03/inner'

const { t } = useI18n('infra') // 国际化

const props = defineProps<{
  studentId?: number // 学生编号（主表的关联字段）
}>()
const loading = ref(false) // 列表的加载中
const list = ref([]) // 列表的数据

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await Demo03StudentApi.getDemo03GradeByStudentId(props.studentId)
    if (!data) {
      return
    }
    list.value.push(data)
  } finally {
    loading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

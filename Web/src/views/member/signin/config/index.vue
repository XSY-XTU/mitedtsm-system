<template>
  <doc-alert title="会员等级、积分、签到" url="https://doc.iocoder.cn/member/level/" />

  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-button
      type="primary"
      plain
      @click="openForm('create')"
      v-hasPermi="['point:sign-in-config:create']"
    >
      <Icon icon="ep:plus" class="mr-5px" /> {{ t('common.add') }}
    </el-button>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :table-layout="'auto'">
      <el-table-column
        :label="t('signIn.config.day')"
        align="center"
        prop="day"
        :formatter="(_, __, cellValue) => t('signIn.record.dayLabel', { day: cellValue })"
      />
      <el-table-column :label="t('signIn.config.point')" align="center" prop="point" />
      <el-table-column :label="t('signIn.config.experience')" align="center" prop="experience" />
      <el-table-column :label="t('signIn.config.status')" align="center" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column :label="t('common.action')" align="center" fixed="right" min-width="150">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['point:sign-in-config:update']"
          >
            {{ t('common.edit') }}
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['point:sign-in-config:delete']"
          >
            {{ t('common.del') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <SignInConfigForm ref="formRef" @success="getList" />
</template>
<script lang="ts" setup>
import * as SignInConfigApi from '@/api/member/signin/config'
import SignInConfigForm from './SignInConfigForm.vue'
import { DICT_TYPE } from '@/utils/dict'
import { useI18n } from '@/hooks/web/useI18n'

defineOptions({ name: 'SignInConfig' })

const message = useMessage() // 消息弹窗
const { t } = useI18n('member') // 国际化

const loading = ref(true) // 列表的加载中
const list = ref([]) // 列表的数据

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await SignInConfigApi.getSignInConfigList()
    console.log(data)
    list.value = data
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
    await SignInConfigApi.deleteSignInConfig(id)
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
<template>
  <doc-alert title="公众号素材" url="https://doc.iocoder.cn/mp/material/" />
  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form class="-mb-15px" label-width="auto">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('account.title')" prop="accountId">
            <WxAccountSelect @change="onAccountChanged" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-tabs v-model="type" @tab-change="onTabChange">
      <!-- tab 1：图片  -->
      <el-tab-pane :name="UploadType.Image">
        <template #label>
          <el-row align="middle"> <Icon icon="ep:picture" />{{ t('material.typeImage') }} </el-row>
        </template>
        <UploadFile
          v-hasPermi="['mp:material:upload-permanent']"
          :type="UploadType.Image"
          @uploaded="getList"
        >
          {{ t('material.imageTip') }}
        </UploadFile>
        <!-- 列表 -->
        <ImageTable :loading="loading" :list="list" @delete="handleDelete" />
        <!-- 分页组件 -->
        <Pagination
          :total="total"
          v-model:page="queryParams.pageNo"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
        />
      </el-tab-pane>

      <!-- tab 2：语音  -->
      <el-tab-pane :name="UploadType.Voice">
        <template #label>
          <el-row align="middle"> <Icon icon="ep:microphone" />{{ t('material.typeVoice') }} </el-row>
        </template>
        <UploadFile
          v-hasPermi="['mp:material:upload-permanent']"
          :type="UploadType.Voice"
          @uploaded="getList"
        >
          {{ t('material.voiceTip') }}
        </UploadFile>
        <!-- 列表 -->
        <VoiceTable :list="list" :loading="loading" @delete="handleDelete" />
        <!-- 分页组件 -->
        <Pagination
          :total="total"
          v-model:page="queryParams.pageNo"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
        />
      </el-tab-pane>

      <!-- tab 3：视频 -->
      <el-tab-pane :name="UploadType.Video">
        <template #label>
          <el-row align="middle"> <Icon icon="ep:video-play" /> {{ t('material.typeVideo') }} </el-row>
        </template>
        <el-button
          v-hasPermi="['mp:material:upload-permanent']"
          type="primary"
          plain
          @click="showCreateVideo = true"
          >{{ t('material.createVideo') }}</el-button
        >
        <!-- 新建视频的弹窗 -->
        <UploadVideo v-model="showCreateVideo" />
        <!-- 列表 -->
        <VideoTable :list="list" :loading="loading" @delete="handleDelete" />
        <!-- 分页组件 -->
        <Pagination
          :total="total"
          v-model:page="queryParams.pageNo"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
        />
      </el-tab-pane>
    </el-tabs>
  </ContentWrap>
</template>
<script lang="ts" setup name="MpMaterial">
import WxAccountSelect from '@/views/mp/components/wx-account-select'
import ImageTable from './components/ImageTable.vue'
import VoiceTable from './components/VoiceTable.vue'
import VideoTable from './components/VideoTable.vue'
import UploadFile from './components/UploadFile.vue'
import UploadVideo from './components/UploadVideo.vue'
import { UploadType } from './components/upload'
import * as MpMaterialApi from '@/api/mp/material'
const message = useMessage() // 消息
const { t } = useI18n('mp') // 国际化

const type = ref<UploadType>(UploadType.Image) // 素材类型
const loading = ref(false) // 遮罩层
const list = ref<any[]>([]) // 总条数
const total = ref(0) // 数据列表

const accountId = ref(-1)
provide('accountId', accountId)

// 查询参数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  accountId: accountId,
  permanent: true
})
const showCreateVideo = ref(false) // 是否新建视频的弹窗

/** 侦听公众号变化 **/
const onAccountChanged = (id: number) => {
  accountId.value = id
  queryParams.accountId = id
  queryParams.pageNo = 1
  getList()
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await MpMaterialApi.getMaterialPage({
      ...queryParams,
      type: type.value
    })
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

/** 处理 table 切换 */
const onTabChange = () => {
  // 提前情况数据，避免 tab 切换后显示垃圾数据
  list.value = []
  total.value = 0
  // 从第一页开始查询
  handleQuery()
}

/** 处理删除操作 */
const handleDelete = async (id: number) => {
  await message.confirm(t('material.confirmDelete'))
  await MpMaterialApi.deletePermanentMaterial(id)
  message.alertSuccess(t('common.delSuccess'))
}
</script>

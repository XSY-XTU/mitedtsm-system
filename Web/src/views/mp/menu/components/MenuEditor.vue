<template>
  <div>
    <div class="configure_page">
      <div class="delete_btn">
        <el-button type="danger" @click="emit('delete')">
          <Icon icon="ep:delete" />
          {{ t('menu.deleteCurrentMenu') }}
        </el-button>
      </div>
      <div>
        <span>{{ t('menu.name') }}：</span>
        <el-input
          class="input_width"
          v-model="menu.name"
          :placeholder="t('menu.namePlaceholder')"
          :maxlength="isParent ? 4 : 7"
          clearable
        />
      </div>
      <div v-if="isLeave">
        <div class="menu_content">
          <span>{{ t('menu.key') }}：</span>
          <el-input
            class="input_width"
            v-model="menu.menuKey"
            :placeholder="t('menu.keyPlaceholder')"
            clearable
          />
        </div>
        <div class="menu_content">
          <span>{{ t('menu.content') }}：</span>
          <el-select v-model="menu.type" clearable :placeholder="t('common.selectText')" class="menu_option">
            <el-option
              v-for="item in menuOptions"
              :label="item.label"
              :value="item.value"
              :key="item.value"
            />
          </el-select>
        </div>
        <div class="configur_content" v-if="menu.type === 'view'">
          <span>{{ t('menu.url') }}：</span>
          <el-input class="input_width" v-model="menu.url" :placeholder="t('menu.urlPlaceholder')" clearable />
        </div>
        <div class="configur_content" v-if="menu.type === 'miniprogram'">
          <div class="applet">
            <span>{{ t('menu.miniAppId') }}：</span>
            <el-input
              class="input_width"
              v-model="menu.miniProgramAppId"
              :placeholder="t('menu.miniAppIdPlaceholder')"
              clearable
            />
          </div>
          <div class="applet">
            <span>{{ t('menu.pagePath') }}：</span>
            <el-input
              class="input_width"
              v-model="menu.miniProgramPagePath"
              :placeholder="t('menu.pagePathPlaceholder')"
              clearable
            />
          </div>
          <div class="applet">
            <span>{{ t('menu.backupUrl') }}：</span>
            <el-input
              class="input_width"
              v-model="menu.url"
              :placeholder="t('menu.backupUrlPlaceholder')"
              clearable
            />
          </div>
          <p class="blue">{{ t('menu.miniAppTip') }}</p>
        </div>
        <div class="configur_content" v-if="menu.type === 'article_view_limited'">
          <el-row>
            <div class="select-item" v-if="menu && menu.replyArticles">
              <WxNews :articles="menu.replyArticles" />
              <el-row class="ope-row" justify="center" align="middle">
                <el-button type="danger" circle @click="deleteMaterial">
                  <icon icon="ep:delete" />
                </el-button>
              </el-row>
            </div>
            <div v-else>
              <el-row justify="center">
                <el-col :span="24" style="text-align: center">
                  <el-button type="success" @click="showNewsDialog = true">
                    {{ t('material.selectFromLibrary') }}
                    <Icon icon="ep:circle-check" />
                  </el-button>
                </el-col>
              </el-row>
            </div>
            <el-dialog :title="t('material.selectNews')" v-model="showNewsDialog" width="80%" destroy-on-close>
              <WxMaterialSelect
                type="news"
                :account-id="props.accountId"
                @select-material="selectMaterial"
              />
            </el-dialog>
          </el-row>
        </div>
        <div
          class="configur_content"
          v-if="menu.type === 'click' || menu.type === 'scancode_waitmsg'"
        >
          <WxReplySelect v-if="hackResetWxReplySelect" v-model="menu.reply" />
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import WxReplySelect from '@/views/mp/components/wx-reply'
import WxNews from '@/views/mp/components/wx-news'
import WxMaterialSelect from '@/views/mp/components/wx-material-select'
import menuOptions from './menuOptions'

const message = useMessage()
const { t } = useI18n('mp') // 国际化

const props = defineProps<{
  accountId: number
  modelValue: any
  isParent: boolean
}>()

const emit = defineEmits<{
  (e: 'delete', v: void)
  (e: 'update:modelValue', v: any)
}>()

const menu = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emit('update:modelValue', val)
  }
})
const showNewsDialog = ref(false)
const hackResetWxReplySelect = ref(false)
const isLeave = computed<boolean>(() => !(menu.value.children?.length > 0))

watch(menu, () => {
  hackResetWxReplySelect.value = false // 销毁组件
  nextTick(() => {
    hackResetWxReplySelect.value = true // 重建组件
  })
})

// ======================== 菜单编辑（素材选择） ========================
const selectMaterial = (item: any) => {
  const articleId = item.articleId
  const articles = item.content.newsItem
  // 提示，针对多图文
  if (articles.length > 1) {
    message.alertWarning(t('material.multiArticleTip'))
  }
  showNewsDialog.value = false

  // 设置菜单的回复
  menu.value.articleId = articleId
  menu.value.replyArticles = []
  articles.forEach((article) => {
    menu.value.replyArticles.push({
      title: article.title,
      description: article.digest,
      picUrl: article.picUrl,
      url: article.url
    })
  })
}

const deleteMaterial = () => {
  delete menu.value['articleId']
  delete menu.value['replyArticles']
}
</script>

<style lang="scss" scoped>
.el-input {
  width: 70%;
  margin-right: 2%;
}

.configure_page {
  .delete_btn {
    margin-bottom: 15px;
    text-align: right;
  }

  .menu_content {
    margin-top: 20px;
  }

  .configur_content {
    padding: 20px 10px;
    margin-top: 20px;
    background-color: #fff;
    border-radius: 5px;

    .select-item {
      width: 280px;
      padding: 10px;
      margin: 0 auto 10px;
      border: 1px solid #eaeaea;

      .ope-row {
        padding-top: 10px;
        text-align: center;
      }
    }
  }

  .blue {
    margin-top: 10px;
    color: #29b6f6;
  }

  .applet {
    margin-bottom: 20px;

    span {
      width: 20%;
    }
  }

  .input_width {
    width: 40%;
  }

  .material {
    .input_width {
      width: 30%;
    }

    .el-textarea {
      width: 80%;
    }
  }
}
</style>

<template>
  <doc-alert :title="tAi('chat.title')" url="https://doc.iocoder.cn/ai/chat/" />

  <ContentWrap>
    <el-tabs>
      <el-tab-pane :label="tAi('chat.manager.conversationList')">
        <ChatConversationList />
      </el-tab-pane>
      <el-tab-pane :label="tAi('chat.manager.messageList')">
        <ChatMessageList />
      </el-tab-pane>
    </el-tabs>
  </ContentWrap>
</template>

<script setup lang="ts">
import ChatConversationList from './ChatConversationList.vue'
import ChatMessageList from './ChatMessageList.vue'

/** AI 聊天对话 列表 */
defineOptions({ name: 'AiChatManager' })

const tAi = useI18n('ai').t
</script>
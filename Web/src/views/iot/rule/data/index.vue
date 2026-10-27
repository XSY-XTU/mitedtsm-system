<template>
  <el-tabs v-model="activeTab" type="border-card">
    <el-tab-pane :label="t('rule.data.tabRule')" name="rule">
      <RuleIndex />
    </el-tab-pane>
    <el-tab-pane :label="t('rule.data.tabSink')" name="sink" lazy>
      <SinkIndex />
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
import RuleIndex from './rule/index.vue'
import SinkIndex from './sink/index.vue'

/** IoT 数据流转 */
defineOptions({ name: 'IoTDataRule' })

const { t } = useI18n('iot')
const activeTab = ref('rule')
</script>

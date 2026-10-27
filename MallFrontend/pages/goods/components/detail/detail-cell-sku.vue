<template>
  <!-- SKU 选择的提示框 -->
  <detail-cell :label="t('selectSpec')" :value="value" />
</template>

<script setup>
  import { computed } from 'vue';
  import detailCell from './detail-cell.vue';
  import { useI18n } from '@/sheep/i18n';

  const { t } = useI18n('goods');

  const props = defineProps({
    modelValue: {
      type: Array,
      default() {
        return [];
      },
    },
    sku: {
      type: Object
    }
  });
  const value = computed(() => {
    if (!props.sku?.id) {
      return t('specRequired');
    }
    let str = '';
    props.sku.properties.forEach(property => {
      str += property.propertyName + ':' + property.valueName + ' ';
    });
    return str;
  });
</script>

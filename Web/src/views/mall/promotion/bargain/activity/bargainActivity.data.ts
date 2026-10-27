import type { CrudSchema } from '@/hooks/web/useCrudSchemas'
import { dateFormatter2 } from '@/utils/formatTime'

const { t } = useI18n()

// 表单校验
export const rules = reactive({
  name: [required],
  startTime: [required],
  endTime: [required],
  helpMaxCount: [required],
  bargainCount: [required],
  singleLimitCount: [required]
})

// CrudSchema https://doc.iocoder.cn/vue3/crud-schema/
const crudSchemas = reactive<CrudSchema[]>([
  {
    label: t('mall.promotion.bargain.nameLabel'),
    field: 'name',
    isSearch: true,
    isTable: false,
    form: {
      colProps: {
        span: 24
      }
    }
  },
  {
    label: t('mall.promotion.bargain.startTimeLabel'),
    field: 'startTime',
    formatter: dateFormatter2,
    isSearch: true,
    search: {
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        type: 'daterange'
      }
    },
    form: {
      component: 'DatePicker',
      componentProps: {
        type: 'date',
        valueFormat: 'x'
      }
    },
    table: {
      width: 120
    }
  },
  {
    label: t('mall.promotion.bargain.endTimeLabel'),
    field: 'endTime',
    formatter: dateFormatter2,
    isSearch: true,
    search: {
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        type: 'daterange'
      }
    },
    form: {
      component: 'DatePicker',
      componentProps: {
        type: 'date',
        valueFormat: 'x'
      }
    },
    table: {
      width: 120
    }
  },
  {
    label: t('mall.promotion.bargain.helpMaxCount'),
    field: 'helpMaxCount',
    isSearch: false,
    form: {
      component: 'InputNumber',
      labelMessage: t('mall.promotion.bargain.helpMaxCountTip'),
      value: 2
    }
  },
  {
    label: t('mall.promotion.bargain.bargainCount'),
    field: 'bargainCount',
    isSearch: false,
    form: {
      component: 'InputNumber',
      labelMessage: t('mall.promotion.bargain.helpMaxCountTip'),
      value: 2
    }
  },
  {
    label: t('mall.promotion.bargain.totalLimitCount'),
    field: 'totalLimitCount',
    isSearch: false,
    form: {
      component: 'InputNumber',
      labelMessage: t('mall.promotion.bargain.totalLimitCountTip'),
      value: 0
    }
  },
  {
    label: t('mall.promotion.bargain.randomMinPrice'),
    field: 'randomMinPrice',
    isSearch: false,
    isTable: false,
    form: {
      component: 'InputNumber',
      componentProps: {
        min: 0,
        precision: 2,
        step: 0.1
      },
      labelMessage: t('mall.promotion.bargain.randomMinPriceTip'),
      value: 0
    }
  },
  {
    label: t('mall.promotion.bargain.randomMaxPrice'),
    field: 'randomMaxPrice',
    isSearch: false,
    isTable: false,
    form: {
      component: 'InputNumber',
      componentProps: {
        min: 0,
        precision: 2,
        step: 0.1
      },
      labelMessage: t('mall.promotion.bargain.randomMaxPriceTip'),
      value: 0
    }
  },
  {
    label: t('mall.promotion.bargain.spuId'),
    field: 'spuId',
    isSearch: false,
    form: {
      colProps: {
        span: 24
      }
    }
  }
])
export const { allSchemas } = useCrudSchemas(crudSchemas)

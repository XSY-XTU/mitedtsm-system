import type { CrudSchema } from '@/hooks/web/useCrudSchemas'
import { dateFormatter2 } from '@/utils/formatTime'

// 表单校验
export const rules = reactive({
  name: [required],
  totalLimitCount: [required],
  singleLimitCount: [required],
  startTime: [required],
  endTime: [required],
  userSize: [required],
  limitDuration: [required],
  virtualGroup: [required]
})

// CrudSchema https://doc.iocoder.cn/vue3/crud-schema/
// 注意：label 需要使用完整的国际化 key 路径，因为 useCrudSchemas 中的 t() 没有命名空间
const crudSchemas = reactive<CrudSchema[]>([
  {
    label: 'mall.promotion.combination.combination.name',
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
    label: 'mall.promotion.combination.combination.startTime',
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
    label: 'mall.promotion.combination.combination.endTime',
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
    label: 'mall.promotion.combination.combination.userSize',
    field: 'userSize',
    isSearch: false,
    form: {
      component: 'InputNumber',
      labelMessage: 'mall.promotion.combination.combination.userSizeTip',
      value: 2
    }
  },
  {
    label: 'mall.promotion.combination.combination.limitDuration',
    field: 'limitDuration',
    isSearch: false,
    isTable: false,
    form: {
      component: 'InputNumber',
      labelMessage: 'mall.promotion.combination.combination.limitDurationTip',
      componentProps: {
        placeholder: 'mall.promotion.combination.combination.limitDurationPlaceholder'
      }
    }
  },
  {
    label: 'mall.promotion.combination.combination.totalLimitCount',
    field: 'totalLimitCount',
    isSearch: false,
    isTable: false,
    form: {
      component: 'InputNumber',
      value: 0
    }
  },
  {
    label: 'mall.promotion.combination.combination.singleLimitCount',
    field: 'singleLimitCount',
    isSearch: false,
    isTable: false,
    form: {
      component: 'InputNumber',
      value: 0
    }
  },
  {
    label: 'mall.promotion.combination.combination.virtualGroup',
    field: 'virtualGroup',
    dictType: DICT_TYPE.INFRA_BOOLEAN_STRING,
    dictClass: 'boolean',
    isSearch: true,
    form: {
      component: 'Radio',
      value: false
    }
  },
  {
    label: 'mall.promotion.combination.combination.spuId',
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

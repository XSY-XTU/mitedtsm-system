import { useI18n } from '@/hooks/web/useI18n'

const { t } = useI18n('crm')

/** 跟进状态 */
export const FOLLOWUP_STATUS = [
  { label: t('backlog.followUpStatusPending'), value: false },
  { label: t('backlog.followUpStatusDone'), value: true }
]

/** 归属范围 */
export const SCENE_TYPES = [
  { label: t('backlog.sceneMyResponsible'), value: 1 },
  { label: t('backlog.sceneMyInvolved'), value: 2 },
  { label: t('backlog.sceneSubordinate'), value: 3 }
]

/** 联系状态 */
export const CONTACT_STATUS = [
  { label: t('backlog.contactToday'), value: 1 },
  { label: t('backlog.contactOverdue'), value: 2 },
  { label: t('backlog.contactDone'), value: 3 }
]

/** 审批状态 */
export const AUDIT_STATUS = [
  { label: t('backlog.auditPending'), value: 10 },
  { label: t('backlog.auditPassed'), value: 20 },
  { label: t('backlog.auditRejected'), value: 30 }
]

/** 回款提醒类型 */
export const RECEIVABLE_REMIND_TYPE = [
  { label: t('backlog.receivablePending'), value: 1 },
  { label: t('backlog.receivableOverdue'), value: 2 },
  { label: t('backlog.receivableDone'), value: 3 }
]

/** 合同过期状态 */
export const CONTRACT_EXPIRY_TYPE = [
  { label: t('backlog.contractExpiring'), value: 1 },
  { label: t('backlog.contractExpired'), value: 2 }
]
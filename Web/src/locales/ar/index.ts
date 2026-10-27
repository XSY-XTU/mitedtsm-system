import common from './common'
import lock from './lock'
import error from './error'
import permission from './permission'
import setting from './setting'
import size from './size'
import login from './login'
import captcha from './captcha'
import router from './router'
import analysis from './analysis'
import workplace from './workplace'
import form from './form'
import watermark from './watermark'
import table from './table'
import action from './action'
import dialog from './dialog'
import sys from './sys'
import profile from './profile'
import cropper from './cropper'
// Business modules
import system from './system'
import mall from './mall'
import crm from './crm'
import erp from './erp'
import iot from './iot'
import bpm from './bpm'
import ai from './ai'
import pay from './pay'
import member from './member'
import mp from './mp'
import infra from './infra'
// Dictionary translations
import dict from './dict'

export default {
  common,
  lock,
  error,
  permission,
  setting,
  size,
  login,
  captcha,
  router,
  analysis,
  workplace,
  form,
  watermark,
  table,
  action,
  dialog,
  sys,
  profile,
  cropper,
  // Business modules
  system,
  mall,
  crm,
  erp,
  iot,
  bpm,
  ai,
  pay,
  member,
  mp,
  infra,
  // Dictionary translations
  dict,
  // Avoid warning when menu name is OAuth 2.0
  'OAuth 2.0': 'OAuth 2.0'
}

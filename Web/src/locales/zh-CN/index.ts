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
// 业务模块
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
// 字典翻译
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
  // 业务模块
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
  // 字典翻译
  dict,
  // 避免菜单名是 OAuth 2.0 时，一直 warn 报错
  'OAuth 2.0': 'OAuth 2.0'
}

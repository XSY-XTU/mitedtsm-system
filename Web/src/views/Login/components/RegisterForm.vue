<template>
  <el-form
    v-show="getShow"
    ref="formLogin"
    :model="registerData.registerForm"
    :rules="registerRules"
    class="login-form"
    label-position="top"
    label-width="auto"
    size="large"
  >
    <el-row class="mx-[-10px]">
      <el-col :span="24" class="px-10px">
        <el-form-item>
          <LoginFormTitle class="w-full" />
        </el-form-item>
      </el-col>
      <el-col :span="24" class="px-10px">
        <el-form-item v-if="registerData.tenantEnable === 'true'" prop="tenantName">
          <el-input
            v-model="registerData.registerForm.tenantName"
            :placeholder="t('login.tenantname')"
            :prefix-icon="iconHouse"
            link
            type="primary"
            size="large"
          />
        </el-form-item>
      </el-col>
      <el-col :span="24" class="px-10px">
        <el-form-item prop="username">
          <el-input
            v-model="registerData.registerForm.username"
            :placeholder="t('login.username')"
            size="large"
            :prefix-icon="iconAvatar"
          />
        </el-form-item>
      </el-col>
      <el-col :span="24" class="px-10px">
        <el-form-item prop="nickname">
          <el-input
            v-model="registerData.registerForm.nickname"
            :placeholder="t('login.nicknamePlaceholder')"
            size="large"
            :prefix-icon="iconAvatar"
          />
        </el-form-item>
      </el-col>
      <el-col :span="24" class="px-10px">
        <el-form-item prop="password">
          <el-input
            v-model="registerData.registerForm.password"
            type="password"
            auto-complete="off"
            :placeholder="t('login.password')"
            size="large"
            :prefix-icon="iconLock"
            show-password
          />
        </el-form-item>
      </el-col>
      <el-col :span="24" class="px-10px">
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerData.registerForm.confirmPassword"
            type="password"
            size="large"
            auto-complete="off"
            :placeholder="t('login.checkPassword')"
            :prefix-icon="iconLock"
            show-password
          />
        </el-form-item>
      </el-col>
      <el-col :span="24" class="px-10px">
        <el-form-item>
          <XButton
            :loading="loginLoading"
            :title="t('login.register')"
            class="w-full"
            type="primary"
            @click="getCode()"
          />
        </el-form-item>
      </el-col>
      <Verify
        v-if="registerData.captchaEnable === 'true'"
        ref="verify"
        :captchaType="captchaType"
        :imgSize="{ width: '400px', height: '200px' }"
        mode="pop"
        @success="handleRegister"
      />
    </el-row>
    <XButton :title="t('login.hasUser')" class="w-full" @click="handleBackLogin()" />
  </el-form>
</template>
<script lang="ts" setup>
import { ElLoading } from 'element-plus'
import LoginFormTitle from './LoginFormTitle.vue'
import type { RouteLocationNormalizedLoaded } from 'vue-router'
import { useIcon } from '@/hooks/web/useIcon'
import * as authUtil from '@/utils/auth'
import { usePermissionStore } from '@/store/modules/permission'
import * as LoginApi from '@/api/login'
import { LoginStateEnum, useLoginState, useFormValid } from './useLogin'

defineOptions({ name: 'RegisterForm' })

const { t } = useI18n()
const iconHouse = useIcon({ icon: 'ep:house' })
const iconAvatar = useIcon({ icon: 'ep:avatar' })
const iconLock = useIcon({ icon: 'ep:lock' })
const formLogin = ref()
const {validForm} = useFormValid(formLogin)
const { handleBackLogin, getLoginState } = useLoginState()
const { currentRoute, push } = useRouter()
const permissionStore = usePermissionStore()
const redirect = ref<string>('')
const loginLoading = ref(false)
const verify = ref()
const captchaType = ref('blockPuzzle') // blockPuzzle 滑块 clickWord 点击文字 pictureWord 文字验证码

const getShow = computed(() => unref(getLoginState) === LoginStateEnum.REGISTER)

const equalToPassword = (_rule, value, callback) => {
  if (registerData.registerForm.password !== value) {
    callback(new Error(t('login.passwordNotMatch')))
  } else {
    callback()
  }
}

const registerRules = {
  tenantName: [
    { required: true, trigger: 'blur', message: t('login.tenantRequired') },
    { min: 2, max: 20, message: t('login.tenantLength'), trigger: 'blur' }
  ],
  username: [
    { required: true, trigger: 'blur', message: t('login.usernameRequired') },
    { min: 4, max: 30, message: t('login.usernameLength'), trigger: 'blur' }
  ],
  nickname: [
    { required: true, trigger: 'blur', message: t('login.nicknameRequired') },
    { min: 0, max: 30, message: t('login.nicknameLength'), trigger: 'blur' }
  ],
  password: [
    { required: true, trigger: 'blur', message: t('login.passwordRequired') },
    { min: 5, max: 20, message: t('login.passwordLength'), trigger: 'blur' },
    { pattern: /^[^<>"'|\\]+$/, message: t('login.passwordInvalidChars'), trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, trigger: 'blur', message: t('login.confirmPasswordRequired') },
    { required: true, validator: equalToPassword, trigger: 'blur' }
  ]
}

const registerData = reactive({
  isShowPassword: false,
  captchaEnable: import.meta.env.VITE_APP_CAPTCHA_ENABLE,
  tenantEnable: import.meta.env.VITE_APP_TENANT_ENABLE,
  registerForm: {
    tenantName: import.meta.env.VITE_APP_DEFAULT_LOGIN_TENANT || '',
    nickname: '',
    tenantId: 0,
    username: '',
    password: '',
    confirmPassword: '',
    captchaVerification: ''
  }
})

const loading = ref() // ElLoading.service 返回的实例
// 提交注册
const handleRegister = async (params: any) => {
  loading.value = true
  try {
    if (registerData.tenantEnable) {
      await getTenantId()
      registerData.registerForm.tenantId = authUtil.getTenantId()
    }

    if (registerData.captchaEnable) {
      registerData.registerForm.captchaVerification = params.captchaVerification
    }

    const data = await validForm()
    if (!data) {
      return
    }

    const res = await LoginApi.register(registerData.registerForm)
    if (!res) {
      return
    }
    loading.value = ElLoading.service({
      lock: true,
      text: t('login.loadingSystem'),
      background: 'rgba(0, 0, 0, 0.7)'
    })

    authUtil.removeLoginForm()

    authUtil.setToken(res)
    if (!redirect.value) {
      redirect.value = '/'
    }
    // 判断是否为SSO登录
    if (redirect.value.indexOf('sso') !== -1) {
      window.location.href = window.location.href.replace('/login?redirect=', '')
    } else {
      push({ path: redirect.value || permissionStore.addRouters[0].path })
    }
  } finally {
    loginLoading.value = false
    loading.value.close()
  }
}

// 获取验证码
const getCode = async () => {
  // 情况一，未开启：则直接注册
  if (registerData.captchaEnable === 'false') {
    await handleRegister({})
  } else {
    // 情况二，已开启：则展示验证码；只有完成验证码的情况，才进行注册
    // 弹出验证码
    verify.value.show()
  }
}

// 获取租户 ID
const getTenantId = async () => {
  if (registerData.tenantEnable === 'true') {
    const res = await LoginApi.getTenantIdByName(registerData.registerForm.tenantName)
    authUtil.setTenantId(res)
  }
}

// 根据域名，获得租户信息
const getTenantByWebsite = async () => {
  if (registerData.tenantEnable === 'true') {
    const website = location.host
    const res = await LoginApi.getTenantByWebsite(website)
    if (res) {
      registerData.registerForm.tenantName = res.name
      authUtil.setTenantId(res.id)
    }
  }
}

watch(
  () => currentRoute.value,
  (route: RouteLocationNormalizedLoaded) => {
    redirect.value = route?.query?.redirect as string
  },
  {
    immediate: true
  }
)
onMounted(() => {
  // getCookie()
  getTenantByWebsite()
})
</script>

<style lang="scss" scoped>
:deep(.anticon) {
  &:hover {
    color: var(--el-color-primary) !important;
  }
}

.login-code {
  float: right;
  width: 100%;
  height: 38px;

  img {
    width: 100%;
    height: auto;
    max-width: 100px;
    vertical-align: middle;
    cursor: pointer;
  }
}
</style>

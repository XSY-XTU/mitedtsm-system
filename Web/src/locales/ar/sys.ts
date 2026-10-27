export default {
  api: {
    operationFailed: 'فشلت العملية',
    errorTip: 'نص الخطأ',
    errorMessage: 'فشلت العملية، يوجد خلل في النظام!',
    timeoutMessage: 'انتهت مهلة تسجيل الدخول، يرجى تسجيل الدخول مرة أخرى!',
    apiTimeoutMessage: 'انتهت مهلة طلب الواجهة، يرجى تحديث الصفحة والمحاولة مرة أخرى!',
    apiRequestFailed: 'فشل طلب الواجهة، يرجى المحاولة مرة أخرى لاحقاً!',
    networkException: 'شذوذ الشبكة',
    networkExceptionMsg:
      'يرجى التحقق مما إذا كان اتصال الشبكة لديك طبيعياً! الشبكة غير طبيعية',

    errMsg401: 'لا يمتلك المستخدم إذناً (خطأ في رمز أو اسم المستخدم أو كلمة المرور)!',
    errMsg403: 'المستخدم مصرح له، لكن الوصول ممنوع!',
    errMsg404: 'خطأ في طلب الشبكة، لم يتم العثور على المورد!',
    errMsg405: 'خطأ في طلب الشبكة، طريقة الطلب غير مسموح بها!',
    errMsg408: 'انتهت مهلة طلب الشبكة!',
    errMsg500: 'خطأ في الخادم، يرجى الاتصال بالمسؤول!',
    errMsg501: 'الشبكة غير منفذة!',
    errMsg502: 'خطأ في الشبكة!',
    errMsg503: 'الخدمة غير متاحة، الخادم معطل مؤقتاً أو قيد الصيانة!',
    errMsg504: 'انتهت مهلة الشبكة!',
    errMsg505: 'إصدار http لا يدعم الطلب!',
    errMsg901: 'وضع العرض التوضيحي، لا يُسمح بعمليات الكتابة!'
  },
  app: {
    logoutTip: 'تذكير',
    logoutMessage: 'تأكيد الخروج من النظام؟',
    menuLoading: 'جاري تحميل القائمة...'
  },
  exception: {
    backLogin: 'العودة لتسجيل الدخول',
    backHome: 'العودة للرئيسية',
    subTitle403: "عذراً، ليس لديك حق الوصول إلى هذه الصفحة.",
    subTitle404: 'عذراً، الصفحة التي زرتها غير موجودة.',
    subTitle500: 'عذراً، يبلّغ الخادم عن خطأ.',
    noDataTitle: 'لا توجد بيانات في الصفحة الحالية.',
    networkErrorTitle: 'خطأ في الشبكة',
    networkErrorSubTitle:
      'عذراً، تم قطع اتصال الشبكة لديك، يرجى التحقق من اتصالك!'
  },
  lock: {
    unlock: 'انقر لفتح القفل',
    alert: 'خطأ في كلمة مرور شاشة القفل',
    backToLogin: 'العودة لتسجيل الدخول',
    entry: 'دخول النظام',
    placeholder: 'الرجاء إدخال كلمة مرور شاشة القفل أو كلمة مرور المستخدم'
  },
  login: {
    backSignIn: 'العودة لتسجيل الدخول',
    mobileSignInFormTitle: 'تسجيل الدخول عبر الهاتف',
    qrSignInFormTitle: 'تسجيل الدخول عبر رمز QR',
    signInFormTitle: 'تسجيل الدخول',
    signUpFormTitle: 'التسجيل',
    forgetFormTitle: 'إعادة تعيين كلمة المرور',

    signInTitle: 'نظام إدارة الخلفية',
    signInDesc: 'أدخل بياناتك الشخصية وابدأ!',
    policy: 'أوافق على سياسة الخصوصية',
    scanSign: 'مسح الرمز لإكمال تسجيل الدخول',

    loginButton: 'تسجيل الدخول',
    registerButton: 'تسجيل',
    rememberMe: 'تذكرني',
    forgetPassword: 'نسيت كلمة المرور؟',
    otherSignIn: 'تسجيل الدخول باستخدام',

    // notify
    loginSuccessTitle: 'تم تسجيل الدخول بنجاح',
    loginSuccessDesc: 'مرحباً بعودتك',

    // placeholder
    accountPlaceholder: 'الرجاء إدخال اسم المستخدم',
    passwordPlaceholder: 'الرجاء إدخال كلمة المرور',
    smsPlaceholder: 'الرجاء إدخال رمز SMS',
    mobilePlaceholder: 'الرجاء إدخال رقم الهاتف',
    policyPlaceholder: 'التسجيل بعد التحقق',
    diffPwd: 'كلمتا المرور غير متطابقتين',

    userName: 'اسم المستخدم',
    password: 'كلمة المرور',
    confirmPassword: 'تأكيد كلمة المرور',
    email: 'البريد الإلكتروني',
    smsCode: 'رمز SMS',
    mobile: 'الهاتف المحمول'
  }
}

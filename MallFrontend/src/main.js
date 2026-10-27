import App from './App.vue';
import { createSSRApp } from 'vue';
import { setupPinia } from './sheep/store';
import i18n from './sheep/i18n';


export function createApp() {

  const app = createSSRApp(App);
  
  setupPinia(app);
  
  // 初始化 i18n
  app.use(i18n);

  return {
    app,
  };
}

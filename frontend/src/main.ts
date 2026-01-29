import { createApp } from "vue";
import { createRouter, createWebHistory } from "vue-router";
import Antd from "ant-design-vue";
import "ant-design-vue/dist/reset.css";
import App from "./App.vue";

const routes = [
  {
    path: "/",
    component: () => import("./pages/HomePage.vue")
  },
  {
    path: "/employees",
    component: () => import("./pages/EmployeeListPage.vue")
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

createApp(App).use(router).use(Antd).mount("#app");

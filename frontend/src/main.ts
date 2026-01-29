import { createApp } from "vue";
import { createRouter, createWebHistory } from "vue-router";
import Antd from "ant-design-vue";
import "ant-design-vue/dist/reset.css";
import App from "./App.vue";
import { getAccessToken } from "./services";

const routes = [
  {
    path: "/login",
    component: () => import("./pages/LoginPage.vue")
  },
  {
    path: "/",
    component: () => import("./pages/HomePage.vue")
  },
  {
    path: "/employees",
    component: () => import("./pages/EmployeeListPage.vue")
  },
  {
    path: "/profile",
    component: () => import("./pages/ProfilePage.vue")
  },
  {
    path: "/settings",
    component: () => import("./pages/SettingsPage.vue")
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  if (to.path === "/login") {
    next();
    return;
  }
  const token = getAccessToken();
  if (!token) {
    next({
      path: "/login",
      query: { redirect: to.fullPath }
    });
    return;
  }
  next();
});

createApp(App).use(router).use(Antd).mount("#app");

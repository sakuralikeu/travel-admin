import { createApp } from "vue";
import { createRouter, createWebHistory } from "vue-router";
import Antd from "ant-design-vue";
import "ant-design-vue/dist/reset.css";
import App from "./App.vue";
import { getAccessToken, getCurrentUser } from "./services";

const routes = [
  {
    path: "/login",
    component: () => import("./pages/LoginPage.vue")
  },
  {
    path: "/",
    component: () => import("./pages/HomePage.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/customers",
    component: () => import("./pages/CustomerListPage.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/public-pool",
    component: () => import("./pages/PublicPoolPage.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/operation-logs",
    component: () => import("./pages/OperationLogPage.vue"),
    meta: { requiresAuth: true, roles: ["SUPERVISOR", "MANAGER", "SUPER_ADMIN"] }
  },
  {
    path: "/approvals",
    component: () => import("./pages/ApprovalPage.vue"),
    meta: { requiresAuth: true, roles: ["SUPERVISOR", "MANAGER", "SUPER_ADMIN"] }
  },
  {
    path: "/employees",
    component: () => import("./pages/EmployeeListPage.vue"),
    meta: { requiresAuth: true, roles: ["SUPER_ADMIN", "MANAGER"] }
  },
  {
    path: "/profile",
    component: () => import("./pages/ProfilePage.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/settings",
    component: () => import("./pages/SettingsPage.vue"),
    meta: { requiresAuth: true, roles: ["SUPER_ADMIN"] }
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
  const user = getCurrentUser();
  const meta = to.meta as { roles?: string[] } | undefined;
  if (meta && meta.roles && meta.roles.length > 0) {
    if (!user || !meta.roles.includes(user.role)) {
      next({ path: "/" });
      return;
    }
  }
  next();
});

createApp(App).use(router).use(Antd).mount("#app");

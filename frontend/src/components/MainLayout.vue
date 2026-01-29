<template>
  <a-layout style="min-height: 100vh">
    <a-layout-sider v-model:collapsed="collapsed" collapsible>
      <div class="logo">
        员工客户管理系统
      </div>
      <a-menu
        theme="dark"
        mode="inline"
        :selected-keys="[selectedKey]"
        @click="handleMenuClick"
      >
        <a-menu-item key="home">
          首页
        </a-menu-item>
        <a-menu-item v-if="canViewEmployees" key="employees">
          员工管理
        </a-menu-item>
        <a-menu-item key="customers">
          客户管理
        </a-menu-item>
        <a-menu-item v-if="canViewLogs" key="operation-logs">
          操作日志
        </a-menu-item>
        <a-menu-item key="profile">
          个人中心
        </a-menu-item>
        <a-menu-item v-if="canViewSettings" key="settings">
          系统设置
        </a-menu-item>
      </a-menu>
    </a-layout-sider>
    <a-layout>
      <a-layout-header class="header">
        <div class="header-right">
          <a-dropdown>
            <a-space class="user-info">
              <a-avatar size="small">
                U
              </a-avatar>
              <span>{{ currentUserName }}</span>
            </a-space>
            <template #overlay>
              <a-menu>
                <a-menu-item key="profile" @click="goProfile">
                  个人中心
                </a-menu-item>
                <a-menu-item v-if="canViewSettings" key="settings" @click="goSettings">
                  系统设置
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item key="logout" @click="handleLogout">
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>
      <a-layout-content style="padding: 24px">
        <slot />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import { message } from "ant-design-vue";
import { clearAccessToken, getCurrentUser } from "../services";

const props = defineProps<{
  selectedKey: string;
}>();

const router = useRouter();
const collapsed = ref(false);

const currentUser = getCurrentUser();

const canViewEmployees = computed(() => {
  if (!currentUser) {
    return false;
  }
  return currentUser.role === "SUPER_ADMIN" || currentUser.role === "MANAGER";
});

const canViewSettings = computed(() => {
  if (!currentUser) {
    return false;
  }
  return currentUser.role === "SUPER_ADMIN";
});

const canViewLogs = computed(() => {
  if (!currentUser) {
    return false;
  }
  return (
    currentUser.role === "SUPERVISOR" ||
    currentUser.role === "MANAGER" ||
    currentUser.role === "SUPER_ADMIN"
  );
});

const currentUserName = computed(() => {
  if (!currentUser) {
    return "未登录";
  }
  return currentUser.name || currentUser.username;
});

function handleMenuClick(info: { key: string }) {
  if (info.key === "home") {
    router.push("/");
    return;
  }
  if (info.key === "employees") {
    router.push("/employees");
    return;
  }
  if (info.key === "customers") {
    router.push("/customers");
    return;
  }
  if (info.key === "operation-logs") {
    router.push("/operation-logs");
    return;
  }
  if (info.key === "profile") {
    router.push("/profile");
    return;
  }
  if (info.key === "settings") {
    router.push("/settings");
  }
}

function goProfile() {
  router.push("/profile");
}

function goSettings() {
  router.push("/settings");
}

function handleLogout() {
  clearAccessToken();
  message.success("已退出登录");
  router.replace("/login");
}
</script>

<style scoped>
.logo {
  height: 48px;
  margin: 16px;
  color: #fff;
  font-size: 16px;
  display: flex;
  align-items: center;
}

.header {
  background: #fff;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 0 16px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  cursor: pointer;
}
</style>


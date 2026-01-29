<template>
  <a-layout style="min-height: 100vh">
    <a-layout-header>
      <div class="header-inner">
        <div class="logo">员工客户管理系统</div>
        <a-menu
          theme="dark"
          mode="horizontal"
          :selected-keys="['settings']"
        >
          <a-menu-item key="home">
            <RouterLink to="/">首页</RouterLink>
          </a-menu-item>
          <a-menu-item key="employees">
            <RouterLink to="/employees">员工管理</RouterLink>
          </a-menu-item>
        </a-menu>
        <a-dropdown>
          <a-space class="user-info">
            <a-avatar size="small">A</a-avatar>
            <span>当前用户</span>
          </a-space>
          <template #overlay>
            <a-menu>
              <a-menu-item key="profile" @click="goProfile">
                个人中心
              </a-menu-item>
              <a-menu-item key="settings" @click="goSettings">
                设置
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
      <a-card title="系统设置">
        <p>这里将用于配置系统级参数（如密码策略、登录安全策略等）。</p>
        <p>当前仅为占位页面，后续可根据实际需求逐步补充具体设置项。</p>
      </a-card>
    </a-layout-content>
  </a-layout>
</template>

<script setup lang="ts">
import { RouterLink, useRouter } from "vue-router";
import { message } from "ant-design-vue";
import { clearAccessToken } from "../services";

const router = useRouter();

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
.header-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  color: #fff;
  font-size: 18px;
}

.user-info {
  color: #fff;
  cursor: pointer;
}
</style>


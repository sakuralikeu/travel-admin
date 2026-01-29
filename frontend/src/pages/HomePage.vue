<template>
  <a-layout style="min-height: 100vh">
    <a-layout-header>
      <div class="header-inner">
        <div class="logo">员工客户管理系统</div>
        <a-menu
          theme="dark"
          mode="horizontal"
          :selected-keys="['home']"
        >
          <a-menu-item key="home">
            <RouterLink to="/">首页</RouterLink>
          </a-menu-item>
          <a-menu-item key="employees">
            <RouterLink to="/employees">员工管理</RouterLink>
          </a-menu-item>
          <a-menu-item key="profile">
            <RouterLink to="/profile">个人中心</RouterLink>
          </a-menu-item>
          <a-menu-item key="settings">
            <RouterLink to="/settings">设置</RouterLink>
          </a-menu-item>
        </a-menu>
      </div>
    </a-layout-header>
    <a-layout-content style="padding: 24px">
      <a-typography-title :level="3">欢迎使用员工客户管理系统</a-typography-title>
      <a-typography-paragraph>
        可以通过顶部导航快速进入各个业务模块，例如员工管理。
      </a-typography-paragraph>
    </a-layout-content>
  </a-layout>
</template>

<script setup lang="ts">
import { RouterLink } from "vue-router";
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
</style>

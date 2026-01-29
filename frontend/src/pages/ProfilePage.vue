<template>
  <a-layout style="min-height: 100vh">
    <a-layout-header>
      <div class="header-inner">
        <div class="logo">员工客户管理系统</div>
        <a-menu
          theme="dark"
          mode="horizontal"
          :selected-keys="['profile']"
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
      <a-card title="个人中心">
        <a-descriptions bordered :column="1" v-if="employee">
          <a-descriptions-item label="姓名">
            {{ employee.name }}
          </a-descriptions-item>
          <a-descriptions-item label="登录账号">
            {{ employee.username }}
          </a-descriptions-item>
          <a-descriptions-item label="角色">
            {{ roleLabel }}
          </a-descriptions-item>
          <a-descriptions-item label="手机号">
            {{ employee.phone }}
          </a-descriptions-item>
          <a-descriptions-item label="邮箱">
            {{ employee.email || "未填写" }}
          </a-descriptions-item>
          <a-descriptions-item label="部门">
            {{ employee.department || "未填写" }}
          </a-descriptions-item>
          <a-descriptions-item label="职位">
            {{ employee.position || "未填写" }}
          </a-descriptions-item>
          <a-descriptions-item label="状态">
            {{ statusLabel }}
          </a-descriptions-item>
          <a-descriptions-item label="入职日期">
            {{ employee.hireDate || "未填写" }}
          </a-descriptions-item>
        </a-descriptions>
        <div v-else>
          正在加载个人信息...
        </div>
      </a-card>
    </a-layout-content>
  </a-layout>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from "vue";
import { RouterLink, useRouter } from "vue-router";
import { message } from "ant-design-vue";
import type { Employee } from "../types";
import { clearAccessToken, fetchCurrentEmployee } from "../services";

const router = useRouter();

const employee = ref<Employee | null>(null);

const roleLabel = computed(() => {
  if (!employee.value) return "";
  switch (employee.value.role) {
    case "SUPER_ADMIN":
      return "超级管理员";
    case "MANAGER":
      return "经理";
    case "SUPERVISOR":
      return "主管";
    case "EMPLOYEE":
      return "普通员工";
    default:
      return employee.value.role;
  }
});

const statusLabel = computed(() => {
  if (!employee.value) return "";
  switch (employee.value.status) {
    case "ACTIVE":
      return "在职";
    case "RESIGNED":
      return "离职";
    case "DISABLED":
      return "停用";
    default:
      return employee.value.status;
  }
});

onMounted(async () => {
  try {
    const data = await fetchCurrentEmployee();
    employee.value = data;
  } catch (error) {
    message.error("加载个人信息失败");
  }
});

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

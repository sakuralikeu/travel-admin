<template>
  <MainLayout selected-key="profile">
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
  </MainLayout>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from "vue";
import { useRouter } from "vue-router";
import { message } from "ant-design-vue";
import type { Employee } from "../types";
import { clearAccessToken, fetchCurrentEmployee, getCurrentUser } from "../services";
import MainLayout from "../components/MainLayout.vue";

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
</style>

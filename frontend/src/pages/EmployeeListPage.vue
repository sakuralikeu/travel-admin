<template>
  <a-layout style="min-height: 100vh">
    <a-layout-header>
      <div class="header-inner">
        <div class="logo">员工客户管理系统</div>
        <a-menu
          theme="dark"
          mode="horizontal"
          :selected-keys="['employees']"
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
      <a-card title="员工管理">
        <div class="toolbar">
          <a-input-search
            v-model:value="keyword"
            placeholder="搜索姓名、账号或手机号"
            enter-button="搜索"
            style="max-width: 320px"
            @search="handleSearch"
          />
          <a-button type="primary" @click="openCreateModal">
            新增员工
          </a-button>
        </div>
        <a-table
          :data-source="employees"
          :loading="loading"
          :pagination="false"
          row-key="id"
        >
          <a-table-column title="登录账号" data-index="username" key="username" />
          <a-table-column title="姓名" data-index="name" key="name" />
          <a-table-column title="手机号" data-index="phone" key="phone" />
          <a-table-column title="邮箱" data-index="email" key="email" />
          <a-table-column title="部门" data-index="department" key="department" />
          <a-table-column title="职位" data-index="position" key="position" />
          <a-table-column title="角色" data-index="role" key="role">
            <template #default="{ text }">
              {{ getRoleLabel(text) }}
            </template>
          </a-table-column>
          <a-table-column title="状态" data-index="status" key="status">
            <template #default="{ text }">
              {{ getStatusLabel(text) }}
            </template>
          </a-table-column>
          <a-table-column title="入职日期" data-index="hireDate" key="hireDate" />
          <a-table-column key="action" title="操作">
            <template #default="{ record }">
              <a-button type="link" @click="openEditModal(record)">
                编辑
              </a-button>
              <a-button type="link" danger @click="handleDelete(record)">
                删除
              </a-button>
            </template>
          </a-table-column>
        </a-table>
        <div class="pagination">
          <a-pagination
            :current="pageNum"
            :page-size="pageSize"
            :total="total"
            :show-size-changer="true"
            :page-size-options="['10', '20', '50']"
            @change="handlePageChange"
            @showSizeChange="handlePageSizeChange"
          />
        </div>
      </a-card>
      <a-modal
        v-model:open="modalOpen"
        :title="isEdit ? '编辑员工' : '新增员工'"
        :confirm-loading="modalConfirmLoading"
        destroy-on-close
        @ok="handleSubmit"
      >
        <a-form
          :model="formState"
          :label-col="{ span: 6 }"
          :wrapper-col="{ span: 18 }"
        >
          <a-form-item label="登录账号" required>
            <a-input v-model:value="formState.username" />
          </a-form-item>
          <a-form-item label="姓名" required>
            <a-input v-model:value="formState.name" />
          </a-form-item>
          <a-form-item label="手机号" required>
            <a-input v-model:value="formState.phone" />
          </a-form-item>
          <a-form-item label="邮箱">
            <a-input v-model:value="formState.email" />
          </a-form-item>
          <a-form-item label="部门">
            <a-input v-model:value="formState.department" />
          </a-form-item>
          <a-form-item label="职位">
            <a-input v-model:value="formState.position" />
          </a-form-item>
          <a-form-item label="角色" required>
            <a-select v-model:value="formState.role" placeholder="请选择角色">
              <a-select-option
                v-for="item in employeeRoleOptions"
                :key="item.value"
                :value="item.value"
              >
                {{ item.label }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="状态" required>
            <a-select v-model:value="formState.status" placeholder="请选择状态">
              <a-select-option
                v-for="item in employeeStatusOptions"
                :key="item.value"
                :value="item.value"
              >
                {{ item.label }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="入职日期">
            <a-date-picker
              v-model:value="formState.hireDate"
              value-format="YYYY-MM-DD"
            />
          </a-form-item>
          <a-form-item label="离职日期">
            <a-date-picker
              v-model:value="formState.resignDate"
              value-format="YYYY-MM-DD"
            />
          </a-form-item>
          <a-form-item :label="isEdit ? '新密码' : '初始密码'" :required="!isEdit">
            <a-input-password v-model:value="formState.password" />
          </a-form-item>
        </a-form>
      </a-modal>
    </a-layout-content>
  </a-layout>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { RouterLink } from "vue-router";
import { message } from "ant-design-vue";
import type { Employee, EmployeeFormValues } from "../types";
import {
  createEmployee,
  deleteEmployee,
  fetchEmployeePage,
  updateEmployee
} from "../services";

const employees = ref<Employee[]>([]);
const loading = ref(false);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const keyword = ref("");

const modalOpen = ref(false);
const modalConfirmLoading = ref(false);
const isEdit = ref(false);
const editingEmployeeId = ref<number | null>(null);

const formState = reactive<EmployeeFormValues>({
  username: "",
  name: "",
  phone: "",
  email: "",
  department: "",
  position: "",
  role: undefined,
  status: undefined,
  hireDate: null,
  resignDate: null,
  password: ""
});

const employeeRoleOptions = [
  { label: "超级管理员", value: "SUPER_ADMIN" },
  { label: "经理", value: "MANAGER" },
  { label: "主管", value: "SUPERVISOR" },
  { label: "普通员工", value: "EMPLOYEE" }
];

const employeeStatusOptions = [
  { label: "在职", value: "ACTIVE" },
  { label: "离职", value: "RESIGNED" },
  { label: "停用", value: "DISABLED" }
];

function getRoleLabel(value: string) {
  const item = employeeRoleOptions.find(option => option.value === value);
  return item ? item.label : value;
}

function getStatusLabel(value: string) {
  const item = employeeStatusOptions.find(option => option.value === value);
  return item ? item.label : value;
}

async function loadEmployees() {
  try {
    loading.value = true;
    const page = await fetchEmployeePage({
      keyword: keyword.value || undefined,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    });
    employees.value = page.records;
    total.value = page.total;
    pageNum.value = page.pageNum;
    pageSize.value = page.pageSize;
  } catch (error) {
    message.error("加载员工列表失败");
  } finally {
    loading.value = false;
  }
}

function resetForm() {
  formState.username = "";
  formState.name = "";
  formState.phone = "";
  formState.email = "";
  formState.department = "";
  formState.position = "";
  formState.role = undefined;
  formState.status = undefined;
  formState.hireDate = null;
  formState.resignDate = null;
  formState.password = "";
}

function openCreateModal() {
  isEdit.value = false;
  editingEmployeeId.value = null;
  resetForm();
  modalOpen.value = true;
}

function openEditModal(record: Employee) {
  isEdit.value = true;
  editingEmployeeId.value = record.id;
  formState.username = record.username;
  formState.name = record.name;
  formState.phone = record.phone;
  formState.email = record.email ?? "";
  formState.department = record.department ?? "";
  formState.position = record.position ?? "";
  formState.role = record.role;
  formState.status = record.status;
  formState.hireDate = record.hireDate;
  formState.resignDate = record.resignDate;
  formState.password = "";
  modalOpen.value = true;
}

async function handleSubmit() {
  if (!formState.username || !formState.name || !formState.phone) {
    message.warning("请填写必填字段");
    return;
  }
  if (!formState.role || !formState.status) {
    message.warning("请选择角色和状态");
    return;
  }
  if (!isEdit.value && (!formState.password || formState.password.length < 6)) {
    message.warning("请设置至少6位的初始密码");
    return;
  }
  try {
    modalConfirmLoading.value = true;
    if (isEdit.value && editingEmployeeId.value != null) {
      await updateEmployee(editingEmployeeId.value, formState);
      message.success("更新员工成功");
    } else {
      await createEmployee(formState);
      message.success("新增员工成功");
      pageNum.value = 1;
    }
    modalOpen.value = false;
    await loadEmployees();
  } catch (error) {
    message.error("保存员工信息失败");
  } finally {
    modalConfirmLoading.value = false;
  }
}

async function handleDelete(record: Employee) {
  try {
    loading.value = true;
    await deleteEmployee(record.id);
    message.success("删除员工成功");
    if (employees.value.length === 1 && pageNum.value > 1) {
      pageNum.value -= 1;
    }
    await loadEmployees();
  } catch (error) {
    message.error("删除员工失败");
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  pageNum.value = 1;
  loadEmployees();
}

function handlePageChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  loadEmployees();
}

function handlePageSizeChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  loadEmployees();
}

onMounted(() => {
  loadEmployees();
});
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

.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  gap: 16px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>

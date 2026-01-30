<template>
  <MainLayout selected-key="employees">
    <a-card title="员工管理">
        <div class="toolbar">
          <a-input-search
            v-model:value="keyword"
            placeholder="搜索姓名、账号或手机号"
            enter-button="搜索"
            style="max-width: 320px"
            @search="handleSearch"
          />
          <a-button
            v-if="canManageEmployees"
            type="primary"
            @click="openCreateModal"
          >
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
          <a-table-column v-if="canManageEmployees" key="action" title="操作">
            <template #default="{ record }">
              <a-button type="link" @click="openEditModal(record)">
                编辑
              </a-button>
              <a-button type="link" danger @click="handleDelete(record)">
                删除
              </a-button>
            </template>
          </a-table-column>
          <a-table-column
            v-if="canHandleResignCustomers"
            key="resign"
            title="离职客户处理"
          >
            <template #default="{ record }">
              <a-button
                type="link"
                :disabled="record.status !== 'RESIGNED'"
                @click="openResignModal(record)"
              >
                处理离职客户
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
      <a-modal
        v-model:open="resignModalOpen"
        title="处理离职员工客户"
        :confirm-loading="resignConfirmLoading"
        destroy-on-close
        @ok="handleResignSubmit"
      >
        <a-form :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
          <a-form-item label="处理方式" required>
            <a-radio-group v-model:value="resignMoveToPublicPool">
              <a-radio :value="true">
                转入公海
              </a-radio>
              <a-radio :value="false">
                转给指定员工
              </a-radio>
            </a-radio-group>
          </a-form-item>
          <a-form-item
            v-if="!resignMoveToPublicPool"
            label="目标员工"
            required
          >
            <a-select
              v-model:value="resignTargetEmployeeId"
              placeholder="请选择接收客户的员工"
            >
              <a-select-option
                v-for="employee in resignCandidates"
                :key="employee.id"
                :value="employee.id"
              >
                {{ employee.name || employee.username }}（ID: {{ employee.id }}）
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="原因" required>
            <a-input-textarea
              v-model:value="resignReason"
              :rows="3"
              placeholder="请输入处理原因"
            />
          </a-form-item>
        </a-form>
      </a-modal>
  </MainLayout>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { message } from "ant-design-vue";
import MainLayout from "../components/MainLayout.vue";
import type { Employee, EmployeeFormValues } from "../types";
import {
  createEmployee,
  deleteEmployee,
  fetchEmployeePage,
  updateEmployee,
  getCurrentUser,
  handleEmployeeResign
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

const currentUser = getCurrentUser();

const canManageEmployees = computed(() => {
  if (!currentUser) {
    return false;
  }
  return currentUser.role === "SUPER_ADMIN";
});

const canHandleResignCustomers = computed(() => {
  if (!currentUser) {
    return false;
  }
  return currentUser.role === "SUPER_ADMIN" || currentUser.role === "MANAGER";
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

const resignModalOpen = ref(false);
const resignConfirmLoading = ref(false);
const resignEmployeeId = ref<number | null>(null);
const resignMoveToPublicPool = ref(true);
const resignTargetEmployeeId = ref<number | null>(null);
const resignReason = ref("");

const resignCandidates = computed(() =>
  employees.value.filter(
    item =>
      item.status === "ACTIVE" &&
      resignEmployeeId.value != null &&
      item.id !== resignEmployeeId.value
  )
);

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

function openResignModal(record: Employee) {
  resignEmployeeId.value = record.id;
  resignMoveToPublicPool.value = true;
  resignTargetEmployeeId.value = null;
  resignReason.value = "";
  resignModalOpen.value = true;
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

async function handleResignSubmit() {
  if (resignEmployeeId.value == null) {
    message.error("未找到需要处理的离职员工");
    return;
  }
  if (!resignMoveToPublicPool.value && !resignTargetEmployeeId.value) {
    message.warning("请选择目标员工");
    return;
  }
  if (!resignReason.value || !resignReason.value.trim()) {
    message.warning("请输入处理原因");
    return;
  }
  try {
    resignConfirmLoading.value = true;
    await handleEmployeeResign({
      employeeId: resignEmployeeId.value,
      targetEmployeeId: resignMoveToPublicPool.value
        ? null
        : resignTargetEmployeeId.value,
      moveToPublicPool: resignMoveToPublicPool.value,
      reason: resignReason.value.trim()
    });
    message.success("处理离职员工客户成功");
    resignModalOpen.value = false;
    await loadEmployees();
  } catch (error) {
    message.error("处理离职员工客户失败");
  } finally {
    resignConfirmLoading.value = false;
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

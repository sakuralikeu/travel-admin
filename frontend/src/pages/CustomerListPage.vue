<template>
  <MainLayout selected-key="customers">
    <a-card title="客户管理">
        <div class="toolbar">
          <a-input-search
            v-model:value="keyword"
            placeholder="搜索姓名、手机号或微信"
            enter-button="搜索"
            style="max-width: 320px"
            @search="handleSearch"
          />
          <div class="toolbar-filters">
            <a-select
              v-model:value="level"
              allow-clear
              placeholder="客户等级"
              style="width: 140px"
            >
              <a-select-option value="VIP">VIP</a-select-option>
              <a-select-option value="NORMAL">普通</a-select-option>
            </a-select>
            <a-select
              v-model:value="status"
              allow-clear
              placeholder="客户状态"
              style="width: 160px"
            >
              <a-select-option value="NEW">新建未跟进</a-select-option>
              <a-select-option value="FOLLOWING">跟进中</a-select-option>
              <a-select-option value="DEAL">已成单</a-select-option>
              <a-select-option value="LOST">已流失</a-select-option>
            </a-select>
          </div>
          <a-button type="primary" @click="openCreateModal">
            新增客户
          </a-button>
        </div>
        <a-table
          :data-source="customers"
          :loading="loading"
          :pagination="false"
          row-key="id"
        >
          <a-table-column title="姓名" data-index="name" key="name" />
          <a-table-column title="手机号" data-index="phone" key="phone" />
          <a-table-column title="微信" data-index="wechat" key="wechat" />
          <a-table-column title="邮箱" data-index="email" key="email" />
          <a-table-column title="等级" data-index="level" key="level">
            <template #default="{ text }">
              {{ getLevelLabel(text) }}
            </template>
          </a-table-column>
          <a-table-column title="状态" data-index="status" key="status">
            <template #default="{ text }">
              {{ getCustomerStatusLabel(text) }}
            </template>
          </a-table-column>
          <a-table-column title="偏好目的地" data-index="preferredDestination" key="preferredDestination" />
          <a-table-column title="偏好预算" data-index="preferredBudget" key="preferredBudget" />
          <a-table-column title="偏好出行时间" data-index="preferredTravelTime" key="preferredTravelTime" />
          <a-table-column title="分配员工ID" data-index="assignedTo" key="assignedTo" />
          <a-table-column title="最近跟进时间" data-index="lastFollowUpTime" key="lastFollowUpTime" />
          <a-table-column key="action" title="操作">
            <template #default="{ record }">
              <a-button type="link" @click="openEditModal(record)">
                编辑
              </a-button>
              <a-popconfirm
                title="确定删除该客户吗？"
                ok-text="删除"
                cancel-text="取消"
                @confirm="handleDelete(record)"
              >
                <a-button type="link" danger>
                  删除
                </a-button>
              </a-popconfirm>
              <a-button
                v-if="canAssignCustomer"
                type="link"
                @click="openAssignModal(record)"
              >
                分配
              </a-button>
              <a-button type="link" @click="openTransferModal(record)">
                流转记录
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
        :title="isEdit ? '编辑客户' : '新增客户'"
        :confirm-loading="modalConfirmLoading"
        destroy-on-close
        @ok="handleSubmit"
      >
        <a-form
          :model="formState"
          :label-col="{ span: 6 }"
          :wrapper-col="{ span: 18 }"
        >
          <a-form-item label="姓名" required>
            <a-input v-model:value="formState.name" />
          </a-form-item>
          <a-form-item label="手机号" required>
            <a-input v-model:value="formState.phone" />
          </a-form-item>
          <a-form-item label="微信">
            <a-input v-model:value="formState.wechat" />
          </a-form-item>
          <a-form-item label="邮箱">
            <a-input v-model:value="formState.email" />
          </a-form-item>
          <a-form-item label="等级" required>
            <a-select v-model:value="formState.level">
              <a-select-option value="VIP">VIP</a-select-option>
              <a-select-option value="NORMAL">普通</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="状态" required>
            <a-select v-model:value="formState.status">
              <a-select-option value="NEW">新建未跟进</a-select-option>
              <a-select-option value="FOLLOWING">跟进中</a-select-option>
              <a-select-option value="DEAL">已成单</a-select-option>
              <a-select-option value="LOST">已流失</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="偏好目的地">
            <a-input v-model:value="formState.preferredDestination" />
          </a-form-item>
          <a-form-item label="偏好预算">
            <a-input v-model:value="formState.preferredBudget" />
          </a-form-item>
          <a-form-item label="偏好出行时间">
            <a-input v-model:value="formState.preferredTravelTime" />
          </a-form-item>
          <a-form-item label="备注">
            <a-input-textarea
              v-model:value="formState.remark"
              :rows="3"
            />
          </a-form-item>
        </a-form>
      </a-modal>
      <a-modal
        v-model:open="assignModalOpen"
        title="分配客户"
        :confirm-loading="assignConfirmLoading"
        destroy-on-close
        @ok="handleAssignSubmit"
      >
        <a-form :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
          <a-form-item label="目标员工" required>
            <a-select
              v-model:value="assignTargetEmployeeId"
              :loading="employeeLoading"
              placeholder="请选择接收客户的员工"
            >
              <a-select-option
                v-for="employee in employees"
                :key="employee.id"
                :value="employee.id"
              >
                {{ employee.name || employee.username }}（ID: {{ employee.id }}）
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="转移原因" required>
            <a-input-textarea
              v-model:value="assignReason"
              :rows="3"
              placeholder="请输入转移原因"
            />
          </a-form-item>
        </a-form>
      </a-modal>
      <a-modal
        v-model:open="transferModalOpen"
        title="客户流转记录"
        :footer="null"
        width="720px"
        destroy-on-close
      >
        <a-table
          :data-source="transferRecords"
          :loading="transferLoading"
          :pagination="false"
          row-key="id"
          size="small"
        >
          <a-table-column title="时间" data-index="createdAt" key="createdAt" />
          <a-table-column
            title="原员工ID"
            data-index="fromEmployeeId"
            key="fromEmployeeId"
          />
          <a-table-column
            title="新员工ID"
            data-index="toEmployeeId"
            key="toEmployeeId"
          />
          <a-table-column
            title="操作人ID"
            data-index="operatorId"
            key="operatorId"
          />
          <a-table-column title="类型" data-index="type" key="type">
            <template #default="{ text }">
              {{ getTransferTypeLabel(text) }}
            </template>
          </a-table-column>
          <a-table-column title="原因" data-index="reason" key="reason" />
        </a-table>
        <div class="pagination">
          <a-pagination
            :current="transferPageNum"
            :page-size="transferPageSize"
            :total="transferTotal"
            :show-size-changer="true"
            :page-size-options="['10', '20', '50']"
            @change="handleTransferPageChange"
            @showSizeChange="handleTransferPageSizeChange"
          />
        </div>
      </a-modal>
  </MainLayout>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { message } from "ant-design-vue";
import type {
  Employee,
  Customer,
  CustomerFormValues,
  CustomerLevel,
  CustomerStatus,
  CustomerTransferRecord,
  CustomerTransferType
} from "../types";
import {
  createCustomer,
  deleteCustomer,
  fetchCustomerPage,
  updateCustomer,
  fetchEmployeePage,
  assignCustomer,
  fetchCustomerTransferRecords
} from "../services";
import { createApproval } from "../services/approval";
import MainLayout from "../components/MainLayout.vue";
import { getCurrentUser } from "../services";

const customers = ref<Customer[]>([]);
const loading = ref(false);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const keyword = ref("");
const level = ref<CustomerLevel | undefined>();
const status = ref<CustomerStatus | undefined>();

const modalOpen = ref(false);
const modalConfirmLoading = ref(false);
const isEdit = ref(false);
const editingCustomerId = ref<number | null>(null);

const formState = reactive<CustomerFormValues>({
  name: "",
  phone: "",
  email: "",
  wechat: "",
  preferredDestination: "",
  preferredBudget: "",
  preferredTravelTime: "",
  level: "NORMAL",
  status: "NEW",
  assignedTo: null,
  remark: ""
});

const currentUser = getCurrentUser();

const canAssignCustomer = computed(() => {
  if (!currentUser) {
    return false;
  }
  return (
    currentUser.role === "SUPERVISOR" ||
    currentUser.role === "MANAGER" ||
    currentUser.role === "SUPER_ADMIN"
  );
});

const employees = ref<Employee[]>([]);
const employeeLoading = ref(false);
const assignModalOpen = ref(false);
const assignConfirmLoading = ref(false);
const assigningCustomerId = ref<number | null>(null);
const assignTargetEmployeeId = ref<number | null>(null);
const assignReason = ref("");

const transferModalOpen = ref(false);
const transferRecords = ref<CustomerTransferRecord[]>([]);
const transferLoading = ref(false);
const transferPageNum = ref(1);
const transferPageSize = ref(10);
const transferTotal = ref(0);
const transferCustomerId = ref<number | null>(null);

function getLevelLabel(value: CustomerLevel) {
  if (value === "VIP") {
    return "VIP";
  }
  if (value === "NORMAL") {
    return "普通";
  }
  return value;
}

function getCustomerStatusLabel(value: CustomerStatus) {
  if (value === "NEW") {
    return "新建未跟进";
  }
  if (value === "FOLLOWING") {
    return "跟进中";
  }
  if (value === "DEAL") {
    return "已成单";
  }
  if (value === "LOST") {
    return "已流失";
  }
  if (value === "PUBLIC_POOL") {
    return "公海客户";
  }
  return value;
}

function getTransferTypeLabel(value: CustomerTransferType) {
  if (value === "ASSIGN") {
    return "手动分配";
  }
  if (value === "TRANSFER") {
    return "手动转移";
  }
  if (value === "CLAIM_FROM_POOL") {
    return "公海领取";
  }
  if (value === "AUTO_RECYCLE_TO_POOL") {
    return "自动回收至公海";
  }
  if (value === "EMPLOYEE_RESIGN_TRANSFER") {
    return "离职客户转移";
  }
  if (value === "EMPLOYEE_RESIGN_TO_POOL") {
    return "离职客户转公海";
  }
  return value;
}

async function loadCustomers() {
  try {
    loading.value = true;
    const page = await fetchCustomerPage({
      keyword: keyword.value || undefined,
      level: level.value,
      status: status.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    });
    customers.value = page.records;
    total.value = page.total;
  } catch (error) {
    message.error("加载客户列表失败");
  } finally {
    loading.value = false;
  }
}

async function loadEmployees() {
  if (employees.value.length > 0) {
    return;
  }
  try {
    employeeLoading.value = true;
    const page = await fetchEmployeePage({
      pageNum: 1,
      pageSize: 100
    });
    employees.value = page.records;
  } catch (error) {
    message.error("加载员工列表失败");
  } finally {
    employeeLoading.value = false;
  }
}

function openCreateModal() {
  isEdit.value = false;
  editingCustomerId.value = null;
  formState.name = "";
  formState.phone = "";
  formState.wechat = "";
  formState.email = "";
  formState.preferredDestination = "";
  formState.preferredBudget = "";
  formState.preferredTravelTime = "";
  formState.level = "NORMAL";
  formState.status = "NEW";
  formState.assignedTo = null;
  formState.remark = "";
  modalOpen.value = true;
}

function openEditModal(record: Customer) {
  isEdit.value = true;
  editingCustomerId.value = record.id;
  formState.name = record.name;
  formState.phone = record.phone;
  formState.wechat = record.wechat || "";
  formState.email = record.email || "";
  formState.preferredDestination = record.preferredDestination || "";
  formState.preferredBudget = record.preferredBudget || "";
  formState.preferredTravelTime = record.preferredTravelTime || "";
  formState.level = record.level;
  formState.status = record.status;
  formState.assignedTo = record.assignedTo ?? null;
  formState.remark = record.remark || "";
  modalOpen.value = true;
}

async function openAssignModal(record: Customer) {
  if (!canAssignCustomer.value) {
    message.warning("当前角色无权分配客户");
    return;
  }
  assigningCustomerId.value = record.id;
  assignTargetEmployeeId.value = record.assignedTo;
  assignReason.value = "";
  assignModalOpen.value = true;
  await loadEmployees();
}

function openTransferModal(record: Customer) {
  transferCustomerId.value = record.id;
  transferPageNum.value = 1;
  transferPageSize.value = 10;
  loadTransferRecords();
}

async function handleSubmit() {
  if (!formState.name || !formState.phone) {
    message.warning("请填写姓名和手机号");
    return;
  }
  try {
    modalConfirmLoading.value = true;
    if (isEdit.value && editingCustomerId.value != null) {
      await updateCustomer(editingCustomerId.value, formState);
      message.success("更新客户成功");
    } else {
      await createCustomer(formState);
      message.success("创建客户成功");
    }
    modalOpen.value = false;
    await loadCustomers();
  } catch (error) {
    message.error("保存客户失败");
  } finally {
    modalConfirmLoading.value = false;
  }
}

async function handleAssignSubmit() {
  if (assigningCustomerId.value == null) {
    message.error("未找到要分配的客户");
    return;
  }
  if (!assignTargetEmployeeId.value) {
    message.warning("请选择目标员工");
    return;
  }
  if (!assignReason.value || !assignReason.value.trim()) {
    message.warning("请输入转移原因");
    return;
  }
  try {
    assignConfirmLoading.value = true;
    const customer = customers.value.find(
      item => item.id === assigningCustomerId.value
    );
    if (!customer) {
      message.error("未找到要分配的客户");
      return;
    }
    const fromEmployeeId = customer.assignedTo;
    const toEmployeeId = assignTargetEmployeeId.value;
    let crossDepartment = false;
    if (fromEmployeeId != null && toEmployeeId != null) {
      const fromEmployee = employees.value.find(
        item => item.id === fromEmployeeId
      );
      const toEmployee = employees.value.find(
        item => item.id === toEmployeeId
      );
      if (fromEmployee && toEmployee) {
        if (fromEmployee.department != null || toEmployee.department != null) {
          crossDepartment =
            fromEmployee.department !== toEmployee.department;
        }
      }
    }
    if (
      crossDepartment &&
      currentUser &&
      currentUser.role === "SUPERVISOR"
    ) {
      await createApproval({
        operationType: "CUSTOMER_TRANSFER",
        customerId: assigningCustomerId.value,
        targetEmployeeId: assignTargetEmployeeId.value,
        reason: assignReason.value.trim()
      });
      message.success("已提交跨部门客户转移审批");
      assignModalOpen.value = false;
      return;
    }
    await assignCustomer(assigningCustomerId.value, {
      targetEmployeeId: assignTargetEmployeeId.value,
      reason: assignReason.value.trim()
    });
    message.success("分配客户成功");
    assignModalOpen.value = false;
    await loadCustomers();
  } catch (error) {
    const msg = error instanceof Error ? error.message : "分配客户失败";
    message.error(msg);
  } finally {
    assignConfirmLoading.value = false;
  }
}

async function handleDelete(record: Customer) {
  try {
    loading.value = true;
    if (record.level === "VIP") {
      await createApproval({
        operationType: "VIP_CUSTOMER_DELETE",
        customerId: record.id,
        reason: `申请删除VIP客户：${record.name || ""} (ID: ${record.id})`
      });
      message.success("已提交删除VIP客户审批");
    } else {
      await deleteCustomer(record.id);
      message.success("删除客户成功");
      if (customers.value.length === 1 && pageNum.value > 1) {
        pageNum.value -= 1;
      }
      await loadCustomers();
    }
  } catch (error) {
    const msg = error instanceof Error ? error.message : "操作失败";
    message.error(msg);
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  pageNum.value = 1;
  loadCustomers();
}

function handlePageChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  loadCustomers();
}

function handlePageSizeChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  loadCustomers();
}

async function loadTransferRecords() {
  if (transferCustomerId.value == null) {
    return;
  }
  try {
    transferLoading.value = true;
    const page = await fetchCustomerTransferRecords(transferCustomerId.value, {
      pageNum: transferPageNum.value,
      pageSize: transferPageSize.value
    });
    transferRecords.value = page.records;
    transferTotal.value = page.total;
  } catch (error) {
    message.error("加载客户流转记录失败");
  } finally {
    transferLoading.value = false;
  }
}

function handleTransferPageChange(page: number, size: number) {
  transferPageNum.value = page;
  transferPageSize.value = size;
  loadTransferRecords();
}

function handleTransferPageSizeChange(page: number, size: number) {
  transferPageNum.value = page;
  transferPageSize.value = size;
  loadTransferRecords();
}

onMounted(() => {
  loadCustomers();
});
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  gap: 16px;
}

.toolbar-filters {
  display: flex;
  gap: 8px;
  align-items: center;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>

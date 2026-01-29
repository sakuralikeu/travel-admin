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
  </MainLayout>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { message } from "ant-design-vue";
import type {
  Customer,
  CustomerFormValues,
  CustomerLevel,
  CustomerStatus
} from "../types";
import {
  createCustomer,
  deleteCustomer,
  fetchCustomerPage,
  updateCustomer
} from "../services";
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

async function handleDelete(record: Customer) {
  try {
    loading.value = true;
    await deleteCustomer(record.id);
    message.success("删除客户成功");
    if (customers.value.length === 1 && pageNum.value > 1) {
      pageNum.value -= 1;
    }
    await loadCustomers();
  } catch (error) {
    message.error("删除客户失败");
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

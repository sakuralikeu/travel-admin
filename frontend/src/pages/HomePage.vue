<template>
  <MainLayout selected-key="home">
    <a-space direction="vertical" size="large" class="home-content">
      <div>
        <a-typography-title :level="3">
          欢迎回来，{{ currentUserName }}
        </a-typography-title>
        <a-typography-paragraph>
          从这里可以总览员工、客户、公海池、审批与异常预警的关键指标，并快速进入各业务模块。
        </a-typography-paragraph>
      </div>
      <a-row :gutter="[16, 16]" class="summary-row">
        <a-col :xs="24" :sm="12" :md="8" :lg="6">
          <a-card :loading="overviewLoading">
            <a-statistic title="客户总数" :value="customerTotal" />
            <div class="card-footer">
              <a-button type="link" size="small" @click="goCustomers">
                前往客户列表
              </a-button>
            </div>
          </a-card>
        </a-col>
        <a-col :xs="24" :sm="12" :md="8" :lg="6">
          <a-card :loading="overviewLoading">
            <a-statistic title="公海客户数" :value="publicPoolTotal" />
            <div class="card-footer">
              <a-button type="link" size="small" @click="goPublicPool">
                查看公海客户
              </a-button>
            </div>
          </a-card>
        </a-col>
        <a-col v-if="canViewEmployees" :xs="24" :sm="12" :md="8" :lg="6">
          <a-card :loading="overviewLoading">
            <a-statistic title="员工总数" :value="employeeTotal" />
            <div class="card-footer">
              <a-button type="link" size="small" @click="goEmployees">
                管理员工
              </a-button>
            </div>
          </a-card>
        </a-col>
        <a-col v-if="canViewTradeWarnings" :xs="24" :sm="12" :md="8" :lg="6">
          <a-card :loading="overviewLoading">
            <a-statistic title="未关闭预警数" :value="openWarningTotal" />
            <div class="card-footer">
              <a-button type="link" size="small" @click="goTradeWarnings">
                查看预警列表
              </a-button>
            </div>
          </a-card>
        </a-col>
        <a-col v-if="canViewApprovals" :xs="24" :sm="12" :md="8" :lg="6">
          <a-card :loading="overviewLoading">
            <a-statistic title="待审批事项" :value="pendingApprovalTotal" />
            <div class="card-footer">
              <a-button type="link" size="small" @click="goApprovals">
                前往审批中心
              </a-button>
            </div>
          </a-card>
        </a-col>
      </a-row>
      <a-row
        v-if="canViewApprovals || canViewTradeWarnings"
        :gutter="[16, 16]"
        class="detail-row"
      >
        <a-col v-if="canViewApprovals" :xs="24" :md="12">
          <a-card title="待审批事项" :loading="overviewLoading">
            <a-table
              :data-source="pendingApprovals"
              :pagination="false"
              row-key="id"
              size="small"
              :locale="{ emptyText: '当前没有待审批的记录' }"
            >
              <a-table-column
                title="类型"
                data-index="operationType"
                key="operationType"
              >
                <template #default="{ text }">
                  {{ getApprovalOperationTypeLabel(text) }}
                </template>
              </a-table-column>
              <a-table-column
                title="客户ID"
                data-index="customerId"
                key="customerId"
              />
              <a-table-column
                title="申请人ID"
                data-index="requesterId"
                key="requesterId"
              />
              <a-table-column
                title="状态"
                data-index="status"
                key="status"
              >
                <template #default="{ text }">
                  {{ getApprovalStatusLabel(text) }}
                </template>
              </a-table-column>
              <a-table-column
                title="创建时间"
                data-index="createdAt"
                key="createdAt"
              />
            </a-table>
            <div class="card-footer">
              <a-button type="link" size="small" @click="goApprovals">
                查看全部审批
              </a-button>
            </div>
          </a-card>
        </a-col>
        <a-col v-if="canViewTradeWarnings" :xs="24" :md="12">
          <a-card title="最近异常交易预警" :loading="overviewLoading">
            <a-table
              :data-source="recentWarnings"
              :pagination="false"
              row-key="id"
              size="small"
              :locale="{ emptyText: '当前没有异常交易预警' }"
            >
              <a-table-column title="类型" data-index="type" key="type">
                <template #default="{ text }">
                  {{ getWarningTypeLabel(text) }}
                </template>
              </a-table-column>
              <a-table-column title="等级" data-index="level" key="level">
                <template #default="{ text }">
                  {{ getWarningLevelLabel(text) }}
                </template>
              </a-table-column>
              <a-table-column
                title="客户ID"
                data-index="customerId"
                key="customerId"
              />
              <a-table-column
                title="员工ID"
                data-index="employeeId"
                key="employeeId"
              />
              <a-table-column
                title="预警说明"
                data-index="message"
                key="message"
              />
            </a-table>
            <div class="card-footer">
              <a-button type="link" size="small" @click="goTradeWarnings">
                查看全部预警
              </a-button>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </a-space>
  </MainLayout>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { message } from "ant-design-vue";
import { useRouter } from "vue-router";
import MainLayout from "../components/MainLayout.vue";
import {
  fetchCustomerPage,
  fetchEmployeePage,
  fetchPublicPoolPage,
  getCurrentUser
} from "../services";
import { fetchApprovalPage } from "../services/approval";
import { fetchTradeWarningPage } from "../services/trade-warning";
import type {
  Approval,
  ApprovalStatus,
  SensitiveOperationType
} from "../types/approval";
import type {
  TradeWarning,
  TradeWarningStatus,
  TradeWarningType,
  WarningLevel
} from "../types/trade-warning";

const router = useRouter();

const currentUser = getCurrentUser();

const overviewLoading = ref(false);
const employeeTotal = ref(0);
const customerTotal = ref(0);
const publicPoolTotal = ref(0);
const openWarningTotal = ref(0);
const pendingApprovalTotal = ref(0);

const pendingApprovals = ref<Approval[]>([]);
const recentWarnings = ref<TradeWarning[]>([]);

const currentUserName = computed(() => {
  if (!currentUser) {
    return "管理员";
  }
  return currentUser.name || currentUser.username;
});

const canViewEmployees = computed(() => {
  if (!currentUser) {
    return false;
  }
  return currentUser.role === "SUPER_ADMIN" || currentUser.role === "MANAGER";
});

const canViewApprovals = computed(() => {
  if (!currentUser) {
    return false;
  }
  return (
    currentUser.role === "SUPERVISOR" ||
    currentUser.role === "MANAGER" ||
    currentUser.role === "SUPER_ADMIN"
  );
});

const canViewTradeWarnings = computed(() => {
  if (!currentUser) {
    return false;
  }
  return (
    currentUser.role === "SUPERVISOR" ||
    currentUser.role === "MANAGER" ||
    currentUser.role === "SUPER_ADMIN"
  );
});

function getApprovalOperationTypeLabel(value: SensitiveOperationType) {
  if (value === "VIP_CUSTOMER_DELETE") {
    return "删除VIP客户";
  }
  if (value === "CUSTOMER_TRANSFER") {
    return "跨部门客户转移";
  }
  return value;
}

function getApprovalStatusLabel(value: ApprovalStatus) {
  if (value === "PENDING") {
    return "待审批";
  }
  if (value === "APPROVED") {
    return "已通过";
  }
  if (value === "REJECTED") {
    return "已拒绝";
  }
  return value;
}

function getWarningTypeLabel(value: TradeWarningType) {
  if (value === "PRICE_ABNORMAL") {
    return "价格异常";
  }
  if (value === "PAYMENT_ACCOUNT_ABNORMAL") {
    return "收款账户异常";
  }
  if (value === "LONG_UNPAID") {
    return "长期未收款";
  }
  if (value === "FREQUENT_AMOUNT_CHANGE") {
    return "频繁修改金额";
  }
  if (value === "FREQUENT_CANCEL") {
    return "频繁作废/取消";
  }
  return value;
}

function getWarningLevelLabel(value: WarningLevel) {
  if (value === "REMIND") {
    return "提醒";
  }
  if (value === "WARNING") {
    return "警告";
  }
  if (value === "CRITICAL") {
    return "严重";
  }
  return value;
}

async function loadOverview() {
  try {
    overviewLoading.value = true;
    const tasks: Promise<void>[] = [];

    tasks.push(
      fetchCustomerPage({
        pageNum: 1,
        pageSize: 1
      }).then(page => {
        customerTotal.value = page.total;
      })
    );

    tasks.push(
      fetchPublicPoolPage({
        pageNum: 1,
        pageSize: 1
      }).then(page => {
        publicPoolTotal.value = page.total;
      })
    );

    if (canViewEmployees.value) {
      tasks.push(
        fetchEmployeePage({
          pageNum: 1,
          pageSize: 1
        }).then(page => {
          employeeTotal.value = page.total;
        })
      );
    }

    if (canViewTradeWarnings.value) {
      tasks.push(
        fetchTradeWarningPage({
          status: "OPEN" as TradeWarningStatus,
          pageNum: 1,
          pageSize: 5
        }).then(page => {
          openWarningTotal.value = page.total;
          recentWarnings.value = page.records;
        })
      );
    }

    if (canViewApprovals.value) {
      tasks.push(
        fetchApprovalPage({
          status: "PENDING",
          pageNum: 1,
          pageSize: 5
        }).then(page => {
          pendingApprovalTotal.value = page.total;
          pendingApprovals.value = page.records;
        })
      );
    }

    await Promise.all(tasks);
  } catch (error) {
    const msg = error instanceof Error ? error.message : "加载首页概览失败";
    message.error(msg);
  } finally {
    overviewLoading.value = false;
  }
}

function goEmployees() {
  router.push("/employees");
}

function goCustomers() {
  router.push("/customers");
}

function goPublicPool() {
  router.push("/public-pool");
}

function goTradeWarnings() {
  router.push("/trade-warnings");
}

function goApprovals() {
  router.push("/approvals");
}

onMounted(() => {
  loadOverview();
});
</script>

<style scoped>
.home-content {
  width: 100%;
}

.summary-row {
  margin-top: 8px;
}

.detail-row {
  margin-top: 8px;
}

.card-footer {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
</style>

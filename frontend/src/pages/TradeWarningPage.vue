<template>
  <MainLayout selected-key="trade-warnings">
    <a-card title="异常交易预警">
      <div class="toolbar">
        <div class="toolbar-filters">
          <a-select
            v-model:value="status"
            allow-clear
            placeholder="预警状态"
            style="width: 140px"
          >
            <a-select-option value="OPEN">未关闭</a-select-option>
            <a-select-option value="CLOSED">已关闭</a-select-option>
          </a-select>
          <a-select
            v-model:value="level"
            allow-clear
            placeholder="预警等级"
            style="width: 140px"
          >
            <a-select-option value="REMIND">提醒</a-select-option>
            <a-select-option value="WARNING">警告</a-select-option>
            <a-select-option value="CRITICAL">严重</a-select-option>
          </a-select>
          <a-select
            v-model:value="type"
            allow-clear
            placeholder="预警类型"
            style="width: 220px"
          >
            <a-select-option value="PRICE_ABNORMAL">
              价格异常
            </a-select-option>
            <a-select-option value="PAYMENT_ACCOUNT_ABNORMAL">
              收款账户异常
            </a-select-option>
            <a-select-option value="LONG_UNPAID">
              长期未收款
            </a-select-option>
            <a-select-option value="FREQUENT_AMOUNT_CHANGE">
              频繁修改金额
            </a-select-option>
            <a-select-option value="FREQUENT_CANCEL">
              频繁作废/取消
            </a-select-option>
          </a-select>
          <a-input-number
            v-model:value="employeeId"
            :min="1"
            placeholder="员工ID"
            style="width: 140px"
          />
          <a-input-number
            v-model:value="customerId"
            :min="1"
            placeholder="客户ID"
            style="width: 140px"
          />
          <a-button type="primary" @click="handleSearch">
            查询
          </a-button>
        </div>
        <div class="toolbar-actions">
          <a-button v-if="canScan" @click="handleScan" :loading="scanLoading">
            扫描生成预警
          </a-button>
        </div>
      </div>
      <a-table
        :data-source="warnings"
        :loading="loading"
        :pagination="false"
        row-key="id"
        size="small"
      >
        <a-table-column title="ID" data-index="id" key="id" />
        <a-table-column
          title="类型"
          data-index="type"
          key="type"
        >
          <template #default="{ text }">
            {{ getTypeLabel(text) }}
          </template>
        </a-table-column>
        <a-table-column
          title="等级"
          data-index="level"
          key="level"
        >
          <template #default="{ text }">
            {{ getLevelLabel(text) }}
          </template>
        </a-table-column>
        <a-table-column
          title="状态"
          data-index="status"
          key="status"
        >
          <template #default="{ text }">
            {{ getStatusLabel(text) }}
          </template>
        </a-table-column>
        <a-table-column
          title="订单ID"
          data-index="orderId"
          key="orderId"
        />
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
        <a-table-column
          title="首次发现时间"
          data-index="firstDetectedAt"
          key="firstDetectedAt"
        />
        <a-table-column
          title="最近发现时间"
          data-index="lastDetectedAt"
          key="lastDetectedAt"
        />
        <a-table-column
          title="关闭时间"
          data-index="closedAt"
          key="closedAt"
        />
        <a-table-column
          title="关闭人ID"
          data-index="closedBy"
          key="closedBy"
        />
        <a-table-column
          title="关闭原因"
          data-index="closedReason"
          key="closedReason"
        />
        <a-table-column key="action" title="操作">
          <template #default="{ record }">
            <a-button
              v-if="record.status === 'OPEN' && canClose"
              type="link"
              @click="openCloseModal(record)"
            >
              关闭
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
      <a-modal
        v-model:open="closeModalOpen"
        title="关闭预警"
        :confirm-loading="closeConfirmLoading"
        destroy-on-close
        @ok="handleCloseSubmit"
      >
        <a-form :label-col="{ span: 5 }" :wrapper-col="{ span: 19 }">
          <a-form-item label="关闭原因">
            <a-input-textarea
              v-model:value="closeReason"
              :rows="3"
              placeholder="请输入关闭原因"
            />
          </a-form-item>
        </a-form>
      </a-modal>
    </a-card>
  </MainLayout>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { message } from "ant-design-vue";
import MainLayout from "../components/MainLayout.vue";
import type {
  TradeWarning,
  TradeWarningQueryParams,
  TradeWarningStatus,
  TradeWarningType,
  WarningLevel
} from "../types/trade-warning";
import {
  closeTradeWarning,
  fetchTradeWarningPage,
  scanTradeWarnings
} from "../services/trade-warning";
import { getCurrentUser } from "../services";

const warnings = ref<TradeWarning[]>([]);
const loading = ref(false);
const scanLoading = ref(false);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

const status = ref<TradeWarningStatus | undefined>("OPEN");
const level = ref<WarningLevel | undefined>();
const type = ref<TradeWarningType | undefined>();
const employeeId = ref<number | undefined>();
const customerId = ref<number | undefined>();

const closeModalOpen = ref(false);
const closeConfirmLoading = ref(false);
const closeReason = ref("");
const currentWarningId = ref<number | null>(null);

const currentUser = getCurrentUser();

const canScan = computed(() => {
  if (!currentUser) {
    return false;
  }
  return (
    currentUser.role === "MANAGER" || currentUser.role === "SUPER_ADMIN"
  );
});

const canClose = computed(() => {
  if (!currentUser) {
    return false;
  }
  return (
    currentUser.role === "SUPERVISOR" ||
    currentUser.role === "MANAGER" ||
    currentUser.role === "SUPER_ADMIN"
  );
});

function getTypeLabel(value: TradeWarningType) {
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

function getLevelLabel(value: WarningLevel) {
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

function getStatusLabel(value: TradeWarningStatus) {
  if (value === "OPEN") {
    return "未关闭";
  }
  if (value === "CLOSED") {
    return "已关闭";
  }
  return value;
}

async function loadWarnings() {
  const params: TradeWarningQueryParams = {
    employeeId: employeeId.value,
    customerId: customerId.value,
    type: type.value,
    level: level.value,
    status: status.value,
    pageNum: pageNum.value,
    pageSize: pageSize.value
  };
  try {
    loading.value = true;
    const page = await fetchTradeWarningPage(params);
    warnings.value = page.records;
    total.value = page.total;
  } catch (error) {
    const msg = error instanceof Error ? error.message : "加载预警失败";
    message.error(msg);
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  pageNum.value = 1;
  loadWarnings();
}

function handlePageChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  loadWarnings();
}

function handlePageSizeChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  loadWarnings();
}

function openCloseModal(record: TradeWarning) {
  currentWarningId.value = record.id;
  closeReason.value = "";
  closeModalOpen.value = true;
}

async function handleCloseSubmit() {
  if (currentWarningId.value == null) {
    return;
  }
  try {
    closeConfirmLoading.value = true;
    await closeTradeWarning({
      id: currentWarningId.value,
      reason: closeReason.value
    });
    message.success("关闭预警成功");
    closeModalOpen.value = false;
    await loadWarnings();
  } catch (error) {
    const msg = error instanceof Error ? error.message : "关闭预警失败";
    message.error(msg);
  } finally {
    closeConfirmLoading.value = false;
  }
}

async function handleScan() {
  try {
    scanLoading.value = true;
    await scanTradeWarnings();
    message.success("扫描完成");
    await loadWarnings();
  } catch (error) {
    const msg = error instanceof Error ? error.message : "扫描预警失败";
    message.error(msg);
  } finally {
    scanLoading.value = false;
  }
}

onMounted(() => {
  loadWarnings();
});
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  gap: 16px;
}

.toolbar-filters {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
}

.toolbar-actions {
  display: flex;
  align-items: center;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>


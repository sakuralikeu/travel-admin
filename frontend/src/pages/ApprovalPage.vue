<template>
  <MainLayout selected-key="approvals">
    <a-card title="敏感操作审批">
      <div class="toolbar">
        <div class="toolbar-filters">
          <a-select
            v-model:value="status"
            allow-clear
            placeholder="审批状态"
            style="width: 160px"
          >
            <a-select-option value="PENDING">待审批</a-select-option>
            <a-select-option value="APPROVED">已通过</a-select-option>
            <a-select-option value="REJECTED">已拒绝</a-select-option>
          </a-select>
          <a-select
            v-model:value="operationType"
            allow-clear
            placeholder="审批类型"
            style="width: 200px"
          >
            <a-select-option value="VIP_CUSTOMER_DELETE">
              删除VIP客户
            </a-select-option>
            <a-select-option value="CUSTOMER_TRANSFER">
              跨部门客户转移
            </a-select-option>
          </a-select>
          <a-button type="primary" @click="handleSearch">
            查询
          </a-button>
        </div>
      </div>
      <a-table
        :data-source="approvals"
        :loading="loading"
        :pagination="false"
        row-key="id"
        size="small"
      >
        <a-table-column title="ID" data-index="id" key="id" />
        <a-table-column
          title="类型"
          data-index="operationType"
          key="operationType"
        >
          <template #default="{ text }">
            {{ getOperationTypeLabel(text) }}
          </template>
        </a-table-column>
        <a-table-column
          title="客户ID"
          data-index="customerId"
          key="customerId"
        />
        <a-table-column
          title="原员工ID"
          data-index="fromEmployeeId"
          key="fromEmployeeId"
        />
        <a-table-column
          title="目标员工ID"
          data-index="toEmployeeId"
          key="toEmployeeId"
        />
        <a-table-column
          title="申请人ID"
          data-index="requesterId"
          key="requesterId"
        />
        <a-table-column title="原因" data-index="reason" key="reason" />
        <a-table-column title="状态" data-index="status" key="status">
          <template #default="{ text }">
            {{ getStatusLabel(text) }}
          </template>
        </a-table-column>
        <a-table-column
          title="审批人ID"
          data-index="approverId"
          key="approverId"
        />
        <a-table-column
          title="审批意见"
          data-index="decisionReason"
          key="decisionReason"
        />
        <a-table-column title="创建时间" data-index="createdAt" key="createdAt" />
        <a-table-column title="更新时间" data-index="updatedAt" key="updatedAt" />
        <a-table-column key="action" title="操作">
          <template #default="{ record }">
            <a-space v-if="record.status === 'PENDING'">
              <a-button type="link" @click="openDecisionModal(record, true)">
                通过
              </a-button>
              <a-button type="link" danger @click="openDecisionModal(record, false)">
                拒绝
              </a-button>
            </a-space>
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
        v-model:open="decisionModalOpen"
        :title="decisionApproved ? '通过审批' : '拒绝审批'"
        :confirm-loading="decisionConfirmLoading"
        destroy-on-close
        @ok="handleDecisionSubmit"
      >
        <a-form :label-col="{ span: 5 }" :wrapper-col="{ span: 19 }">
          <a-form-item label="审批意见">
            <a-input-textarea
              v-model:value="decisionReason"
              :rows="3"
              placeholder="请输入审批意见"
            />
          </a-form-item>
        </a-form>
      </a-modal>
    </a-card>
  </MainLayout>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { message } from "ant-design-vue";
import MainLayout from "../components/MainLayout.vue";
import type { Approval, ApprovalStatus, SensitiveOperationType } from "../types/approval";
import { decideApproval, fetchApprovalPage } from "../services/approval";

const approvals = ref<Approval[]>([]);
const loading = ref(false);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const status = ref<ApprovalStatus | undefined>("PENDING");
const operationType = ref<SensitiveOperationType | undefined>();

const decisionModalOpen = ref(false);
const decisionConfirmLoading = ref(false);
const decisionApproved = ref(true);
const decisionReason = ref("");
const currentApprovalId = ref<number | null>(null);

function getOperationTypeLabel(value: SensitiveOperationType) {
  if (value === "VIP_CUSTOMER_DELETE") {
    return "删除VIP客户";
  }
  if (value === "CUSTOMER_TRANSFER") {
    return "跨部门客户转移";
  }
  return value;
}

function getStatusLabel(value: ApprovalStatus) {
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

async function loadApprovals() {
  try {
    loading.value = true;
    const page = await fetchApprovalPage({
      operationType: operationType.value,
      status: status.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    });
    approvals.value = page.records;
    total.value = page.total;
  } catch (error) {
    message.error("加载审批记录失败");
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  pageNum.value = 1;
  loadApprovals();
}

function handlePageChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  loadApprovals();
}

function handlePageSizeChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  loadApprovals();
}

function openDecisionModal(record: Approval, approved: boolean) {
  currentApprovalId.value = record.id;
  decisionApproved.value = approved;
  decisionReason.value = "";
  decisionModalOpen.value = true;
}

async function handleDecisionSubmit() {
  if (currentApprovalId.value == null) {
    message.error("未找到要处理的审批记录");
    return;
  }
  try {
    decisionConfirmLoading.value = true;
    await decideApproval({
      id: currentApprovalId.value,
      approved: decisionApproved.value,
      decisionReason: decisionReason.value
    });
    message.success("提交审批结果成功");
    decisionModalOpen.value = false;
    await loadApprovals();
  } catch (error) {
    const msg = error instanceof Error ? error.message : "提交审批结果失败";
    message.error(msg);
  } finally {
    decisionConfirmLoading.value = false;
  }
}

onMounted(() => {
  loadApprovals();
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


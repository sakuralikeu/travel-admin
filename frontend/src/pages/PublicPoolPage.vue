<template>
  <MainLayout selected-key="public-pool">
    <a-card title="公海客户">
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
        </div>
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
        <a-table-column title="等级" data-index="level" key="level">
          <template #default="{ text }">
            {{ getLevelLabel(text) }}
          </template>
        </a-table-column>
        <a-table-column
          title="最近跟进时间"
          data-index="lastFollowUpTime"
          key="lastFollowUpTime"
        />
        <a-table-column
          title="进入公海时间"
          data-index="publicPoolEnterTime"
          key="publicPoolEnterTime"
        />
        <a-table-column
          title="进入公海原因"
          data-index="publicPoolEnterReason"
          key="publicPoolEnterReason"
        />
        <a-table-column key="action" title="操作">
          <template #default="{ record }">
            <a-button
              type="link"
              :disabled="!canClaimCustomer(record)"
              @click="handleClaim(record)"
            >
              领取
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
  </MainLayout>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { message } from "ant-design-vue";
import MainLayout from "../components/MainLayout.vue";
import type { Customer, CustomerLevel } from "../types";
import {
  claimFromPublicPool,
  fetchPublicPoolPage,
  getCurrentUser
} from "../services";

const customers = ref<Customer[]>([]);
const loading = ref(false);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const keyword = ref("");
const level = ref<CustomerLevel | undefined>();

const currentUser = getCurrentUser();

const canClaimVip = computed(() => {
  if (!currentUser) {
    return false;
  }
  return (
    currentUser.role === "SUPERVISOR" ||
    currentUser.role === "MANAGER" ||
    currentUser.role === "SUPER_ADMIN"
  );
});

function getLevelLabel(value: CustomerLevel) {
  if (value === "VIP") {
    return "VIP";
  }
  if (value === "NORMAL") {
    return "普通";
  }
  return value;
}

async function loadPublicPoolCustomers() {
  try {
    loading.value = true;
    const page = await fetchPublicPoolPage({
      keyword: keyword.value || undefined,
      level: level.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    });
    customers.value = page.records;
    total.value = page.total;
  } catch (error) {
    message.error("加载公海客户列表失败");
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  pageNum.value = 1;
  loadPublicPoolCustomers();
}

function handlePageChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  loadPublicPoolCustomers();
}

function handlePageSizeChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  loadPublicPoolCustomers();
}

function canClaimCustomer(record: Customer) {
  if (record.level === "VIP") {
    return canClaimVip.value;
  }
  return true;
}

async function handleClaim(record: Customer) {
  try {
    loading.value = true;
    await claimFromPublicPool(record.id);
    message.success("领取公海客户成功");
    if (customers.value.length === 1 && pageNum.value > 1) {
      pageNum.value -= 1;
    }
    await loadPublicPoolCustomers();
  } catch (error) {
    const msg =
      error instanceof Error ? error.message : "领取公海客户失败";
    message.error(msg);
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  loadPublicPoolCustomers();
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


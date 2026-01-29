<template>
  <MainLayout selected-key="operation-logs">
    <a-card title="操作日志">
        <div class="toolbar">
          <a-input-search
            v-model:value="keyword"
            placeholder="按模块、操作名称或路径搜索"
            enter-button="搜索"
            style="max-width: 320px"
            @search="handleSearch"
          />
        </div>
        <a-table
          :data-source="logs"
          :loading="loading"
          :pagination="false"
          row-key="id"
          size="small"
        >
          <a-table-column title="时间" data-index="createdAt" key="createdAt" />
          <a-table-column title="模块" data-index="module" key="module" />
          <a-table-column title="操作名称" data-index="name" key="name" />
          <a-table-column title="类型" data-index="type" key="type" />
          <a-table-column title="请求路径" data-index="requestPath" key="requestPath" />
          <a-table-column title="HTTP 方法" data-index="httpMethod" key="httpMethod" />
          <a-table-column title="操作者ID" data-index="operatorId" key="operatorId" />
          <a-table-column title="是否成功" data-index="success" key="success">
            <template #default="{ text }">
              <a-tag :color="text ? 'green' : 'red'">
                {{ text ? "成功" : "失败" }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column title="耗时(ms)" data-index="duration" key="duration" />
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
import type { OperationLog, OperationLogQueryParams } from "../types/log";
import { fetchOperationLogPage } from "../services/log";
import { getCurrentUser } from "../services";
import MainLayout from "../components/MainLayout.vue";

const logs = ref<OperationLog[]>([]);
const loading = ref(false);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const keyword = ref("");

const currentUser = getCurrentUser();

async function loadLogs() {
  const params: OperationLogQueryParams = {
    keyword: keyword.value || undefined,
    pageNum: pageNum.value,
    pageSize: pageSize.value
  };
  try {
    loading.value = true;
    const page = await fetchOperationLogPage(params);
    logs.value = page.records;
    total.value = page.total;
  } catch (error) {
    message.error("加载操作日志失败");
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  pageNum.value = 1;
  loadLogs();
}

function handlePageChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  loadLogs();
}

function handlePageSizeChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  loadLogs();
}

onMounted(() => {
  loadLogs();
});
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 16px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>

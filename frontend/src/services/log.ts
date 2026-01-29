import type { ApiResult, PageResult } from "../types";
import type { OperationLog, OperationLogQueryParams } from "../types/log";
import { getAccessToken } from "./index";

const OPERATION_LOG_BASE_URL = "/api/operation-logs";

async function requestJson<T>(input: RequestInfo, init?: RequestInit): Promise<T> {
  const headers: Record<string, string> = {
    ...(init && init.headers ? (init.headers as Record<string, string>) : {})
  };
  const token = getAccessToken();
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }
  const response = await fetch(input, {
    ...init,
    headers
  });
  if (!response.ok) {
    throw new Error("网络请求失败");
  }
  const result = (await response.json()) as ApiResult<T>;
  if (result.code !== 200) {
    throw new Error(result.message || "请求失败");
  }
  return result.data;
}

export async function fetchOperationLogPage(
  params: OperationLogQueryParams
): Promise<PageResult<OperationLog>> {
  const searchParams = new URLSearchParams();
  if (params.module) {
    searchParams.append("module", params.module);
  }
  if (params.type) {
    searchParams.append("type", params.type);
  }
  if (params.operatorId != null) {
    searchParams.append("operatorId", String(params.operatorId));
  }
  if (params.success != null) {
    searchParams.append("success", params.success ? "true" : "false");
  }
  if (params.keyword) {
    searchParams.append("keyword", params.keyword);
  }
  if (params.pageNum != null) {
    searchParams.append("pageNum", String(params.pageNum));
  }
  if (params.pageSize != null) {
    searchParams.append("pageSize", String(params.pageSize));
  }
  const queryString = searchParams.toString();
  const url = queryString
    ? `${OPERATION_LOG_BASE_URL}?${queryString}`
    : OPERATION_LOG_BASE_URL;
  return requestJson<PageResult<OperationLog>>(url, {
    method: "GET"
  });
}


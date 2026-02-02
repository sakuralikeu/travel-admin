import type { ApiResult, PageResult } from "../types";
import type {
  TradeWarning,
  TradeWarningQueryParams
} from "../types/trade-warning";
import { getAccessToken } from "./index";

const TRADE_WARNING_BASE_URL = "/api/trade-warnings";

async function requestJson<T>(
  input: RequestInfo,
  init?: RequestInit
): Promise<T> {
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

export async function fetchTradeWarningPage(
  params: TradeWarningQueryParams
): Promise<PageResult<TradeWarning>> {
  const searchParams = new URLSearchParams();
  if (params.employeeId != null) {
    searchParams.append("employeeId", String(params.employeeId));
  }
  if (params.customerId != null) {
    searchParams.append("customerId", String(params.customerId));
  }
  if (params.type) {
    searchParams.append("type", params.type);
  }
  if (params.level) {
    searchParams.append("level", params.level);
  }
  if (params.status) {
    searchParams.append("status", params.status);
  }
  if (params.pageNum != null) {
    searchParams.append("pageNum", String(params.pageNum));
  }
  if (params.pageSize != null) {
    searchParams.append("pageSize", String(params.pageSize));
  }
  const queryString = searchParams.toString();
  const url = queryString
    ? `${TRADE_WARNING_BASE_URL}?${queryString}`
    : TRADE_WARNING_BASE_URL;
  return requestJson<PageResult<TradeWarning>>(url, {
    method: "GET"
  });
}

export async function scanTradeWarnings(): Promise<void> {
  await requestJson<void>(`${TRADE_WARNING_BASE_URL}/scan`, {
    method: "POST"
  });
}

export async function closeTradeWarning(params: {
  id: number;
  reason?: string;
}): Promise<void> {
  const body: Record<string, unknown> = {};
  if (params.reason && params.reason.trim().length > 0) {
    body.reason = params.reason.trim();
  }
  await requestJson<void>(`${TRADE_WARNING_BASE_URL}/${params.id}/close`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(body)
  });
}


import type { ApiResult, PageResult } from "../types";
import type {
  Approval,
  ApprovalQueryParams,
  SensitiveOperationType
} from "../types/approval";
import { getAccessToken } from "./index";

const APPROVAL_BASE_URL = "/api/approvals";

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

export async function fetchApprovalPage(
  params: ApprovalQueryParams
): Promise<PageResult<Approval>> {
  const searchParams = new URLSearchParams();
  if (params.operationType) {
    searchParams.append("operationType", params.operationType);
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
    ? `${APPROVAL_BASE_URL}?${queryString}`
    : APPROVAL_BASE_URL;
  return requestJson<PageResult<Approval>>(url, {
    method: "GET"
  });
}

export async function createApproval(payload: {
  operationType: SensitiveOperationType;
  customerId: number;
  targetEmployeeId?: number | null;
  reason: string;
}): Promise<void> {
  const body: Record<string, unknown> = {
    operationType: payload.operationType,
    customerId: payload.customerId,
    reason: payload.reason
  };
  if (payload.targetEmployeeId != null) {
    body.targetEmployeeId = payload.targetEmployeeId;
  }
  await requestJson<void>(APPROVAL_BASE_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(body)
  });
}

export async function decideApproval(params: {
  id: number;
  approved: boolean;
  decisionReason?: string;
}): Promise<void> {
  const body: Record<string, unknown> = {
    approved: params.approved
  };
  if (params.decisionReason && params.decisionReason.trim().length > 0) {
    body.decisionReason = params.decisionReason.trim();
  }
  await requestJson<void>(`${APPROVAL_BASE_URL}/${params.id}/decision`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(body)
  });
}


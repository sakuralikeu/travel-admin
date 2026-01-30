export type SensitiveOperationType =
  | "VIP_CUSTOMER_DELETE"
  | "CUSTOMER_TRANSFER";

export type ApprovalStatus = "PENDING" | "APPROVED" | "REJECTED";

export interface Approval {
  id: number;
  operationType: SensitiveOperationType;
  customerId: number | null;
  fromEmployeeId: number | null;
  toEmployeeId: number | null;
  requesterId: number | null;
  reason: string;
  status: ApprovalStatus;
  approverId: number | null;
  decisionReason: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface ApprovalQueryParams {
  operationType?: SensitiveOperationType;
  status?: ApprovalStatus;
  pageNum?: number;
  pageSize?: number;
}


export type WarningLevel = "REMIND" | "WARNING" | "CRITICAL";

export type TradeWarningStatus = "OPEN" | "CLOSED";

export type TradeWarningType =
  | "PRICE_ABNORMAL"
  | "PAYMENT_ACCOUNT_ABNORMAL"
  | "LONG_UNPAID"
  | "FREQUENT_AMOUNT_CHANGE"
  | "FREQUENT_CANCEL";

export interface TradeWarning {
  id: number;
  orderId: number | null;
  customerId: number | null;
  employeeId: number | null;
  type: TradeWarningType;
  level: WarningLevel;
  status: TradeWarningStatus;
  message: string;
  firstDetectedAt: string | null;
  lastDetectedAt: string | null;
  closedAt: string | null;
  closedBy: number | null;
  closedReason: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface TradeWarningQueryParams {
  employeeId?: number;
  customerId?: number;
  type?: TradeWarningType;
  level?: WarningLevel;
  status?: TradeWarningStatus;
  pageNum?: number;
  pageSize?: number;
}

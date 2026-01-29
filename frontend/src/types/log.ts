export interface OperationLog {
  id: number;
  module: string;
  name: string;
  type: string;
  requestPath: string;
  httpMethod: string;
  operatorId: number | null;
  ip: string | null;
  userAgent: string | null;
  success: boolean;
  errorMessage: string | null;
  duration: number;
  createdAt: string;
}

export interface OperationLogQueryParams {
  module?: string;
  type?: string;
  operatorId?: number;
  success?: boolean;
  keyword?: string;
  pageNum?: number;
  pageSize?: number;
}


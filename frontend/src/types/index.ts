export interface ApiResult<T> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  pageNum: number;
  pageSize: number;
  totalPages: number;
}

export type EmployeeRole = "SUPER_ADMIN" | "MANAGER" | "SUPERVISOR" | "EMPLOYEE";

export type EmployeeStatus = "ACTIVE" | "RESIGNED" | "DISABLED";

export interface Employee {
  id: number;
  username: string;
  name: string;
  phone: string;
  email: string | null;
  department: string | null;
  position: string | null;
  role: EmployeeRole;
  status: EmployeeStatus;
  hireDate: string | null;
  resignDate: string | null;
  createdBy: number | null;
  updatedBy: number | null;
  createdAt: string;
  updatedAt: string;
}

export interface EmployeeQueryParams {
  keyword?: string;
  role?: EmployeeRole;
  status?: EmployeeStatus;
  department?: string;
  pageNum?: number;
  pageSize?: number;
}

export interface EmployeeFormValues {
  id?: number;
  username: string;
  name: string;
  phone: string;
  email?: string;
  department?: string;
  position?: string;
  role?: EmployeeRole;
  status?: EmployeeStatus;
  hireDate?: string | null;
  resignDate?: string | null;
  password?: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  userId: number;
  username: string;
  name: string;
  role: EmployeeRole;
}

export type CustomerLevel = "VIP" | "NORMAL";

export type CustomerStatus =
  | "NEW"
  | "FOLLOWING"
  | "DEAL"
  | "LOST"
  | "PUBLIC_POOL";

export interface Customer {
  id: number;
  name: string;
  phone: string;
  wechat: string | null;
  email: string | null;
  preferredDestination: string | null;
  preferredBudget: string | null;
  preferredTravelTime: string | null;
  level: CustomerLevel;
  status: CustomerStatus;
  assignedTo: number | null;
  source: string | null;
  tags: string | null;
  remark: string | null;
  lastFollowUpTime: string | null;
  createdBy: number | null;
  updatedBy: number | null;
  createdAt: string;
  updatedAt: string;
}

export interface CustomerQueryParams {
  keyword?: string;
  level?: CustomerLevel;
  status?: CustomerStatus;
  assignedTo?: number;
  pageNum?: number;
  pageSize?: number;
}

export interface CustomerFormValues {
  id?: number;
  name: string;
  phone: string;
  wechat?: string;
  email?: string;
  preferredDestination?: string;
  preferredBudget?: string;
  preferredTravelTime?: string;
  level: CustomerLevel;
  status: CustomerStatus;
  assignedTo: number | null;
  remark?: string;
}

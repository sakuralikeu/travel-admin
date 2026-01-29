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

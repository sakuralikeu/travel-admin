import type {
  ApiResult,
  PageResult,
  Employee,
  EmployeeFormValues,
  EmployeeQueryParams,
  LoginRequest,
  LoginResponse,
  Customer,
  CustomerFormValues,
  CustomerQueryParams,
  CustomerTransferRecord
} from "../types";

const EMPLOYEE_BASE_URL = "/api/employees";
const CUSTOMER_BASE_URL = "/api/customers";
const AUTH_LOGIN_URL = "/api/auth/login";
const AUTH_ME_URL = "/api/auth/me";

const TOKEN_STORAGE_KEY = "travel_admin_token";
const USER_STORAGE_KEY = "travel_admin_user";

export function getAccessToken(): string | null {
  return localStorage.getItem(TOKEN_STORAGE_KEY);
}

function setAccessToken(token: string) {
  localStorage.setItem(TOKEN_STORAGE_KEY, token);
}

function setCurrentUser(user: LoginResponse) {
  localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(user));
}

export function getCurrentUser(): LoginResponse | null {
  const raw = localStorage.getItem(USER_STORAGE_KEY);
  if (!raw) {
    return null;
  }
  try {
    return JSON.parse(raw) as LoginResponse;
  } catch {
    return null;
  }
}

export function clearAccessToken() {
  localStorage.removeItem(TOKEN_STORAGE_KEY);
  localStorage.removeItem(USER_STORAGE_KEY);
}

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

export async function login(payload: LoginRequest): Promise<LoginResponse> {
  const data = await requestJson<LoginResponse>(AUTH_LOGIN_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  });
  setAccessToken(data.token);
  setCurrentUser(data);
  return data;
}

export async function fetchEmployeePage(
  params: EmployeeQueryParams
): Promise<PageResult<Employee>> {
  const searchParams = new URLSearchParams();
  if (params.keyword) {
    searchParams.append("keyword", params.keyword);
  }
  if (params.role) {
    searchParams.append("role", params.role);
  }
  if (params.status) {
    searchParams.append("status", params.status);
  }
  if (params.department) {
    searchParams.append("department", params.department);
  }
  if (params.pageNum != null) {
    searchParams.append("pageNum", String(params.pageNum));
  }
  if (params.pageSize != null) {
    searchParams.append("pageSize", String(params.pageSize));
  }
  const queryString = searchParams.toString();
  const url = queryString
    ? `${EMPLOYEE_BASE_URL}?${queryString}`
    : EMPLOYEE_BASE_URL;
  return requestJson<PageResult<Employee>>(url, {
    method: "GET"
  });
}

export async function createEmployee(
  payload: EmployeeFormValues
): Promise<Employee> {
  const body = {
    username: payload.username,
    name: payload.name,
    phone: payload.phone,
    email: payload.email ?? null,
    department: payload.department ?? null,
    position: payload.position ?? null,
    role: payload.role,
    status: payload.status,
    hireDate: payload.hireDate ?? null,
    resignDate: payload.resignDate ?? null,
    password: payload.password
  };
  return requestJson<Employee>(EMPLOYEE_BASE_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(body)
  });
}

export async function updateEmployee(
  id: number,
  payload: EmployeeFormValues
): Promise<Employee> {
  const body: Record<string, unknown> = {
    username: payload.username,
    name: payload.name,
    phone: payload.phone,
    email: payload.email ?? null,
    department: payload.department ?? null,
    position: payload.position ?? null,
    role: payload.role,
    status: payload.status,
    hireDate: payload.hireDate ?? null,
    resignDate: payload.resignDate ?? null
  };
  if (payload.password && payload.password.trim().length > 0) {
    body.password = payload.password;
  }
  return requestJson<Employee>(`${EMPLOYEE_BASE_URL}/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(body)
  });
}

export async function deleteEmployee(id: number): Promise<void> {
  await requestJson<void>(`${EMPLOYEE_BASE_URL}/${id}`, {
    method: "DELETE"
  });
}

export async function fetchCurrentEmployee(): Promise<Employee> {
  return requestJson<Employee>(AUTH_ME_URL, {
    method: "GET"
  });
}

export async function fetchCustomerPage(
  params: CustomerQueryParams
): Promise<PageResult<Customer>> {
  const searchParams = new URLSearchParams();
  if (params.keyword) {
    searchParams.append("keyword", params.keyword);
  }
  if (params.level) {
    searchParams.append("level", params.level);
  }
  if (params.status) {
    searchParams.append("status", params.status);
  }
  if (params.assignedTo != null) {
    searchParams.append("assignedTo", String(params.assignedTo));
  }
  if (params.pageNum != null) {
    searchParams.append("pageNum", String(params.pageNum));
  }
  if (params.pageSize != null) {
    searchParams.append("pageSize", String(params.pageSize));
  }
  const queryString = searchParams.toString();
  const url = queryString ? `${CUSTOMER_BASE_URL}?${queryString}` : CUSTOMER_BASE_URL;
  return requestJson<PageResult<Customer>>(url, {
    method: "GET"
  });
}

export async function fetchPublicPoolPage(
  params: CustomerQueryParams
): Promise<PageResult<Customer>> {
  const searchParams = new URLSearchParams();
  if (params.keyword) {
    searchParams.append("keyword", params.keyword);
  }
  if (params.level) {
    searchParams.append("level", params.level);
  }
  if (params.pageNum != null) {
    searchParams.append("pageNum", String(params.pageNum));
  }
  if (params.pageSize != null) {
    searchParams.append("pageSize", String(params.pageSize));
  }
  const queryString = searchParams.toString();
  const url = queryString
    ? `${CUSTOMER_BASE_URL}/public-pool?${queryString}`
    : `${CUSTOMER_BASE_URL}/public-pool`;
  return requestJson<PageResult<Customer>>(url, {
    method: "GET"
  });
}

export async function createCustomer(
  payload: CustomerFormValues
): Promise<Customer> {
  const body = {
    name: payload.name,
    phone: payload.phone,
    wechat: payload.wechat ?? null,
    email: payload.email ?? null,
    preferredDestination: payload.preferredDestination ?? null,
    preferredBudget: payload.preferredBudget ?? null,
    preferredTravelTime: payload.preferredTravelTime ?? null,
    level: payload.level,
    status: payload.status,
    assignedTo: payload.assignedTo,
    remark: payload.remark ?? null
  };
  return requestJson<Customer>(CUSTOMER_BASE_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(body)
  });
}

export async function updateCustomer(
  id: number,
  payload: CustomerFormValues
): Promise<Customer> {
  const body = {
    name: payload.name,
    phone: payload.phone,
    wechat: payload.wechat ?? null,
    email: payload.email ?? null,
    preferredDestination: payload.preferredDestination ?? null,
    preferredBudget: payload.preferredBudget ?? null,
    preferredTravelTime: payload.preferredTravelTime ?? null,
    level: payload.level,
    status: payload.status,
    assignedTo: payload.assignedTo,
    remark: payload.remark ?? null
  };
  return requestJson<Customer>(`${CUSTOMER_BASE_URL}/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(body)
  });
}

export async function deleteCustomer(id: number): Promise<void> {
  await requestJson<void>(`${CUSTOMER_BASE_URL}/${id}`, {
    method: "DELETE"
  });
}

export async function claimFromPublicPool(customerId: number): Promise<void> {
  await requestJson<void>(`${CUSTOMER_BASE_URL}/public-pool/claim`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ customerId })
  });
}

export async function assignCustomer(
  customerId: number,
  payload: { targetEmployeeId: number; reason: string }
): Promise<void> {
  await requestJson<void>(`${CUSTOMER_BASE_URL}/${customerId}/assign`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  });
}

export async function fetchCustomerTransferRecords(
  customerId: number,
  params: { pageNum?: number; pageSize?: number }
): Promise<PageResult<CustomerTransferRecord>> {
  const searchParams = new URLSearchParams();
  if (params.pageNum != null) {
    searchParams.append("pageNum", String(params.pageNum));
  }
  if (params.pageSize != null) {
    searchParams.append("pageSize", String(params.pageSize));
  }
  const queryString = searchParams.toString();
  const url = queryString
    ? `${CUSTOMER_BASE_URL}/${customerId}/transfers?${queryString}`
    : `${CUSTOMER_BASE_URL}/${customerId}/transfers`;
  return requestJson<PageResult<CustomerTransferRecord>>(url, {
    method: "GET"
  });
}

export async function handleEmployeeResign(payload: {
  employeeId: number;
  targetEmployeeId?: number | null;
  moveToPublicPool?: boolean;
  reason?: string;
}): Promise<void> {
  await requestJson<void>(`${CUSTOMER_BASE_URL}/employee/resign`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  });
}

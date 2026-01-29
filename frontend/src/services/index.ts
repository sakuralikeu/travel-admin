import type {
  ApiResult,
  PageResult,
  Employee,
  EmployeeFormValues,
  EmployeeQueryParams
} from "../types";

const EMPLOYEE_BASE_URL = "/api/employees";

async function requestJson<T>(input: RequestInfo, init?: RequestInit): Promise<T> {
  const response = await fetch(input, init);
  if (!response.ok) {
    throw new Error("网络请求失败");
  }
  const result = (await response.json()) as ApiResult<T>;
  if (result.code !== 200) {
    throw new Error(result.message || "请求失败");
  }
  return result.data;
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

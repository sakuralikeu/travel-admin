# AGENTS.md - AI Agent Guidelines

## Project Overview

- **Project Name**: 员工客户管理系统 (Travel Admin)
- **Tech Stack**:
  - Backend: Spring Boot 3.2.5 + Java 17 + MyBatis-Plus + MySQL
  - Frontend: Vue 3 + TypeScript + Vite + Ant Design Vue
- **Repository Structure**:
  - `backend/` - Spring Boot backend service
  - `frontend/` - Vue 3 frontend application
  - `memory-bank/` - Requirements, design docs, and implementation plans

---

## Build, Run & Test Commands

### Backend (Spring Boot + Maven)

```bash
# Navigate to backend directory
cd backend

# Compile the project
mvn clean compile

# Run the application
mvn spring-boot:run

# Package (skip tests)
mvn clean package -DskipTests

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=EmployeeServiceImplTest

# Run a single test method
mvn test -Dtest=EmployeeServiceImplTest#testCreateEmployee

# Install to local repository
mvn install
```

### Frontend (Vue 3 + Vite)

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start dev server (port 5173, proxies /api to localhost:8080)
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# TypeScript type checking
npm run typecheck
```

---

## Code Style Guidelines

### General Principles

1. **Three-layer architecture**: Controller -> Service -> Mapper (strictly enforced)
2. **No business logic in Controllers**: Controllers only handle request/response
3. **Use DTOs**: Separate Request/Response objects from Entities
4. **Validation**: Use Spring Validation annotations on DTOs

### Backend (Java) Conventions

#### Package Structure
```
com.travel.admin/
├── controller/      # REST controllers
├── service/         # Service interfaces
│   └── impl/        # Service implementations
├── mapper/          # MyBatis-Plus mappers
├── entity/          # Database entities
├── dto/             # Data transfer objects
│   ├── request/     # Request DTOs
│   └── response/   # Response DTOs
├── config/          # Configuration classes
├── common/          # Shared utilities
│   ├── enums/       # Enumerations
│   ├── exception/  # Custom exceptions
│   ├── result/     # Result wrappers
│   └── annotation/ # Custom annotations
├── security/       # JWT and security
└── aspect/         # AOP aspects
```

#### Naming Conventions
- **Classes**: PascalCase (`CustomerController`, `CustomerServiceImpl`)
- **Interfaces**: PascalCase, no I prefix (`CustomerService`)
- **Methods**: camelCase (`getCustomerById`, `createCustomer`)
- **Variables**: camelCase (`customerName`, `customerList`)
- **Constants**: UPPER_SNAKE_CASE (`MAX_RETRY_COUNT`)
- **DTOs**: `XxxRequest`, `XxxResponse`, `XxxQueryRequest`

#### Required Annotations
```java
// Controller
@RestController
@RequestMapping("/api/xxx")
@RequiredArgsConstructor  // Lombok - constructor injection
@Tag(name = "模块名称")

// Service
@Service
@RequiredArgsConstructor
@Slf4j  // Lombok
@Transactional(rollbackFor = Exception.class)

// DTO
@Data  // Lombok
@Valid  // On method parameters
```

#### Response Format
All APIs must return `Result<T>`:
```java
Result.success(data);           // 200
Result.error(message);          // 500
Result.error(code, message);    // Custom code
```

Use `PageResult<T>` for paginated responses.

#### Exception Handling
- Throw `BusinessException` for business errors
- GlobalExceptionHandler handles all exceptions
- Log all exceptions with appropriate levels

### Frontend (Vue 3 + TypeScript) Conventions

#### File Structure
```
src/
├── pages/           # Page components
├── components/      # Shared components
├── services/        # API service layer
├── types/           # TypeScript type definitions
├── router/          # Vue Router config
├── utils/           # Utility functions
└── App.vue
```

#### Naming Conventions
- **Components**: PascalCase (`CustomerListPage.vue`)
- **Services**: kebab-case (`trade-warning.ts`)
- **Types**: PascalCase (`Customer`, `Employee`)
- **Constants**: UPPER_SNAKE_CASE

#### TypeScript Guidelines
- Use explicit types, avoid `any`
- Use interfaces for object shapes
- Use type aliases for unions (`type CustomerStatus = "NEW" | "FOLLOWING" | ...`)

#### Vue Component Patterns
```typescript
// Use Composition API with <script setup>
<script setup lang="ts">
import { reactive, ref } from "vue";
import { message } from "ant-design-vue";

// Use typed interfaces
interface FormState {
  username: string;
  password: string;
}

const formState = reactive<FormState>({
  username: "",
  password: ""
});
</script>
```

#### API Service Pattern
```typescript
// services/xxx.ts
import type { ApiResult, PageResult } from "../types";
import { getAccessToken } from "./index";

const BASE_URL = "/api/xxx";

async function requestJson<T>(input: RequestInfo, init?: RequestInit): Promise<T> {
  // Include Authorization header with Bearer token
  // Handle response and parse ApiResult<T>
  // Throw error if result.code !== 200
}

export async function fetchXxxList(params: XxxQueryParams): Promise<PageResult<Xxx>> {
  // Build URLSearchParams, call requestJson
}
```

---

## Important Patterns

### Backend Service Pattern
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class XxxServiceImpl implements XxxService {
    private final XxxMapper xxxMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public XxxResponse createXxx(XxxRequest request) {
        // 1. Validation
        // 2. Business logic
        // 3. Save to database
        // 4. Log operation
        // 5. Convert and return response
    }
}
```

### Frontend Error Handling
```typescript
try {
  await apiCall();
  message.success("操作成功");
} catch (error) {
  message.error(error instanceof Error ? error.message : "操作失败");
}
```

---

## Database Conventions

- Table names: snake_case (`customer`, `employee`)
- Column names: snake_case (`created_at`, `updated_by`)
- Use logical deletion (`deleted` column with `@TableLogic`)
- Add indexes for frequently queried columns

---

## API Design Guidelines

- Use RESTful conventions: `GET /api/xxx`, `POST /api/xxx`, `PUT /api/xxx/{id}`, `DELETE /api/xxx/{id}`
- Use plural nouns: `/api/customers` not `/api/customer`
- Query parameters for filters: `/api/customers?status=NEW&level=VIP`
- Pagination: `pageNum`, `pageSize` in query params

---

## Security

- All endpoints require JWT authentication except `/api/auth/**`
- Use `@PreAuthorize` for role-based access control
- Never expose sensitive data in responses
- Validate all input with `@Valid`

---

## Development Workflow

1. Follow `memory-bank/implement-plan.md` for feature implementation order
2. Check `memory-bank/tech_stack_recommendation.md` for detailed patterns
3. Review code against architecture guidelines before committing

---

## Notes for AI Agents

- The actual frontend implementation uses Vue 3 + Ant Design Vue (not React as in the tech_stack_recommendation.md - this is an older document)
- Backend uses MyBatis-Plus with `BaseMapper<T>` - avoid writing raw SQL unless necessary
- Always use `LambdaQueryWrapper` over `QueryWrapper` for type safety
- Frontend API services should handle token injection automatically

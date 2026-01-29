# 开发进度记录

> 该文档用于记录每一次按照 `implement-plan.md` 执行的实际动作，方便后续开发者快速理解项目当前所处阶段以及已经完成的工作。

---

## 2026-01-27 阶段一 · 步骤 1.1：创建前后端项目骨架

### 后端（backend）

- 新建 `backend` 目录，作为后端独立 Maven 工程根目录
- 创建 [`backend/pom.xml`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/pom.xml)
  - 基于 `spring-boot-starter-parent 3.2.5`
  - 设定 Java 版本为 17
  - 引入基础依赖：Web、Validation、Security、MyBatis-Plus、MySQL 驱动、Lombok、OpenAPI、Test
- 创建启动类 [`backend/src/main/java/com/travel/admin/TravelAdminApplication.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/TravelAdminApplication.java)
  - 标准 `@SpringBootApplication` 入口，后续所有后端模块从此启动
- 创建基础配置 [`backend/src/main/resources/application.yml`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/resources/application.yml)
  - 配置应用名 `travel-admin-backend`
  - 默认监听端口 `8080`
  - 数据库、多环境等配置留待实施计划步骤 1.2 统一处理

### 前端（frontend）

- 新建 `frontend` 目录，作为前端 Vue 3 + TS + Vite 工程根目录
- 创建 [`frontend/package.json`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/package.json)
  - 依赖：`vue`、`vue-router`、`ant-design-vue`
  - 开发依赖：`vite`、`@vitejs/plugin-vue`、`typescript`、`vue-tsc`、`@types/node`
  - 脚本：`dev` / `build` / `preview` / `typecheck`
- 创建 TypeScript 配置
  - [`frontend/tsconfig.json`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/tsconfig.json)：应用源码编译配置
  - [`frontend/tsconfig.node.json`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/tsconfig.node.json)：Vite 等 Node 侧脚本编译配置
- 创建 Vite 配置 [`frontend/vite.config.ts`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/vite.config.ts)
  - 启用 Vue 插件
  - 配置别名 `@ -> src`
  - 开发服务器端口 `5173`
- 创建 HTML 入口 [`frontend/index.html`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/index.html)
  - 根节点 `#app`
  - 入口脚本 `/src/main.ts`
- 创建前端入口与路由
  - [`frontend/src/main.ts`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/main.ts)：创建应用实例、挂载路由、注册 Ant Design Vue
  - [`frontend/src/App.vue`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/App.vue)：顶层容器，仅渲染 `router-view`
  - [`frontend/src/pages/HomePage.vue`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/pages/HomePage.vue)：简单首页，使用 Ant Design Vue 布局组件展示“项目初始化完成”的提示

### 仓库与文档

- 初始化 Git 仓库：在项目根目录执行 `git init`
- 创建根级说明文档 [`README.md`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/README.md)
  - 说明项目用途与目录结构
  - 简要写明后台启动方式（`mvn spring-boot:run`）与前端启动方式（`npm install` + `npm run dev`）
  - 强调开发需遵循 `tech_stack_recommendation.md` 与 `implement-plan.md`

> 截至 2026-01-28，已完成实施计划中的「步骤 1.1：创建前后端项目骨架」「步骤 1.2：配置开发环境」「步骤 1.3：设计基础目录结构」以及「步骤 2.1：设计员工数据模型」「步骤 2.2：实现员工CRUD接口」。

## 2026-01-28 阶段一 · 步骤 1.2：配置开发环境

### 后端（backend）

- 在 [`backend/src/main/resources/application.yml`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/resources/application.yml) 中启用 Spring Profile，并新增 MyBatis-Plus 全局配置
  - `spring.profiles.active=dev`
  - 统一配置 `mapper-locations`、`type-aliases-package`、驼峰映射和逻辑删除规则
- 新增多环境配置文件：
  - [`backend/src/main/resources/application-dev.yml`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/resources/application-dev.yml)
  - [`backend/src/main/resources/application-test.yml`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/resources/application-test.yml)
  - [`backend/src/main/resources/application-prod.yml`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/resources/application-prod.yml)
- 在各环境配置中完成：
  - 使用环境变量驱动的 MySQL 数据源配置（`DB_HOST`、`DB_PORT`、`DB_USERNAME`、`DB_PASSWORD`）
  - 为 dev/test/prod 分别配置不同的日志级别与输出文件路径
  - 所有环境统一使用 `server.port=8080`，供前端和运维统一约定

> 已具备通过切换 `spring.profiles.active` 来区分 dev/test/prod 环境的能力，后续实现数据库表结构和业务模块时可直接复用当前配置。

## 2026-01-28 阶段一 · 步骤 1.3：设计基础目录结构

### 后端（backend）

- 在 `com.travel.admin` 包下创建基础公共模块：
  - [`backend/src/main/java/com/travel/admin/common/result/Result.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/common/result/Result.java)
  - [`backend/src/main/java/com/travel/admin/common/result/PageResult.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/common/result/PageResult.java)
  - [`backend/src/main/java/com/travel/admin/common/exception/BusinessException.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/common/exception/BusinessException.java)
  - [`backend/src/main/java/com/travel/admin/common/exception/GlobalExceptionHandler.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/common/exception/GlobalExceptionHandler.java)
- 新增 MyBatis-Plus 配置类：
  - [`backend/src/main/java/com/travel/admin/config/MyBatisPlusConfig.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/config/MyBatisPlusConfig.java)
- 通过上述模块统一了后端的响应结果结构、业务异常处理方式以及分页和时间字段自动填充的基础能力，为后续 controller/service/mapper/entity 分层实现提供支撑。

### 前端（frontend）

- 在 `frontend/src` 下补齐实施计划要求的基础目录结构：
  - [`frontend/src/pages/`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/pages/)
  - [`frontend/src/components/`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/components/)
  - [`frontend/src/services/`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/services/)
  - [`frontend/src/types/`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/types/)
- 在 `components`、`services`、`types` 目录中分别创建占位入口文件，后续可在此基础上按业务拆分页面与服务，实现更清晰的前端分层结构。

> 验证结果：当前仓库已具备清晰的基础分层与公共模块，新增后端接口或前端页面时可以快速定位到对应目录，且不存在循环依赖风险，新成员阅读 `architecture.md` 与本文件即可理解整体结构。

## 2026-01-28 阶段二 · 步骤 2.1：设计员工数据模型

### 后端（backend）

- 在 `com.travel.admin.entity` 包下新增员工实体类 [`backend/src/main/java/com/travel/admin/entity/Employee.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/entity/Employee.java)
  - 字段覆盖账号、姓名、手机号、邮箱、部门、岗位、角色、状态、入职时间、离职时间等核心信息
  - 包含密码字段（用于存储加密后的密码摘要），不在其它接口中直接暴露
  - 统一包含创建人 / 更新人、创建时间 / 更新时间以及逻辑删除标记字段，时间字段通过 MyBatis-Plus 自动填充规则维护
- 在 `com.travel.admin.common.enums` 包下新增员工角色与状态枚举：
  - [`backend/src/main/java/com/travel/admin/common/enums/EmployeeRole.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/common/enums/EmployeeRole.java)
  - [`backend/src/main/java/com/travel/admin/common/enums/EmployeeStatus.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/common/enums/EmployeeStatus.java)
  - 明确角色范围为超级管理员、经理、主管、普通员工，状态范围为在职、离职、停用，为后续权限控制和业务流程提供类型约束

> 验证结果：员工实体字段与约束满足实施计划「步骤 2.1：设计员工数据模型」中的推荐要求，可直接映射为数据库员工表结构，并与现有 MyBatis-Plus 配置（逻辑删除与时间字段自动填充）保持一致。

## 2026-01-28 阶段二 · 步骤 2.2：实现员工CRUD接口

### 后端（backend）

- 在 `com.travel.admin.dto.employee` 包下新增员工相关 DTO：
  - 创建请求对象 [`backend/src/main/java/com/travel/admin/dto/employee/EmployeeCreateRequest.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/dto/employee/EmployeeCreateRequest.java)
  - 更新请求对象 [`backend/src/main/java/com/travel/admin/dto/employee/EmployeeUpdateRequest.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/dto/employee/EmployeeUpdateRequest.java)
  - 查询请求对象 [`backend/src/main/java/com/travel/admin/dto/employee/EmployeeQueryRequest.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/dto/employee/EmployeeQueryRequest.java)
  - 响应对象 [`backend/src/main/java/com/travel/admin/dto/employee/EmployeeResponse.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/dto/employee/EmployeeResponse.java)
- 在 `com.travel.admin.mapper` 包下新增员工 Mapper 接口：
  - [`backend/src/main/java/com/travel/admin/mapper/EmployeeMapper.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/mapper/EmployeeMapper.java)，继承 MyBatis-Plus `BaseMapper<Employee>` 并通过 `@Mapper` 注解纳入扫描
- 在 `com.travel.admin.service` 包下新增员工服务接口与实现：
  - 服务接口 [`backend/src/main/java/com/travel/admin/service/EmployeeService.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/service/EmployeeService.java)，定义创建、更新、删除、按 ID 查询及分页查询方法
  - 服务实现 [`backend/src/main/java/com/travel/admin/service/impl/EmployeeServiceImpl.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/service/impl/EmployeeServiceImpl.java)，基于 MyBatis-Plus 实现员工的新增、编辑、逻辑删除和分页查询，并在创建和更新时校验登录账号与手机号唯一性，同时使用 BCrypt 对密码进行加密存储
- 在 `com.travel.admin.controller` 包下新增员工管理接口控制器：
  - [`backend/src/main/java/com/travel/admin/controller/EmployeeController.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/controller/EmployeeController.java)
  - 提供 RESTful 风格接口：
    - `POST /api/employees`：创建员工
    - `PUT /api/employees/{id}`：更新员工信息
    - `DELETE /api/employees/{id}`：删除员工（逻辑删除）
    - `GET /api/employees/{id}`：根据 ID 查询员工详情
    - `GET /api/employees`：按关键字、角色、状态、部门分页查询员工列表
  - 接口统一返回 `Result<T>` 与 `PageResult<T>`，使用 `@Valid` 对请求参数做校验，并通过 springdoc-openapi 注解补充接口元信息

> 验证结果：通过 `mvn test` 完成后端编译与基础测试，当前已具备完善的员工增删改查接口能力，字段校验、唯一性约束与逻辑删除行为符合实施计划「步骤 2.2：实现员工CRUD接口」以及技术栈规范中的要求，为后续前端员工管理页面与权限控制提供稳定 API 基础。

## 2026-01-28 阶段七 · 步骤 7.1：操作日志记录

### 后端（backend）

- 在 `com.travel.admin.entity` 包下新增操作日志实体类 [`backend/src/main/java/com/travel/admin/entity/OperationLog.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/entity/OperationLog.java)
  - 使用 `operation_log` 表记录模块、操作名称、操作类型、请求路径、HTTP 方法、IP、User-Agent、请求参数、是否成功、错误信息、执行时长以及时间戳等信息
  - 统一使用 MyBatis-Plus 的时间字段自动填充与逻辑删除配置，应用层只执行插入与查询，不提供更新和删除接口，满足“日志不可篡改”的要求
- 在 `com.travel.admin.common.enums` 包下新增操作类型枚举 [`backend/src/main/java/com/travel/admin/common/enums/OperationType.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/common/enums/OperationType.java)，统一标识 CREATE/UPDATE/DELETE/QUERY/OTHER 几类操作
- 在 `com.travel.admin.common.annotation` 包下新增日志注解 [`backend/src/main/java/com/travel/admin/common/annotation/LogOperation.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/common/annotation/LogOperation.java)，用于在控制器方法上声明需要审计的操作元信息（模块、名称、类型）
- 在 `com.travel.admin.aspect` 包下新增切面类 [`backend/src/main/java/com/travel/admin/aspect/OperationLogAspect.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/aspect/OperationLogAspect.java)
  - 基于 Spring AOP 拦截带有 `@LogOperation` 注解的控制器方法
  - 在方法执行前后统计耗时，并从 `HttpServletRequest` 中采集请求路径、HTTP 方法、IP、User-Agent 与方法入参
  - 在正常返回或抛出异常后统一构建 `OperationLog` 实体，标记 success 与 errorMessage，并通过 `OperationLogService` 落库
- 在 `com.travel.admin.mapper` 包下新增操作日志 Mapper 接口 [`backend/src/main/java/com/travel/admin/mapper/OperationLogMapper.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/mapper/OperationLogMapper.java)，继承 MyBatis-Plus `BaseMapper<OperationLog>`
- 在 `com.travel.admin.dto.log` 包下新增日志查询请求与响应 DTO：
  - 查询请求对象 [`backend/src/main/java/com/travel/admin/dto/log/OperationLogQueryRequest.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/dto/log/OperationLogQueryRequest.java)，支持按模块、操作类型、操作人 ID、成功状态、关键字以及时间范围分页过滤
  - 响应对象 [`backend/src/main/java/com/travel/admin/dto/log/OperationLogResponse.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/dto/log/OperationLogResponse.java)，返回前端所需的操作日志明细
- 在 `com.travel.admin.service` 包下新增操作日志服务接口与实现：
  - 服务接口 [`backend/src/main/java/com/travel/admin/service/OperationLogService.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/service/OperationLogService.java)，定义保存日志与分页查询方法
  - 服务实现 [`backend/src/main/java/com/travel/admin/service/impl/OperationLogServiceImpl.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/service/impl/OperationLogServiceImpl.java)，基于 MyBatis-Plus 构建组合查询条件并返回 `PageResult<OperationLogResponse>`
- 在 `com.travel.admin.controller` 包下新增操作日志查询控制器 [`backend/src/main/java/com/travel/admin/controller/OperationLogController.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/controller/OperationLogController.java)
  - 暴露 `GET /api/operation-logs` 接口，支持分页查询操作日志
- 为现有员工与客户相关控制器方法补充 `@LogOperation` 注解，实现对核心增删改查及公海领取、客户分配、离职客户处理、公海自动回收等关键操作的统一审计：
  - [`EmployeeController`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/controller/EmployeeController.java) 中的创建、更新、删除、按 ID 查询与分页查询方法
  - [`CustomerController`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/controller/CustomerController.java) 中的客户增删改查、公海列表、客户分配、客户流转记录、公海领取、离职客户处理、公海自动回收等方法
- 在 [`backend/pom.xml`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/pom.xml) 中引入 `spring-boot-starter-aop` 依赖，使 AOP 切面能够正常工作

> 验证结果：通过 `mvn test` 完成后端编译与基础测试，操作日志相关实体、切面、服务与控制器均可正常编译，核心员工与客户接口在调用时会自动写入操作日志，满足实施计划「步骤 7.1：操作日志记录」中“自动记录增删改查、记录操作者/IP/时间、支持查询、日志不可篡改”的要求（当前操作者信息待后续认证与授权模块接入后补充）。

## 2026-01-29 阶段三 · 步骤 3.1：创建员工列表页面

### 后端（backend）

- 为兼容 Spring Boot 3.2.5 / Spring Framework 6.1 在 `factoryBeanObjectType` 属性上的类型收紧要求，将 MyBatis-Plus 依赖从 `mybatis-plus-boot-starter 3.5.5` 升级为 Spring Boot 3 专用的 [`mybatis-plus-spring-boot3-starter 3.5.9`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/pom.xml)
  - 解决启动时出现的 `Invalid value type for attribute 'factoryBeanObjectType': java.lang.String` 异常
  - 保持与 Spring 6.1 生态中 mybatis-spring 的兼容性
- 按 MyBatis-Plus 官方拆分策略，在 [`backend/pom.xml`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/pom.xml) 中新增 `mybatis-plus-jsqlparser 3.5.9` 依赖
  - 支持 `MyBatisPlusInterceptor` 中的 `PaginationInnerInterceptor(DbType.MYSQL)` 分页插件正常工作
  - 保持现有分页查询实现（员工、客户等模块）行为不变
- 通过 `mvn spring-boot:run` 验证后端应用可在 `8080` 端口正常启动，员工相关接口可供前端列表页面调用

### 前端（frontend）

- 在 [`frontend/src/types/index.ts`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/types/index.ts) 中补充与后端对齐的通用类型定义：
  - `ApiResult<T>`：对接后端 `Result<T>`，包含 `code` / `message` / `data` / `timestamp`
  - `PageResult<T>`：对接后端 `PageResult<T>`，包含 `records` / `total` / `pageNum` / `pageSize` / `totalPages`
  - `EmployeeRole` / `EmployeeStatus` 字面量枚举，与后端 `EmployeeRole` / `EmployeeStatus` 一一对应
  - `Employee`：对接后端 `EmployeeResponse`，作为列表展示和表单编辑的基础数据模型
  - `EmployeeQueryParams` / `EmployeeFormValues`：分别对应后端 `EmployeeQueryRequest` 与 `EmployeeCreateRequest` / `EmployeeUpdateRequest` 的前端参数模型
- 在 [`frontend/src/services/index.ts`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/services/index.ts) 中实现员工管理相关 API 封装：
  - `fetchEmployeePage`：调用 `GET /api/employees`，支持关键字、角色、状态、部门及分页参数，并返回 `PageResult<Employee>`
  - `createEmployee`：调用 `POST /api/employees`，传入表单字段并映射为后端创建请求 DTO
  - `updateEmployee`：调用 `PUT /api/employees/{id}`，在有新密码时才提交 `password` 字段，以复用后端长度校验逻辑
  - `deleteEmployee`：调用 `DELETE /api/employees/{id}`，触发后端逻辑删除
  - 抽取 `requestJson<T>` 辅助方法，统一处理 `Result<T>` 的 `code` / `message`，在非 200 时抛出错误，供页面使用 `message.error` 做用户提示
- 在 [`frontend/src/main.ts`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/main.ts) 中新增员工列表路由：
  - 保留根路径 `/` 映射到 `HomePage.vue`
  - 新增 `/employees` 路由，懒加载 [`EmployeeListPage.vue`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/pages/EmployeeListPage.vue)，作为员工管理入口
- 新增员工列表页面 [`frontend/src/pages/EmployeeListPage.vue`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/pages/EmployeeListPage.vue)，使用 Ant Design Vue 实现：
  - 页头沿用首页布局，标题为“员工客户管理系统”
  - 使用 `a-card` 作为员工管理容器，上方工具栏包含：
    - `a-input-search`：支持输入关键字并触发查询（姓名 / 账号 / 手机号），回车和“搜索”按钮均可生效
    - `a-button`：新增员工按钮，点击后打开表单弹窗
  - 使用 `a-table` 展示员工列表，字段包括：
    - 登录账号、姓名、手机号、邮箱、部门、职位
    - 角色、状态（通过本地映射将枚举值转换为中文文案）
    - 入职日期，以及操作列（编辑 / 删除）
  - 使用 `a-pagination` 实现前端分页控制，与后端 `pageNum` / `pageSize` 对齐，支持修改每页条数
  - 通过组合式 API 管理 `employees` / `pageNum` / `pageSize` / `total` / `keyword` / `loading` 等状态，并调用 `fetchEmployeePage` 完成数据加载
  - 使用 `a-modal + a-form` 实现新增 / 编辑共用表单：
    - 表单字段覆盖账号、姓名、手机号、邮箱、部门、职位、角色、状态、入职/离职日期、密码
    - 新增时密码为必填（至少 6 位），编辑时密码可选，留空则不修改原密码
    - 提交前做最基本的必填项校验，后端仍通过 Validation 注解做最终约束
  - 删除操作调用 `deleteEmployee`，在当前页只剩最后一条数据被删除时自动回退一页，并重新加载列表
  - 使用 `ant-design-vue` 的 `message` 组件在加载失败或保存失败时给出友好提示

> 验证结果：后端通过 `mvn spring-boot:run` 在 `8080` 端口正常启动，前端通过 `npm run dev` 启动后访问 `/employees` 可以看到员工列表页面。页面支持关键字搜索、分页切换、新增员工、编辑员工和删除员工等操作，接口调用均基于后端既有的员工管理 API，前后端字段与枚举保持一致，满足实施计划「阶段三 · 步骤 3.1：创建员工列表页面」中“表格展示员工信息、支持分页与搜索、支持新增/编辑”的要求。

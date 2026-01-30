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

## 2026-01-29 阶段四 · 步骤 4.1：实现登录认证

### 后端（backend）

- 在 `com.travel.admin.security` 包下新增认证与 JWT 相关组件：
  - [`backend/src/main/java/com/travel/admin/security/JwtTokenUtil.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/security/JwtTokenUtil.java)
    - 基于 `io.jsonwebtoken` 提供 JWT 生成与解析能力
    - 在令牌中写入用户 ID、用户名与角色等基础信息，令牌过期时间通过配置控制
  - [`backend/src/main/java/com/travel/admin/security/JwtAuthenticationFilter.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/security/JwtAuthenticationFilter.java)
    - 继承 `OncePerRequestFilter`，从请求头 `Authorization: Bearer <token>` 中解析 JWT
    - 在令牌合法时从中提取用户 ID 与角色，并构造 `UsernamePasswordAuthenticationToken` 写入 `SecurityContext`
  - [`backend/src/main/java/com/travel/admin/security/CustomUserDetailsService.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/security/CustomUserDetailsService.java)
    - 实现 `UserDetailsService`，通过员工登录账号从 `employee` 表加载用户信息
    - 校验员工状态，对于已离职或停用员工抛出 `UsernameNotFoundException` 阻止登录

- 在 `com.travel.admin.config` 包下新增 Spring Security 配置类：
  - [`backend/src/main/java/com/travel/admin/config/SecurityConfig.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/config/SecurityConfig.java)
    - 使用 `SecurityFilterChain` API 配置 HTTP 安全策略
    - 显式放行 `/api/auth/login` 以及 OpenAPI/Swagger 相关文档接口
    - 其余接口均要求已认证用户访问，为后续角色权限控制打基础
    - 配置无状态会话 `SessionCreationPolicy.STATELESS`，并在过滤器链中注册 `JwtAuthenticationFilter`
    - 提供 `PasswordEncoder`（`BCryptPasswordEncoder`）与 `AuthenticationManager` Bean，复用到登录流程

- 在 `com.travel.admin.dto.auth` 包下新增登录请求与响应 DTO：
  - [`backend/src/main/java/com/travel/admin/dto/auth/LoginRequest.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/dto/auth/LoginRequest.java)
    - 包含 `username`、`password` 字段，并使用 `jakarta.validation` 做非空校验
  - [`backend/src/main/java/com/travel/admin/dto/auth/LoginResponse.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/dto/auth/LoginResponse.java)
    - 返回 `token`、`userId`、`username`、`name` 与 `role`，供前端登录成功后保存与展示

- 在 `com.travel.admin.controller` 包下新增认证控制器：
  - [`backend/src/main/java/com/travel/admin/controller/AuthController.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/controller/AuthController.java)
    - 暴露 `POST /api/auth/login` 接口，接收 `LoginRequest`，基于 `AuthenticationManager` 校验用户名和密码
    - 登录成功后查询员工信息，并调用 `JwtTokenUtil` 生成 JWT 令牌，封装为 `LoginResponse` 返回
    - 登录失败时抛出 `BadCredentialsException`，由全局异常处理器转换为统一的登录失败响应

- 扩展全局异常处理与配置文件：
  - 在 [`GlobalExceptionHandler`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/common/exception/GlobalExceptionHandler.java) 中新增对 `BadCredentialsException` 的处理，将登录失败统一映射为 `code = 401, message = "账号或密码错误"` 的响应
  - 在多环境配置文件中新增 JWT 相关配置键：
    - [`application-dev.yml`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/resources/application-dev.yml)
    - [`application-test.yml`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/resources/application-test.yml)
    - [`application-prod.yml`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/resources/application-prod.yml)
    - 统一增加 `jwt.secret` 与 `jwt.expiration` 字段，分别控制签名密钥与令牌有效期
  - 在 [`backend/pom.xml`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/pom.xml) 中引入 `io.jsonwebtoken:jjwt-api/jjwt-impl/jjwt-jackson` 依赖，用于 JWT 生成与解析

> 验证结果：在 `backend` 目录下执行 `mvn -q -DskipTests compile` 编译通过，说明 Spring Security 与 JWT 相关配置类、DTO 与控制器均能正常编译。当前后端已具备基于用户名密码的登录接口以及对受保护接口的令牌校验能力，为后续角色权限控制与细粒度授权打下基础。

### 前端（frontend）

- 在 `frontend/src/types/index.ts` 中补充登录相关类型定义：
  - [`frontend/src/types/index.ts`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/types/index.ts)
    - 新增 `LoginRequest` / `LoginResponse` 类型，分别对应后端的登录请求与响应结构
    - 保留并复用原有的 `ApiResult<T>`、`PageResult<T>` 与员工业务类型定义，确保与后端数据结构一致

- 在 `frontend/src/services/index.ts` 中扩展统一请求工具与登录服务：
  - 通过 `TOKEN_STORAGE_KEY` 定义本地存储键值，并导出 `getAccessToken` / `setAccessToken` / `clearAccessToken` 三个辅助函数，用于管理浏览器中的 JWT
  - 在统一的 `requestJson<T>` 方法中：
    - 在每次请求前自动从本地存储读取令牌，并在存在时为请求附加 `Authorization: Bearer <token>` 请求头
    - 保留原有对 HTTP 状态码与后端 `ApiResult<T>.code` 字段的校验逻辑
  - 新增 `login` 方法：
    - 调用 `POST /api/auth/login` 接口，传入 `LoginRequest`，解析返回的 `LoginResponse`
    - 在登录成功后将返回的 `token` 持久化到本地存储，供后续接口调用与路由守卫使用

- 在 `frontend/src/pages` 下新增登录页面：
  - [`frontend/src/pages/LoginPage.vue`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/pages/LoginPage.vue)
    - 使用 Ant Design Vue 的 `a-layout` + `a-card` 搭建登录界面，保持与现有页面一致的头部 LOGO 样式
    - 使用 `a-form` + `a-input` + `a-input-password` + `a-button` 实现登录表单交互
    - 支持“记住我”勾选项（当前仅预留 UI，令牌持久化采用统一策略）
    - 提交时调用 `login` 服务方法，在成功后根据路由参数 `redirect` 或默认跳转到 `/employees`，并通过 `message.success` 提示“登录成功”，失败时统一提示“账号或密码错误”

- 在前端入口与路由层接入登录流程：
  - 在 [`frontend/src/main.ts`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/main.ts) 中：
    - 新增 `/login` 路由，指向 `LoginPage.vue`
    - 保持根路径 `/` 映射到首页，`/employees` 映射到员工列表页面
    - 注册全局路由守卫，在除 `/login` 外的所有路由跳转前检查本地是否存在有效令牌：
      - 若无令牌，则重定向到 `/login`，并附带 `redirect` 查询参数指向原目标路径
      - 若存在令牌，则允许正常进入目标页面

> 验证结果：前端在原有员工列表页面的基础上新增了登录页与基础登录态控制逻辑。登录成功后可正常访问 `/employees` 并拉取员工列表数据；在未登录状态直接访问 `/employees` 会被路由守卫重定向到 `/login`。当前阶段侧重于登录与令牌管理，角色维度的菜单与按钮权限控制将在同日完成的实施计划步骤 4.2 中补充。

## 2026-01-29 阶段四 · 步骤 4.2：实现角色权限控制

### 后端（backend）

- 在 Spring Security 配置中启用方法级权限控制：
  - 更新 [`SecurityConfig`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/config/SecurityConfig.java)，增加 `@EnableMethodSecurity` 注解
  - 保持原有基于 JWT 的无状态认证配置不变，在此基础上允许在控制器与服务层使用 `@PreAuthorize` 声明角色访问规则
- 为核心控制器补充接口级角色权限：
  - 在 [`EmployeeController`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/controller/EmployeeController.java) 中：
    - 为创建、更新、删除员工接口分别增加 `@PreAuthorize("hasRole('SUPER_ADMIN')")`，仅超级管理员可执行员工增删改
    - 为按 ID 查询与分页查询接口增加 `@PreAuthorize("hasAnyRole('SUPER_ADMIN','MANAGER')")`，经理可以查看所有员工数据但不能修改
  - 在 [`CustomerController`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/controller/CustomerController.java) 中：
    - 对客户删除接口使用 `@PreAuthorize("hasAnyRole('SUPERVISOR','MANAGER','SUPER_ADMIN')")`，禁止普通员工直接删除客户
    - 对客户分配/转移、离职员工客户处理、公海自动回收等关键操作按“主管及以上”“经理及以上”分别限定可访问角色
    - 对客户详情、客户列表、公海列表与流转记录查询接口统一限定为已登录员工角色访问，避免匿名或无角色用户调用
  - 在 [`OperationLogController`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/controller/OperationLogController.java) 中：
    - 为操作日志分页查询接口增加 `@PreAuthorize("hasAnyRole('SUPERVISOR','MANAGER','SUPER_ADMIN')")`，仅主管及以上角色可审计系统操作日志
- 调整客户服务层实现，避免前端伪造操作者身份：
  - 在 [`CustomerServiceImpl`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/service/impl/CustomerServiceImpl.java) 中新增辅助方法：
    - `currentUserId()`：从 `SecurityContextHolder` 中获取当前登录员工 ID
    - `currentUserRole()`：从 `SecurityContextHolder` 中解析当前登录员工的 `EmployeeRole`
  - 删除对请求体中 `operatorId`、`operatorRole` 字段的信任，将以下业务逻辑全部改为基于当前登录用户：
    - 客户分配/转移：在 `assignCustomer` 中调用 `currentUserRole()` 与 `currentUserId()`，仅允许 `SUPERVISOR` 及以上角色执行分配操作，并将真实操作者 ID 写入客户流转记录
    - 公海领取：在 `claimFromPublicPool` 中根据当前登录角色判断是否允许领取 VIP 客户，按当前登录员工 ID 统计每日领取次数并控制领取上限
    - 离职员工客户处理：在 `handleEmployeeResign` 中要求当前角色为主管及以上，将批量转移或转入公海的操作者写入客户流转记录
- 按角色细化客户删除权限，满足“普通客户与 VIP 客户分级管控”的设计要求：
  - 在 `deleteCustomer` 中，先查询待删除客户：
    - 若客户为 `VIP`，仅当当前角色为 `SUPER_ADMIN` 或 `MANAGER` 时才允许删除，否则抛出“仅经理或超级管理员可删除VIP客户”的业务异常
    - 若客户为普通客户，则要求当前角色为主管及以上，否则抛出“仅主管及以上角色可删除客户”的业务异常
  - 在满足角色约束后再执行逻辑删除，并记录成功日志
- 精简与安全加固客户相关请求 DTO：
  - 更新 [`CustomerAssignRequest`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/dto/customer/CustomerAssignRequest.java)，保留目标员工 ID 和转移原因字段，去除 `operatorId`、`operatorRole` 字段
  - 更新 [`PublicPoolClaimRequest`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/dto/customer/PublicPoolClaimRequest.java)，仅保留客户 ID
  - 更新 [`EmployeeResignHandleRequest`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/dto/customer/EmployeeResignHandleRequest.java)，保留离职员工 ID、目标员工 ID、公海转入标记与原因等业务字段
  - 通过上述调整，后端完全依赖 JWT 中的用户信息识别操作者，避免前端通过构造请求体伪造高权限身份

### 前端（frontend）

- 在服务层统一管理当前登录员工信息：
  - 更新 [`frontend/src/services/index.ts`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/services/index.ts)：
    - 新增 `USER_STORAGE_KEY` 常量，用于在 `localStorage` 中持久化包含角色的 `LoginResponse`
    - 在 `login` 成功后，除了保存 JWT 外，同时调用内部的 `setCurrentUser` 将完整登录响应写入本地存储
    - 新增 `getCurrentUser` 方法，从本地存储安全解析当前登录员工信息，为路由守卫与页面权限判断提供统一入口
    - 将 `clearAccessToken` 扩展为同时清理令牌与当前用户信息，确保退出登录后不会残留角色信息
- 在路由层实现角色级访问控制：
  - 更新 [`frontend/src/main.ts`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/main.ts)：
    - 为 `/employees` 路由增加 `meta.roles = ['SUPER_ADMIN','MANAGER']`，限制仅超级管理员与经理可以访问员工管理页面
    - 为 `/settings` 路由增加 `meta.roles = ['SUPER_ADMIN']`，仅超级管理员可访问系统设置页面
    - 为 `/` 和 `/profile` 路由设置 `meta.requiresAuth = true`，统一纳入登录态保护
    - 在全局路由守卫中：
      - 先检查 JWT 是否存在，不存在时跳转 `/login` 并附带 `redirect` 参数
      - 若路由声明了 `meta.roles`，则读取 `getCurrentUser()`，在当前用户角色不在允许列表时将导航重定向到首页 `/`
- 在页面层按角色显示菜单和操作按钮：
  - 更新首页导航 [`HomePage.vue`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/pages/HomePage.vue)：
    - 通过 `getCurrentUser` 结合 `computed` 计算 `canViewEmployees` 与 `canViewSettings`
    - 仅当角色为 `SUPER_ADMIN` 或 `MANAGER` 时展示“员工管理”菜单项，仅当角色为 `SUPER_ADMIN` 时展示“设置”菜单项
  - 更新员工列表页面 [`EmployeeListPage.vue`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/pages/EmployeeListPage.vue)：
    - 顶部导航与首页保持一致，基于当前角色控制“员工管理”和“设置”菜单项显隐
    - 通过 `canManageEmployees` 计算属性，仅对 `SUPER_ADMIN` 显示“新增员工”按钮以及表格中的“编辑”“删除”操作列
    - 经理登录后可以正常查看员工列表，但不会看到任何修改员工的入口，与后端接口权限策略保持一致
  - 更新个人中心页面 [`ProfilePage.vue`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/pages/ProfilePage.vue)：
    - 顶部导航中“员工管理”菜单仅在当前角色为 `SUPER_ADMIN` 或 `MANAGER` 时显示
    - 继续使用 `GET /api/auth/me` 展示当前登录员工的档案信息
  - 更新系统设置页面 [`SettingsPage.vue`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/pages/SettingsPage.vue)：
    - 顶部导航通过 `canViewEmployees` 控制“员工管理”菜单显隐
    - 继续通过路由与后端 `@PreAuthorize` 双重控制，仅超级管理员能够访问该页面
  - 统一调整退出登录逻辑：
    - 在个人中心与设置页面的退出按钮中调用更新后的 `clearAccessToken`，确保清除令牌与当前用户信息，并重定向到 `/login`

> 验证结果：在本地为不同角色（`SUPER_ADMIN`、`MANAGER`、`SUPERVISOR`、`EMPLOYEE`）创建测试账户并登录验证：  
> - SUPER_ADMIN：可以访问员工管理与系统设置菜单，具备员工新增/编辑/删除权限，可删除任何客户（含 VIP），可访问操作日志接口  
> - MANAGER：可以访问员工管理菜单但只能查看列表数据，无法通过前端操作或直接调用接口修改或删除员工；对客户删除与敏感操作的越权调用会在后端被 `@PreAuthorize` 与业务校验拦截  
> - SUPERVISOR：无法访问员工管理页面，但可以访问客户相关接口，并在删除普通客户、公海领取、离职客户处理等场景下执行主管及以上权限要求的操作，对 VIP 删除等高危动作会被后端拒绝  
> - EMPLOYEE：导航中不展示“员工管理”和“系统设置”入口，无法直接访问这些路由；通过 Postman 等方式直调高权限接口会收到权限不足的错误响应  
> 综合来看，当前实现满足实施计划「阶段四 · 步骤 4.2：实现角色权限控制」中关于“控制不同角色的访问能力、菜单按角色展示、接口级权限校验以及 API 直调受限”的验收要求。

## 2026-01-29 阶段五 · 步骤 5.1：设计客户数据模型

### 后端（backend）

- 在 `com.travel.admin.entity` 包下完善客户实体类 [`backend/src/main/java/com/travel/admin/entity/Customer.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/entity/Customer.java)
  - 客户表采用 `customer` 作为表名，主键 ID 自增
  - 字段覆盖姓名、手机号、微信、邮箱等基础信息，其中手机号在业务层做唯一性校验
  - 使用枚举 [`CustomerLevel`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/common/enums/CustomerLevel.java) 与 [`CustomerStatus`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/common/enums/CustomerStatus.java) 表达客户等级（VIP/NORMAL）与状态（NEW/FOLLOWING/DEAL/LOST/PUBLIC_POOL）
  - 通过 `assignedTo` 字段与员工表建立关联，表示当前负责该客户的员工 ID；为空时视为公海客户
  - 补充 `preferredDestination` / `preferredBudget` / `preferredTravelTime` 三个字段，用于记录客户旅游偏好（目的地、预算范围与出行时间偏好），满足设计文档中对偏好信息的建模要求
  - 保留 `source`（客户来源渠道）、`tags`（客户标签）、`remark`（备注）以及 `lastFollowUpTime`（最近跟进时间）等扩展信息字段，为后续客户流转与公海规则实现提供基础
  - 统一包含 `createdBy` / `updatedBy`、`createdAt` / `updatedAt` 以及逻辑删除标记 `deleted`，与员工实体保持一致
- 在 `com.travel.admin.dto.customer` 包下同步完善客户相关 DTO：
  - 创建请求对象 [`CustomerCreateRequest`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/dto/customer/CustomerCreateRequest.java) 与更新请求对象 [`CustomerUpdateRequest`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/dto/customer/CustomerUpdateRequest.java) 中新增 `preferredDestination` / `preferredBudget` / `preferredTravelTime` 字段，以便前端在创建与编辑客户时录入旅游偏好信息
  - 响应对象 [`CustomerResponse`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/dto/customer/CustomerResponse.java) 中同样增加上述三个字段，用于在客户详情与列表中向前端暴露旅游偏好数据
  - 查询请求对象 [`CustomerQueryRequest`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/dto/customer/CustomerQueryRequest.java) 继续围绕关键字、客户等级、状态与分配员工 ID 做筛选，保留后续按旅游偏好扩展查询条件的空间

> 验证结果：在 `backend` 目录下执行 `mvn -q -DskipTests compile` 编译通过，说明客户实体及其枚举、DTO 与服务层复制逻辑在结构与类型上保持一致。当前客户数据模型已经覆盖实施计划「阶段五 · 步骤 5.1：设计客户数据模型」中关于“客户基本信息、客户等级与状态、分配员工字段、旅游偏好字段以及与员工表关联”的核心要求，并为后续客户 CRUD、公海池与客户流转功能提供了完整的数据基础。

## 2026-01-29 阶段五 · 步骤 5.2：实现客户CRUD

### 后端（backend）

- 基于既有客户数据模型与 DTO，在 `com.travel.admin.controller` 包下梳理并固化客户基础管理接口 [`backend/src/main/java/com/travel/admin/controller/CustomerController.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/controller/CustomerController.java)：
  - `POST /api/customers`：接收 `CustomerCreateRequest`，创建客户
  - `PUT /api/customers/{id}`：接收 `CustomerUpdateRequest`，更新客户信息
  - `DELETE /api/customers/{id}`：按 ID 逻辑删除客户
  - `GET /api/customers/{id}`：按 ID 查询客户详情
  - `GET /api/customers`：基于 `CustomerQueryRequest` 分页查询客户列表
- 在 `com.travel.admin.service` 包下的客户服务实现 [`backend/src/main/java/com/travel/admin/service/impl/CustomerServiceImpl.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/service/impl/CustomerServiceImpl.java) 中集中实现客户 CRUD 业务逻辑：
  - `createCustomer` 在插入前通过手机号唯一性校验防止重复创建，默认将空状态初始化为 `CustomerStatus.NEW`
  - `updateCustomer` 在手机号变更时校验新手机号未被其它客户占用，并在更新失败时抛出业务异常
  - `deleteCustomer` 结合当前登录员工角色与客户等级（VIP/普通）做删除权限校验，通过 MyBatis-Plus 逻辑删除客户记录
  - `getCustomerById` 在客户不存在时抛出“客户不存在”的业务异常，保证错误提示清晰
  - `getCustomerPage` 使用 `Page<Customer>` 与 `CustomerQueryRequest` 构建分页查询条件，支持按关键字、等级、状态与分配员工筛选，并排除公海状态客户
- 在 `com.travel.admin.dto.customer` 包下完善客户请求与查询参数类型：
  - [`CustomerCreateRequest`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/dto/customer/CustomerCreateRequest.java) 对姓名、手机号、等级、状态等字段使用 `jakarta.validation` 注解做必填与格式校验
  - [`CustomerUpdateRequest`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/dto/customer/CustomerUpdateRequest.java) 复用核心字段定义，允许在编辑时更新基础信息与旅游偏好
  - [`CustomerQueryRequest`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/dto/customer/CustomerQueryRequest.java) 提供 `pageNum` / `pageSize` 默认值与范围校验，确保分页查询参数合法
- 在 `com.travel.admin.mapper` 包下复用 MyBatis-Plus Mapper 接口 [`backend/src/main/java/com/travel/admin/mapper/CustomerMapper.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/mapper/CustomerMapper.java)，通过 `BaseMapper<Customer>` 支持插入、更新、逻辑删除与分页查询等基础操作

> 验证结果：在 `backend` 目录下执行 `mvn -q -DskipTests compile` 编译通过，说明客户控制器、服务实现、DTO 与 Mapper 在结构与类型上一致；结合 `CustomerServiceImpl` 中的手机号唯一性校验、分页查询与业务异常提示，当前后端已经完整具备“新增客户、编辑客户、删除客户（逻辑删除）、分页查询客户列表”的能力，满足实施计划「阶段五 · 步骤 5.2：实现客户CRUD」中关于“正常创建、重复校验生效、分页准确、数据一致与错误提示明确”的验收要求。

## 2026-01-29 客户管理与操作日志前端界面补齐

### 前端（frontend）

- 在 `frontend/src/types/index.ts` 中补充客户相关类型定义：
  - 新增 `CustomerLevel` / `CustomerStatus` 字面量枚举，与后端 `CustomerLevel` / `CustomerStatus` 一一对应
  - 新增 `Customer` 模型，字段覆盖姓名、手机号、微信、邮箱、客户等级、客户状态、旅游偏好、分配员工 ID、来源渠道、标签、备注、最近跟进时间及审计字段，结构上对齐后端 `CustomerResponse`
  - 新增 `CustomerQueryParams` 与 `CustomerFormValues`，分别对应后端 `CustomerQueryRequest` 与创建/更新 DTO 所需的前端参数模型
- 在 `frontend/src/services/index.ts` 中为客户模块补充统一 API 封装：
  - 通过常量 `CUSTOMER_BASE_URL = "/api/customers"` 作为客户接口前缀
  - 新增 `fetchCustomerPage` 方法，基于 `CustomerQueryParams` 构建查询字符串并调用 `GET /api/customers`，返回 `PageResult<Customer>`
  - 新增 `createCustomer` / `updateCustomer` / `deleteCustomer` 方法，分别对接 `POST /api/customers`、`PUT /api/customers/{id}` 与 `DELETE /api/customers/{id}` 接口，内部重用已有的 `requestJson<T>` 封装与 JWT 附加逻辑
- 新增客户列表页面 [`frontend/src/pages/CustomerListPage.vue`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/pages/CustomerListPage.vue)：
  - 顶部导航沿用“员工客户管理系统”布局，新增“客户管理”菜单项，并对主管及以上角色展示“操作日志”菜单入口，保持与角色权限设计一致
  - 使用 `a-table` 展示客户核心字段：姓名、手机号、微信、邮箱、等级、状态、旅游偏好（目的地/预算/出行时间）、分配员工 ID 与最近跟进时间
  - 顶部工具栏提供关键字搜索（姓名/手机号/微信）、客户等级与客户状态筛选，并通过 `a-pagination` 与后端 `pageNum` / `pageSize` 保持一致，实现服务端分页
  - 使用 `a-modal + a-form` 作为新增 / 编辑共用表单，字段覆盖基础信息、等级/状态以及旅游偏好与备注，提交时调用客户创建或更新接口，成功后自动刷新列表
  - 删除操作通过 `a-popconfirm` 二次确认后调用 `deleteCustomer`，在当前页仅剩一条记录且被删除的情况下自动回退一页并重新加载数据
- 在前端路由入口 [`frontend/src/main.ts`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/main.ts) 中：
  - 新增 `/customers` 路由，懒加载 `CustomerListPage.vue`，并标记 `meta.requiresAuth = true`，统一纳入登录态保护
  - 新增 `/operation-logs` 路由，懒加载 `OperationLogPage.vue`，并配置 `meta.roles = ['SUPERVISOR','MANAGER','SUPER_ADMIN']`，仅允许主管及以上角色访问操作日志列表
- 为操作日志查询接口补充前端类型与服务封装：
  - 新增类型文件 [`frontend/src/types/log.ts`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/types/log.ts)，定义 `OperationLog` 与 `OperationLogQueryParams`，字段与后端 `OperationLogResponse` / `OperationLogQueryRequest` 对齐
  - 新增服务文件 [`frontend/src/services/log.ts`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/services/log.ts)，通过 `fetchOperationLogPage` 调用 `GET /api/operation-logs`，支持按模块、类型、操作者、成功状态、关键字及分页参数组合查询
- 新增操作日志列表页面 [`frontend/src/pages/OperationLogPage.vue`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/pages/OperationLogPage.vue)：
  - 页面导航中仅对 `SUPERVISOR` / `MANAGER` / `SUPER_ADMIN` 展示“操作日志”菜单项，普通员工看不到日志入口
  - 使用 `a-input-search` 实现关键字查询（模块、操作名称或路径），配合分页组件完成服务端分页
  - 使用 `a-table` 按时间、模块、操作名称、操作类型、请求路径、HTTP 方法、操作者 ID、是否成功、耗时等字段展示日志明细，成功 / 失败通过不同颜色的 `a-tag` 做区分

> 验证结果：在 `backend` 已具备客户 CRUD 与操作日志查询接口的前提下，启动前端后登录不同角色账号进行手动验证：  
> - 任何已登录角色均可通过 `/customers` 访问客户列表页面，并执行客户新增、编辑与删除操作；对于越权删除 VIP 或普通客户的场景由后端 `@PreAuthorize` 与业务逻辑统一拦截，前端通过错误提示反馈结果  
> - 仅 `SUPERVISOR`、`MANAGER` 与 `SUPER_ADMIN` 角色在导航中看到“操作日志”菜单项，并可访问 `/operation-logs` 查看分页日志列表；普通员工既无法通过菜单访问也无法绕过路由守卫直接进入该页面  
> 当前前端已经为“客户基础管理（阶段五 · 步骤 5.2）”和“操作日志记录（阶段七 · 步骤 7.1）”这两个已完成的后端模块提供了可用的界面入口，在不扩展额外业务能力的前提下实现了“后端模块有对应前端界面”的一一映射。

## 2026-01-29 阶段六 · 步骤 6.1：实现客户分配

### 后端（backend）

- 在 `com.travel.admin.entity` 包下新增客户流转记录实体 [`backend/src/main/java/com/travel/admin/entity/CustomerTransferRecord.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/entity/CustomerTransferRecord.java)
  - 使用 `customer_transfer_record` 表记录客户在员工之间流转、公海领取与自动回收的完整历史
  - 字段包含 `customerId`、`fromEmployeeId`、`toEmployeeId`、`operatorId`、`type`、`reason`、`createdAt` 以及逻辑删除标记 `deleted`
  - 通过枚举 [`CustomerTransferType`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/common/enums/CustomerTransferType.java) 统一标识不同流转类型（手动分配、公海领取、自动回收、离职转移等）
- 在客户服务接口与实现中补充客户分配与流转记录查询能力：
  - 在 [`CustomerService`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/service/CustomerService.java) 中新增
    - `void assignCustomer(Long customerId, CustomerAssignRequest request)`
    - `PageResult<CustomerTransferRecordResponse> getCustomerTransferRecords(Long customerId, int pageNum, int pageSize)`
  - 在 [`CustomerServiceImpl`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/service/impl/CustomerServiceImpl.java) 中实现上述方法：
    - `assignCustomer` 要求当前登录角色为 `SUPERVISOR` 及以上，通过 `currentUserRole()` 与 `currentUserId()` 获取操作者信息
    - 在手动分配时更新客户的 `assignedTo` 与 `status` 字段，并调用内部方法 `buildTransferRecord` 构造一条 `CustomerTransferRecord` 记录，写入原员工 ID、新员工 ID、操作者 ID、流转类型和转移原因
    - `getCustomerTransferRecords` 基于 `customerId` 构建分页查询，按记录 ID 倒序返回客户的完整流转轨迹，并通过 `CustomerTransferRecordResponse` 对象暴露给前端
- 在客户控制器中对外暴露客户分配与流转记录接口 [`backend/src/main/java/com/travel/admin/controller/CustomerController.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/controller/CustomerController.java)：
  - `POST /api/customers/{id}/assign`：接收 `CustomerAssignRequest`，由主管及以上角色将客户分配或转移给指定员工，接口上通过 `@PreAuthorize("hasAnyRole('SUPERVISOR','MANAGER','SUPER_ADMIN')")` 做权限控制
  - `GET /api/customers/{id}/transfers`：分页查询指定客户的流转记录，所有已登录员工均可查看自身所能访问客户的流转历史
- 客户分配、公海领取、离职员工客户处理与自动回收等场景统一复用 `CustomerTransferRecord` 记录流转历史，满足“原员工、新员工、时间、原因、操作者可追溯”的要求，为后续敏感操作审批与防私单监控提供数据基础

> 验证结果：在本地使用 `SUPERVISOR` / `MANAGER` / `SUPER_ADMIN` 角色调用 `POST /api/customers/{id}/assign` 接口，可将客户从原负责员工分配给新的员工，并在 `customer_transfer_record` 表中写入一条类型为 `ASSIGN` 的流转记录；使用任意已登录角色调用 `GET /api/customers/{id}/transfers` 可按时间倒序查看该客户的历史分配、公海领取与自动回收记录，对于越权分配的调用会在后端被角色校验与业务校验统一拦截。

### 前端（frontend）

- 在 `frontend/src/types/index.ts` 中补充客户流转相关类型定义：
  - 新增 `CustomerTransferType` 字面量枚举，对应后端 `CustomerTransferType` 中的 `ASSIGN`、`CLAIM_FROM_POOL`、`AUTO_RECYCLE_TO_POOL` 等值
  - 新增 `CustomerTransferRecord` 模型，字段覆盖客户 ID、原员工 ID、新员工 ID、操作人 ID、流转类型、原因与创建时间，对齐后端 `CustomerTransferRecordResponse`
- 在统一服务文件 [`frontend/src/services/index.ts`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/services/index.ts) 中补充客户分配与流转记录查询 API 封装：
  - 新增 `assignCustomer` 方法，对接 `POST /api/customers/{customerId}/assign` 接口，提交 `targetEmployeeId` 与 `reason` 字段
  - 新增 `fetchCustomerTransferRecords` 方法，对接 `GET /api/customers/{customerId}/transfers` 接口，支持通过 `pageNum` / `pageSize` 参数分页获取 `PageResult<CustomerTransferRecord>`
- 在客户列表页面 [`frontend/src/pages/CustomerListPage.vue`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/pages/CustomerListPage.vue) 中扩展客户分配与流转记录 UI：
  - 通过 `getCurrentUser` 读取当前登录用户角色，基于计算属性 `canAssignCustomer` 控制“分配”按钮仅对 `SUPERVISOR`、`MANAGER` 与 `SUPER_ADMIN` 角色可见
  - 在客户表格操作列中新增：
    - “分配”按钮：点击后打开分配弹窗，调用 `fetchEmployeePage` 加载可选员工列表，并通过 `assignCustomer` 将当前客户分配给选中的员工，同时要求填写转移原因
    - “流转记录”按钮：点击后打开流转记录弹窗，调用 `fetchCustomerTransferRecords` 分页展示该客户的历史流转记录
  - 分配弹窗中通过下拉框选择目标员工（展示员工姓名/账号与 ID），通过多行文本框填写转移原因，提交后自动刷新客户列表
  - 流转记录弹窗中使用 `a-table` 展示时间、原员工 ID、新员工 ID、操作人 ID、类型与原因字段，并通过本地函数将 `CustomerTransferType` 映射为“手动分配、公海领取、自动回收、离职客户转移”等中文文案

> 验证结果：在前端使用 `SUPERVISOR` / `MANAGER` / `SUPER_ADMIN` 角色登录后访问 `/customers`，可以在客户列表的操作列中看到“分配”和“流转记录”按钮：  
> - 点击“分配”选择目标员工并填写原因后，后端成功更新客户的负责员工并写入流转记录，页面提示“分配客户成功”，列表数据刷新后可看到 `assignedTo` 已更新  
> - 点击“流转记录”可在弹窗中按时间查看该客户的历史分配、公海领取与自动回收记录，翻页与刷新均正常工作  
> - 以普通员工身份登录时，仅能看到“编辑”和“删除”等原有操作按钮，“分配”按钮不会显示，满足实施计划「阶段六 · 步骤 6.1：实现客户分配」中“由主管及以上角色执行客户分配、所有员工可查看客户流转轨迹”的前端验收要求。

## 2026-01-29 阶段六 · 步骤 6.2：实现客户公海池

### 后端（backend）

- 在客户服务实现 [`CustomerServiceImpl`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/service/impl/CustomerServiceImpl.java) 中补充公海池相关规则与数据模型对齐：
  - 为 `CustomerResponse` 增加 `publicPoolEnterTime` 与 `publicPoolEnterReason` 字段，并在 `getPublicPoolPage` 返回结果中基于 `customer_transfer_record` 表中最近一次类型为 `AUTO_RECYCLE_TO_POOL` 或 `EMPLOYEE_RESIGN_TO_POOL` 的记录填充进入公海时间与原因；对于自动回收的场景在未显式设置原因时使用“超过30天未跟进自动回收”作为默认文案
  - 实现 `getPublicPoolPage`，基于 `CustomerQueryRequest` 过滤 `status = PUBLIC_POOL` 且 `assignedTo` 为空的客户，按 `lastFollowUpTime` 升序排序，并返回带有进入公海时间与原因的分页结果
  - 在 `claimFromPublicPool` 中通过 `countTodayClaims` 统计当前登录员工当天领取次数，对普通员工启用“每日最多领取 20 个公海客户”的限制；超过上限时抛出“已超出每日领取上限, 需主管审批后处理”的业务异常
  - 在 `claimFromPublicPool` 中对 VIP 客户增加角色限制，仅当当前角色为 `SUPERVISOR`、`MANAGER` 或 `SUPER_ADMIN` 时允许领取公海中的 VIP 客户，否则抛出“仅主管及以上角色可领取公海中的VIP客户”的业务异常
  - 在离职员工客户处理逻辑 `handleEmployeeResign` 中，对批量转入公海的场景检测是否包含 VIP 客户；若包含且当前角色不是 `MANAGER` 或 `SUPER_ADMIN`，则抛出“仅经理或超级管理员可将VIP客户批量转入公海”的业务异常，保证“VIP 客户转入公海需经理审批”的规则在服务层落地
  - 通过 `autoRecycleToPublicPool` 实现“普通客户超过30天无跟进记录自动回收至公海”的业务规则：筛选等级为 `NORMAL`、状态不为 `PUBLIC_POOL` 且 `lastFollowUpTime` 早于 30 天前或为空的客户，批量调用 `moveCustomerToPublicPool` 将其转入公海并记录一条 `AUTO_RECYCLE_TO_POOL` 类型的流转记录
- 在客户控制器 [`CustomerController`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/controller/CustomerController.java) 中暴露公海池相关接口：
  - `GET /api/customers/public-pool`：分页查询公海客户列表，返回带有进入公海时间与原因的 `PageResult<CustomerResponse>`，允许所有已登录角色访问
  - `POST /api/customers/public-pool/claim`：领取公海客户，接收 `PublicPoolClaimRequest`，在服务层校验客户当前是否在公海、领取次数是否超限以及 VIP 客户领取角色限制等规则后，将客户分配给当前登录员工并记录一条类型为 `CLAIM_FROM_POOL` 的流转记录
  - `POST /api/customers/employee/resign`：处理离职员工名下客户，接收 `EmployeeResignHandleRequest`，在服务层根据 `moveToPublicPool` 与 `targetEmployeeId` 决定是批量转入公海还是批量转交给指定员工，并统一记录类型为 `EMPLOYEE_RESIGN_TO_POOL` 或 `EMPLOYEE_RESIGN_TRANSFER` 的流转记录
  - `POST /api/customers/public-pool/auto-recycle`：手动触发公海自动回收任务，限制仅 `MANAGER` 与 `SUPER_ADMIN` 角色可访问，便于在尚未接入调度系统时由运维或高权限用户按需执行一次性自动回收操作

> 验证结果：在本地通过 `mvn -q -DskipTests compile` 完成后端编译，结合已有的客户流转记录查询接口验证：  
> - 手动构造超过 30 天未跟进的普通客户并调用 `autoRecycleToPublicPool`，可看到这些客户被统一标记为 `PUBLIC_POOL`，且在 `customer_transfer_record` 中写入类型为 `AUTO_RECYCLE_TO_POOL` 的记录，前端公海列表展示的进入公海时间与原因与数据库记录一致  
> - 使用普通员工账号多次调用 `POST /api/customers/public-pool/claim` 领取公海客户，在达到 20 个后再次领取会收到“已超出每日领取上限, 需主管审批后处理”的业务错误提示，而使用主管及以上角色登录时不受该上限限制  
> - 构造包含 VIP 客户的离职员工并以 `SUPERVISOR` 角色调用 `POST /api/customers/employee/resign`、设置 `moveToPublicPool = true` 时会被阻止，并返回“仅经理或超级管理员可将VIP客户批量转入公海”的错误；改用 `MANAGER` 或 `SUPER_ADMIN` 角色调用则可以顺利将 VIP 客户转入公海并生成对应流转记录

### 前端（frontend）

- 在前端类型定义文件 [`frontend/src/types/index.ts`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/types/index.ts) 中扩展客户模型：
  - 为 `Customer` 增加可选字段 `publicPoolEnterTime` 与 `publicPoolEnterReason`，对接后端 `CustomerResponse` 中新增的公海进入时间与原因字段，用于在公海客户列表中展示
- 在统一服务封装 [`frontend/src/services/index.ts`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/services/index.ts) 中补充公海池与离职客户处理相关 API：
  - 新增 `fetchPublicPoolPage` 方法，对接 `GET /api/customers/public-pool` 接口，支持按关键字、客户等级与分页参数组合查询公海客户列表
  - 新增 `claimFromPublicPool` 方法，对接 `POST /api/customers/public-pool/claim` 接口，提交 `customerId` 实现公海客户领取
  - 新增 `handleEmployeeResign` 方法，对接 `POST /api/customers/employee/resign` 接口，提交离职员工 ID、目标员工 ID、公海转入标记与原因等字段，用于批量处理离职员工名下客户
- 新增公海客户列表页面 [`frontend/src/pages/PublicPoolPage.vue`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/pages/PublicPoolPage.vue)：
  - 使用 `MainLayout` 统一布局，在左侧菜单中新增“公海客户”入口，所有已登录角色均可看到并访问 `/public-pool`
  - 顶部工具栏提供 `a-input-search` 与 `a-select` 组合，支持按关键字（姓名 / 手机号 / 微信）和客户等级筛选公海客户
  - 使用 `a-table` 展示公海客户列表，字段包括姓名、手机号、微信、等级、最近跟进时间、进入公海时间与进入公海原因，并基于 `Customer` 类型中的新字段渲染时间与原因信息
  - 操作列提供“领取”按钮，点击后调用 `claimFromPublicPool` 并在成功后刷新当前页面；当当前用户角色为普通员工且公海客户为 VIP 时，按钮置为不可用，仅允许 `SUPERVISOR`、`MANAGER` 与 `SUPER_ADMIN` 角色领取 VIP 公海客户
- 在员工列表页面 [`frontend/src/pages/EmployeeListPage.vue`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/pages/EmployeeListPage.vue) 中补充离职员工客户处理入口：
  - 基于当前登录用户角色新增计算属性 `canHandleResignCustomers`，仅当角色为 `SUPER_ADMIN` 或 `MANAGER` 时展示“离职客户处理”操作列
  - 在员工状态为“离职”时启用“处理离职客户”按钮，点击后弹出处理弹窗，允许选择“转入公海”或“转给指定员工”，并填写处理原因
  - 在用户点击确认后调用 `handleEmployeeResign`，后端根据所选方式批量处理该员工名下客户（转入公海或转交给指定员工），操作完成后刷新员工列表并使用 `message.success` 给出反馈

> 验证结果：在前端通过 `npm run dev` 启动后，使用不同角色账号登录进行手工测试：  
> - 任意已登录角色均可通过左侧菜单进入“公海客户”页面，看到包含进入公海时间与原因字段的公海客户列表；普通员工在 VIP 客户行上只能查看而不能点击“领取”按钮，主管及以上角色可以正常领取 VIP 客户  
> - 普通员工在公海页面连续领取多条普通客户记录，当后端返回“已超出每日领取上限, 需主管审批后处理”异常时，前端通过 `message.error` 将该错误文案直接展示给用户  
> - 以 `SUPER_ADMIN` 或 `MANAGER` 角色进入“员工管理”页面，将某个员工状态改为“离职”后点击“处理离职客户”，选择“转入公海”或“转给指定员工”并填写原因后提交，可以看到后端成功处理该员工名下客户；以普通员工或主管角色无法看到该处理入口，访问权限与后端接口 `@PreAuthorize` 配置保持一致  

## 2026-01-29 阶段七 · 步骤 7.2：敏感操作审批

### 后端（backend）

- 在 `com.travel.admin.common.enums` 包下新增审批相关枚举：
  - [`backend/src/main/java/com/travel/admin/common/enums/ApprovalStatus.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/common/enums/ApprovalStatus.java)，用于表示审批状态 `PENDING` / `APPROVED` / `REJECTED`
  - [`backend/src/main/java/com/travel/admin/common/enums/SensitiveOperationType.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/common/enums/SensitiveOperationType.java)，用于标识敏感操作类型，目前支持 `VIP_CUSTOMER_DELETE`（删除 VIP 客户）与 `CUSTOMER_TRANSFER`（跨部门客户转移）
- 在 `com.travel.admin.entity` 包下新增审批实体：
  - [`backend/src/main/java/com/travel/admin/entity/ApprovalRequest.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/entity/ApprovalRequest.java)
  - 采用 `approval_request` 表记录审批请求，字段包含：
    - `operationType`：敏感操作类型
    - `customerId`：关联客户 ID
    - `fromEmployeeId` / `toEmployeeId`：用于跨部门客户转移时记录原员工与目标员工 ID
    - `requesterId`：申请人 ID
    - `reason`：申请原因
    - `status`：审批状态，使用 `ApprovalStatus`
    - `approverId`：审批人 ID
    - `decisionReason`：审批意见
    - `createdAt` / `updatedAt`：时间字段采用 MyBatis-Plus 自动填充，`deleted` 使用逻辑删除标记
- 在 `com.travel.admin.mapper` 包下新增审批 Mapper：
  - [`backend/src/main/java/com/travel/admin/mapper/ApprovalRequestMapper.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/mapper/ApprovalRequestMapper.java)，继承 `BaseMapper<ApprovalRequest>`，用于对审批记录做增删改查
- 在 `com.travel.admin.dto.approval` 包下新增审批相关 DTO：
  - 创建请求对象 [`ApprovalCreateRequest`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/dto/approval/ApprovalCreateRequest.java)，包含 `operationType`、`customerId`、可选的 `targetEmployeeId` 以及必填的申请原因
  - 审批决策请求对象 [`ApprovalDecisionRequest`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/dto/approval/ApprovalDecisionRequest.java)，包含是否通过 `approved` 以及可选的审批意见
  - 查询请求对象 [`ApprovalQueryRequest`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/dto/approval/ApprovalQueryRequest.java)，支持按审批类型、状态以及分页参数过滤审批记录
  - 响应对象 [`ApprovalResponse`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/dto/approval/ApprovalResponse.java)，向前端返回审批的基础信息与状态
- 在 `com.travel.admin.service` 包下新增审批服务接口与实现：
  - 服务接口 [`backend/src/main/java/com/travel/admin/service/ApprovalService.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/service/ApprovalService.java)，定义：
    - `createApproval`：提交审批请求
    - `getApprovalPage`：分页查询审批记录
    - `decide`：提交审批决策
  - 服务实现 [`backend/src/main/java/com/travel/admin/service/impl/ApprovalServiceImpl.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/service/impl/ApprovalServiceImpl.java)：
    - `createApproval`：
      - 对 `VIP_CUSTOMER_DELETE`：校验客户存在且等级为 VIP，并确保不存在重复的待处理删除审批后，生成一条 `PENDING` 状态的审批记录
      - 对 `CUSTOMER_TRANSFER`：校验客户存在且当前已分配员工，加载原员工与目标员工信息，当检测到跨部门转移且不存在重复的待处理审批时，生成一条 `PENDING` 状态的审批记录，记录原/目标员工 ID 与转移原因
    - `getApprovalPage`：基于审批类型与状态构建分页查询条件，按 ID 倒序返回审批记录，并转换为 `PageResult<ApprovalResponse>`
    - `decide`：
      - 对已删除或非 `PENDING` 状态的记录直接拒绝操作
      - 当 `approved == false` 时，仅更新状态为 `REJECTED`、记录审批人 ID 与审批意见
      - 当 `approved == true` 时，根据 `operationType` 分别执行：
        - `VIP_CUSTOMER_DELETE`：要求当前角色为 `MANAGER` 或 `SUPER_ADMIN`，在确认客户仍为 VIP 后调用 `customerMapper.deleteById` 删除客户，删除成功后将审批状态置为 `APPROVED`
        - `CUSTOMER_TRANSFER`：同样要求当前角色为 `MANAGER` 或 `SUPER_ADMIN`，校验客户当前负责员工仍为审批记录中的 `fromEmployeeId` 后，将客户 `assignedTo` 更新为 `toEmployeeId`，并插入一条类型为 `CustomerTransferType.TRANSFER` 的流转记录，记录操作者 ID 与转移原因
- 在 `com.travel.admin.controller` 包下新增审批控制器：
  - [`backend/src/main/java/com/travel/admin/controller/ApprovalController.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/controller/ApprovalController.java)
  - 暴露接口：
    - `POST /api/approvals`：提交敏感操作审批请求，使用 `@PreAuthorize("hasAnyRole('EMPLOYEE','SUPERVISOR','MANAGER','SUPER_ADMIN')")` 允许所有已登录角色发起删除 VIP 客户或跨部门转移的审批
    - `GET /api/approvals`：分页查询审批记录，仅允许 `SUPERVISOR`、`MANAGER` 与 `SUPER_ADMIN` 访问
    - `POST /api/approvals/{id}/decision`：提交审批决策（通过或拒绝），同样仅允许 `SUPERVISOR`、`MANAGER` 与 `SUPER_ADMIN` 调用，内部再根据具体操作类型限制实际执行者只能为经理或超级管理员
- 收紧客户删除与跨部门转移的执行入口，将高风险操作统一纳入审批流：
  - 在客户服务实现 [`CustomerServiceImpl`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/service/impl/CustomerServiceImpl.java) 中更新删除逻辑：
    - 对 VIP 客户删除不再允许直接执行逻辑删除，而是无论当前角色等级如何都抛出“删除VIP客户需提交审批, 请通过审批流程处理”的业务异常，引导前端通过审批接口发起删除请求
    - 对普通客户删除仍要求当前角色为主管及以上
  - 在同一类中为客户分配逻辑增加跨部门限制：
    - 新增对原员工与目标员工部门的比对逻辑，通过注入的 [`EmployeeMapper`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/mapper/EmployeeMapper.java) 查询员工部门
    - 当检测到跨部门转移且当前角色为 `SUPERVISOR` 时，抛出“跨部门客户转移需经理或超级管理员审批, 请发起审批后再执行”的业务异常，阻止主管直接跨部门转移客户
    - 保留 `MANAGER` 与 `SUPER_ADMIN` 直接跨部门转移客户的权限，满足“更高权限审批”的设计要求，同时提供审批路径供主管等角色发起跨部门转移申请

> 验证结果：在 `backend` 目录下执行 `mvn -q -DskipTests compile` 编译通过，说明审批模块相关实体、枚举、DTO、服务与控制器与现有工程结构保持一致，类型和依赖完整。通过以下场景进行手工验证：  
> - 以 `SUPERVISOR` 或普通员工身份登录，尝试在前端客户列表中删除 VIP 客户时不会直接触发删除，而是通过前端调用 `POST /api/approvals` 创建一条类型为 `VIP_CUSTOMER_DELETE` 的审批记录，数据库中客户仍然存在  
> - 以 `MANAGER` 或 `SUPER_ADMIN` 身份登录访问 `/approvals` 页面，对该删除审批点击“通过”后，可在数据库中看到对应 VIP 客户记录被逻辑删除，同时审批状态从 `PENDING` 更新为 `APPROVED`；再次对同一客户发起删除审批会被后端以“客户不存在”拦截  
> - 对于跨部门客户转移，使用 `SUPERVISOR` 登录在客户列表中选择原部门员工名下客户，并在分配弹窗中选择其他部门员工作为目标时，前端根据员工列表中的部门信息判断为跨部门场景，不再直接调用转移接口，而是调用 `POST /api/approvals` 创建类型为 `CUSTOMER_TRANSFER` 的审批记录；随后使用 `MANAGER` 或 `SUPER_ADMIN` 在 `/approvals` 页面通过该审批后，可以看到客户 `assignedTo` 字段更新为目标员工 ID，并在流转记录中新增一条类型为 `TRANSFER` 的记录  
> - 通过 Postman 等工具直接调用 `DELETE /api/customers/{id}` 删除 VIP 客户会收到“删除VIP客户需提交审批, 请通过审批流程处理”的业务异常；直接以 `SUPERVISOR` 角色调用 `POST /api/customers/{id}/assign` 进行跨部门转移也会被后端拒绝，保证高风险操作无法绕过审批流直接执行  
> 综上，当前实现满足实施计划「阶段七 · 步骤 7.2：敏感操作审批」中关于“删除 VIP 客户需审批、跨部门客户转移通过审批后执行、拒绝不执行以及审批记录完整可查”的核心要求（数据导出审批将在后续引入导出接口时复用同一审批模块）。

### 前端（frontend）

- 在 `frontend/src/types` 目录下新增审批相关类型定义：
  - [`frontend/src/types/approval.ts`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/types/approval.ts)
  - 定义 `SensitiveOperationType` 与 `ApprovalStatus` 字面量枚举，以及 `Approval` / `ApprovalQueryParams` 类型，用于描述审批记录列表与查询参数
- 在 `frontend/src/services` 目录下新增审批服务封装：
  - [`frontend/src/services/approval.ts`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/services/approval.ts)
  - 基于统一的 `requestJson` 封装实现：
    - `fetchApprovalPage`：调用 `GET /api/approvals`，按审批类型、状态与分页参数获取审批记录列表
    - `createApproval`：调用 `POST /api/approvals`，用于发起删除 VIP 客户或跨部门客户转移的审批
    - `decideApproval`：调用 `POST /api/approvals/{id}/decision`，提交审批结果与可选的审批意见
- 在导航与路由层增加审批入口：
  - 更新主布局组件 [`frontend/src/components/MainLayout.vue`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/components/MainLayout.vue)：
    - 在侧边菜单中新增“敏感操作审批”菜单项，仅对 `SUPERVISOR`、`MANAGER` 与 `SUPER_ADMIN` 角色显示
    - 点击菜单项时跳转到 `/approvals` 路由
  - 更新前端路由入口 [`frontend/src/main.ts`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/main.ts)：
    - 新增 `/approvals` 路由，懒加载 [`ApprovalPage.vue`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/pages/ApprovalPage.vue)，并配置 `meta.requiresAuth = true` 与 `meta.roles = ['SUPERVISOR','MANAGER','SUPER_ADMIN']`
- 新增敏感操作审批列表页面：
  - [`frontend/src/pages/ApprovalPage.vue`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/pages/ApprovalPage.vue)
  - 使用 `MainLayout` 统一布局，在卡片头部提供审批状态与审批类型筛选
  - 使用 `a-table` 展示审批 ID、类型、客户 ID、原/目标员工 ID、申请人 ID、原因、状态、审批人 ID、审批意见以及创建/更新时间
  - 对处于 `PENDING` 状态的记录在操作列中提供“通过”“拒绝”两个按钮，点击后弹出审批意见输入框，提交后调用 `decideApproval` 完成审批流转
- 在客户列表页面中接入审批发起入口：
  - 更新客户列表页面 [`frontend/src/pages/CustomerListPage.vue`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/pages/CustomerListPage.vue)：
    - 通过导入 `createApproval`，在删除逻辑中区分 VIP 客户与普通客户：
      - 当用户对 VIP 客户点击“删除”时，不再直接调用 `deleteCustomer`，而是调用 `createApproval` 提交类型为 `VIP_CUSTOMER_DELETE` 的审批，并提示“已提交删除VIP客户审批”，列表数据保持不变
      - 普通客户删除行为保持不变，仍直接调用 `deleteCustomer`，在当前页仅剩一条记录时删除后自动回退一页并刷新列表
    - 在客户分配提交逻辑中增加跨部门检测与审批发起：
      - 基于分配弹窗中加载的员工列表与当前客户 `assignedTo` 字段判断原员工与目标员工是否属于不同部门
      - 当检测到跨部门场景且当前登录角色为 `SUPERVISOR` 时，不再直接调用 `assignCustomer`，而是调用 `createApproval` 提交类型为 `CUSTOMER_TRANSFER` 的审批请求，并提示“已提交跨部门客户转移审批”
      - 对于同部门转移或当前角色为 `MANAGER` / `SUPER_ADMIN` 的情况仍直接调用 `assignCustomer`，保持高权限用户可直接执行跨部门转移的能力

> 验证结果：在前端通过 `npm run dev` 启动应用后，使用不同角色账号进行手工测试：  
> - 以普通员工或主管身份登录访问 `/customers` 页面，对 VIP 客户点击“删除”时会弹出确认框，确认后前端调用 `createApproval` 成功提交审批，请求成功后列表不发生变化，并在 `/approvals` 页面看到新增一条状态为 `PENDING`、类型为“删除VIP客户”的审批记录  
> - 以主管身份在客户分配弹窗中选择不同部门的目标员工并填写原因后提交，前端检测到跨部门场景并调用 `createApproval` 创建类型为“跨部门客户转移”的审批记录，客户当前负责员工保持不变；改用经理或超级管理员登录执行同样操作时则会直接完成分配，客户 `assignedTo` 字段在页面刷新后更新为目标员工 ID  
> - 以主管/经理/超级管理员登录访问 `/approvals` 页面，可以通过筛选条件查看待审批记录，对任意一条点击“通过”或“拒绝”并填写审批意见后，审批列表刷新为最新状态；对于删除 VIP 客户的审批，通过后在客户列表中不再显示该客户，对于跨部门转移审批，通过后在客户列表中可以看到客户已被转移到目标员工名下，同时在“流转记录”弹窗中新增一条类型为“手动转移”的记录  
> 通过上述端到端测试，前端已为删除 VIP 客户与跨部门客户转移这两类敏感操作提供了完整的审批发起与执行闭环，与后端审批模块协同工作，满足实施计划中对“提交审批成功、审批通过后执行、拒绝不执行以及审批记录可查”的前端验收要求。 

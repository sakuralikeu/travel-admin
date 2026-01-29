# 项目架构与目录说明

> 本文档用于从「工程结构」视角解释当前仓库中各目录与关键文件的作用，帮助后续开发者快速建立对整体架构的认知。  
> 实施计划、需求与技术规范请参考同目录下其它文档。

---

## 顶层目录结构

- `backend/`：后端服务工程（Spring Boot 3 + Java 17 + MyBatis-Plus）
- `frontend/`：前端管理界面工程（Vue 3 + TypeScript + Vite + Ant Design Vue）
- `memory-bank/`：需求、设计、技术方案与实施计划等文档
- `README.md`：仓库级说明文档，给新成员的入口说明

---

## backend 目录结构与文件作用

后端采用标准 Spring Boot 分层结构，当前已建立基础工程骨架，后续会在此目录下继续扩展 controller / service / mapper / entity 等模块。

- [`backend/pom.xml`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/pom.xml)
  - Maven 工程配置文件
  - 声明 Spring Boot 版本、Java 版本以及所有后端依赖（Web、Validation、Security、MyBatis-Plus、MySQL、Lombok、OpenAPI、Test 等）
  - 后续新增依赖也统一在此维护

- [`backend/src/main/java/com/travel/admin/TravelAdminApplication.java`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/java/com/travel/admin/TravelAdminApplication.java)
  - 后端应用入口类
  - 标记 `@SpringBootApplication`，负责启动 Spring 容器
  - 将来所有接口、业务逻辑、数据访问类都位于 `com.travel.admin` 包层级下

- [`backend/src/main/resources/application.yml`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/backend/src/main/resources/application.yml)
  - Spring Boot 默认配置文件
  - 已配置应用名、端口、多环境 profile 入口以及 MyBatis-Plus 全局配置
  - 通过 `spring.profiles.active=dev` 与 `application-*.yml` 组合完成 dev/test/prod 环境区分

> 当前已在 `com.travel.admin` 包下创建基础公共模块：  
> - `backend/src/main/java/com/travel/admin/common/result/`：统一响应结果封装  
> - `backend/src/main/java/com/travel/admin/common/exception/`：业务异常与全局异常处理  
> - `backend/src/main/java/com/travel/admin/config/`：MyBatis-Plus 分页与自动填充配置、Spring Security 安全配置  
> 后续新增具体业务功能时，将按以下分层创建 controller / service / mapper / entity 等模块：  
> - `backend/src/main/java/com/travel/admin/controller/`：对外 REST 接口层（包含员工管理、客户管理、操作日志查询与认证接口）  
> - `backend/src/main/java/com/travel/admin/service/`：业务服务层（含 `impl` 实现）  
> - `backend/src/main/java/com/travel/admin/mapper/`：MyBatis-Plus Mapper 接口  
> - `backend/src/main/java/com/travel/admin/entity/`：数据库实体  
> - `backend/src/main/java/com/travel/admin/dto/`：请求/响应 DTO（含员工、客户、日志与登录请求/响应模型）  
> - `backend/src/main/java/com/travel/admin/security/`：JWT 工具类、自定义用户详情服务与认证过滤器  
> - `backend/src/main/resources/mapper/`：MyBatis XML 映射文件  

---

## frontend 目录结构与文件作用

前端采用 Vue 3 + TypeScript + Vite 架构，UI 使用 Ant Design Vue。在完成工程骨架之后，已按实施计划逐步扩展页面、类型定义与服务层，以支撑员工管理等核心功能。

- [`frontend/package.json`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/package.json)
  - 前端工程的依赖与脚本声明
  - `dependencies`：包含 `vue`、`vue-router`、`ant-design-vue`
  - `devDependencies`：包含 Vite、Vue 插件、TypeScript、类型工具等
  - `scripts`：
    - `dev`：启动开发服务器
    - `build`：打包构建
    - `preview`：预览构建产物
    - `typecheck`：仅做 TypeScript 类型检查

- [`frontend/tsconfig.json`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/tsconfig.json)
  - 前端源码的 TypeScript 编译配置
  - 设定目标为 `ESNext`，严格模式开启，包含 `.ts` / `.tsx` / `.vue` 文件

- [`frontend/tsconfig.node.json`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/tsconfig.node.json)
  - Vite 等 Node 侧脚本的 TypeScript 配置
  - 主要用于为 `vite.config.ts` 提供类型支持

- [`frontend/vite.config.ts`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/vite.config.ts)
  - Vite 构建工具配置
  - 注册 Vue 插件
  - 配置别名 `@` 指向 `src` 目录，便于在代码中使用简洁路径
  - 配置开发服务器端口为 `5173`

- [`frontend/index.html`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/index.html)
  - 前端应用 HTML 入口
  - 定义根节点 `#app`
  - 引入入口脚本 `/src/main.ts`

- [`frontend/src/main.ts`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/main.ts)
  - 前端应用入口脚本
  - 创建 Vue 应用实例
  - 创建并注册 `vue-router` 路由，当前包含登录页 `/login`、根路径 `/` 的首页和 `/employees` 的员工管理页面
  - 在路由守卫中基于本地存储的 JWT 做基础登录态校验，未登录访问受保护页面时自动跳转到登录页
  - 全局注册 Ant Design Vue 组件库
  - 将应用挂载到 `#app` 元素

- [`frontend/src/App.vue`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/App.vue)
  - 顶层视图组件
  - 当前仅渲染 `router-view`，作为布局与页面切换的根容器

- [`frontend/src/pages/HomePage.vue`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/pages/HomePage.vue)
  - 简单的首页页面
  - 使用 Ant Design Vue 的 `Layout` 和 `Typography` 组件
  - 用于验证前端工程初始化成功，并提示后续开发将按实施计划推进

- [`frontend/src/pages/EmployeeListPage.vue`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/pages/EmployeeListPage.vue)
  - 员工管理列表页面，对应实施计划「阶段三 · 步骤 3.1」
  - 使用 `a-table` 按账号、姓名、手机号、邮箱、部门、职位、角色、状态等字段展示员工数据
  - 使用 `a-input-search` 和分页组件实现按关键字搜索与分页浏览
  - 使用 `a-modal + a-form` 实现员工的新增与编辑，提交时调用后端 `POST /api/employees` 与 `PUT /api/employees/{id}` 接口
  - 操作列提供编辑与删除按钮，删除时调用 `DELETE /api/employees/{id}` 完成逻辑删除

- [`frontend/src/pages/LoginPage.vue`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/pages/LoginPage.vue)
  - 登录页面，对应实施计划「阶段四 · 步骤 4.1」
  - 使用 `a-form`、`a-input`、`a-input-password` 与 `a-button` 实现登录表单交互
  - 调用后端 `POST /api/auth/login` 接口完成用户名密码登录，并在成功后保存 JWT 到本地存储，按路由参数 `redirect` 或默认跳转到 `/employees`

- [`frontend/src/types/index.ts`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/types/index.ts)
  - 定义前端通用类型模型：
    - `ApiResult<T>`：与后端 `Result<T>` 对齐
    - `PageResult<T>`：与后端 `PageResult<T>` 对齐
  - 定义员工与认证相关业务类型：
    - `EmployeeRole` / `EmployeeStatus`：与后端枚举一一对应
    - `Employee`：对接后端 `EmployeeResponse`，用于列表展示与表单编辑
    - `EmployeeQueryParams` / `EmployeeFormValues`：对接后端查询与创建/更新请求 DTO
    - `LoginRequest` / `LoginResponse`：对接后端登录请求与响应模型

- [`frontend/src/services/index.ts`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/frontend/src/services/index.ts)
  - 封装与后端交互的 HTTP 请求：
    - `fetchEmployeePage`：调用 `GET /api/employees` 获取分页员工列表
    - `createEmployee`：调用 `POST /api/employees` 创建员工
    - `updateEmployee`：调用 `PUT /api/employees/{id}` 更新员工信息
    - `deleteEmployee`：调用 `DELETE /api/employees/{id}` 删除员工
    - `login`：调用 `POST /api/auth/login` 完成用户名密码登录，并在成功后保存 JWT
  - 内部统一使用 `fetch` 与 `ApiResult<T>` 类型解析后端响应，在 `code != 200` 时抛出错误，交由页面层使用 Ant Design Vue 的 `message` 组件做反馈
  - 在公共请求方法中自动从本地存储读取 JWT，并在有令牌时为请求附加 `Authorization: Bearer <token>` 头

> 当前已在 `src` 下创建基础前端目录结构并完成首个业务页面与登录入口实现：  
> - `frontend/src/pages/`：页面组件目录，当前包含 `HomePage.vue`、`EmployeeListPage.vue` 与 `LoginPage.vue`  
> - `frontend/src/components/`：通用组件与业务组件目录  
> - `frontend/src/services/`：封装与后端交互的 HTTP 请求，已实现员工模块与登录接口调用，并在请求层统一挂载 JWT  
> - `frontend/src/types/`：前端 TypeScript 类型定义，已对齐后端员工管理与登录认证的 DTO 与枚举  
> 未来将按员工、客户、订单等业务模块进一步在 `pages` 下拆分子目录，并在 `stores` 中补充状态管理实现。  

---

## memory-bank 文档区

`memory-bank` 目录集中存放与业务、技术方案相关的设计文档，是开发的“脑”，代码只是这些文档的实现。

- [`memory-bank/employee_management_design.md`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/memory-bank/employee_management_design.md)
  - 员工与客户管理系统的业务与功能设计文档
  - 包含模块划分、权限角色、数据安全、防私单等核心需求

- [`memory-bank/tech_stack_recommendation.md`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/memory-bank/tech_stack_recommendation.md)
  - 推荐技术栈与 AI 代码生成规范
  - 对前后端技术选型、项目结构、分层规范、异常处理、统一响应等做出约束
  - 当前后端与前端工程骨架均严格遵循该文档

- [`memory-bank/implement-plan.md`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/memory-bank/implement-plan.md)
  - 实施级开发计划
  - 将业务需求拆解为多个阶段和具体步骤，每一步都包含“任务 / 具体要求 / 验证测试”
  - 当前我们已完成其中的「阶段一 · 步骤 1.1：创建前后端项目骨架」

- [`memory-bank/progress.md`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/memory-bank/progress.md)
  - 按时间记录每次执行实施计划时所做的实际工作
  - 为后续开发者提供“已经完成哪些步骤、做了哪些具体改动”的时间线视图

- [`memory-bank/architecture.md`](file:///e:/Users/Fengye/Documents/软开/origin-code/travel_admin/memory-bank/architecture.md)（当前文档）
  - 专注于解释项目的工程层级结构和各文件职责
  - 与 `employee_management_design.md`（业务视角）和 `implement-plan.md`（实施视角）互补

---

## 小结

当前仓库处于「工程骨架已创建」阶段：

- 后端具备可启动的 Spring Boot 应用与基础依赖配置
- 前端具备可启动的 Vue 3 + Vite 应用与 UI 库接入
- 架构与进度说明文档已经与代码状态同步，后续每完成一个实施步骤，应同步更新 `progress.md` 与本文件，保证“文档即架构”的一致性。

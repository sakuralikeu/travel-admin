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

> 当前为止，仅完成实施计划中的「步骤 1.1：创建前后端项目骨架」，尚未开始多环境、数据库、日志等配置（步骤 1.2 及后续阶段）。

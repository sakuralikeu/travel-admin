# 员工客户管理系统（travel_admin）

本仓库用于实现员工客户管理、防私单与合规审计相关功能，所有实施步骤以 `memory-bank/implement-plan.md` 为准。

## 项目结构

- `backend`：Spring Boot 3 + Java 17 + MyBatis-Plus 后端服务
- `frontend`：Vue 3 + TypeScript + Vite 前端管理界面
- `memory-bank`：需求设计、技术方案与实施计划等文档

## 快速开始

### 后端

1. 进入 `backend` 目录
2. 执行 `mvn spring-boot:run` 启动后端服务

### 前端

1. 进入 `frontend` 目录
2. 执行 `npm install`
3. 执行 `npm run dev` 启动前端开发服务器

## 开发约定

- 严格遵循 `memory-bank/tech_stack_recommendation.md` 中的技术栈与分层规范
- 按照 `memory-bank/implement-plan.md` 中的阶段与步骤推进开发


<template>
  <div class="login-page">
    <div class="background">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="gradient-orb orb-3"></div>
      <div class="particles">
        <span v-for="i in 20" :key="i" class="particle" :style="getParticleStyle(i)"></span>
      </div>
    </div>
    
    <a-layout class="login-layout">
      <a-layout-header class="login-header">
        <div class="header-content">
          <div class="logo">
            <svg class="logo-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
              <circle cx="12" cy="7" r="4"></circle>
            </svg>
            <span>员工客户管理系统</span>
          </div>
        </div>
      </a-layout-header>
      
      <a-layout-content class="login-content">
        <div class="login-container">
          <div class="login-left">
            <div class="slogan-section">
              <h1 class="title">统一员工与客户资源</h1>
              <p class="subtitle">防私单 · 合规 · 可追溯</p>
              <div class="features">
                <div class="feature-item" v-for="(feature, index) in features" :key="index" :style="{ animationDelay: `${index * 0.1}s` }">
                  <div class="feature-icon">
                    <component :is="feature.icon" />
                  </div>
                  <span>{{ feature.text }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <div class="login-right">
            <a-card class="login-card" :bordered="false">
              <template #title>
                <div class="card-title">
                  <span>欢迎回来</span>
                </div>
              </template>
              
              <a-tabs v-model:activeKey="activeTab" class="login-tabs">
                <a-tab-pane key="password" tab="账号密码登录">
                  <a-form
                    :model="formState"
                    :label-col="{ span: 0 }"
                    :wrapper-col="{ span: 24 }"
                    @submit.prevent="handleSubmit"
                    class="login-form"
                  >
                    <a-form-item name="username">
                      <a-input
                        v-model:value="formState.username"
                        placeholder="请输入登录账号"
                        size="large"
                        class="login-input"
                      >
                        <template #prefix>
                          <UserOutlined class="input-icon" />
                        </template>
                      </a-input>
                    </a-form-item>
                    
                    <a-form-item name="password">
                      <a-input-password
                        v-model:value="formState.password"
                        placeholder="请输入密码"
                        size="large"
                        class="login-input"
                      >
                        <template #prefix>
                          <LockOutlined class="input-icon" />
                        </template>
                      </a-input-password>
                    </a-form-item>
                    
                    <a-form-item>
                      <div class="form-extra">
                        <a-checkbox v-model:checked="rememberMe">
                          记住我
                        </a-checkbox>
                        <a-typography-link @click="handleForgotPassword" class="forgot-link">
                          忘记密码？
                        </a-typography-link>
                      </div>
                    </a-form-item>
                    
                    <a-form-item>
                      <a-button
                        type="primary"
                        html-type="submit"
                        :loading="submitting"
                        size="large"
                        block
                        class="login-button"
                      >
                        <span v-if="!submitting">登 录</span>
                      </a-button>
                    </a-form-item>
                    
                    <a-form-item :wrapper-col="{ span: 24 }">
                      <div class="register-row">
                        <span>还没有账号？</span>
                        <a-typography-link @click="handleRegister" class="register-link">
                          立即创建账号
                        </a-typography-link>
                      </div>
                    </a-form-item>
                  </a-form>
                </a-tab-pane>
                
                <a-tab-pane key="code" tab="验证码登录">
                  <div class="placeholder-block">
                    <LockOutlined class="placeholder-icon" />
                    <p>验证码登录暂未开通，将在后续版本提供。</p>
                  </div>
                </a-tab-pane>
                
                <a-tab-pane key="third" tab="第三方登录">
                  <div class="placeholder-block">
                    <LinkOutlined class="placeholder-icon" />
                    <p>第三方登录（企业微信、飞书等）暂未接入。</p>
                  </div>
                </a-tab-pane>
              </a-tabs>
            </a-card>
          </div>
        </div>
      </a-layout-content>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, h } from "vue";
import { useRoute, useRouter } from "vue-router";
import { message } from "ant-design-vue";
import {
  UserOutlined,
  LockOutlined,
  LinkOutlined,
  TeamOutlined,
  SafetyOutlined,
  BarChartOutlined
} from "@ant-design/icons-vue";
import type { LoginRequest } from "../types";
import { clearAccessToken, login } from "../services";

const router = useRouter();
const route = useRoute();

const activeTab = ref("password");

const formState = reactive<LoginRequest>({
  username: "",
  password: ""
});

const rememberMe = ref(false);
const submitting = ref(false);

const features = [
  { icon: TeamOutlined, text: "员工管理" },
  { icon: SafetyOutlined, text: "合规审计" },
  { icon: BarChartOutlined, text: "数据分析" }
];

function getParticleStyle(index: number) {
  const left = Math.random() * 100;
  const delay = Math.random() * 5;
  const duration = 3 + Math.random() * 4;
  return {
    left: `${left}%`,
    animationDelay: `${delay}s`,
    animationDuration: `${duration}s`
  };
}

async function handleSubmit() {
  if (activeTab.value !== "password") {
    message.info("当前仅支持账号密码登录");
    return;
  }
  if (!formState.username || !formState.password) {
    message.warning("请输入账号和密码");
    return;
  }
  try {
    submitting.value = true;
    clearAccessToken();
    await login(formState);
    const redirect = (route.query.redirect as string) || "/employees";
    message.success("登录成功");
    router.replace(redirect);
  } catch (error) {
    message.error("账号或密码错误");
  } finally {
    submitting.value = false;
  }
}

function handleRegister() {
  message.info("账号创建功能将由管理员在后台完成");
}

function handleForgotPassword() {
  message.info("请联系系统管理员重置密码");
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
}

.background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
}

.gradient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.6;
  animation: float 8s ease-in-out infinite;
}

.orb-1 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  top: -100px;
  left: -100px;
}

.orb-2 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  bottom: -50px;
  right: -50px;
  animation-delay: -3s;
}

.orb-3 {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: -5s;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -30px) scale(1.05);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.95);
  }
}

.particles {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.particle {
  position: absolute;
  width: 4px;
  height: 4px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  animation: particle-float linear infinite;
}

@keyframes particle-float {
  0% {
    transform: translateY(100vh) scale(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100vh) scale(1);
    opacity: 0;
  }
}

.login-layout {
  position: relative;
  z-index: 1;
  background: transparent !important;
}

.login-header {
  background: rgba(255, 255, 255, 0.05) !important;
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  height: 64px;
  line-height: 64px;
  padding: 0 40px;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #fff;
  font-size: 20px;
  font-weight: 600;
}

.logo-icon {
  width: 32px;
  height: 32px;
  color: #667eea;
}

.login-content {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 64px);
  padding: 40px 20px;
}

.login-container {
  display: flex;
  max-width: 1000px;
  width: 100%;
  gap: 80px;
  align-items: center;
}

.login-left {
  flex: 1;
  color: #fff;
}

.slogan-section {
  animation: slideInLeft 0.8s ease-out;
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-40px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.title {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 16px;
  background: linear-gradient(135deg, #fff 0%, #a5b4fc 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  font-size: 24px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 40px;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 16px;
  color: rgba(255, 255, 255, 0.8);
  animation: slideInLeft 0.8s ease-out both;
}

.feature-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  font-size: 18px;
}

.login-right {
  flex: 0 0 420px;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  animation: slideInRight 0.8s ease-out;
  overflow: hidden;
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(40px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.login-card :deep(.ant-card-head) {
  border-bottom: 1px solid #f0f0f0;
  padding: 20px 24px;
}

.login-card :deep(.ant-card-head-title) {
  padding: 0;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a2e;
  text-align: center;
  padding: 8px 0;
}

.login-tabs :deep(.ant-tabs-nav) {
  margin-bottom: 24px;
}

.login-tabs :deep(.ant-tabs-tab) {
  font-size: 15px;
  padding: 12px 0;
}

.login-form :deep(.ant-form-item) {
  margin-bottom: 20px;
}

.login-input :deep(.ant-input-affix-wrapper) {
  border-radius: 10px;
  padding: 10px 16px;
  transition: all 0.3s;
}

.login-input :deep(.ant-input-affix-wrapper:hover) {
  border-color: #667eea;
}

.login-input :deep(.ant-input-affix-wrapper-focused) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15);
}

.login-input :deep(.ant-input) {
  font-size: 15px;
}

.input-icon {
  color: #a5b4fc;
  font-size: 18px;
}

.form-extra {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-extra :deep(.ant-checkbox-wrapper) {
  color: #666;
}

.forgot-link {
  color: #667eea;
  font-size: 14px;
}

.forgot-link:hover {
  color: #764ba2;
}

.login-button {
  height: 48px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.login-button:active {
  transform: translateY(0);
}

.register-row {
  display: flex;
  justify-content: center;
  gap: 8px;
  color: #666;
}

.register-link {
  color: #667eea;
  font-weight: 500;
}

.register-link:hover {
  color: #764ba2;
}

.placeholder-block {
  padding: 40px 0;
  text-align: center;
  color: rgba(0, 0, 0, 0.45);
}

.placeholder-icon {
  font-size: 48px;
  color: #d9d9d9;
  margin-bottom: 16px;
}

.placeholder-block p {
  font-size: 14px;
}

@media (max-width: 900px) {
  .login-container {
    flex-direction: column;
    gap: 40px;
  }
  
  .login-left {
    text-align: center;
  }
  
  .title {
    font-size: 32px;
  }
  
  .subtitle {
    font-size: 18px;
  }
  
  .features {
    align-items: center;
  }
  
  .login-right {
    flex: 0 0 auto;
    width: 100%;
    max-width: 420px;
  }
}
</style>

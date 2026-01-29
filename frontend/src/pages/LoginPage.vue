<template>
  <a-layout style="min-height: 100vh">
    <a-layout-header>
      <div class="logo">员工客户管理系统</div>
    </a-layout-header>
    <a-layout-content class="content">
      <div class="login-wrapper">
        <div class="slogan">
          <h1>员工客户管理系统</h1>
          <p>统一员工与客户资源，防私单合规可追溯</p>
        </div>
        <a-card class="login-card">
          <a-tabs v-model:active-key="activeTab">
            <a-tab-pane key="password" tab="账号密码登录">
              <a-form
                :model="formState"
                :label-col="{ span: 6 }"
                :wrapper-col="{ span: 18 }"
                @submit.prevent="handleSubmit"
              >
                <a-form-item label="账号" required>
                  <a-input
                    v-model:value="formState.username"
                    placeholder="请输入登录账号"
                  />
                </a-form-item>
                <a-form-item label="密码" required>
                  <a-input-password
                    v-model:value="formState.password"
                    placeholder="请输入密码"
                  />
                </a-form-item>
                <a-form-item :wrapper-col="{ offset: 6, span: 18 }">
                  <div class="form-extra">
                    <a-checkbox v-model:checked="rememberMe">
                      记住我
                    </a-checkbox>
                    <a-typography-link @click="handleForgotPassword">
                      忘记密码
                    </a-typography-link>
                  </div>
                </a-form-item>
                <a-form-item :wrapper-col="{ offset: 6, span: 18 }">
                  <a-button
                    type="primary"
                    html-type="submit"
                    :loading="submitting"
                    block
                  >
                    登录
                  </a-button>
                </a-form-item>
                <a-form-item :wrapper-col="{ offset: 6, span: 18 }">
                  <div class="register-row">
                    <span>还没有账号？</span>
                    <a-typography-link @click="handleRegister">
                      立即创建账号
                    </a-typography-link>
                  </div>
                </a-form-item>
              </a-form>
            </a-tab-pane>
            <a-tab-pane key="code" tab="验证码登录">
              <div class="placeholder-block">
                <p>验证码登录暂未开通，将在后续版本提供。</p>
              </div>
            </a-tab-pane>
            <a-tab-pane key="third" tab="第三方登录">
              <div class="placeholder-block">
                <p>第三方登录（企业微信、飞书等）暂未接入。</p>
              </div>
            </a-tab-pane>
          </a-tabs>
        </a-card>
      </div>
    </a-layout-content>
  </a-layout>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { message } from "ant-design-vue";
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
.logo {
  color: #fff;
  font-size: 18px;
}

.content {
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-wrapper {
  display: flex;
  justify-content: center;
  align-items: stretch;
  padding: 40px 16px;
  gap: 40px;
}

.login-card {
  width: 360px;
}

.slogan {
  max-width: 360px;
}

.slogan h1 {
  font-size: 28px;
  margin-bottom: 12px;
}

.slogan p {
  color: rgba(0, 0, 0, 0.45);
}

.form-extra {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.register-row {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.placeholder-block {
  padding: 16px 0;
  color: rgba(0, 0, 0, 0.45);
}

@media (max-width: 768px) {
  .login-wrapper {
    flex-direction: column;
    align-items: center;
  }

  .slogan {
    text-align: center;
  }
}
</style>

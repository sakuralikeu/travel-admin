<template>
  <MainLayout selected-key="settings">
    <a-card title="系统设置">
      <a-typography-paragraph class="settings-description">
        当前页面用于配置密码策略与登录安全等系统级参数，设置结果暂存于本地，可作为后续接入后端统一配置的基础。
      </a-typography-paragraph>
      <a-tabs v-model:active-key="activeTab">
        <a-tab-pane key="password" tab="密码策略">
          <a-form layout="vertical">
            <a-row :gutter="[16, 16]">
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label="最小密码长度">
                  <a-input-number
                    v-model:value="passwordPolicy.minLength"
                    :min="6"
                    :max="64"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label="必须包含数字">
                  <a-switch v-model:checked="passwordPolicy.requireNumber" />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label="必须包含大写字母">
                  <a-switch v-model:checked="passwordPolicy.requireUppercase" />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label="必须包含小写字母">
                  <a-switch v-model:checked="passwordPolicy.requireLowercase" />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label="必须包含特殊字符">
                  <a-switch
                    v-model:checked="passwordPolicy.requireSpecialChar"
                  />
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </a-tab-pane>
        <a-tab-pane key="login" tab="登录安全">
          <a-form layout="vertical">
            <a-row :gutter="[16, 16]">
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label="空闲自动退出时间（分钟）">
                  <a-input-number
                    v-model:value="loginSecurity.sessionTimeoutMinutes"
                    :min="10"
                    :max="480"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label="连续登录失败锁定阈值">
                  <a-input-number
                    v-model:value="loginSecurity.maxLoginFailures"
                    :min="3"
                    :max="20"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label="账号锁定时长（分钟）">
                  <a-input-number
                    v-model:value="loginSecurity.lockDurationMinutes"
                    :min="5"
                    :max="1440"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </a-tab-pane>
      </a-tabs>
      <div class="settings-actions">
        <a-space>
          <a-button type="primary" :loading="saving" @click="handleSave">
            保存设置
          </a-button>
          <a-button @click="handleResetToDefault">
            恢复默认值
          </a-button>
        </a-space>
      </div>
    </a-card>
  </MainLayout>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { message } from "ant-design-vue";
import MainLayout from "../components/MainLayout.vue";

type PasswordPolicySettings = {
  minLength: number;
  requireNumber: boolean;
  requireUppercase: boolean;
  requireLowercase: boolean;
  requireSpecialChar: boolean;
};

type LoginSecuritySettings = {
  sessionTimeoutMinutes: number;
  maxLoginFailures: number;
  lockDurationMinutes: number;
};

type SystemSettings = {
  passwordPolicy: PasswordPolicySettings;
  loginSecurity: LoginSecuritySettings;
};

const STORAGE_KEY = "travel_admin_system_settings";

const activeTab = ref("password");
const saving = ref(false);

const passwordPolicy = reactive<PasswordPolicySettings>({
  minLength: 8,
  requireNumber: true,
  requireUppercase: false,
  requireLowercase: true,
  requireSpecialChar: false
});

const loginSecurity = reactive<LoginSecuritySettings>({
  sessionTimeoutMinutes: 60,
  maxLoginFailures: 5,
  lockDurationMinutes: 30
});

function loadFromStorage() {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) {
    return;
  }
  try {
    const parsed = JSON.parse(raw) as SystemSettings;
    if (parsed.passwordPolicy) {
      passwordPolicy.minLength = parsed.passwordPolicy.minLength ?? 8;
      passwordPolicy.requireNumber =
        parsed.passwordPolicy.requireNumber ?? true;
      passwordPolicy.requireUppercase =
        parsed.passwordPolicy.requireUppercase ?? false;
      passwordPolicy.requireLowercase =
        parsed.passwordPolicy.requireLowercase ?? true;
      passwordPolicy.requireSpecialChar =
        parsed.passwordPolicy.requireSpecialChar ?? false;
    }
    if (parsed.loginSecurity) {
      loginSecurity.sessionTimeoutMinutes =
        parsed.loginSecurity.sessionTimeoutMinutes ?? 60;
      loginSecurity.maxLoginFailures =
        parsed.loginSecurity.maxLoginFailures ?? 5;
      loginSecurity.lockDurationMinutes =
        parsed.loginSecurity.lockDurationMinutes ?? 30;
    }
  } catch {
    message.warning("读取本地系统设置失败，已使用默认值");
  }
}

function handleResetToDefault() {
  passwordPolicy.minLength = 8;
  passwordPolicy.requireNumber = true;
  passwordPolicy.requireUppercase = false;
  passwordPolicy.requireLowercase = true;
  passwordPolicy.requireSpecialChar = false;
  loginSecurity.sessionTimeoutMinutes = 60;
  loginSecurity.maxLoginFailures = 5;
  loginSecurity.lockDurationMinutes = 30;
}

async function handleSave() {
  try {
    saving.value = true;
    const settings: SystemSettings = {
      passwordPolicy: {
        minLength: passwordPolicy.minLength,
        requireNumber: passwordPolicy.requireNumber,
        requireUppercase: passwordPolicy.requireUppercase,
        requireLowercase: passwordPolicy.requireLowercase,
        requireSpecialChar: passwordPolicy.requireSpecialChar
      },
      loginSecurity: {
        sessionTimeoutMinutes: loginSecurity.sessionTimeoutMinutes,
        maxLoginFailures: loginSecurity.maxLoginFailures,
        lockDurationMinutes: loginSecurity.lockDurationMinutes
      }
    };
    localStorage.setItem(STORAGE_KEY, JSON.stringify(settings));
    message.success("系统设置已保存，仅在本地生效");
  } catch {
    message.error("保存系统设置失败");
  } finally {
    saving.value = false;
  }
}

onMounted(() => {
  loadFromStorage();
});
</script>

<style scoped>
.settings-description {
  margin-bottom: 16px;
}

.settings-actions {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>

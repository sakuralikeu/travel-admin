<template>
  <MainLayout selected-key="home">
    <a-typography-title :level="3">欢迎使用员工客户管理系统</a-typography-title>
    <a-typography-paragraph>
      可以通过左侧导航快速进入员工管理、客户管理和操作日志等业务模块。
    </a-typography-paragraph>
  </MainLayout>
</template>

<script setup lang="ts">
import { Typography } from "ant-design-vue";
import MainLayout from "../components/MainLayout.vue";

const { Title: aTypographyTitle, Paragraph: aTypographyParagraph } = Typography;
</script>

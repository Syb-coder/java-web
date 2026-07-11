/** @type {import('tailwindcss').Config} */

// Tailwind 配置：定义学生信息管理系统主题色板与字体
// PRD 4.5 节指定视觉风格为淡蓝、白色为主色调，搭配适量亮色点缀
export default {
  darkMode: "class",
  content: ["./index.html", "./src/**/*.{js,ts,vue}"],
  theme: {
    container: {
      center: true,
    },
    extend: {
      colors: {
        // 主色：用于导航栏、按钮、强调元素，取沉稳的靛蓝
        primary: {
          50: "#eef4ff",
          100: "#d9e6ff",
          200: "#bcd3ff",
          300: "#8eb6ff",
          400: "#598dff",
          500: "#3366ff",
          600: "#1f47f5",
          700: "#1735d9",
          800: "#1a2eb0",
          900: "#1c2d8a",
          DEFAULT: "#3366ff",
        },
        // 辅助色：用于成功/通过状态
        accent: {
          50: "#ecfdf5",
          100: "#d1fae5",
          500: "#10b981",
          600: "#059669",
          700: "#047857",
          DEFAULT: "#10b981",
        },
        // 中性背景色：页面底色与卡片层
        ink: {
          50: "#f7f8fa",
          100: "#eef0f4",
          200: "#e2e5ea",
          300: "#cbd0d8",
          400: "#9aa3b2",
          500: "#6b7280",
          600: "#4b5563",
          700: "#374151",
          800: "#1f2937",
          900: "#111827",
        },
      },
      fontFamily: {
        // 正文使用系统无衬线字体，保证中文渲染清晰
        sans: ['"PingFang SC"', '"Microsoft YaHei"', '"Helvetica Neue"', "Helvetica", "Arial", "sans-serif"],
      },
      boxShadow: {
        // 卡片阴影：柔和下落，营造层次感
        card: "0 1px 3px 0 rgb(0 0 0 / 0.06), 0 1px 2px -1px rgb(0 0 0 / 0.04)",
        cardhover: "0 4px 12px -2px rgb(0 0 0 / 0.08), 0 2px 6px -2px rgb(0 0 0 / 0.04)",
      },
    },
  },
  plugins: [],
};

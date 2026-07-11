import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

// 路由配置：登录页独立，后台页面共享 AdminLayout 布局
const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    // 登录页无需鉴权，独立于主布局之外
    component: () => import('@/pages/Login.vue'),
    meta: { title: '登录' },
  },
  {
    path: '/',
    // 后台主布局：包含顶栏、侧栏与内容区
    component: () => import('@/layouts/AdminLayout.vue'),
    // 路由前置守卫：未登录用户重定向到登录页
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'dashboard',
        component: () => import('@/pages/Dashboard.vue'),
        meta: { title: '首页' },
      },
      {
        path: 'student',
        name: 'student',
        component: () => import('@/pages/StudentManage.vue'),
        meta: { title: '学生信息管理' },
      },
      {
        path: 'teacher',
        name: 'teacher',
        component: () => import('@/pages/TeacherManage.vue'),
        meta: { title: '教师信息管理' },
      },
      {
        path: 'class',
        name: 'class',
        component: () => import('@/pages/ClassManage.vue'),
        meta: { title: '班级管理' },
      },
      {
        path: 'course',
        name: 'course',
        component: () => import('@/pages/CourseManage.vue'),
        meta: { title: '课程管理' },
      },
      {
        path: 'score',
        name: 'score',
        component: () => import('@/pages/ScoreManage.vue'),
        meta: { title: '成绩管理' },
      },
      // P1 全量开发新增路由
      {
        path: 'major',
        name: 'major',
        component: () => import('@/pages/MajorManage.vue'),
        meta: { title: '专业管理' },
      },
      {
        path: 'attendance',
        name: 'attendance',
        component: () => import('@/pages/AttendanceManage.vue'),
        meta: { title: '考勤管理' },
      },
      {
        path: 'approval',
        name: 'approval',
        component: () => import('@/pages/ApprovalManage.vue'),
        meta: { title: '审批管理' },
      },
      {
        path: 'admin-user',
        name: 'admin-user',
        component: () => import('@/pages/AdminManage.vue'),
        meta: { title: '管理员管理' },
      },
      {
        path: 'schedule',
        name: 'schedule',
        component: () => import('@/pages/ScheduleView.vue'),
        meta: { title: '课表展示' },
      },
      {
        path: 'profile',
        name: 'profile',
        component: () => import('@/pages/Profile.vue'),
        meta: { title: '个人中心' },
      },
    ],
  },
  {
    // 兜底路由：未知路径重定向到首页
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard',
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 全局前置守卫：校验登录态与页面标题
router.beforeEach((to, _from, next) => {
  // 设置浏览器标签页标题
  document.title = to.meta.title ? `${to.meta.title} - 学生信息管理系统` : '学生信息管理系统'
  const userStore = useUserStore()
  // 需要鉴权且未登录时跳转登录页
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ name: 'login' })
  } else {
    // 已登录用户访问登录页时跳转首页，避免重复登录
    if (to.name === 'login' && userStore.isLoggedIn) {
      next({ name: 'dashboard' })
    } else {
      next()
    }
  }
})

export default router

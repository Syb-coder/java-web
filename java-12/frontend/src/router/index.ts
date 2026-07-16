// 路由配置：定义前台与后台路由，含登录守卫
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // ===== 前台路由 =====
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      children: [
        { path: '', name: 'Home', component: () => import('@/pages/Home.vue') },
        { path: 'post/:id', name: 'PostDetail', component: () => import('@/pages/PostDetail.vue') },
        { path: 'post/create', name: 'PostCreate', component: () => import('@/pages/PostEdit.vue'), meta: { requiresAuth: true } },
        { path: 'post/edit/:id', name: 'PostEdit', component: () => import('@/pages/PostEdit.vue'), meta: { requiresAuth: true } },
        { path: 'search', name: 'Search', component: () => import('@/pages/Search.vue') },
        { path: 'profile', name: 'Profile', component: () => import('@/pages/Profile.vue'), meta: { requiresAuth: true } },
      ],
    },
    // ===== 登录/注册（无布局） =====
    { path: '/login', name: 'Login', component: () => import('@/pages/Login.vue') },
    { path: '/register', name: 'Register', component: () => import('@/pages/Register.vue') },
    // ===== 后台管理 =====
    {
      path: '/admin',
      component: () => import('@/layouts/AdminLayout.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        { path: '', name: 'AdminDashboard', component: () => import('@/pages/admin/Dashboard.vue') },
        { path: 'plates', name: 'AdminPlates', component: () => import('@/pages/admin/PlateManage.vue') },
        { path: 'users', name: 'AdminUsers', component: () => import('@/pages/admin/UserManage.vue') },
        { path: 'moderators', name: 'AdminModerators', component: () => import('@/pages/admin/ModeratorManage.vue') },
        { path: 'logs', name: 'AdminLogs', component: () => import('@/pages/admin/OperateLog.vue') },
      ],
    },
  ],
})

// 全局前置守卫：校验登录态和管理员权限
router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else if (to.meta.requiresAdmin && !userStore.isAdmin) {
    next({ name: 'Home' })
  } else {
    next()
  }
})

export default router

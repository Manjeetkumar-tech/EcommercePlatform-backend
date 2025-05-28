import { createRouter, createWebHistory } from "vue-router";
import UserLogin from "../components/UserLogin.vue";
import UserDashboard from "../components/UserDashboard.vue";
import UserRegister from "../components/UserRegister.vue";

const routes = [
  {
    path: "/login",
    name: "UserLogin",
    component: UserLogin,
  },
  {
    path: "/dashboard",
    name: "UserDashboard",
    component: UserDashboard,
  },
  {
    path: "/register",
    name: "UserRegister",
    component: UserRegister,
  },
  {
    path: "/",
    redirect: "/login", // Default route
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;

// If you want to restrict access to the /dashboard route unless the user is logged in, use a navigation guard.

// Example: Add a Navigation Guard

// In router/index.js:

// router.beforeEach((to, from, next) => {
//   const isAuthenticated = !!localStorage.getItem("token"); // Check if the user is authenticated
//   if (to.name === "Dashboard" && !isAuthenticated) {
//     next({ name: "Login" }); // Redirect to login if not authenticated
//   } else {
//     next(); // Allow access
//   }
// });

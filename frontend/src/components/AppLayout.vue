<template>
  <div class="layout">
    <header>
      <nav>
        <div class="logo">
          <router-link to="/">E-Commerce</router-link>
        </div>
        <div class="nav-links">
          <router-link v-if="!isAuthenticated" to="/login">Login</router-link>
          <router-link v-if="!isAuthenticated" to="/register"
            >Register</router-link
          >
          <router-link v-if="isAuthenticated" to="/dashboard"
            >Dashboard</router-link
          >
          <router-link v-if="isAuthenticated" @click.prevent="logoutUser"
            >Logout</router-link
          >
        </div>
      </nav>
    </header>

    <main>
      <router-view></router-view>
    </main>

    <footer>
      <p>&copy; 2024 E-Commerce. All rights reserved.</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, watchEffect } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";

// Reactive authentication status
const isAuthenticated = ref(!!localStorage.getItem("token"));
const router = useRouter();

// Watch for changes in authentication status
watchEffect(() => {
  isAuthenticated.value = !!localStorage.getItem("token");
});

const logoutUser = async () => {
  // Clear the token from localStorage
  localStorage.removeItem("token");

  // Optionally, call the backend logout endpoint
  try {
    await axios.post("http://localhost:8080/api/users/logout");
  } catch (error) {
    console.error("Logout error:", error);
  }

  // Redirect to the login page
  router.push("/login");
};
</script>

<style scoped>
.layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

header {
  background-color: #333;
  color: white;
  padding: 1rem;
}

header nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

header .logo {
  font-size: 1.5rem;
  font-weight: bold;
}

header .nav-links a {
  color: white;
  text-decoration: none;
  margin: 0 10px;
}

header .nav-links a:hover {
  text-decoration: underline;
}

footer {
  background-color: #333;
  color: white;
  padding: 1rem;
  text-align: center;
  margin-top: auto;
}

main {
  flex-grow: 1;
  padding: 20px;
}
</style>

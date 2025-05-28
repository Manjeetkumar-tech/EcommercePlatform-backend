<template>
  <div class="login-form">
    <h2>Login</h2>
    <p>
      Don't have an account?
      <router-link to="/register">Register here</router-link>
    </p>
    <form @submit.prevent="loginUser">
      <div>
        <label for="email">Email:</label>
        <input
          type="email"
          v-model="email"
          id="email"
          required
          placeholder="Enter your email"
        />
      </div>

      <div>
        <label for="password">Password:</label>
        <input
          type="password"
          v-model="password"
          id="password"
          required
          placeholder="Enter your password"
        />
      </div>
      <button type="submit">Login</button>
    </form>
    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
  </div>
</template>

<script setup>
import { ref } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

const email = ref("");
const password = ref("");
const errorMessage = ref("");
const router = useRouter();

const loginUser = async () => {
  try {
    const response = await axios.post("http://localhost:8080/api/users/login", {
      email: email.value,
      password: password.value,
    });
    if (response.data.success) {
      localStorage.setItem("user", JSON.stringify(response.data.user));
      if (response.data.token) {
        localStorage.setItem("token", response.data.token);
      }
      console.log("Login successful, redirecting to dashboard...");
      router.push("/dashboard"); // Triggering the redirect
      console.log("Redirected to /dashboard"); // This should show in the console if the navigation is triggered
    } else {
      errorMessage.value =
        response.data.message || "Login failed. Please try again.";
    }
  } catch (error) {
    errorMessage.value =
      error.response?.data?.message || "Login failed. Please try again.";
  }
};
</script>

<style scoped>
.login-form {
  max-width: 400px;
  margin: auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
}
.error {
  color: red;
}
</style>

<template>
  <div class="register-form">
    <h2>Register</h2>
    <form @submit.prevent="registerUser">
      <div>
        <label for="username">Username:</label>
        <input
          type="text"
          v-model="username"
          id="username"
          required
          placeholder="Enter your username"
        />
      </div>

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

      <button type="submit">Register</button>
    </form>
    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    <p v-if="successMessage" class="success">{{ successMessage }}</p>
  </div>
</template>

<script setup>
import { ref } from "vue";
import axios from "axios";

const username = ref("");
const email = ref("");
const password = ref("");
const errorMessage = ref("");
const successMessage = ref("");

const registerUser = async () => {
  try {
    const response = await axios.post(
      "http://localhost:8080/api/users/register",
      {
        username: username.value,
        email: email.value,
        password: password.value,
      }
    );

    // Check if the response contains user data, implying a successful registration
    if (response.data && response.data.id) {
      successMessage.value = "Registration successful! You can now log in.";
      errorMessage.value = "";
    } else {
      // Handle any failure (though your API doesn't provide a failure message)
      errorMessage.value = "Registration failed. Please try again.";
      successMessage.value = "";
    }
  } catch (error) {
    errorMessage.value =
      error.response?.data || "Registration failed. Please try again.";
    successMessage.value = "";
  }
};
</script>

<style scoped>
.register-form {
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
.success {
  color: green;
}
</style>

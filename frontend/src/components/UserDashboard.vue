<template>
  <div class="dashboard-container">
    <header class="dashboard-header">
      <h2>Welcome to Your Dashboard</h2>
      <button @click="logoutUser" class="logout-button">Logout</button>
    </header>
    <p class="login-success-message">You are logged in successfully!</p>

    <div v-if="isLoading" class="loading-message">Loading products...</div>
    <div v-else-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
    <div v-else-if="products.length" class="products-section">
      <h3 class="products-heading">Our Products</h3>
      <ul class="products-grid">
        <li v-for="product in products" :key="product.id" class="product-card">
          <div class="product-name">{{ product.name }}</div>
          <div class="product-price">${{ product.price }}</div>
        </li>
      </ul>
    </div>
    <div v-else class="no-products-message">No products available.</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

const router = useRouter();
const products = ref([]);
const isLoading = ref(true);
const errorMessage = ref("");

const fetchProducts = async () => {
  isLoading.value = true;
  errorMessage.value = "";
  try {
    // Simulate API delay
    // await new Promise(resolve => setTimeout(resolve, 1000));
    const response = await axios.get("http://localhost:8080/api/products");
    products.value = response.data;
  } catch (error) {
    console.error("Failed to fetch products:", error);
    errorMessage.value = "Failed to load products. Please try again later.";
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  if (!localStorage.getItem("token")) {
    router.push("/login");
  } else {
    fetchProducts();
  }
});

const logoutUser = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("user"); // Also remove user info
  router.push("/login");
};
</script>

<style scoped>
/* General body-like feel for the dashboard page */
.dashboard-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 25px;
  background-color: #f4f7f6; /* Light greenish grey, softer than pure white */
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e0e0e0;
}

.dashboard-header h2 {
  color: #2c3e50; /* Dark blue-grey */
  font-size: 2.2em;
  font-weight: 600;
}

.logout-button {
  background-color: #e74c3c; /* Softer red */
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1em;
  font-weight: 500;
  transition: background-color 0.3s ease;
}

.logout-button:hover {
  background-color: #c0392b; /* Darker red on hover */
}

.login-success-message {
  color: #27ae60; /* Green for success */
  background-color: #e9f7ef;
  padding: 10px;
  border-radius: 6px;
  margin-bottom: 25px;
  text-align: center;
  border: 1px solid #a7d7c5;
}

/* Common styling for status messages */
.loading-message,
.error-message,
.no-products-message {
  text-align: center;
  padding: 25px;
  margin-top: 25px;
  border-radius: 8px;
  font-size: 1.15em;
  font-weight: 500;
}

.loading-message {
  background-color: #eaf2f8; /* Light blue */
  color: #3498db;
  border: 1px solid #aed6f1;
}

.error-message {
  background-color: #fdedec; /* Light red */
  color: #c0392b;
  border: 1px solid #f5b7b1;
}

.no-products-message {
  background-color: #fef9e7; /* Light yellow */
  color: #f39c12;
  border: 1px solid #f9e79f;
}

.products-section {
  margin-top: 30px;
}

.products-heading {
  text-align: center;
  font-size: 1.8em;
  color: #34495e; /* Darker blue-grey */
  margin-bottom: 25px;
  font-weight: 600;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); /* Slightly larger cards */
  gap: 25px;
  list-style-type: none;
  padding: 0;
}

.product-card {
  background-color: #ffffff;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
  display: flex;
  flex-direction: column;
  justify-content: space-between; /* For consistent card height feel */
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.12);
}

.product-name {
  font-size: 1.3em;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.product-price {
  font-size: 1.15em;
  color: #007bff; /* Primary blue for price */
  font-weight: 500;
  margin-top: 10px; /* Ensure it's pushed down if name is short */
}
</style>

<template>
  <AppLayout>
    <!-- <img alt="Vue logo" src="./assets/logo.png" /> -->
    <router-view></router-view>
    <!-- <HelloWorld msg="Welcome to Your Vue.js App" /> -->
  </AppLayout>
</template>

<script>
import AppLayout from "./components/AppLayout.vue";

export default {
  name: "App",
  components: {
    AppLayout,
  },
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>

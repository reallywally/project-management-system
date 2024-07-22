<template>
  <div class="login">
    <h2>Login</h2>
    <form @submit.prevent="handleLogin">
      <div>
        <label for="username">Username:</label>
        <input type="text" v-model="username" />
      </div>
      <div>
        <label for="password">Password:</label>
        <input type="password" v-model="password" />
      </div>
      <button type="submit">Login</button>
    </form>
    <p v-if="error" class="error">{{ error }}</p>
  </div>
</template>

<script>
import axios from 'axios';
import { saveToken, getToken } from '../auth';

export default {
  data() {
    return {
      username: '',
      password: '',
      error: ''
    };
  },
  methods: {
    async handleLogin() {
      try {
        const response = await axios.post('localhost:8000/api/login', {
          username: this.username,
          password: this.password
        });
        saveToken(response.data.token);
        this.$router.push('/dashboard');
      } catch (err) {
        this.error = 'Login failed. Please check your credentials.';
      }
    }
  }
};
</script>

<style>
.login {
  max-width: 300px;
  margin: 0 auto;
  padding: 1em;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.error {
  color: red;
}
</style>

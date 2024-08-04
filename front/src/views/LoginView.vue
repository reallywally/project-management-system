<template>
  <v-container>
    <v-form @submit.prevent="handleLogin">
      <v-text-field label="email" v-model="email"></v-text-field>
      <v-text-field label="Password" type="password" v-model="password"></v-text-field>
      <v-btn type="submit" color="primary">Login</v-btn>
    </v-form>
  </v-container>
</template>

<script>
import { mapActions } from 'vuex';

export default {
  name: 'LoginView',
  data() {
    return {
      email: '',
      password: ''
    };
  },
  methods: {
    ...mapActions(['login']),
    async handleLogin() {
      await this.login({ email: this.email, password: this.password });
      if (this.$store.getters.isAuthenticated) {
        this.$router.push('/dashboard');
      } else {
        alert('Login failed');
      }
    }
  }
};
</script>

<template>
  <v-app>
    <v-navigation-drawer v-if="isAuthenticated" app>
      <v-list>
        <v-list-item-group>
          <v-list-item to="/dashboard" exact>
            <v-list-item-content>
              <v-list-item-title>Dashboard</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/client">
            <v-list-item-content>
              <v-list-item-title>Client</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/project">
            <v-list-item-content>
              <v-list-item-title>Project</v-list-item-title>
            </v-list-item-content>
          </v-list-item>

        </v-list-item-group>
      </v-list>
    </v-navigation-drawer>
    <v-app-bar app>
      <v-toolbar-title>My App</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn v-if="isAuthenticated" @click="logout">Logout</v-btn>
    </v-app-bar>
    <v-main>
      <router-view></router-view>
    </v-main>
  </v-app>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';

export default {
  name: 'App',
  components: {},

  computed: {
    ...mapGetters(['isAuthenticated'])
  },
  methods: {
    ...mapActions(['logout']),
  }
};
</script>

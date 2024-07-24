<template>
  <v-card>
    <v-navigation-drawer app>
      <v-list>
        <v-list-item-group>
          <v-list-item to="/" exact>
            <v-list-item-content>
              <v-list-item-title>Home</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/client">
            <v-list-item-content>
              <v-list-item-title>Client</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/dashboard">
            <v-list-item-content>
              <v-list-item-title>Dashboard</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list-item-group>
      </v-list>
    </v-navigation-drawer>

  </v-card>
</template>

<script>
export default {
  data() {
    return {
      items: [
        {title: 'Client', icon: 'mdi-view-dashboard', to: '/client'},
        {title: 'Dashboard', icon: 'mdi-view-dashboard', to: '/dashboard'},

        // 필요한 다른 메뉴 항목을 여기에 추가
      ]
    };
  }
};
</script>

<style>
/* 필요한 스타일을 여기에 추가 */
</style>

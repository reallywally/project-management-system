<template>

  <div>
    <h2>Welcome to the Dashboard</h2>
    <!-- 대시보드 콘텐츠를 여기에 추가하세요 -->
  </div>
</template>

<script>
import Menu from './Menu.vue';
import {isLoggedIn} from '@/auth';

export default {
  components: {
    Menu
  },
};
</script>

<style>
.dashboard {
  padding: 1em;
}
</style>

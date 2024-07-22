<template>
  <div class="dashboard">
    <Menu />
    <div class="content">
      <h2>Welcome to the Dashboard</h2>
      <!-- 대시보드 콘텐츠를 여기에 추가하세요 -->
    </div>
  </div>
</template>

<script>
import Menu from './Menu.vue';

export default {
  components: {
    Menu
  }
};
</script>

<style>
.dashboard {
  display: flex;
}
.content {
  flex: 1;
  padding: 1em;
}
</style>

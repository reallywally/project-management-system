<template>
  <div className="menu">
    <h3>Menu</h3>
    <ul>
      <li>
        <router-link to="/dashboard">Dashboard</router-link>
      </li>
      <li>
        <router-link to="/client">Client</router-link>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'Menu'
};
</script>

<style>
.menu {
  width: 200px;
  padding: 1em;
  border-right: 1px solid #ccc;
}
</style>

<template>
  <v-container>
    <h1>Project Read</h1>
    <v-form>
      <v-text-field label="name" v-model="name"></v-text-field>
      <v-textarea label="description" v-model="description"></v-textarea>
      <v-text-field label="category" v-model="category"></v-text-field>
    </v-form>
  </v-container>
</template>

<script>
import {getProject} from '@/api/project';
export default {
  name: "ProjectRead",
  created() {
    this.projectId = this.$route.params.projectId;
    this.searchProject(this.projectId);
  },
  data() {
    return {
      name: '',
      description: '',
      category: '',
    };
  },
  methods:{
    searchProject(projectId) {
      getProject(projectId).then((res) => {
        console.log('---', res);
        this.name = res.data.name;
        this.description = res.data.description;
        this.category = res.data.category;
      });
    }
  },

}
</script>

<style scoped>

</style>
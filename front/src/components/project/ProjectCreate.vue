<template>
  <v-container>
    <h1>Project Create</h1>
    <v-form>
      <v-text-field label="name" v-model="name"></v-text-field>
      <v-textarea label="description" v-model="description"></v-textarea>
      <v-text-field label="category" v-model="category"></v-text-field>
      <v-btn type="submit" color="primary" @click="create">Create</v-btn>
    </v-form>
  </v-container>
</template>

<script>
// import ProjectCreate from "@/entity/ProjectCreate";
import {postProject} from '@/api/project';

export default {
  name: "ProjectCreate",
  data() {
    return {
      name: '',
      description: '',
      category: '',
    };
  },
  methods:{
    create(){
      // 벨리데이션 추가
      const project = {
        name: this.name,
        description: this.description,
        category: this.category,
      }

      const res = postProject(project);
      res.then((res) => {
        console.log(res)
      });
    },

  }
}
</script>

<style scoped>

</style>
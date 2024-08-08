<template>
  <v-container>
    <v-row>
      <v-col>
        <h2>Project List</h2>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-btn to="/project/create" color="primary">Create Project</v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-data-table
          :headers="headers"
          :items="projects"
          class="row-pointer mt-0" fixed-header
          dense
      >
        <template v-slot:[`item.name`]="{ item}">
          <a href="#" @click.prevent="goToDetail(item.id)">{{ item.name }}</a>
        </template>
      </v-data-table>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import ProjectRepository from "@/repository/ProjectRepository";
import { container } from 'tsyringe'
import Paging from '@/entity/data/Paging'
import Project from '@/entity/project/Project'
import {getProjects} from '@/api/project';
import {onMounted, reactive} from "vue";

const PROJECT_REPOSITORY = container.resolve(ProjectRepository)



// state
type StateType = {
  projectList: Paging<Project>
}
const state = reactive<StateType>({
  projectList: new Paging<Project>(),
})

// methods
function getProjects(page = 1) {
  PROJECT_REPOSITORY.getList(page)
  .then(projectList => {
    console.log(projectList)
    // state.projectList = projectList
  })
}

function goToDetail(id) {
  this.$router.push(`/projects/${id}`);
}

onMounted(() => {
  getProjects()
  console.log("mounted")
})


</script>

<style scoped>

</style>
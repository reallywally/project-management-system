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
      <v-col>
        <v-list>
          <v-list-item-group>
            <v-list-item v-for="project in projects" :key="project.id" :to="`/project/${project.id}`">
              <v-list-item-content>
                <v-list-item-title>{{ project.name }}</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list-item-group>
        </v-list>
      </v-col>
    </v-row>
  </v-container>

</template>

<script>
import {getProjects} from '@/api/project';

export default {
  name: "ProjectList",
  computed: {},
  created() {
    this.searchProjects(0, 10);

  },
  data() {
    return {
      projects: {},
      pageModel: {
        totalElements: 0,
        totalPages: 0,
      },
    };
  },
  methods: {
    searchProjects(page, size) {
      const pageModel = {
        page: page,
        size: size,
      };

      const res = getProjects(pageModel);
      res.then((res) => {
        this.products = res.data.content;
        this.pageModel = {
          totalElements: res.data.totalElements,
          totalPages: res.data.totalPages,
        };
      });
    }
  }
}
</script>

<style scoped>

</style>
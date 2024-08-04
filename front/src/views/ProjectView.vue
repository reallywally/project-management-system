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
        <template v-slot:item.name="{ item, index}">
          <a href="#" @click.prevent="goToDetail(item.id)">{{ item.name }}</a>
        </template>
      </v-data-table>
    </v-row>
  </v-container>
</template>

<script>
import {getProjects} from '@/api/project';

export default {
  name: "ProjectView",
  computed: {},
  created() {
    this.searchProjects(0, 10);
  },
  data() {
    return {
      headers: [
        { text: 'name', value: 'name' , width: 60, align: 'right'},
        { text: 'description', value: 'description' , width: 60, align: 'right'},
        { text: 'category', value: 'category' , width: 60, align: 'right'},
      ],
      projects: [],
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
        console.log(res)
        this.projects = res.data.content;

        this.pageModel = {
          totalElements: res.data.totalElements,
          totalPages: res.data.totalPages,
        };
      });
    },
    goToDetail(id) {
      this.$router.push(`/projects/${id}`);
    }
  }
}
</script>

<style scoped>

</style>
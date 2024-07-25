import axios from 'axios';

const projectModule = {
    state: {
        projects: []
    },
    mutations: {
        setProjects(state, projects) {
            state.projects = projects;
        },
        addProject(state, project) {
            state.projects.push(project);
        },
        updateProject(state, updatedProject) {
            const index = state.projects.findIndex(project => project.id === updatedProject.id);
            if (index !== -1) {
                state.projects.splice(index, 1, updatedProject);
            }
        },
        deleteProject(state, projectId) {
            state.projects = state.projects.filter(project => project.id !== projectId);
        }
    },
    actions: {
        async fetchProjects({ commit }) {
            try {
                const response = await axios.get('http://yourserver.com/api/projects');
                commit('setProjects', response.data);
            } catch (error) {
                console.error('Error fetching projects:', error);
            }
        },
        async createProject({ commit }, project) {
            try {
                const response = await axios.post('http://yourserver.com/api/projects', project);
                commit('addProject', response.data);
            } catch (error) {
                console.error('Error creating project:', error);
            }
        },
        async updateProject({ commit }, project) {
            try {
                const response = await axios.put(`http://yourserver.com/api/projects/${project.id}`, project);
                commit('updateProject', response.data);
            } catch (error) {
                console.error('Error updating project:', error);
            }
        },
        async deleteProject({ commit }, projectId) {
            try {
                await axios.delete(`http://yourserver.com/api/projects/${projectId}`);
                commit('deleteProject', projectId);
            } catch (error) {
                console.error('Error deleting project:', error);
            }
        }
    },
    getters: {
        projects: (state) => state.projects
    }
};

export default projectModule;

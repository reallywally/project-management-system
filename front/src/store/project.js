const projectModule = {
    state: {
        projects: []
    },
    getters: {
        projects: (state) => state.projects
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
    },

};

export default projectModule;

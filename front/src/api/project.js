import {request} from "./index";

export const getProjects = (pageModel) => {
  return request.get(`/api/projects`, {
    params: pageModel,
  });
};

export const getProject = (id) => {
  return request.get(`/api/projects/${id}`);
};

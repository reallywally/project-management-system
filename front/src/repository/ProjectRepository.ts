import HttpRepository from '@/repository/HttpRepository'
import { inject, singleton } from 'tsyringe'
import type ProjectCreate from '@/entity/project/ProjectCreate'
import { plainToClass, plainToInstance } from 'class-transformer'
import Project from '@/entity/project/Project'
import Paging from '@/entity/data/Paging'

@singleton()
export default class ProjectRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}

  public create(request: ProjectCreate) {
    return this.httpRepository.post({
      path: '/api/projects',
      body: request,
    })
  }

  public get(projectId: number) {
    return this.httpRepository.get<Project>({ path: `/api/projects/${projectId}` }, Project)
  }

  public getList(page: number) {
    return this.httpRepository.getList<Project>(
      {
        path: `/api/projects?page=${page}&size=3`,
      },
      Project
    )
  }

  public delete(postId: number) {
    return this.httpRepository.delete({
      path: `/api/projects/${postId}`,
    })
  }
}

import AxiosHttpClient, { type HttpRequestConfig } from '@/http/AxiosHttpClient'
import { inject, singleton } from 'tsyringe'
import { plainToInstance } from 'class-transformer'
import Null from '@/entity/data/Null'
import Paging from '@/entity/data/Paging'

@singleton()
export default class HttpRepository {
  constructor(@inject(AxiosHttpClient) private readonly httpClient: AxiosHttpClient) {}

  public get<T>(config: HttpRequestConfig, clazz: { new (...args: any[]) }): Promise<T> {
    return this.httpClient.request({ ...config, method: 'GET' }).then((response) => plainToInstance(clazz, response))
  }

  public getList<T>(config: HttpRequestConfig, clazz: { new (...args: any[]) }): Promise<Paging<T>> {
    return this.httpClient.request({ ...config, method: 'GET' }).then((response) => {
      const paging = plainToInstance<Paging<T>, any>(Paging, response)
      const items = plainToInstance<T, any>(clazz, response.items)
      paging.setItems(items)
      return paging
    })
  }

  public post<T>(config: HttpRequestConfig, clazz: { new (...args: any[]) } | null = null): Promise<T> {
    return this.httpClient
      .request({ ...config, method: 'POST' })
      .then((response) => plainToInstance(clazz !== null ? clazz : Null, response))
  }

  public patch<T>(config: HttpRequestConfig, clazz: { new (...args: any[]) } | null = null): Promise<T> {
    return this.httpClient
      .request({
        ...config,
        method: 'PATCH',
      })
      .then((response) => plainToInstance(clazz !== null ? clazz : Null, response))
  }

  public delete<T>(config: HttpRequestConfig, clazz: { new (...args: any[]) } | null = null): Promise<T> {
    return this.httpClient
      .request({ ...config, method: 'DELETE' })
      .then((response) => plainToInstance(clazz !== null ? clazz : Null, response))
  }
}

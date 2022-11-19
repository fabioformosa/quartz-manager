import {Injectable} from '@angular/core';
import {ApiService} from './api.service';
import {CONTEXT_PATH, getBaseUrl} from './config.service';
import {Observable} from 'rxjs';

@Injectable()
export default class JobService {

  constructor(
    private apiService: ApiService
  ) {
  }

  fetchJobs = (): Observable<string[]> => {
    return this.apiService.get(getBaseUrl() + `${CONTEXT_PATH}/jobs`)
  }

}

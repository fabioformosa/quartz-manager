import {Injectable} from '@angular/core';
import {ApiService} from './api.service';
import {CONTEXT_PATH, getBaseUrl} from './config.service';
import {Observable} from 'rxjs';
import {ScheduledJob} from '../model/scheduled-job.model';

@Injectable()
export default class JobService {

  constructor(
    private apiService: ApiService
  ) {
  }

  fetchJobs = (): Observable<string[]> => {
    return this.apiService.get(getBaseUrl() + `${CONTEXT_PATH}/job-classes`)
  }

  fetchScheduledJobs = (): Observable<ScheduledJob[]> => {
    return this.apiService.get(getBaseUrl() + `${CONTEXT_PATH}/jobs`)
  }

  triggerJob = (job: ScheduledJob): Observable<void> => {
    return this.apiService.post(getBaseUrl() + `${CONTEXT_PATH}/jobs/${job.jobKeyDTO.group}/${job.jobKeyDTO.name}/trigger`, {})
  }

  deleteJob = (job: ScheduledJob): Observable<void> => {
    return this.apiService.delete(getBaseUrl() + `${CONTEXT_PATH}/jobs/${job.jobKeyDTO.group}/${job.jobKeyDTO.name}`)
  }

}

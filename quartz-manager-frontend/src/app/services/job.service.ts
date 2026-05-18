import {Injectable} from '@angular/core';
import {ApiService} from './api.service';
import {CONTEXT_PATH, getBaseUrl} from './config.service';
import {Observable} from 'rxjs';
import {ScheduledJob} from '../model/scheduled-job.model';
import {ScheduledJobCommand} from '../model/scheduled-job.command';
import {InterruptResult} from '../model/interrupt-result.model';

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

  getScheduledJob = (group: string, name: string): Observable<ScheduledJob> => {
    return this.apiService.get(getBaseUrl() + `${CONTEXT_PATH}/jobs/${group || 'DEFAULT'}/${name}`)
  }

  createJob = (group: string, name: string, command: ScheduledJobCommand): Observable<ScheduledJob> => {
    return this.apiService.post(getBaseUrl() + `${CONTEXT_PATH}/jobs/${group || 'DEFAULT'}/${name}`, command)
  }

  updateJob = (group: string, name: string, command: ScheduledJobCommand): Observable<ScheduledJob> => {
    return this.apiService.put(getBaseUrl() + `${CONTEXT_PATH}/jobs/${group || 'DEFAULT'}/${name}`, command)
  }

  triggerJob = (job: ScheduledJob): Observable<void> => {
    return this.apiService.post(getBaseUrl() + `${CONTEXT_PATH}/jobs/${job.jobKeyDTO.group}/${job.jobKeyDTO.name}/trigger`, {})
  }

  pauseJob = (job: ScheduledJob): Observable<void> => {
    return this.apiService.post(getBaseUrl() + `${CONTEXT_PATH}/jobs/${job.jobKeyDTO.group}/${job.jobKeyDTO.name}/pause`, {})
  }

  pauseJobGroup = (group: string): Observable<void> => {
    return this.apiService.post(getBaseUrl() + `${CONTEXT_PATH}/jobs/groups/${group || 'DEFAULT'}/pause`, {})
  }

  interruptJob = (job: ScheduledJob): Observable<InterruptResult> => {
    return this.apiService.post(getBaseUrl() + `${CONTEXT_PATH}/jobs/${job.jobKeyDTO.group}/${job.jobKeyDTO.name}/interrupt`, {})
  }

  interruptJobKey = (group: string, name: string): Observable<InterruptResult> => {
    return this.apiService.post(getBaseUrl() + `${CONTEXT_PATH}/jobs/${group || 'DEFAULT'}/${name}/interrupt`, {})
  }

  deleteJob = (job: ScheduledJob): Observable<void> => {
    return this.apiService.delete(getBaseUrl() + `${CONTEXT_PATH}/jobs/${job.jobKeyDTO.group}/${job.jobKeyDTO.name}`)
  }

}

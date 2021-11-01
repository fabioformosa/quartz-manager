import { Injectable } from '@angular/core';
import { getBaseUrl } from '.';
import { ApiService } from './api.service';

@Injectable()
export class SchedulerService {

  constructor(
    private apiService: ApiService
  ) { }

  startScheduler = () => {
    return this.apiService.get(getBaseUrl() + '/quartz-manager/scheduler/run')
  }

  stopScheduler = () => {
    return this.apiService.get(getBaseUrl() + '/quartz-manager/scheduler/stop')
  }

  pauseScheduler = () => {
    return this.apiService.get(getBaseUrl() + '/quartz-manager/scheduler/pause')
  }

  resumeScheduler = () => {
    return this.apiService.get(getBaseUrl() + '/quartz-manager/scheduler/resume')
  }

  getStatus = () => {
    return this.apiService.get(getBaseUrl() + '/quartz-manager/scheduler/status')
  }

  getScheduler = () => {
    return this.apiService.get(getBaseUrl() + '/quartz-manager/scheduler')
  }

  getConfig = () => {
    return this.apiService.get(getBaseUrl() + '/quartz-manager/scheduler/config')
  }

  saveConfig = (config: Object) => {
    return this.apiService.post(getBaseUrl() + '/quartz-manager/triggers/mytrigger', config)
  }

  updateConfig = (config: Object) => {
    return this.apiService.put(getBaseUrl() + '/quartz-manager/triggers/mytrigger', config)
  }


}

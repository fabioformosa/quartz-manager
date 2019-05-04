import { Injectable } from '@angular/core';
import { Headers } from '@angular/http';
import { ApiService } from './api.service';

@Injectable()
export class SchedulerService {

  constructor(
    private apiService: ApiService
  ) { }

  startScheduler = () => {
    return this.apiService.get('/quartz-manager/scheduler/run')
  }
  
  stopScheduler = () => {
    return this.apiService.get('/quartz-manager/scheduler/stop')
  }
  
  pauseScheduler = () => {
    return this.apiService.get('/quartz-manager/scheduler/pause')
  }
  
  resumeScheduler = () => {
    return this.apiService.get('/quartz-manager/scheduler/resume')
  }

  getStatus = () => {
    return this.apiService.get('/quartz-manager/scheduler')
  }

  getConfig = () => {
    return this.apiService.get('/quartz-manager/scheduler/config')
  }

  updateConfig = (config: Object) => {
    return this.apiService.post('/quartz-manager/scheduler/config', config)
  }
}

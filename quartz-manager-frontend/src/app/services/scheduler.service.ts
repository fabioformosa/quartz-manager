import { Injectable } from '@angular/core';
import { getBaseUrl } from '.';
import { ApiService } from './api.service';
import {Trigger} from '../model/trigger.model';
import {Observable} from 'rxjs';
import {SimpleTriggerCommand} from '../model/simple-trigger.command';
import {SchedulerConfig} from '../model/schedulerConfig.model';

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

  // deprecated
  getConfig = () => {
    return this.apiService.get(getBaseUrl() + '/quartz-manager/scheduler/config')
  }

  getSimpleTriggerConfig = (): Observable<Trigger> => {
    return this.apiService.get(getBaseUrl() + '/quartz-manager/simple-triggers/my-simple-trigger');
  }

  // deprecated
  saveConfig = (config: Object) => {
    return this.apiService.post(getBaseUrl() + '/quartz-manager/triggers/mytrigger', config)
  }

  saveSimpleTriggerConfig = (config: SimpleTriggerCommand) => {
    return this.apiService.post(getBaseUrl() + '/quartz-manager/simple-triggers/my-simple-trigger', config)
  }

  // deprecated
  updateConfig = (config: SchedulerConfig) => {
    return this.apiService.put(getBaseUrl() + '/quartz-manager/triggers/mytrigger', config)
  }

  updateSimpleTriggerConfig = (config: SimpleTriggerCommand) => {
    return this.apiService.put(getBaseUrl() + '/quartz-manager/simple-triggers/my-simple-trigger', config)
  }


}

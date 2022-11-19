import {Injectable} from '@angular/core';
import {CONTEXT_PATH, getBaseUrl} from '.';
import {ApiService} from './api.service';
import {Trigger} from '../model/trigger.model';
import {Observable} from 'rxjs';
import {SimpleTriggerCommand} from '../model/simple-trigger.command';
import {Scheduler} from '../model/scheduler.model';


@Injectable()
export class SchedulerService {

  constructor(
    private apiService: ApiService
  ) { }

  startScheduler = (): Observable<void> => {
    return this.apiService.get(getBaseUrl() + `${CONTEXT_PATH}/scheduler/run`);
  }

  stopScheduler = (): Observable<void> => {
    return this.apiService.get(getBaseUrl() + `${CONTEXT_PATH}/scheduler/stop`);
  }

  pauseScheduler = (): Observable<void> => {
    return this.apiService.get(getBaseUrl() + `${CONTEXT_PATH}/scheduler/pause`);
  }

  resumeScheduler = (): Observable<void> => {
    return this.apiService.get(getBaseUrl() + `${CONTEXT_PATH}/scheduler/resume`);
  }

  getStatus = () => {
    return this.apiService.get(getBaseUrl() + `${CONTEXT_PATH}/scheduler/status`);
  }

  getScheduler = (): Observable<Scheduler> => {
    return this.apiService.get(getBaseUrl() + `${CONTEXT_PATH}/scheduler`);
  }

  getSimpleTriggerConfig = (triggerName: string): Observable<Trigger> => {
    return this.apiService.get(getBaseUrl() + `${CONTEXT_PATH}/simple-triggers/${triggerName}`);
  }

  saveSimpleTriggerConfig = (config: SimpleTriggerCommand) => {
    return this.apiService.post(getBaseUrl() + `${CONTEXT_PATH}/simple-triggers/${config.triggerName}`, config)
  }

  updateSimpleTriggerConfig = (config: SimpleTriggerCommand) => {
    return this.apiService.put(getBaseUrl() + `${CONTEXT_PATH}/simple-triggers/${config.triggerName}`, config)
  }


}

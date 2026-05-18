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
    return this.apiService.post(getBaseUrl() + `${CONTEXT_PATH}/scheduler/start`, {});
  }

  startSchedulerDelayed = (seconds: number): Observable<void> => {
    return this.apiService.post(getBaseUrl() + `${CONTEXT_PATH}/scheduler/start-delayed/${seconds}`, {});
  }

  pauseAll = (): Observable<void> => {
    return this.apiService.post(getBaseUrl() + `${CONTEXT_PATH}/scheduler/pause-all`, {});
  }

  clearScheduler = (): Observable<void> => {
    return this.apiService.delete(getBaseUrl() + `${CONTEXT_PATH}/scheduler`);
  }

  shutdownScheduler = (): Observable<void> => {
    return this.apiService.post(getBaseUrl() + `${CONTEXT_PATH}/scheduler/shutdown`, {});
  }

  standbyScheduler = (): Observable<void> => {
    return this.apiService.post(getBaseUrl() + `${CONTEXT_PATH}/scheduler/standby`, {});
  }

  resumeScheduler = (): Observable<void> => {
    return this.apiService.post(getBaseUrl() + `${CONTEXT_PATH}/scheduler/resume`, {});
  }

  getStatus = () => {
    return this.apiService.get(getBaseUrl() + `${CONTEXT_PATH}/scheduler/status`);
  }

  getScheduler = (): Observable<Scheduler> => {
    return this.apiService.get(getBaseUrl() + `${CONTEXT_PATH}/scheduler`);
  }

  getSimpleTriggerConfig = (triggerName: string, triggerGroup = 'DEFAULT'): Observable<Trigger> => {
    return this.apiService.get(getBaseUrl() + `${CONTEXT_PATH}/simple-triggers/${triggerGroup}/${triggerName}`);
  }

  saveSimpleTriggerConfig = (config: SimpleTriggerCommand) => {
    return this.apiService.post(getBaseUrl() + `${CONTEXT_PATH}/simple-triggers/${config.triggerGroup}/${config.triggerName}`, config)
  }

  updateSimpleTriggerConfig = (config: SimpleTriggerCommand) => {
    return this.apiService.put(getBaseUrl() + `${CONTEXT_PATH}/simple-triggers/${config.triggerGroup}/${config.triggerName}`, config)
  }


}

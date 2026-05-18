import {ApiService} from './api.service';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {Trigger} from '../model/trigger.model';
import {TriggerKey} from '../model/triggerKey.model';
import {CONTEXT_PATH, getBaseUrl} from './config.service';
import {TriggerCommand} from '../model/trigger-command.model';

@Injectable()
export class TriggerService {

  constructor(
    private apiService: ApiService) {
  }

  fetchTriggers = (): Observable<Array<TriggerKey>> => {
    return this.apiService.get(getBaseUrl() + `${CONTEXT_PATH}/triggers`);
  }

  getTrigger = (triggerKey: TriggerKey): Observable<Trigger> => {
    return this.apiService.get(getBaseUrl() + `${CONTEXT_PATH}/triggers/${triggerKey.group || 'DEFAULT'}/${triggerKey.name}`);
  }

  saveTrigger = (group: string, name: string, config: TriggerCommand): Observable<Trigger> => {
    return this.apiService.post(getBaseUrl() + `${CONTEXT_PATH}/triggers/${group || 'DEFAULT'}/${name}`, config);
  }

  updateTrigger = (group: string, name: string, config: TriggerCommand): Observable<Trigger> => {
    return this.apiService.put(getBaseUrl() + `${CONTEXT_PATH}/triggers/${group || 'DEFAULT'}/${name}`, config);
  }

  pauseTrigger = (triggerKey: TriggerKey): Observable<void> => {
    return this.apiService.post(getBaseUrl() + `${CONTEXT_PATH}/triggers/${triggerKey.group || 'DEFAULT'}/${triggerKey.name}/pause`, {});
  }

  resumeTrigger = (triggerKey: TriggerKey): Observable<void> => {
    return this.apiService.post(getBaseUrl() + `${CONTEXT_PATH}/triggers/${triggerKey.group || 'DEFAULT'}/${triggerKey.name}/resume`, {});
  }

  resetTriggerFromErrorState = (triggerKey: TriggerKey): Observable<void> => {
    return this.apiService.post(getBaseUrl() + `${CONTEXT_PATH}/triggers/${triggerKey.group || 'DEFAULT'}/${triggerKey.name}/reset-error`, {});
  }

  unscheduleTrigger = (triggerKey: TriggerKey): Observable<void> => {
    return this.apiService.delete(getBaseUrl() + `${CONTEXT_PATH}/triggers/${triggerKey.group || 'DEFAULT'}/${triggerKey.name}`);
  }

}

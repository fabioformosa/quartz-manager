import {ApiService} from './api.service';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {Trigger} from '../model/trigger.model';
import {TriggerKey} from '../model/triggerKey.model';
import {getBaseUrl} from './config.service';

@Injectable()
export class TriggerService {

  constructor(
    private apiService: ApiService) {
  }

  fetchTriggers = (): Observable<Array<TriggerKey>> => {
    return this.apiService.get(getBaseUrl() + 'quartz-manager/triggers');
  }


}

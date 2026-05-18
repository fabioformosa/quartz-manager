import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ApiService} from './api.service';
import {CONTEXT_PATH, getBaseUrl} from './config.service';
import {CurrentExecution} from '../model/current-execution.model';

@Injectable()
export class ExecutionService {

  constructor(private apiService: ApiService) {
  }

  fetchCurrentExecutions = (): Observable<CurrentExecution[]> => {
    return this.apiService.get(getBaseUrl() + `${CONTEXT_PATH}/executions/current`);
  }

  fetchRecoveringExecutions = (): Observable<CurrentExecution[]> => {
    return this.apiService.get(getBaseUrl() + `${CONTEXT_PATH}/executions/recovering`);
  }
}

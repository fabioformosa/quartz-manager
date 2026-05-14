import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ApiService} from './api.service';
import {CONTEXT_PATH, getBaseUrl} from './config.service';
import {CalendarIncludedTimeTest, QuartzCalendar} from '../model/calendar.model';

@Injectable()
export class CalendarService {
  constructor(private apiService: ApiService) {}

  fetchCalendars = (): Observable<QuartzCalendar[]> => {
    return this.apiService.get(getBaseUrl() + `${CONTEXT_PATH}/calendars`);
  }

  getCalendar = (name: string): Observable<QuartzCalendar> => {
    return this.apiService.get(getBaseUrl() + `${CONTEXT_PATH}/calendars/${name}`);
  }

  createCalendar = (name: string, calendar: QuartzCalendar): Observable<QuartzCalendar> => {
    return this.apiService.post(getBaseUrl() + `${CONTEXT_PATH}/calendars/${name}`, calendar);
  }

  updateCalendar = (name: string, calendar: QuartzCalendar): Observable<QuartzCalendar> => {
    return this.apiService.put(getBaseUrl() + `${CONTEXT_PATH}/calendars/${name}`, calendar);
  }

  deleteCalendar = (name: string): Observable<void> => {
    return this.apiService.delete(getBaseUrl() + `${CONTEXT_PATH}/calendars/${name}`);
  }

  testIncludedTime = (name: string, time: Date): Observable<CalendarIncludedTimeTest> => {
    return this.apiService.post(getBaseUrl() + `${CONTEXT_PATH}/calendars/${name}/included-time-test`, {time});
  }
}

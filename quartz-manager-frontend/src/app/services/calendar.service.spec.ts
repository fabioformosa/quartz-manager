import {jest} from '@jest/globals';
import {CalendarService} from './calendar.service';

describe('CalendarService', () => {
  let apiService: any;
  let calendarService: CalendarService;

  beforeEach(() => {
    apiService = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      delete: jest.fn()
    };
    calendarService = new CalendarService(apiService);
  });

  it('uses calendar registry endpoints', () => {
    const calendar: any = {name: 'weekends', type: 'WEEKLY'};
    const time = new Date('2026-05-12T12:00:00.000Z');

    calendarService.fetchCalendars();
    calendarService.getCalendar('weekends');
    calendarService.createCalendar('weekends', calendar);
    calendarService.updateCalendar('weekends', calendar);
    calendarService.deleteCalendar('weekends');
    calendarService.testIncludedTime('weekends', time);

    expect(apiService.get).toHaveBeenCalledWith('/quartz-manager/calendars');
    expect(apiService.get).toHaveBeenCalledWith('/quartz-manager/calendars/weekends');
    expect(apiService.post).toHaveBeenCalledWith('/quartz-manager/calendars/weekends', calendar);
    expect(apiService.put).toHaveBeenCalledWith('/quartz-manager/calendars/weekends', calendar);
    expect(apiService.delete).toHaveBeenCalledWith('/quartz-manager/calendars/weekends');
    expect(apiService.post).toHaveBeenCalledWith('/quartz-manager/calendars/weekends/included-time-test', {time});
  });
});

import {TriggerKey} from './triggerKey.model';

export type CalendarType = 'ANNUAL' | 'CRON' | 'DAILY' | 'HOLIDAY' | 'MONTHLY' | 'WEEKLY';

export class QuartzCalendar {
  name: string;
  type: CalendarType = 'WEEKLY';
  description: string;
  cronExpression: string;
  timeZone: string;
  rangeStartingTime: string;
  rangeEndingTime: string;
  invertTimeRange: boolean;
  excludedDaysOfWeek: number[];
  excludedDaysOfMonth: number[];
  excludedDates: Date[];
  triggerKeys: TriggerKey[];
}

export class CalendarIncludedTimeTest {
  time: Date;
  included: boolean;
  nextIncludedTime: Date;
}

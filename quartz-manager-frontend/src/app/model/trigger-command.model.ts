import {JobKeyModel} from './jobKey.model';

export type TriggerType = 'SIMPLE' | 'CRON' | 'DAILY_TIME_INTERVAL' | 'CALENDAR_INTERVAL';

export class TriggerCommand {
  triggerType: TriggerType = 'SIMPLE';
  jobClass: string;
  jobKey: JobKeyModel;
  startDate: Date;
  endDate: Date;
  description: string;
  priority: number;
  calendarName: string;
  misfireInstruction: string;
  jobDataMap: {[key: string]: unknown};
  repeatCount: number;
  repeatInterval: number;
  repeatIntervalUnit: string;
  cronExpression: string;
  timeZone: string;
  startTimeOfDay: string;
  endTimeOfDay: string;
  daysOfWeek: number[];
  preserveHourOfDayAcrossDaylightSavings: boolean;
  skipDayIfHourDoesNotExist: boolean;
}

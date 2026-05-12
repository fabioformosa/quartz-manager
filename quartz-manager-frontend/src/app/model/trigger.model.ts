import {TriggerKey} from './triggerKey.model';
import {JobKeyModel} from './jobKey.model';
import {JobDetail} from './jobDetail.model';

export class Trigger {
  triggerKeyDTO: TriggerKey = new TriggerKey();
  priority: number;
  startTime: Date;
  description: string;
  endTime: Date;
  finalFireTime: Date;
  misfireInstruction: number;
  nextFireTime: Date;
  previousFireTime: Date;
  type: string;
  state: string;
  calendarName: string;
  jobKeyDTO: JobKeyModel;
  jobDetailDTO: JobDetail = new JobDetail();
  mayFireAgain: boolean;
  jobDataMap: {[key: string]: unknown};
  cronExpression: string;
  timeZone: string;
  repeatInterval: number;
  repeatCount: number;
  repeatIntervalUnit: string;
  startTimeOfDay: string;
  endTimeOfDay: string;
  daysOfWeek: number[];
  preserveHourOfDayAcrossDaylightSavings: boolean;
  skipDayIfHourDoesNotExist: boolean;
}

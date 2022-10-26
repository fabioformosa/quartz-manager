import {Moment} from 'moment/moment';

export class SimpleTriggerForm {
  triggerName: string;
  jobClass: string;
  startDate: Moment;
  endDate: Moment;
  repeatCount: number;
  repeatInterval: number;
  misfireInstruction: string;
}

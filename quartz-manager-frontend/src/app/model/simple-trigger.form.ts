import {Moment} from 'moment/moment';

export class SimpleTriggerForm {
  triggerName: string;
  startDate: Moment;
  endDate: Moment;
  repeatCount: number;
  repeatInterval: number;
}

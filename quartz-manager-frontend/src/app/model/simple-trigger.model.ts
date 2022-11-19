import {Trigger} from './trigger.model';

export class SimpleTrigger extends Trigger {
  repeatCount: number;
  repeatInterval: number;
  timesTriggered: number;
}

import {JobKeyModel} from './jobKey.model';
import {TriggerKey} from './triggerKey.model';

export class CurrentExecution {
  fireInstanceId: string;
  jobKeyDTO: JobKeyModel;
  triggerKeyDTO: TriggerKey;
  fireTime: Date;
  scheduledFireTime: Date;
  previousFireTime: Date;
  nextFireTime: Date;
  runTime: number;
  refireCount: number;
  recovering: boolean;
  node: string;
}

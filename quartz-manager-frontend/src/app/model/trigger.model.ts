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
  jobKeyDTO: JobKeyModel;
  jobDetailDTO: JobDetail = new JobDetail();
  mayFireAgain: boolean;
}

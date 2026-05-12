import {JobKeyModel} from './jobKey.model';
import {TriggerKey} from './triggerKey.model';

export class ScheduledJob {
  jobKeyDTO: JobKeyModel;
  jobClassName: string;
  description: string;
  durable: boolean;
  requestsRecovery: boolean;
  jobDataMap: {[key: string]: unknown};
  triggerKeys: TriggerKey[];
}

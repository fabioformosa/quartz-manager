import {TriggerKey} from './triggerKey.model';

export class Scheduler {
  name: string;
  instanceId: string;
  status: string;
  triggerKeys: TriggerKey[];

  constructor(name: string, instanceId: string, status: string, triggerKeys: TriggerKey[]) {
    this.name = name;
    this.status = status;
    this.instanceId = instanceId;
    this.triggerKeys = triggerKeys;
  }
}

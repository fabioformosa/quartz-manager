import {TriggerKey} from './triggerKey.model';

export class Scheduler {
  name: string;
  instanceId: string;
  triggerKeys: TriggerKey[];

  constructor(name: string, instanceId: string, triggerKeys: TriggerKey[]) {
    this.name = name;
    this.instanceId = instanceId;
    this.triggerKeys = triggerKeys;
  }
}

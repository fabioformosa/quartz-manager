import {TriggerKey} from './triggerKey.model';

export class Scheduler {
  name: string;
  instanceId: string;
  status: string;
  triggerKeys: TriggerKey[];
  quartzVersion: string;
  jobStoreClass: string;
  jobStoreSupportsPersistence: boolean;
  clustered: boolean;
  threadPoolClass: string;
  threadPoolSize: number;
  runningSince: string;
  numberOfJobsExecuted: number;

  constructor(name: string, instanceId: string, status: string, triggerKeys: TriggerKey[]) {
    this.name = name;
    this.status = status;
    this.instanceId = instanceId;
    this.triggerKeys = triggerKeys;
  }
}

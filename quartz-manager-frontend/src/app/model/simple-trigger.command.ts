export class SimpleTriggerCommand {
  triggerName: string;
  triggerGroup: string;
  jobClass: string;
  jobKey: {group: string; name: string};
  startDate: Date;
  endDate: Date;
  repeatCount: number;
  repeatInterval: number;
  misfireInstruction: string;
  jobDataMap: {[key: string]: unknown};
}

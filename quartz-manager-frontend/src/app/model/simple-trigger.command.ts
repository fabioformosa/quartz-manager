export class SimpleTriggerCommand {
  triggerName: string;
  triggerGroup: string;
  jobClass: string;
  startDate: Date;
  endDate: Date;
  repeatCount: number;
  repeatInterval: number;
  misfireInstruction: string;
}

export class SimpleTriggerCommand {
  triggerName: string;
  jobClass: string;
  startDate: Date;
  endDate: Date;
  repeatCount: number;
  repeatInterval: number;
  misfireInstruction: string;
}

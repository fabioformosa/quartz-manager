export class ScheduledJobCommand {
  jobClass: string;
  description: string;
  durable: boolean;
  requestsRecovery: boolean;
  jobDataMap: {[key: string]: unknown};
}

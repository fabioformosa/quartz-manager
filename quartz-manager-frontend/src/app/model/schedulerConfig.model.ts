export class SchedulerConfig {

    triggerPerDay = 0;
    maxCount = 0;
    timesTriggered = 0;

    constructor(triggerPerDay = 0, maxCount = 0, timesTriggered = 0) {
      this.triggerPerDay = triggerPerDay;
      this.maxCount = maxCount;
      this.timesTriggered = timesTriggered;
    }

  }

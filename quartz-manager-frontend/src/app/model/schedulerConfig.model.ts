export class SchedulerConfig {

    triggerPerDay : number = 0
    maxCount : number = 0

    constructor(triggerPerDay = 0, maxCount = 0) {
      this.triggerPerDay = triggerPerDay
      this.maxCount = maxCount
    }
  
  }
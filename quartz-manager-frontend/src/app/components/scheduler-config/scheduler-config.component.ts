import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { SchedulerService } from '../../services';
import { SchedulerConfig } from '../../model/schedulerConfig.model'
import {Scheduler} from '../../model/scheduler.model';

@Component({
  selector: 'qrzmng-scheduler-config',
  templateUrl: './scheduler-config.component.html',
  styleUrls: ['./scheduler-config.component.scss']
})
export class SchedulerConfigComponent implements OnInit {

  config: SchedulerConfig = new SchedulerConfig()
  configBackup: SchedulerConfig = new SchedulerConfig()
  scheduler: Scheduler;

  triggerLoading = true;
  enabledTriggerForm = false;
  private fetchedTriggers = false;
  private triggerInProgress = false;

  constructor(
    private schedulerService: SchedulerService
  ) { }

  ngOnInit() {
    this.triggerLoading = true;
    this._getScheduler();
    this.retrieveConfig();
  }

  retrieveConfig = () => {
    this.schedulerService.getConfig()
      .subscribe(res => {
        this.config = new SchedulerConfig(res.triggerPerDay, res.maxCount, res.timesTriggered)
        this.configBackup = res
        this.triggerLoading = false;
        this.triggerInProgress = res.timesTriggered < res.maxCount;
      })
  }

  private _getScheduler() {
    this.schedulerService.getScheduler()
      .subscribe( res => {
        this.scheduler = <Scheduler>res;
        this.fetchedTriggers = this.scheduler.triggerKeys.length > 0
      })
  }

  shouldShowTriggerConfig = (): boolean => this.fetchedTriggers && this.triggerInProgress;

  submitConfig = () => {
    this.schedulerService.updateConfig(this.config)
      .subscribe(res => {
        this.configBackup = this.config;
        this.enabledTriggerForm = false;
        this.fetchedTriggers = true;
        this.triggerInProgress = true;
      }, error => {
        this.config = this.configBackup;
      });
  };

  enableTriggerForm = () =>    this.enabledTriggerForm = true;
}

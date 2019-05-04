import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { SchedulerService } from '../../services';
import { SchedulerConfig } from '../../model/schedulerConfig.model'

@Component({
  selector: 'scheduler-config',
  templateUrl: './scheduler-config.component.html',
  styleUrls: ['./scheduler-config.component.scss']
})
export class SchedulerConfigComponent implements OnInit {

  constructor(
    private schedulerService: SchedulerService
  ) { }

  config : SchedulerConfig = new SchedulerConfig()
  configBackup : SchedulerConfig = new SchedulerConfig()

  ngOnInit() {
    this.retrieveConfig()
  }

  retrieveConfig = () => {
    this.schedulerService.getConfig()
      .subscribe(res => {
        this.config = new SchedulerConfig(res.triggerPerDay, res.maxCount)
        this.configBackup = res.maxCount
      })
  }

  submitConfig = () => {
    this.schedulerService.updateConfig(this.config)
      .subscribe(res => {
        this.configBackup = this.config;
      }, error => {
        this.config = this.configBackup;
      });
  };

}

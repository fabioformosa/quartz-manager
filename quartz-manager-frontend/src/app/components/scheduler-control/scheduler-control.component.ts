import {Component, OnInit} from '@angular/core';
import {SchedulerService, UserService} from '../../services';
import {Scheduler} from '../../model/scheduler.model';

@Component({
  selector: 'qrzmng-scheduler-control',
  templateUrl: './scheduler-control.component.html',
  styleUrls: ['./scheduler-control.component.scss']
})
export class SchedulerControlComponent implements OnInit {

  scheduler: Scheduler;

  constructor(
    private userService: UserService,
    private schedulerService: SchedulerService
  ) {
  }

  ngOnInit() {
    this._getScheduler();
  }

  private _getScheduler() {
    this.schedulerService.getScheduler()
      .subscribe(resp => this.scheduler = resp);
  }

  startScheduler = function () {
    this.schedulerService.startScheduler().subscribe((res) => {
      this.scheduler.status = 'RUNNING'
    }, (res) => {
      console.log(JSON.stringify(res))
    });
  };

  stopScheduler = function () {
    this.schedulerService.stopScheduler().subscribe((res) => {
      this.scheduler.status = 'STOPPED'
    }, (res) => {
      console.log(JSON.stringify(res))
    });
  };

  pauseScheduler = function () {
    this.schedulerService.pauseScheduler().subscribe((res) => {
      this.scheduler.status = 'PAUSED'
    }, (res) => {
      console.log(JSON.stringify(res))
    });
  };

  resumeScheduler = function () {
    this.schedulerService.resumeScheduler().subscribe((res) => {
      this.scheduler.status = 'RUNNING'
    }, (res) => {
      console.log(JSON.stringify(res))
    });
  };

  stop = function () {
    if (this.scheduler.status !== 'STOPPED') {
      this.stopScheduler();
    }
  }

  startOrPause = function () {
    switch (this.scheduler.status) {
      case 'RUNNING':
        this.pauseScheduler();
        break;
      case 'PAUSED':
        this.resumeScheduler();
        break;
      default:
        this.startScheduler();
        break;
    }
  };

}

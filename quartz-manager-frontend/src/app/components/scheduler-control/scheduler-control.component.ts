import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { UserService, SchedulerService } from '../../services';

@Component({
  selector: 'scheduler-control',
  templateUrl: './scheduler-control.component.html',
  styleUrls: ['./scheduler-control.component.scss']
})
export class SchedulerControlComponent implements OnInit {

  schedulerState;
  
  constructor(
    private userService: UserService,
    private schedulerService: SchedulerService
  ) { }

  ngOnInit() {
    this.schedulerService.getStatus().subscribe(res => {this.schedulerState = res.data}, err => {console.log(err)});
  }

  startScheduler = function(){
    this.schedulerService.startScheduler().subscribe((res) => {this.schedulerState = 'running'}, (res) => {console.log(JSON.stringify(res))});
  };

  stopScheduler = function(){
    this.schedulerService.stopScheduler().subscribe((res) => {this.schedulerState = 'stopped'}, (res) => {console.log(JSON.stringify(res))});
  };

  pauseScheduler = function(){
    this.schedulerService.pauseScheduler().subscribe((res) => {this.schedulerState = 'paused'}, (res) => {console.log(JSON.stringify(res))});
  };

  resumeScheduler = function(){
    this.schedulerService.resumeScheduler().subscribe((res) => {this.schedulerState = 'running'}, (res) => {console.log(JSON.stringify(res))});
  };

  stop = function(){  
    if(this.schedulerState != 'stopped')
      this.stopScheduler();
  }

  startOrPause = function(){
    switch (this.schedulerState) {
    case 'running':	this.pauseScheduler();
            break;
    case 'paused':	this.resumeScheduler();
            break;
    default:
      this.startScheduler();
      break;
    }
  };

}

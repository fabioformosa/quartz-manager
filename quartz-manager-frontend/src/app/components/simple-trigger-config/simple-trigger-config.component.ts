import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {JobService, SchedulerService} from '../../services';
import {Scheduler} from '../../model/scheduler.model';
import {SimpleTriggerCommand} from '../../model/simple-trigger.command';
import {SimpleTrigger} from '../../model/simple-trigger.model';
import {SimpleTriggerForm} from '../../model/simple-trigger.form';
import * as moment from 'moment';
import {TriggerKey} from '../../model/triggerKey.model';

@Component({
  selector: 'qrzmng-simple-trigger-config',
  templateUrl: './simple-trigger-config.component.html',
  styleUrls: ['./simple-trigger-config.component.scss']
})
export class SimpleTriggerConfigComponent implements OnInit {

  simpleTriggerForm: SimpleTriggerForm = new SimpleTriggerForm();
  formBackup: SimpleTriggerForm = new SimpleTriggerForm();

  trigger: SimpleTrigger;
  scheduler: Scheduler;

  triggerLoading = true;

  private fetchedTriggers = false;
  private triggerInProgress = false;

  private selectedTriggerKey: TriggerKey;

  private jobs: Array<String>;

  enabledTriggerForm = false;

  @Output()
  onNewTrigger = new EventEmitter<SimpleTrigger>();

  constructor(
    private schedulerService: SchedulerService,
    private jobService: JobService
  ) { }

  ngOnInit() {
    this.fetchJobs();
  }

  private fetchJobs() {
    this.jobService.fetchJobs().subscribe(jobs => this.jobs = jobs);
  }

  openTriggerForm() {
      this.enabledTriggerForm = true;
  }

  private closeTriggerForm() {
    this.enabledTriggerForm = false;
  }

  @Input()
  set triggerKey(triggerKey: TriggerKey){
    this.selectedTriggerKey = {...triggerKey} as TriggerKey;
    this.fetchSelectedTrigger();
  }


  fetchSelectedTrigger = () => {
    this.triggerLoading = true;
    this.schedulerService.getSimpleTriggerConfig(this.selectedTriggerKey.name)
      .subscribe((retTrigger: SimpleTrigger) => {
          this.trigger = retTrigger;
          this.formBackup = this.simpleTriggerForm;
          this.simpleTriggerForm = this._fromTriggerToForm(retTrigger);
          this.triggerLoading = false;
          this.triggerInProgress = this.trigger.mayFireAgain;
      })
  }

  shouldShowTheTriggerCardContent = (): boolean => this.trigger !== null || this.enabledTriggerForm;

  existsATriggerInProgress = (): boolean => this.trigger && this.triggerInProgress;

  cancelConfigForm = () => this.enabledTriggerForm = false;

  submitConfig = () => {
    const schedulerServiceCall = this.existsATriggerInProgress() ?
      this.schedulerService.updateSimpleTriggerConfig : this.schedulerService.saveSimpleTriggerConfig;

    const simpleTriggerCommand = this._fromFormToCommand(this.simpleTriggerForm);
    schedulerServiceCall(simpleTriggerCommand)
      .subscribe((retTrigger: SimpleTrigger) => {
        this.trigger = retTrigger;
        this.formBackup = this.simpleTriggerForm;
        this.simpleTriggerForm = this._fromTriggerToForm(retTrigger);
        this.fetchedTriggers = true;
        this.triggerInProgress = this.trigger.mayFireAgain;

        if (schedulerServiceCall === this.schedulerService.saveSimpleTriggerConfig) {
          this.onNewTrigger.emit(retTrigger);
        }

        this.closeTriggerForm();
      }, error => {
        this.simpleTriggerForm = this.formBackup;
      });
  };

  private _fromTriggerToForm = (simpleTrigger: SimpleTrigger): SimpleTriggerForm => {
    const command = new SimpleTriggerForm();
    command.triggerName = simpleTrigger.triggerKeyDTO.name;
    command.jobClass = simpleTrigger.jobDetailDTO.jobClassName;
    command.repeatCount = simpleTrigger.repeatCount;
    command.repeatInterval = simpleTrigger.repeatInterval;
    command.startDate = moment(simpleTrigger.startTime);
    command.endDate = moment(simpleTrigger.endTime);
    return command;
  }

  private _fromFormToCommand = (simpleTriggerForm: SimpleTriggerForm): SimpleTriggerCommand => {
    const simpleTriggerCommand = new SimpleTriggerCommand();
    simpleTriggerCommand.triggerName = simpleTriggerForm.triggerName;
    simpleTriggerCommand.jobClass = simpleTriggerForm.jobClass;
    simpleTriggerCommand.repeatCount = simpleTriggerForm.repeatCount;
    simpleTriggerCommand.repeatInterval = simpleTriggerForm.repeatInterval;
    simpleTriggerCommand.startDate = simpleTriggerForm.startDate?.toDate();
    simpleTriggerCommand.endDate = simpleTriggerForm.endDate?.toDate();
    return simpleTriggerCommand;
  }

}

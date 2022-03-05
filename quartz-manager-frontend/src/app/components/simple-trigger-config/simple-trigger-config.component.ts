import {Component, OnInit} from '@angular/core';
import {SchedulerService} from '../../services';
import {Scheduler} from '../../model/scheduler.model';
import {SimpleTriggerCommand} from '../../model/simple-trigger.command';
import {SimpleTrigger} from '../../model/simple-trigger.model';
import {SimpleTriggerForm} from '../../model/simple-trigger.form';
import * as moment from 'moment';

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
  enabledTriggerForm = false;
  private fetchedTriggers = false;
  private triggerInProgress = false;

  constructor(
    private schedulerService: SchedulerService
  ) { }

  ngOnInit() {
    this.triggerLoading = true;
    this.retrieveConfiguredTriggerIfExists();
  }

  retrieveConfiguredTriggerIfExists = () => {
    this.schedulerService.getSimpleTriggerConfig()
      .subscribe((retTrigger: SimpleTrigger) => {
          this.trigger = retTrigger;
          this.formBackup = this.simpleTriggerForm;
          this.simpleTriggerForm = this._fromTriggerToForm(retTrigger);

          this.triggerLoading = false;
          this.triggerInProgress = this.trigger.mayFireAgain;
      })
  }

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
        this.enabledTriggerForm = false;
        this.fetchedTriggers = true;
        this.triggerInProgress = this.trigger.mayFireAgain;
      }, error => {
        this.simpleTriggerForm = this.formBackup;
      });
  };

  enableTriggerForm = () => this.enabledTriggerForm = true;

  private _fromTriggerToCommand = (simpleTrigger: SimpleTrigger) => {
    const command = new SimpleTriggerCommand();
    command.repeatCount = simpleTrigger.repeatCount;
    command.repeatInterval = simpleTrigger.repeatInterval;
    command.startDate = simpleTrigger.startTime;
    command.endDate = simpleTrigger.endTime;
    return command;
  }

  private _fromTriggerToForm = (simpleTrigger: SimpleTrigger): SimpleTriggerForm => {
    const command = new SimpleTriggerForm();
    command.repeatCount = simpleTrigger.repeatCount;
    command.repeatInterval = simpleTrigger.repeatInterval;
    command.startDate = moment(simpleTrigger.startTime);
    command.endDate = moment(simpleTrigger.endTime);
    return command;
  }

  private _fromFormToCommand = (simpleTriggerForm: SimpleTriggerForm): SimpleTriggerCommand => {
    const simpleTriggerCommand = new SimpleTriggerCommand();
    simpleTriggerCommand.repeatCount = simpleTriggerForm.repeatCount;
    simpleTriggerCommand.repeatInterval = simpleTriggerForm.repeatInterval;
    simpleTriggerCommand.startDate = simpleTriggerForm.startDate.toDate();
    simpleTriggerCommand.endDate = simpleTriggerForm.endDate.toDate();
    return simpleTriggerCommand;
  }

}

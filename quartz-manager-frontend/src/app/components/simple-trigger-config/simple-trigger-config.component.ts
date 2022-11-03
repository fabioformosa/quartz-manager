import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {SchedulerService} from '../../services';
import {Scheduler} from '../../model/scheduler.model';
import {SimpleTriggerCommand} from '../../model/simple-trigger.command';
import {SimpleTrigger} from '../../model/simple-trigger.model';
import {SimpleTriggerForm} from '../../model/simple-trigger.form';
import * as moment from 'moment';
import {TriggerKey} from '../../model/triggerKey.model';
import JobService from '../../services/job.service';
import {MisfireInstruction, MisfireInstructionCaption} from '../../model/misfire-instruction.model';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'qrzmng-simple-trigger-config',
  templateUrl: './simple-trigger-config.component.html',
  styleUrls: ['./simple-trigger-config.component.scss']
})
export class SimpleTriggerConfigComponent implements OnInit {

  trigger: SimpleTrigger;

  simpleTriggerReactiveForm: FormGroup = this.formBuilder.group({
    triggerName: [this.trigger?.triggerKeyDTO.name, Validators.required],
    jobClass: [this.trigger?.jobDetailDTO.jobClassName, Validators.required],
    triggerPeriod: this.formBuilder.group({
      startDate: [this.trigger?.startTime && moment(this.trigger?.startTime)],
      endDate: [this.trigger?.endTime && moment(this.trigger?.endTime)]
    }),
    triggerRecurrence: this.formBuilder.group({
      repeatCount: [this.trigger?.repeatCount],
      repeatInterval: [this.trigger?.repeatInterval]
    }),
    misfireInstruction: [MisfireInstruction[this.trigger?.misfireInstruction], Validators.required]
  });

  // simpleTriggerForm: SimpleTriggerForm = new SimpleTriggerForm();
  // formBackup: SimpleTriggerForm = new SimpleTriggerForm();

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
    private formBuilder: FormBuilder,
    private schedulerService: SchedulerService,
    private jobService: JobService
  ) {
  }

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
  set triggerKey(triggerKey: TriggerKey) {
    this.selectedTriggerKey = {...triggerKey} as TriggerKey;
    this.fetchSelectedTrigger();
  }


  fetchSelectedTrigger = () => {
    this.triggerLoading = true;
    this.schedulerService.getSimpleTriggerConfig(this.selectedTriggerKey.name)
      .subscribe((retTrigger: SimpleTrigger) => {
        this.trigger = retTrigger;
        this.simpleTriggerReactiveForm.setValue(this._fromTriggerToReactiveForm(retTrigger))
        // this.formBackup = this.simpleTriggerForm;
        // this.simpleTriggerForm = this._fromTriggerToForm(retTrigger);
        this.triggerLoading = false;
        this.triggerInProgress = this.trigger.mayFireAgain;
      })
  }

  shouldShowTheTriggerCardContent = (): boolean => this.trigger !== null || this.enabledTriggerForm;

  existsATriggerInProgress = (): boolean => this.trigger && this.triggerInProgress;

  onResetReactiveForm = () => {
    this.simpleTriggerReactiveForm.setValue(this._fromTriggerToReactiveForm(this.trigger));
    this.closeTriggerForm();
  };

  onSubmitTriggerConfig = () => {
    const schedulerServiceCall = this.existsATriggerInProgress() ?
      this.schedulerService.updateSimpleTriggerConfig : this.schedulerService.saveSimpleTriggerConfig;

    const simpleTriggerCommand = this._fromReactiveFormToCommand();
    schedulerServiceCall(simpleTriggerCommand)
      .subscribe((retTrigger: SimpleTrigger) => {
        this.trigger = retTrigger;

        this.simpleTriggerReactiveForm.setValue(this._fromTriggerToReactiveForm(retTrigger));

        this.fetchedTriggers = true;
        this.triggerInProgress = this.trigger.mayFireAgain;

        if (schedulerServiceCall === this.schedulerService.saveSimpleTriggerConfig) {
          this.onNewTrigger.emit(retTrigger);
        }

        this.closeTriggerForm();
      }, error => {
        this.simpleTriggerReactiveForm.setValue(this._fromTriggerToReactiveForm(this.trigger));
      });

  }

  private _fromTriggerToReactiveForm = (simpleTrigger: SimpleTrigger): SimpleTriggerReactiveForm => {
    const simpleTriggerReactiveForm = new SimpleTriggerReactiveForm();
    simpleTriggerReactiveForm.triggerName = simpleTrigger.triggerKeyDTO.name;
    simpleTriggerReactiveForm.jobClass = simpleTrigger.jobDetailDTO.jobClassName;
    simpleTriggerReactiveForm.triggerRecurrence.repeatCount = simpleTrigger.repeatCount || null;
    simpleTriggerReactiveForm.triggerRecurrence.repeatInterval = simpleTrigger.repeatInterval || null;
    simpleTriggerReactiveForm.triggerPeriod.startDate = (simpleTrigger.startTime && moment(simpleTrigger.startTime)) || null;
    simpleTriggerReactiveForm.triggerPeriod.endDate = (simpleTrigger.endTime && moment(simpleTrigger.endTime)) || null;
    simpleTriggerReactiveForm.misfireInstruction = (simpleTrigger.misfireInstruction && MisfireInstruction[simpleTrigger.misfireInstruction]) || null;
    return simpleTriggerReactiveForm;
  };

  private _fromTriggerToForm = (simpleTrigger: SimpleTrigger): SimpleTriggerForm => {
    const command = new SimpleTriggerForm();
    command.triggerName = simpleTrigger.triggerKeyDTO.name;
    command.jobClass = simpleTrigger.jobDetailDTO.jobClassName;
    command.repeatCount = simpleTrigger.repeatCount;
    command.repeatInterval = simpleTrigger.repeatInterval;
    command.startDate = moment(simpleTrigger.startTime);
    command.endDate = moment(simpleTrigger.endTime);
    command.misfireInstruction = MisfireInstruction[simpleTrigger.misfireInstruction];
    return command;
  }

  private _fromReactiveFormToCommand = (): SimpleTriggerCommand => {
    const reactiveFormValue = this.simpleTriggerReactiveForm.value;
    const simpleTriggerCommand = new SimpleTriggerCommand();
    simpleTriggerCommand.triggerName = reactiveFormValue.triggerName;
    simpleTriggerCommand.jobClass = reactiveFormValue.jobClass;
    simpleTriggerCommand.repeatCount = reactiveFormValue.triggerRecurrence.repeatCount;
    simpleTriggerCommand.repeatInterval = reactiveFormValue.triggerRecurrence.repeatInterval;
    simpleTriggerCommand.startDate = reactiveFormValue.triggerPeriod.startDate?.toDate();
    simpleTriggerCommand.endDate = reactiveFormValue.triggerPeriod.endDate?.toDate();
    simpleTriggerCommand.misfireInstruction = reactiveFormValue.misfireInstruction;
    return simpleTriggerCommand;
  }

  getMisfireInstructionCaption(): string {
    const misfireInstructionKey = this.simpleTriggerReactiveForm.controls.misfireInstruction.value as unknown as keyof typeof MisfireInstruction;
    return MisfireInstructionCaption.get(MisfireInstruction[misfireInstructionKey]);
  }
}

class SimpleTriggerReactiveForm {
  triggerName: string;
  jobClass: string;
  triggerPeriod: {
    startDate?;
    endDate?;
  } = {};
  triggerRecurrence: {
    repeatCount?: number;
    repeatInterval?: number;
  } = {};
  misfireInstruction: string;
}

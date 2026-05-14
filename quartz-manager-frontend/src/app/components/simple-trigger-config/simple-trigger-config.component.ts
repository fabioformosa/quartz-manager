import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {SchedulerService} from '../../services';
import {Scheduler} from '../../model/scheduler.model';
import {SimpleTriggerCommand} from '../../model/simple-trigger.command';
import {SimpleTrigger} from '../../model/simple-trigger.model';
import {TriggerKey} from '../../model/triggerKey.model';
import JobService from '../../services/job.service';
import {MisfireInstruction, MisfireInstructionCaption} from '../../model/misfire-instruction.model';
import {AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidationErrors, Validators} from '@angular/forms';

@Component({
    selector: 'qrzmng-simple-trigger-config',
    templateUrl: './simple-trigger-config.component.html',
    styleUrls: ['./simple-trigger-config.component.scss'],
    standalone: false
})
export class SimpleTriggerConfigComponent implements OnInit {

  trigger: SimpleTrigger = null;

  simpleTriggerReactiveForm: UntypedFormGroup = this.formBuilder.group({
    triggerName: [this.trigger?.triggerKeyDTO.name, Validators.required],
    jobClass: [this.trigger?.jobDetailDTO.jobClassName, Validators.required],
    triggerPeriod: this.formBuilder.group({
      startDate: [this.trigger?.startTime && new Date(this.trigger.startTime)],
      endDate: [this.trigger?.endTime && new Date(this.trigger.endTime)]
    }, {validators: this._triggerPeriodValidator}),
    triggerRecurrence: this.formBuilder.group({
      repeatCount: [this.trigger?.repeatCount],
      repeatInterval: [this.trigger?.repeatInterval]
    }, {validators: this._triggerRepetitionValidator}),
    misfireInstruction: [MisfireInstruction[this.trigger?.misfireInstruction], Validators.required]
  });

  scheduler: Scheduler;

  triggerLoading = false;

  private triggerInProgress = false;

  private selectedTriggerKey: TriggerKey;

  private jobs: Array<String>;

  @Output()
  onNewTrigger = new EventEmitter<SimpleTrigger>();

  @Output()
  triggerFormOpenChange = new EventEmitter<boolean>();

  @Output()
  onTriggerSubmitting = new EventEmitter<TriggerKey>();

  constructor(
    private formBuilder: UntypedFormBuilder,
    private schedulerService: SchedulerService,
    private jobService: JobService
  ) {
  }

  ngOnInit() {
    this.simpleTriggerReactiveForm.disable();
    this.fetchJobs();
  }

  private fetchJobs() {
    this.jobService.fetchJobs().subscribe(jobs => this.jobs = jobs);
  }

  openTriggerForm() {
    this.simpleTriggerReactiveForm.enable();
    this.triggerFormOpenChange.emit(true);
  }

  private closeTriggerForm() {
    this.simpleTriggerReactiveForm.disable();
    this.triggerFormOpenChange.emit(false);
  }

  @Input()
  set triggerKey(triggerKey: TriggerKey) {
    if (!triggerKey) {
      return;
    } else if (!this.selectedTriggerKey || this.selectedTriggerKey.name !== triggerKey.name) {
      this._resetTheTrigger();
      this.selectedTriggerKey = {...triggerKey} as TriggerKey;
      this.fetchSelectedTrigger();
      this.simpleTriggerReactiveForm.disable();
    }
  }

  openNewTriggerForm() {
    this._resetTheTrigger();
    this.openTriggerForm();
  }

  private _resetTheTrigger() {
    this.trigger = null;
    this.triggerInProgress = false;
    this.selectedTriggerKey = null;
    this.simpleTriggerReactiveForm.reset(new SimpleTriggerReactiveForm());
  }

  fetchSelectedTrigger = () => {
    this.triggerLoading = true;
    this.schedulerService.getSimpleTriggerConfig(this.selectedTriggerKey.name)
      .subscribe((retTrigger: SimpleTrigger) => {
        this.trigger = retTrigger;
        this.simpleTriggerReactiveForm.setValue(this._fromTriggerToReactiveForm(retTrigger))
        this.triggerLoading = false;
        this.triggerInProgress = this.trigger.mayFireAgain;
        this.simpleTriggerReactiveForm.disable();
      })
  }

  shouldShowTheTriggerCardContent = (): boolean => this.trigger !== null || this.simpleTriggerReactiveForm.enabled;

  existsATriggerInProgress = (): boolean => this.trigger && this.triggerInProgress;

  onResetReactiveForm = () => {
    if (this.trigger) {
      this.simpleTriggerReactiveForm.setValue(this._fromTriggerToReactiveForm(this.trigger));
    } else {
      this.simpleTriggerReactiveForm.reset(new SimpleTriggerReactiveForm());
    }
    this.closeTriggerForm();
  };

  onSubmitTriggerConfig = () => {
    const schedulerServiceCall = this.existsATriggerInProgress() ?
      this.schedulerService.updateSimpleTriggerConfig : this.schedulerService.saveSimpleTriggerConfig;

    const simpleTriggerCommand = this._fromReactiveFormToCommand();
    if (!this.trigger) {
      this.onTriggerSubmitting.emit(new TriggerKey(simpleTriggerCommand.triggerName, null));
      setTimeout(() => this.submitTriggerConfig(schedulerServiceCall, simpleTriggerCommand));
      return;
    }

    this.submitTriggerConfig(schedulerServiceCall, simpleTriggerCommand);

  }

  private submitTriggerConfig(schedulerServiceCall, simpleTriggerCommand: SimpleTriggerCommand) {
    this.triggerLoading = true;
    schedulerServiceCall(simpleTriggerCommand)
      .subscribe((retTrigger: SimpleTrigger) => {
        this.trigger = retTrigger;

        this.simpleTriggerReactiveForm.setValue(this._fromTriggerToReactiveForm(retTrigger));

        this.triggerInProgress = this.trigger.mayFireAgain;

        if (schedulerServiceCall === this.schedulerService.saveSimpleTriggerConfig) {
          this.onNewTrigger.emit(retTrigger);
        }

        this.closeTriggerForm();
      }, error => {
        if (this.trigger) {
          this.simpleTriggerReactiveForm.setValue(this._fromTriggerToReactiveForm(this.trigger));
        }
        this.triggerLoading = false;
      }, () => {
        this.triggerLoading = false;
      });
  }

  private _triggerPeriodValidator(control: AbstractControl): ValidationErrors | null {
    const startDate = control.get('startDate');
    const endDate = control.get('endDate');
    if (startDate.value && endDate.value) {
      return endDate.value < startDate.value ?
        <ValidationErrors>{invalidTriggerPeriod: true} : null;
    }
    return null;
  }

  private _triggerRepetitionValidator(control: AbstractControl): ValidationErrors | null {
    const repeatInterval = control.get('repeatInterval');
    const repeatCount = control.get('repeatCount');
    if ((repeatCount.value && repeatInterval.value) || (!repeatCount.value && !repeatInterval.value)) {
      repeatInterval.setErrors(null);
      repeatCount.setErrors(null);
      return null;
    }
    const errors = <ValidationErrors>{invalidTriggerRecurrence: true};
    repeatInterval.setErrors(errors);
    repeatCount.setErrors(errors);
    return errors;
  }

  private _fromTriggerToReactiveForm = (simpleTrigger: SimpleTrigger): SimpleTriggerReactiveForm => {
    const simpleTriggerReactiveForm = new SimpleTriggerReactiveForm();
    simpleTriggerReactiveForm.triggerName = simpleTrigger.triggerKeyDTO.name;
    simpleTriggerReactiveForm.jobClass = simpleTrigger.jobDetailDTO.jobClassName;
    simpleTriggerReactiveForm.triggerRecurrence.repeatCount = simpleTrigger.repeatCount || null;
    simpleTriggerReactiveForm.triggerRecurrence.repeatInterval = simpleTrigger.repeatInterval || null;
    simpleTriggerReactiveForm.triggerPeriod.startDate = (simpleTrigger.startTime && new Date(simpleTrigger.startTime)) || null;
    simpleTriggerReactiveForm.triggerPeriod.endDate = (simpleTrigger.endTime && new Date(simpleTrigger.endTime)) || null;
    simpleTriggerReactiveForm.misfireInstruction = (simpleTrigger.misfireInstruction
      && MisfireInstruction[simpleTrigger.misfireInstruction]) || null;
    return simpleTriggerReactiveForm;
  };

  private _fromReactiveFormToCommand = (): SimpleTriggerCommand => {
    const reactiveFormValue = this.simpleTriggerReactiveForm.getRawValue();
    const simpleTriggerCommand = new SimpleTriggerCommand();
    simpleTriggerCommand.triggerName = reactiveFormValue.triggerName;
    simpleTriggerCommand.triggerGroup = this.selectedTriggerKey?.group || 'DEFAULT';
    simpleTriggerCommand.jobClass = reactiveFormValue.jobClass;
    simpleTriggerCommand.repeatCount = reactiveFormValue.triggerRecurrence.repeatCount;
    simpleTriggerCommand.repeatInterval = reactiveFormValue.triggerRecurrence.repeatInterval;
    simpleTriggerCommand.startDate = reactiveFormValue.triggerPeriod.startDate;
    simpleTriggerCommand.endDate = reactiveFormValue.triggerPeriod.endDate;
    simpleTriggerCommand.misfireInstruction = reactiveFormValue.misfireInstruction;
    return simpleTriggerCommand;
  }

  getMisfireInstructionCaption(): string {
    const misfireInstructionKey = this.simpleTriggerReactiveForm.controls
      .misfireInstruction.value as unknown as keyof typeof MisfireInstruction;
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

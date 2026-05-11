import {Component, NgZone, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {map} from 'rxjs/operators';

import {SchedulerService, TriggerService} from '../../services';
import JobService from '../../services/job.service';
import {LogsRxWebsocketService} from '../../services/logs.rx-websocket.service';
import {ProgressRxWebsocketService} from '../../services/progress.rx-websocket.service';
import {Scheduler} from '../../model/scheduler.model';
import {SimpleTriggerCommand} from '../../model/simple-trigger.command';
import {SimpleTrigger} from '../../model/simple-trigger.model';
import {TriggerKey} from '../../model/triggerKey.model';
import TriggerFiredBundle from '../../model/trigger-fired-bundle.model';

type ConsolePage = 'dashboard' | 'jobs' | 'triggers' | 'calendars' | 'executions' | 'events' | 'scheduler';
type WizardMode = 'create' | 'edit';

interface ConsoleLogRecord {
  time: Date;
  severity: string;
  type: string;
  source: string;
  message: string;
}

interface TriggerDraft {
  triggerName: string;
  group: string;
  jobClass: string;
  startDate: string;
  endDate: string;
  repeatIntervalAmount: number;
  repeatIntervalUnit: string;
  repeatCount: number;
  misfireInstruction: string;
}

@Component({
    selector: 'manager',
    templateUrl: './manager.component.html',
    styleUrls: ['./manager.component.scss'],
    standalone: false
})
export class ManagerComponent implements OnInit, OnDestroy {

  readonly roadmapMessage = 'This feature is not supported by the current backend yet. '
    + 'It is tracked in the Quartz Manager roadmap and will come with a future release.';

  activePage: ConsolePage = 'dashboard';
  scheduler: Scheduler;
  schedulerLoading = false;
  triggerKeys: TriggerKey[] = [];
  triggerDetailsByName: {[triggerName: string]: SimpleTrigger} = {};
  selectedTriggerKey: TriggerKey;
  selectedTrigger: SimpleTrigger;
  selectedJobClass: string;
  jobs: string[] = [];
  logs: ConsoleLogRecord[] = [];
  progress: TriggerFiredBundle;
  roadmapNotice: string;
  operationNotice: string;
  operationError: string;
  triggerLoading = false;
  wizardMode: WizardMode = 'create';
  wizardOpen = false;
  detailDrawerOpen = false;
  wizardSubmitting = false;
  wizardError: string;
  triggerDraft: TriggerDraft = this.buildEmptyDraft();

  private readonly roadmapPages = new Set<ConsolePage>(['calendars', 'executions']);
  private readonly subscriptions: Subscription[] = [];
  private logsSubscription: Subscription;
  private progressSubscription: Subscription;
  private noticeTimer: ReturnType<typeof setTimeout>;

  constructor(
    private schedulerService: SchedulerService,
    private triggerService: TriggerService,
    private jobService: JobService,
    private logsRxWebsocketService: LogsRxWebsocketService,
    private progressRxWebsocketService: ProgressRxWebsocketService,
    private ngZone: NgZone
  ) {}

  ngOnInit() {
    this.refreshScheduler();
    this.fetchTriggers();
    this.fetchJobs();
  }

  ngOnDestroy() {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.unsubscribeFromTriggerTopics();
    if (this.noticeTimer) {
      clearTimeout(this.noticeTimer);
    }
  }

  selectPage(page: ConsolePage) {
    this.activePage = page;
    this.closeDrawers();
    if (this.roadmapPages.has(page)) {
      this.showRoadmapNotice(`${this.getPageTitle(page)} is on the Quartz Manager roadmap`);
    }
  }

  jumpToScheduler() {
    this.selectPage('scheduler');
  }

  handleConsoleClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    const roadmapElement = target.closest('[data-roadmap]') as HTMLElement;
    if (!roadmapElement) {
      return;
    }
    event.preventDefault();
    event.stopPropagation();
    this.showRoadmapNotice(roadmapElement.getAttribute('data-roadmap'));
  }

  showRoadmapNotice(feature?: string) {
    this.operationError = null;
    this.operationNotice = null;
    this.roadmapNotice = feature ? `${feature}. ${this.roadmapMessage}` : this.roadmapMessage;
    if (this.noticeTimer) {
      clearTimeout(this.noticeTimer);
    }
    this.noticeTimer = setTimeout(() => this.roadmapNotice = null, 8000);
  }

  dismissNotice() {
    this.roadmapNotice = null;
    this.operationNotice = null;
    this.operationError = null;
    this.wizardError = null;
  }

  openDetailDrawer() {
    this.detailDrawerOpen = true;
    this.wizardOpen = false;
  }

  closeDetailDrawer() {
    this.detailDrawerOpen = false;
  }

  closeWizardDrawer() {
    this.wizardOpen = false;
  }

  closeDrawers() {
    this.detailDrawerOpen = false;
    this.wizardOpen = false;
  }

  refreshScheduler() {
    this.schedulerLoading = true;
    const subscription = this.schedulerService.getScheduler().subscribe({
      next: scheduler => {
        this.scheduler = scheduler;
        this.schedulerLoading = false;
      },
      error: () => {
        this.schedulerLoading = false;
        this.operationError = 'Unable to load scheduler metadata.';
      }
    });
    this.subscriptions.push(subscription);
  }

  startScheduler() {
    const subscription = this.schedulerService.startScheduler().subscribe({
      next: () => this.setSchedulerStatus('RUNNING', 'Scheduler started.'),
      error: () => this.operationError = 'Unable to start the scheduler.'
    });
    this.subscriptions.push(subscription);
  }

  standbyScheduler() {
    const subscription = this.schedulerService.pauseScheduler().subscribe({
      next: () => this.setSchedulerStatus('PAUSED', 'Scheduler moved to standby.'),
      error: () => this.operationError = 'Unable to move the scheduler to standby.'
    });
    this.subscriptions.push(subscription);
  }

  resumeScheduler() {
    const subscription = this.schedulerService.resumeScheduler().subscribe({
      next: () => this.setSchedulerStatus('RUNNING', 'Scheduler resumed.'),
      error: () => this.operationError = 'Unable to resume the scheduler.'
    });
    this.subscriptions.push(subscription);
  }

  shutdownScheduler() {
    if (!window.confirm('Shutdown the scheduler instance?')) {
      return;
    }
    const subscription = this.schedulerService.stopScheduler().subscribe({
      next: () => this.setSchedulerStatus('STOPPED', 'Scheduler shut down.'),
      error: () => this.operationError = 'Unable to shut down the scheduler.'
    });
    this.subscriptions.push(subscription);
  }

  toggleStandby() {
    if (this.scheduler?.status === 'PAUSED') {
      this.resumeScheduler();
      return;
    }
    this.standbyScheduler();
  }

  fetchTriggers() {
    const subscription = this.triggerService.fetchTriggers().subscribe({
      next: triggerKeys => {
        this.triggerKeys = triggerKeys || [];
        this.fetchTriggerDetails(this.triggerKeys);
        if (this.triggerKeys.length > 0) {
          this.selectTrigger(this.selectedTriggerKey || this.triggerKeys[0], false);
        } else {
          this.resetWizard();
        }
      },
      error: () => this.operationError = 'Unable to load triggers.'
    });
    this.subscriptions.push(subscription);
  }

  fetchJobs() {
    const subscription = this.jobService.fetchJobs().subscribe({
      next: jobs => {
        this.jobs = jobs || [];
        this.selectedJobClass = this.jobs[0];
        if (!this.triggerDraft.jobClass && this.jobs.length > 0) {
          this.triggerDraft.jobClass = this.jobs[0];
        }
      },
      error: () => this.operationError = 'Unable to load eligible job classes.'
    });
    this.subscriptions.push(subscription);
  }

  fetchTriggerDetails(triggerKeys: TriggerKey[]) {
    triggerKeys.forEach(triggerKey => {
      const subscription = this.schedulerService.getSimpleTriggerConfig(triggerKey.name).subscribe({
        next: trigger => this.triggerDetailsByName[triggerKey.name] = trigger as SimpleTrigger,
        error: () => {
          this.triggerDetailsByName[triggerKey.name] = null;
        }
      });
      this.subscriptions.push(subscription);
    });
  }

  selectTrigger(triggerKey: TriggerKey, openDrawer = true) {
    if (!triggerKey?.name) {
      return;
    }
    this.selectedTriggerKey = {...triggerKey};
    if (openDrawer) {
      this.openDetailDrawer();
    }
    this.triggerLoading = true;
    this.selectedTrigger = this.triggerDetailsByName[triggerKey.name] || null;
    this.subscribeToTriggerTopics(this.selectedTriggerKey);
    const subscription = this.schedulerService.getSimpleTriggerConfig(triggerKey.name).subscribe({
      next: trigger => {
        this.selectedTrigger = trigger as SimpleTrigger;
        this.triggerDetailsByName[triggerKey.name] = trigger as SimpleTrigger;
        this.triggerLoading = false;
      },
      error: () => {
        this.triggerLoading = false;
        this.showRoadmapNotice('Only SimpleTrigger details are supported by the current backend');
      }
    });
    this.subscriptions.push(subscription);
  }

  selectJob(jobClass: string) {
    this.selectedJobClass = jobClass;
    this.openDetailDrawer();
  }

  openCreateTriggerWizard() {
    this.resetWizard();
    this.wizardOpen = true;
    this.detailDrawerOpen = false;
    this.selectPage('dashboard');
    this.wizardOpen = true;
  }

  openRescheduleWizard(triggerKey?: TriggerKey) {
    if (triggerKey) {
      this.selectTrigger(triggerKey, false);
    }
    if (!this.selectedTrigger && !this.selectedTriggerKey) {
      this.showRoadmapNotice('Reschedule requires a SimpleTrigger loaded from the backend');
      return;
    }

    const trigger = this.selectedTrigger || this.triggerDetailsByName[this.selectedTriggerKey.name];
    const repeatInterval = this.splitRepeatInterval(trigger?.repeatInterval || 60000);
    this.wizardMode = 'edit';
    this.wizardOpen = true;
    this.detailDrawerOpen = false;
    this.triggerDraft = {
      triggerName: this.selectedTriggerKey.name,
      group: this.selectedTriggerKey.group || 'DEFAULT',
      jobClass: trigger?.jobDetailDTO?.jobClassName || this.jobs[0] || '',
      startDate: this.toDatetimeLocalValue(trigger?.startTime),
      endDate: this.toDatetimeLocalValue(trigger?.endTime),
      repeatIntervalAmount: repeatInterval.amount,
      repeatIntervalUnit: repeatInterval.unit,
      repeatCount: trigger?.repeatCount ?? -1,
      misfireInstruction: this.getMisfireInstructionName(trigger?.misfireInstruction)
    };
    this.selectPage('dashboard');
    this.wizardOpen = true;
  }

  resetWizard() {
    this.wizardMode = 'create';
    this.wizardError = null;
    this.triggerDraft = this.buildEmptyDraft();
  }

  submitTriggerWizard() {
    this.wizardError = null;
    if (!this.canSubmitTrigger()) {
      this.wizardError = 'Trigger name, job class, misfire policy, and both repeat fields are required for the current backend.';
      return;
    }

    const command = new SimpleTriggerCommand();
    command.triggerName = this.triggerDraft.triggerName.trim();
    command.jobClass = this.triggerDraft.jobClass;
    command.startDate = this.fromDatetimeLocalValue(this.triggerDraft.startDate);
    command.endDate = this.fromDatetimeLocalValue(this.triggerDraft.endDate);
    command.repeatInterval = this.getRepeatIntervalMs();
    command.repeatCount = this.triggerDraft.repeatCount;
    command.misfireInstruction = this.triggerDraft.misfireInstruction;

    this.wizardSubmitting = true;
    const request = this.wizardMode === 'edit'
      ? this.schedulerService.updateSimpleTriggerConfig(command)
      : this.schedulerService.saveSimpleTriggerConfig(command);

    const subscription = request.subscribe({
      next: trigger => {
        this.wizardSubmitting = false;
        this.triggerDetailsByName[trigger.triggerKeyDTO.name] = trigger as SimpleTrigger;
        this.upsertTriggerKey(trigger.triggerKeyDTO);
        this.selectTrigger(trigger.triggerKeyDTO);
        this.wizardOpen = false;
        this.detailDrawerOpen = true;
        this.operationNotice = this.wizardMode === 'edit' ? 'SimpleTrigger rescheduled.' : 'SimpleTrigger created.';
        if (this.wizardMode === 'create') {
          this.resetWizard();
        }
      },
      error: () => {
        this.wizardSubmitting = false;
        this.wizardError = 'Unable to save the SimpleTrigger with the current backend.';
      }
    });
    this.subscriptions.push(subscription);
  }

  getSchedulerStatusClass(): string {
    switch (this.scheduler?.status) {
      case 'RUNNING': return 'running';
      case 'PAUSED': return 'paused';
      case 'STOPPED': return 'error';
      default: return '';
    }
  }

  getTriggerDetail(triggerKey: TriggerKey): SimpleTrigger {
    return triggerKey?.name ? this.triggerDetailsByName[triggerKey.name] : null;
  }

  getTriggerGroup(triggerKey: TriggerKey): string {
    return triggerKey?.group || 'DEFAULT';
  }

  getTriggerType(triggerKey: TriggerKey): string {
    return this.getTriggerDetail(triggerKey) ? 'SimpleTrigger' : 'SimpleTrigger';
  }

  getTriggerState(triggerKey: TriggerKey): string {
    const trigger = this.getTriggerDetail(triggerKey);
    if (!trigger) {
      return 'UNKNOWN';
    }
    if (!trigger.mayFireAgain) {
      return 'COMPLETE';
    }
    if (this.scheduler?.status === 'PAUSED') {
      return 'PAUSED';
    }
    return 'NORMAL';
  }

  getTriggerStateClass(triggerKey: TriggerKey): string {
    const state = this.getTriggerState(triggerKey);
    if (state === 'NORMAL') {
      return 'normal';
    }
    if (state === 'PAUSED') {
      return 'paused';
    }
    if (state === 'UNKNOWN') {
      return 'warn';
    }
    return '';
  }

  getTriggerJobName(triggerKey: TriggerKey): string {
    const trigger = this.getTriggerDetail(triggerKey);
    return trigger?.jobKeyDTO?.name || this.shortClassName(trigger?.jobDetailDTO?.jobClassName) || 'Roadmap';
  }

  getTriggerNextFireLabel(triggerKey: TriggerKey): string {
    const trigger = this.getTriggerDetail(triggerKey);
    return this.formatDateTime(trigger?.nextFireTime) || 'not available';
  }

  getTriggerPreviousFireLabel(triggerKey: TriggerKey): string {
    const trigger = this.getTriggerDetail(triggerKey);
    return this.formatDateTime(trigger?.['previousFireTime']) || 'not available';
  }

  getSelectedTriggerGroup(): string {
    return this.getTriggerGroup(this.selectedTriggerKey);
  }

  getSelectedTriggerState(): string {
    return this.selectedTriggerKey ? this.getTriggerState(this.selectedTriggerKey) : 'NONE';
  }

  getSelectedTriggerStateClass(): string {
    return this.selectedTriggerKey ? this.getTriggerStateClass(this.selectedTriggerKey) : '';
  }

  getSelectedJobName(): string {
    return this.selectedTriggerKey ? this.getTriggerJobName(this.selectedTriggerKey) : '-';
  }

  getSelectedTriggerRepeatSummary(): string {
    if (!this.selectedTrigger) {
      return 'not loaded';
    }
    const repeatInterval = this.selectedTrigger.repeatInterval;
    if (!repeatInterval) {
      return 'Run once';
    }
    return `Every ${this.formatDuration(repeatInterval)}`;
  }

  getProgressPercentage(): number {
    return this.progress?.percentage >= 0 ? this.progress.percentage : 0;
  }

  getProgressLabel(): string {
    if (!this.progress || this.progress.percentage < 0) {
      return 'Waiting for progress events';
    }
    return `${this.progress.percentage}% / ${this.progress.timesTriggered || 0} fired`;
  }

  getExecutionLoadValue(): string {
    return this.logs.length > 0 ? `${this.logs.length}` : '0';
  }

  getJobClassRows(): string[] {
    return this.jobs.length > 0 ? this.jobs : ['No eligible Quartz Manager job classes returned by the backend'];
  }

  getSelectedJobShortName(): string {
    return this.shortClassName(this.selectedJobClass) || '-';
  }

  getWizardTitle(): string {
    return this.wizardMode === 'edit' ? 'Reschedule SimpleTrigger' : 'Create SimpleTrigger';
  }

  getWizardCta(): string {
    return this.wizardMode === 'edit' ? 'Save Reschedule' : 'Create SimpleTrigger';
  }

  canSubmitTrigger(): boolean {
    return !!(
      this.triggerDraft.triggerName?.trim()
      && this.triggerDraft.jobClass
      && this.triggerDraft.misfireInstruction
      && this.triggerDraft.repeatCount !== null
      && this.triggerDraft.repeatCount !== undefined
      && this.triggerDraft.repeatIntervalAmount
      && this.triggerDraft.repeatIntervalUnit
    );
  }

  getFirePreview(): string[] {
    const start = this.fromDatetimeLocalValue(this.triggerDraft.startDate) || new Date();
    const repeatInterval = this.getRepeatIntervalMs();
    if (!repeatInterval || repeatInterval <= 0) {
      return [this.formatDateTime(start) || 'Next fire unavailable'];
    }

    return Array.from({length: 5}).map((_, index) => {
      const fireTime = new Date(start.getTime() + repeatInterval * index);
      return `${index + 1}. ${this.formatDateTime(fireTime)}`;
    });
  }

  shortClassName(className: string): string {
    if (!className) {
      return null;
    }
    const parts = className.split('.');
    return parts[parts.length - 1];
  }

  formatDateTime(value: Date | string): string {
    if (!value) {
      return null;
    }
    const date = value instanceof Date ? value : new Date(value);
    if (Number.isNaN(date.getTime())) {
      return null;
    }
    return date.toLocaleString();
  }

  formatDuration(milliseconds: number): string {
    if (!milliseconds) {
      return '0 ms';
    }
    if (milliseconds % 3600000 === 0) {
      return `${milliseconds / 3600000} h`;
    }
    if (milliseconds % 60000 === 0) {
      return `${milliseconds / 60000} min`;
    }
    if (milliseconds % 1000 === 0) {
      return `${milliseconds / 1000} sec`;
    }
    return `${milliseconds} ms`;
  }

  private setSchedulerStatus(status: string, notice: string) {
    if (!this.scheduler) {
      this.scheduler = new Scheduler(null, null, status, []);
    }
    this.scheduler.status = status;
    this.operationNotice = notice;
    this.roadmapNotice = null;
  }

  private subscribeToTriggerTopics(triggerKey: TriggerKey) {
    this.unsubscribeFromTriggerTopics();
    this.logs = [];
    this.progress = null;

    this.logsSubscription = this.logsRxWebsocketService.watch(`/topic/logs/${triggerKey.name}`)
      .pipe(map((msg: any) => JSON.parse(msg.body)))
      .subscribe(logRecord => this.ngZone.run(() => this.addLogRecord(logRecord)), err => console.log(err));

    this.progressSubscription = this.progressRxWebsocketService.watch(`/topic/progress/${triggerKey.name}`)
      .pipe(map((msg: any) => JSON.parse(msg.body)))
      .subscribe(progress => this.ngZone.run(() => this.progress = progress), err => console.log(err));
  }

  private unsubscribeFromTriggerTopics() {
    if (this.logsSubscription) {
      this.logsSubscription.unsubscribe();
      this.logsSubscription = null;
    }
    if (this.progressSubscription) {
      this.progressSubscription.unsubscribe();
      this.progressSubscription = null;
    }
  }

  private addLogRecord(logRecord: any) {
    const selectedSource = this.selectedTriggerKey?.group || this.selectedTriggerKey?.name || 'trigger';
    this.logs = [{
      time: logRecord.date,
      severity: logRecord.type || 'INFO',
      type: 'JOB_LOG',
      source: logRecord.threadName || selectedSource,
      message: logRecord.message || JSON.stringify(logRecord)
    }, ...this.logs].slice(0, 50);
  }

  private upsertTriggerKey(triggerKey: TriggerKey) {
    if (!this.triggerKeys.some(currentTriggerKey => currentTriggerKey.name === triggerKey.name)) {
      this.triggerKeys = [triggerKey, ...this.triggerKeys];
    }
  }

  private buildEmptyDraft(): TriggerDraft {
    return {
      triggerName: '',
      group: 'DEFAULT',
      jobClass: this.jobs[0] || '',
      startDate: this.toDatetimeLocalValue(new Date()),
      endDate: '',
      repeatIntervalAmount: 1,
      repeatIntervalUnit: 'minutes',
      repeatCount: -1,
      misfireInstruction: 'MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT'
    };
  }

  private getRepeatIntervalMs(): number {
    const amount = Number(this.triggerDraft.repeatIntervalAmount || 0);
    switch (this.triggerDraft.repeatIntervalUnit) {
      case 'seconds': return amount * 1000;
      case 'minutes': return amount * 60000;
      case 'hours': return amount * 3600000;
      case 'days': return amount * 86400000;
      default: return amount;
    }
  }

  private splitRepeatInterval(milliseconds: number): {amount: number; unit: string} {
    if (milliseconds && milliseconds % 86400000 === 0) {
      return {amount: milliseconds / 86400000, unit: 'days'};
    }
    if (milliseconds && milliseconds % 3600000 === 0) {
      return {amount: milliseconds / 3600000, unit: 'hours'};
    }
    if (milliseconds && milliseconds % 60000 === 0) {
      return {amount: milliseconds / 60000, unit: 'minutes'};
    }
    if (milliseconds && milliseconds % 1000 === 0) {
      return {amount: milliseconds / 1000, unit: 'seconds'};
    }
    return {amount: milliseconds || 1, unit: 'milliseconds'};
  }

  private fromDatetimeLocalValue(value: string): Date {
    if (!value) {
      return null;
    }
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? null : date;
  }

  private toDatetimeLocalValue(value: Date | string): string {
    if (!value) {
      return '';
    }
    const date = value instanceof Date ? value : new Date(value);
    if (Number.isNaN(date.getTime())) {
      return '';
    }
    const offsetDate = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
    return offsetDate.toISOString().slice(0, 16);
  }

  private getMisfireInstructionName(misfireInstruction: number): string {
    switch (misfireInstruction) {
      case 1: return 'MISFIRE_INSTRUCTION_FIRE_NOW';
      case 2: return 'MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT';
      case 3: return 'MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT';
      case 4: return 'MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT';
      case 5: return 'MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT';
      default: return 'MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT';
    }
  }

  private getPageTitle(page: ConsolePage): string {
    switch (page) {
      case 'calendars': return 'Quartz calendars';
      case 'executions': return 'Execution history and currently executing jobs';
      default: return page;
    }
  }
}

import {Component, NgZone, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {map} from 'rxjs/operators';

import {CalendarService, ExecutionService, SchedulerService, TriggerService} from '../../services';
import JobService from '../../services/job.service';
import {LogsRxWebsocketService} from '../../services/logs.rx-websocket.service';
import {ProgressRxWebsocketService} from '../../services/progress.rx-websocket.service';
import {Scheduler} from '../../model/scheduler.model';
import {Trigger} from '../../model/trigger.model';
import {TriggerCommand, TriggerType} from '../../model/trigger-command.model';
import {CalendarType, QuartzCalendar} from '../../model/calendar.model';
import {ScheduledJob} from '../../model/scheduled-job.model';
import {ScheduledJobCommand} from '../../model/scheduled-job.command';
import {TriggerKey} from '../../model/triggerKey.model';
import TriggerFiredBundle from '../../model/trigger-fired-bundle.model';
import {CurrentExecution} from '../../model/current-execution.model';

type ConsolePage = 'dashboard' | 'jobs' | 'triggers' | 'calendars' | 'executions' | 'events' | 'scheduler';
type WizardMode = 'create' | 'edit';
type JobTargetType = 'stored' | 'class';
type JobDataMapType = 'string' | 'number' | 'boolean' | 'json' | 'null';
type CalendarRuleMode = 'dates' | 'weekdays' | 'monthdays' | 'timeRange' | 'cron';

interface ConsoleLogRecord {
  time: Date;
  receivedAt?: number;
  severity: string;
  type: string;
  source: string;
  message: string;
}

interface TriggerStateCount {
  state: string;
  count: number;
}

interface TriggerDraft {
  triggerName: string;
  group: string;
  triggerType: TriggerType;
  jobTargetType: JobTargetType;
  storedJobKey: string;
  jobClass: string;
  startDate: string;
  endDate: string;
  repeatIntervalAmount: number;
  repeatIntervalUnit: string;
  repeatCount: number;
  misfireInstruction: string;
  cronExpression: string;
  timeZone: string;
  startTimeOfDay: string;
  endTimeOfDay: string;
  daysOfWeek: number[];
  preserveHourOfDayAcrossDaylightSavings: boolean;
  skipDayIfHourDoesNotExist: boolean;
  calendarName: string;
  jobDataMapEntries: JobDataMapEntry[];
}

interface CalendarDraft {
  name: string;
  type: CalendarType;
  description: string;
  cronExpression: string;
  timeZone: string;
  rangeStartingTime: string;
  rangeEndingTime: string;
  invertTimeRange: boolean;
  excludedDaysOfWeek: number[];
  excludedDaysOfMonth: number[];
  excludedDates: string[];
  includedTime: string;
}

interface JobDraft {
  name: string;
  group: string;
  jobClass: string;
  description: string;
  durable: boolean;
  requestsRecovery: boolean;
  jobDataMapEntries: JobDataMapEntry[];
}

interface JobDataMapEntry {
  key: string;
  type: JobDataMapType;
  value: string;
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
  triggerDetailsByName: {[triggerName: string]: Trigger} = {};
  selectedTriggerKey: TriggerKey;
  selectedTrigger: Trigger;
  selectedJobClass: string;
  selectedScheduledJob: ScheduledJob;
  jobs: string[] = [];
  scheduledJobs: ScheduledJob[] = [];
  currentExecutions: CurrentExecution[] = [];
  recoveringExecutions: CurrentExecution[] = [];
  selectedCurrentExecution: CurrentExecution;
  logs: ConsoleLogRecord[] = [];
  progress: TriggerFiredBundle;
  roadmapNotice: string;
  operationNotice: string;
  operationError: string;
  triggerLoading = false;
  wizardMode: WizardMode = 'create';
  wizardOpen = false;
  jobWizardOpen = false;
  detailDrawerOpen = false;
  wizardSubmitting = false;
  jobWizardSubmitting = false;
  wizardError: string;
  jobWizardError: string;
  triggerDraft: TriggerDraft = this.buildEmptyDraft();
  calendarDraft: CalendarDraft = this.buildEmptyCalendarDraft();
  jobDraft: JobDraft = this.buildEmptyJobDraft();
  jobWizardMode: WizardMode = 'create';
  jobGroupFilter = 'ALL';
  triggerGroupFilter = 'ALL';
  jobSearch = '';
  triggerSearch = '';
  streamPaused = false;
  dashboardEventTextFilter = '';
  eventSeverityFilter = 'ALL';
  eventTypeFilter = 'ALL';
  eventTextFilter = '';
  executionSearch = '';
  calendarSearch = '';
  calendars: QuartzCalendar[] = [];
  selectedCalendar: QuartzCalendar;
  calendarWizardOpen = false;
  calendarWizardMode: WizardMode = 'create';
  calendarWizardSubmitting = false;
  calendarWizardError: string;
  calendarIncludedTimeResult: string;

  private readonly roadmapPages = new Set<ConsolePage>();
  private readonly subscriptions: Subscription[] = [];
  private logsSubscription: Subscription;
  private progressSubscription: Subscription;
  private noticeTimer: ReturnType<typeof setTimeout>;

  constructor(
    private schedulerService: SchedulerService,
    private triggerService: TriggerService,
    private calendarService: CalendarService,
    private jobService: JobService,
    private executionService: ExecutionService,
    private logsRxWebsocketService: LogsRxWebsocketService,
    private progressRxWebsocketService: ProgressRxWebsocketService,
    private ngZone: NgZone
  ) {}

  ngOnInit() {
    this.refreshScheduler();
    this.fetchTriggers();
    this.fetchJobs();
    this.fetchScheduledJobs();
    this.fetchCurrentExecutions(false);
    this.fetchRecoveringExecutions(false);
    this.fetchCalendars();
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

  closeJobWizardDrawer() {
    this.jobWizardOpen = false;
  }

  closeCalendarWizardDrawer() {
    this.calendarWizardOpen = false;
  }

  closeDrawers() {
    this.detailDrawerOpen = false;
    this.wizardOpen = false;
    this.jobWizardOpen = false;
    this.calendarWizardOpen = false;
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

  startSchedulerDelayed(seconds = 60) {
    const subscription = this.schedulerService.startSchedulerDelayed(seconds).subscribe({
      next: () => this.operationNotice = `Scheduler will start in ${seconds} seconds.`,
      error: () => this.operationError = 'Unable to schedule delayed start.'
    });
    this.subscriptions.push(subscription);
  }

  standbyScheduler() {
    const subscription = this.schedulerService.standbyScheduler().subscribe({
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
    const subscription = this.schedulerService.shutdownScheduler().subscribe({
      next: () => this.setSchedulerStatus('STOPPED', 'Scheduler shut down.'),
      error: () => this.operationError = 'Unable to shut down the scheduler.'
    });
    this.subscriptions.push(subscription);
  }

  pauseAllTriggerGroups() {
    const subscription = this.schedulerService.pauseAll().subscribe({
      next: () => {
        this.triggerKeys.forEach(triggerKey => {
          const detail = this.triggerDetailsByName[this.getTriggerDetailKey(triggerKey)];
          if (detail) {
            detail.state = 'PAUSED';
          }
        });
        if (this.selectedTrigger) {
          this.selectedTrigger.state = 'PAUSED';
        }
        this.operationNotice = 'All triggers paused.';
      },
      error: () => this.operationError = 'Unable to pause all triggers.'
    });
    this.subscriptions.push(subscription);
  }

  clearScheduler() {
    if (!window.confirm('Clear every job, trigger, and calendar from this scheduler? This cannot be undone.')) {
      return;
    }
    const subscription = this.schedulerService.clearScheduler().subscribe({
      next: () => {
        this.triggerKeys = [];
        this.triggerDetailsByName = {};
        this.selectedTriggerKey = null;
        this.selectedTrigger = null;
        this.scheduledJobs = [];
        this.selectedScheduledJob = null;
        this.calendars = [];
        this.selectedCalendar = null;
        this.operationNotice = 'Scheduler cleared.';
      },
      error: () => this.operationError = 'Unable to clear the scheduler.'
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

  fetchScheduledJobs() {
    const subscription = this.jobService.fetchScheduledJobs().subscribe({
      next: scheduledJobs => {
        this.scheduledJobs = scheduledJobs || [];
        this.selectedScheduledJob = this.scheduledJobs[0];
      },
      error: () => this.operationError = 'Unable to load scheduled jobs.'
    });
    this.subscriptions.push(subscription);
  }

  fetchCurrentExecutions(showNotice = true) {
    const subscription = this.executionService.fetchCurrentExecutions().subscribe({
      next: currentExecutions => {
        this.currentExecutions = currentExecutions || [];
        this.selectedCurrentExecution = this.currentExecutions.find(execution => execution.fireInstanceId === this.selectedCurrentExecution?.fireInstanceId)
          || this.currentExecutions[0];
        if (showNotice) {
          this.operationNotice = 'Current executions refreshed.';
        }
      },
      error: () => this.operationError = 'Unable to load current executions.'
    });
    this.subscriptions.push(subscription);
  }

  fetchRecoveringExecutions(showNotice = true) {
    const subscription = this.executionService.fetchRecoveringExecutions().subscribe({
      next: recoveringExecutions => {
        this.recoveringExecutions = recoveringExecutions || [];
        if (showNotice) {
          this.operationNotice = `${this.recoveringExecutions.length} recovering job execution${this.recoveringExecutions.length === 1 ? '' : 's'} found.`;
        }
      },
      error: () => this.operationError = 'Unable to load recovering jobs.'
    });
    this.subscriptions.push(subscription);
  }

  selectCurrentExecution(currentExecution: CurrentExecution) {
    this.selectedCurrentExecution = currentExecution;
    this.openDetailDrawer();
  }

  fetchCalendars() {
    const subscription = this.calendarService.fetchCalendars().subscribe({
      next: calendars => {
        this.calendars = calendars || [];
        this.selectedCalendar = this.selectedCalendar || this.calendars[0];
      },
      error: () => this.operationError = 'Unable to load calendars.'
    });
    this.subscriptions.push(subscription);
  }

  fetchTriggerDetails(triggerKeys: TriggerKey[]) {
    triggerKeys.forEach(triggerKey => {
      const subscription = this.triggerService.getTrigger(triggerKey).subscribe({
        next: trigger => this.triggerDetailsByName[this.getTriggerDetailKey(triggerKey)] = trigger,
        error: () => {
          this.triggerDetailsByName[this.getTriggerDetailKey(triggerKey)] = null;
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
    this.selectedTrigger = this.triggerDetailsByName[this.getTriggerDetailKey(triggerKey)] || null;
    this.subscribeToTriggerTopics(this.selectedTriggerKey);
    const subscription = this.triggerService.getTrigger(triggerKey).subscribe({
      next: trigger => {
        this.selectedTrigger = trigger;
        this.triggerDetailsByName[this.getTriggerDetailKey(triggerKey)] = trigger;
        this.triggerLoading = false;
      },
      error: () => {
        this.triggerLoading = false;
        this.operationError = 'Unable to load trigger details.';
      }
    });
    this.subscriptions.push(subscription);
  }

  selectJob(jobClass: string) {
    this.selectedJobClass = jobClass;
    this.openDetailDrawer();
  }

  selectScheduledJob(job: ScheduledJob) {
    this.selectedScheduledJob = job;
    this.selectedJobClass = job?.jobClassName || this.selectedJobClass;
    this.openDetailDrawer();
  }

  openCreateJobWizard() {
    this.jobWizardMode = 'create';
    this.jobDraft = this.buildEmptyJobDraft();
    this.jobWizardError = null;
    this.jobWizardOpen = true;
    this.detailDrawerOpen = false;
    this.selectPage('jobs');
    this.jobWizardOpen = true;
  }

  openEditJobWizard() {
    if (!this.selectedScheduledJob?.jobKeyDTO) {
      this.showRoadmapNotice('Editing requires a stored job returned by the backend');
      return;
    }
    this.jobWizardMode = 'edit';
    this.jobWizardError = null;
    this.jobDraft = {
      name: this.selectedScheduledJob.jobKeyDTO.name,
      group: this.selectedScheduledJob.jobKeyDTO.group || 'DEFAULT',
      jobClass: this.selectedScheduledJob.jobClassName,
      description: this.selectedScheduledJob.description || '',
      durable: this.selectedScheduledJob.durable,
      requestsRecovery: this.selectedScheduledJob.requestsRecovery,
      jobDataMapEntries: this.toJobDataMapEntries(this.selectedScheduledJob.jobDataMap)
    };
    this.jobWizardOpen = true;
    this.detailDrawerOpen = false;
    this.selectPage('jobs');
    this.jobWizardOpen = true;
  }

  submitJobWizard() {
    this.jobWizardError = null;
    if (!this.canSubmitJob()) {
      this.jobWizardError = 'Job name, group, and class are required.';
      return;
    }

    let jobDataMap: {[key: string]: unknown};
    try {
      jobDataMap = this.serializeJobDataMap(this.jobDraft.jobDataMapEntries);
    } catch (err) {
      this.jobWizardError = this.getErrorMessage(err, 'JobDataMap contains invalid values.');
      return;
    }

    const command = new ScheduledJobCommand();
    command.jobClass = this.jobDraft.jobClass;
    command.description = this.jobDraft.description;
    command.durable = this.jobDraft.durable;
    command.requestsRecovery = this.jobDraft.requestsRecovery;
    command.jobDataMap = jobDataMap;

    this.jobWizardSubmitting = true;
    const group = this.jobDraft.group || 'DEFAULT';
    const name = this.jobDraft.name.trim();
    const request = this.jobWizardMode === 'edit'
      ? this.jobService.updateJob(group, name, command)
      : this.jobService.createJob(group, name, command);

    const subscription = request.subscribe({
      next: job => {
        this.jobWizardSubmitting = false;
        this.upsertScheduledJob(job);
        this.selectedScheduledJob = job;
        this.selectedJobClass = job.jobClassName;
        this.jobWizardOpen = false;
        this.detailDrawerOpen = true;
        this.operationNotice = this.jobWizardMode === 'edit' ? 'Stored job updated.' : 'Stored job created.';
      },
      error: () => {
        this.jobWizardSubmitting = false;
        this.jobWizardError = 'Unable to save the stored job.';
      }
    });
    this.subscriptions.push(subscription);
  }

  triggerSelectedJobNow() {
    if (!this.selectedScheduledJob) {
      this.showRoadmapNotice('Trigger-now requires a scheduled job key returned by the backend');
      return;
    }
    const subscription = this.jobService.triggerJob(this.selectedScheduledJob).subscribe({
      next: () => this.operationNotice = 'Job triggered.',
      error: () => this.operationError = 'Unable to trigger the selected job.'
    });
    this.subscriptions.push(subscription);
  }

  pauseSelectedJob() {
    if (!this.selectedScheduledJob) {
      this.showRoadmapNotice('Pause requires a scheduled job key returned by the backend');
      return;
    }
    const subscription = this.jobService.pauseJob(this.selectedScheduledJob).subscribe({
      next: () => {
        this.operationNotice = 'Job paused.';
        this.fetchTriggerDetails(this.triggerKeys);
      },
      error: () => this.operationError = 'Unable to pause the selected job.'
    });
    this.subscriptions.push(subscription);
  }

  interruptSelectedJob() {
    if (!this.selectedScheduledJob) {
      this.showRoadmapNotice('Interrupt requires a scheduled job key returned by the backend');
      return;
    }
    const jobLabel = this.getSelectedJobKeyLabel();
    if (!window.confirm(`Interrupt running executions of job ${jobLabel}?`)) {
      return;
    }
    const subscription = this.jobService.interruptJob(this.selectedScheduledJob).subscribe({
      next: result => {
        this.operationNotice = result?.interrupted ? `Interrupt signal sent to ${jobLabel}.` : `No interruptible execution found for ${jobLabel}.`;
        this.fetchCurrentExecutions(false);
      },
      error: () => this.operationError = 'Unable to interrupt the selected job.'
    });
    this.subscriptions.push(subscription);
  }

  interruptSelectedExecutionJobKey() {
    const jobKey = this.selectedCurrentExecution?.jobKeyDTO;
    if (!jobKey?.name) {
      this.showRoadmapNotice('Select a current execution before interrupting by job key');
      return;
    }
    const jobLabel = this.getCurrentExecutionJobLabel();
    if (!window.confirm(`Interrupt running executions of job ${jobLabel}?`)) {
      return;
    }
    const subscription = this.jobService.interruptJobKey(jobKey.group, jobKey.name).subscribe({
      next: result => {
        this.operationNotice = result?.interrupted ? `Interrupt signal sent to ${jobLabel}.` : `No interruptible execution found for ${jobLabel}.`;
        this.fetchCurrentExecutions(false);
        this.fetchRecoveringExecutions(false);
      },
      error: () => this.operationError = 'Unable to interrupt the selected execution job key.'
    });
    this.subscriptions.push(subscription);
  }

  pauseCurrentJobGroup() {
    const group = this.jobGroupFilter !== 'ALL'
      ? this.jobGroupFilter
      : this.selectedScheduledJob?.jobKeyDTO?.group;
    if (!group) {
      this.showRoadmapNotice('Select a job group or a scheduled job before pausing a group');
      return;
    }
    if (!window.confirm(`Pause all jobs in group ${group}?`)) {
      return;
    }
    const subscription = this.jobService.pauseJobGroup(group).subscribe({
      next: () => {
        this.operationNotice = `Job group ${group} paused.`;
        this.fetchTriggerDetails(this.triggerKeys);
      },
      error: () => this.operationError = 'Unable to pause the selected job group.'
    });
    this.subscriptions.push(subscription);
  }

  deleteSelectedJob() {
    if (!this.selectedScheduledJob) {
      this.showRoadmapNotice('Delete requires a scheduled job key returned by the backend');
      return;
    }
    if (!window.confirm(`Delete job ${this.selectedScheduledJob.jobKeyDTO.group}.${this.selectedScheduledJob.jobKeyDTO.name}?`)) {
      return;
    }
    const subscription = this.jobService.deleteJob(this.selectedScheduledJob).subscribe({
      next: () => {
        this.operationNotice = 'Job deleted.';
        this.scheduledJobs = this.scheduledJobs.filter(job => !this.sameJob(job, this.selectedScheduledJob));
        this.selectedScheduledJob = this.scheduledJobs[0];
      },
      error: () => this.operationError = 'Unable to delete the selected job.'
    });
    this.subscriptions.push(subscription);
  }

  selectCalendar(calendar: QuartzCalendar) {
    this.selectedCalendar = calendar;
    this.openDetailDrawer();
  }

  openCreateCalendarWizard() {
    this.calendarWizardMode = 'create';
    this.calendarWizardError = null;
    this.calendarDraft = this.buildEmptyCalendarDraft();
    this.calendarWizardOpen = true;
    this.detailDrawerOpen = false;
    this.selectPage('calendars');
    this.calendarWizardOpen = true;
  }

  openEditCalendarWizard() {
    if (!this.selectedCalendar) {
      return;
    }
    this.calendarWizardMode = 'edit';
    this.calendarWizardError = null;
    this.calendarDraft = this.fromCalendarToDraft(this.selectedCalendar);
    this.calendarWizardOpen = true;
    this.detailDrawerOpen = false;
    this.selectPage('calendars');
    this.calendarWizardOpen = true;
  }

  submitCalendarWizard() {
    this.calendarWizardError = null;
    if (!this.canSubmitCalendar()) {
      this.calendarWizardError = 'Calendar name, type, and rule fields are required.';
      return;
    }

    const calendar = this.fromCalendarDraftToCommand();
    const name = this.calendarDraft.name.trim();
    this.calendarWizardSubmitting = true;
    const request = this.calendarWizardMode === 'edit'
      ? this.calendarService.updateCalendar(name, calendar)
      : this.calendarService.createCalendar(name, calendar);

    const subscription = request.subscribe({
      next: savedCalendar => {
        this.calendarWizardSubmitting = false;
        this.upsertCalendar(savedCalendar);
        this.selectedCalendar = savedCalendar;
        this.calendarWizardOpen = false;
        this.detailDrawerOpen = true;
        this.operationNotice = this.calendarWizardMode === 'edit' ? 'Calendar updated.' : 'Calendar created.';
      },
      error: () => {
        this.calendarWizardSubmitting = false;
        this.calendarWizardError = 'Unable to save the calendar.';
      }
    });
    this.subscriptions.push(subscription);
  }

  deleteSelectedCalendar() {
    if (!this.selectedCalendar || !window.confirm(`Delete calendar ${this.selectedCalendar.name}?`)) {
      return;
    }
    const calendarName = this.selectedCalendar.name;
    const subscription = this.calendarService.deleteCalendar(calendarName).subscribe({
      next: () => {
        this.calendars = this.calendars.filter(calendar => calendar.name !== calendarName);
        this.selectedCalendar = this.calendars[0];
        this.operationNotice = 'Calendar deleted.';
      },
      error: () => this.operationError = 'Unable to delete the selected calendar.'
    });
    this.subscriptions.push(subscription);
  }

  testSelectedCalendarTime() {
    if (!this.selectedCalendar) {
      return;
    }
    const testTime = this.fromDatetimeLocalValue(this.calendarDraft.includedTime) || new Date();
    const subscription = this.calendarService.testIncludedTime(this.selectedCalendar.name, testTime).subscribe({
      next: result => this.calendarIncludedTimeResult = result.included
        ? 'Included at the tested time.'
        : `Excluded. Next included time: ${this.formatDateTime(result.nextIncludedTime) || '-'}`,
      error: () => this.operationError = 'Unable to test the selected calendar.'
    });
    this.subscriptions.push(subscription);
  }

  pauseSelectedTrigger() {
    if (!this.selectedTriggerKey) {
      return;
    }
    const subscription = this.triggerService.pauseTrigger(this.selectedTriggerKey).subscribe({
      next: () => this.setSelectedTriggerState('PAUSED', 'Trigger paused.'),
      error: () => this.operationError = 'Unable to pause the selected trigger.'
    });
    this.subscriptions.push(subscription);
  }

  resumeSelectedTrigger() {
    if (!this.selectedTriggerKey) {
      return;
    }
    const subscription = this.triggerService.resumeTrigger(this.selectedTriggerKey).subscribe({
      next: () => this.setSelectedTriggerState('NORMAL', 'Trigger resumed.'),
      error: () => this.operationError = 'Unable to resume the selected trigger.'
    });
    this.subscriptions.push(subscription);
  }

  resetSelectedTriggerFromErrorState() {
    if (!this.selectedTriggerKey) {
      this.showRoadmapNotice('Reset error requires a trigger key returned by the backend');
      return;
    }
    const triggerLabel = `${this.getSelectedTriggerGroup()}.${this.selectedTriggerKey.name}`;
    if (!window.confirm(`Reset trigger ${triggerLabel} from ERROR state?`)) {
      return;
    }
    const subscription = this.triggerService.resetTriggerFromErrorState(this.selectedTriggerKey).subscribe({
      next: () => {
        this.operationNotice = `Trigger ${triggerLabel} reset from error state.`;
        this.fetchTriggerDetails(this.triggerKeys);
      },
      error: () => this.operationError = 'Unable to reset the selected trigger from error state.'
    });
    this.subscriptions.push(subscription);
  }

  unscheduleSelectedTrigger() {
    if (!this.selectedTriggerKey) {
      return;
    }
    if (!window.confirm(`Unschedule trigger ${this.getSelectedTriggerGroup()}.${this.selectedTriggerKey.name}?`)) {
      return;
    }
    const triggerKey = {...this.selectedTriggerKey};
    const subscription = this.triggerService.unscheduleTrigger(triggerKey).subscribe({
      next: () => {
        this.operationNotice = 'Trigger unscheduled.';
        this.triggerKeys = this.triggerKeys.filter(currentTriggerKey => !this.sameTriggerKey(currentTriggerKey, triggerKey));
        delete this.triggerDetailsByName[this.getTriggerDetailKey(triggerKey)];
        this.selectedTriggerKey = this.triggerKeys[0];
        this.selectedTrigger = this.selectedTriggerKey
          ? this.triggerDetailsByName[this.getTriggerDetailKey(this.selectedTriggerKey)]
          : null;
      },
      error: () => this.operationError = 'Unable to unschedule the selected trigger.'
    });
    this.subscriptions.push(subscription);
  }

  openCreateTriggerWizard() {
    this.resetWizard();
    this.wizardOpen = true;
    this.detailDrawerOpen = false;
    this.selectPage('triggers');
    this.wizardOpen = true;
  }

  openRescheduleWizard(triggerKey?: TriggerKey) {
    if (triggerKey) {
      this.selectTrigger(triggerKey, false);
    }
    if (!this.selectedTrigger && !this.selectedTriggerKey) {
      this.showRoadmapNotice('Reschedule requires a trigger loaded from the backend');
      return;
    }

    const trigger = this.selectedTrigger || this.triggerDetailsByName[this.getTriggerDetailKey(this.selectedTriggerKey)];
    const repeatInterval = this.splitRepeatInterval(trigger?.repeatInterval || 60000);
    this.wizardMode = 'edit';
    this.wizardOpen = true;
    this.detailDrawerOpen = false;
    this.triggerDraft = {
      triggerName: this.selectedTriggerKey.name,
      group: this.selectedTriggerKey.group || 'DEFAULT',
      triggerType: this.getTriggerTypeValue(trigger),
      jobTargetType: trigger?.jobKeyDTO ? 'stored' : 'class',
      storedJobKey: trigger?.jobKeyDTO
        ? this.getJobOptionValue(trigger.jobKeyDTO.group, trigger.jobKeyDTO.name)
        : this.getDefaultStoredJobKey(),
      jobClass: trigger?.jobDetailDTO?.jobClassName || this.jobs[0] || '',
      startDate: this.toDatetimeLocalValue(trigger?.startTime),
      endDate: this.toDatetimeLocalValue(trigger?.endTime),
      repeatIntervalAmount: repeatInterval.amount,
      repeatIntervalUnit: repeatInterval.unit,
      repeatCount: trigger?.repeatCount ?? -1,
      misfireInstruction: this.getMisfireInstructionName(trigger?.misfireInstruction, this.getTriggerTypeValue(trigger)),
      cronExpression: trigger?.cronExpression || '0 0/5 * * * ?',
      timeZone: trigger?.timeZone || Intl.DateTimeFormat().resolvedOptions().timeZone,
      startTimeOfDay: trigger?.startTimeOfDay || '08:00:00',
      endTimeOfDay: trigger?.endTimeOfDay || '18:00:00',
      daysOfWeek: trigger?.daysOfWeek || [2, 3, 4, 5, 6],
      preserveHourOfDayAcrossDaylightSavings: !!trigger?.preserveHourOfDayAcrossDaylightSavings,
      skipDayIfHourDoesNotExist: !!trigger?.skipDayIfHourDoesNotExist,
      calendarName: trigger?.calendarName || '',
      jobDataMapEntries: this.toJobDataMapEntries(trigger?.jobDataMap)
    };
    this.selectPage('triggers');
    this.wizardOpen = true;
  }

  duplicateSelectedTrigger() {
    if (!this.selectedTriggerKey) {
      return;
    }
    this.openRescheduleWizard(this.selectedTriggerKey);
    this.wizardMode = 'create';
    this.triggerDraft.triggerName = `${this.selectedTriggerKey.name}-copy`;
  }

  resetWizard() {
    this.wizardMode = 'create';
    this.wizardError = null;
    this.triggerDraft = this.buildEmptyDraft();
  }

  submitTriggerWizard() {
    this.wizardError = null;
    if (!this.canSubmitTrigger()) {
      this.wizardError = 'Trigger name, target job, type, and schedule fields are required.';
      return;
    }

    const command = new TriggerCommand();
    command.triggerType = this.triggerDraft.triggerType;
    command.jobClass = this.triggerDraft.jobTargetType === 'class' ? this.triggerDraft.jobClass : null;
    command.jobKey = this.triggerDraft.jobTargetType === 'stored' ? this.parseJobOptionValue(this.triggerDraft.storedJobKey) : null;
    command.startDate = this.fromDatetimeLocalValue(this.triggerDraft.startDate);
    command.endDate = this.fromDatetimeLocalValue(this.triggerDraft.endDate);
    command.repeatInterval = this.getTriggerCommandRepeatInterval();
    command.repeatCount = this.triggerDraft.triggerType === 'SIMPLE' ? this.triggerDraft.repeatCount : null;
    command.repeatIntervalUnit = this.getTriggerCommandRepeatIntervalUnit();
    command.misfireInstruction = this.triggerDraft.misfireInstruction;
    command.cronExpression = this.triggerDraft.triggerType === 'CRON' ? this.triggerDraft.cronExpression : null;
    command.timeZone = this.triggerDraft.timeZone;
    command.startTimeOfDay = this.triggerDraft.triggerType === 'DAILY_TIME_INTERVAL' ? this.triggerDraft.startTimeOfDay : null;
    command.endTimeOfDay = this.triggerDraft.triggerType === 'DAILY_TIME_INTERVAL' ? this.triggerDraft.endTimeOfDay : null;
    command.daysOfWeek = this.triggerDraft.triggerType === 'DAILY_TIME_INTERVAL' ? this.triggerDraft.daysOfWeek : null;
    command.preserveHourOfDayAcrossDaylightSavings = this.triggerDraft.triggerType === 'CALENDAR_INTERVAL'
      ? this.triggerDraft.preserveHourOfDayAcrossDaylightSavings
      : null;
    command.skipDayIfHourDoesNotExist = this.triggerDraft.triggerType === 'CALENDAR_INTERVAL'
      ? this.triggerDraft.skipDayIfHourDoesNotExist
      : null;
    command.calendarName = this.triggerDraft.calendarName || null;
    try {
      command.jobDataMap = this.serializeJobDataMap(this.triggerDraft.jobDataMapEntries);
    } catch (err) {
      this.wizardError = this.getErrorMessage(err, 'JobDataMap contains invalid values.');
      return;
    }

    this.wizardSubmitting = true;
    const group = this.triggerDraft.group || 'DEFAULT';
    const name = this.triggerDraft.triggerName.trim();
    const request = this.wizardMode === 'edit'
      ? this.triggerService.updateTrigger(group, name, command)
      : this.triggerService.saveTrigger(group, name, command);

    const subscription = request.subscribe({
      next: trigger => {
        this.wizardSubmitting = false;
        this.triggerDetailsByName[this.getTriggerDetailKey(trigger.triggerKeyDTO)] = trigger;
        this.upsertTriggerKey(trigger.triggerKeyDTO);
        this.selectTrigger(trigger.triggerKeyDTO);
        this.wizardOpen = false;
        this.detailDrawerOpen = true;
        this.operationNotice = this.wizardMode === 'edit' ? 'Trigger rescheduled.' : 'Trigger created.';
        if (this.wizardMode === 'create') {
          this.resetWizard();
        }
      },
      error: () => {
        this.wizardSubmitting = false;
        this.wizardError = 'Unable to save the trigger.';
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

  getTriggerDetail(triggerKey: TriggerKey): Trigger {
    return triggerKey?.name ? this.triggerDetailsByName[this.getTriggerDetailKey(triggerKey)] : null;
  }

  getTriggerGroup(triggerKey: TriggerKey): string {
    return triggerKey?.group || 'DEFAULT';
  }

  getTriggerType(triggerKey: TriggerKey): string {
    return this.getTriggerDetail(triggerKey)?.type || 'Trigger';
  }

  getTriggerState(triggerKey: TriggerKey): string {
    const trigger = this.getTriggerDetail(triggerKey);
    if (!trigger) {
      return 'UNKNOWN';
    }
    if (trigger.state) {
      return trigger.state;
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
    const jobGroup = trigger?.jobKeyDTO?.group ? `${trigger.jobKeyDTO.group}.` : '';
    return trigger?.jobKeyDTO?.name
      ? `${jobGroup}${trigger.jobKeyDTO.name}`
      : this.shortClassName(trigger?.jobDetailDTO?.jobClassName) || 'Pending';
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

  getSelectedTriggerTimeZone(): string {
    return this.selectedTrigger?.timeZone || 'not applicable';
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

  getRecoveringExecutionsLabel(): string {
    return `${this.recoveringExecutions.length} LIVE`;
  }

  toggleStreamPause() {
    this.streamPaused = !this.streamPaused;
    this.operationNotice = this.streamPaused ? 'Event stream paused.' : 'Event stream resumed.';
  }

  getStreamStatusLabel(): string {
    return this.streamPaused ? 'PAUSED' : 'STREAMING';
  }

  getStreamStatusClass(): string {
    return this.streamPaused ? 'paused' : 'normal';
  }

  getTriggerStateCounts(): TriggerStateCount[] {
    const stateCounts = (this.triggerKeys || []).reduce((counts, triggerKey) => {
      const state = this.getTriggerState(triggerKey);
      counts[state] = (counts[state] || 0) + 1;
      return counts;
    }, {} as {[state: string]: number});
    return Object.entries(stateCounts)
      .map(([state, count]) => ({state, count}))
      .sort((first, second) => first.state.localeCompare(second.state));
  }

  getEventTypeOptions(): string[] {
    return Array.from(new Set(this.logs.map(log => log.type).filter(Boolean))).sort((first, second) => first.localeCompare(second));
  }

  getEventLogRows(): ConsoleLogRecord[] {
    const textFilter = this.eventTextFilter?.trim().toLowerCase();
    return (this.logs || []).filter(log => {
      const severityMatches = this.eventSeverityFilter === 'ALL' || log.severity === this.eventSeverityFilter;
      const typeMatches = this.eventTypeFilter === 'ALL' || log.type === this.eventTypeFilter;
      const searchable = `${log.source} ${log.type} ${log.message} ${this.selectedTriggerKey?.group || ''} ${this.selectedTriggerKey?.name || ''}`.toLowerCase();
      return severityMatches && typeMatches && (!textFilter || searchable.includes(textFilter));
    });
  }

  getDashboardLogRows(): ConsoleLogRecord[] {
    const textFilter = this.dashboardEventTextFilter?.trim().toLowerCase();
    return (this.logs || []).filter(log => {
      const searchable = `${log.source} ${log.type} ${log.message} ${this.selectedTriggerKey?.group || ''} ${this.selectedTriggerKey?.name || ''}`.toLowerCase();
      return !textFilter || searchable.includes(textFilter);
    });
  }

  exportDashboardEvents() {
    this.exportLogs(this.getDashboardLogRows(), 'quartz-manager-dashboard-events.csv');
  }

  exportEventStream() {
    this.exportLogs(this.getEventLogRows(), 'quartz-manager-events.csv');
  }

  exportJobs() {
    const rows = this.getScheduledJobRows().map(job => [
      job.jobKeyDTO?.group || 'DEFAULT',
      job.jobKeyDTO?.name || '',
      job.jobClassName || '',
      job.durable ? 'true' : 'false',
      job.requestsRecovery ? 'true' : 'false',
      `${job.triggerKeys?.length || 0}`
    ]);
    this.downloadCsv('quartz-manager-jobs.csv', ['group', 'name', 'class', 'durable', 'requestsRecovery', 'triggerCount'], rows);
  }

  getSelectedTriggerCalendar(): QuartzCalendar {
    const calendarName = this.selectedTrigger?.calendarName;
    return calendarName ? this.calendars.find(calendar => calendar.name === calendarName) : null;
  }

  getExecutionRows(): CurrentExecution[] {
    const textFilter = this.executionSearch?.trim().toLowerCase();
    return (this.currentExecutions || []).filter(execution => {
      const searchable = `${execution.fireInstanceId || ''} ${execution.jobKeyDTO?.group || ''} ${execution.jobKeyDTO?.name || ''} ${execution.triggerKeyDTO?.group || ''} ${execution.triggerKeyDTO?.name || ''} ${execution.node || ''}`.toLowerCase();
      return !textFilter || searchable.includes(textFilter);
    });
  }

  getCurrentExecutionJobLabel(currentExecution = this.selectedCurrentExecution): string {
    return currentExecution?.jobKeyDTO ? `${currentExecution.jobKeyDTO.group}.${currentExecution.jobKeyDTO.name}` : '-';
  }

  getCurrentExecutionTriggerLabel(currentExecution = this.selectedCurrentExecution): string {
    return currentExecution?.triggerKeyDTO ? `${currentExecution.triggerKeyDTO.group}.${currentExecution.triggerKeyDTO.name}` : '-';
  }

  getCurrentExecutionRunTime(currentExecution = this.selectedCurrentExecution): string {
    if (!currentExecution) {
      return '-';
    }
    return this.formatDuration(currentExecution.runTime);
  }

  getJobClassRows(): string[] {
    return this.jobs.length > 0 ? this.jobs : ['No eligible Quartz Manager job classes returned by the backend'];
  }

  getSelectedJobShortName(): string {
    return this.shortClassName(this.selectedScheduledJob?.jobClassName || this.selectedJobClass) || '-';
  }

  getSelectedJobKeyLabel(): string {
    if (!this.selectedScheduledJob?.jobKeyDTO) {
      return '-';
    }
    return `${this.selectedScheduledJob.jobKeyDTO.group}.${this.selectedScheduledJob.jobKeyDTO.name}`;
  }

  getScheduledJobRows(): ScheduledJob[] {
    const search = this.jobSearch?.trim().toLowerCase();
    return (this.scheduledJobs || []).filter(job => {
      const groupMatches = this.jobGroupFilter === 'ALL' || job.jobKeyDTO?.group === this.jobGroupFilter;
      const searchable = `${job.jobKeyDTO?.group}.${job.jobKeyDTO?.name} ${job.jobClassName}`.toLowerCase();
      return groupMatches && (!search || searchable.includes(search));
    });
  }

  getTriggerRows(): TriggerKey[] {
    const search = this.triggerSearch?.trim().toLowerCase();
    return (this.triggerKeys || []).filter(triggerKey => {
      const group = this.getTriggerGroup(triggerKey);
      const groupMatches = this.triggerGroupFilter === 'ALL' || group === this.triggerGroupFilter;
      const searchable = `${group}.${triggerKey.name} ${this.getTriggerJobName(triggerKey)}`.toLowerCase();
      return groupMatches && (!search || searchable.includes(search));
    });
  }

  getJobGroups(): string[] {
    return this.getUniqueGroups(this.scheduledJobs.map(job => job.jobKeyDTO?.group));
  }

  getTriggerGroups(): string[] {
    return this.getUniqueGroups(this.triggerKeys.map(triggerKey => this.getTriggerGroup(triggerKey)));
  }

  getStoredJobOptions(): {label: string; value: string}[] {
    return this.scheduledJobs.map(job => ({
      label: `${job.jobKeyDTO.group}.${job.jobKeyDTO.name}`,
      value: this.getJobOptionValue(job.jobKeyDTO.group, job.jobKeyDTO.name)
    }));
  }

  getSelectedJobDataMapPreview(): string {
    return this.formatJson(this.selectedScheduledJob?.jobDataMap || {});
  }

  getSelectedTriggerDataMapPreview(): string {
    return this.formatJson(this.selectedTrigger?.jobDataMap || {});
  }

  getJobDraftDataMapPreview(): string {
    try {
      return this.formatJson(this.serializeJobDataMap(this.jobDraft.jobDataMapEntries));
    } catch (err) {
      return this.getErrorMessage(err, 'Invalid JobDataMap');
    }
  }

  getTriggerDraftDataMapPreview(): string {
    try {
      return this.formatJson(this.serializeJobDataMap(this.triggerDraft.jobDataMapEntries));
    } catch (err) {
      return this.getErrorMessage(err, 'Invalid JobDataMap');
    }
  }

  getWizardTitle(): string {
    return this.wizardMode === 'edit' ? 'Reschedule Trigger' : 'Create Trigger';
  }

  getWizardCta(): string {
    return this.wizardMode === 'edit' ? 'Save Reschedule' : 'Create Trigger';
  }

  canSubmitTrigger(): boolean {
    const hasTarget = this.triggerDraft.jobTargetType === 'stored'
      ? !!this.triggerDraft.storedJobKey
      : !!this.triggerDraft.jobClass;
    return !!(this.triggerDraft.triggerName?.trim()
      && hasTarget
      && this.triggerDraft.triggerType
      && this.hasValidTriggerSchedule());
  }

  hasValidTriggerSchedule(): boolean {
    switch (this.triggerDraft.triggerType) {
      case 'CRON': return !!this.triggerDraft.cronExpression?.trim();
      case 'DAILY_TIME_INTERVAL': return !!(this.triggerDraft.repeatIntervalAmount
        && this.triggerDraft.repeatIntervalUnit
        && this.triggerDraft.startTimeOfDay
        && this.triggerDraft.endTimeOfDay);
      case 'CALENDAR_INTERVAL': return !!(this.triggerDraft.repeatIntervalAmount && this.triggerDraft.repeatIntervalUnit);
      default: return this.triggerDraft.repeatCount !== null
        && this.triggerDraft.repeatCount !== undefined
        && !!this.triggerDraft.repeatIntervalAmount;
    }
  }

  canSubmitJob(): boolean {
    return !!(this.jobDraft.name?.trim() && this.jobDraft.group?.trim() && this.jobDraft.jobClass);
  }

  canSubmitCalendar(): boolean {
    if (!this.calendarDraft.name?.trim() || !this.calendarDraft.type) {
      return false;
    }
    switch (this.calendarDraft.type) {
      case 'CRON': return !!this.calendarDraft.cronExpression?.trim();
      case 'DAILY': return !!(this.calendarDraft.rangeStartingTime && this.calendarDraft.rangeEndingTime);
      default: return true;
    }
  }

  getCalendarRows(): QuartzCalendar[] {
    const search = this.calendarSearch?.trim().toLowerCase();
    return (this.calendars || []).filter(calendar => !search || `${calendar.name} ${calendar.type}`.toLowerCase().includes(search));
  }

  getCalendarRuleMode(): CalendarRuleMode {
    switch (this.calendarDraft.type) {
      case 'WEEKLY': return 'weekdays';
      case 'MONTHLY': return 'monthdays';
      case 'DAILY': return 'timeRange';
      case 'CRON': return 'cron';
      default: return 'dates';
    }
  }

  toggleCalendarWeekday(day: number) {
    this.calendarDraft.excludedDaysOfWeek = this.toggleNumberValue(this.calendarDraft.excludedDaysOfWeek, day);
  }

  toggleCalendarMonthday(day: number) {
    this.calendarDraft.excludedDaysOfMonth = this.toggleNumberValue(this.calendarDraft.excludedDaysOfMonth, day);
  }

  addCalendarDate() {
    this.calendarDraft.excludedDates = [...(this.calendarDraft.excludedDates || []), this.toDatetimeLocalValue(new Date())];
  }

  removeCalendarDate(index: number) {
    this.calendarDraft.excludedDates.splice(index, 1);
  }

  addJobDataMapEntry(entries: JobDataMapEntry[]) {
    entries.push({key: '', type: 'string', value: ''});
  }

  removeJobDataMapEntry(entries: JobDataMapEntry[], index: number) {
    entries.splice(index, 1);
  }

  getFirePreview(): string[] {
    if (this.triggerDraft.triggerType === 'CRON') {
      return [`Cron expression: ${this.triggerDraft.cronExpression || '-'}`];
    }
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

  getTriggerTypeValue(trigger: Trigger): TriggerType {
    const type = trigger?.type || '';
    if (type.includes('Cron')) {
      return 'CRON';
    }
    if (type.includes('DailyTimeInterval')) {
      return 'DAILY_TIME_INTERVAL';
    }
    if (type.includes('CalendarInterval')) {
      return 'CALENDAR_INTERVAL';
    }
    return 'SIMPLE';
  }

  selectTriggerType(triggerType: TriggerType) {
    this.triggerDraft.triggerType = triggerType;
    this.triggerDraft.misfireInstruction = this.getDefaultMisfireInstruction(triggerType);
  }

  toggleDayOfWeek(day: number) {
    const days = new Set(this.triggerDraft.daysOfWeek || []);
    if (days.has(day)) {
      days.delete(day);
    } else {
      days.add(day);
    }
    this.triggerDraft.daysOfWeek = Array.from(days).sort((first, second) => first - second);
  }

  isDayOfWeekSelected(day: number): boolean {
    return (this.triggerDraft.daysOfWeek || []).includes(day);
  }

  getCalendarOptions(): string[] {
    return this.calendars.map(calendar => calendar.name);
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

  private setSelectedTriggerState(state: string, notice: string) {
    if (this.selectedTrigger) {
      this.selectedTrigger.state = state;
    }
    if (this.selectedTriggerKey?.name && this.triggerDetailsByName[this.getTriggerDetailKey(this.selectedTriggerKey)]) {
      this.triggerDetailsByName[this.getTriggerDetailKey(this.selectedTriggerKey)].state = state;
    }
    this.operationNotice = notice;
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
    if (this.streamPaused) {
      return;
    }
    const selectedSource = this.selectedTriggerKey?.group || this.selectedTriggerKey?.name || 'trigger';
    this.logs = [{
      time: logRecord.date,
      receivedAt: Date.now(),
      severity: logRecord.type || 'INFO',
      type: 'JOB_LOG',
      source: logRecord.threadName || selectedSource,
      message: logRecord.message || JSON.stringify(logRecord)
    }, ...this.logs].slice(0, 50);
  }

  private upsertTriggerKey(triggerKey: TriggerKey) {
    if (!this.triggerKeys.some(currentTriggerKey => this.sameTriggerKey(currentTriggerKey, triggerKey))) {
      this.triggerKeys = [triggerKey, ...this.triggerKeys];
    }
  }

  private upsertScheduledJob(job: ScheduledJob) {
    const otherJobs = this.scheduledJobs.filter(currentJob => !this.sameJob(currentJob, job));
    this.scheduledJobs = [job, ...otherJobs];
  }

  private upsertCalendar(calendar: QuartzCalendar) {
    const otherCalendars = this.calendars.filter(currentCalendar => currentCalendar.name !== calendar.name);
    this.calendars = [calendar, ...otherCalendars];
  }

  private sameTriggerKey(first: TriggerKey, second: TriggerKey): boolean {
    return first?.name === second?.name && this.getTriggerGroup(first) === this.getTriggerGroup(second);
  }

  private getTriggerDetailKey(triggerKey: TriggerKey): string {
    return `${this.getTriggerGroup(triggerKey)}.${triggerKey.name}`;
  }

  private sameJob(first: ScheduledJob, second: ScheduledJob): boolean {
    return first?.jobKeyDTO?.name === second?.jobKeyDTO?.name && first?.jobKeyDTO?.group === second?.jobKeyDTO?.group;
  }

  private buildEmptyDraft(): TriggerDraft {
    return {
      triggerName: '',
      group: 'DEFAULT',
      triggerType: 'SIMPLE',
      jobTargetType: this.scheduledJobs.length > 0 ? 'stored' : 'class',
      storedJobKey: this.getDefaultStoredJobKey(),
      jobClass: this.jobs[0] || '',
      startDate: this.toDatetimeLocalValue(new Date()),
      endDate: '',
      repeatIntervalAmount: 1,
      repeatIntervalUnit: 'minutes',
      repeatCount: -1,
      misfireInstruction: 'MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT',
      cronExpression: '0 0/5 * * * ?',
      timeZone: Intl.DateTimeFormat().resolvedOptions().timeZone,
      startTimeOfDay: '08:00:00',
      endTimeOfDay: '18:00:00',
      daysOfWeek: [2, 3, 4, 5, 6],
      preserveHourOfDayAcrossDaylightSavings: false,
      skipDayIfHourDoesNotExist: false,
      calendarName: '',
      jobDataMapEntries: []
    };
  }

  private buildEmptyCalendarDraft(): CalendarDraft {
    return {
      name: '',
      type: 'WEEKLY',
      description: '',
      cronExpression: '0 0 0 ? * SAT,SUN',
      timeZone: Intl.DateTimeFormat().resolvedOptions().timeZone,
      rangeStartingTime: '22:00:00',
      rangeEndingTime: '06:00:00',
      invertTimeRange: false,
      excludedDaysOfWeek: [1, 7],
      excludedDaysOfMonth: [],
      excludedDates: [],
      includedTime: this.toDatetimeLocalValue(new Date())
    };
  }

  private buildEmptyJobDraft(): JobDraft {
    return {
      name: '',
      group: 'DEFAULT',
      jobClass: this.jobs[0] || '',
      description: '',
      durable: true,
      requestsRecovery: false,
      jobDataMapEntries: []
    };
  }

  private getDefaultStoredJobKey(): string {
    const job = this.scheduledJobs[0];
    return job?.jobKeyDTO ? this.getJobOptionValue(job.jobKeyDTO.group, job.jobKeyDTO.name) : '';
  }

  private getJobOptionValue(group: string, name: string): string {
    return `${group || 'DEFAULT'}::${name}`;
  }

  private parseJobOptionValue(value: string): {group: string; name: string} {
    const [group, name] = (value || '').split('::');
    return name ? {group: group || 'DEFAULT', name} : null;
  }

  private fromCalendarToDraft(calendar: QuartzCalendar): CalendarDraft {
    return {
      name: calendar.name,
      type: calendar.type,
      description: calendar.description || '',
      cronExpression: calendar.cronExpression || '0 0 0 ? * SAT,SUN',
      timeZone: calendar.timeZone || Intl.DateTimeFormat().resolvedOptions().timeZone,
      rangeStartingTime: calendar.rangeStartingTime || '22:00:00',
      rangeEndingTime: calendar.rangeEndingTime || '06:00:00',
      invertTimeRange: !!calendar.invertTimeRange,
      excludedDaysOfWeek: calendar.excludedDaysOfWeek || [],
      excludedDaysOfMonth: calendar.excludedDaysOfMonth || [],
      excludedDates: (calendar.excludedDates || []).map(date => this.toDatetimeLocalValue(date)),
      includedTime: this.toDatetimeLocalValue(new Date())
    };
  }

  private fromCalendarDraftToCommand(): QuartzCalendar {
    const calendar = new QuartzCalendar();
    calendar.name = this.calendarDraft.name.trim();
    calendar.type = this.calendarDraft.type;
    calendar.description = this.calendarDraft.description;
    calendar.cronExpression = this.calendarDraft.type === 'CRON' ? this.calendarDraft.cronExpression : null;
    calendar.timeZone = this.calendarDraft.timeZone;
    calendar.rangeStartingTime = this.calendarDraft.type === 'DAILY' ? this.calendarDraft.rangeStartingTime : null;
    calendar.rangeEndingTime = this.calendarDraft.type === 'DAILY' ? this.calendarDraft.rangeEndingTime : null;
    calendar.invertTimeRange = this.calendarDraft.type === 'DAILY' ? this.calendarDraft.invertTimeRange : null;
    calendar.excludedDaysOfWeek = this.calendarDraft.type === 'WEEKLY' ? this.calendarDraft.excludedDaysOfWeek : null;
    calendar.excludedDaysOfMonth = this.calendarDraft.type === 'MONTHLY' ? this.calendarDraft.excludedDaysOfMonth : null;
    calendar.excludedDates = ['ANNUAL', 'HOLIDAY'].includes(this.calendarDraft.type)
      ? (this.calendarDraft.excludedDates || []).map(value => this.fromDatetimeLocalValue(value)).filter(Boolean)
      : null;
    return calendar;
  }

  private toggleNumberValue(values: number[], value: number): number[] {
    const set = new Set(values || []);
    if (set.has(value)) {
      set.delete(value);
    } else {
      set.add(value);
    }
    return Array.from(set).sort((first, second) => first - second);
  }

  private getUniqueGroups(groups: string[]): string[] {
    return Array.from(new Set((groups || []).filter(Boolean))).sort((first, second) => first.localeCompare(second));
  }

  private toJobDataMapEntries(jobDataMap: {[key: string]: unknown}): JobDataMapEntry[] {
    return Object.entries(jobDataMap || {}).map(([key, value]) => ({
      key,
      type: this.getJobDataMapType(value),
      value: this.getJobDataMapEntryValue(value)
    }));
  }

  private getJobDataMapType(value: unknown): JobDataMapType {
    if (value === null) {
      return 'null';
    }
    if (typeof value === 'number') {
      return 'number';
    }
    if (typeof value === 'boolean') {
      return 'boolean';
    }
    if (typeof value === 'object') {
      return 'json';
    }
    return 'string';
  }

  private getJobDataMapEntryValue(value: unknown): string {
    if (value === null) {
      return '';
    }
    if (typeof value === 'object') {
      return JSON.stringify(value);
    }
    return `${value ?? ''}`;
  }

  private serializeJobDataMap(entries: JobDataMapEntry[]): {[key: string]: unknown} {
    return (entries || []).reduce((dataMap, entry) => {
      const key = entry.key?.trim();
      if (!key) {
        throw new Error('JobDataMap keys cannot be blank.');
      }
      if (Object.prototype.hasOwnProperty.call(dataMap, key)) {
        throw new Error(`JobDataMap key "${key}" is duplicated.`);
      }
      dataMap[key] = this.serializeJobDataMapValue(entry);
      return dataMap;
    }, {} as {[key: string]: unknown});
  }

  private serializeJobDataMapValue(entry: JobDataMapEntry): unknown {
    switch (entry.type) {
      case 'number': {
        const value = Number(entry.value);
        if (Number.isNaN(value)) {
          throw new Error(`JobDataMap key "${entry.key}" must be a number.`);
        }
        return value;
      }
      case 'boolean':
        if (entry.value !== 'true' && entry.value !== 'false') {
          throw new Error(`JobDataMap key "${entry.key}" must be true or false.`);
        }
        return entry.value === 'true';
      case 'json':
        try {
          return JSON.parse(entry.value || 'null');
        } catch (err) {
          throw new Error(`JobDataMap key "${entry.key}" contains invalid JSON: ${this.getErrorMessage(err, 'Invalid JSON')}`);
        }
      case 'null':
        return null;
      default:
        return entry.value || '';
    }
  }

  private formatJson(value: unknown): string {
    return JSON.stringify(value || {}, null, 2);
  }

  private exportLogs(logs: ConsoleLogRecord[], filename: string) {
    const rows = logs.map(log => [
      this.formatDateTime(log.time) || '',
      log.severity || '',
      log.type || '',
      log.source || '',
      log.message || ''
    ]);
    this.downloadCsv(filename, ['time', 'severity', 'type', 'source', 'message'], rows);
  }

  private downloadCsv(filename: string, headers: string[], rows: string[][]) {
    const csv = [headers, ...rows]
      .map(row => row.map(value => this.escapeCsvValue(value)).join(','))
      .join('\n');
    const blob = new Blob([csv], {type: 'text/csv;charset=utf-8;'});
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.click();
    URL.revokeObjectURL(url);
  }

  private escapeCsvValue(value: string): string {
    const normalizedValue = `${value ?? ''}`;
    return /[",\n]/.test(normalizedValue) ? `"${normalizedValue.replace(/"/g, '""')}"` : normalizedValue;
  }

  private getErrorMessage(err: unknown, fallback: string): string {
    return err instanceof Error ? err.message : fallback;
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

  private getTriggerCommandRepeatInterval(): number {
    if (this.triggerDraft.triggerType === 'SIMPLE') {
      return this.getRepeatIntervalMs();
    }
    return Number(this.triggerDraft.repeatIntervalAmount || 0);
  }

  private getTriggerCommandRepeatIntervalUnit(): string {
    if (this.triggerDraft.triggerType === 'SIMPLE' || this.triggerDraft.triggerType === 'CRON') {
      return null;
    }
    const unit = this.triggerDraft.repeatIntervalUnit || 'minutes';
    switch (unit) {
      case 'seconds': return 'SECOND';
      case 'minutes': return 'MINUTE';
      case 'hours': return 'HOUR';
      case 'days': return 'DAY';
      case 'weeks': return 'WEEK';
      case 'months': return 'MONTH';
      case 'years': return 'YEAR';
      default: return unit.toUpperCase();
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

  private getMisfireInstructionName(misfireInstruction: number, triggerType: TriggerType = 'SIMPLE'): string {
    if (triggerType !== 'SIMPLE') {
      switch (misfireInstruction) {
        case 1: return 'IGNORE_MISFIRES';
        case 2: return 'FIRE_AND_PROCEED';
        case 3: return 'DO_NOTHING';
        default: return 'FIRE_AND_PROCEED';
      }
    }
    switch (misfireInstruction) {
      case 1: return 'MISFIRE_INSTRUCTION_FIRE_NOW';
      case 2: return 'MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT';
      case 3: return 'MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT';
      case 4: return 'MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT';
      case 5: return 'MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT';
      default: return 'MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT';
    }
  }

  private getDefaultMisfireInstruction(triggerType: TriggerType): string {
    return triggerType === 'SIMPLE'
      ? 'MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT'
      : 'FIRE_AND_PROCEED';
  }

  private getPageTitle(page: ConsolePage): string {
    switch (page) {
      case 'calendars': return 'Quartz calendars';
      case 'executions': return 'Execution history and currently executing jobs';
      default: return page;
    }
  }
}

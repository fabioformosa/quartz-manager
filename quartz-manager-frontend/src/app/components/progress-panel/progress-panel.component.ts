import {Component, Input, OnDestroy, OnInit} from '@angular/core'
import TriggerFiredBundle from '../../model/trigger-fired-bundle.model';
import {TriggerKey} from '../../model/triggerKey.model';
import {ProgressRxWebsocketService} from '../../services/progress.rx-websocket.service';
import {map} from 'rxjs/operators';

@Component({
  selector: 'progress-panel',
  templateUrl: './progress-panel.component.html',
  styleUrls: ['./progress-panel.component.scss']
})
export class ProgressPanelComponent implements OnInit, OnDestroy {

  progress: TriggerFiredBundle = ProgressPanelComponent._buildEmptyProgress();
  percentageStr: string;
  progressUpdated = false;

  topicSubscription;
  private selectedTriggerKey: TriggerKey;

  constructor(
    private progressRxWebsocketService: ProgressRxWebsocketService
  ) { }

  @Input()
  set triggerKey(triggerKey: TriggerKey) {
    if (!triggerKey || !triggerKey.name) {
      this._unsubscribeFromTopic();
      this.selectedTriggerKey = null;
      this._resetProgress();
      return;
    }

    if (this.selectedTriggerKey?.name === triggerKey.name) {
      return;
    }

    this._resetProgress();
    this.selectedTriggerKey = {...triggerKey} as TriggerKey;
    this._subscribeToTheTopic(this.selectedTriggerKey);
  }

  private _subscribeToTheTopic = (triggerKey: TriggerKey) => {
    this._unsubscribeFromTopic();
    this.topicSubscription = this.progressRxWebsocketService.watch(`/topic/progress/${triggerKey.name}`)
      .pipe(map((msg: any) => JSON.parse(msg.body)))
      .subscribe(this.onNewProgressMsg, (err) => {
        console.log(err);
        // TODO in case of 401
        // this.apiService.get('/quartz-manager/session/refresh');
      });
  };

  onNewProgressMsg = (receivedMsg) => {
      this.progress = receivedMsg;
      this.percentageStr = this.progress.percentage + '%';
      this._markProgressUpdated();
  }

  ngOnInit() {
  }

  ngOnDestroy() {
    this._unsubscribeFromTopic();
  }

  private _unsubscribeFromTopic() {
    if (this.topicSubscription) {
      this.topicSubscription.unsubscribe();
      this.topicSubscription = null;
    }
  }

  private _resetProgress() {
    this.progress = ProgressPanelComponent._buildEmptyProgress();
    this.percentageStr = null;
    this.progressUpdated = false;
  }

  private _markProgressUpdated() {
    this.progressUpdated = false;
    setTimeout(() => this.progressUpdated = true);
  }

  private static _buildEmptyProgress() {
    const progress = new TriggerFiredBundle();
    progress.percentage = -1;
    return progress;
  }

}

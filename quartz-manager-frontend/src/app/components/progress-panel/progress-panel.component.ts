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

  progress: TriggerFiredBundle = new TriggerFiredBundle();
  percentageStr: string;

  topicSubscription;
  private selectedTriggerKey: TriggerKey;

  constructor(
    private progressRxWebsocketService: ProgressRxWebsocketService
  ) { }

  @Input()
  set triggerKey(triggerKey: TriggerKey) {
    this.selectedTriggerKey = {...triggerKey} as TriggerKey;
    if (this.selectedTriggerKey && this.selectedTriggerKey.name) {
      this._subscribeToTheTopic(this.selectedTriggerKey);
    }
  }

  private _subscribeToTheTopic = (triggerKey: TriggerKey) => {
    if (this.topicSubscription) {
      this.topicSubscription.unsubscribe();
    }
    this.topicSubscription = this.progressRxWebsocketService.watch(`/topic/progress/${triggerKey.name}`)
      .pipe(map(msg => JSON.parse(msg.body)))
      .subscribe(this.onNewProgressMsg, (err) => {
        console.log(err);
        // TODO in case of 401
        // this.apiService.get('/quartz-manager/session/refresh');
      });
  };

  onNewProgressMsg = (receivedMsg) => {
      this.progress = receivedMsg;
      this.percentageStr = this.progress.percentage + '%';
  }

  ngOnInit() {
  }

  ngOnDestroy() {
    if (this.topicSubscription) {
      this.topicSubscription.unsubscribe();
    }
    this.topicSubscription.unsubscribe();
    this.topicSubscription = null;
  }

}

import {Subject} from 'rxjs';
import {ProgressPanelComponent} from './progress-panel.component';
import {TriggerKey} from '../../model/triggerKey.model';
import {jest} from '@jest/globals';

describe('ProgressPanelComponent', () => {

  it('should subscribe to the selected trigger progress topic', () => {
    const messages = new Subject<any>();
    const progressRxWebsocketService = {
      watch: jest.fn(() => messages.asObservable())
    };
    const component = new ProgressPanelComponent(progressRxWebsocketService as any);

    component.triggerKey = new TriggerKey('trigger-1', null);

    expect(progressRxWebsocketService.watch).toHaveBeenCalledWith('/topic/progress/trigger-1');

    messages.next({body: JSON.stringify({percentage: 75, timesTriggered: 3})});

    expect(component.progress.percentage).toEqual(75);
    expect(component.percentageStr).toEqual('75%');
  });

  it('should unsubscribe from the previous topic when the trigger changes', () => {
    const firstMessages = new Subject<any>();
    const secondMessages = new Subject<any>();
    const progressRxWebsocketService = {
      watch: jest.fn()
        .mockReturnValueOnce(firstMessages.asObservable())
        .mockReturnValueOnce(secondMessages.asObservable())
    };
    const component = new ProgressPanelComponent(progressRxWebsocketService as any);

    component.triggerKey = new TriggerKey('trigger-1', null);
    const firstSubscription = component.topicSubscription;
    jest.spyOn(firstSubscription, 'unsubscribe');

    component.triggerKey = new TriggerKey('trigger-2', null);

    expect(firstSubscription.unsubscribe).toHaveBeenCalled();
    expect(progressRxWebsocketService.watch).toHaveBeenCalledWith('/topic/progress/trigger-2');
  });

  it('should ignore destroy when no topic was selected', () => {
    const progressRxWebsocketService = {
      watch: jest.fn()
    };
    const component = new ProgressPanelComponent(progressRxWebsocketService as any);

    expect(() => component.ngOnDestroy()).not.toThrow();
  });

});

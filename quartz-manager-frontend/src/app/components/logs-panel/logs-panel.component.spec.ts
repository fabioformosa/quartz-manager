import {Subject} from 'rxjs';
import {LogsPanelComponent} from './logs-panel.component';
import {TriggerKey} from '../../model/triggerKey.model';
import {jest} from '@jest/globals';

describe('LogsPanelComponent', () => {

  it('should subscribe to the selected trigger logs topic', () => {
    const messages = new Subject<any>();
    const logsRxWebsocketService = {
      watch: jest.fn(() => messages.asObservable())
    };
    const component = new LogsPanelComponent(logsRxWebsocketService as any, null);

    component.triggerKey = new TriggerKey('trigger-1', null);

    expect(logsRxWebsocketService.watch).toHaveBeenCalledWith('/topic/logs/trigger-1');

    const logRecord = {
      date: new Date(),
      type: 'INFO',
      message: 'job completed',
      threadName: 'worker-1'
    };
    messages.next({body: JSON.stringify(logRecord)});

    expect(component.logs[0]).toEqual({
      time: logRecord.date.toISOString(),
      type: 'INFO',
      msg: 'job completed',
      threadName: 'worker-1'
    });
  });

  it('should unsubscribe from the previous topic when the trigger changes', () => {
    const firstMessages = new Subject<any>();
    const secondMessages = new Subject<any>();
    const logsRxWebsocketService = {
      watch: jest.fn()
        .mockReturnValueOnce(firstMessages.asObservable())
        .mockReturnValueOnce(secondMessages.asObservable())
    };
    const component = new LogsPanelComponent(logsRxWebsocketService as any, null);

    component.triggerKey = new TriggerKey('trigger-1', null);
    const firstSubscription = component.topicSubscription;
    jest.spyOn(firstSubscription, 'unsubscribe');

    component.triggerKey = new TriggerKey('trigger-2', null);

    expect(firstSubscription.unsubscribe).toHaveBeenCalled();
    expect(logsRxWebsocketService.watch).toHaveBeenCalledWith('/topic/logs/trigger-2');
  });

  it('should ignore destroy when no topic was selected', () => {
    const logsRxWebsocketService = {
      watch: jest.fn()
    };
    const component = new LogsPanelComponent(logsRxWebsocketService as any, null);

    expect(() => component.ngOnDestroy()).not.toThrow();
  });

});

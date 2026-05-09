import {Subject} from 'rxjs';
import {LogsPanelComponent} from './logs-panel.component';
import {TriggerKey} from '../../model/triggerKey.model';
import {jest} from '@jest/globals';

describe('LogsPanelComponent', () => {

  const ngZone = {run: jest.fn((fn: () => void) => fn())};

  beforeEach(() => ngZone.run.mockClear());

  it('should subscribe to the selected trigger logs topic', () => {
    const messages = new Subject<any>();
    const logsRxWebsocketService = {
      watch: jest.fn(() => messages.asObservable())
    };
    const component = new LogsPanelComponent(logsRxWebsocketService as any, null, ngZone as any);

    component.triggerKey = new TriggerKey('trigger-1', null);

    expect(logsRxWebsocketService.watch.mock.calls[0]).toEqual(['/topic/logs/trigger-1']);
    expect(component.selectedTriggerName).toEqual('trigger-1');
    expect(component.isWaitingForLogs()).toBeTruthy();

    const logRecord = {
      date: new Date(),
      type: 'INFO',
      message: 'job completed',
      threadName: 'worker-1'
    };
    messages.next({body: JSON.stringify(logRecord)});

    expect(ngZone.run).toHaveBeenCalled();
    expect(component.logs[0]).toEqual({
      time: logRecord.date.toISOString(),
      type: 'INFO',
      msg: 'job completed',
      threadName: 'worker-1'
    });
    expect(component.isWaitingForLogs()).toBeFalsy();
  });

  it('should unsubscribe from the previous topic when the trigger changes', () => {
    const firstMessages = new Subject<any>();
    const secondMessages = new Subject<any>();
    const logsRxWebsocketService = {
      watch: jest.fn()
        .mockReturnValueOnce(firstMessages.asObservable())
        .mockReturnValueOnce(secondMessages.asObservable())
    };
    const component = new LogsPanelComponent(logsRxWebsocketService as any, null, ngZone as any);

    component.triggerKey = new TriggerKey('trigger-1', null);
    const firstSubscription = component.topicSubscription;
    jest.spyOn(firstSubscription, 'unsubscribe');

    component.triggerKey = new TriggerKey('trigger-2', null);

    expect(firstSubscription.unsubscribe).toHaveBeenCalled();
    expect(logsRxWebsocketService.watch.mock.calls[1]).toEqual(['/topic/logs/trigger-2']);
  });

  it('should clear logs when the trigger changes', () => {
    const firstMessages = new Subject<any>();
    const secondMessages = new Subject<any>();
    const logsRxWebsocketService = {
      watch: jest.fn()
        .mockReturnValueOnce(firstMessages.asObservable())
        .mockReturnValueOnce(secondMessages.asObservable())
        .mockReturnValueOnce(firstMessages.asObservable())
    };
    const component = new LogsPanelComponent(logsRxWebsocketService as any, null, ngZone as any);

    component.triggerKey = new TriggerKey('trigger-1', null);
    firstMessages.next({body: JSON.stringify({date: new Date(), type: 'INFO', message: 'first log', threadName: 'worker-1'})});
    expect(component.logs.length).toEqual(1);

    component.triggerKey = new TriggerKey('trigger-2', null);
    expect(component.logs).toEqual([]);
    expect(component.selectedTriggerName).toEqual('trigger-2');
    expect(component.isWaitingForLogs()).toBeTruthy();

    secondMessages.next({body: JSON.stringify({date: new Date(), type: 'INFO', message: 'second log', threadName: 'worker-2'})});
    expect(component.logs.length).toEqual(1);

    component.triggerKey = new TriggerKey('trigger-1', null);
    expect(component.logs).toEqual([]);
    expect(component.selectedTriggerName).toEqual('trigger-1');
    expect(component.isWaitingForLogs()).toBeTruthy();
  });

  it('should clear logs when no trigger is selected', () => {
    const messages = new Subject<any>();
    const logsRxWebsocketService = {
      watch: jest.fn(() => messages.asObservable())
    };
    const component = new LogsPanelComponent(logsRxWebsocketService as any, null, ngZone as any);

    component.triggerKey = new TriggerKey('trigger-1', null);
    messages.next({body: JSON.stringify({date: new Date(), type: 'INFO', message: 'first log', threadName: 'worker-1'})});

    component.triggerKey = null;

    expect(component.logs).toEqual([]);
    expect(component.selectedTriggerName).toBeNull();
    expect(component.isWaitingForLogs()).toBeFalsy();
  });

  it('should ignore destroy when no topic was selected', () => {
    const logsRxWebsocketService = {
      watch: jest.fn()
    };
    const component = new LogsPanelComponent(logsRxWebsocketService as any, null, ngZone as any);

    expect(() => component.ngOnDestroy()).not.toThrow();
  });

});

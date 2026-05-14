import {Subject} from 'rxjs';
import {ProgressPanelComponent} from './progress-panel.component';
import {TriggerKey} from '../../model/triggerKey.model';
import {jest} from '@jest/globals';

describe('ProgressPanelComponent', () => {

  const ngZone = {run: jest.fn((fn: () => void) => fn())};

  beforeEach(() => ngZone.run.mockClear());

  it('should subscribe to the selected trigger progress topic', () => {
    jest.useFakeTimers();
    const messages = new Subject<any>();
    const progressRxWebsocketService = {
      watch: jest.fn(() => messages.asObservable())
    };
    const component = new ProgressPanelComponent(progressRxWebsocketService as any, ngZone as any);

    component.triggerKey = new TriggerKey('trigger-1', null);

    expect(progressRxWebsocketService.watch.mock.calls[0]).toEqual(['/topic/progress/trigger-1']);

    messages.next({body: JSON.stringify({percentage: 75, timesTriggered: 3})});
    jest.runOnlyPendingTimers();

    expect(ngZone.run).toHaveBeenCalled();
    expect(component.progress.percentage).toEqual(75);
    expect(component.percentageStr).toEqual('75%');
    expect(component.progressUpdated).toBeTruthy();
    jest.useRealTimers();
  });

  it('should unsubscribe from the previous topic when the trigger changes', () => {
    const firstMessages = new Subject<any>();
    const secondMessages = new Subject<any>();
    const progressRxWebsocketService = {
      watch: jest.fn()
        .mockReturnValueOnce(firstMessages.asObservable())
        .mockReturnValueOnce(secondMessages.asObservable())
    };
    const component = new ProgressPanelComponent(progressRxWebsocketService as any, ngZone as any);

    component.triggerKey = new TriggerKey('trigger-1', null);
    const firstSubscription = component.topicSubscription;
    jest.spyOn(firstSubscription, 'unsubscribe');

    component.triggerKey = new TriggerKey('trigger-2', null);

    expect(firstSubscription.unsubscribe).toHaveBeenCalled();
    expect(progressRxWebsocketService.watch.mock.calls[1]).toEqual(['/topic/progress/trigger-2']);
  });

  it('should reset progress when the trigger changes', () => {
    const firstMessages = new Subject<any>();
    const secondMessages = new Subject<any>();
    const progressRxWebsocketService = {
      watch: jest.fn()
        .mockReturnValueOnce(firstMessages.asObservable())
        .mockReturnValueOnce(secondMessages.asObservable())
        .mockReturnValueOnce(firstMessages.asObservable())
    };
    const component = new ProgressPanelComponent(progressRxWebsocketService as any, ngZone as any);

    component.triggerKey = new TriggerKey('trigger-1', null);
    firstMessages.next({body: JSON.stringify({percentage: 75, timesTriggered: 3})});
    expect(component.progress.percentage).toEqual(75);

    component.triggerKey = new TriggerKey('trigger-2', null);
    expect(component.progress.percentage).toEqual(-1);
    expect(component.percentageStr).toBeNull();
    expect(component.progressUpdated).toBeFalsy();

    secondMessages.next({body: JSON.stringify({percentage: 20, timesTriggered: 1})});
    expect(component.progress.percentage).toEqual(20);

    component.triggerKey = new TriggerKey('trigger-1', null);
    expect(component.progress.percentage).toEqual(-1);
  });

  it('should reset progress when no trigger is selected', () => {
    const messages = new Subject<any>();
    const progressRxWebsocketService = {
      watch: jest.fn(() => messages.asObservable())
    };
    const component = new ProgressPanelComponent(progressRxWebsocketService as any, ngZone as any);

    component.triggerKey = new TriggerKey('trigger-1', null);
    messages.next({body: JSON.stringify({percentage: 75, timesTriggered: 3})});

    component.triggerKey = null;

    expect(component.progress.percentage).toEqual(-1);
    expect(component.percentageStr).toBeNull();
    expect(component.progressUpdated).toBeFalsy();
  });

  it('should ignore destroy when no topic was selected', () => {
    const progressRxWebsocketService = {
      watch: jest.fn()
    };
    const component = new ProgressPanelComponent(progressRxWebsocketService as any, ngZone as any);

    expect(() => component.ngOnDestroy()).not.toThrow();
  });

});

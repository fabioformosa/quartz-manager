import {TriggerService} from './trigger.service';
import {TriggerKey} from '../model/triggerKey.model';
import {jest} from '@jest/globals';

describe('TriggerService', () => {
  let apiService: any;
  let triggerService: TriggerService;

  beforeEach(() => {
    apiService = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      delete: jest.fn()
    };
    triggerService = new TriggerService(apiService);
  });

  it('uses grouped trigger lifecycle endpoints', () => {
    const triggerKey = new TriggerKey('sampleTrigger', 'DEFAULT');

    triggerService.getTrigger(triggerKey);
    triggerService.pauseTrigger(triggerKey);
    triggerService.resumeTrigger(triggerKey);
    triggerService.unscheduleTrigger(triggerKey);

    expect(apiService.get).toHaveBeenCalledWith('/quartz-manager/triggers/DEFAULT/sampleTrigger');
    expect(apiService.post).toHaveBeenCalledWith('/quartz-manager/triggers/DEFAULT/sampleTrigger/pause', {});
    expect(apiService.post).toHaveBeenCalledWith('/quartz-manager/triggers/DEFAULT/sampleTrigger/resume', {});
    expect(apiService.delete).toHaveBeenCalledWith('/quartz-manager/triggers/DEFAULT/sampleTrigger');
  });

  it('uses generic trigger create and update endpoints', () => {
    const command: any = {triggerType: 'CRON', cronExpression: '0 0/5 * * * ?'};

    triggerService.saveTrigger('OPS', 'cronTrigger', command);
    triggerService.updateTrigger('OPS', 'cronTrigger', command);

    expect(apiService.post).toHaveBeenCalledWith('/quartz-manager/triggers/OPS/cronTrigger', command);
    expect(apiService.put).toHaveBeenCalledWith('/quartz-manager/triggers/OPS/cronTrigger', command);
  });
});

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
});

import {SchedulerService} from './scheduler.service';
import {SimpleTriggerCommand} from '../model/simple-trigger.command';
import {jest} from '@jest/globals';

describe('SchedulerService', () => {
  let apiService: any;
  let schedulerService: SchedulerService;

  beforeEach(() => {
    apiService = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn()
    };
    schedulerService = new SchedulerService(apiService);
  });

  it('uses POST scheduler lifecycle endpoints', () => {
    schedulerService.startScheduler();
    schedulerService.standbyScheduler();
    schedulerService.resumeScheduler();
    schedulerService.shutdownScheduler();

    expect(apiService.post).toHaveBeenCalledWith('/quartz-manager/scheduler/start', {});
    expect(apiService.post).toHaveBeenCalledWith('/quartz-manager/scheduler/standby', {});
    expect(apiService.post).toHaveBeenCalledWith('/quartz-manager/scheduler/resume', {});
    expect(apiService.post).toHaveBeenCalledWith('/quartz-manager/scheduler/shutdown', {});
  });

  it('uses grouped simple trigger endpoints', () => {
    const command = new SimpleTriggerCommand();
    command.triggerGroup = 'DEFAULT';
    command.triggerName = 'sampleTrigger';

    schedulerService.getSimpleTriggerConfig(command.triggerName, command.triggerGroup);
    schedulerService.saveSimpleTriggerConfig(command);
    schedulerService.updateSimpleTriggerConfig(command);

    expect(apiService.get).toHaveBeenCalledWith('/quartz-manager/simple-triggers/DEFAULT/sampleTrigger');
    expect(apiService.post).toHaveBeenCalledWith('/quartz-manager/simple-triggers/DEFAULT/sampleTrigger', command);
    expect(apiService.put).toHaveBeenCalledWith('/quartz-manager/simple-triggers/DEFAULT/sampleTrigger', command);
  });
});

import JobService from './job.service';
import {ScheduledJob} from '../model/scheduled-job.model';
import {jest} from '@jest/globals';

describe('JobService', () => {
  let apiService: any;
  let jobService: JobService;

  beforeEach(() => {
    apiService = {
      get: jest.fn(),
      post: jest.fn(),
      delete: jest.fn()
    };
    jobService = new JobService(apiService);
  });

  it('uses job class and scheduled job endpoints', () => {
    const job = new ScheduledJob();
    job.jobKeyDTO = {group: 'DEFAULT', name: 'sampleJob'};

    jobService.fetchJobs();
    jobService.fetchScheduledJobs();
    jobService.triggerJob(job);
    jobService.deleteJob(job);

    expect(apiService.get).toHaveBeenCalledWith('/quartz-manager/job-classes');
    expect(apiService.get).toHaveBeenCalledWith('/quartz-manager/jobs');
    expect(apiService.post).toHaveBeenCalledWith('/quartz-manager/jobs/DEFAULT/sampleJob/trigger', {});
    expect(apiService.delete).toHaveBeenCalledWith('/quartz-manager/jobs/DEFAULT/sampleJob');
  });
});

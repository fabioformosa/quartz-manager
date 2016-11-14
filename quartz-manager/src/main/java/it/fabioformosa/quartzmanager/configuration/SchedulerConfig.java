package it.fabioformosa.quartzmanager.configuration;

import java.io.IOException;
import java.util.Properties;

import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import it.fabioformosa.quartzmanager.jobs.SampleJob;
import it.fabioformosa.quartzmanager.scheduler.AutowiringSpringBeanJobFactory;
import it.fabioformosa.quartzmanager.scheduler.TriggerMonitor;
import it.fabioformosa.quartzmanager.scheduler.TriggerMonitorImpl;

@Configuration
@ConditionalOnProperty(name = "quartz.enabled")
public class SchedulerConfig {

	private static JobDetailFactoryBean createJobDetail(Class jobClass) {
		JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
		factoryBean.setJobClass(jobClass);
		factoryBean.setDurability(false);
		return factoryBean;
	}

	private static SimpleTriggerFactoryBean createTrigger(JobDetail jobDetail, long pollFrequencyMs,
			int repeatCount) {
		SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
		factoryBean.setJobDetail(jobDetail);
		factoryBean.setStartDelay(0L);
		factoryBean.setRepeatInterval(pollFrequencyMs);
		factoryBean.setRepeatCount(repeatCount);
		factoryBean
				.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);// in case of misfire, ignore all missed triggers and continue
		return factoryBean;
	}

	@Bean(name = "triggerMonitor")
	public TriggerMonitor createTriggerMonitor(@Qualifier("jobTrigger") Trigger trigger) {
		TriggerMonitor triggerMonitor = new TriggerMonitorImpl();
		triggerMonitor.setTrigger(trigger);
		return triggerMonitor;
	}

	@Bean
	public JobDetailFactoryBean jobDetail() {
		return createJobDetail(SampleJob.class);
	}

	@Bean
	public JobFactory jobFactory(ApplicationContext applicationContext) {
		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		jobFactory.setApplicationContext(applicationContext);
		return jobFactory;
	}

	@Bean
	public Properties quartzProperties() throws IOException {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
		propertiesFactoryBean.afterPropertiesSet();
		return propertiesFactoryBean.getObject();
	}

	@Bean(name = "jobTrigger")
	public SimpleTriggerFactoryBean sampleJobTrigger(@Qualifier("jobDetail") JobDetail jobDetail,
			@Value("${job.frequency}") long frequency, @Value("${job.repeatCount}") int repeatCount) {
		return createTrigger(jobDetail, frequency, repeatCount);
	}

	@Bean(name = "scheduler")
	public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory,
			@Qualifier("jobTrigger") Trigger sampleJobTrigger) throws IOException {
		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		factory.setJobFactory(jobFactory);
		factory.setQuartzProperties(quartzProperties());
		factory.setTriggers(sampleJobTrigger);
		factory.setAutoStartup(false);
		return factory;
	}
}
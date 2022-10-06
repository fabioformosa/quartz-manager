package it.fabioformosa.quartzmanager.configuration;

import it.fabioformosa.quartzmanager.common.properties.QuartzModuleProperties;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.quartz.Scheduler;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.ArrayList;
import java.util.List;

class SchedulerConfigTest {

  public static final String TEST_SCHEDULER_NAME = "foo";
  public static final String QUARTZ_SCHEDULER_DEFAULT_NAME = "QuartzScheduler";

  @Test
  void givenASchedulerName_whenTheSchedulerIsInstatiated_thenTheSchedulerHasThatName() throws Exception {
    QuartzModuleProperties quartzModuleProperties = new QuartzModuleProperties();
    quartzModuleProperties.getProperties().put("org.quartz.scheduler.instanceName", TEST_SCHEDULER_NAME);
    List<QuartzModuleProperties> quartzModulePropertiesList = new ArrayList<>();
    quartzModulePropertiesList.add(quartzModuleProperties);
    SchedulerConfig schedulerConfig = new SchedulerConfig(quartzModulePropertiesList);
    GenericApplicationContext applicationContext = new GenericApplicationContext();
    applicationContext.refresh();
    SchedulerFactoryBean schedulerFactoryBean = schedulerConfig.schedulerFactoryBean(schedulerConfig.jobFactory(applicationContext));

    schedulerFactoryBean.afterPropertiesSet();
    Scheduler scheduler = schedulerFactoryBean.getScheduler();
    Assertions.assertThat(scheduler.getSchedulerName()).isEqualTo(TEST_SCHEDULER_NAME);
  }

  @Test
  void givenNoSchedulerName_whenTheSchedulerIsInstatiated_thenTheSchedulerHasTheDefaultName() throws Exception {
    QuartzModuleProperties quartzModuleProperties = new QuartzModuleProperties();
    List<QuartzModuleProperties> quartzModulePropertiesList = new ArrayList<>();
    quartzModulePropertiesList.add(quartzModuleProperties);
    SchedulerConfig schedulerConfig = new SchedulerConfig(quartzModulePropertiesList);
    GenericApplicationContext applicationContext = new GenericApplicationContext();
    applicationContext.refresh();
    SchedulerFactoryBean schedulerFactoryBean = schedulerConfig.schedulerFactoryBean(schedulerConfig.jobFactory(applicationContext));

    schedulerFactoryBean.afterPropertiesSet();
    Scheduler scheduler = schedulerFactoryBean.getScheduler();
    Assertions.assertThat(scheduler.getSchedulerName()).isEqualTo(QUARTZ_SCHEDULER_DEFAULT_NAME);
  }

}

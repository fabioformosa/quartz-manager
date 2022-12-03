package it.fabioformosa.quartzmanager.api.configuration;

import it.fabioformosa.quartzmanager.api.common.properties.QuartzModuleProperties;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.quartz.Scheduler;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

class SchedulerConfigTest {

  public static final String TEST_SCHEDULER_NAME = "foo";
  public static final String QUARTZ_SCHEDULER_DEFAULT_NAME = "QuartzScheduler";

  @Test
  void givenASchedulerName_whenTheSchedulerIsInstantiated_thenTheSchedulerHasThatName() throws Exception {
    List<QuartzModuleProperties> quartzModulePropertiesList = getQuartzModulePropertiesWithASchedulerName(TEST_SCHEDULER_NAME);
    SchedulerConfig schedulerConfig = new SchedulerConfig(quartzModulePropertiesList);
    GenericApplicationContext applicationContext = new GenericApplicationContext();
    applicationContext.refresh();
    SchedulerFactoryBean schedulerFactoryBean = schedulerConfig.schedulerFactoryBean(schedulerConfig.jobFactory(applicationContext), null);

    schedulerFactoryBean.afterPropertiesSet();
    Scheduler scheduler = schedulerFactoryBean.getScheduler();
    Assertions.assertThat(scheduler.getSchedulerName()).isEqualTo(TEST_SCHEDULER_NAME);
  }

  private static List<QuartzModuleProperties> getQuartzModulePropertiesWithASchedulerName(String schedulerName) {
    QuartzModuleProperties quartzModuleProperties = new QuartzModuleProperties();
    quartzModuleProperties.getProperties().put("org.quartz.scheduler.instanceName", schedulerName);
    List<QuartzModuleProperties> quartzModulePropertiesList = new ArrayList<>();
    quartzModulePropertiesList.add(quartzModuleProperties);
    return quartzModulePropertiesList;
  }

  @Test
  void givenNoSchedulerName_whenTheSchedulerIsInstantiated_thenTheSchedulerHasTheDefaultName() throws Exception {
    QuartzModuleProperties quartzModuleProperties = new QuartzModuleProperties();
    List<QuartzModuleProperties> quartzModulePropertiesList = new ArrayList<>();
    quartzModulePropertiesList.add(quartzModuleProperties);
    SchedulerConfig schedulerConfig = new SchedulerConfig(quartzModulePropertiesList);
    GenericApplicationContext applicationContext = new GenericApplicationContext();
    applicationContext.refresh();
    SchedulerFactoryBean schedulerFactoryBean = schedulerConfig.schedulerFactoryBean(schedulerConfig.jobFactory(applicationContext), null);

    schedulerFactoryBean.afterPropertiesSet();
    Scheduler scheduler = schedulerFactoryBean.getScheduler();
    Assertions.assertThat(scheduler.getSchedulerName()).isEqualTo(QUARTZ_SCHEDULER_DEFAULT_NAME);
  }

  @Test
  void givenAManagedProperties_whenTheSchedulerIsInstantiated_thenTheManagedPropsHavePriority() throws Exception {
    List<QuartzModuleProperties> quartzModulePropertiesList = getQuartzModulePropertiesWithASchedulerName(TEST_SCHEDULER_NAME);
    SchedulerConfig schedulerConfig = new SchedulerConfig(quartzModulePropertiesList);
    GenericApplicationContext applicationContext = new GenericApplicationContext();
    applicationContext.refresh();

    Properties managedProps = new Properties();
    String overridden_scheduler_name = "OVERRIDDEN_SCHEDULER_NAME";
    managedProps.put("org.quartz.scheduler.instanceName", overridden_scheduler_name);
    SchedulerFactoryBean schedulerFactoryBean = schedulerConfig.schedulerFactoryBean(schedulerConfig.jobFactory(applicationContext), managedProps);

    schedulerFactoryBean.afterPropertiesSet();
    Scheduler scheduler = schedulerFactoryBean.getScheduler();
    Assertions.assertThat(scheduler.getSchedulerName()).isEqualTo(overridden_scheduler_name);
  }

  @Test
  void givenAnEmptyManagedPropFile_whenSchedulerConfigRuns_thenItReturnsAnEmptyPropList() throws IOException {
    List<QuartzModuleProperties> quartzModulePropertiesList = getQuartzModulePropertiesWithASchedulerName(TEST_SCHEDULER_NAME);
    SchedulerConfig schedulerConfig = new SchedulerConfig(quartzModulePropertiesList);
    GenericApplicationContext applicationContext = new GenericApplicationContext();
    applicationContext.refresh();

    Properties properties = schedulerConfig.quartzProperties();
    Assertions.assertThat(properties).isEmpty();
  }

}

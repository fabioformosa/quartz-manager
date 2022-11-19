package it.fabioformosa.quartzmanager.api.configuration;

import it.fabioformosa.quartzmanager.api.common.properties.QuartzModuleProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "quartz-manager.quartz.enabled", matchIfMissing = true)
public class QuartzDefaultPropertiesConfig {

  protected static final String QUARTZ_MANAGER_SCHEDULER_DEFAULT_NAME = "quartz-manager-scheduler";

  @Bean("quartzDefaultProperties")
  public QuartzModuleProperties defaultApiQuartzProps() {
    QuartzModuleProperties quartzModuleProperties = new QuartzModuleProperties();
    quartzModuleProperties.getProperties().setProperty("org.quartz.scheduler.instanceName", QUARTZ_MANAGER_SCHEDULER_DEFAULT_NAME);
    quartzModuleProperties.getProperties().setProperty("org.quartz.threadPool.threadCount", "1");
    return quartzModuleProperties;
  }

}

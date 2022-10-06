package it.fabioformosa.quartzmanager.api.configuration;

import it.fabioformosa.quartzmanager.api.scheduler.AutowiringSpringBeanJobFactory;
import it.fabioformosa.quartzmanager.api.common.properties.QuartzModuleProperties;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

@ComponentScan(basePackages = {"it.fabioformosa.quartzmanager.api"})
@Configuration
@ConditionalOnProperty(name = "quartz.enabled", matchIfMissing = true)
public class SchedulerConfig {

  private final List<QuartzModuleProperties> quartzModuleProperties;

  @Autowired(required = false)
  public SchedulerConfig(List<QuartzModuleProperties> quartzModuleProperties) {
    this.quartzModuleProperties = quartzModuleProperties;
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

  @Bean(name = "scheduler")
  public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory) throws IOException {
    SchedulerFactoryBean factory = new SchedulerFactoryBean();
    factory.setJobFactory(jobFactory);
    Properties mergedProperties = new Properties();
    quartzModuleProperties.stream().forEach(prop -> mergedProperties.putAll(prop.getProperties()));
    mergedProperties.putAll(quartzProperties());
    factory.setQuartzProperties(mergedProperties);
    factory.setAutoStartup(false);
    return factory;
  }
}

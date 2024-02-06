package it.fabioformosa.quartzmanager.api.persistence;

import it.fabioformosa.quartzmanager.api.common.properties.QuartzModuleProperties;
import liquibase.integration.spring.SpringLiquibase;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;

import javax.sql.DataSource;

@Configuration
public class PersistenceConfig {

    @Value("${quartz-manager.persistence.quartz.datasource.url}")
    private String quartzDatasourceUrl;

    @Value("${quartz-manager.persistence.quartz.datasource.user}")
    private String quartzDatasourceUser;

    @Value("${quartz-manager.persistence.quartz.datasource.password}")
    private String quartzDatasourcePassword;

    @Data
    public class PersistenceDatasourceProps {
        private String changeLog;
        private String contexts;
    }

    @Bean
    public SpringLiquibase liquibase(PersistenceDatasourceProps persistenceDatasourceProps, DataSource quartzManagerDatasource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setContexts(persistenceDatasourceProps.getContexts());
        liquibase.setChangeLog(persistenceDatasourceProps.getChangeLog());
        liquibase.setDataSource(quartzManagerDatasource);
        liquibase.setDropFirst(false);
        return liquibase;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.liquibase")
    public PersistenceDatasourceProps persistenceDatasourceProps() {
        return new PersistenceDatasourceProps();
    }

    @Bean("quartzPersistenceProperties")
    public QuartzModuleProperties persistenceQuartzProps(QuartzPersistencePropConfig quartzPersistencePropConfig) {
      QuartzModuleProperties quartzModuleProperties = new QuartzModuleProperties();
      quartzModuleProperties.setProperties(quartzPersistencePropConfig.getProperties());
      quartzModuleProperties.getProperties().setProperty("org.quartz.dataSource.quartzDataSource.URL", quartzDatasourceUrl);
      quartzModuleProperties.getProperties().setProperty("org.quartz.dataSource.quartzDataSource.user", quartzDatasourceUser);
      quartzModuleProperties.getProperties().setProperty("org.quartz.dataSource.quartzDataSource.password", quartzDatasourcePassword);
      return quartzModuleProperties;
    }

    @Primary
    @Bean
    public DataSource quartzManagerDatasource() {
        return DataSourceBuilder.create()
                .url(quartzDatasourceUrl)
                .driverClassName("org.postgresql.Driver")
                .username(quartzDatasourceUser)
                .password(quartzDatasourcePassword)
                .build();
    }

}

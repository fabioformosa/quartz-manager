package it.fabioformosa.quartzmanager.persistence;

import it.fabioformosa.quartzmanager.common.properties.QuartzModuleProperties;
import liquibase.integration.spring.SpringLiquibase;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:quartz-manager-application-persistence.properties")
public class PersistenceConfig {

    @Data
    public class PersistenceDatasourceProps {
        private String url;
        private String changeLog;
        private String contexts;
        private String user;
        private String password;
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
    @ConfigurationProperties(prefix = "spring.quartz")
    public QuartzModuleProperties persistenceQuartzProps() {
        return new QuartzModuleProperties();
    }

    @Primary
    @Bean
    public DataSource quartzManagerDatasource(PersistenceDatasourceProps persistenceDatasourceProps) {
        return DataSourceBuilder.create()
                .url(persistenceDatasourceProps.getUrl())
                .driverClassName("org.postgresql.Driver")
                .username(persistenceDatasourceProps.getUser())
                .password(persistenceDatasourceProps.getPassword())
                .build();
    }

}

package org.springframework.data.ebean.repository.sample;

import io.ebean.EbeanServer;
import io.ebean.EbeanServerFactory;
import io.ebean.config.CurrentUserProvider;
import io.ebean.config.ServerConfig;
import io.ebean.spring.txn.SpringJdbcTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.ebean.repository.config.EnableEbeanRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * @author Xuegui Yuan
 */
@Configuration
@EnableEbeanRepositories(value = "org.springframework.data.ebean.repository.sample")
@EnableTransactionManagement
public class SampleConfig {
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Bean
    @Primary
    public ServerConfig defaultEbeanServerConfig() {
        ServerConfig config = new ServerConfig();

        config.setDataSource(dataSource());
        config.addPackage("org.springframework.data.ebean.domain.sample");
        config.setExternalTransactionManager(new SpringJdbcTransactionManager());

        config.loadFromProperties();
        config.setDefaultServer(true);
        config.setRegister(true);
        config.setAutoCommitMode(false);
        config.setExpressionNativeIlike(true);

        config.setCurrentUserProvider(new CurrentUserProvider() {
            @Override
            public Object currentUser() {
                return "test"; // just for test, can rewrite to get the currentUser from threadLocal
            }
        });

        return config;
    }

    @Bean
    @Primary
    public EbeanServer defaultEbeanServer(ServerConfig defaultEbeanServerConfig) {
        return EbeanServerFactory.create(defaultEbeanServerConfig);
    }
}

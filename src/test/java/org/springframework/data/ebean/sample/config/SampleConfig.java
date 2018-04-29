package org.springframework.data.ebean.sample.config;

import io.ebean.EbeanServer;
import io.ebean.EbeanServerFactory;
import io.ebean.config.CurrentUserProvider;
import io.ebean.config.ServerConfig;
import io.ebean.spring.txn.SpringJdbcTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.ebean.querychannel.EbeanQueryChannelService;
import org.springframework.data.ebean.querychannel.QueryChannelService;
import org.springframework.data.ebean.repository.config.EnableEbeanRepositories;
import org.springframework.data.ebean.sample.domain.UserDomainService;
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
@EnableEbeanRepositories(value = "org.springframework.data.ebean.sample")
@EnableTransactionManagement
public class SampleConfig {
  @Bean
  public PlatformTransactionManager transactionManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  @Bean
  public QueryChannelService ebeanQueryChannelService(EbeanServer ebeanServer) {
    return new EbeanQueryChannelService(ebeanServer);
  }

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Bean
  @Primary
  public ServerConfig defaultEbeanServerConfig() {
    ServerConfig config = new ServerConfig();

    config.setDataSource(dataSource());
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
  public DataSource dataSource() {
    return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
  }

  @Bean
  @Primary
  public EbeanServer defaultEbeanServer(ServerConfig defaultEbeanServerConfig) {
    return EbeanServerFactory.create(defaultEbeanServerConfig);
  }

  @Bean
  public UserDomainService userDomainService() {
    return new UserDomainService();
  }
}

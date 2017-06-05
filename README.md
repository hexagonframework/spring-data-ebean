# spring-data-ebean
> Ebean implementation for spring data.

The primary goal of the [Spring Data](http://projects.spring.io/spring-data) project is to make it easier to build Spring-powered applications that use data access technologies. This module deals with enhanced support for Ebean ORM based data access layers.

## Features ##

* Ebean implementation of CRUD methods for JPA Entities
* Dynamic query generation from query method names
* Transparent triggering of Ebean Query by query methods
* Implementation domain base classes providing basic properties
* Support for transparent auditing (created, last changed)
* Possibility to integrate custom repository code
* Easy Spring integration with custom namespace

## Quick Start ##

Download the jar through Maven:

```xml
<dependency>
  <groupId>org.springframework.data</groupId>
  <artifactId>spring-data-ebean</artifactId>
  <version>${version}.RELEASE</version>
</dependency>
```

Also include ebean jars and ebean properties.

The simple Spring Data Ebean configuration with Java-Config looks like this: 
```java
@Configuration
@EnableEbeanRepositories("org.springframework.data.ebean.repository.config")
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
        config.addPackage("org.springframework.data.ebean.domain.config");
        config.setExternalTransactionManager(new SpringJdbcTransactionManager());

        config.setDefaultServer(true);
        config.setAutoCommitMode(false);
        config.setExpressionNativeIlike(true);

        return config;
    }

    @Bean
    @Primary
    public EbeanServer defaultEbeanServer(ServerConfig defaultEbeanServerConfig) {
        return EbeanServerFactory.create(defaultEbeanServerConfig);
    }
}
```

Create an entity:

```java
@Entity
public class User {

  @Id
  @GeneratedValue
  private Integer id;
  private String firstname;
  private String lastname;
  @Column(nullable = false, unique = true) private String emailAddress;
       
  // Getters and setters
  // (Firstname, Lastname,emailAddress)-constructor and noargs-constructor
  // equals / hashcode
}
```

Create a repository interface in `io.hexagon.demo.domain.repository`:

```java
public interface UserRepository extends CrudRepository<User, Long> {
  List<User> findByLastname(String lastname);
}
```

Write a test client

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class UserRepositoryIntegrationTest {
     
  @Autowired UserRepository repository;
     
  @Test
  public void sampleTestCase() {
    User user = new User("Xuegui", "Yuan", "yuanxuegui@163.com");
    user = repository.save(user);

    List<User> result = (List<User>) repository.findAll();
    assertEquals(1, result.size());
    assertEquals("Yuan", result.get(0).getLastname());
    assertThat(result, hasItem(user));
  }
}
```

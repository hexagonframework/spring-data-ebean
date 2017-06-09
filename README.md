# spring-data-ebean
[![Build Status](https://travis-ci.org/hexagonframework/spring-data-ebean.svg?branch=master)](https://travis-ci.org/hexagonframework/spring-data-ebean) [![Gitter chat](https://badges.gitter.im/hexagonframework/spring-data-ebean/gitter.png)](https://gitter.im/hexagonframework/spring-data-ebean)
> Ebean implementation for spring data.

[简体中文](README_zh.md)

The primary goal of the [Spring Data](http://projects.spring.io/spring-data) project is to make it easier to build Spring-powered applications that use data access technologies. This module deals with enhanced support for [Ebean ORM](https://ebean-orm.github.io) based data access layers.

## Features ##

* Ebean implementation of CRUD methods for JPA Entities
* Dynamic query generation from query method names and annotation
* Transparent triggering of Ebean Query by query methods
* Implementation domain base classes providing basic properties
* Support for transparent auditing (created, last changed)
* Possibility to integrate custom repository code
* Easy Spring integration with custom namespace

## Maven cental links

[Maven central - spring-data-ebean](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.github.hexagonframework.data%22%20AND%20a%3A%22spring-data-ebean%22 "maven central spring-data-ebean")

## Current version

[![Maven Central : ebean](https://maven-badges.herokuapp.com/maven-central/io.github.hexagonframework.data/spring-data-ebean/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.hexagonframework.data/spring-data-ebean) - spring-data-ebean

## Quick Start ##

Download the jar through Maven:

```xml
<dependency>
  <groupId>io.github.hexagonframework.data</groupId>
  <artifactId>spring-data-ebean</artifactId>
  <version>{current version}</version>
</dependency>
```

The simple Spring Data Ebean configuration with Java-Config looks like this: 
```java
@Configuration
@EnableEbeanRepositories("org.springframework.data.ebean.repository.sample")
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

Create a repository interface in `org.springframework.data.ebean.repository.sample`:

```java
public interface UserRepository extends EbeanRepository<User, Long> {
    @EbeanQuery("where emailAddress = :emailAddress order by id desc")
    User findUserByEmailAddressEqualsOql(@Param("emailAddress") String emailAddress);

    @EbeanQuery("select (firstname,lastname,address) fetch manager (lastname) where lastname = :lastname order by id desc")
    List<User> findByLastnameOql(@Param("lastname") String lastname);

    @EbeanQuery(nativeQuery = true, value = "select * from user where email_address = :emailAddress order by id desc")
    User findUserByEmailAddressEquals(@Param("emailAddress") String emailAddress);

    @EbeanQuery(nativeQuery = true, value = "select * from user where lastname = :lastname order by id desc")
    List<User> findUsersByLastnameEquals(@Param("lastname") String lastname);

    @EbeanQuery(nativeQuery = true, value = "update user set email_address = :newEmail where email_address = :oldEmail")
    @EbeanModifying
    int changeUserEmailAddress(@Param("oldEmail") String oldEmail, @Param("newEmail") String newEmail);

    @EbeanQuery("delete from user where emailAddress = :emailAddress")
    @EbeanModifying
    int deleteUserByEmailAddressOql(@Param("emailAddress") String emailAddress);

    @EbeanQuery(nativeQuery = true, value = "delete from user where email_address = :emailAddress")
    @EbeanModifying
    int deleteUserByEmailAddress(@Param("emailAddress") String emailAddress);

    @EbeanQuery(name = "withManagerById")
    List<User> findByLastnameNamedOql(@Param("lastname") String lastname);
}
```

Create a named query config in `resources/ebean.xml`:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ebean xmlns="http://ebean-orm.github.io/xml/ns/ebean">
    <entity class="org.springframework.data.ebean.domain.sample.User">
        <named-query name="withManagerById">
            <query>
                select (firstname,lastname,address)
                fetch manager (lastname)
                where lastname = :lastname order by id desc
            </query>
        </named-query>
        <raw-sql name="byEmailAddressEquals">
            <query>
                select * from user
                where email_address = :emailAddress
                order by id desc
            </query>
        </raw-sql>
    </entity>
</ebean>
```

Write a test client:

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SampleConfig.class)
public class UserRepositoryIntegrationTest {

    @Autowired
    UserRepository repository;

    @Test
    public void sampleTestCase() {
        User user = new User("Xuegui", "Yuan", "yuanxuegui@163.com");
        user.setAge(29);
        user = repository.save(user);

        List<User> result1 = (List<User>) repository.findAll();
        result1.forEach(it -> System.out.println(it));
        assertEquals(1, result1.size());
        assertEquals("Yuan", result1.get(0).getLastname());
        assertThat(result1, hasItem(user));

        List<User> result2  = repository.findByLastnameOql("Yuan");
        assertEquals(1, result2.size());
        assertEquals("Yuan", result2.get(0).getLastname());
        assertThat(result2, hasItem(user));

        List<User> result3 = repository.findUsersByLastnameEquals("Yuan");
        assertEquals(1, result3.size());
        assertEquals("Yuan", result3.get(0).getLastname());

        User result4 = repository.findUserByEmailAddressEqualsOql("yuanxuegui@163.com");
        assertEquals("yuanxuegui@163.com", result4.getEmailAddress());

        User result5 = repository.findUserByEmailAddressEquals("yuanxuegui@163.com");
        assertEquals("yuanxuegui@163.com", result5.getEmailAddress());

        int result6 = repository.changeUserEmailAddress("yuanxuegui@163.com", "yuanxuegui@126.com");
        assertEquals(1, result6);

        List<User> result7  = repository.findByLastnameOql("Yuan");
        assertEquals("yuanxuegui@126.com", result7.get(0).getEmailAddress());

        int result8 = repository.deleteUserByEmailAddress("yuanxuegui@126.com");
        assertEquals(1, result8);

        User result9 = repository.findUserByEmailAddressEquals("yuanxuegui@126.com");
        assertNull(result9);

        user = new User("Xuegui", "Yuan", "yuanxuegui@163.com");
        user.setAge(29);
        user = repository.save(user);

        User result10 = repository.findUserByEmailAddressEquals("yuanxuegui@163.com");
        assertNotNull(result10);

        int result11 = repository.deleteUserByEmailAddressOql("yuanxuegui@163.com");
        assertEquals(1, result11);

        User result12 = repository.findUserByEmailAddressEquals("yuanxuegui@163.com");
        assertNull(result12);
    }
}
```

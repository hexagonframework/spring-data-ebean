# spring-data-ebean
> Ebean implementation for spring data.

[![Build Status](https://travis-ci.org/hexagonframework/spring-data-ebean.svg?branch=master)](https://travis-ci.org/hexagonframework/spring-data-ebean) [![Gitter chat](https://badges.gitter.im/hexagonframework/spring-data-ebean/gitter.png)](https://gitter.im/hexagonframework/spring-data-ebean)
[![Maven Central : ebean](https://maven-badges.herokuapp.com/maven-central/io.github.hexagonframework.data/spring-data-ebean/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.hexagonframework.data/spring-data-ebean) 
 
[Maven cental link](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.github.hexagonframework.data%22%20AND%20a%3A%22spring-data-ebean%22 "maven central spring-data-ebean")


[简体中文](README_zh.md)

The primary goal of the [Spring Data](http://projects.spring.io/spring-data) project is to make it easier to build Spring-powered applications that use data access technologies. This module deals with enhanced support for [Ebean ORM](https://ebean-orm.github.io) based data access layers.

## Features ##

* Ebean implementation of CRUD methods for JPA Entities
* Dynamic query generation from query method names and annotation
* Support query channel service
* Transparent triggering of Ebean Query by query methods
* Implementation domain base classes providing basic properties
* Support for transparent auditing (created, last changed)
* Possibility to integrate custom repository code
* Easy Spring integration with custom namespace

## Why choose [Ebean ORM](https://ebean-orm.github.io)

[CLICK HERE TO SEE](http://ebean-orm.github.io/architecture/compare-jpa)

## Quick Start ##

Create maven project，recommend to use spring boot to build web project.

If using spring-boot-starter-data-ebean, see example [spring-boot-data-ebean-samples](https://github.com/hexagonframework/spring-boot-data-ebean-samples)

else following:
download the jar through Maven:

```xml
<dependency>
  <groupId>io.github.hexagonframework.data</groupId>
  <artifactId>spring-data-ebean</artifactId>
  <version>{current version}</version>
</dependency>
```

If using maven to compile, package, run，should add:

```xml
<build>
    <plugins>
      <plugin>
        <groupId>io.repaint.maven</groupId>
        <artifactId>tiles-maven-plugin</artifactId>
        <version>2.8</version>
        <extensions>true</extensions>
        <configuration>
          <tiles>
            <tile>org.avaje.tile:java-compile:1.1</tile>
            <tile>io.ebean.tile:enhancement:5.1</tile>
          </tiles>
        </configuration>
      </plugin>
    </plugins>
  </build>
```

If run with ide, should install, enable ebean enhancement plugin.

The simple Spring Data Ebean configuration with Java-Config looks like this: 
```java
@Configuration
@EnableEbeanRepositories(value = "org.springframework.data.ebean.sample")
@EnableTransactionManagement
public class SampleConfig {
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
      return new DataSourceTransactionManager(dataSource);
    }
    
    @Bean
    public EbeanQueryChannelService ebeanQueryChannelService(EbeanServer ebeanServer) {
      return new EbeanQueryChannelService(ebeanServer);
    }
    
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Bean
    @Primary
    public ServerConfig defaultEbeanServerConfig() {
      ServerConfig config = new ServerConfig();
    
      config.setDataSource(dataSource());
      config.addPackage("org.springframework.data.ebean.sample.domain");
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
}
```

Create an table entity or sql entity:

Table entity
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
Sql entity(The feature to replace MyBatis)
```java
@Entity
@Sql
@Getter
@Setter
public class UserInfo {
  private String firstName;
  private String lastName;
  private String emailAddress;
}
```
Create a repository interface in `org.springframework.data.ebean.sample`:

```java
public interface UserRepository extends EbeanRepository<User, Long> {
    @Query("where emailAddress = :emailAddress order by id desc")
    User findUserByEmailAddressEqualsOql(@Param("emailAddress") String emailAddress);

    @Query("select (firstname,lastname,address) fetch manager (lastname) where lastname = :lastname order by id desc")
    List<User> findByLastnameOql(@Param("lastname") String lastname);

    @Query(nativeQuery = true, value = "select * from user where email_address = :emailAddress order by id desc")
    User findUserByEmailAddressEquals(@Param("emailAddress") String emailAddress);

    @Query(nativeQuery = true, value = "select * from user where lastname = :lastname order by id desc")
    List<User> findUsersByLastnameEquals(@Param("lastname") String lastname);

    @Query(nativeQuery = true, value = "update user set email_address = :newEmail where email_address = :oldEmail")
    @Modifying
    int changeUserEmailAddress(@Param("oldEmail") String oldEmail, @Param("newEmail") String newEmail);

    @Query("delete from user where emailAddress = :emailAddress")
    @Modifying
    int deleteUserByEmailAddressOql(@Param("emailAddress") String emailAddress);

    @Query(nativeQuery = true, value = "delete from user where email_address = :emailAddress")
    @Modifying
    int deleteUserByEmailAddress(@Param("emailAddress") String emailAddress);

    @Query(name = "withManagerById")
    List<User> findByLastnameNamedOql(@Param("lastname") String lastname);
    
    List<User> findAllByEmailAddressAndLastname(@Param("emailAddress") String emailAddress, @Param("lastname") String lastname);
}
```

Create a named query config in `resources/ebean.xml`:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ebean xmlns="http://ebean-orm.github.io/xml/ns/ebean">
    <entity class="org.springframework.data.ebean.sample.domain.User">
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
    <entity class="org.springframework.data.ebean.sample.domain.UserInfo">
        <raw-sql name="userInfo">
            <query>
                select first_name, last_name, email_address from user
            </query>
        </raw-sql>
        <raw-sql name="userInfoByEmail">
            <query>
                select first_name, last_name, email_address from user
                where email_address = :emailAddress
                order by id desc
            </query>
        </raw-sql>
    </entity>
</ebean>
```

Write a test client:

`UserRepositoryIntegrationTests.java`
```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SampleConfig.class)
public class UserRepositoryIntegrationTests {

    @Autowired
    UserRepository repository;

    // Test fixture
    User user;

    @Before
    public void setUp() throws Exception {
        SimpleGuavaDomainEventPublisher.getInstance().register(new Object() {
            @Subscribe
            public void lister(UserEmailChangedEvent userEmailChangedEvent) {
                System.out.println(userEmailChangedEvent.toString());
            }
        });
        repository.deleteAll();
        user = new User("Xuegui", "Yuan", "yuanxuegui@163.com");
        user.setAge(29);
        user = repository.save(user);
    }

    @Test
    public void sampleTestCase() {
        // test find all orm query
        List<User> result1 = (List<User>) repository.findAll();
        result1.forEach(it -> System.out.println(it));
        assertEquals(1, result1.size());
        assertEquals("Yuan", result1.get(0).getFullName().getLastName());
        assertThat(result1, hasItem(user));

        // test find list orm query
        List<User> result2 = repository.findByLastnameOql("Yuan");
        assertEquals(1, result2.size());
        assertEquals("Yuan", result2.get(0).getFullName().getLastName());
        assertThat(result2, hasItem(user));

        // test find list sql query
        List<User> result3 = repository.findUsersByLastNameEquals("Yuan");
        assertEquals(1, result3.size());
        assertEquals("Yuan", result3.get(0).getFullName().getLastName());

        // test find one orm query
        User result4 = repository.findUserByEmailAddressEqualsOql("yuanxuegui@163.com");
        assertEquals("yuanxuegui@163.com", result4.getEmailAddress());

        // test find one sql query
        User result5 = repository.findUserByEmailAddressEquals("yuanxuegui@163.com");
        assertEquals("yuanxuegui@163.com", result5.getEmailAddress());

        // test update orm query
        int result6 = repository.changeUserEmailAddress("yuanxuegui@163.com", "yuanxuegui@126.com");
        assertEquals(1, result6);

        // test find list orm query
        List<User> result7 = repository.findByLastnameOql("Yuan");
        assertEquals("yuanxuegui@126.com", result7.get(0).getEmailAddress());

        // test delete sql query
        int result8 = repository.deleteUserByEmailAddress("yuanxuegui@126.com");
        assertEquals(1, result8);

        // test find one sql query
        User result9 = repository.findUserByEmailAddressEquals("yuanxuegui@126.com");
        assertNull(result9);

        // test create
        user = new User("Xuegui", "Yuan", "yuanxuegui@163.com");
        user.setAge(29);
        user = repository.save(user);

        // test find list named orm query
        List<User> result10 = repository.findByLastNameNamedOql("Yuan");
        assertEquals(1, result10.size());
        assertEquals("Yuan", result10.get(0).getFullName().getLastName());

        // test find one orm query
        User result11 = repository.findUserByEmailAddressEquals("yuanxuegui@163.com");
        assertNotNull(result11);

        // test delete orm update
        int result12 = repository.deleteUserByEmailAddressOql("yuanxuegui@163.com");
        assertEquals(1, result12);

        // test find one sql query
        User result13 = repository.findUserByEmailAddressEquals("yuanxuegui@163.com");
        assertNull(result13);
    }

    @Test
    public void testFindByMethodName() {
        List<User> result1 = repository.findAllByEmailAddressAndFullNameLastName("yuanxuegui@163.com", "Yuan");
        assertEquals(1, result1.size());
        assertEquals("Yuan", result1.get(0).getFullName().getLastName());
        assertThat(result1, hasItem(user));
    }

    @Test
    public void testFindByExample() {
        User u = new User();
        u.setEmailAddress("YUANXUEGUI");
        List<User> result1 = repository.findAll(Example.of(u, ExampleMatcher.matchingAll()
                .withIgnoreCase(true)
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)));
        assertEquals(1, result1.size());
        assertEquals("Yuan", result1.get(0).getFullName().getLastName());
        assertThat(result1, hasItem(user));

        List<User> result2 = repository.findAll(Example.of(u, ExampleMatcher.matchingAll()
                .withIgnoreCase(false)
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT)));
        assertEquals(0, result2.size());
    }

    @Test
    public void testAuditable() {
        User u = repository.findUserByEmailAddressEqualsOql("yuanxuegui@163.com");
        assertEquals("test", u.getCreatedBy());
        assertEquals("test", u.getLastModifiedBy());
    }

    @Test
    public void testDomainEvent() {
        user.changeEmail("yuanxuegui@126.com");
        repository.save(user);
        User u = repository.findOneByProperty("emailAddress", "yuanxuegui@126.com");
        assertNotNull(u);
        assertEquals("yuanxuegui@126.com", u.getEmailAddress());
    }
}
```
`EbeanQueryChannelServiceIntegrationTests.java`
```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SampleConfig.class)
public class EbeanQueryChannelServiceIntegrationTests {
  // Test fixture
  User user;
  @Autowired
  private EbeanQueryChannelService ebeanQueryChannelService;
  @Autowired
  private UserRepository repository;

  @Before
  public void setUp() throws Exception {
    user = new User("Xuegui", "Yuan", "yuanxuegui@163.com");
    user.setAge(29);
    user = repository.save(user);
  }

  @Test
  public void createSqlQueryMappingColumns() {
    String sql1 = "select first_name, last_name, email_address from user where last_name= :lastName";
    String sql2 = "select first_name as firstName, last_name as lastName, email_address as emailAddress from user where last_name= :lastName";
    Map<String, String> columnsMapping = Maps.newHashMap();
    columnsMapping.put("first_name", "firstName");
    columnsMapping.put("last_name", "lastName");

    Query<UserInfo> query1 = ebeanQueryChannelService.createSqlQuery(UserInfo.class,
        sql1);
    Query<UserInfo> query2 = ebeanQueryChannelService.createSqlQuery(UserInfo.class,
        sql2);
    Query<UserInfo> query3 = ebeanQueryChannelService.createSqlQueryMappingColumns(UserInfo.class,
        sql1, columnsMapping);

    query1.setParameter("lastName", "Yuan");
    query2.setParameter("lastName", "Yuan");
    query3.setParameter("lastName", "Yuan");
    UserInfo userInfo1 = query1.findOne();
    UserInfo userInfo2 = query2.findOne();
    UserInfo userInfo3 = query3.findOne();
    assertEquals("Xuegui", userInfo1.getFirstName());
    assertEquals("yuanxuegui@163.com", userInfo1.getEmailAddress());
    assertEquals("Xuegui", userInfo2.getFirstName());
    assertEquals("yuanxuegui@163.com", userInfo2.getEmailAddress());
    assertEquals("Xuegui", userInfo3.getFirstName());
    assertEquals("yuanxuegui@163.com", userInfo3.getEmailAddress());
  }

  @Test
  public void createNamedQuery() {
    UserInfo userInfo = ebeanQueryChannelService.createNamedQuery(UserInfo.class,
        "userInfoByEmail").setParameter("emailAddress",
        "yuanxuegui@163.com").findUnique();
    assertEquals("Xuegui", userInfo.getFirstName());
    assertEquals("yuanxuegui@163.com", userInfo.getEmailAddress());
  }

  @Test
  public void createNamedQueryWhere() {
    UserInfo userInfo = ebeanQueryChannelService.createNamedQuery(UserInfo.class,
        "userInfo").where()
        .eq("emailAddress", "yuanxuegui@163.com").findUnique();
    assertEquals("Xuegui", userInfo.getFirstName());
    assertEquals("yuanxuegui@163.com", userInfo.getEmailAddress());
  }

}
```

# spring-data-ebean
[![Build Status](https://travis-ci.org/hexagonframework/spring-data-ebean.svg?branch=master)](https://travis-ci.org/hexagonframework/spring-data-ebean) [![Gitter chat](https://badges.gitter.im/hexagonframework/spring-data-ebean/gitter.png)](https://gitter.im/hexagonframework/spring-data-ebean)


[Spring Data](http://projects.spring.io/spring-data/)项目的主要目标是使构建使用DDD仓储接口与实现的Spring应用程序变得更加容易。此模块是基于[Ebean ORM](https://ebean-orm.github.io)（轻量级JPA）的仓储层实现。
通过使用此模块，你可以在基于Ebean ORM下使用Spring Data模式带来的便利性。
如果你还没有接触过[Spring Data](http://projects.spring.io/spring-data/)或[Ebean ORM](https://ebean-orm.github.io)，建议先了解下该项目。

## 支持的一些特性 ##

* 对标准Entity支持完整CRUD操作，包括常用的查询操作
* 支持通过接口中的注解生成对应的查询（orm查询、sql查询、命名orm查询、命名sql查询）
* 支持通过接口中的方法名生成对应的查询
* 提供基础属性的实体基类
* 原生支持使用注解实现审计（如创建人、创建时间、修改人、最后修改时间)
* 支持自定义编写基于Ebean的查询，方便而不失灵活性
* 方便的与Spring集成
* 支持MySQL、Oracle、SQL Server、H2、PostgreSQL等数据库

## 为什么选择[Ebean ORM](https://ebean-orm.github.io)

基于JPA注解的轻量级ORM实现，支持Mybatis不支持的实体关联，但相比Hibernate/JPA具有Mybatis的查询灵活性，支持查询[partial objects](https://ebean-orm.github.io/docs/query/partialobjects)。
对于实现领域模型仓储接口的聚合根实体保存(保存聚合根实体同时保存聚合根上的关联实体、值对象)和partial objects等技术要求，Ebean都非常适用。
[查看更多详情](http://ebean-orm.github.io/architecture/compare-jpa)

## Maven cental链接

[Maven central - spring-data-ebean](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.github.hexagonframework.data%22%20AND%20a%3A%22spring-data-ebean%22 "maven central spring-data-ebean")

## 当前版本

[![Maven Central : ebean](https://maven-badges.herokuapp.com/maven-central/io.github.hexagonframework.data/spring-data-ebean/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.hexagonframework.data/spring-data-ebean) - spring-data-ebean

## 快速开始 ##

通过Maven引入依赖包:
```xml
<dependency>
  <groupId>io.github.hexagonframework.data</groupId>
  <artifactId>spring-data-ebean</artifactId>
  <version>{current version}</version>
</dependency>
```

最简单的通过Java注解配置的Spring Data Ebean 配置如下所示：
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

创建一个实体类:

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

创建一个仓储接口,使用包名 `org.springframework.data.ebean.repository.sample`:

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

对于使用到的命名sql查询、命名orm查询，编写XML文件`resources/ebean.xml`：

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

编写一个测试用例:

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

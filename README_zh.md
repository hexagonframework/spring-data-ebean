# spring-data-ebean
[![Build Status](https://travis-ci.org/hexagonframework/spring-data-ebean.svg?branch=master)](https://travis-ci.org/hexagonframework/spring-data-ebean) [![Gitter chat](https://badges.gitter.im/hexagonframework/spring-data-ebean/gitter.png)](https://gitter.im/hexagonframework/spring-data-ebean)


项目的主要目标是使构建使用数据访问技术的 Spring 应用程序变得更加容易。此模块处理增强基于Ebean的仓储层的支持。
通过使用此模块，你可以在基于Ebean为ORM的结构下使用Spring Data模式带来的便利性。
如果你还没有接触过[Spring Data](http://projects.spring.io/spring-data/)或[Ebean ORM](https://ebean-orm.github.io)，建议先了解下该项目。

## 支持的一些特性 ##

* 对标准Entity支持完整CRUD操作
* 支持通过接口中的方法名生成对应的查询
* 提供基础属性的实体基类
* 支持透明审计（如创建时间、最后修改)
* 自持自定义编写基于Ebean的查询，方便而不失灵活性
* 方便的与Spring集成
* 支持MySQL、Oracle、SQL Server、H2、PostgreSQL等数据库


## 快速开始 ##

通过Maven引入依赖包:
```xml
<dependency>
  <groupId>io.github.hexagonframework.data</groupId>
  <artifactId>spring-data-ebean</artifactId>
  <version>${version}.RELEASE</version>
</dependency>
```

Also include ebean jars and ebean properties.

最简单的通过Java注解配置的Spring Data Ebean 配置如下所示：
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
    @EbeanQuery("select (lastname) where lastname = :lastname order by id desc")
    List<User> findByLastnameOmq(@Param("lastname") String lastname);

    @EbeanQuery(nativeQuery = true, value = "select * from user where lastname = :lastname order by id desc")
    List<User> findByLastname(@Param("lastname") String lastname);
}
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
        user = repository.save(user);

        List<User> result = (List<User>) repository.findAll();
        result.forEach(it -> System.out.println(it));
        assertEquals(1, result.size());
        assertEquals("Yuan", result.get(0).getLastname());
        assertThat(result, hasItem(user));

        List<User> users = repository.findByLastname("Yuan");
        assertEquals(1, result.size());
        assertEquals("Yuan", result.get(0).getLastname());
        assertThat(result, hasItem(user));
    }
}
```

# spring-data-ebean
> Ebean implementation for spring data.

[![Build Status](https://travis-ci.org/hexagonframework/spring-data-ebean.svg?branch=master)](https://travis-ci.org/hexagonframework/spring-data-ebean) [![Gitter chat](https://badges.gitter.im/hexagonframework/spring-data-ebean/gitter.png)](https://gitter.im/hexagonframework/spring-data-ebean)
[![GitHub release](https://img.shields.io/github/release/hexagonframework/spring-data-ebean.svg)](https://github.com/hexagonframework/spring-data-ebean/releases)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

[MAVEN中央仓库链接](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.github.hexagonframework.data%22%20AND%20a%3A%22spring-data-ebean%22 "maven central spring-data-ebean")


[QQ群: 635113788]

* [码云 spring-data-ebean(定时拉取Github)](https://gitee.com/hexagonframework/spring-data-ebean)
* [Github spring-data-ebean](https://github.com/hexagonframework/spring-data-ebean)
* [码云 spring-data-ebean(定时拉取Github)](https://gitee.com/hexagonframework/spring-data-ebean-spring-boot)
* [Github spring-data-ebean-spring-boot](https://github.com/hexagonframework/spring-data-ebean-spring-boot)

[Spring Data](http://projects.spring.io/spring-data/)项目的主要目标是使构建使用DDD仓储接口与实现的Spring应用程序变得更加容易。此模块是基于[Ebean ORM](https://ebean-orm.github.io)（轻量级JPA）的仓储层实现。
通过使用此模块，你可以在基于Ebean ORM下使用Spring Data模式带来的便利性。
如果你还没有接触过[Spring Data](http://projects.spring.io/spring-data/)或[Ebean ORM](https://ebean-orm.github.io)，建议先了解下该项目。

## 支持的一些特性 ##

* 对标准Entity支持完整CRUD操作，包括常用的查询操作
* 支持通过接口中的注解生成对应的查询（orm查询、sql查询、命名orm查询、命名sql查询、dto查询）
* 支持通过接口中的方法名生成对应的查询
* 支持QueryChannel服务，用于CQRS
* 提供基础属性的实体基类和面向领域编程DDD基类
* 原生支持使用注解实现审计（如创建人、创建时间、修改人、最后修改时间)
* 支持自定义编写基于Ebean的查询，方便而不失灵活性
* 方便的与Spring集成
* 支持MySQL、Oracle、SQL Server、H2、PostgreSQL等数据库

#### 实现的场景 ####

1. 基于主键获取单个实体
2. 根据条件获取实体的列表
3. 保存新的单个实体并返回主键
4. 批量插入相同类型的多个实体并返回生成的主键列表
5. 更新单个现有实体——同时更新所有的实体字段
6. 获取多对一关系(部门的所属公司)
7. 获取一对多关系(公司的部门)
8. 更新实体一对多的关系(公司的部门)-增加两个项目，更新两个项目和删除一个项目-所有只需一句代码/一次操作
9. 复杂的选择-构造选择的条件基于一些布尔条件+加入一些连接
10. 使用JDBC简单语句(非PreparedStatement)执行查询
11. 基于主键移除单个实体

## 为什么选择[Ebean ORM](https://ebean-orm.github.io)

基于JPA注解的轻量级ORM实现，支持Mybatis不支持的实体关联，但相比Hibernate/JPA具有Mybatis的查询灵活性，支持查询[partial objects](https://ebean-orm.github.io/docs/query/partialobjects)。
对于实现领域模型仓储接口的聚合根实体保存(保存聚合根实体同时保存聚合根上的关联实体、值对象)和partial objects等技术要求，Ebean都非常适用。

我选择关系型数据持久化框架的基本原则:
1. 拥抱SQL而非隐藏
2. 可以实现面向领域编程
3. 可以利用JPA注解，但不能是JPA的完整实现（这点我偏向于Ebean）
4. 足够成熟以应对企业级应用（Ebean和Hibernate同时期作品，资格老，而且持续更新以满足更高需求）

#### 框架优缺点比较
> 参考：[java-persistence-frameworks-comparison](https://github.com/bwajtr/java-persistence-frameworks-comparison)

**Hibernate/JPA**
* [Compare to JPA](http://ebean-orm.github.io/architecture/compare-jpa)
* 反正比Hibernate/JPA好

**JDBC Template**
* 优点
    * 感觉自己非常接近JDBC本身
    * 实现了所有的场景而没有更大的问题-没有隐藏的惊喜
    * 非常容易的批量操作
    * 设置简单
* 缺点
    * 在Java代码中内联SQL使得实现仓储接口的可读性不强。如果Java支持多行字符串，那就更好了
    * 调试日志可以更好 

**jOOQ**
* 优点
  * 非常流畅，很容易编写新的查询，代码非常易读
  * 一旦设置好，就很容易使用，非常适合简单的查询
  * 很棒的日志记录器调试输出
* 缺点
  * 某些数据库的付费许可证——很难说服经理们相信它是值得的:)
  * 对于大的查询来说没有那么多的实用性——最好使用原生SQL(参见场景9)
  * 批量操作的怪异语法(如果您不使用UpdatableRecord)。但这没什么大不了的……

**MyBatis**
* 优点
  * 在XML映射文件里写SQL语句很爽
* 缺点
  * 实现一个DAO、仓储要写很多文件（DAO/Repository、XXXMapper、XXXMapper.xml），方法多了比较繁琐
  * 无法在一个方法里做批处理，无法级联加载
  * 即时是简单的CRUD，都显得繁琐，导致存在各种弥补mybatis这一缺陷的第三方封装框架
  * 无法面向对象，无法实现DDD

**EBean**
* 优点
  * 所有场景都实现非常完美，代码可读性高
  * 实现批处理超级简单
  * ORM查询、sql查询、DTO查询都非常简单  
* 缺点
  * DTO查询功能较新，有待增加XML mapping对DTO的支持
  * 必须“增强”实体，但实际上它基本上只是关于环境的设置(IDE插件和Gradle插件)，然后你就不用去想它了。

## 快速开始 ##

建立maven项目，建议使用spring boot和[spring-data-ebean-spring-boot](https://github.com/hexagonframework/spring-data-ebean-spring-boot.git)建立web项目

实例：[spring-boot-data-ebean-samples](https://github.com/hexagonframework/spring-boot-data-ebean-samples)


1、创建一个表格实体类或SQL实体类或DTO类:

表格实体：
```java
@Entity
@Getter
@Setter
public class User {

  @Id
  @GeneratedValue
  private Integer id;
  private String firstname;
  private String lastname;
  @Column(nullable = false, unique = true) private String emailAddress;
}
```
SQL实体：

Sql实体：
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
POJO DTO：
```java
@Getter
@Setter
public class UserDTO {
  private String firstName;
  private String lastName;
  private String emailAddress;
}
```

2、创建一个仓储接口
```
public interface UserRepository extends EbeanRepository<User, Long> {
    @Query("where emailAddress = :emailAddress order by id desc")
    User findUserByEmailAddressEqualsOql(@Param("emailAddress") String emailAddress);

    /**
     *  select fetch query细粒度控制查询字段
     */
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

    /**
     * 命名ORM/SQL查询 
     */
    @Query(name = "withManagerById")
    List<User> findByLastnameNamedOql(@Param("lastname") String lastname);
    
    List<User> findAllByEmailAddressAndLastname(@Param("emailAddress") String emailAddress, @Param("lastname") String lastname);
}
```

3、 对于使用到的命名sql查询、命名orm查询，编写XML文件：

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

4、 编写你的使用代码:

`UserRepositoryIntegrationTests.java`
```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SampleConfig.class)
public class UserRepositoryIntegrationTests {

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
    @Test
    public void testFindByExample() {
        User u = new User();
        u.setEmailAddress("YUANXUEGUI");
        List<User> result1 = repository.findAll(Example.of(u, ExampleMatcher.matchingAll()
                .withIgnoreCase(true)
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)));
        assertEquals(1, result1.size());
        assertEquals("Yuan", result1.get(0).getLastname());
        assertThat(result1, hasItem(user));

        List<User> result2 = repository.findAll(Example.of(u, ExampleMatcher.matchingAll()
                .withIgnoreCase(false)
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT)));
        assertEquals(0, result2.size());
    }
}
```
查询对象 `UserQuery`
```java
@Data
@IncludeFields("emailAddress,fullName(lastName,firstName),age")
public class UserQuery {
    @ExprParam(expr = ExprType.CONTAINS)
    private String emailAddress;

    @ExprParam(name = "age", expr = ExprType.GE)
    private int ageStart;

    @ExprParam(name = "age", expr = ExprType.LE)
    private int ageEnd;
}
```
`EbeanQueryChannelServiceIntegrationTests.java`
```java
package org.springframework.data.ebean.querychannel;

import io.ebean.Query;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.ebean.sample.config.SampleConfig;
import org.springframework.data.ebean.sample.domain.User;
import org.springframework.data.ebean.sample.domain.UserInfo;
import org.springframework.data.ebean.sample.domain.UserRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Xuegui Yuan
 */
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
    public void initUser() {
        repository.deleteAll();
        user = new User("QueryChannel", "Test", "testquerychannel@163.com");
        user.setAge(29);
        user = repository.save(user);
    }


    @Test
    public void createSqlQueryMappingColumns() {
        String sql1 = "select first_name, last_name, email_address from user where last_name= :lastName";
        String sql2 = "select first_name as firstName, last_name as lastName, email_address as emailAddress from user where last_name= :lastName";
        Map<String, String> columnsMapping = new HashMap<>();
        columnsMapping.put("first_name", "firstName");
        columnsMapping.put("last_name", "lastName");

        Query<UserInfo> query1 = ebeanQueryChannelService.createSqlQuery(UserInfo.class,
                sql1);
        Query<UserInfo> query2 = ebeanQueryChannelService.createSqlQuery(UserInfo.class,
                sql2);
        Query<UserInfo> query3 = ebeanQueryChannelService.createSqlQueryMappingColumns(UserInfo.class,
                sql1, columnsMapping);

        query1.setParameter("lastName", "Test");
        query2.setParameter("lastName", "Test");
        query3.setParameter("lastName", "Test");
        UserInfo userInfo1 = query1.findOne();
        UserInfo userInfo2 = query2.findOne();
        UserInfo userInfo3 = query3.findOne();
        assertEquals("QueryChannel", userInfo1.getFirstName());
        assertEquals("testquerychannel@163.com", userInfo1.getEmailAddress());
        assertEquals("QueryChannel", userInfo2.getFirstName());
        assertEquals("testquerychannel@163.com", userInfo2.getEmailAddress());
        assertEquals("QueryChannel", userInfo3.getFirstName());
        assertEquals("testquerychannel@163.com", userInfo3.getEmailAddress());
    }

    @Test
    public void createNamedQuery() {
        UserInfo userInfo = ebeanQueryChannelService.createNamedQuery(UserInfo.class,
                "userInfoByEmail").setParameter("emailAddress",
                "testquerychannel@163.com").findOne();
        assertEquals("QueryChannel", userInfo.getFirstName());
        assertEquals("testquerychannel@163.com", userInfo.getEmailAddress());
    }

    @Test
    public void createNamedQueryWhere() {
        UserInfo userInfo = ebeanQueryChannelService.createNamedQuery(UserInfo.class,
                "userInfo").where()
                .eq("emailAddress", "testquerychannel@163.com").findOne();
        assertEquals("QueryChannel", userInfo.getFirstName());
        assertEquals("testquerychannel@163.com", userInfo.getEmailAddress());
    }

    @Test
    public void createDtoQuery() {
        String sql = "select first_name, last_name, email_address from user where email_address = :emailAddress";
        UserDTO userDTO = ebeanQueryChannelService.createDtoQuery(UserDTO.class, sql)
                .setParameter("emailAddress", "testquerychannel@163.com")
                .findOne();
        assertEquals("QueryChannel", userDTO.getFirstName());
        assertEquals("testquerychannel@163.com", userDTO.getEmailAddress());
    }
    
    @Test
    public void query_queryObject() {
        UserQuery userQuery = new UserQuery();
        userQuery.setEmailAddress("testquerychannel@163.com");
        userQuery.setAgeStart(1);
        userQuery.setAgeEnd(30);
        UserDTO user = queryChannel.createQuery(User.class, userQuery)
                .asDto(UserDTO.class)
                .setRelaxedMode()
                .findOne();
        assertEquals("testquerychannel@163.com", user.getEmailAddress());
    }
    
    @Test
    public void applyQueryObject() {
        UserQuery userQuery = new UserQuery();
        userQuery.setEmailAddress("testquerychannel@163.com");
        userQuery.setAgeStart(1);
        userQuery.setAgeEnd(30);
        UserInfo userInfo = EbeanQueryChannelService.applyWhere(queryChannel.createNamedQuery(UserInfo.class,
                "userInfo").where(), userQuery).findOne();
        assertEquals("QueryChannel", userInfo.getFirstName());
        assertEquals("testquerychannel@163.com", userInfo.getEmailAddress());
    }

}
```

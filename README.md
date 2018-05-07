# spring-data-ebean
> Ebean implementation for spring data.

[![Build Status](https://travis-ci.org/hexagonframework/spring-data-ebean.svg?branch=master)](https://travis-ci.org/hexagonframework/spring-data-ebean) [![Gitter chat](https://badges.gitter.im/hexagonframework/spring-data-ebean/gitter.png)](https://gitter.im/hexagonframework/spring-data-ebean)
[![GitHub release](https://img.shields.io/github/release/hexagonframework/spring-data-ebean.svg)](https://github.com/hexagonframework/spring-data-ebean/releases)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

[MAVEN Central Link](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.github.hexagonframework.data%22%20AND%20a%3A%22spring-data-ebean%22 "maven central spring-data-ebean")


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

#### Scenarios implemented ####
1. Fetch single entity based on primary key
2. Fetch list of entities based on condition
3. Save new single entity and return primary key
4. Batch insert multiple entities of the same type and return generated keys
5. Update single existing entity - update all fields of entity at once
6. Fetch many-to-one relation (Company for Department)
7. Fetch one-to-many relation (Departments for Company)
8. Update entities one-to-many relation (Departments in Company) - add two items, update two items and delete one item - all at once
9. Complex select - construct select where conditions based on some boolean conditions + throw in some JOINs
10. Execute query using JDBC simple Statement (not PreparedStatement)
11. Remove single entity based on primary key

## Why choose [Ebean ORM](https://ebean-orm.github.io)
Conditions on frameworks which I choose for consideration:
1. The framework should embrace - not hide - SQL language and RDBMS we are using
2. The framework can implement DDD
3. Can utilize JPA annotations, but must not be full JPA implementation
4. The framework must be mature enough for "enterprise level" use

#### Subjective pros/cons of each framework 
> Reference：[java-persistence-frameworks-comparison](https://github.com/bwajtr/java-persistence-frameworks-comparison)

**Hibernate/JPA**
* [Compare to JPA](http://ebean-orm.github.io/architecture/compare-jpa)

**JDBC Template**
* Pros
    * Feels like you are very close to JDBC itself
    * Implemented all of the scenarios without bigger issues - there were no hidden surprises
    * Very easy batch operations
    * Easy setup
* Cons
    * Methods in JDBCDataRepositoryImpl are not much readable - that's because you have to inline SQL in Java code. It would have been better if Java supported multiline strings.
    * Debug logging could be better  

**jOOQ**
* Pros
  * Very fluent, very easy to write new queries, code is very readable
  * Once setup it's very easy to use, excellent for simple queries
  * Awesome logger debug output
* Cons
  * Paid license for certain databases - it'll be difficult to persuade managers that it's worth it :)
  * Not so much usable for big queries - it's better to use native SQL (see scenario 9.)
  * Weird syntax of batch operations (in case that you do not use UpdatableRecord). But it's not a big deal... 

**MyBatis**
* Pros
  * Writing SQL statements in XML mapper file feels good - it's easy to work with parameters
* Cons
  * Quite a lot of files for single DAO implementation, DAO/Repository and XXXMapper and XXXMapper.xml
  * Can't run batch and non-batch operations in single SqlSession
  * Too many XML mapper files.
  * Can't implement DDD

**EBean**
* Pros
  * Everything looks very nice - all the scenarios are implemented by very readable code
  * Super simple batch operations (actually it's only about using right method :) ) 
  * Although there are methods which make CRUD operations and Querying super simple, there are still means how to execute plain SQL and even a way how to get the basic JDBC Transaction object, which you can use for core JDBC stuff. That is really good.
* Cons
  * DTO query do not support XML mapping
  * Necessity of "enhancement" of the entities - this was quite surprising to me - but actually it's basically only about right environment setup (IDE plugin and Gradle plugin) and then you don't have to think about it

## Quick Start ##

Create maven project，recommend to use spring boot and [spring-data-ebean-spring-boot](https://github.com/hexagonframework/spring-data-ebean-spring-boot.git) to build web project.

Examples: [spring-boot-data-ebean-samples](https://github.com/hexagonframework/spring-boot-data-ebean-samples)

1. Create modal as table entity or sql entity or DTO:

Table entity:
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
Sql entity:

Sql entity:
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

POJO DTO:
```java
@Getter
@Setter
public class UserDTO {
  private String firstName;
  private String lastName;
  private String emailAddress;
}
```
2. Create a repository interface:

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

3. Options to create a named query config in xml when using named query:

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

4. Write your code to use model and repository(FOR DDD CURD) or `EbeanQueryChannelService`(FOR DTO QUERY):

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
QueryObject `UserQuery`
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

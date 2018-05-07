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
public class EbeanQueryChannelServiceIntegrationTest {
  // Test fixture
  User user;
  @Autowired
  private QueryChannelService queryChannel;
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

    Query<UserInfo> query1 = queryChannel.createSqlQuery(UserInfo.class,
        sql1);
    Query<UserInfo> query2 = queryChannel.createSqlQuery(UserInfo.class,
        sql2);
    Query<UserInfo> query3 = queryChannel.createSqlQueryMappingColumns(UserInfo.class,
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
    UserInfo userInfo = queryChannel.createNamedQuery(UserInfo.class,
        "userInfoByEmail").setParameter("emailAddress",
        "testquerychannel@163.com").findOne();
    assertEquals("QueryChannel", userInfo.getFirstName());
    assertEquals("testquerychannel@163.com", userInfo.getEmailAddress());
  }

  @Test
  public void createNamedQueryWhere() {
    UserInfo userInfo = queryChannel.createNamedQuery(UserInfo.class,
        "userInfo").where()
        .eq("emailAddress", "testquerychannel@163.com").findOne();
    assertEquals("QueryChannel", userInfo.getFirstName());
    assertEquals("testquerychannel@163.com", userInfo.getEmailAddress());
  }

  @Test
  public void createDtoQuery() {
    String sql = "select first_name, last_name, email_address from user where email_address = :emailAddress";
    UserDTO userDTO = queryChannel.createDtoQuery(UserDTO.class, sql)
        .setParameter("emailAddress", "testquerychannel@163.com")
        .findOne();
    assertEquals("QueryChannel", userDTO.getFirstName());
    assertEquals("testquerychannel@163.com", userDTO.getEmailAddress());
  }

  @Test
  public void createNamedDtoQuery() {
    UserDTO userDTO = queryChannel.createNamedDtoQuery(UserDTO.class, "byEmail")
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

    User user = queryChannel.createQuery(User.class, userQuery)
            .findOne();
    assertEquals("testquerychannel@163.com", user.getEmailAddress());

    UserDTO userDTO = queryChannel.createQuery(User.class, userQuery)
            .asDto(UserDTO.class)
            .setRelaxedMode()
            .findOne();
    assertEquals("testquerychannel@163.com", userDTO.getEmailAddress());
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
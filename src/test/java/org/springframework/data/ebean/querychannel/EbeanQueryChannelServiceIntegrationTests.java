package org.springframework.data.ebean.querychannel;

import com.google.common.collect.Maps;
import io.ebean.Query;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.ebean.sample.config.SampleConfig;
import org.springframework.data.ebean.sample.domain.User;
import org.springframework.data.ebean.sample.domain.UserInfo;
import org.springframework.data.ebean.sample.domain.UserRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

  @Test
  public void createSqlQueryMappingColumns() {
    user = new User("Xuegui", "Yuan", "yuanxuegui@163.com");
    user.setAge(29);
    user = repository.save(user);
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
        "yuanxuegui@163.com").findOne();
    assertEquals("Xuegui", userInfo.getFirstName());
    assertEquals("yuanxuegui@163.com", userInfo.getEmailAddress());
  }

//  @Test
//  public void createNamedQueryWhere() {
//    UserInfo userInfo = ebeanQueryChannelService.createNamedQuery(UserInfo.class,
//        "userInfo").where()
//        .eq("emailAddress", "yuanxuegui@163.com").findUnique();
//    assertEquals("Xuegui", userInfo.getFirstName());
//    assertEquals("yuanxuegui@163.com", userInfo.getEmailAddress());
//  }

}
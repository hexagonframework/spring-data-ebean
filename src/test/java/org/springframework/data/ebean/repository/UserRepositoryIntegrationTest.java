package org.springframework.data.ebean.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.ebean.sample.config.SampleConfig;
import org.springframework.data.ebean.sample.domain.User;
import org.springframework.data.ebean.sample.domain.UserRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;

/**
 * @author Xuegui Yuan
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SampleConfig.class)
public class UserRepositoryIntegrationTest {

  @Autowired
  UserRepository userRepository;

  // Test fixture
  User user;

  @Before
  public void setUp() throws Exception {
    userRepository.deleteAll();
    user = new User("Xuegui", "Yuan", "yuanxuegui@163.com");
    user.setAge(29);
    user = userRepository.save(user);
  }

  @Test
  public void updateQuery() throws Exception {
    assertNotNull(userRepository.updateQuery());
  }

  @Test
  public void sqlUpdateOf() throws Exception {
    assertNotNull(userRepository.sqlUpdateOf("update user set age = 30 where id = ?"));
  }

  @Test
  public void save() throws Exception {
    User u = new User("test", "test", "test@163.com");
    u = userRepository.save(u);
    assertNotNull(u.getId());
  }

  @Test
  public void saveAll() throws Exception {
    List<User> users = new ArrayList<>(3);
    for (int i = 0; i < 3; i++) {
      users.add(new User("test", "test" + i, "test" + i + "@163.com"));
    }
    userRepository.saveAll(users);
    for (User u : users) {
      assertNotNull(u.getId());
    }
  }

  @Test
  public void update() throws Exception {
    User u = new User("update", "update", "update@163.com");
    userRepository.save(u);

    User u1 = new User();
    u1.setId(u.getId());
    u1.setAge(31);
    userRepository.update(u1);
    u = userRepository.findById(u.getId()).get();
    assertEquals(31, u.getAge());
  }

  @Test
  public void updateAll() throws Exception {
    List<User> users = new ArrayList<>(3);
    for (int i = 0; i < 3; i++) {
      User u = new User();
      u.setId(i + 1L);
      u.setAge(32);
      users.add(u);
    }
    userRepository.updateAll(users);
    users = userRepository.findAllById(Arrays.asList(1L, 2L, 3L));
    for (User u : users) {
      assertEquals(32, u.getAge());
    }
  }

  @Test
  public void deleteById() throws Exception {
    userRepository.deleteById(1L);
    assertEquals(false, userRepository.findById(1L).isPresent());

    user.changeEmail("yuanxuegui@126.com");
    userRepository.save(user);
    User u = userRepository.findByProperty("emailAddress", "yuanxuegui@126.com").get();
    assertNotNull(u);

    userRepository.deleteById(u.getId());
  }

  @Test
  public void delete() throws Exception {
    User u = new User();
    u.setId(1L);
    userRepository.delete(u);
    assertEquals(false, userRepository.findById(1L).isPresent());
  }

  @Test
  public void deleteAll() throws Exception {
    userRepository.deleteAll();
    assertEquals(0, userRepository.count());
  }

  @Test
  public void deleteAll_entities() throws Exception {
    List<User> users = new ArrayList<>(3);
    for (int i = 0; i < 3; i++) {
      User u = new User();
      u.setId(i + 1L);
      users.add(u);
    }
    userRepository.deleteAll(users);
    users = userRepository.findAllById(Arrays.asList(1L, 2L, 3L));
    assertEquals(0, users.size());
  }

  @Test
  public void findById() throws Exception {
    User u = new User("findById", "findById", "findById@163.com");
    userRepository.save(u);
    assertEquals(true, userRepository.findById(u.getId()).isPresent());
  }

  @Test
  public void findById_fetchPath() throws Exception {
    User u = new User("find", "find", "find@163.com");
    userRepository.save(u);
    u = userRepository.findById("emailAddress", u.getId()).get();
    assertEquals("find@163.com", u.getEmailAddress());
  }

  @Test
  public void findByProperty() throws Exception {
    User u = new User("findOneByProperty", "findOneByProperty", "findOneByProperty@163.com");
    userRepository.save(u);
    User u1 = userRepository.findByProperty("id", u.getId()).get();
    assertEquals("findOneByProperty@163.com", u1.getEmailAddress());
  }

  @Test
  public void findByProperty_fetchPath() throws Exception {
    User u = new User("findOneByProperty_fetchPath", "findOneByProperty_fetchPath", "findOneByProperty_fetchPath@163.com");
    userRepository.save(u);
    User u1 = userRepository.findByProperty("id,emailAddress", "id", u.getId()).get();
    assertEquals("findOneByProperty_fetchPath@163.com", u1.getEmailAddress());
  }

  @Test
  public void findAllByProperty() throws Exception {
    User u = new User("findOneByProperty", "findOneByProperty", "findOneByProperty@163.com");
    userRepository.save(u);
    List<User> users = userRepository.findAllByProperty("id", u.getId());
    assertNotEquals(0, users.size());
  }

  @Test
  public void findAllByProperty_fetchPath() throws Exception {
    User u = new User("findOneByProperty", "findOneByProperty", "findOneByProperty@163.com");
    userRepository.save(u);
    List<User> users = userRepository.findAllByProperty("fullName(lastName)", "id", u.getId());
    assertNotEquals(0, users.size());
  }

  @Test
  public void findAllByProperty_fetchPath_sort() throws Exception {
    User u = new User("findAllByProperty_fetchPath_sort", "findAllByProperty_fetchPath_sort", "findAllByProperty_fetchPath_sort@163.com");
    userRepository.save(u);
    List<User> users = userRepository.findAllByProperty("fullName(lastName)",
            "id", u.getId(),
            Sort.by(Sort.Direction.DESC, "id"));
    assertNotEquals(0, users.size());
  }

  @Test
  public void findAllById() throws Exception {
    User u = new User("findAllById", "findAllById", "findAllById@163.com");
    userRepository.save(u);
    List<User> users = userRepository.findAllById(Arrays.asList(u.getId()));
    assertEquals(1, users.size());
  }

  @Test
  public void findAll() throws Exception {
    List<User> users = userRepository.findAll();
    assertEquals(1, users.size());
  }

  @Test
  public void findAll_sort() throws Exception {
    User u = new User("findAll_sort", "findAll_sort", "findAll_sort@163.com");
    userRepository.save(u);
    List<User> result1 = userRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    assertNotEquals(0, result1.size());
    assertEquals("findAll_sort", result1.get(0).getFullName().getLastName());
  }

  @Test
  public void findAll_fetchPath() throws Exception {
    User u = new User("findAll_fetchPath", "findAll_fetchPath", "findAll_fetchPath@163.com");
    userRepository.save(u);
    List<User> result1 = userRepository.findAll("fullName(lastName)");
    assertNotEquals(0, result1.size());
  }


  @Test
  public void findAll_fetchPath_ids() throws Exception {
    User u = new User("findAll_fetchPath_ids", "findAll_fetchPath_ids", "findAll_fetchPath_ids@163.com");
    userRepository.save(u);
    List<User> result1 = userRepository.findAll("fullName(lastName)", Arrays.asList(u.getId()));
    assertEquals(1, result1.size());
    assertEquals("findAll_fetchPath_ids", result1.get(0).getFullName().getLastName());
  }

  @Test
  public void findAll_fetchPath_sort() throws Exception {
    User u = new User("findAll_fetchPath_ids", "findAll_fetchPath_ids", "findAll_fetchPath_ids@163.com");
    userRepository.save(u);
    List<User> result1 = userRepository.findAll("fullName(lastName)",
            Sort.by(Sort.Direction.DESC, "id"));
    assertNotEquals(1, result1.size());
  }

  @Test
  public void findAll_fetchPath_pageable() throws Exception {
    User u = new User("findAll_fetchPath_pageable", "findAll_fetchPath_pageable", "findAll_fetchPath_pageable@163.com");
    userRepository.save(u);
    Page<User> page = userRepository.findAll("fullName(lastName)",
            PageRequest.of(0, 20, Sort.Direction.DESC, "id"));
    assertNotNull(page);
  }

  @Test
  public void findAll_example() throws Exception {
    User u = new User("findOneByProperty", "findOneByProperty", "findOneByProperty@163.com");
    userRepository.save(u);
    u.setEmailAddress("FINDOneByProperty");
    List<User> result1 = userRepository.findAll(Example.of(u, ExampleMatcher.matchingAll()
            .withIgnoreCase(true)
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)));
    assertEquals(1, result1.size());
    assertEquals("findOneByProperty", result1.get(0).getFullName().getLastName());
  }

  @Test
  public void findAll_fetchPath_example() throws Exception {
    User u = new User("findAll_fetchPath_example", "findAll_fetchPath_example", "findAll_fetchPath_example@163.com");
    userRepository.save(u);
    u.setEmailAddress("FINDAll_fetchPath_example");
    List<User> result1 = userRepository.findAll("fullName(lastName)", Example.of(u, ExampleMatcher.matchingAll()
            .withIgnoreCase(true)
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)));
    assertEquals(1, result1.size());
    assertEquals("findAll_fetchPath_example", result1.get(0).getFullName().getLastName());
  }

  @Test
  public void findAll_fetchPath_example_sort() throws Exception {
    User u = new User("findAll_fetchPath_example_sort", "findAll_fetchPath_example_sort", "findAll_fetchPath_example_sort@163.com");
    userRepository.save(u);
    u.setEmailAddress("FINDAll_fetchPath_example_sort");
    List<User> result1 = userRepository.findAll("fullName(lastName)",
            Example.of(u, ExampleMatcher.matchingAll()
                    .withIgnoreCase(true)
                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)),
            Sort.by(Sort.Direction.DESC, "fullName.lastName")

    );
    assertEquals(1, result1.size());
    assertEquals("findAll_fetchPath_example_sort", result1.get(0).getFullName().getLastName());
  }

  @Test
  public void findAll_example_sort() throws Exception {
    User u = new User("findAll_example_sort", "findAll_example_sort", "findAll_example_sort@163.com");
    userRepository.save(u);
    u.setEmailAddress("findAll_example_sort");
    List<User> result1 = userRepository.findAll(Example.of(u, ExampleMatcher.matchingAll()
                    .withIgnoreCase(true)
                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)),
            Sort.by(Sort.Direction.DESC, "id")
    );
    assertEquals(1, result1.size());
    assertEquals("findAll_example_sort", result1.get(0).getFullName().getLastName());
  }


  @Test
  public void findAll_example_pageable() throws Exception {
    User userExample = new User("X", "Y", "y");
    Page<User> page = userRepository.findAll(Example.of(userExample),
            PageRequest.of(0, 20, Sort.Direction.DESC, "id"));
    assertNotNull(page);
  }

  @Test
  public void findAll_fetchPath_example_pageable() throws Exception {
    User userExample = new User("X", "Y", "y");
    Page<User> page = userRepository.findAll("fullName(lastName)", Example.of(userExample),
            PageRequest.of(0, 20, Sort.Direction.DESC, "id"));
    assertNotNull(page);
  }

  @Test
  public void findAll_pageable() {
    Page<User> page = userRepository.findAll(PageRequest.of(0, 20, Sort.Direction.DESC, "id"));
    assertNotNull(page);
  }

  @Test
  public void findOne_example() throws Exception {
    User u = new User("findOne_example", "findOne_example", "findOne_example@163.com");
    userRepository.save(u);
    User userExample = new User("example", "example", "example");
    assertEquals(true, userRepository.findOne(Example.of(userExample, ExampleMatcher.matchingAll()
            .withIgnoreCase(true)
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING))).isPresent());
  }

  @Test
  public void count() throws Exception {
    User u = new User("findOne_example", "findOne_example", "findOne_example@163.com");
    userRepository.save(u);
    User userExample = new User("example", "example", "example");
    assertEquals(true, userRepository.findOne(Example.of(userExample, ExampleMatcher.matchingAll()
            .withIgnoreCase(true)
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING))).isPresent());
  }

  @Test
  public void exists_example() throws Exception {
    User u = new User("exists_example", "exists_example", "exists_example@163.com");
    userRepository.save(u);
    User userExample = new User("exists_example", "exists_example", "exists_example");
    assertEquals(true, userRepository.exists(Example.of(userExample, ExampleMatcher.matchingAll()
            .withIgnoreCase(true)
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING))));
  }

  @Test
  public void existsById() throws Exception {
    User u = new User("existsById", "existsById", "existsById@163.com");
    userRepository.save(u);
    User userExample = new User("existsById", "existsById", "existsById");
    assertEquals(true, userRepository.existsById(u.getId()));
  }

  @Test
  public void count_example() throws Exception {
    User u = new User("count_example", "count_example", "count_example@163.com");
    userRepository.save(u);
    User userExample = new User("count_example", "count_example", "count_example");
    assertEquals(1, userRepository.count(Example.of(userExample, ExampleMatcher.matchingAll()
            .withIgnoreCase(true)
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING))));
  }

  @Test
  public void queryAndMethod() {
    // test find all orm query
    List<User> result1 = (List<User>) userRepository.findAll();
    result1.forEach(it -> System.out.println(it));
    assertEquals(1, result1.size());
    assertEquals("Yuan", result1.get(0).getFullName().getLastName());
    assertThat(result1, hasItem(user));

    // test find list orm query
    List<User> result2 = userRepository.findByLastnameOql("Yuan");
    assertEquals(1, result2.size());
    assertEquals("Yuan", result2.get(0).getFullName().getLastName());
    assertThat(result2, hasItem(user));

    // test find list sql query
    List<User> result3 = userRepository.findUsersByLastNameEquals("Yuan");
    assertEquals(1, result3.size());
    assertEquals("Yuan", result3.get(0).getFullName().getLastName());

    // test find one orm query
    User result4 = userRepository.findUserByEmailAddressEqualsOql("yuanxuegui@163.com");
    assertEquals("yuanxuegui@163.com", result4.getEmailAddress());

    // test find one sql query
    User result5 = userRepository.findUserByEmailAddressEquals("yuanxuegui@163.com");
    assertEquals("yuanxuegui@163.com", result5.getEmailAddress());

    // test update orm query
    int result6 = userRepository.changeUserEmailAddress("yuanxuegui@163.com", "yuanxuegui@126.com");
    assertEquals(1, result6);

    // test find list orm query
    List<User> result7 = userRepository.findByLastnameOql("Yuan");
    assertEquals("yuanxuegui@126.com", result7.get(0).getEmailAddress());

    // test delete sql query
    int result8 = userRepository.deleteUserByEmailAddress("yuanxuegui@126.com");
    assertEquals(1, result8);

    // test find one sql query
    User result9 = userRepository.findUserByEmailAddressEquals("yuanxuegui@126.com");
    assertNull(result9);

    // test create
    user = new User("Xuegui", "Yuan", "yuanxuegui@163.com");
    user.setAge(29);
    user = userRepository.save(user);

    // test find list named orm query
    List<User> result10 = userRepository.findByLastNameNamedOql("Yuan");
    assertEquals(1, result10.size());
    assertEquals("Yuan", result10.get(0).getFullName().getLastName());

    // test find one orm query
    User result11 = userRepository.findUserByEmailAddressEquals("yuanxuegui@163.com");
    assertNotNull(result11);

    // test delete orm update
    int result12 = userRepository.deleteUserByEmailAddressOql("yuanxuegui@163.com");
    assertEquals(1, result12);

    // test find one sql query
    User result13 = userRepository.findUserByEmailAddressEquals("yuanxuegui@163.com");
    assertNull(result13);
  }

  @Test
  public void testFindByMethodName() {
    List<User> result1 = userRepository.findAllByEmailAddressAndFullNameLastName("yuanxuegui@163.com", "Yuan");
    assertEquals(1, result1.size());
    assertEquals("Yuan", result1.get(0).getFullName().getLastName());
    assertThat(result1, hasItem(user));
  }

  @Test
  public void testAuditable() {
    User u = userRepository.findUserByEmailAddressEqualsOql("yuanxuegui@163.com");
    assertEquals("test", u.getCreatedBy().orElse(null));
    assertEquals("test", u.getLastModifiedBy().orElse(null));
  }

  @Test
  public void testDomainEvent() {
    user.changeEmail("yuanxuegui@126.com");
    userRepository.save(user);
    User u = userRepository.findByProperty("emailAddress", "yuanxuegui@126.com").get();
    assertNotNull(u);
    assertEquals("yuanxuegui@126.com", u.getEmailAddress());
  }

  @Test
  public void findUserByEmailAddressEqualsOql_pageable() {
    Page<User> page = userRepository.findUserByEmailAddressEqualsOql("yuanxuegui@163.com", PageRequest.of(0, 20, Sort.Direction.DESC, "id"));
    assertNotNull(page);
  }

}
package org.springframework.data.ebean.repository;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.ebean.sample.config.SampleConfig;
import org.springframework.data.ebean.sample.domain.User;
import org.springframework.data.ebean.sample.domain.UserRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * @author Xuegui Yuan
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SampleConfig.class)
public class UserRepositoryIntegrationTests {

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
    public void sampleTestCase() {
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
    public void testFindByExample() {
        User u = new User();
        u.setEmailAddress("YUANXUEGUI");
        List<User> result1 = userRepository.findAll(Example.of(u, ExampleMatcher.matchingAll()
                .withIgnoreCase(true)
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)));
        assertEquals(1, result1.size());
        assertEquals("Yuan", result1.get(0).getFullName().getLastName());
        assertThat(result1, hasItem(user));

        List<User> result2 = userRepository.findAll(Example.of(u, ExampleMatcher.matchingAll()
                .withIgnoreCase(false)
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT)));
        assertEquals(0, result2.size());
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
        User u = userRepository.findOneByProperty("emailAddress", "yuanxuegui@126.com");
        assertNotNull(u);
        assertEquals("yuanxuegui@126.com", u.getEmailAddress());
    }

    @Test
    public void testDeleteUser() {
        user.changeEmail("yuanxuegui@126.com");
        userRepository.save(user);
        User u = userRepository.findOneByProperty("emailAddress", "yuanxuegui@126.com");
        assertNotNull(u);

        userRepository.deleteById(u.getId());
    }
}
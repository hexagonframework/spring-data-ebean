package org.springframework.data.ebean.repository;

import com.google.common.eventbus.Subscribe;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.ebean.eventbus.guava.SimpleGuavaDomainEventPublisher;
import org.springframework.data.ebean.sample.config.SampleConfig;
import org.springframework.data.ebean.sample.domain.User;
import org.springframework.data.ebean.sample.domain.UserEmailChangedEvent;
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

    @Test
    public void testDeleteUser() {
        user.changeEmail("yuanxuegui@126.com");
        repository.save(user);
        User u = repository.findOneByProperty("emailAddress", "yuanxuegui@126.com");
        assertNotNull(u);

        repository.delete(u.getId());
    }
}
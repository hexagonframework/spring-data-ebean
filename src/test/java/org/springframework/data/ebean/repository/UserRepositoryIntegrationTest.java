package org.springframework.data.ebean.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.ebean.domain.sample.User;
import org.springframework.data.ebean.repository.sample.SampleConfig;
import org.springframework.data.ebean.repository.sample.UserRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Xuegui Yuan
 */
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
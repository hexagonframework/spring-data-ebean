package org.springframework.data.ebean.repository.sample;

import org.springframework.data.ebean.domain.sample.User;
import org.springframework.data.ebean.repository.EbeanRepository;

/**
 * @author Xuegui Yuan
 */
public interface UserRepository extends EbeanRepository<User, Long> {
    //List<User> findByLastname(String lastname); // NOT SUPPORT YET
}

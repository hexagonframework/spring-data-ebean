package org.springframework.data.ebean.repository.sample;

import org.springframework.data.ebean.domain.sample.User;
import org.springframework.data.ebean.repository.EbeanQuery;
import org.springframework.data.ebean.repository.EbeanRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Xuegui Yuan
 */
public interface UserRepository extends EbeanRepository<User, Long> {
    @EbeanQuery("select (lastname) where lastname = :lastname order by id desc")
    List<User> findByLastnameOql(@Param("lastname") String lastname);

    @EbeanQuery(nativeQuery = true, value = "select * from user where lastname = :lastname order by id desc")
    List<User> findByLastname(@Param("lastname") String lastname);
}

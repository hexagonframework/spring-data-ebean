package org.springframework.data.ebean.repository.sample;

import org.springframework.data.ebean.domain.sample.User;
import org.springframework.data.ebean.repository.EbeanModifying;
import org.springframework.data.ebean.repository.EbeanQuery;
import org.springframework.data.ebean.repository.EbeanRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Xuegui Yuan
 */
public interface UserRepository extends EbeanRepository<User, Long> {
    @EbeanQuery("where emailAddress = :emailAddress order by id desc")
    User findUserByEmailAddressEqualsOql(@Param("emailAddress") String emailAddress);

    @EbeanQuery("select (firstname,lastname,address) fetch manager (lastname) where lastname = :lastname order by id desc")
    List<User> findByLastnameOql(@Param("lastname") String lastname);

    @EbeanQuery(nativeQuery = true, value = "select * from user where email_address = :emailAddress order by id desc")
    User findUserByEmailAddressEquals(@Param("emailAddress") String emailAddress);

    @EbeanQuery(nativeQuery = true, value = "select * from user where lastname = :lastname order by id desc")
    List<User> findUsersByLastnameEquals(@Param("lastname") String lastname);

    @EbeanQuery(nativeQuery = true, value = "update user set email_address = :newEmail where email_address = :oldEmail")
    @EbeanModifying
    int changeUserEmailAddress(@Param("oldEmail") String oldEmail, @Param("newEmail") String newEmail);

    @EbeanQuery("delete from user where emailAddress = :emailAddress")
    @EbeanModifying
    int deleteUserByEmailAddressOql(@Param("emailAddress") String emailAddress);

    @EbeanQuery(nativeQuery = true, value = "delete from user where email_address = :emailAddress")
    @EbeanModifying
    int deleteUserByEmailAddress(@Param("emailAddress") String emailAddress);
}

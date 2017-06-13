package org.springframework.data.ebean.repository.sample;

import org.springframework.data.ebean.annotations.Modifying;
import org.springframework.data.ebean.annotations.Query;
import org.springframework.data.ebean.domain.sample.User;
import org.springframework.data.ebean.repository.EbeanRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Xuegui Yuan
 */
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

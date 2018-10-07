package org.springframework.data.ebean.sample.domain;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.ebean.annotation.Modifying;
import org.springframework.data.ebean.annotation.Query;
import org.springframework.data.ebean.repository.EbeanRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Xuegui Yuan
 */
@Repository
public interface UserRepository extends EbeanRepository<User, Long> {
  @Query("where emailAddress = :emailAddress order by id desc")
  User findUserByEmailAddressEqualsOql(@Param("emailAddress") String emailAddress);

  @Query("select (fullName,address) fetch manager (fullName) where fullName.lastName = :lastName order by id desc")
  List<User> findByLastnameOql(@Param("lastName") String lastName);

  @Query(nativeQuery = true, value = "select * from user where email_address = :emailAddress order by id desc")
  User findUserByEmailAddressEquals(@Param("emailAddress") String emailAddress);

  @Query(nativeQuery = true, value = "select * from user where last_name = :lastName order by id desc")
  List<User> findUsersByLastNameEquals(@Param("lastName") String lastName);

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
  List<User> findByLastNameNamedOql(@Param("lastName") String lastName);

  List<User> findAllByEmailAddressAndFullNameLastName(@Param("emailAddress") String emailAddress, @Param("lastName") String lastName);

  @Query( value = "where emailAddress = :emailAddress")
  Page<User> findUserByEmailAddressEqualsOql(@Param("emailAddress") String lastName, Pageable page);

}

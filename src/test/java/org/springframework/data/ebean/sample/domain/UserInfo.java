package org.springframework.data.ebean.sample.domain;

import io.ebean.annotation.Sql;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Xuegui Yuan
 */
@Entity
@Sql
@Getter
@Setter
public class UserInfo {
  private String firstName;
  private String lastName;
  private String emailAddress;
}

package org.springframework.data.ebean.querychannel;

import lombok.Getter;
import lombok.Setter;

/**
 * @author XueguiYuan
 * @version 1.0 (created time: 2018/3/3).
 */
@Getter
@Setter
public class UserDTO {
  private String lastName;
  private String firstName;
  private String emailAddress;
  private int age;
}
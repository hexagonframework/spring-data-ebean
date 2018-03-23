package org.springframework.data.ebean.querychannel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author XueguiYuan
 * @version 1.0 (created time: 2018/3/3).
 */
@Getter
@Setter
@AllArgsConstructor
public class UserDTO {
  private String firstName;
  private String lastName;
  private String emailAddress;
}
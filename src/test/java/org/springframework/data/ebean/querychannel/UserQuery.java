package org.springframework.data.ebean.querychannel;

import lombok.Data;
import org.springframework.data.ebean.annotation.ExprParam;
import org.springframework.data.ebean.annotation.IncludeFields;

/**
 * @author XueguiYuan
 * @version 1.0 (created time: 2018/4/29).
 */
@Data
@IncludeFields("emailAddress,fullName(lastName,firstName),age")
public class UserQuery {
    @ExprParam(expr = ExprType.CONTAINS)
    private String emailAddress;

    @ExprParam(name = "age", expr = ExprType.GE)
    private int ageStart;

    @ExprParam(name = "age", expr = ExprType.LE)
    private int ageEnd;
}

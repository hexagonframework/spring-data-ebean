package org.springframework.data.ebean.domain.sample;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author Xuegui Yuan
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class FullName implements Serializable {
    private String firstName;
    private String lastName;
}

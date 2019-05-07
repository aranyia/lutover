package org.lutover.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode
@ToString
public class Account {

    private String id;

    private String currency;

    private BigDecimal balance;

}

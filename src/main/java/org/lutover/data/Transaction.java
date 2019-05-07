package org.lutover.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode
@ToString
public class Transaction {

    private String reference;

    private Date createdAt;

    private String source;

    private String recipient;

    private String currency;

    private BigDecimal amount;

    private BigDecimal fxRate;

    private TransactionStatus status;

}

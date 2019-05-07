package org.lutover.app.api.v1;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransactionResponse {

    private String reference;

    private Date createdAt;

    private BigDecimal debitAmount;

    private BigDecimal recipientAmount;

    private BigDecimal fxRate;

    private String status;

}

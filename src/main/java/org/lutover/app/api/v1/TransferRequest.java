package org.lutover.app.api.v1;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    private String sourceAccountId;

    private String targetAccountId;

    private BigDecimal amount;

}

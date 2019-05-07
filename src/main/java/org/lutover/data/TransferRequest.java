package org.lutover.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class TransferRequest {

    private String sourceAccountId;

    private String targetAccountId;

    private BigDecimal amount;

}

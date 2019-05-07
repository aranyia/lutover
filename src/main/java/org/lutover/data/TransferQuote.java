package org.lutover.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode
@ToString
public class TransferQuote extends TransferRequest {

    private String reference;

    private String sourceCurrency;

    private String targetCurrency;

    private BigDecimal fxRate;

    /**
     * Fees considered, but treated as zero until fee service would be implemented
     */
    private BigDecimal fees = BigDecimal.ZERO;

}

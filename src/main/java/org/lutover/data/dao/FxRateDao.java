package org.lutover.data.dao;

import java.math.BigDecimal;

/**
 * Data layer for FX-rates.
 */
public interface FxRateDao {

    BigDecimal getFxRate(String sourceCurrency, String targetCurrency);

}

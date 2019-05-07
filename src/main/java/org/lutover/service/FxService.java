package org.lutover.service;

import java.math.BigDecimal;

/**
 * Service to provide FX-rates for transfers involving distinct source & target currencies.
 */
public interface FxService {

    BigDecimal getFxRate(String sourceCurrency, String targetCurrency);

}

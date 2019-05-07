package org.lutover.service;

import org.lutover.data.dao.FxRateDao;

import javax.inject.Inject;
import java.math.BigDecimal;

public class FxServiceImpl implements FxService {

    private final FxRateDao fxRateDao;

    @Inject
    public FxServiceImpl(FxRateDao fxRateDao) {
        this.fxRateDao = fxRateDao;
    }

    @Override
    public BigDecimal getFxRate(String sourceCurrency, String targetCurrency) {
        if (sourceCurrency.equalsIgnoreCase(targetCurrency)) {
            return BigDecimal.ONE;
        }

        try {
            return fxRateDao.getFxRate(sourceCurrency, targetCurrency);
        } catch (IllegalArgumentException dataException) {
            throw new IllegalArgumentException("transfer currency pair is not supported", dataException);
        }
    }
}

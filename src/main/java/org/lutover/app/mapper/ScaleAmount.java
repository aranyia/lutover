package org.lutover.app.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * To perform default scaling of calculated amounts.
 */
public interface ScaleAmount {

    int SCALE_DEFAULT = 2;

    static BigDecimal scale(BigDecimal amount) {
        if (amount == null) {
            return null;
        }
        return amount.setScale(SCALE_DEFAULT, RoundingMode.HALF_EVEN);
    }
}

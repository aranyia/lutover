package org.lutover.data.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FxRateDaoImpl implements FxRateDao {

    private final Statement statement = H2DbManager.getStatement();

    @Override
    public BigDecimal getFxRate(String sourceCurrency, String targetCurrency) {
        try {
            final ResultSet resultSet = statement
                    .executeQuery("SELECT fxrate FROM FX_RATE WHERE SOURCE_CURRENCY ='" + sourceCurrency + "' " +
                            "AND TARGET_CURRENCY = '" + targetCurrency + "'");
            if (!resultSet.next()) {
                throw new IllegalArgumentException("FX for currency pair not found");
            }
            return resultSet.getBigDecimal("fxrate");
        } catch (SQLException dbException) {
            throw new RuntimeException(dbException);
        }
    }
}

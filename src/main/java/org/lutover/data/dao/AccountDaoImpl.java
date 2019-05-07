package org.lutover.data.dao;

import org.lutover.data.Account;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountDaoImpl implements AccountDao {

    private Statement statement = H2DbManager.getStatement();

    @Override
    public Account findById(String accountId) {
        try {
            final ResultSet resultSet = statement.executeQuery("SELECT * FROM ACCOUNT WHERE id = '" + accountId + "'");
            if (!resultSet.next()) {
                throw new IllegalArgumentException("account does not exist: " + accountId);
            }

            final Account account = new Account();
            account.setId(resultSet.getString("id"));
            account.setCurrency(resultSet.getString("currency"));
            account.setBalance(resultSet.getBigDecimal("balance"));
            return account;
        } catch (SQLException dbException) {
            throw new RuntimeException(dbException);
        }
    }

    @Override
    public void updateBalance(String accountId, BigDecimal balance) {
        try {
            statement.executeUpdate("UPDATE ACCOUNT SET balance = " + balance + " WHERE id = '" + accountId + "'");
        } catch (SQLException dbException) {
            throw new RuntimeException(dbException);
        }
    }
}

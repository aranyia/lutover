package org.lutover.data.dao;

import org.lutover.data.Transaction;
import org.lutover.data.TransactionStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TransactionDaoImpl implements TransactionDao {

    private final Statement statement = H2DbManager.getStatement();

    @Override
    public void persist(Transaction transaction) {
        try {
            statement.executeUpdate("INSERT INTO TRANSACTION" +
                    "(reference, source_acc, target_acc, currency, amount, fxrate, status)" +
                    "VALUES(" + String.format("'%s','%s','%s','%s',%f,%f,'%s'",
                    transaction.getReference(), transaction.getSource(), transaction.getRecipient(),
                    transaction.getCurrency(), transaction.getAmount(), transaction.getFxRate(), transaction.getStatus()) + ")");
        } catch (SQLException dbException) {
            throw new RuntimeException(dbException);
        }
    }

    @Override
    public Transaction findByReference(String reference) {
        try {
            final ResultSet resultSet = statement.executeQuery("SELECT * FROM TRANSACTION WHERE REFERENCE ='" + reference + "'");
            if (!resultSet.next()) {
                throw new IllegalArgumentException("transaction not found");
            }

            final Transaction transaction = new Transaction();
            transaction.setStatus(TransactionStatus.valueOf(resultSet.getString("status")));
            transaction.setReference(resultSet.getString("reference"));
            transaction.setCreatedAt(resultSet.getDate("createdat"));
            transaction.setSource(resultSet.getString("source_acc"));
            transaction.setRecipient(resultSet.getString("target_acc"));
            transaction.setAmount(resultSet.getBigDecimal("amount"));
            transaction.setCurrency(resultSet.getString("currency"));
            transaction.setFxRate(resultSet.getBigDecimal("fxrate"));
            return transaction;
        } catch (SQLException dbException) {
            throw new RuntimeException(dbException);
        }
    }

    @Override
    public void updateStatus(String reference, TransactionStatus status) {
        try {
            statement.executeUpdate(
                    "UPDATE TRANSACTION SET STATUS = '" + status.name() + "' WHERE REFERENCE ='" + reference + "'");
        } catch (SQLException dbException) {
            throw new RuntimeException(dbException);
        }
    }
}

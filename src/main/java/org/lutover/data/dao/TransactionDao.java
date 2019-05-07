package org.lutover.data.dao;

import org.lutover.data.Transaction;
import org.lutover.data.TransactionStatus;

/**
 * Data layer for transaction operations.
 */
public interface TransactionDao {

    void persist(Transaction transaction);

    Transaction findByReference(String reference);

    void updateStatus(String reference, TransactionStatus status);

}

package org.lutover.service;

import org.lutover.data.Context;
import org.lutover.data.Transaction;
import org.lutover.data.TransactionStatus;

/**
 * Service for operations on transactions. It creates and retrieves transactions,
 * can be used to maintain transaction status.
 */
public interface TransactionService {

    void createTransaction(Transaction transaction);

    Transaction getTransaction(String reference, Context context);

    void updateTransactionStatus(String reference, TransactionStatus status);

}

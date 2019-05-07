package org.lutover.service;

import org.lutover.data.Context;
import org.lutover.data.Transaction;
import org.lutover.data.TransactionStatus;
import org.lutover.data.dao.TransactionDao;

import javax.inject.Inject;

public class TransactionServiceImpl implements TransactionService {

    private final TransactionDao transactionDao;

    @Inject
    public TransactionServiceImpl(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    @Override
    public void createTransaction(Transaction transaction) {
        transactionDao.persist(transaction);
    }

    @Override
    public Transaction getTransaction(String reference, Context context) {
        return transactionDao.findByReference(reference);
    }

    @Override
    public void updateTransactionStatus(String reference, TransactionStatus status) {
        transactionDao.updateStatus(reference, status);
    }
}

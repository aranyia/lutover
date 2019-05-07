package org.lutover.service;

import org.lutover.data.TransactionStatus;
import org.lutover.data.TransferQuote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.lutover.app.mapper.ScaleAmount.scale;
import static org.lutover.data.TransactionStatus.FAILED;
import static org.lutover.data.TransactionStatus.PROCESSED;
import static org.lutover.data.TransactionStatus.PROCESSING;

/**
 * Transfer task to execute the quote allocated to a transfer request.
 * The resulting transaction will be persisted with the appropriate status.
 */
public class TransferTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(TransferTask.class);

    private final AccountService accountService;

    private final TransactionService transactionService;

    private final TransferQuote transferQuote;

    public TransferTask(AccountService accountService, TransactionService transactionService, TransferQuote transferQuote) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.transferQuote = transferQuote;
    }

    @Override
    public void run() {
        final BigDecimal sourceAmount = transferQuote.getAmount();
        final BigDecimal targetAmount = scale(sourceAmount.multiply(transferQuote.getFxRate()));
        final BigDecimal feesAmount = transferQuote.getFees();

        final BigDecimal debitAmount = sourceAmount.add(feesAmount);

        TransactionStatus transactionStatus = PROCESSING;
        try {
            accountService.debitAccount(transferQuote.getSourceAccountId(), debitAmount);
        } catch (Exception debitException) {
            log.error("failed to debit account #" + transferQuote.getSourceAccountId(), debitException);
            transactionStatus = FAILED;
        }

        if (!FAILED.equals(transactionStatus)) {
            try {
                accountService.creditAccount(transferQuote.getTargetAccountId(), targetAmount);
                transactionStatus = PROCESSED;
            } catch (Exception creditException) {
                log.error("failed to credit account #" + transferQuote.getTargetAccountId(), creditException);
                transactionStatus = FAILED;

                reverseDebit(transferQuote.getSourceAccountId(), debitAmount);
            }
        }
        transactionService.updateTransactionStatus(transferQuote.getReference(), transactionStatus);
    }

    /**
     * Initiates a reversal of a debit happened on a specific account.
     * To be used in cases of failures while executing a transfer.
     *
     * @param accountId   unique identifier of the account to be credited with the reversal
     * @param debitAmount the debited amount to be credited back to the account
     */
    private void reverseDebit(String accountId, BigDecimal debitAmount) {
        try {
            accountService.creditAccount(accountId, debitAmount);
            log.info("reversed amount to account #" + accountId);
        } catch (Exception reversalException) {
            log.error("reversal failed to account #" + accountId, reversalException);
        }
    }
}

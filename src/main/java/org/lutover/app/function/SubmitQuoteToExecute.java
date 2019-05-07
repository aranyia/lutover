package org.lutover.app.function;

import org.lutover.service.AccountService;
import org.lutover.service.TransactionService;
import org.lutover.service.TransferTask;
import org.lutover.data.TransferQuote;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * Consumes quotes to forward them for execution as transactions.
 */
public class SubmitQuoteToExecute implements Consumer<TransferQuote> {

    private final AccountService accountService;

    private final TransactionService transactionService;

    private final ExecutorService executorService;

    public SubmitQuoteToExecute(AccountService accountService, TransactionService transactionService,
                                ExecutorService executorService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.executorService = executorService;
    }

    @Override
    public void accept(TransferQuote transferQuote) {
        final TransferTask transferTask = new TransferTask(accountService, transactionService, transferQuote);
        executorService.submit(transferTask);
    }
}

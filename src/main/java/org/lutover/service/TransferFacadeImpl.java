package org.lutover.service;

import org.lutover.app.ContextHolder;
import org.lutover.app.function.GetTransferQuote;
import org.lutover.app.function.MapQuoteToTransaction;
import org.lutover.app.function.SubmitQuoteToExecute;
import org.lutover.data.Context;
import org.lutover.data.Transaction;
import org.lutover.data.TransferQuote;
import org.lutover.data.TransferRequest;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class TransferFacadeImpl implements TransferFacade {

    private final TransactionService transactionService;

    private final ContextHolder contextHolder;

    private final Function<TransferRequest, TransferQuote> getQuote;

    private final Function<TransferQuote, Transaction> mapToTransaction;

    private final Consumer<TransferQuote> submitQuoteToExecute;

    @Inject
    public TransferFacadeImpl(AccountService accountService, FxService fxService, TransactionService transactionService,
                              ExecutorService executorService, ContextHolder contextHolder) {
        this.transactionService = transactionService;
        this.contextHolder = contextHolder;

        getQuote = new GetTransferQuote(accountService, fxService);
        mapToTransaction = new MapQuoteToTransaction();
        submitQuoteToExecute = new SubmitQuoteToExecute(accountService, transactionService, executorService);
    }

    /**
     * Implement the process to map and execute a transfer request:
     * (.) -> {request} -> {quote} -> {transaction} -> ()
     *
     * @param transferRequest the details of the transfer requested
     * @param context         context of the request initiated in
     * @return data of the transaction created
     */
    @Override
    public Transaction transfer(TransferRequest transferRequest, Context context) {
        contextHolder.validateIdempotence(context);

        return Stream.of(transferRequest)
                .map(getQuote)
                .peek(submitQuoteToExecute)
                .map(mapToTransaction)
                .peek(transactionService::createTransaction)
                .findFirst().orElseThrow();
    }

    @Override
    public Transaction getByReference(String transactionReference, Context context) {
        return transactionService.getTransaction(transactionReference, context);
    }
}

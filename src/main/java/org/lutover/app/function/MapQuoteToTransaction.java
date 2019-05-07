package org.lutover.app.function;

import org.lutover.data.Transaction;
import org.lutover.data.TransactionStatus;
import org.lutover.data.TransferQuote;

import java.util.Date;
import java.util.function.Function;

/**
 * Creating transaction data by mapping an allocated quote.
 */
public class MapQuoteToTransaction implements Function<TransferQuote, Transaction> {

    @Override
    public Transaction apply(TransferQuote transferQuote) {
        final Transaction transaction = new Transaction();
        transaction.setCreatedAt(new Date());
        transaction.setAmount(transferQuote.getAmount());
        transaction.setCurrency(transferQuote.getSourceCurrency());
        transaction.setSource(transferQuote.getSourceAccountId());
        transaction.setRecipient(transferQuote.getTargetAccountId());
        transaction.setFxRate(transferQuote.getFxRate());
        transaction.setReference(transferQuote.getReference());
        transaction.setStatus(TransactionStatus.CREATED);

        return transaction;
    }
}

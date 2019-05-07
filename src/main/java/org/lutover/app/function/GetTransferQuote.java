package org.lutover.app.function;

import org.lutover.service.AccountService;
import org.lutover.service.FxService;
import org.lutover.data.Account;
import org.lutover.data.TransferQuote;
import org.lutover.data.TransferRequest;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Function;

/**
 * Allocating a quote based on the transfer request, determining FX-rate and fees if applicable.
 */
public class GetTransferQuote implements Function<TransferRequest, TransferQuote> {

    private final AccountService accountService;

    private final FxService fxService;

    public GetTransferQuote(AccountService accountService, FxService fxService) {
        this.accountService = accountService;
        this.fxService = fxService;
    }

    @Override
    public TransferQuote apply(TransferRequest req) {
        validateRequest(req);

        final Account sourceAccount = accountService.getAccount(req.getSourceAccountId());
        final Account targetAccount = accountService.getAccount(req.getTargetAccountId());

        final TransferQuote quote = new TransferQuote();
        quote.setReference(generateQuoteReference());
        quote.setSourceAccountId(req.getSourceAccountId());
        quote.setTargetAccountId(req.getTargetAccountId());
        quote.setAmount(req.getAmount());
        quote.setSourceCurrency(sourceAccount.getCurrency());
        quote.setTargetCurrency(targetAccount.getCurrency());
        quote.setFxRate(fxService.getFxRate(quote.getSourceCurrency(), quote.getTargetCurrency()));

        return quote;
    }

    /**
     * Generates a unique identifier to be used for quotes.
     *
     * @return a unique identifier
     */
    private String generateQuoteReference() {
        return UUID.randomUUID().toString();
    }

    private void validateRequest(TransferRequest req) {
        if (req.getAmount() == null || BigDecimal.ZERO.compareTo(req.getAmount()) >= 0) {
            throw new IllegalArgumentException("requested amount is invalid");
        }
    }
}

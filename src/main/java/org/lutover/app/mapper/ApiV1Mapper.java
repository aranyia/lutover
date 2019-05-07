package org.lutover.app.mapper;

import org.lutover.app.api.v1.TransactionResponse;
import org.lutover.data.Transaction;
import org.lutover.data.TransferRequest;

import static org.lutover.app.mapper.ScaleAmount.scale;

public interface ApiV1Mapper {

    static TransactionResponse mapToApi(Transaction transaction) {
        final TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setReference(transaction.getReference());
        transactionResponse.setCreatedAt(transaction.getCreatedAt());
        transactionResponse.setDebitAmount(transaction.getAmount());
        transactionResponse.setStatus(transaction.getStatus() != null ? transaction.getStatus().toString() : null);

        if (transaction.getFxRate() == null) {
            transactionResponse.setRecipientAmount(transaction.getAmount());
        } else {
            transactionResponse.setFxRate(transaction.getFxRate());
            transactionResponse.setRecipientAmount(scale(transaction.getAmount().multiply(transaction.getFxRate())));
        }
        return transactionResponse;
    }

    static TransferRequest mapFromApi(org.lutover.app.api.v1.TransferRequest apiRequest) {
        final TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAmount(apiRequest.getAmount());
        transferRequest.setSourceAccountId(apiRequest.getSourceAccountId());
        transferRequest.setTargetAccountId(apiRequest.getTargetAccountId());

        return transferRequest;
    }
}

package org.lutover.service;

import org.lutover.data.Context;
import org.lutover.data.Transaction;
import org.lutover.data.TransferRequest;

/**
 * Facade for transfer functionality. To provide interface for initiating transfers
 * and retrieving resulting transactions.
 */
public interface TransferFacade {

    Transaction transfer(TransferRequest transferRequest, Context context);

    Transaction getByReference(String transactionReference, Context context);

}

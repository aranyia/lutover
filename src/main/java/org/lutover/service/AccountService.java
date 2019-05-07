package org.lutover.service;

import org.lutover.data.Account;

import java.math.BigDecimal;

/**
 * Service with operations on accounts. Retrieves accounts by account identifier.
 * Makes credit or debit against the account balance.
 */
public interface AccountService {

    Account getAccount(String accountId);

    void debitAccount(String accountId, BigDecimal amount);

    void creditAccount(String accountId, BigDecimal amount);

}

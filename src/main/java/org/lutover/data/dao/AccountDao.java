package org.lutover.data.dao;

import org.lutover.data.Account;

import java.math.BigDecimal;

/**
 * Data layer for account operations.
 */
public interface AccountDao {

    Account findById(String accountId);

    void updateBalance(String accountId, BigDecimal balance);

}

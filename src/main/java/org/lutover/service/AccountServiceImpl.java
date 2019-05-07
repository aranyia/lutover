package org.lutover.service;

import org.lutover.data.Account;
import org.lutover.data.dao.AccountDao;

import javax.inject.Inject;
import java.math.BigDecimal;

public class AccountServiceImpl implements AccountService {

    private final AccountDao accountDao;

    @Inject
    public AccountServiceImpl(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public Account getAccount(final String accountId) {
        return accountDao.findById(accountId);
    }

    @Override
    public void debitAccount(String accountId, BigDecimal amount) {
        final Account account = accountDao.findById(accountId);
        final BigDecimal newBalance = account.getBalance().subtract(amount);

        if (newBalance.signum() < 0) {
            throw new IllegalStateException("insufficient balance to debit the amount");
        }
        account.setBalance(newBalance);
        accountDao.updateBalance(accountId, newBalance);
    }

    @Override
    public void creditAccount(String accountId, BigDecimal amount) {
        final Account account = accountDao.findById(accountId);
        final BigDecimal newBalance = account.getBalance().add(amount);

        account.setBalance(newBalance);
        accountDao.updateBalance(accountId, newBalance);
    }
}

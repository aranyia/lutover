package org.lutover.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lutover.data.Account;
import org.lutover.data.dao.AccountDao;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

    private AccountService accountService;

    @Mock
    private AccountDao accountDao;

    @Before
    public void init() {
        accountService = new AccountServiceImpl(accountDao);
    }

    @Test
    public void testDebitAccountThenSuccess() {
        final String accountId = "RV10000001";

        final Account account = new Account();
        account.setId(accountId);
        account.setCurrency("USD");
        account.setBalance(BigDecimal.TEN);

        doReturn(account).when(accountDao).findById(accountId);

        accountService.debitAccount(accountId, BigDecimal.ONE);
    }

    @Test(expected = IllegalStateException.class)
    public void testDebitAccountThenException() {
        final String accountId = "RV10000001";

        final Account account = new Account();
        account.setId(accountId);
        account.setCurrency("USD");
        account.setBalance(BigDecimal.ZERO);

        doReturn(account).when(accountDao).findById(accountId);

        accountService.debitAccount(accountId, BigDecimal.ONE);
    }

    @Test
    public void testCreditAccountThenSuccess() {
        final String accountId = "RV10000002";

        final Account account = new Account();
        account.setId(accountId);
        account.setCurrency("USD");
        account.setBalance(BigDecimal.ZERO);

        doReturn(account).when(accountDao).findById(accountId);

        accountService.creditAccount(accountId, BigDecimal.TEN);
    }
}

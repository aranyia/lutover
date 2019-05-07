package org.lutover.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lutover.app.ContextHolder;
import org.lutover.data.Account;
import org.lutover.data.Context;
import org.lutover.data.Transaction;
import org.lutover.data.TransferRequest;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class TransferFacadeTest {

    private TransferFacade transferFacade;

    @Mock
    private AccountService accountService;

    @Mock
    private FxService fxService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private ContextHolder contextHolder;

    @Before
    public void init() {
        final ExecutorService executorService = new ScheduledThreadPoolExecutor(1);
        transferFacade = new TransferFacadeImpl(accountService, fxService, transactionService, executorService, contextHolder);
    }

    @Test
    public void testTransferThenSuccess() {
        final String sourceAccountId = "RV10000001";
        final String targetAccountId = "RV10000002";

        final TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSourceAccountId(sourceAccountId);
        transferRequest.setTargetAccountId(targetAccountId);
        transferRequest.setAmount(BigDecimal.ONE);

        final Context context = new Context();
        context.setIdempotenceId("A11000001");

        final Account sourceAccount = new Account();
        sourceAccount.setId(sourceAccountId);
        sourceAccount.setCurrency("USD");
        sourceAccount.setBalance(BigDecimal.TEN);

        final Account targetAccount = new Account();
        targetAccount.setId(targetAccountId);
        targetAccount.setCurrency("USD");
        targetAccount.setBalance(BigDecimal.ZERO);

        doReturn(sourceAccount).when(accountService).getAccount(sourceAccountId);
        doReturn(targetAccount).when(accountService).getAccount(targetAccountId);

        transferFacade.transfer(transferRequest, context);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTransferWithRepeatIdempotenceIdThenException() {
        final String sourceAccountId = "RV10000001";
        final String targetAccountId = "RV10000002";

        final TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSourceAccountId(sourceAccountId);
        transferRequest.setTargetAccountId(targetAccountId);
        transferRequest.setAmount(BigDecimal.ONE);

        final Context context = new Context();
        context.setIdempotenceId("A11000001");

        doThrow(IllegalArgumentException.class).when(contextHolder).validateIdempotence(context);

        transferFacade.transfer(transferRequest, context);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTransferWithInvalidAmountThenException() {
        final String sourceAccountId = "RV10000001";
        final String targetAccountId = "RV10000002";

        final TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSourceAccountId(sourceAccountId);
        transferRequest.setTargetAccountId(targetAccountId);
        transferRequest.setAmount(BigDecimal.ZERO);

        final Context context = new Context();
        context.setIdempotenceId("A11000000");

        transferFacade.transfer(transferRequest, context);
    }

    @Test
    public void testGetByReferenceThenSuccess() {
        final String transactionReference = UUID.randomUUID().toString();

        final Context context = new Context();
        context.setIdempotenceId("A11000002");

        final Transaction expectedTransaction = new Transaction();
        expectedTransaction.setReference(transactionReference);

        doReturn(expectedTransaction).when(transactionService).getTransaction(transactionReference, context);

        final Transaction actualTransaction = transferFacade.getByReference(transactionReference, context);

        assertThat(actualTransaction, notNullValue());
        assertThat(actualTransaction.getReference(), is(transactionReference));
    }
}

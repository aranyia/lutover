package org.lutover.app;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.lutover.data.dao.AccountDao;
import org.lutover.data.dao.AccountDaoImpl;
import org.lutover.data.dao.FxRateDao;
import org.lutover.data.dao.FxRateDaoImpl;
import org.lutover.data.dao.H2DbManager;
import org.lutover.data.dao.TransactionDao;
import org.lutover.data.dao.TransactionDaoImpl;
import org.lutover.service.AccountService;
import org.lutover.service.AccountServiceImpl;
import org.lutover.service.FxService;
import org.lutover.service.FxServiceImpl;
import org.lutover.service.TransactionService;
import org.lutover.service.TransactionServiceImpl;
import org.lutover.service.TransferFacade;
import org.lutover.service.TransferFacadeImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Guice module which defines bindings for the dependency injection of
 * account and transaction related services & DAO layer.
 */
public class TransferModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AccountService.class).to(AccountServiceImpl.class);
        bind(AccountDao.class).to(AccountDaoImpl.class);
        bind(FxService.class).to(FxServiceImpl.class);
        bind(FxRateDao.class).to(FxRateDaoImpl.class);
        bind(TransactionService.class).to(TransactionServiceImpl.class);
        bind(TransactionDao.class).to(TransactionDaoImpl.class);
        bind(TransferFacade.class).to(TransferFacadeImpl.class);
    }

    @Provides
    public ExecutorService getExecutorService() {
        return new ScheduledThreadPoolExecutor(1);
    }

    @Provides
    public ContextHolder getContextHolder() {
        return new ContextHolder();
    }

    @Provides
    public H2DbManager getDbManager() {
        return new H2DbManager();
    }
}

package org.lutover.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lutover.app.api.v1.TransactionResponse;
import org.lutover.app.api.v1.TransferRequest;
import org.lutover.data.TransactionStatus;
import org.lutover.data.dao.H2DbManager;
import spark.Spark;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class LutoverTransferIntegrationTest {

    private ObjectMapper objectMapper;

    private HttpClient httpClient;

    @Before
    public void init() {
        httpClient = new HttpClient("http://localhost:8090");
        objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        Application.main(new String[]{});
        Spark.awaitInitialization();
    }

    @After
    public void tearDown() throws SQLException {
        H2DbManager.closeConnection();

        Spark.stop();
        Spark.awaitStop();
    }

    @Test
    public void testTransferThenSuccess() throws IOException {
        final BigDecimal debitAmount = new BigDecimal("2.00");
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSourceAccountId("RV10000102");
        transferRequest.setTargetAccountId("RV10000101");
        transferRequest.setAmount(debitAmount);

        final HttpClient.Response response = doPostWithIdempotenceId("/api/v1/transfer", transferRequest, "A10000001");
        final TransactionResponse transactionResponse = objectMapper.readValue(response.body, TransactionResponse.class);

        assertThat(response.status, is(200));
        assertThat(transactionResponse, notNullValue());
        assertThat(transactionResponse.getReference(), notNullValue());
        assertThat(transactionResponse.getDebitAmount(), is(debitAmount));
    }

    @Test
    public void testTransferThenSuccessThenGetStatus() throws IOException {
        final BigDecimal debitAmount = new BigDecimal("15.00");
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSourceAccountId("RV10000103");
        transferRequest.setTargetAccountId("RV10000104");
        transferRequest.setAmount(debitAmount);

        final HttpClient.Response response = doPostWithIdempotenceId("/api/v1/transfer", transferRequest, "A10000001");
        final TransactionResponse transactionResponse = objectMapper.readValue(response.body, TransactionResponse.class);

        final HttpClient.Response statusResponse = httpClient.get("/api/v1/transfer/" + transactionResponse.getReference());
        final TransactionResponse statusTransactionResponse = objectMapper.readValue(statusResponse.body, TransactionResponse.class);

        assertThat(response.status, is(200));
        assertThat(transactionResponse, notNullValue());
        assertThat(transactionResponse.getReference(), notNullValue());
        assertThat(transactionResponse.getDebitAmount(), is(debitAmount));

        assertThat(statusResponse.status, is(200));
        assertThat(statusTransactionResponse, notNullValue());
        assertThat(statusTransactionResponse.getReference(), is(transactionResponse.getReference()));
        assertThat(statusTransactionResponse.getStatus(), is(TransactionStatus.PROCESSED.name()));
    }

    @Test
    public void testMultipleTransfersThenSuccess() throws IOException {
        final BigDecimal debitAmountFirst = new BigDecimal("2.00");
        TransferRequest transferRequestFirst = new TransferRequest();
        transferRequestFirst.setSourceAccountId("RV10000102");
        transferRequestFirst.setTargetAccountId("RV10000101");
        transferRequestFirst.setAmount(debitAmountFirst);

        final BigDecimal debitAmountSecond = new BigDecimal("3.00");
        TransferRequest transferRequestSecond = new TransferRequest();
        transferRequestSecond.setSourceAccountId("RV10000102");
        transferRequestSecond.setTargetAccountId("RV10000101");
        transferRequestSecond.setAmount(debitAmountSecond);

        final HttpClient.Response responseFirst = doPostWithIdempotenceId("/api/v1/transfer", transferRequestFirst, "A10000001");
        final TransactionResponse transactionResponseFirst = objectMapper.readValue(responseFirst.body, TransactionResponse.class);

        final HttpClient.Response responseSecond = doPostWithIdempotenceId("/api/v1/transfer", transferRequestSecond, "A10000002");
        final TransactionResponse transactionResponseSecond = objectMapper.readValue(responseSecond.body, TransactionResponse.class);

        assertThat(responseFirst.status, is(200));
        assertThat(transactionResponseFirst, notNullValue());
        assertThat(transactionResponseFirst.getReference(), notNullValue());
        assertThat(transactionResponseFirst.getDebitAmount(), is(debitAmountFirst));

        assertThat(responseSecond.status, is(200));
        assertThat(transactionResponseSecond, notNullValue());
        assertThat(transactionResponseSecond.getReference(), notNullValue());
        assertThat(transactionResponseSecond.getDebitAmount(), is(debitAmountSecond));
    }

    @Test
    public void testMultipleTransfersWithSameIdempotenceIdThenFail() throws IOException {
        final BigDecimal debitAmount = new BigDecimal("2.00");
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSourceAccountId("RV10000102");
        transferRequest.setTargetAccountId("RV10000101");
        transferRequest.setAmount(debitAmount);

        final String idempotenceId = "A10000001";

        final HttpClient.Response response = doPostWithIdempotenceId("/api/v1/transfer", transferRequest, idempotenceId);
        final TransactionResponse transactionResponse = objectMapper.readValue(response.body, TransactionResponse.class);

        assertThat(response.status, is(200));
        assertThat(transactionResponse, notNullValue());
        assertThat(transactionResponse.getReference(), notNullValue());
        assertThat(transactionResponse.getDebitAmount(), is(debitAmount));

        try {
            doPostWithIdempotenceId("/api/v1/transfer", transferRequest, "A10000001");
        } catch (IOException exception) {
            assertThat(exception.getMessage().contains("response code: 400"), is(Boolean.TRUE));
        }
    }

    @Test
    public void testTransferWhenInsufficientBalanceThenFail() {
        final BigDecimal debitAmount = new BigDecimal("20000.00");
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSourceAccountId("RV10000102");
        transferRequest.setTargetAccountId("RV10000101");
        transferRequest.setAmount(debitAmount);

        try {
            doPostWithIdempotenceId("/api/v1/transfer", transferRequest, "A10000001");
        } catch (IOException exception) {
            assertThat(exception.getMessage().contains("response code: 400"), is(Boolean.TRUE));
        }
    }

    private HttpClient.Response doPostWithIdempotenceId(String path, Object entity, String idempotenceId) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        headers.put("x-idempotence-id", idempotenceId);

        return httpClient.post(path, objectMapper.writeValueAsBytes(entity), headers);
    }
}

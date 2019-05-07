package org.lutover.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.lutover.app.handlers.CreateTransferHandler;
import org.lutover.app.handlers.GetTransferHandler;
import org.lutover.app.handlers.InternalServerErrorHandler;
import org.lutover.app.handlers.TransferExceptionHandler;
import org.lutover.data.dao.H2DbManager;
import org.lutover.service.TransferFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.defaultResponseTransformer;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.internalServerError;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;

/**
 * SparkJava application with initialization and definition of transfer API handlers.
 */
public class Application {

    private static final int PORT_DEFAULT = 8090;

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(SerializationFeature.INDENT_OUTPUT, Boolean.TRUE);

    public static void main(String[] args) {
        port(PORT_DEFAULT);

        initializeDatabase();

        defaultResponseTransformer(objectMapper::writeValueAsString);

        internalServerError(new InternalServerErrorHandler());
        exception(IllegalArgumentException.class, new TransferExceptionHandler(objectMapper));
        exception(IllegalStateException.class, new TransferExceptionHandler(objectMapper));

        final Injector transferInjector = Guice.createInjector(new TransferModule());
        final TransferFacade transferFacade = transferInjector.getInstance(TransferFacade.class);

        path("/api", () -> {
            path("/v1/transfer", () -> {
                post("", new CreateTransferHandler(objectMapper, transferFacade));
                get("/:ref", new GetTransferHandler(transferFacade));
            });

            before("/*", (request, response) ->
                    log.info("request (ID: {}) body: {}", request.headers("x-idempotence-id"), request.body()));
            after("/v1/*", (request, response) -> response.type("application/json; version=1"));
        });
    }

    private static void initializeDatabase() {
        try {
            H2DbManager.createDB();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
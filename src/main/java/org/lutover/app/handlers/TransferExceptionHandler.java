package org.lutover.app.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.http.HttpStatus;
import org.lutover.app.api.v1.ErrorResponse;
import org.lutover.app.mapper.ContextMapper;
import org.lutover.data.Context;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;

/**
 * Handles exceptions to map them to standardized error responses.
 */
public class TransferExceptionHandler implements ExceptionHandler<Exception> {

    private final ObjectMapper objectMapper;

    public TransferExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(Exception exception, Request request, Response response) {
        response.status(HttpStatus.BAD_REQUEST_400);
        response.type("application/json; version=1");

        final Context context = ContextMapper.map(request);

        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorType(exception.getClass().getSimpleName());
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setPath(request.pathInfo());
        errorResponse.setMethod(request.requestMethod());
        if (context != null) {
            errorResponse.setIdempotenceId(context.getIdempotenceId());
        }

        try {
            response.body(objectMapper.writeValueAsString(errorResponse));
        } catch (JsonProcessingException jsonException) {
            throw new RuntimeException(jsonException);
        }
    }
}

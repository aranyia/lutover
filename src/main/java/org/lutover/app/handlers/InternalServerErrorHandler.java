package org.lutover.app.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.http.HttpStatus;
import org.lutover.app.api.v1.ErrorResponse;
import org.lutover.app.mapper.ContextMapper;
import org.lutover.data.Context;
import spark.Request;
import spark.Response;
import spark.Route;

public class InternalServerErrorHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
        response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
        response.type("application/json; version=1");

        final Context context = ContextMapper.map(request);

        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorType("Internal server error");
        errorResponse.setMessage("Unexpected server error occurred");
        errorResponse.setPath(request.pathInfo());
        errorResponse.setMethod(request.requestMethod());
        if (context != null) {
            errorResponse.setIdempotenceId(context.getIdempotenceId());
        }

        return errorResponse;
    }
}

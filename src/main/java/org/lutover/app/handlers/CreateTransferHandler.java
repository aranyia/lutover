package org.lutover.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lutover.app.mapper.ApiV1Mapper;
import org.lutover.app.mapper.ContextMapper;
import org.lutover.service.TransferFacade;
import org.lutover.data.TransferRequest;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Initiates transactions between two account based on the incoming transfer request.
 */
public class CreateTransferHandler implements Route {

    private final ObjectMapper objectMapper;

    private final TransferFacade transferFacade;

    public CreateTransferHandler(ObjectMapper objectMapper, TransferFacade transferFacade) {
        this.objectMapper = objectMapper;
        this.transferFacade = transferFacade;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        final TransferRequest transferRequest =
                ApiV1Mapper.mapFromApi(objectMapper.readValue(request.body(), org.lutover.app.api.v1.TransferRequest.class));

        return ApiV1Mapper.mapToApi(transferFacade.transfer(transferRequest, ContextMapper.map(request)));
    }
}

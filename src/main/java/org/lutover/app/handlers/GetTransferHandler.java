package org.lutover.app.handlers;

import org.lutover.app.mapper.ApiV1Mapper;
import org.lutover.app.mapper.ContextMapper;
import org.lutover.service.TransferFacade;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Retrieves a transaction identified by its transaction reference.
 * Can be used to track the status of a transaction initiated as a result of a transfer request.
 */
public class GetTransferHandler implements Route {

    private final TransferFacade transferFacade;

    public GetTransferHandler(TransferFacade transferFacade) {
        this.transferFacade = transferFacade;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        final String transactionReference = request.params(":ref");

        return ApiV1Mapper.mapToApi(transferFacade.getByReference(transactionReference, ContextMapper.map(request)));
    }
}

package org.lutover.app.api.v1;

import lombok.Data;

@Data
public class ErrorResponse {

    private String errorType;

    private String message;

    private String path;

    private String method;

    private String idempotenceId;

}

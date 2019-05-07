package org.lutover.app.mapper;

import org.lutover.data.Context;
import spark.Request;

/**
 * Mapping incoming request to be used as context for transaction,
 * to identify requesting agents and ensuring to be idempotent.
 */
public interface ContextMapper {

    static Context map(Request request) {
        final Context context = new Context();
        context.setAgentId(request.userAgent());
        context.setIdempotenceId(request.headers("x-idempotence-id"));
        return context;
    }
}

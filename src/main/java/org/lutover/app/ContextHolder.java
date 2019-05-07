package org.lutover.app;

import org.lutover.data.Context;

import java.util.LinkedList;

/**
 * Holding context of incoming requests. Used to ensure that transfer requests are idempotent.
 * <p>
 * Production-ready implementation could utilize distributed caching (eg. Hazelcast, Redis), to ensure
 * requests are idempotent across all instances of the service.
 */
public class ContextHolder {

    private static final int REQUEST_RECORD_WINDOW_DEFAULT = 200;

    private final LinkedList<String> recentIdempotenceIds;

    ContextHolder() {
        this(REQUEST_RECORD_WINDOW_DEFAULT);
    }

    ContextHolder(int requestRecordWindow) {
        recentIdempotenceIds = new LimitedQueue<>(requestRecordWindow);
    }

    public void validateIdempotence(Context context) {
        if (context == null || context.getIdempotenceId() == null) {
            throw new IllegalArgumentException("context must not be empty");
        }

        final String idempotenceId = context.getIdempotenceId();
        if (recentIdempotenceIds.contains(idempotenceId)) {
            throw new IllegalArgumentException("request processed before with same idempotence ID");
        } else {
            recentIdempotenceIds.push(idempotenceId);
        }
    }

    private static class LimitedQueue<E> extends LinkedList<E> {

        private int limit;

        LimitedQueue(int limit) {
            this.limit = limit;
        }

        @Override
        public boolean add(E o) {
            final boolean addResult = super.add(o);
            while (size() > limit) {
                super.remove();
            }
            return addResult;
        }
    }
}

package org.fs.publication.events;

import org.fs.common.IEvent;

/**
 * Created by Fatih on 05/06/16.
 * as org.fs.publication.events.BookReadFailure
 */
public final class BookReadFailure implements IEvent {

    private final Throwable throwable;

    public BookReadFailure(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getCause() {
        return this.throwable;
    }
}
package org.fs.publication.events;

import org.fs.common.IEvent;

/**
 * Created by Fatih on 05/06/16.
 * as org.fs.publication.events.PageSelectedWithIndex
 */
public final class PageSelectedWithIndex implements IEvent {

    private final int contentIndex;

    public PageSelectedWithIndex(final int contentIndex) {
        this.contentIndex = contentIndex;
    }

    public int contentIndex() {
        return contentIndex;
    }
}
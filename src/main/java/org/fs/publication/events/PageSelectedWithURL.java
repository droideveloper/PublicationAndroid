package org.fs.publication.events;

import org.fs.common.IEvent;

/**
 * Created by Fatih on 05/06/16.
 * as org.fs.publication.events.PageSelectedWithURL
 * this event thrown by either MenuFragmentPresenter or PageFragmentPresenter
 * either way we gone catch it then set the content in PageFragment
 */
public final class PageSelectedWithURL implements IEvent {

    private final String contentURL;

    public PageSelectedWithURL(final String contentURL) {
        this.contentURL = contentURL;
    }

    public String contentURL() {
        return contentURL;
    }
}
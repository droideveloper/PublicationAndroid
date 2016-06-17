package org.fs.publication.events;

import org.fs.common.IEvent;
import org.fs.publication.entities.Download;

/**
 * Created by Fatih on 06/06/16.
 * as org.fs.publication.events.DownloadEventFound
 */
public final class DownloadFound implements IEvent {

    private final Download download;

    public DownloadFound(Download download) {
        this.download = download;
    }

    public Download getDownload() {
        return download;
    }
}
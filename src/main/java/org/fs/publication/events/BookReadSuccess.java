package org.fs.publication.events;

import org.fs.common.IEvent;
import org.fs.publication.entities.Configuration;

import java.io.File;

/**
 * Created by Fatih on 05/06/16.
 * as org.fs.publication.events.BookReadSuccess
 */
public class BookReadSuccess implements IEvent {

    private final File          directory;
    private final Configuration config;

    public BookReadSuccess(File directory, Configuration config) {
        this.directory = directory;
        this.config = config;
    }

    public File getDirectory() {
        return directory;
    }

    public Configuration getConfig() {
        return config;
    }
}

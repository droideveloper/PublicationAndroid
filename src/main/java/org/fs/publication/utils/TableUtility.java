package org.fs.publication.utils;

import org.fs.exception.AndroidException;

/**
 * Created by Fatih on 05/06/16.
 * as org.fs.publication.utils.TableUtility
 */
public final class TableUtility {

    /**
     * SQLiteTable
     */
    public static class DownloadTable {
        public static final String TABLE_NAME       = "cover_downloads_completed";
        public static final String CLM_COVER_NAME   = "cover_name";
        public static final String CLM_FILE_PATH    = "cover_file_path";
    }

    private TableUtility() {
        throw new AndroidException("no instance allowed, don't be jackass please...");
    }
}

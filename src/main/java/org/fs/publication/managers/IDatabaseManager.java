package org.fs.publication.managers;

import com.j256.ormlite.support.ConnectionSource;

import org.fs.publication.entities.Book;
import org.fs.publication.entities.Download;

import java.sql.SQLException;

/**
 * Created by Fatih on 05/06/16.
 * as org.fs.publication.managers.IDatabaseManager
 */
public interface IDatabaseManager {

    void        saveOrUpdate(Download download);
    void        delete(Download download);
    void        hasDownload(Book book);

    void        dropTables(ConnectionSource conn) throws SQLException;
    void        createTables(ConnectionSource conn) throws SQLException;
}

package org.fs.publication.managers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.fs.common.BusManager;
import org.fs.core.AbstractApplication;
import org.fs.exception.AndroidException;
import org.fs.publication.R;
import org.fs.publication.entities.Book;
import org.fs.publication.entities.Download;
import org.fs.publication.events.DownloadFound;
import org.fs.publication.utils.TableUtility;
import org.fs.util.PreconditionUtility;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Fatih on 06/06/16.
 * as org.fs.publication.managers.DatabaseManager
 */
public final class DatabaseManager extends OrmLiteSqliteOpenHelper implements IDatabaseManager {

    private final static String DB_NAME    = "publication.db";
    private final static int    DB_VERSION = 1;

    private final BusManager busManager;

    public DatabaseManager(Context context, BusManager busManager) {
        super(context, DB_NAME, null, DB_VERSION, R.raw.ormlite_config);
        this.busManager = busManager;
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource conn) {
        try {
            createTables(conn);
        } catch (SQLException e) {
            throw new AndroidException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource conn, int prevVersion, int nextVersion) {
        try {
            dropTables(conn);
            onCreate(db, conn);
        } catch (SQLException e) {
            throw new AndroidException(e);
        }
    }

    @Override public void saveOrUpdate(Download download) {
        PreconditionUtility.checkNotNull(download, "can not update null");
        Observable.just(download)
                  .flatMap(new Func1<Download, Observable<Integer>>() {
                      @Override public Observable<Integer> call(Download download) {
                          RuntimeExceptionDao<Download, Long> downloadDao = getRuntimeExceptionDao(Download.class);
                          if(download.getId() != null) {
                              if(downloadDao.idExists(download.getId())) {
                                  return Observable.just(downloadDao.update(download));
                              }
                              return Observable.error(new AndroidException("either not exits or exist, but what's the 3rd option, you manually added id in object ? why, god why ?"));
                          } else {
                              return Observable.just(downloadDao.create(download));
                          }
                      }
                  })
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Action1<Integer>() {
                      @Override public void call(Integer aInteger) { /*no op*/ }
                  }, new Action1<Throwable>() {
                      @Override public void call(Throwable throwable) {
                          //log if error
                          StringWriter str = new StringWriter();
                          PrintWriter ptr = new PrintWriter(str);
                          throwable.printStackTrace(ptr);
                          log(Log.ERROR, str.toString());
                      }
                  });
    }

    @Override public void delete(Download download) {
        PreconditionUtility.checkNotNull(download, "can not be null");
        Observable.just(download)
                  .flatMap(new Func1<Download, Observable<?>>() {
                      @Override
                      public Observable<?> call(Download download) {
                          RuntimeExceptionDao<Download, Long> downloadDao = getRuntimeExceptionDao(Download.class);
                          return Observable.just(downloadDao.delete(download));
                      }
                  })
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe();
    }

    @Override public void hasDownload(Book book) {
        PreconditionUtility.checkNotNull(book, "can not check on null");
        Observable.just(book)
                  .flatMap(new Func1<Book, Observable<Download>>() {
                      @Override
                      public Observable<Download> call(Book book) {
                          try {
                              RuntimeExceptionDao<Download, Long> downloadDao = getRuntimeExceptionDao(Download.class);
                              //SELECT * from cover_downloads_completed WHERE cover_name = '${book.getName()}';
                              PreparedQuery<Download> query = downloadDao.queryBuilder()
                                                                                .where().eq(TableUtility.DownloadTable.CLM_COVER_NAME, book.getName())
                                                                         .prepare();
                              return Observable.just(downloadDao.queryForFirst(query));
                          } catch (SQLException se) {
                              return Observable.error(se);
                          }
                      }
                  })
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(new Action1<Download>() {
                     @Override public void call(Download download) {
                        busManager.post(new DownloadFound(download));
                     }
                 }, new Action1<Throwable>() {
                     @Override public void call(Throwable throwable) {
                        StringWriter str = new StringWriter();
                        PrintWriter ptr = new PrintWriter(str);
                        throwable.printStackTrace(ptr);
                        log(Log.ERROR, str.toString());
                     }
                 });
    }

    @Override public void dropTables(ConnectionSource conn) throws SQLException {
        TableUtils.dropTable(conn, Download.class, true);
    }

    @Override public void createTables(ConnectionSource conn) throws SQLException {
        TableUtils.createTable(conn, Download.class);
    }

    protected void log(String msg) {
        log(Log.DEBUG, msg);
    }

    protected void log(int lv, String msg) {
        if (isLogEnabled()) {
            Log.println(lv, getClassTag(), msg);
        }
    }

    protected boolean isLogEnabled() {
        return AbstractApplication.isDebug();
    }

    protected String getClassTag() {
        return DatabaseManager.class.getSimpleName();
    }
}

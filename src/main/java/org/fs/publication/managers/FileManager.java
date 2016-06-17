package org.fs.publication.managers;

import android.content.Context;
import android.os.Build;
import android.os.StatFs;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import org.fs.common.BusManager;
import org.fs.core.AbstractApplication;
import org.fs.publication.entities.Configuration;
import org.fs.publication.events.BookReadFailure;
import org.fs.publication.events.BookReadSuccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Fatih on 05/06/16.
 * as org.fs.publication.managers.FileManager
 */
public final class FileManager implements IFileManager {

    private final Gson       gson;
    private final File       directory;
    private final BusManager busManager;

    public FileManager(Context context, Gson gson, BusManager busManager) {
        this.gson = gson;
        this.busManager = busManager;
        this.directory = new File(context.getFilesDir(), File.pathSeparator + "unzipped");
        if(!this.directory.exists()) {
            boolean created = this.directory.mkdirs();
            log(String.format(Locale.US, "%s is created? %s",
                              this.directory.toString(),
                              String.valueOf(created)));
        }
    }

    @Override public boolean hasEnoughStorage(long required) {
        StatFs stats = new StatFs(directory.getPath());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return required <= (stats.getBlockSizeLong() * stats.getBlockCountLong());
        } else {
            return required <= (stats.getBlockSize() * stats.getBlockCount());
        }
    }

    @Override public boolean newDirectory(File parent, String newDirectoryName) {
        return new File(parent, newDirectoryName).mkdirs();
    }

    @Override public void newFile(File parent, String fileName, ZipInputStream zis) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(parent, fileName));
        try {
            byte[] buffer = new byte[8096];
            for (int read = zis.read(buffer); read != -1; read = zis.read(buffer)) {
                fos.write(buffer, 0, read);
            }
        } finally {
            fos.close();
        }
    }

    @Override public void extract(final File file) {
        Observable.just(file)
                  .flatMap(new Func1<File, Observable<Configuration>>() {
                      @Override public Observable<Configuration> call(File file) {
                          try {
                              //TODO if extracted already leave it be just read .json config file
                              newDirectory(directory, file.getName());
                              readStream(new ZipInputStream(new FileInputStream(file)), new File(directory, file.getName()));
                              return Observable.just(readContentsInfo(new File(directory, file.getName())));
                          } catch (IOException ioe) {
                              log(ioe);
                          }
                          return null;
                      }
                  })
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Action1<Configuration>() {
                      @Override public void call(Configuration config) {
                            busManager.post(new BookReadSuccess(new File(directory, file.getName()), config));
                      }
                  }, new Action1<Throwable>() {
                      @Override public void call(Throwable throwable) {
                            busManager.post(new BookReadFailure(throwable));
                      }
                  });
    }

    @Override public void readStream(ZipInputStream zis, File directory) throws IOException {
        try {
            for (ZipEntry zipEntry = zis.getNextEntry(); zipEntry != null; zipEntry = zis.getNextEntry()) {
                final String entryName = zipEntry.getName();
                if (entryName.startsWith("_") || entryName.startsWith(".")) continue;
                if (zipEntry.isDirectory()) {
                    newDirectory(directory, entryName);
                } else {
                    newFile(directory, entryName, zis);
                }
            }
        } finally {
            zis.close();
        }
    }

    @Override public File findByTagAndGetFirst(File directory, final String tag) {
        File[] array = directory.listFiles(new FilenameFilter() {
            @Override public boolean accept(File dir, String filename) {
                return filename.contains(tag);
            }
        });
        return (array != null && array.length > 0) ? array[0] : null;
    }

    @Override public Configuration readContentsInfo(File directory) throws IOException {
        File jsonFile = findByTagAndGetFirst(directory, ".json");
        if(jsonFile != null) {
            if(gson != null) {
                TypeToken<Configuration> type = TypeToken.get(Configuration.class);
                TypeAdapter<Configuration> adapter = gson.getAdapter(type);
                JsonReader reader = gson.newJsonReader(new FileReader(jsonFile));
                try {
                    return adapter.read(reader);
                } finally {
                    reader.close();
                }
            }
        } else {
            log("can not find *.json file");
        }
        return null;
    }

    /*Log Helper Methods*/

    @Override public void log(String msg) {
        log(Log.DEBUG, msg);
    }

    @Override public void log(Exception exp) {
        StringWriter strWriter = new StringWriter();
        PrintWriter prtWriter = new PrintWriter(strWriter);
        exp.printStackTrace(prtWriter);
        log(Log.ERROR, strWriter.toString());
    }

    @Override public void log(int lv, String msg) {
        if(isLogEnabled()) {
            Log.println(lv, getClassTag(), msg);
        }
    }

    @Override public String getClassTag() {
        return FileManager.class.getSimpleName();
    }

    @Override public boolean isLogEnabled() {
        return AbstractApplication.isDebug();
    }
}

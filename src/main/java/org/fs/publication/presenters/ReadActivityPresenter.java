package org.fs.publication.presenters;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;

import org.fs.common.AbstractPresenter;
import org.fs.common.BusManager;
import org.fs.core.AbstractApplication;
import org.fs.publication.R;
import org.fs.publication.entities.Configuration;
import org.fs.publication.events.BookReadFailure;
import org.fs.publication.events.BookReadSuccess;
import org.fs.publication.managers.IFileManager;
import org.fs.publication.views.ContentsFragmentView;
import org.fs.publication.views.IReadActivityView;
import org.fs.publication.views.MenuFragmentView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Fatih on 06/06/16.
 * as org.fs.publication.presenters.ReadActivityPresenter
 */
public class ReadActivityPresenter extends AbstractPresenter<IReadActivityView> implements IReadActivityPresenter, Action1<Object> {

    private static final String INDEX_FILE  = "index.html";
    private static final String INDEX_FILE2 = "index.htm";

    public static final String KEY_DOWNLOADED_FILE = "downloaded.file";
    public static final String KEY_DIRECTORY       = "extracted.directory";
    public static final String KEY_CONFIGURATION   = "downloaded.configuration";

    private File          file;
    private File          directory;
    private Configuration configuration;
    private Subscription  busListener;

    @Inject BusManager      busManager;
    @Inject IFileManager    fileManager;

    public ReadActivityPresenter(IReadActivityView view) {
        super(view);
    }

    @Override public void restoreState(Bundle input) {
        if(input != null) {
            String filePath = input.getString(KEY_DOWNLOADED_FILE);
            if(filePath != null) {
                file = new File(filePath);
            }
            String directoryPath = input.getString(KEY_DIRECTORY);
            if(directoryPath != null) {
                directory = new File(directoryPath);
            }
            configuration = input.getParcelable(KEY_CONFIGURATION);
        }
    }

    @Override public void storeState(Bundle output) {
        if(file != null) {
            output.putString(KEY_DOWNLOADED_FILE, file.getPath());
        }
        if(directory != null) {
            output.putString(KEY_DIRECTORY, directory.getPath());
        }
        if(configuration != null) {
            output.putParcelable(KEY_CONFIGURATION, configuration);
        }
    }

    @Override public void extractAsync(File file) {
        if(fileManager.hasEnoughStorage(file.length())) {
            view.showProgress();
            fileManager.extract(file);
        } else {
            String errorText = String.format(Locale.US, "Device does not have enough space. required : %d", file.length());//TODO put res sources
            notifyError(errorText);
        }
    }

    @Override public void call(Object event) {
        if(event instanceof BookReadSuccess || event instanceof BookReadFailure) {
            if(event instanceof BookReadSuccess) {
                configuration   = ((BookReadSuccess) event).getConfig();
                directory       = ((BookReadSuccess) event).getDirectory();

                ContentsFragmentView fragmentContentsView = ContentsFragmentView.newInstance(directory);
                view.replace(R.id.vgContent, fragmentContentsView);

                File menuFile = findMenuFile(directory);
                if(isMenuFileExists(menuFile)) {
                    MenuFragmentView fragmentMenuView = MenuFragmentView.newInstance(menuFile.getPath(),
                                                                                     configuration.getContents(),
                                                                                     configuration.getTitle());
                    view.replace(R.id.vgMenu, fragmentMenuView);
                }
            } else {
                String errorText = String.format(Locale.US, "Error is occured while extracting %s", file.getName());//TODO put res sources
                notifyError(errorText);
            }
            view.hideProgress();
        }
    }

    @Override public void notifyError(String errorText) {
        final Snackbar errorView = Snackbar.make(view.getContentView(), errorText, Snackbar.LENGTH_INDEFINITE);
        errorView.setAction("OK", new View.OnClickListener() {
            @Override public void onClick(View v) {
                view.hideError(errorView);
            }
        });
        view.showError(errorView);
    }

    @Override public boolean isMenuFileExists(File menuFile) {
        return menuFile != null && menuFile.exists();
    }

    @Override public File findMenuFile(File parent) {
        File[] files = parent.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String fileName) {
                return fileName.contains(INDEX_FILE) || fileName.contains(INDEX_FILE2);
            }
        });
        return (files != null && files.length > 0) ? files[0] : null;
    }

    @Override public void onCreate() {
        view.getActivityComponent().inject(this);
        busListener = busManager.register(this);
        extractAsync(file);
    }

    @Override public void onStart() {
        //no op
    }

    @Override public void onStop() {
        if(busListener != null) {
            busManager.unregister(busListener);
            busListener = null;
        }
    }

    @Override public void onDestroy() {
        //no op
    }

    @Override protected String getClassTag() {
        return ReadActivityPresenter.class.getSimpleName();
    }

    @Override protected boolean isLogEnabled() {
        return AbstractApplication.isDebug();
    }

}
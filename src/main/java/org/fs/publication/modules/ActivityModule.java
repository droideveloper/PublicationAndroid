package org.fs.publication.modules;

import android.app.Activity;

import com.google.gson.Gson;

import org.fs.common.BusManager;
import org.fs.publication.adapters.BookRecyclerAdapter;
import org.fs.publication.components.ForActivity;
import org.fs.publication.managers.DatabaseManager;
import org.fs.publication.managers.FileManager;
import org.fs.publication.managers.IDatabaseManager;
import org.fs.publication.managers.IFileManager;
import org.fs.publication.managers.IPreferenceManager;
import org.fs.publication.managers.PreferenceManager;
import org.fs.publication.nets.IServiceEndpoint;
import org.fs.publication.views.ShelfActivityView;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by Fatih on 06/06/16.
 * as org.fs.publication.modules.ActivityModule
 */
@Module
public class ActivityModule {

    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @ForActivity @Provides public IFileManager provideFileManager(Gson gson, BusManager busManager) {
        return new FileManager(activity, gson, busManager);
    }

    @ForActivity @Provides public IDatabaseManager provideDatabaseManager(BusManager busManager) {
        return new DatabaseManager(activity, busManager);
    }

    @ForActivity @Provides public IServiceEndpoint provideServiceEndpoint(Retrofit retrofit) {
        return retrofit.create(IServiceEndpoint.class);
    }

    @ForActivity @Provides public IPreferenceManager providePreferenceManager() {
        return new PreferenceManager(activity);
    }

    @ForActivity @Provides public BookRecyclerAdapter provideAdapter() {
        return new BookRecyclerAdapter(((ShelfActivityView) activity).bindPresenter());
    }
}

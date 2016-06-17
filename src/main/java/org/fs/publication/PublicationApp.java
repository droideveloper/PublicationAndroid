package org.fs.publication;

import org.fs.core.AbstractApplication;
import org.fs.publication.components.AppComponent;
import org.fs.publication.components.DaggerAppComponent;
import org.fs.publication.modules.AppModule;

/**
 * Created by Fatih on 06/06/16.
 * as org.fs.publication.PublicationApp
 */
public class PublicationApp extends AbstractApplication {

    private final static String BASE_URL    = "http://bakerframework.com/"; //TODO change value
    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";        //TODO change value

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                                         .appModule(new AppModule(this, BASE_URL, DATE_FORMAT))
                                         .build();
    }

    @Override protected String getClassTag() {
        return PublicationApp.class.getSimpleName();
    }

    @Override protected boolean isLogEnabled() {
        return isDebug();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
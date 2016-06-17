package org.fs.publication.components;

import com.google.gson.Gson;

import org.fs.common.BusManager;
import org.fs.publication.modules.AppModule;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

/**
 * Created by Fatih on 06/06/16.
 * as org.fs.publication.components.AppComponent
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    Retrofit    getRetrofit();
    BusManager  getBusManager();
    Gson        getGson();
}

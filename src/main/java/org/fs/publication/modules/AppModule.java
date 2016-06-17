package org.fs.publication.modules;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.fs.common.BusManager;
import org.fs.net.RxJavaCallAdapterFactory;
import org.fs.net.converter.GsonConverterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by Fatih on 06/06/16.
 * as org.fs.publication.modules.AppModule
 */
@Module
public class AppModule {

    private final Context context;
    private final String baseUrl;
    private final String dateStr;

    public AppModule(Context context, String baseUrl, String dateStr) {
        this.context = context;
        this.baseUrl = baseUrl;
        this.dateStr = dateStr;
    }

    @Singleton @Provides public Context provideContext() {
        return context;
    }

    @Singleton @Provides public BusManager provideBusManager() {
        return new BusManager();
    }

    @Singleton @Provides public OkHttpClient provideHttpClient() {
        return new OkHttpClient();
    }

    @Singleton @Provides public Gson provideGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat(dateStr);
        return builder.create();
    }

    @Singleton @Provides public Retrofit provideRetrofit(Gson gson, OkHttpClient client) {
        return new Retrofit.Builder()
                           .baseUrl(baseUrl)
                           .client(client)
                           .addCallAdapterFactory(RxJavaCallAdapterFactory.create())        //for service method return Observable<T>
                           .addConverterFactory(GsonConverterFactory.createWithGson(gson))  //for json to object serialization
                           .build();
    }
}

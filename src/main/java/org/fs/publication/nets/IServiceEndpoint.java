package org.fs.publication.nets;

import org.fs.publication.entities.Book;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Fatih on 04/06/16.
 * as org.fs.publication.nets.IServiceEndpoint
 */
public interface IServiceEndpoint {

    //TODO change this for your methods, as required with @see PublicationApp#BASE_URL
    @GET("demo/{shelfName}")
    Observable<List<Book>> fetchBookByPath(@Path("shelfName") String shelfName);
}

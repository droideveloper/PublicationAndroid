package org.fs.publication.presenters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;

import org.fs.common.IPresenter;
import org.fs.publication.adapters.BookViewHolder;
import org.fs.publication.entities.Book;

/**
 * Created by Fatih on 07/06/16.
 * as org.fs.publication.presenters.ISelfActivityPresenter
 */
public interface IShelfActivityPresenter extends IPresenter {

    void restoreState(Bundle input);
    void storeState(Bundle output);
    void fetchShelfAsync();
    void handleError(@StringRes int stringId);

    void actionCancel(Book book);
    void actionDownload(Book book);
    void actionRead(Book book);
    void actionDelete(Book book);

    BookViewHolder.OnViewHolderSelectedListener bindSelectedListener();
    Context                                     bindContext();
}
package org.fs.publication.adapters;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.view.View;

import org.fs.evoke.NetworkJob;
import org.fs.publication.entities.Book;
import org.fs.publication.entities.Download;

/**
 * Created by Fatih on 07/06/16.
 * as org.fs.publication.adapters.IBookViewHolder
 */
public interface IBookViewHolder {

    /**
     * Show progress if Book is downloading, percentage in our progress
     * @param percent place of current index in 100%
     */
    void showProgress(int percent);

    /**
     * Disable progress we do not need it, default is disabled!
     */
    void hideProgress();

    /**
     * if we do not have a download on regarding this book object we do not show delete, simple deletes cached object
     */
    void showDeleteAction();

    /**
     * hide button for delete operation which is defaults
     */
    void hideDeleteAction();

    /**
     * after an operation triggered one of the next operation will be selected kinda logic on one button
     * @param stringId
     */
    void changeActionText(@StringRes int stringId);

    /**
     * called from Adapter not from outside of adapter do not use it
     * @param book book entity instance
     * @param listener listener that will implemented in presenter of ShelfActivityView ;)
     */
    void setBookAndListener(Book book, BookViewHolder.OnViewHolderSelectedListener listener);

    /**
     * we need this object before download we might want to cancel job
     * @param job network job instance
     */
    void setNetworkJob(NetworkJob job);

    /**
     * wee get to set it because it will provide info for us in read especially
     * @param download Download instance if we have downloaded this book
     */
    void setDownload(Download download);

    /**
     * internal use outside call cause problem, do not use it
     */
    void notifyDataSet();

    /**
     * internal use for now but we can call it as we want
     * @param view view that we are looking on from activity it might seem to hard to call but it's easy actually findViewById(android.R.id.content) then pass this as parent ;)
     * @param viewId id of view look for in parent
     * @param <T> Type of the view anything can be actually the good part of this is, it casts returned value as field value
     * @return instance of T or whatever our field requires as type
     */
    <T> T   findViewById(View view, @IdRes int viewId);

    /**
     * instance we need to get in presenter for some operations
     * @return Book entity instance
     */
    Book    getBookObject();

    /**
     * gets String resource from strings defined text in xml
     * @param stringId id of string resource
     * @return a string if it exits else it will not compile at all
     */
    String  getString(@StringRes int stringId);

    /**
     * if progress is visible or not
     * @return true if visible
     */
    boolean isProgressVisible();

    /**
     * if btnDelete is visible or not
     * @return true if visible
     */
    boolean isDeleteActionVisible();

    /**
     *
     * @return if there is a network job
     */
    NetworkJob getNetworkJob();

    /**
     *
     * @return if there is a download object
     */
    Download   getDownload();
}

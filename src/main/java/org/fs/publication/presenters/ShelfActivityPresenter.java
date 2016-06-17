package org.fs.publication.presenters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import org.fs.common.AbstractPresenter;
import org.fs.common.BusManager;
import org.fs.core.AbstractApplication;
import org.fs.evoke.DownloadManager;
import org.fs.evoke.NetworkJob;
import org.fs.exception.AndroidException;
import org.fs.publication.R;
import org.fs.publication.adapters.BookRecyclerAdapter;
import org.fs.publication.adapters.BookViewHolder;
import org.fs.publication.adapters.IBookViewHolder;
import org.fs.publication.entities.Book;
import org.fs.publication.entities.Download;
import org.fs.publication.events.DownloadFound;
import org.fs.publication.managers.IDatabaseManager;
import org.fs.publication.nets.IServiceEndpoint;
import org.fs.publication.views.IShelfActivityView;
import org.fs.publication.views.ReadActivityView;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Fatih on 08/06/16.
 * as org.fs.publication.presenters.ShelfActivityPresenter
 */
public class ShelfActivityPresenter extends AbstractPresenter<IShelfActivityView> implements IShelfActivityPresenter,
                                                                                             BookViewHolder.OnViewHolderSelectedListener,
                                                                                             Action1<Object>,
                                                                                             SwipeRefreshLayout.OnRefreshListener {

    /**
     * we say that 10 view will be shown at a time is good ?
     * who knows it might be fit to be fully scaled
     */
    private final static int MAX_HOLDER_REF_COUNT = 10;
    private final static String SHELF             = "shelf.json";

    private Map<Book, IBookViewHolder> bookViewHolderReference = new HashMap<>(MAX_HOLDER_REF_COUNT);

    @Inject BusManager          busManager;
    @Inject IDatabaseManager    databaseManager;
    @Inject BookRecyclerAdapter bookAdapter;
    @Inject IServiceEndpoint    serviceEndpoint;

    private Subscription        busListener;

    public ShelfActivityPresenter(IShelfActivityView view) {
        super(view);
    }

    @Override
    public void restoreState(Bundle input) {

    }

    @Override
    public void storeState(Bundle output) {

    }

    @Override public void fetchShelfAsync() {
        if(!view.isRefreshVisible()) {
            view.showRefresh();
        }
        Observable<List<Book>> shelfObservable =  serviceEndpoint.fetchBookByPath(SHELF);
        shelfObservable = shelfObservable.subscribeOn(Schedulers.io());
        shelfObservable = shelfObservable.observeOn(AndroidSchedulers.mainThread());
        shelfObservable.subscribe(new Action1<List<Book>>() {
                           @Override
                           public void call(List<Book> books) {
                                if(view.isRefreshVisible()) {
                                    view.hideRefresh();
                                }
                                bookAdapter.appenData(books, false);
                                bookAdapter.notifyDataSetChanged();
                           }
                       }, new Action1<Throwable>() {
                           @Override
                           public void call(Throwable throwable) {
                               log(new Exception(throwable));
                               throw new AndroidException(throwable);
                           }
                       });

    }

    @Override public BookViewHolder.OnViewHolderSelectedListener bindSelectedListener() {
        return this;
    }

    @Override public void onRefresh() {
        fetchShelfAsync();
    }

    @Override public void onSelectViewHolder(final IBookViewHolder viewHolder, int action) {
        if(viewHolder != null) {
            final Book book = viewHolder.getBookObject();
            if(bookViewHolderReference.containsKey(book)) {
                switch (action) {
                    case BookViewHolder.ACTION_CANCEL:      actionCancel(book);     break;
                    case BookViewHolder.ACTION_DELETE:      actionDelete(book);     break;
                    case BookViewHolder.ACTION_DOWNLOAD:    actionDownload(book);   break;
                    case BookViewHolder.ACTION_READ:        actionRead(book);       break;
                    default: throw new AndroidException("unknown action : " + action);
                }
            }
        }
    }

    @Override public void actionCancel(final Book book) {

        final IBookViewHolder viewHolder = bookViewHolderReference.get(book);
        if(viewHolder != null) {

            NetworkJob job = viewHolder.getNetworkJob();
            if(job != null) {
                DownloadManager.cancel(job.getId());
                viewHolder.setNetworkJob(null);//nullify job
                viewHolder.changeActionText(R.string.btn_download);
                if(viewHolder.isProgressVisible()) {
                    viewHolder.hideProgress();//hide ProgressGroup
                }
            }
        }
    }

    @Override public void actionDelete(final Book book) {

        final IBookViewHolder viewHolder = bookViewHolderReference.get(book);
        if(viewHolder != null) {

            Download download = viewHolder.getDownload();
            if(download != null) {
                databaseManager.delete(download);
                File cachedDownload = new File(download.getFilePath());
                if(cachedDownload.exists()) {
                    cachedDownload.delete();//need to clean up everything
                }
                viewHolder.setDownload(null);//nullify download
                viewHolder.changeActionText(R.string.btn_download);//read --> download
                if(viewHolder.isDeleteActionVisible()) {
                    viewHolder.hideDeleteAction();//hide deleteAction
                }
            }
        }
    }

    @Override public void actionDownload(final Book book) {

        final IBookViewHolder viewHolder = bookViewHolderReference.get(book);
        if(viewHolder != null) {

            NetworkJob job = new NetworkJob.Builder()
                    .fileName(book.getName())
                    .url(book.getUrl())
                    .type(NetworkJob.ConnectionType.UNSPECIFIED)
                    .policy(NetworkJob.Policy.DELETE_ON_ERROR)
                    .build();

            //we gone use it in cancel
            viewHolder.setNetworkJob(job);
            DownloadManager.schedule(job, new DownloadManager.OnJobListener() {
                @Override public void onComplete(File file) {
                    //save it in the database
                    Download download = new Download(book.getName(), file.getPath(), null);
                    databaseManager.saveOrUpdate(download);
                    //change relevant part
                    if (viewHolder.isProgressVisible()) {
                        viewHolder.hideProgress();
                    }
                    viewHolder.showDeleteAction();
                    viewHolder.changeActionText(R.string.btn_read);
                    viewHolder.setNetworkJob(null);//nullify job
                    viewHolder.setDownload(download);//set download in here
                }

                @Override public void onError(int errorCode) {
                    if (viewHolder.isProgressVisible()) {
                        viewHolder.hideProgress();
                    }
                    viewHolder.changeActionText(R.string.btn_download);
                    //showUser what is just happened
                    switch (errorCode) {
                        case DownloadManager.ERROR_USER_CANCEL: {
                            handleError(R.string.download_manager_error_cancel);
                            viewHolder.setNetworkJob(null);//nullify job
                            break;
                        }
                        case DownloadManager.ERROR_NETWORK_STATE_CHANGED: {
                            handleError(R.string.download_manager_error_network);
                            //we need to nullify but user is waiting for next tryCount crap
                            break;
                        }
                        case DownloadManager.ERROR_STORAGE_SHORTAGE: {
                            handleError(R.string.download_manager_error_storage);
                            //we need to nullify but user is waiting for next tryCount crap
                            break;
                        }
                        case DownloadManager.ERROR_UNKNOWN:
                        default: {
                            handleError(R.string.download_manager_error_unknown);
                            viewHolder.setNetworkJob(null);//nullify job
                            break;
                        }
                    }
                }

                @Override public void onProgress(int percentage) {
                    //sadly all the time will be shown, but we can get boolean from isProgressVisible ;)
                    if (!viewHolder.isProgressVisible()) {
                        viewHolder.changeActionText(R.string.btn_cancel);
                    }
                    viewHolder.showProgress(percentage);
                }
            });
        }
    }

    @Override public void actionRead(final Book book) {

        final IBookViewHolder viewHolder = bookViewHolderReference.get(book);
        if(viewHolder != null) {

            Download download = viewHolder.getDownload();
            if(download != null) {

                File target = new File(download.getFilePath());
                if(target.exists()) {
                    Intent intent = new Intent(view.getContext(), ReadActivityView.class);
                    intent.putExtra(ReadActivityPresenter.KEY_DOWNLOADED_FILE, target.getPath());
                    view.startActivity(intent);
                }
            }
        }
    }

    @Override public void onAttachedWindow(IBookViewHolder viewHolder) {
        if(viewHolder != null) {
            final Book book = viewHolder.getBookObject();
            if(!bookViewHolderReference.containsKey(book)) {
                bookViewHolderReference.put(book, viewHolder);
                databaseManager.hasDownload(book);
            }
        }
    }

    @Override public void onDetachedWindow(IBookViewHolder viewHolder) {
        if(viewHolder != null) {
            final Book book = viewHolder.getBookObject();
            if(bookViewHolderReference.containsKey(book)) {
                bookViewHolderReference.remove(book);
            }
        }
    }

    @Override public void call(Object event) {
        if(event instanceof DownloadFound) {
          if(!bookViewHolderReference.isEmpty()) {
              DownloadFound foundEvent = (DownloadFound) event;
              //download might be null for some reason =)
              String coverName = foundEvent.getDownload() != null ? foundEvent.getDownload().getCoverName() : null;
              //check currents
              Set<Book> bookKeys = bookViewHolderReference.keySet();
              for(Book book : bookKeys) {
                  if(book.getName().equalsIgnoreCase(coverName)) {
                      IBookViewHolder viewHolder = bookViewHolderReference.get(book);
                      if(viewHolder != null) {
                          viewHolder.setDownload(foundEvent.getDownload());
                          if(!viewHolder.isDeleteActionVisible()) {
                              viewHolder.showDeleteAction();
                          }
                          viewHolder.changeActionText(R.string.btn_read);
                      }
                      break;
                  }
              }
          }
       }
    }

    @Override public Context bindContext() {
        return view.getContext();
    }

    @Override public void handleError(@StringRes int stringId) {
        String errorText  = view.getContext().getString(stringId);
        String actionText = view.getContext().getString(android.R.string.ok);
        final Snackbar snackbar = Snackbar.make(view.getContextView(), errorText, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(actionText, new View.OnClickListener() {
            @Override public void onClick(View v) {
                view.hideError(snackbar);
            }
        });
        view.showError(snackbar);
    }

    @Override public void onCreate() {
        view.getActivityComponent().inject(this);
        busListener = busManager.register(this);
        view.configureViews();
        view.setRefreshListener(this);
        view.setAdapter(bookAdapter);
        fetchShelfAsync();
    }

    @Override public void onStart() {
        DownloadManager.register(view.getContext());
    }

    @Override public void onStop() {
        DownloadManager.unregister(view.getContext());
        if(busListener != null) {
            busManager.unregister(busListener);
            busListener = null;
        }
    }

    @Override public void onDestroy() {
        //no op
    }

    @Override protected String getClassTag() {
        return ShelfActivityPresenter.class.getSimpleName();
    }

    @Override protected boolean isLogEnabled() {
        return AbstractApplication.isDebug();
    }


}
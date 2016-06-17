package org.fs.publication.views;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import org.fs.common.IView;
import org.fs.publication.adapters.BookRecyclerAdapter;
import org.fs.publication.components.ActivityComponent;
import org.fs.publication.components.AppComponent;

/**
 * Created by Fatih on 07/06/16.
 * as org.fs.publication.views.ISelfActivityView
 */
public interface IShelfActivityView extends IView {

    void configureViews();
    void showRefresh();
    void hideRefresh();
    void setRefreshListener(SwipeRefreshLayout.OnRefreshListener listener);
    void setAdapter(BookRecyclerAdapter adapter);
    void showError(Snackbar snackbar);
    void hideError(Snackbar snackbar);
    void startActivity(Intent intent);

    boolean             isRefreshVisible();
    Context             getContext();
    AppComponent        getAppComponent();
    ActivityComponent   getActivityComponent();
    View                getContextView();
}
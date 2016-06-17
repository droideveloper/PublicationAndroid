package org.fs.publication.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.fs.core.AbstractActivity;
import org.fs.core.AbstractApplication;
import org.fs.publication.PublicationApp;
import org.fs.publication.R;
import org.fs.publication.adapters.BookRecyclerAdapter;
import org.fs.publication.components.ActivityComponent;
import org.fs.publication.components.AppComponent;
import org.fs.publication.components.DaggerActivityComponent;
import org.fs.publication.modules.ActivityModule;
import org.fs.publication.presenters.IShelfActivityPresenter;
import org.fs.publication.presenters.ShelfActivityPresenter;
import org.fs.publication.utils.ViewUtility;

/**
 * Created by Fatih on 07/06/16.
 * as org.fs.publication.views.ShelfActivityView
 */
public class ShelfActivityView extends AbstractActivity<IShelfActivityPresenter> implements IShelfActivityView {

    private ActivityComponent   activityComponent;
    private RecyclerView        rwShelf;
    private SwipeRefreshLayout  vgSwipe;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_shelf_activity);
    }

    @Override protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        View content = findViewById(android.R.id.content);
        vgSwipe = ViewUtility.findViewById(content, R.id.vgSwipe);
        rwShelf = ViewUtility.findViewById(content, R.id.rwShelf);

        presenter.restoreState(savedInstanceState != null ? savedInstanceState : getIntent().getExtras());
        presenter.onCreate();
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.storeState(outState);
    }

    @Override protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override protected void onStop() {
        presenter.onStop();
        super.onStop();
    }

    @Override protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override public boolean isRefreshVisible() {
        return vgSwipe.isRefreshing();
    }

    @Override public void configureViews() {
        vgSwipe.setColorSchemeColors(R.color.colorBlueSwipeProgress,
                                     R.color.colorPurpleSwipeProgress,
                                     R.color.colorGreenSwipeProgress,
                                     R.color.colorOrangeSwipeProgress);

        rwShelf.setLayoutManager(new LinearLayoutManager(getContext()));
        rwShelf.setHasFixedSize(true);
        rwShelf.setItemAnimator(new DefaultItemAnimator());
    }

    @Override public void showRefresh() {
        vgSwipe.setRefreshing(true);
    }

    @Override public void hideRefresh() {
        vgSwipe.setRefreshing(false);
    }

    @Override public void setRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        vgSwipe.setOnRefreshListener(listener);
    }

    @Override public void setAdapter(BookRecyclerAdapter adapter) {
        rwShelf.setAdapter(adapter);
    }

    @Override public Context getContext() {
        return this;
    }

    @Override public AppComponent getAppComponent() {
        return ((PublicationApp) getApplication()).getAppComponent();
    }

    @Override public ActivityComponent getActivityComponent() {
        if(activityComponent == null) {
            activityComponent = DaggerActivityComponent.builder()
                                                       .appComponent(getAppComponent())
                                                       .activityModule(new ActivityModule(this))
                                                       .build();
        }
        return activityComponent;
    }

    @Override public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

    @Override public View getContextView() {
        return findViewById(android.R.id.content);
    }

    @Override protected IShelfActivityPresenter presenter() {
        return new ShelfActivityPresenter(this);
    }

    @Override public void showError(Snackbar snackbar) {
        snackbar.show();
    }

    @Override public void hideError(Snackbar snackbar) {
        snackbar.dismiss();
    }

    @Override protected String getClassTag() {
        return ShelfActivityView.class.getSimpleName();
    }

    @Override protected boolean isLogEnabled() {
        return AbstractApplication.isDebug();
    }

    //needed to access it
    public IShelfActivityPresenter bindPresenter() {
        return presenter;
    }

}
package org.fs.publication.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import org.fs.core.AbstractActivity;
import org.fs.core.AbstractApplication;
import org.fs.publication.PublicationApp;
import org.fs.publication.R;
import org.fs.publication.components.ActivityComponent;
import org.fs.publication.components.AppComponent;
import org.fs.publication.components.DaggerActivityComponent;
import org.fs.publication.modules.ActivityModule;
import org.fs.publication.presenters.IReadActivityPresenter;
import org.fs.publication.presenters.ReadActivityPresenter;

/**
 * Created by Fatih on 06/06/16.
 * as org.fs.publication.views.ReadActivityView
 */
public class ReadActivityView extends AbstractActivity<IReadActivityPresenter> implements IReadActivityView {

    private ViewGroup           vgProgress;
    private AppComponent        appComponent;
    private ActivityComponent   activityComponent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_read_activity);
    }

    @Override protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        vgProgress = (ViewGroup) findViewById(R.id.vgProgress);

        presenter.restoreState(savedInstanceState != null ?  savedInstanceState  : getIntent().getExtras());
        presenter.onCreate();
    }

    @Override public void onSaveInstanceState(Bundle outState) {
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

    @Override public View getContentView() {
        return findViewById(android.R.id.content);
    }

    @Override public void showError(Snackbar snackbar) {
        snackbar.show();
    }

    @Override public void hideError(Snackbar snackbar) {
        snackbar.dismiss();
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

    @Override public AppComponent getAppComponent() {
        if(appComponent == null) {
            appComponent = ((PublicationApp) getApplication()).getAppComponent();
        }
        return appComponent;
    }

    @Override protected IReadActivityPresenter presenter() {
        return new ReadActivityPresenter(this);
    }

    @Override public void showProgress() {
        vgProgress.setVisibility(View.VISIBLE);
    }

    @Override public void hideProgress() {
        vgProgress.setVisibility(View.GONE);
    }

    @Override public void replace(@IdRes int viewId, Fragment fragment) {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(viewId, fragment);
        trans.commitAllowingStateLoss();
    }

    @Override public Context getContext() {
        return this;
    }

    @Override protected String getClassTag() {
        return ReadActivityView.class.getSimpleName();
    }

    @Override protected boolean isLogEnabled() {
        return AbstractApplication.isDebug();
    }
}
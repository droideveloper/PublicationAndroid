package org.fs.publication.views;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;

import org.fs.common.IView;
import org.fs.publication.components.ActivityComponent;
import org.fs.publication.components.AppComponent;

/**
 * Created by Fatih on 05/06/16.
 * as org.fs.publication.views.IReadActivityView
 */
public interface IReadActivityView extends IView {

    void    showProgress();
    void    hideProgress();
    void    showError(Snackbar snackbar);
    void    hideError(Snackbar snackbar);
    void    replace(@IdRes int viewId, Fragment fragment);

    View                getContentView();
    ActivityComponent   getActivityComponent();
    AppComponent        getAppComponent();
    Context             getContext();
}
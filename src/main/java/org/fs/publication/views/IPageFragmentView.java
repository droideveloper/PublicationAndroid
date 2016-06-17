package org.fs.publication.views;

import android.content.Context;
import android.view.View;
import android.webkit.WebChromeClient;

import org.fs.common.IView;
import org.fs.publication.components.ActivityComponent;

/**
 * Created by Fatih on 05/06/16.
 * as org.fs.publication.views.IPageFragmentView
 */
public interface IPageFragmentView extends IView {

    //TODO write java docs


    void configureWebView();
    void showProgress();
    void hideProgress();
    void loadPageURL(String contentURL);
    void loadURLWithDefaults(String contentURL);
    void showCustomView(View customView, WebChromeClient.CustomViewCallback callback);
    void hideCustomView();
    void contentScrollY(int y);

    ActivityComponent   activityComponent();
    int                 contentScrollY();
    boolean             isAvailable();
    Context             getContext();
}
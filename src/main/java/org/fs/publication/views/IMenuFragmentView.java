package org.fs.publication.views;

import android.content.Context;

import org.fs.common.IView;
import org.fs.publication.components.ActivityComponent;

/**
 * Created by Fatih on 04/06/16.
 * as org.fs.publication.views.IMenuFragmentView
 */
public interface IMenuFragmentView extends IView {

    //TODO write java docs


    void configureWebView(Object jsBridge, String jsBridgeName);
    void showProgress();
    void hideProgress();
    void updateWebViewLayout(int width, int height);
    void scrollToPositionX(int x);
    void loadURL(String url);
    void loadURLWithDefaults(String url);
    void setPageNumber(String pageNumber);
    void setTitle(String title);
    void showNavigation();
    void hideNavigation();
    void finishParent();

    int scrollXPosition();
    int widthSize();
    boolean isAvailable();
    ActivityComponent getActivityComponent();
    Context getContext();
}

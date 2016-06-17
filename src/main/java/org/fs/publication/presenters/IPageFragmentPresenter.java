package org.fs.publication.presenters;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;

import org.fs.common.IPresenter;

/**
 * Created by Fatih on 05/06/16.
 * as org.fs.publication.presenters.IPageFragmentPresenter
 */
public interface IPageFragmentPresenter extends IPresenter {

    //TODO write java docs


    void restoreState(Bundle input);
    void storeState(Bundle output);

    View.OnTouchListener    provideTouchListener();
    boolean                 hasHandleContentURL(String url);
    WebChromeClient         provideWebChromeClient();
    WebViewClient           provideWebViewClient();
}
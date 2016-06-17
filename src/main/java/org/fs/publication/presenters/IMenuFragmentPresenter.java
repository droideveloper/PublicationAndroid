package org.fs.publication.presenters;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;

import org.fs.common.IPresenter;

/**
 * Created by Fatih on 04/06/16.
 * as org.fs.publication.presenters.IMenuFragmentPresenter
 */
public interface IMenuFragmentPresenter extends IPresenter {

    //TODO write java docs

    void restoreState(Bundle input);
    void storeState(Bundle output);

    void viewSizeChange(float width, float height);//javascriptInterface callback
    void findNavigationIndex(int x, String url); //javascriptInterface callback

    boolean hasHandleForURL(String url);
    void handleSelectedIndexEvent(int selected);
    void delayedHide(long ms);
    void delayedShow(long ms);
    void clearCallbacks();

    WebChromeClient         provideWebChromeClient();
    WebViewClient           provideWebViewClient();
    View.OnClickListener    provideClickListener();
}

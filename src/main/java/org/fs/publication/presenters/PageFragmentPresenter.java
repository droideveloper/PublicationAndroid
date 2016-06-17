package org.fs.publication.presenters;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.fs.common.AbstractPresenter;
import org.fs.common.BusManager;
import org.fs.core.AbstractApplication;
import org.fs.publication.events.HideNavigationEvent;
import org.fs.publication.events.PageSelectedWithURL;
import org.fs.publication.events.ShowNavigationEvent;
import org.fs.publication.utils.GestureHelper;
import org.fs.publication.views.IPageFragmentView;
import org.fs.util.StringUtility;

import javax.inject.Inject;

/**
 * Created by Fatih on 05/06/16.
 * as org.fs.publication.presenters.PageFragmentPresenter
 */
public class PageFragmentPresenter extends AbstractPresenter<IPageFragmentView> implements IPageFragmentPresenter,
                                                                                           View.OnTouchListener,
                                                                                           GestureHelper.OnNavigationStateListener {

    public final static String KEY_CONTENT_URL = "page.content.url";
    public final static String KEY_CONTENT_Y   = "page.content.scrollY";

    private GestureDetector gestureDetector;

    private String contentURL;
    private int    contentScrollY;

    @Inject BusManager busManager;

    private final WebViewClient webViewClient = new WebViewClient() {
        @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return hasHandleContentURL(url);
        }

        @Override public void onPageStarted(WebView v, String url, Bitmap favicon) {
            view.showProgress();
        }

        @Override public void onPageFinished(WebView v, String url) {
            view.hideProgress();
        }
    };

    private final WebChromeClient webChromeClient = new WebChromeClient() {
        @Override public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            return Boolean.TRUE;
        }

        @Override public void onShowCustomView(View v, CustomViewCallback callback) {
            view.showProgress();
            view.showCustomView(v, callback);
            view.hideProgress();
        }

        @Override public void onHideCustomView() {
            view.hideCustomView();
        }

        @Override public View getVideoLoadingProgressView() {
            return null;//already have a progress
        }
    };

    public PageFragmentPresenter(IPageFragmentView view) {
        super(view);
    }

    @Override public void restoreState(Bundle input) {
        if(input != null) {
            contentURL = input.getString(KEY_CONTENT_URL);
            contentScrollY = input.getInt(KEY_CONTENT_Y);
        }
    }

    @Override public void storeState(Bundle output) {
        if(!StringUtility.isNullOrEmpty(contentURL)) {
            output.putString(KEY_CONTENT_URL, contentURL);
        }
        output.putInt(KEY_CONTENT_Y, view.contentScrollY());
    }

    @Override public boolean hasHandleContentURL(String url) {
        if(url.startsWith("http://") || url.startsWith("https://")) {
            view.loadURLWithDefaults(url);
            return true;
        } else if(url.startsWith("file://")) {
            url = url.replace("file://", "file:/");
            busManager.post(new PageSelectedWithURL(url));
            return true;
        }
        return false;
    }

    @Override public WebChromeClient provideWebChromeClient() {
        return webChromeClient;
    }

    @Override public WebViewClient provideWebViewClient() {
        return webViewClient;
    }

    @Override public void onCreate() {
        view.activityComponent().inject(this);
        gestureDetector = new GestureDetector(view.getContext(), GestureHelper.addNavigationStateListener(this));
        view.configureWebView();
    }

    @Override public void onStart() {
        if(view.isAvailable()) {
            view.loadPageURL(contentURL);
            view.contentScrollY(contentScrollY);
        }
    }

    @Override public View.OnTouchListener provideTouchListener() {
        return this;
    }

    @Override public boolean onDoubleTap(MotionEvent e) {
        if(GestureHelper.isDisplay()) {
            busManager.post(new ShowNavigationEvent());
        } else {
            busManager.post(new HideNavigationEvent());
        }
        return true;
    }

    @Override public boolean onTouch(View v, MotionEvent event) {
        return v.onTouchEvent(event) && !gestureDetector.onTouchEvent(event);//nice hack
    }

    @Override public void onStop() {
        //no op
    }

    @Override public void onDestroy() {
        //no op
    }

    @Override protected String getClassTag() {
        return PageFragmentPresenter.class.getSimpleName();
    }

    @Override protected boolean isLogEnabled() {
        return AbstractApplication.isDebug();
    }
}
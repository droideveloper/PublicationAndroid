package org.fs.publication.presenters;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.fs.common.AbstractPresenter;
import org.fs.common.BusManager;
import org.fs.core.AbstractApplication;
import org.fs.publication.events.HideNavigationEvent;
import org.fs.publication.events.PageSelectedWithIndex;
import org.fs.publication.events.PageSelectedWithURL;
import org.fs.publication.events.ShowNavigationEvent;
import org.fs.publication.utils.GestureHelper;
import org.fs.publication.views.IMenuFragmentView;
import org.fs.util.StringUtility;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Fatih on 05/06/16.
 * as org.fs.publication.presenters.MenuFragmentPresenter
 */
public class MenuFragmentPresenter extends AbstractPresenter<IMenuFragmentView> implements IMenuFragmentPresenter,
                                                                                           Action1<Object>,
                                                                                           View.OnClickListener {

    public static final String KEY_MENU_URL             = "menu.content.url";
    public static final String KEY_CONTENTS_ARRAY       = "menu.contents.array";
    public static final String KEY_MENU_SCROLL_X        = "menu.content.scrollX";
    public static final String KEY_CONTENT_TITLE        = "menu.content.title";
    public static final String KEY_PAGE_NUMBER          = "menu.content.page.number";

    private final static long ANIM_DELAY_TIME           = 300L;

    private SparseArray<Integer>  positions             = new SparseArray<>();
    private String                contentURL;
    private ArrayList<String>     contentsArray;
    private int                   contentScrollX;
    private String                contentTitle;
    private int                   contentPageNumber;

    private Handler             handler         = new Handler(Looper.getMainLooper());

    private Subscription        busListener;

    @Inject BusManager          busManager;

    private final Runnable      hideNavigation = new Runnable() {
        @Override
        public void run() {
            if(view.isAvailable()) {
                view.hideNavigation();
            }
        }
    };

    private final Runnable      showNavigation = new Runnable() {
        @Override
        public void run() {
            if(view.isAvailable()) {
                view.showNavigation();
            }
        }
    };

    private final WebChromeClient webChromeClient = new WebChromeClient() {
        @Override public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            System.out.println(consoleMessage.message());
            return false;//AbstractApplication.isDebug();
        }
    };

    private final WebViewClient webViewClient = new WebViewClient() {
        private boolean requiresMeasure = true;
        @Override public void onPageStarted(WebView webView, String url, Bitmap favicon) {
            view.showProgress();
        }

        @Override public void onPageFinished(WebView webView, String url) {
            if(requiresMeasure) {
                final String jsCallback = getJavaScriptURL();
                view.loadURL(jsCallback);
                view.loadURL(getJavascriptIndexLocation());
            }
            view.hideProgress();
            if(contentScrollX > 0) {
                view.scrollToPositionX(contentScrollX);
            }
        }

        @Override public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            return hasHandleForURL(url);
        }

        private String getJavascriptIndexLocation() {
             return "javascript:new function() { " +
                        "var elements = document.body.getElementsByTagName('a');" +
                        "for(var i = 0; i < elements.length; i ++) {" +
                        "  var url = elements[i].href;" +
                        "  if (!String.prototype.startsWith) {" +
                        "       String.prototype.startsWith = function(searchString, position){" +
                        "           position = position || 0;" +
                        "           return this.substr(position, searchString.length) === searchString;" +
                        "       };" +
                        "   }" +
                        "  if(url.startsWith('file://')) {" +
                        "       var rect = elements[i].getBoundingClientRect();" +
                        "       var x = rect.left;" +
                        "       "+ getClassTag() + ".findNavigationIndex(x, url);" +
                        "   }" +
                        "}" +
                     "}";
        }

        private String getJavaScriptURL() {
            return String.format(Locale.US,
                                 "javascript:%s.%s",
                                 getClassTag(), "viewSizeChange(document.body.getBoundingClientRect().width," +
                                         " document.body.getBoundingClientRect().height)");
        }
    };

    public MenuFragmentPresenter(IMenuFragmentView view) {
        super(view);
    }

    @Override public void restoreState(Bundle input) {
        if(input != null) {
            contentURL = input.getString(KEY_MENU_URL);
            contentsArray = input.getStringArrayList(KEY_CONTENTS_ARRAY);
            contentScrollX = input.getInt(KEY_MENU_SCROLL_X);
            contentTitle = input.getString(KEY_CONTENT_TITLE);
            contentPageNumber = input.getInt(KEY_PAGE_NUMBER);
        }
    }

    @Override public void storeState(Bundle output) {
        if(!StringUtility.isNullOrEmpty(contentURL)) {
            output.putString(KEY_MENU_URL, contentURL);
        }
        if(contentsArray != null) {
            output.putStringArrayList(KEY_CONTENTS_ARRAY, contentsArray);
        }
        if(!StringUtility.isNullOrEmpty(contentTitle)) {
            output.putString(KEY_CONTENT_TITLE, contentTitle);
        }
        output.putInt(KEY_MENU_SCROLL_X, view.scrollXPosition());
        output.putInt(KEY_PAGE_NUMBER, contentPageNumber);
    }

    @Override @JavascriptInterface public void viewSizeChange(final float width, final float height) {
        final float density = view.getContext().getResources().getDisplayMetrics().density;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(view.isAvailable()) {
                    view.updateWebViewLayout((int) (width * density), (int)(height * density));
                }
            }
        });
    }

    @Override @JavascriptInterface public void findNavigationIndex(int x, String url) {
        //by adding this we able to collect better spot at there
        float density = view.getContext().getResources().getDisplayMetrics().density;
        for (int i = 0; i < contentsArray.size(); i++) {
            if(url.contains(contentsArray.get(i))) {
                positions.append(i, Math.round(x * density));
                break;
            }
        }
    }

    @Override public WebChromeClient provideWebChromeClient() {
        return webChromeClient;
    }

    @Override public WebViewClient provideWebViewClient() {
        return webViewClient;
    }

    @Override public View.OnClickListener provideClickListener() {
        return this;
    }


    @Override public void onCreate() {
        view.getActivityComponent().inject(this);
        busListener = busManager.register(this);
        view.configureWebView(this, getClassTag());
        view.loadURL(contentURL);
        view.setTitle(contentTitle);
        view.setPageNumber(String.valueOf(contentPageNumber));
        //we hide view after 100ms
        delayedHide(1000L);//or setInitialValue(true); hide it after 1 sec
    }

    @Override public void onStart() {
        //no op
    }

    @Override public void onStop() {
        if(busListener != null) {
            busManager.unregister(busListener);
            busListener = null;
        }
        clearCallbacks();
    }

    @Override public void onDestroy() {
        GestureHelper.addNavigationStateListener(null);
    }

    @Override public boolean hasHandleForURL(String url) {
        if(url.startsWith("http") || url.startsWith("https")) {
            view.loadURLWithDefaults(url);
            return true;
        } else if (url.startsWith("file://")) {
            url = url.replace("file://", "file:/");
            busManager.post(new PageSelectedWithURL(url));
            return true;
        }
        return false;
    }

    @Override public void call(Object event) {
        if(event instanceof PageSelectedWithIndex) {
            PageSelectedWithIndex selectedIndexEvent = (PageSelectedWithIndex) event;
            handleSelectedIndexEvent(selectedIndexEvent.contentIndex()); // we load them as index
            contentPageNumber = selectedIndexEvent.contentIndex() + 1;
            view.setPageNumber(String.valueOf(contentPageNumber));
        } else if(event instanceof ShowNavigationEvent) {
            delayedShow(ANIM_DELAY_TIME);
        } else if(event instanceof HideNavigationEvent) {
            delayedHide(ANIM_DELAY_TIME);
        }
    }


    @Override public void handleSelectedIndexEvent(int selected) {
        if(positions != null) {
            if (positions.indexOfKey(selected) != -1) {
                //need to match selected with other value
                contentScrollX = positions.get(selected);//we get these values from
                view.scrollToPositionX(contentScrollX);
            }
        }
    }

    @Override public void delayedHide(long ms) {
        clearCallbacks();
        //send again
        handler.postDelayed(hideNavigation, ms);
    }

    @Override public void delayedShow(long ms) {
        clearCallbacks();
        //send again
        handler.postDelayed(showNavigation, ms);
    }

    @Override public void clearCallbacks() {
        handler.removeCallbacks(hideNavigation);
        handler.removeCallbacks(showNavigation);
    }

    @Override public void onClick(View clickedView) {
        if (view.isAvailable()) {
            view.finishParent();
        }
    }

    @Override protected String getClassTag() {
        return MenuFragmentPresenter.class.getSimpleName();
    }

    @Override protected boolean isLogEnabled() {
        return AbstractApplication.isDebug();
    }
}
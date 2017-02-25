/*
 * Publication Copyright (C) 2017 Fatih.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fs.publication.presenters;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.fs.common.AbstractPresenter;
import org.fs.common.BusManager;
import org.fs.publication.BuildConfig;
import org.fs.publication.entities.events.PageSelectedByUri;
import org.fs.publication.views.ContentFragmentView;
import org.fs.util.Objects;

public class ContentFragmentPresenterImp extends AbstractPresenter<ContentFragmentView>
    implements ContentFragmentPresenter {

  public final static String KEY_CONTENT_URL = "page.content.url";
  public final static String KEY_CONTENT_Y   = "page.content.scroll.y";

  private final static String WEB_AUTHORITY         = "http://";
  private final static String SECURE_WEB_AUTHORITY  = "https://";
  private final static String LOCAL_AUTHORITY       = "file://";

  String uri;
  int    scrollY;

  public ContentFragmentPresenterImp(ContentFragmentView view) {
    super(view);
  }

  @Override public void restoreState(Bundle restoreState) {
    if(restoreState != null) {
      if(restoreState.containsKey(KEY_CONTENT_URL)) {
        uri = restoreState.getString(KEY_CONTENT_URL);
      }
      scrollY = restoreState.getInt(KEY_CONTENT_Y);
    }
  }

  @Override public void storeState(Bundle storeState) {
    if(!Objects.isNullOrEmpty(uri)) {
      storeState.putString(KEY_CONTENT_URL, uri);
    }
    storeState.putInt(KEY_CONTENT_Y, view.scrollY());
  }

  @Override public void onCreate() {
    if (view.isAvailable()) {
      view.setup();
    }
  }

  @Override public void onStart() {
    if (view.isAvailable()) {
      if (view.shouldLoadUri(uri)) {
        view.loadUri(uri);
      }
      if (scrollY != 0) {
        view.scrollY(scrollY);
      }
    }
  }

  @Override public WebViewClient client() {
    return new WebViewClient() {

      @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest request) {
        if(view.isAvailable()) {
          return shouldOverrideRequest(request);
        }
        return super.shouldOverrideUrlLoading(v, request);
      }

      @Override public boolean shouldOverrideUrlLoading(WebView v, String url) {
        if(view.isAvailable()) {
          return overrideUri(url);
        }
        return super.shouldOverrideUrlLoading(v, url);
      }

      @Override public void onPageStarted(WebView v, String url, Bitmap favicon) {
        if(view.isAvailable()) {
          view.showProgress();
        }
      }

      @Override public void onPageFinished(WebView v, String url) {
        if(view.isAvailable()) {
          view.hideProgress();
        }
      }
    };
  }

  @Override public WebChromeClient chrome() {
    return new WebChromeClient() {

      @Override public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        //log(Log.INFO, consoleMessage.message());
        return BuildConfig.DEBUG;
      }

      @Override public void onShowCustomView(View v, CustomViewCallback callback) {
        if (view.isAvailable()) {
          view.showProgress();
          view.showCustomView(v, callback);
          view.hideProgress();
        }
      }

      @Override public void onHideCustomView() {
        if (view.isAvailable()) {
          view.hideCustomView();
        }
      }

      @Override public View getVideoLoadingProgressView() {
        return null; // already using our default progress loader
      }
    };
  }

  @Override protected String getClassTag() {
    return ContentFragmentPresenterImp.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private boolean shouldOverrideRequest(WebResourceRequest request) {
    Uri uri = request.getUrl();
    return overrideUri(uri.toString());
  }

  private boolean overrideUri(String uri) {
    log(Log.ERROR, uri);
    if(uri.startsWith(WEB_AUTHORITY) || uri.startsWith(SECURE_WEB_AUTHORITY)) {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(Uri.parse(uri));
      view.startActivity(intent);
      return true;
    } else if (uri.startsWith(LOCAL_AUTHORITY)) {
      uri = uri.replace(LOCAL_AUTHORITY, "file:");
      BusManager.send(new PageSelectedByUri(uri));
      return true;
    }
    return false;
  }
}
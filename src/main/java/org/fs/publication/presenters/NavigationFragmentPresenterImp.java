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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.ArrayList;
import org.fs.common.AbstractPresenter;
import org.fs.common.BusManager;
import org.fs.common.ThreadManager;
import org.fs.publication.BuildConfig;
import org.fs.publication.commons.SystemJS;
import org.fs.publication.entities.events.PageSelectedByIndex;
import org.fs.publication.entities.events.PageSelectedByUri;
import org.fs.publication.views.NavigationFragmentView;
import org.fs.util.Collections;
import org.fs.util.Objects;
import rx.Subscription;

public class NavigationFragmentPresenterImp extends AbstractPresenter<NavigationFragmentView>
    implements NavigationFragmentPresenter {

  public final static String KEY_NAVIGATION_SET = "navigation.array";
  public final static String KEY_NAVIGATION_URL = "navigation.url";
  public final static String KEY_NAVIGATION_X   = "navigation.scroll.x";

  private final static String WEB_AUTHORITY         = "http://";
  private final static String SECURE_WEB_AUTHORITY  = "https://";
  private final static String LOCAL_AUTHORITY       = "file://";

  private final static String KEY_BRIDGE            = "bridge";

  private SparseIntArray        positions;
  private ArrayList<String>     contents;
  private String                uri;
  private int                   scrollX;

  private float density;
  private Subscription callback;

  public NavigationFragmentPresenterImp(NavigationFragmentView view) {
    super(view);
    positions = new SparseIntArray();
    density = view.getContext().getResources().getDisplayMetrics().density;
  }

  @Override public void onCreate() {
    if (view.isAvailable()) {
      view.setup();
      view.addJavaScriptBridge(this, KEY_BRIDGE);
    }
  }

  @Override public void restoreState(Bundle restoreState) {
    if (restoreState != null) {
      if (restoreState.containsKey(KEY_NAVIGATION_SET)) {
        contents = restoreState.getStringArrayList(KEY_NAVIGATION_SET);
      }
      if (restoreState.containsKey(KEY_NAVIGATION_URL)) {
        uri = restoreState.getString(KEY_NAVIGATION_URL);
      }
      scrollX = restoreState.getInt(KEY_NAVIGATION_X);
    }
  }

  @Override public void storeState(Bundle storeState) {
    if (!Collections.isNullOrEmpty(contents)) {
      storeState.putStringArrayList(KEY_NAVIGATION_SET, contents);
    }
    if (!Objects.isNullOrEmpty(uri)) {
      storeState.putString(KEY_NAVIGATION_URL, uri);
    }
    storeState.putInt(KEY_NAVIGATION_X, scrollX);
  }

  @Override public void onStart() {
    if(view.isAvailable()) {
      if (view.shouldLoadUri(uri)) {
        view.loadUri(uri);
      }
      if (scrollX != 0) {
        view.scrollX(scrollX);
      }
      callback = BusManager.add((evt) -> {
        if (evt instanceof PageSelectedByIndex) {
          PageSelectedByIndex event = (PageSelectedByIndex) evt;
          selectedScrollX(event.index());
        } else if (evt instanceof PageSelectedByUri) {
          PageSelectedByUri event = (PageSelectedByUri) evt;
          selectedScrollX(contents.indexOf(event.uri()));
        }
      });
    }
  }

  @Override public void onStop() {
    if (callback != null) {
      BusManager.remove(callback);
      callback = null;
    }
  }

  @Override public WebChromeClient chrome() {
    return new WebChromeClient() {

      @Override public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        log(Log.INFO, consoleMessage.message());
        return BuildConfig.DEBUG;
      }
    };
  }

  @Override public WebViewClient client() {
    return new WebViewClient() {

      private boolean loadJavaScript = true;

      @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest request) {
        if (view.isAvailable()) {
          return shouldOverrideRequest(request);
        }
        return super.shouldOverrideUrlLoading(v, request);
      }

      @Override public boolean shouldOverrideUrlLoading(WebView v, String url) {
        if (view.isAvailable()) {
          return overrideUri(url);
        }
        return super.shouldOverrideUrlLoading(v, url);
      }

      @Override public void onPageFinished(WebView v, String url) {
        if(view.isAvailable()) {
          if (loadJavaScript) {
            v.loadUrl(SystemJS.loaded);
            loadJavaScript = false;
          }
          if (scrollX != 0) {
            view.scrollX(scrollX);
          }
        }
      }
    };
  }

  @JavascriptInterface public void boundsOfPage(final float width, final float height) {
    ThreadManager.runOnUiThread(() -> {
      if (view.isAvailable()) {
        view.update(Math.round(density * width), Math.round(density * height));
      }
    });
  }

  @JavascriptInterface public void indexOfUri(final float left, final String uri) {
    for (int i = 0, z = contents.size(); i < z; i++) {
      if (uri.contains(contents.get(i))) {
        positions.append(i, Math.round(left * density));
        break;
      }
    }
  }

  @Override protected String getClassTag() {
    return NavigationFragmentPresenterImp.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  private void selectedScrollX(int selected) {
    if (positions != null) {
      if (positions.indexOfKey(selected) != -1) {
        final int scrollX = positions.get(selected);
        if (view.isAvailable()) {
          view.scrollX(scrollX);
        }
      }
    }
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private boolean shouldOverrideRequest(WebResourceRequest request) {
    Uri uri = request.getUrl();
    return overrideUri(uri.toString());
  }

  private boolean overrideUri(String uri) {
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
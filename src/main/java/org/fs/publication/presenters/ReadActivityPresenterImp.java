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

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.view.View;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import org.fs.common.AbstractPresenter;
import org.fs.common.BusManager;
import org.fs.publication.BuildConfig;
import org.fs.publication.R;
import org.fs.publication.commons.SimplePageChangeListener;
import org.fs.publication.entities.Configuration;
import org.fs.publication.entities.events.PageSelectedByIndex;
import org.fs.publication.entities.events.PageSelectedByUri;
import org.fs.publication.entities.events.VisibilityChange;
import org.fs.publication.views.NavigationFragment;
import org.fs.publication.views.ReadActivityView;
import org.fs.util.Collections;
import org.fs.util.Objects;
import org.fs.util.ObservableList;
import rx.Subscription;

public class ReadActivityPresenterImp extends AbstractPresenter<ReadActivityView>
    implements ReadActivityPresenter {

  public final static String KEY_CONFIGURATION = "key.configuration.object";
  private final static String KEY_PAGE_INDEX   = "key.page.index";

  private final static String INDEX_FILE  = "index.html";
  private final static String INDEX_FILE2 = "index.htm";

  private final static long DEFAULT_DELAY_TIME = 500L;

  private ObservableList<String> contents;
  private Configuration config;
  private Handler thread = new Handler(Looper.getMainLooper());
  private int pageIndex;
  private Subscription  callback;

  public ReadActivityPresenterImp(ReadActivityView view) {
    super(view);
    contents = view.contents();
  }

  @Override public void restoreState(Bundle restoreState) {
    if (restoreState != null) {
      if (restoreState.containsKey(KEY_CONFIGURATION)) {
        config = restoreState.getParcelable(KEY_CONFIGURATION);
      }
      pageIndex = restoreState.getInt(KEY_PAGE_INDEX);
    }
  }

  @Override public void storeState(Bundle storeState) {
    if (!Objects.isNullOrEmpty(config)) {
      storeState.putParcelable(KEY_CONFIGURATION, config);
    }
    storeState.putInt(KEY_PAGE_INDEX, view.currentPageAt());
  }

  @Override public void onCreate() {
    view.setup();
    if (!Objects.isNullOrEmpty(config)) {
      view.setTitle(config.title());
    }
  }

  @Override public void onStart() {
    view.showProgress();
    if (!Objects.isNullOrEmpty(config)) {
      if (!Collections.isNullOrEmpty(config.contents())) {
        // if index.html or index.htm is present among files we use it as menu
        if (!Objects.isNullOrEmpty(config.index())) {
          if (view.isAvailable()) {
            view.newView(R.id.navigation, NavigationFragment.newInstance(config.index(), config.contents()));
          }
        }
        // TODO set index.html or index.htm out of collection as single form
        // book.json does not contain it all the time so not in contents array is understandable
        // if anything as html file will be our content of the pages.
        if (Collections.isNullOrEmpty(contents)) {
          contents.addAll(StreamSupport.stream(config.contents())
              .filter(u -> !(u.contains(INDEX_FILE) || u.contains(INDEX_FILE2)))
              .collect(Collectors.toList()));
        }
      }
    }
    // check index later
    if (pageIndex != 0) {
      view.showPageAt(pageIndex);
    }
    view.hideProgress();
    callback = BusManager.add((evt) -> {
      if (evt instanceof PageSelectedByUri) {
        PageSelectedByUri event = (PageSelectedByUri) evt;
        int index = contents.indexOf((uri) -> uri.equalsIgnoreCase(event.uri()));
        if (index != -1) {
          if (view.isAvailable()) {
            view.showPageAt(index);
          }
        }
      } else if(evt instanceof VisibilityChange) {
        if (view.isDisplayNavigation()) {
          delayedShow(DEFAULT_DELAY_TIME);
        } else {
          delayedHide(DEFAULT_DELAY_TIME);
        }
      }
    });
    // hide after 1000 ms
    delayedHide(1000L);
  }

  @Override public View.OnClickListener clickListener() {
    return (v) -> onBack();
  }

  @Override public void onBackPressed() {
    onBack();
  }

  @Override public void onStop() {
    if (callback != null) {
      BusManager.remove(callback);
      callback = null;
    }
  }

  @Override public ViewPager.OnPageChangeListener changeListener() {
    return new SimplePageChangeListener() {
      @Override public void onPageSelected(int position) {
        if (view.isAvailable()) {
          BusManager.send(new PageSelectedByIndex(position));
        }
      }
    };
  }

  @Override protected String getClassTag() {
    return ReadActivityPresenterImp.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  private void onBack() {
    if (view.isAvailable()) {
      view.finish();
    }
  }

  // handle for hide
  private void delayedHide(final long ms) {
    clearHideAndShow();
    thread.postDelayed(hideThread, ms);
  }

  // handle for show
  private void delayedShow(final long ms) {
    clearHideAndShow();
    thread.postDelayed(showThread, ms);
  }

  // clear all threads
  private void clearHideAndShow() {
    thread.removeCallbacks(hideThread);
    thread.removeCallbacks(showThread);
  }

  // default hide thread
  private final Runnable hideThread = () -> {
    if (view.isAvailable()) {
      view.hideNavigation();
    }
  };

  // default show thread
  private final Runnable showThread = () -> {
    if (view.isAvailable()) {
      view.showNavigation();
    }
  };
}
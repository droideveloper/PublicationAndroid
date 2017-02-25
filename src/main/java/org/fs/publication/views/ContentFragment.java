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
package org.fs.publication.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import java.lang.ref.WeakReference;
import javax.inject.Inject;
import org.fs.anim.FancyInterpolator;
import org.fs.core.AbstractFragment;
import org.fs.publication.BuildConfig;
import org.fs.publication.R;
import org.fs.publication.commons.SimpleAnimatorListener;
import org.fs.publication.commons.components.DaggerFragmentComponent;
import org.fs.publication.commons.modules.FragmentModule;
import org.fs.publication.presenters.ContentFragmentPresenter;
import org.fs.publication.presenters.ContentFragmentPresenterImp;
import org.fs.util.ApiCompats;

import static org.fs.publication.R.layout.view_content_fragment;
import static org.fs.util.ViewUtility.findViewById;

public class ContentFragment extends AbstractFragment<ContentFragmentPresenter>
    implements ContentFragmentView {

  @Inject ContentFragmentPresenter presenter;
  WeakReference<View>      viewReference;

  WeakReference<View>                 customReference;
  WebChromeClient.CustomViewCallback  callback;

  WebView                  pageView;
  ProgressBar              progressView;


  private final static int INITIAL_SCALE = 1;

  public static ContentFragment newInstance(String str) {
    Bundle args = new Bundle();
    args.putString(ContentFragmentPresenterImp.KEY_CONTENT_URL, str);
    // add new fragments
    ContentFragment fragment = new ContentFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater factory, ViewGroup parent, Bundle restoreState) {
    final View view = factory.inflate(view_content_fragment, parent, false);
    pageView = findViewById(view, R.id.viewPage);
    progressView = findViewById(view, R.id.viewProgress);
    viewReference = new WeakReference<>(view);
    return view;
  }

  @Override public void onActivityCreated(Bundle restoreState) {
    super.onActivityCreated(restoreState);
    DaggerFragmentComponent.builder()
        .fragmentModule(new FragmentModule(this))
        .build()
        .inject(this);
    presenter.restoreState(restoreState != null ? restoreState : getArguments());
    presenter.onCreate();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    presenter.storeState(outState);
  }

  @Override public void setup() {
    final WebSettings settings = pageView.getSettings();
    settings.setUseWideViewPort(true);
    settings.setDisplayZoomControls(false);
    settings.setBuiltInZoomControls(false);
    settings.setSupportZoom(false);
    settings.setJavaScriptCanOpenWindowsAutomatically(false);
    // dangerous one check twice on content of js.
    settings.setJavaScriptEnabled(true);//indeed enabled what's wrong enabling it
    pageView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
    pageView.setWebChromeClient(presenter.chrome());
    pageView.setWebViewClient(presenter.client());
    pageView.setInitialScale(INITIAL_SCALE);
  }

  @Override public void loadUri(String uri) {
    pageView.loadUrl(uri);
  }

  @Override public boolean shouldLoadUri(String uri) {
    return !uri.equalsIgnoreCase(pageView.getUrl());
  }

  @Override public void onResume() {
    super.onResume();
    pageView.onResume();
  }

  @Override public void onPause() {
    pageView.onPause();
    super.onPause();
  }

  @Override public void onStart() {
    super.onStart();
    presenter.onStart();
  }

  @Override public void onStop() {
    presenter.onStop();
    super.onStop();
  }

  @Override public void showError(String errorString) {
    final View view = view();
    if (view != null) {
      Snackbar.make(view, errorString, Snackbar.LENGTH_LONG).show();
    }
  }

  @Override public void showError(String errorString, String actionTextString,
      View.OnClickListener callback) {
    final View view = view();
    if (view != null) {
      final Snackbar snackbar = Snackbar.make(view, errorString, Snackbar.LENGTH_LONG);
      snackbar.setAction(actionTextString, v -> {
        if (callback != null) {
          callback.onClick(v);
        }
        snackbar.dismiss();
      });
      snackbar.show();
    }
  }

  @Override public void showCustomView(View view, WebChromeClient.CustomViewCallback callback) {
    this.callback = callback;
    this.customReference = new WeakReference<>(view);
    // not invisible
    pageView.setVisibility(View.INVISIBLE);
    // layout
    FrameLayout layout = (FrameLayout) view();
    if(layout != null) {
      layout.addView(view, 0, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
          FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER));
    }
  }

  @Override public void hideCustomView() {
    FrameLayout layout = (FrameLayout) view();
    if(layout != null) {
      layout.removeView(customReference.get());
    }
    if (callback != null) {
      callback.onCustomViewHidden();
    }
    pageView.setVisibility(View.VISIBLE);
    // clean up
    customReference.clear();
    customReference = null;
    callback = null;
  }

  @Override public int scrollY() {
    return pageView != null ?  pageView.getScrollY() : 0;
  }

  @Override public void scrollY(int y) {
    if(y != scrollY()) {
      pageView.clearAnimation();
      // might need change for scroll.y
      ObjectAnimator yAnim = ObjectAnimator.ofInt(pageView, "scrollY", scrollY(), y);
      yAnim.setDuration(300L);
      yAnim.setInterpolator(new FancyInterpolator());
      yAnim.addListener(new SimpleAnimatorListener() {
        @Override public void onAnimationEnd(Animator animation) {
          if (isAvailable()) {
            if (ApiCompats.isApiAvailable(Build.VERSION_CODES.ICE_CREAM_SANDWICH)) {
              pageView.setScrollY(y);
            }
          }
        }
      });
      yAnim.start();
    }
  }

  @Override public void showProgress() {
    progressView.setIndeterminate(true);
    progressView.setVisibility(View.VISIBLE);
  }

  @Override public void hideProgress() {
    progressView.setVisibility(View.INVISIBLE);
    progressView.setIndeterminate(false);
  }

  @Override public String getStringResource(@StringRes int stringId) {
    return getActivity().getString(stringId);
  }

  @Override public Context getContext() {
    return getActivity();
  }

  @Override public boolean isAvailable() {
    return super.isCallingSafe();
  }

  @Override public void finish() {
    throw new IllegalArgumentException("fragment instances does not support finish options");
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  @Override protected String getClassTag() {
    return ContentFragment.class.getSimpleName();
  }

  private View view() {
    return viewReference != null ? viewReference.get() : null;
  }
}
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import javax.inject.Inject;
import org.fs.anim.FancyInterpolator;
import org.fs.core.AbstractFragment;
import org.fs.publication.BuildConfig;
import org.fs.publication.R;
import org.fs.publication.commons.SimpleAnimatorListener;
import org.fs.publication.commons.components.DaggerFragmentComponent;
import org.fs.publication.commons.modules.FragmentModule;
import org.fs.publication.presenters.NavigationFragmentPresenter;
import org.fs.publication.presenters.NavigationFragmentPresenterImp;
import org.fs.util.ApiCompats;

import static org.fs.publication.R.layout.view_menu_fragment;
import static org.fs.util.ViewUtility.findViewById;

public class NavigationFragment extends AbstractFragment<NavigationFragmentPresenter>
    implements NavigationFragmentView {

  @Inject NavigationFragmentPresenter presenter;
  WeakReference<View>         viewReference;
  WebView                     menuView;

  public static NavigationFragment newInstance(String uri, ArrayList<String> contents) {
    Bundle args = new Bundle();
    args.putString(NavigationFragmentPresenterImp.KEY_NAVIGATION_URL, uri);
    args.putStringArrayList(NavigationFragmentPresenterImp.KEY_NAVIGATION_SET, contents);

    NavigationFragment fragment = new NavigationFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater factory, ViewGroup parent, Bundle restoreState) {
    final View view = factory.inflate(view_menu_fragment, parent, false);
    menuView = findViewById(view, R.id.viewMenu);
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
    final WebSettings settings = menuView.getSettings();
    settings.setDisplayZoomControls(false);
    settings.setBuiltInZoomControls(false);
    settings.setSupportZoom(false);
    settings.setJavaScriptCanOpenWindowsAutomatically(false);
    // dangerous one check twice on content of js.
    settings.setJavaScriptEnabled(true);//indeed enabled what's wrong enabling it
    menuView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
    menuView.setWebChromeClient(presenter.chrome());
    menuView.setWebViewClient(presenter.client());
  }

  @Override public int scrollX() {
    return menuView != null ? menuView.getScrollX() : 0;
  }

  @Override public void scrollX(int x) {
    if (x != scrollX()) {
      ObjectAnimator xAnim = ObjectAnimator.ofInt(menuView, "scrollX", scrollX(), x);
      xAnim.setDuration(300L);
      xAnim.setInterpolator(new FancyInterpolator());
      xAnim.addListener(new SimpleAnimatorListener() {
        @Override public void onAnimationEnd(Animator animation) {
          if(ApiCompats.isApiAvailable(Build.VERSION_CODES.ICE_CREAM_SANDWICH)) {
            menuView.setScrollX(x);
          }
        }
      });
      xAnim.start();
    }
  }

  @Override public void addJavaScriptBridge(NavigationFragmentPresenterImp.AndroidBridge reference, String key) {
    // TODO use javascript bridge only with if you are publishing this app for lv 17 or more
    menuView.addJavascriptInterface(reference, key);
  }

  @Override public boolean shouldLoadUri(String uri) {
    return !uri.equalsIgnoreCase(menuView.getUrl());
  }

  @Override public void update(int width, int height) {
    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) menuView.getLayoutParams();
    layoutParams.height = height;
    // force redraw
    menuView.requestLayout();
  }

  @Override public void onResume() {
    super.onResume();
    presenter.onResume();
    menuView.onResume();
  }

  @Override public void onPause() {
    menuView.onPause();
    presenter.onPause();
    super.onPause();
  }

  @Override public void loadUri(String uri) {
    menuView.loadUrl(uri);
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
    return NavigationFragment.class.getSimpleName();
  }

  private View view() {
    return viewReference != null ? viewReference.get() : null;
  }
}
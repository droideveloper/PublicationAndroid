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
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import javax.inject.Inject;
import org.fs.anim.FancyInterpolator;
import org.fs.core.AbstractActivity;
import org.fs.core.AbstractFragment;
import org.fs.publication.BuildConfig;
import org.fs.publication.R;
import org.fs.publication.commons.SimpleAnimatorListener;
import org.fs.publication.commons.components.DaggerActivityComponent;
import org.fs.publication.commons.modules.ActivityModule;
import org.fs.publication.presenters.ReadActivityPresenter;
import org.fs.publication.views.adapters.ContentStateAdapter;
import org.fs.util.ObservableList;
import org.fs.util.ViewUtility;

import static org.fs.publication.R.layout.view_read_activity;

public class ReadActivity extends AbstractActivity<ReadActivityPresenter>
    implements ReadActivityView {

  @Inject ReadActivityPresenter presenter;
  @Inject ContentStateAdapter   adapter;
  @Inject ObservableList<String> contents;

  private Toolbar     toolbar;
  private ViewPager   viewPager;
  private FrameLayout layout;
  private ProgressBar progress;

  @Override public void onCreate(Bundle restoreState) {
    super.onCreate(restoreState);
    setContentView(view_read_activity);
    // loaded views
    toolbar = ViewUtility.findViewById(this, R.id.toolbar);
    layout = ViewUtility.findViewById(this, R.id.navigation);
    viewPager = ViewUtility.findViewById(this, R.id.viewPager);
    progress = ViewUtility.findViewById(this, R.id.viewProgress);
    //inject it this way
    DaggerActivityComponent.builder()
        .activityModule(new ActivityModule(this))
        .build()
        .inject(this);
    presenter.restoreState(restoreState != null ? restoreState : getIntent().getExtras());
    presenter.onCreate();
  }

  @Override public void onSaveInstanceState(Bundle storeState) {
    super.onSaveInstanceState(storeState);
    presenter.storeState(storeState);
  }

  @Override public void onStart() {
    super.onStart();
    presenter.onStart();
  }

  @Override public void onStop() {
    presenter.onStop();
    super.onStop();
  }

  @Override public void setTitle(String titleStr) {
    toolbar.setTitle(titleStr);
  }

  @Override public <V extends AbstractFragment<?>> void newView(@IdRes int layout, V view) {
    FragmentManager fragmentManager = fragmentManager();
    if (fragmentManager != null) {
      FragmentTransaction trans = fragmentManager.beginTransaction();
      trans.replace(layout, view);
      trans.commit();
    }
  }

  @Override public void showError(String errorString) {
    final View view = view();
    if (view != null) {
      Snackbar.make(view, errorString, Snackbar.LENGTH_LONG).show();
    }
  }

  @Override public boolean isDisplayNavigation() {
    return toolbar.getVisibility() == View.INVISIBLE;
  }

  @Override public void hideNavigation() {
    clearAnimations();
    // translateY from 0 to Height
    ObjectAnimator hideToolbar = ObjectAnimator.ofFloat(toolbar, "translateY", toolbar.getTranslationY(), -1.0f * toolbar.getHeight());
    ObjectAnimator hideLayout = ObjectAnimator.ofFloat(layout, "translateY", layout.getTranslationY(), 1.0f * layout.getHeight());
    // hide
    AnimatorSet hideAnim = new AnimatorSet();
    hideAnim.setDuration(300L);
    hideAnim.setInterpolator(new FancyInterpolator());
    hideAnim.playTogether(hideToolbar, hideLayout);
    hideAnim.addListener(new SimpleAnimatorListener() {
      @Override public void onAnimationEnd(Animator animation) {
        if (isAvailable()) {
          toolbar.setVisibility(View.INVISIBLE);
          layout.setVisibility(View.INVISIBLE);
        }
      }
    });
    hideAnim.start();
  }

  @Override public void showNavigation() {
    clearAnimations();
    // translateY from Height to 0
    ObjectAnimator showToolbar = ObjectAnimator.ofFloat(toolbar, "translateY", -1.0f * toolbar.getHeight(), 0.0f);
    ObjectAnimator showLayout = ObjectAnimator.ofFloat(layout, "translateY", 1.0f * layout.getHeight(), 0.0f);
    // show
    AnimatorSet showAnim = new AnimatorSet();
    showAnim.setDuration(300L);
    showAnim.setInterpolator(new FancyInterpolator());
    showAnim.playTogether(showToolbar, showLayout);
    showAnim.addListener(new SimpleAnimatorListener() {
      @Override public void onAnimationStart(Animator animation) {
        if (isAvailable()) {
          toolbar.setVisibility(View.VISIBLE);
          layout.setVisibility(View.VISIBLE);
        }
      }
    });
  }

  @Override public void showProgress() {
    viewPager.setVisibility(View.INVISIBLE);
    // show progress
    progress.setIndeterminate(true);
    progress.setVisibility(View.VISIBLE);
  }

  @Override public void hideProgress() {
    progress.setIndeterminate(false);
    progress.setVisibility(View.INVISIBLE);
    // show view
    viewPager.setVisibility(View.VISIBLE);
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

  @Override public FragmentManager fragmentManager() {
    return getSupportFragmentManager();
  }

  @Override public ObservableList<String> contents() {
    return contents;
  }

  @Override public int currentPageAt() {
    return viewPager != null ? viewPager.getCurrentItem() : 0;
  }

  @Override public void showPageAt(int index) {
    if (index != viewPager.getCurrentItem()) {
      viewPager.setCurrentItem(index, true);
    }
  }

  @Override public void finish() {
    super.finish();
    overridePendingTransition(R.anim.translate_right_in, R.anim.scale_out);
  }

  @Override public void setup() {
    viewPager.setAdapter(adapter);
    viewPager.addOnPageChangeListener(presenter.changeListener());
    // read icon
    Drawable icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_back);
    toolbar.setNavigationIcon(icon);
    toolbar.setNavigationOnClickListener(presenter.clickListener());
    // read color
    int color = ContextCompat.getColor(getContext(), R.color.colorPrimaryLightText);
    toolbar.setTitleTextColor(color);
  }

  @Override public String getStringResource(@StringRes int stringId) {
    return getString(stringId);
  }

  @Override public Context getContext() {
    return this;
  }

  @Override public boolean isAvailable() {
    return !isFinishing();
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  @Override protected String getClassTag() {
    return ReadActivity.class.getSimpleName();
  }

  private View view() {
    return findViewById(android.R.id.content);
  }

  private void clearAnimations() {
    toolbar.clearAnimation();
    layout.clearAnimation();
  }
}
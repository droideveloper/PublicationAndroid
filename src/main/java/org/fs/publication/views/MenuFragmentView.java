package org.fs.publication.views;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import org.fs.core.AbstractApplication;
import org.fs.core.AbstractFragment;
import org.fs.publication.R;
import org.fs.publication.components.ActivityComponent;
import org.fs.publication.presenters.IMenuFragmentPresenter;
import org.fs.publication.presenters.MenuFragmentPresenter;
import org.fs.publication.utils.AnimationUtility;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Fatih on 04/06/16.
 * as org.fs.publication.views.MenuFragmentView
 */
public class MenuFragmentView extends AbstractFragment<IMenuFragmentPresenter> implements IMenuFragmentView {

    private ProgressBar             pbLoading;
    private WebView                 wvMenuPage;

    private View                    vgTopNavigation;
    private View                    vgBottomNavigation;

    private AppCompatTextView       txtTitle;
    private AppCompatTextView       txtPageNumber;
    private AppCompatImageButton    btnNavigation;

    public static MenuFragmentView newInstance(String contentURL, ArrayList<String> contentsArray, String contentTitle) {
        Bundle args = new Bundle();
        args.putString(MenuFragmentPresenter.KEY_MENU_URL, contentURL);
        args.putStringArrayList(MenuFragmentPresenter.KEY_CONTENTS_ARRAY, contentsArray);
        args.putString(MenuFragmentPresenter.KEY_CONTENT_TITLE, contentTitle);
        MenuFragmentView frag = new MenuFragmentView();
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_menu_fragment, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //views
        wvMenuPage = (WebView) view.findViewById(R.id.wvMenuPage);
        pbLoading = (ProgressBar) view.findViewById(R.id.pbLoading);
        vgTopNavigation = view.findViewById(R.id.vgTopNavigation);
        vgBottomNavigation = view.findViewById(R.id.vgBottomNavigation);
        txtTitle = (AppCompatTextView) view.findViewById(R.id.txtTitle);
        txtPageNumber = (AppCompatTextView) view.findViewById(R.id.txtPageNumber);
        btnNavigation = (AppCompatImageButton) view.findViewById(R.id.btnNavigation);
        //restore callback
        presenter.restoreState((savedInstanceState != null) ? savedInstanceState : getArguments());
        presenter.onCreate();
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.storeState(outState);
    }

    @Override public void onStart() {
        super.onStart();
        presenter.onStart();
        wvMenuPage.onResume();
    }

    @Override public void onStop() {
        presenter.onStop();
        wvMenuPage.onPause();
        super.onStop();
    }

    @Override public void onDestroy() {
        presenter.onDestroy();
        wvMenuPage.destroy();
        super.onDestroy();
    }

    @Override public ActivityComponent getActivityComponent() {
        ReadActivityView activity = (ReadActivityView) getActivity();
        if(isAvailable()) {
            return activity.getActivityComponent();
        }
        return null;
    }

    @Override protected IMenuFragmentPresenter presenter() {
        return new MenuFragmentPresenter(this);
    }

    @SuppressLint("JavascriptInterface")
    @Override public void configureWebView(Object jsBridge, String jsBridgeName) {
        final WebSettings settings = wvMenuPage.getSettings();
        settings.setDisplayZoomControls(false);
        settings.setBuiltInZoomControls(false);
        settings.setSupportZoom(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setJavaScriptEnabled(true);//indeed enabled what's wrong enabling it
        //other settings
        wvMenuPage.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        wvMenuPage.setWebChromeClient(presenter.provideWebChromeClient());
        wvMenuPage.setWebViewClient(presenter.provideWebViewClient());
        wvMenuPage.addJavascriptInterface(jsBridge, jsBridgeName);
        //set up other views
        btnNavigation.setOnClickListener(presenter.provideClickListener());
    }

    @Override public int scrollXPosition() {
        return wvMenuPage.getScrollX();
    }

    @Override public int widthSize() {
        return wvMenuPage.getLayoutParams().width;
    }

    @Override public void setTitle(String title) {
        txtTitle.setText(title);
    }

    @Override public void setPageNumber(String pageNumber) {
        txtPageNumber.setText(pageNumber);
    }

    @Override public void showProgress() {
       pbLoading.setVisibility(View.VISIBLE);
    }

    @Override public void hideProgress() {
        pbLoading.setVisibility(View.GONE);
    }

    @Override public void finishParent() {
        FragmentActivity activity = getActivity();
        if(activity != null) {
            activity.finish();
        }
    }

    @Override public void updateWebViewLayout(int width, int height) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) wvMenuPage.getLayoutParams();
        //params.width = width; do not set width on it else you no longer scroll
        params.height = height;
        wvMenuPage.requestLayout();
    }

    @Override public void scrollToPositionX(int x) {
        wvMenuPage.clearAnimation();
        ObjectAnimator scrollXAnimation = ObjectAnimator.ofInt(wvMenuPage, "scrollX", scrollXPosition(), x);
        scrollXAnimation.setDuration(AnimationUtility.DEFAULT_ANIM_DURATION);
        scrollXAnimation.setInterpolator(new AnimationUtility.DefaultInterpolator());
        scrollXAnimation.start();
    }

    @Override public void loadURL(String url) {
        if(url.startsWith("file:") || !(url.startsWith("javascript:"))) {
            url = Uri.fromFile(new File(url)).toString();
        }
        wvMenuPage.loadUrl(url);
    }

    @Override public void loadURLWithDefaults(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override public void showNavigation() {

        vgTopNavigation.clearAnimation();
        vgBottomNavigation.clearAnimation();

        Animation topIn = AnimationUtils.loadAnimation(getContext(), R.anim.top_nav_in);
        Animation bottomIn = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_nav_in);
        topIn.setDuration(AnimationUtility.DEFAULT_ANIM_DURATION);
        bottomIn.setDuration(AnimationUtility.DEFAULT_ANIM_DURATION);
        topIn.setInterpolator(new AnimationUtility.DefaultInterpolator());
        bottomIn.setInterpolator(new AnimationUtility.DefaultInterpolator());
        topIn.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) { }
            @Override public void onAnimationRepeat(Animation animation) { }
            @Override public void onAnimationEnd(Animation animation) {
                vgTopNavigation.setVisibility(View.VISIBLE);
            }
        });
        bottomIn.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) { }
            @Override public void onAnimationRepeat(Animation animation) { }
            @Override public void onAnimationEnd(Animation animation) {
                vgBottomNavigation.setVisibility(View.VISIBLE);
            }
        });

        vgTopNavigation.startAnimation(topIn);
        vgBottomNavigation.startAnimation(bottomIn);
    }

    @Override public void hideNavigation() {

        vgTopNavigation.clearAnimation();
        vgBottomNavigation.clearAnimation();

        Animation topOut = AnimationUtils.loadAnimation(getContext(), R.anim.top_nav_out);
        Animation bottomOut = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_nav_out);
        topOut.setDuration(AnimationUtility.DEFAULT_ANIM_DURATION);
        bottomOut.setDuration(AnimationUtility.DEFAULT_ANIM_DURATION);
        topOut.setInterpolator(new AnimationUtility.DefaultInterpolator());
        bottomOut.setInterpolator(new AnimationUtility.DefaultInterpolator());
        topOut.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) { }
            @Override public void onAnimationRepeat(Animation animation) { }
            @Override public void onAnimationEnd(Animation animation) {
                vgTopNavigation.setVisibility(View.GONE);
            }
        });
        bottomOut.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) { }
            @Override public void onAnimationRepeat(Animation animation) { }
            @Override public void onAnimationEnd(Animation animation) {
                vgBottomNavigation.setVisibility(View.GONE);
            }
        });

        vgTopNavigation.startAnimation(topOut);
        vgBottomNavigation.startAnimation(bottomOut);
    }

    @Override public boolean isAvailable() {
        return isCallingSafe();
    }

    @Override protected String getClassTag() {
        return MenuFragmentView.class.getSimpleName();
    }

    @Override protected boolean isLogEnabled() {
        return AbstractApplication.isDebug();
    }
}
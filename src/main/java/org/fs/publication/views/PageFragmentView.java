package org.fs.publication.views;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import org.fs.core.AbstractApplication;
import org.fs.core.AbstractFragment;
import org.fs.publication.R;
import org.fs.publication.components.ActivityComponent;
import org.fs.publication.presenters.IPageFragmentPresenter;
import org.fs.publication.presenters.PageFragmentPresenter;
import org.fs.publication.utils.AnimationUtility;

/**
 * Created by Fatih on 05/06/16.
 * as org.fs.publication.views.PageFragmentView
 */
public class PageFragmentView extends AbstractFragment<IPageFragmentPresenter> implements IPageFragmentView {

    private View                                customView;
    private WebChromeClient.CustomViewCallback  callback;

    private FrameLayout     vgPage;
    private WebView         wvContentPage;
    private ProgressBar     pbLoading;

    public static PageFragmentView newInstance(String contentPageURL) {
        Bundle args = new Bundle();
        args.putString(PageFragmentPresenter.KEY_CONTENT_URL, contentPageURL);
        PageFragmentView frag = new PageFragmentView();
        frag.setArguments(args);
        return frag;
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_page_fragment, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vgPage = (FrameLayout) view.findViewById(R.id.vgPage);
        wvContentPage = (WebView) view.findViewById(R.id.wvContentPage);
        pbLoading = (ProgressBar) view.findViewById(R.id.pbLoading);
        //get views
        presenter.restoreState(savedInstanceState != null ? savedInstanceState : getArguments());
        presenter.onCreate();
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.storeState(outState);
    }

    @Override public void onStart() {
        super.onStart();
        wvContentPage.onResume();
        presenter.onStart();
    }

    @Override public void onStop() {
        wvContentPage.onPause();
        presenter.onStop();
        super.onStop();
    }

    @Override public void onDestroy() {
        wvContentPage.destroy();
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override protected IPageFragmentPresenter presenter() {
        return new PageFragmentPresenter(this);
    }

    @Override public void showCustomView(View customView, WebChromeClient.CustomViewCallback callback) {
        this.callback = callback;
        this.customView = customView;
        wvContentPage.setVisibility(View.GONE);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                                                       FrameLayout.LayoutParams.MATCH_PARENT,
                                                                       Gravity.CENTER);
        vgPage.addView(customView, params);
    }

    @Override public void hideCustomView() {
        vgPage.removeView(customView);
        if(callback != null) {
            callback.onCustomViewHidden();
        }
        wvContentPage.setVisibility(View.VISIBLE);
        customView = null;
        callback = null;
    }

    @Override public int contentScrollY() {
        return wvContentPage.getScrollY();
    }

    @Override public void contentScrollY(int y) {
        wvContentPage.clearAnimation();
        ObjectAnimator anim = ObjectAnimator.ofInt(wvContentPage, "scrollY", contentScrollY(), y);
        anim.setDuration(AnimationUtility.DEFAULT_ANIM_DURATION);
        anim.setInterpolator(new AnimationUtility.DefaultInterpolator());
        anim.start();
    }

    @Override public void configureWebView() {
        final WebSettings settings = wvContentPage.getSettings();
        settings.setUseWideViewPort(true);
        settings.setDisplayZoomControls(false);
        settings.setBuiltInZoomControls(false);
        settings.setSupportZoom(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setJavaScriptEnabled(true);//indeed enabled what's wrong enabling it
        wvContentPage.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        wvContentPage.setWebChromeClient(presenter.provideWebChromeClient());
        wvContentPage.setWebViewClient(presenter.provideWebViewClient());
        wvContentPage.setOnTouchListener(presenter.provideTouchListener());
        wvContentPage.setInitialScale(1);
    }

    @Override public void showProgress() {
        pbLoading.setVisibility(View.VISIBLE);
    }

    @Override public void hideProgress() {
        pbLoading.setVisibility(View.GONE);
    }

    @Override public void loadPageURL(String contentURL) {
        wvContentPage.loadUrl(contentURL);
    }

    @Override public void loadURLWithDefaults(String contentURL) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(contentURL));
        startActivity(intent);
    }

    @Override public ActivityComponent activityComponent() {
        return ((ReadActivityView) getActivity()).getActivityComponent();
    }

    @Override public boolean isAvailable() {
        return isCallingSafe();
    }

    @Override protected String getClassTag() {
        return PageFragmentView.class.getSimpleName();
    }

    @Override protected boolean isLogEnabled() {
        return AbstractApplication.isDebug();
    }
}
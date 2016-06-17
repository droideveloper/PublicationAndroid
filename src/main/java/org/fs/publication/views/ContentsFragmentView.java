package org.fs.publication.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.fs.core.AbstractApplication;
import org.fs.core.AbstractFragment;
import org.fs.publication.R;
import org.fs.publication.adapters.ContentsStatePagerAdapter;
import org.fs.publication.components.ActivityComponent;
import org.fs.publication.presenters.ContentsFragmentPresenter;
import org.fs.publication.presenters.IContentsFragmentPresenter;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Fatih on 05/06/16.
 * as org.fs.publication.views.ContentsFragmentView
 */
public class ContentsFragmentView extends AbstractFragment<IContentsFragmentPresenter> implements IContentsFragmentView {

    private ViewPager   vpContents;
    private ProgressBar pbLoading;

    public static ContentsFragmentView newInstance(File contentsDirectory) {
        Bundle args = new Bundle();
        args.putString(ContentsFragmentPresenter.KEY_PUBLICATION_PARENT, contentsDirectory.getPath());
        ContentsFragmentView frag = new ContentsFragmentView();
        frag.setArguments(args);
        return frag;
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_content_fragment, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vpContents = (ViewPager) view.findViewById(R.id.vpContents);
        pbLoading = (ProgressBar) view.findViewById(R.id.pbLoading);

        presenter.restoreState(savedInstanceState != null ? savedInstanceState : getArguments());
        presenter.onCreate();
    }

    @Override public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override public void onStop() {
        presenter.onStop();
        super.onStop();
    }

    @Override public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override protected IContentsFragmentPresenter presenter() {
        return new ContentsFragmentPresenter(this);
    }

    @Override public ActivityComponent getActivityComponent() {
        ReadActivityView activity = (ReadActivityView) getActivity();
        if(isAvailable()) {
            return activity.getActivityComponent();
        }
        return null;
    }

    @Override public void configureViewPager() {
        vpContents.addOnPageChangeListener(presenter.providePageChangeListener());
    }

    @Override public void addAdapter(ArrayList<File> adapterData) {
        ContentsStatePagerAdapter adapter = new ContentsStatePagerAdapter(getChildFragmentManager(), adapterData);
        vpContents.setAdapter(adapter);
    }

    @Override public void selectAtIndex(int index) {
        if(index != selectedIndex()) {
            vpContents.setCurrentItem(index, true);
        }
    }

    @Override public void hideProgress() {
        pbLoading.setVisibility(View.GONE);
    }

    @Override public void showProgress() {
        pbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public int selectedIndex() {
        return vpContents.getCurrentItem();
    }

    @Override public boolean isAvailable() {
        return isCallingSafe();
    }

    @Override protected String getClassTag() {
        return ContentsFragmentView.class.getSimpleName();
    }

    @Override protected boolean isLogEnabled() {
        return AbstractApplication.isDebug();
    }


}
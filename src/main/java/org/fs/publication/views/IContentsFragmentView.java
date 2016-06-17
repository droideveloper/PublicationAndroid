package org.fs.publication.views;

import android.content.Context;

import org.fs.common.IView;
import org.fs.publication.components.ActivityComponent;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Fatih on 05/06/16.
 * as org.fs.publication.views.IContentsFragmentView
 */
public interface IContentsFragmentView extends IView {

    //TODO write java docs


    void configureViewPager();
    void addAdapter(ArrayList<File> adapterData);
    void selectAtIndex(int index);
    void hideProgress();
    void showProgress();

    int     selectedIndex();
    boolean isAvailable();
    ActivityComponent getActivityComponent();
    Context getContext();
}
package org.fs.publication.presenters;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import org.fs.common.IPresenter;

/**
 * Created by Fatih on 05/06/16.
 * as org.fs.publication.presenters.IContentsFragmentPresenter
 */
public interface IContentsFragmentPresenter extends IPresenter {

    //TODO write java docs


    void restoreState(Bundle input);
    void storeState(Bundle output);

    ViewPager.OnPageChangeListener providePageChangeListener();
    int                            findIndexOfContentURL(String contentURL);
}
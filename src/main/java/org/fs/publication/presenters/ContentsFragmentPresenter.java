package org.fs.publication.presenters;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import org.fs.common.AbstractPresenter;
import org.fs.common.BusManager;
import org.fs.core.AbstractApplication;
import org.fs.publication.events.PageSelectedWithIndex;
import org.fs.publication.events.PageSelectedWithURL;
import org.fs.publication.views.IContentsFragmentView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Fatih on 05/06/16.
 * as org.fs.publication.presenters.ContentsFragmentPresenter
 */
public class ContentsFragmentPresenter extends AbstractPresenter<IContentsFragmentView> implements IContentsFragmentPresenter,
                                                                                                   ViewPager.OnPageChangeListener,
                                                                                                   Action1<Object>,
                                                                                                   FilenameFilter {

    public static final String KEY_PUBLICATION_PARENT = "contents.file.parent";
    public static final String KEY_CONTENT_INDEX      = "contents.pager.index";

    private static final String INDEX_FILE  = "index.html";
    private static final String INDEX_FILE2 = "index.htm";

    private static final String HTML_FILE  = ".html";
    private static final String HTML_FILE2 = ".htm";


    private ArrayList<File> contentsArray;
    private int             contentsIndex;
    private File            contentsDirectory;

    private Subscription    busListener;

    @Inject BusManager busManager;

    public ContentsFragmentPresenter(IContentsFragmentView view) {
        super(view);
    }

    @Override
    public void restoreState(Bundle input) {
        if(input != null) {
            String filePath = input.getString(KEY_PUBLICATION_PARENT);
            if(filePath != null) {
                contentsDirectory = new File(filePath);//is there a bug or something or just to protect data from accessing outside ?
            }
            contentsIndex =  input.getInt(KEY_CONTENT_INDEX);
        }
    }

    @Override
    public void storeState(Bundle output) {
        if(contentsDirectory != null) {
            output.putString(KEY_PUBLICATION_PARENT, contentsDirectory.getPath());
        }
        output.putInt(KEY_CONTENT_INDEX, contentsIndex);
    }

    @Override
    public ViewPager.OnPageChangeListener providePageChangeListener() {
        return this;
    }

    @Override
    public void onCreate() {
        view.getActivityComponent().inject(this);
        busListener = busManager.register(this);
        view.configureViewPager();
        if(contentsDirectory != null) {
            contentsArray = new ArrayList<>(Arrays.asList(contentsDirectory.listFiles(this)));
        }
    }

    @Override
    public void onStart() {
        if(view.isAvailable()) {
            view.showProgress();
            view.addAdapter(contentsArray);
            view.selectAtIndex(contentsIndex);
            view.hideProgress();
        }
    }

    @Override public void onStop() {
        if(busListener != null) {
            busManager.unregister(busListener);
            busListener = null;
        }
    }

    @Override public void onDestroy() {
        //no op
    }

    @Override protected String getClassTag() {
        return ContentsFragmentPresenter.class.getSimpleName();
    }

    @Override protected boolean isLogEnabled() {
        return AbstractApplication.isDebug();
    }

    @Override public void call(Object event) {
        if(event instanceof PageSelectedWithURL) {
            PageSelectedWithURL selectedWithURLEvent = (PageSelectedWithURL) event;
            int selectedIndex = findIndexOfContentURL(selectedWithURLEvent.contentURL());
            if(selectedIndex >= 0) {
                view.selectAtIndex(selectedIndex);
            }
        }
    }

    @Override public int findIndexOfContentURL(String contentURL) {
        contentURL = Uri.decode(contentURL);
        for(int i = 0; i < contentsArray.size(); i++) {
            File file = contentsArray.get(i);
            if(contentURL.contains(file.getPath())) {
                return i;
            }
        }
        return -1;
    }

    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //no op
    }

    @Override public void onPageScrollStateChanged(int state) {
        //no op
    }

    @Override public void onPageSelected(int position) {
        //in Marshmallow there is exception thrown for some reason about regarding value as int...
        if(position != (contentsArray.size() -1)) {
            contentsIndex = position;
            busManager.post(new PageSelectedWithIndex(position));
        }
    }

    @Override
    public boolean accept(File parent, String fileName) {
        return !(fileName.equalsIgnoreCase(INDEX_FILE)
                || fileName.equalsIgnoreCase(INDEX_FILE2))
                && (fileName.contains(HTML_FILE)
                || fileName.contains(HTML_FILE2));
    }
}
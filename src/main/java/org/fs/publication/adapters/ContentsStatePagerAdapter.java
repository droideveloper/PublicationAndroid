package org.fs.publication.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.fs.core.AbstractApplication;
import org.fs.core.AbstractStatePagerAdapter;
import org.fs.publication.views.PageFragmentView;

import java.io.File;
import java.util.List;

/**
 * Created by Fatih on 05/06/16.
 * as org.fs.publication.adapters.ContentsStatePagerAdapter
 */
public class ContentsStatePagerAdapter extends AbstractStatePagerAdapter<File> {

    public ContentsStatePagerAdapter(FragmentManager fragmentManager, List<File> dataSet) {
        super(fragmentManager, dataSet);
    }

    @Override protected String getClassTag() {
        return ContentsStatePagerAdapter.class.getSimpleName();
    }

    @Override protected boolean isLogEnabled() {
        return AbstractApplication.isDebug();
    }

    @Override protected Fragment onBind(int position, File element) {
        String contentsURL = element.toURI().toString();
        return PageFragmentView.newInstance(contentsURL);
    }
}
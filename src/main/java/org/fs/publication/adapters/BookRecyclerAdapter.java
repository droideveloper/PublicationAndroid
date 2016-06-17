package org.fs.publication.adapters;

import android.view.ViewGroup;

import org.fs.core.AbstractApplication;
import org.fs.core.AbstractRecyclerAdapter;
import org.fs.publication.R;
import org.fs.publication.entities.Book;
import org.fs.publication.presenters.IShelfActivityPresenter;

import java.util.ArrayList;

/**
 * Created by Fatih on 07/06/16.
 * as org.fs.publication.adapters.BookRecyclerAdapter
 */
public class BookRecyclerAdapter extends AbstractRecyclerAdapter<Book, BookViewHolder> {

    final BookViewHolder.OnViewHolderSelectedListener selectedListener;

    public BookRecyclerAdapter(IShelfActivityPresenter presenter) {
        //Collections.<Book>emptyList(); can not mutable so exception was thrown.
        super(new ArrayList<Book>(), presenter.bindContext());
        this.selectedListener = presenter.bindSelectedListener();
    }

    @Override public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return BookViewHolder.newInstance(parent, getContext(), R.layout.layout_book_view);
    }

    @Override public void onBindViewHolder(BookViewHolder viewHolder, int position) {
        final Book book = getItemAtIndex(position);
        viewHolder.setBookAndListener(book, selectedListener);
    }

    @Override public int getItemViewType(int position) {
        //no op, only have one type of view so be good boy
        return 0;
    }

    @Override protected String getClassTag() {
        return BookRecyclerAdapter.class.getSimpleName();
    }

    @Override protected boolean isLogEnabled() {
        return AbstractApplication.isDebug();
    }
}
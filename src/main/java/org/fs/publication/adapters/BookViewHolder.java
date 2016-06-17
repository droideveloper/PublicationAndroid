package org.fs.publication.adapters;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.fs.core.AbstractApplication;
import org.fs.core.AbstractRecyclerViewHolder;
import org.fs.evoke.NetworkJob;
import org.fs.publication.R;
import org.fs.publication.entities.Book;
import org.fs.publication.entities.Download;
import org.fs.publication.utils.ViewUtility;
import org.fs.publication.widget.ILayout;
import org.fs.publication.widget.Layout;
import org.fs.publication.widget.RatioImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Fatih on 07/06/16.
 * as org.fs.publication.adapters.BookViewHolder
 */
public class BookViewHolder extends AbstractRecyclerViewHolder<Book> implements IBookViewHolder, View.OnClickListener, ILayout.OnAttachStateCallback {

    public static final int ACTION_DELETE   = 0x01;
    public static final int ACTION_CANCEL   = 0x02;
    public static final int ACTION_READ     = 0x03;
    public static final int ACTION_DOWNLOAD = 0x04;

    private final static int ANIMATION_DURATION = 100;

    private RatioImageView imgCover;
    private TextView       txtTitle;
    private TextView       txtInfo;
    private TextView       txtDate;

    private ViewGroup      vgProgress;
    private TextView       txtPercentage;
    private ProgressBar    pgPercentage;

    private Button         btnAction;
    private Button         btnDelete;

    private DateFormat     dateFormat;

    private Download       download;
    private NetworkJob     job;
    private Book           book;
    private OnViewHolderSelectedListener listener;

    /**
     * Static way of creating BookViewHolder, just way of clean newInstance thing
     * @param parent ViewGroup that will display this thing's view as its child
     * @param context context that view created on
     * @param layoutId layout of this view as int id
     * @return BookViewHolder instance
     */
    public static BookViewHolder newInstance(ViewGroup parent, Context context, @LayoutRes int layoutId) {
        LayoutInflater factory = LayoutInflater.from(context);
        View view = factory.inflate(layoutId, parent, false);
        return new BookViewHolder(view);
    }

    public BookViewHolder(View view) {
        super(view);
        //we redefined state listener in order to work with api 11 ? (WTF Google WTF! <<< personal thoughts only)
        Layout contentView = (Layout) view;
        contentView.addLifecycleListener(this);

        imgCover = findViewById(view, R.id.imgCover);
        txtTitle = findViewById(view, R.id.txtTitle);
        txtInfo = findViewById(view, R.id.txtInfo);
        txtDate = findViewById(view, R.id.txtDate);
        vgProgress = findViewById(view, R.id.vgProgress);
        txtPercentage = findViewById(view, R.id.txtPercentage);
        pgPercentage = findViewById(view, R.id.pgPercentage);
        btnAction = findViewById(view, R.id.btnAction);
        btnDelete = findViewById(view, R.id.btnDelete);
        dateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault());
    }

    @Override protected void onBindView(Book book) {
        if(book != null) {
            Glide.with(itemView.getContext())
                 .load(book.getCover())
                 .placeholder(R.drawable.img_placeholder)
                 .error(R.drawable.img_error)
                 .crossFade(ANIMATION_DURATION)
                 .into(imgCover);

            txtTitle.setText(book.getTitle());
            txtInfo.setText(book.getInfo());
            txtDate.setText(dateFormat.format(book.getDate()));
            //clickListener
            btnAction.setOnClickListener(this);
            btnDelete.setOnClickListener(this);
        }
    }

    @Override protected String getClassTag() {
        return BookViewHolder.class.getSimpleName();
    }

    @Override protected boolean isLogEnabled() {
        return AbstractApplication.isDebug();
    }

    @Override public void setBookAndListener(Book book, OnViewHolderSelectedListener listener) {
        this.book = book;
        this.listener = listener;
        notifyDataSet();
    }

    @Override public void setDownload(Download download) {
        this.download = download;
    }

    @Override public Download getDownload() {
        return download;
    }

    @Override public void setNetworkJob(NetworkJob job) {
        this.job = job;
    }

    @Override public NetworkJob getNetworkJob() {
        return job;
    }

    @Override public void notifyDataSet() {
        onBindView(book);
    }

    @Override public String getString(@StringRes int stringId) {
        return itemView.getContext().getString(stringId);
    }

    @Override public void changeActionText(@StringRes int stringId) {
        btnAction.setText(getString(stringId));
    }

    @Override public Book getBookObject() {
        return book;
    }

    @Override public void showProgress(int percent) {
        if(!isProgressVisible()) {
            vgProgress.setVisibility(View.VISIBLE);
        }
        String formated = String.format(Locale.getDefault(), getString(R.string.downloaded_format), percent) + " %";
        txtPercentage.setText(formated);
        pgPercentage.setProgress(percent);
    }

    @Override public boolean isProgressVisible() {
        return ViewUtility.isVisible(vgProgress);
    }

    @Override public void hideProgress() {
        vgProgress.setVisibility(View.GONE);
    }

    @Override public void showDeleteAction() {
        btnDelete.setVisibility(View.VISIBLE);
    }

    @Override public void hideDeleteAction() {
        btnDelete.setVisibility(View.GONE);
    }

    @Override public boolean isDeleteActionVisible() {
        return ViewUtility.isVisible(btnDelete);
    }

    @SuppressWarnings("unchecked") @Override public <T> T findViewById(View view, @IdRes int id) {
        return (T) view.findViewById(id);
    }

    @Override public void onClick(View v) {
        int action;
        if(v.equals(btnAction)) {
            String txtCurrentAction = btnAction.getText().toString();
            if(txtCurrentAction.equalsIgnoreCase(getString(R.string.btn_cancel))) {
                action = ACTION_CANCEL;
            } else if(txtCurrentAction.equalsIgnoreCase(getString(R.string.btn_download))) {
                action = ACTION_DOWNLOAD;
            } else { //only read left
                action = ACTION_READ;
            }
        } else {
            action = ACTION_DELETE;
        }
        //if we have listener
        if(listener != null) {
            listener.onSelectViewHolder(this, action);
        }
    }

    @Override public void onViewAttachedToWindow(View v) {
        if(listener != null) {
            listener.onAttachedWindow(this);
        }
    }

    @Override public void onViewDetachedFromWindow(View v) {
        if(listener != null) {
            listener.onDetachedWindow(this);
        }
    }

    /**
     * listener that will wrap up our click actions with holder itself
     */
    public interface OnViewHolderSelectedListener {
        /**
         * Callback that handles concept of business logic to be transferred into presenter
         * @param viewHolder viewHolder interface passed
         * @param action type of action performed
         */
        void onSelectViewHolder(IBookViewHolder viewHolder, int action);
        //lifecycle
        void onAttachedWindow(IBookViewHolder viewHolder);
        void onDetachedWindow(IBookViewHolder viewHolder);
    }
}
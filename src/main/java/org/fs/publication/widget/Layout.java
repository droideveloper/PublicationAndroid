package org.fs.publication.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Fatih on 07/06/16.
 * as org.fs.publication.widget.Layout
 */
public class Layout extends LinearLayout implements ILayout {

    private OnAttachStateCallback callback;

    public Layout(Context context) {
        super(context);
    }

    public Layout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Layout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(callback != null) {
            callback.onViewAttachedToWindow(this);
        }
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(callback != null) {
            callback.onViewDetachedFromWindow(this);
        }
    }

    @Override public void addLifecycleListener(OnAttachStateCallback callback) {
        this.callback = callback;
    }
}

package org.fs.publication.widget;

import android.view.View;

/**
 * Created by Fatih on 07/06/16.
 * as org.fs.publication.widget.ILayout
 */
public interface ILayout {

    void addLifecycleListener(OnAttachStateCallback listener);

    /**
     * Clone of api 12 mother fuckers!
     */
    interface OnAttachStateCallback {
        void onViewAttachedToWindow(View v);
        void onViewDetachedFromWindow(View v);
    }
}

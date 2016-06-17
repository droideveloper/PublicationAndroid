package org.fs.publication.utils;

import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.View;

import org.fs.exception.AndroidException;

/**
 * Created by Fatih on 07/06/16.
 * as org.fs.publication.utils.ViewUtility
 */
public final class ViewUtility {

    private ViewUtility() {
        throw new AndroidException("no instance for ya");
    }

    /**
     * Checks whether view is visible or not, hint if view object is null it also accepted non-visible
     * @param view view to check visibility
     * @return true or false
     */
    public static boolean isVisible(@Nullable View view) {
        return view != null && (view.getVisibility() == View.VISIBLE);
    }

    /**
     * easy way of casting views into something weird ;) like this a lot
     * @param view view for search on
     * @param viewId id of view to search
     * @param <T> Type of field auto defied
     * @return T type of view
     */
    public static <T> T findViewById(View view, @IdRes int viewId) {
        return (T) view.findViewById(viewId);
    }
 }

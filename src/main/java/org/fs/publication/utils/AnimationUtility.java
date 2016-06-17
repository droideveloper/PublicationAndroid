package org.fs.publication.utils;

import android.os.Build;
import android.support.annotation.AnimRes;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import org.fs.exception.AndroidException;
import org.fs.publication.R;
import org.fs.util.PreconditionUtility;

/**
 * Created by Fatih on 04/06/16.
 * as org.fs.publication.utils.AnimationUtility
 */
public final class AnimationUtility {

    public final static long DEFAULT_ANIM_DURATION  = 200L;

    private final static int VISIBILITY_GONE        = 0x01;
    private final static int VISIBILITY_VISIBLE     = 0x02;

    private AnimationUtility() {
        throw new AndroidException("no instance for you, reflection buddy");
    }

    public static class DefaultInterpolator implements Interpolator {
        @Override public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    }

    private static class SimpleAnimationListener implements Animation.AnimationListener {

        private final View view;
        private final int  viewLayerType;
        private final int  visibilityAtEnd;

        public SimpleAnimationListener(final View view, final int visibilityAtEnd) {
            this.view = view;
            this.viewLayerType = view.getLayerType();
            this.visibilityAtEnd = visibilityAtEnd;
        }

        @Override public void onAnimationStart(Animation animation) {
            if(Build.VERSION.SDK_INT >= 18) {
                setViewLayer(view, View.LAYER_TYPE_HARDWARE);
            } else {
                setViewLayer(view, View.LAYER_TYPE_SOFTWARE);
            }
        }

        @Override public void onAnimationRepeat(Animation animation) { }

        @Override public void onAnimationEnd(Animation animation) {
            if(visibilityAtEnd == VISIBILITY_GONE) {
                setViewGoneOnAnimationEnd(view);
            } else if(visibilityAtEnd == VISIBILITY_VISIBLE) {
                setViewVisibleOnAnimationEnd(view);
            }
            setViewLayer(view, viewLayerType);
        }
    }

    private static void setViewLayer(View view, int viewLayerType) {
        PreconditionUtility.checkNotNull(view, "view can not be null");
        view.setLayerType(viewLayerType, null);
    }

    private static void setViewGoneOnAnimationEnd(View view) {
        PreconditionUtility.checkNotNull(view, "view can not be null");
        view.setVisibility(View.GONE);
    }

    private static void setViewVisibleOnAnimationEnd(View view) {
        PreconditionUtility.checkNotNull(view, "view can not be null");
        view.setVisibility(View.VISIBLE);
    }

    private static Animation createWithListenerAndViewVisibilityOnEnd(Animation anim, View view, int visibilityType) {
        anim.setAnimationListener(new SimpleAnimationListener(view, visibilityType));
        anim.setInterpolator(new DefaultInterpolator());
        anim.setDuration(DEFAULT_ANIM_DURATION);
        return anim;
    }

    private static Animation createWithListenerAndViewVisibilityOnEnd(@AnimRes int res, View view, int visibilityType) {
        Animation anim = AnimationUtils.loadAnimation(view.getContext(), res);
        return createWithListenerAndViewVisibilityOnEnd(anim, view, visibilityType);
    }

    //TODO write java docs for AnimationUtility.class

    /**
     *
     * @param view
     * @return
     */
    public static Animation createExitAnimationForTopNavigation(View view) {
        return createWithListenerAndViewVisibilityOnEnd(R.anim.top_nav_out, view, VISIBILITY_GONE);
    }

    /**
     *
     * @param view
     * @return
     */
    public static Animation createExitAnimationForBottompNavigation(View view) {
        return createWithListenerAndViewVisibilityOnEnd(R.anim.bottom_nav_out, view, VISIBILITY_GONE);
    }

    /**
     *
     * @param view
     * @return
     */
    public static Animation createEnterAnimationForTopNavigation(View view) {
        return createWithListenerAndViewVisibilityOnEnd(R.anim.top_nav_in, view, VISIBILITY_VISIBLE);
    }

    /**
     *
     * @param view
     * @return
     */
    public static Animation createEnterAnimationForBottomNavigation(View view) {
        return createWithListenerAndViewVisibilityOnEnd(R.anim.bottom_nav_in, view, VISIBILITY_VISIBLE);
    }
}

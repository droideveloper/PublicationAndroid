package org.fs.publication.utils;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Fatih on 05/06/16.
 * as org.fs.publication.utils.OO
 */
public class GestureHelper extends GestureDetector.SimpleOnGestureListener {

    private final static GestureHelper gestureHelper;

    static {
        gestureHelper = new GestureHelper();
    }

    private boolean                   toggleState;
    private OnNavigationStateListener proxyListener;

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        setInitialValue(!toggleState);//swap value
        return proxyListener != null && proxyListener.onDoubleTap(e);
    }

    public void setInitialValue(boolean value) {
        toggleState = value;
    }

    /**
     *
     * @param proxyListener is a only needed part at hand while we do not need to wrap others in advance simply wrapping down on wrapper :)
     * @return GestureHelper instance in advance
     */
    public static GestureHelper addNavigationStateListener(OnNavigationStateListener proxyListener) {
        gestureHelper.proxyListener = proxyListener;
        setInitialState(false);
        return gestureHelper;
    }

    public static void setInitialState(boolean state) {
        gestureHelper.setInitialValue(state);
    }

    /**
     * weird but only way of tracking this value
     * @return true or false for toggle state of navigation shown or not to be used
     */
    public static boolean isDisplay() {
        return gestureHelper.toggleState;
    }

    public interface OnNavigationStateListener {
        boolean onDoubleTap(MotionEvent e);
    }
}

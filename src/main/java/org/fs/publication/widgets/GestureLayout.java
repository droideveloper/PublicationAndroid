/*
 * BakerPublicationAndroid Copyright (C) 2017 Fatih.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fs.publication.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import org.fs.common.BusManager;
import org.fs.publication.entities.events.VisibilityChange;

public class GestureLayout extends FrameLayout {

  private GestureDetector detector;
  private View view;

  public GestureLayout(Context context) {
    super(context);
  }

  public GestureLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public GestureLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();

    detector = new GestureDetector(getContext(), new GestureListener());

    ViewGroup parent = (ViewGroup) getParent();
    int z = parent.getChildCount();
    for (int i = 0; i < z; i++) {
      View view = parent.getChildAt(i);
      if (view instanceof WebView) {
        this.view = view;
        break;
      }
    }
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    view = null;
    detector = null;
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    if (view != null) {
      view.onTouchEvent(event);
    }
    return detector != null ? detector.onTouchEvent(event) : super.onTouchEvent(event);
  }

  private class GestureListener extends GestureDetector.SimpleOnGestureListener {

    @Override public boolean onDown(MotionEvent e) {
      return true;
    }

    @Override public boolean onDoubleTap(MotionEvent e) {
      BusManager.send(new VisibilityChange());
      return true;
    }
  };
}

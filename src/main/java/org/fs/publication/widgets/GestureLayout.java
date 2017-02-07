/*
 * Publication Copyright (C) 2017 Fatih.
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

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import org.fs.common.BusManager;
import org.fs.publication.entities.events.VisibilityChange;

public class GestureLayout extends FrameLayout {

  private final GestureDetector gestureDetector;

  public GestureLayout(Context context) {
    super(context);
    gestureDetector = new GestureDetector(context, new GestureListener());
  }

  public GestureLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    gestureDetector = new GestureDetector(context, new GestureListener());
  }

  public GestureLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    gestureDetector = new GestureDetector(context, new GestureListener());
  }

  @TargetApi(Build.VERSION_CODES.N_MR1)
  public GestureLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    gestureDetector = new GestureDetector(context, new GestureListener());
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    gestureDetector.onTouchEvent(event);
    return super.onTouchEvent(event);
  }

  private class GestureListener extends GestureDetector.SimpleOnGestureListener {

    @Override public boolean onDown(MotionEvent e) {
      return true;
    }

    @Override public boolean onDoubleTap(MotionEvent e) {
      BusManager.send(new VisibilityChange());
      return true;
    }
  }
}

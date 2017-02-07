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

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class PaginationTextView extends TextView {

  public PaginationTextView(Context context) {
    super(context);
  }

  public PaginationTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public PaginationTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int vsize = MeasureSpec.getSize(widthMeasureSpec);
    int hsize = MeasureSpec.getSize(heightMeasureSpec);
    if (Math.min(vsize, hsize) == vsize) {
      super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    } else if (Math.min(vsize, hsize) == hsize) {
      super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    } else {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
  }
}

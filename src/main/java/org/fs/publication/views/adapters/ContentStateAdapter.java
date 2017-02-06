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
package org.fs.publication.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import org.fs.core.AbstractStatePagerAdapter;
import org.fs.publication.BuildConfig;
import org.fs.publication.views.ContentFragment;
import org.fs.util.IPropertyChangedListener;
import org.fs.util.ObservableList;

public class ContentStateAdapter extends AbstractStatePagerAdapter<String> implements
    IPropertyChangedListener {

  public ContentStateAdapter(FragmentManager fragmentManager, ObservableList<String> dataSet) {
    super(fragmentManager, dataSet);
    if (dataSet != null) {
      dataSet.registerPropertyChangedListener(this);
    }
  }

  @Override protected Fragment onBind(int position, String uri) {
    return ContentFragment.newInstance(uri);
  }

  @Override public void notifyItemsRemoved(int index, int size) {
    notifyDataSetChanged();
  }

  @Override public void notifyItemsInserted(int index, int size) {
    notifyDataSetChanged();
  }

  @Override public void notifyItemsChanged(int index, int size) {
    notifyDataSetChanged();
  }

  @Override protected String getClassTag() {
    return ContentStateAdapter.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }
}
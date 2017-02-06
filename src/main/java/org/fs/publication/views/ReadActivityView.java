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
package org.fs.publication.views;

import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;
import org.fs.common.IView;
import org.fs.core.AbstractFragment;
import org.fs.util.ObservableList;

public interface ReadActivityView extends IView {
  void setup();

  void showProgress();
  void hideProgress();

  <V extends AbstractFragment<?>> void newView(@IdRes int layout, V view);

  FragmentManager fragmentManager();
  ObservableList<String> contents();

  void hideNavigation();
  void showNavigation();

  void setTitle(String titleStr);

  boolean isDisplayNavigation();

  void showPageAt(int index);
  int currentPageAt();
}
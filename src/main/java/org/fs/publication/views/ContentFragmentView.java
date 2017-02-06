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

import android.view.View;
import android.webkit.WebChromeClient;
import org.fs.common.IView;

public interface ContentFragmentView extends IView {
  void setup();

  void showProgress();
  void hideProgress();

  void showCustomView(View view, WebChromeClient.CustomViewCallback callback);
  void hideCustomView();

  void loadUri(String uri);
  boolean shouldLoadUri(String uri);

  int scrollY();
  void scrollY(int y);
}
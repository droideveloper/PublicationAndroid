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
package org.fs.publication.commons.modules;

import dagger.Module;
import dagger.Provides;
import java.util.Locale;
import org.fs.common.IView;
import org.fs.exception.AndroidException;
import org.fs.publication.commons.scopes.PerActivity;
import org.fs.publication.presenters.ReadActivityPresenter;
import org.fs.publication.presenters.ReadActivityPresenterImp;
import org.fs.publication.views.ReadActivityView;
import org.fs.publication.views.adapters.ContentStateAdapter;
import org.fs.util.ObservableList;

@Module
public class ActivityModule {

  private final IView view;
  private ObservableList<String> contents;

  public ActivityModule(final IView view) {
    this.view = view;
    this.contents = new ObservableList<>();
  }

  @PerActivity @Provides public ReadActivityPresenter readActivityPresenter() {
    if (view instanceof ReadActivityView) {
      return new ReadActivityPresenterImp((ReadActivityView) view, contents);
    }
    throw new AndroidException(
        String.format(Locale.ENGLISH,
            "view instance of this module is not valid %s",
            view.getClass().getSimpleName()));
  }

  @PerActivity @Provides public ContentStateAdapter contentStateAdapter() {
    if (view instanceof ReadActivityView) {
      ReadActivityView v = (ReadActivityView) view;
      return new ContentStateAdapter(v.fragmentManager(), contents);
    }
    throw new AndroidException(
        String.format(Locale.ENGLISH,
            "view instance of this module is not valid %s",
            view.getClass().getSimpleName()));
  }
}

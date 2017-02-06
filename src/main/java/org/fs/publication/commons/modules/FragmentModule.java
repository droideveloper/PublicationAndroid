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
import org.fs.publication.commons.scopes.PerFragment;
import org.fs.publication.presenters.ContentFragmentPresenter;
import org.fs.publication.presenters.ContentFragmentPresenterImp;
import org.fs.publication.presenters.NavigationFragmentPresenter;
import org.fs.publication.presenters.NavigationFragmentPresenterImp;
import org.fs.publication.views.ContentFragmentView;
import org.fs.publication.views.NavigationFragmentView;

@Module public class FragmentModule {

  public final IView view;

  public FragmentModule(final IView view) {
    this.view = view;
  }

  @PerFragment @Provides public ContentFragmentPresenter contentFragmentPresenter() {
    if(view instanceof ContentFragmentView) {
      return new ContentFragmentPresenterImp((ContentFragmentView) view);
    }
    throw new AndroidException(
        String.format(Locale.ENGLISH,
            "view instance of this module is not valid %s",
            view.getClass().getSimpleName()));
  }

  @PerFragment @Provides public NavigationFragmentPresenter navigationFragmentPresenter() {
    if(view instanceof NavigationFragmentView) {
      return new NavigationFragmentPresenterImp((NavigationFragmentView) view);
    }
    throw new AndroidException(
        String.format(Locale.ENGLISH,
            "view instance of this module is not valid %s",
            view.getClass().getSimpleName()));
  }
}
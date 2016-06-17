package org.fs.publication.components;

import org.fs.publication.managers.IDatabaseManager;
import org.fs.publication.managers.IFileManager;
import org.fs.publication.managers.IPreferenceManager;
import org.fs.publication.modules.ActivityModule;
import org.fs.publication.presenters.ContentsFragmentPresenter;
import org.fs.publication.presenters.MenuFragmentPresenter;
import org.fs.publication.presenters.PageFragmentPresenter;
import org.fs.publication.presenters.ReadActivityPresenter;
import org.fs.publication.presenters.ShelfActivityPresenter;

import dagger.Component;

/**
 * Created by Fatih on 06/06/16.
 * as org.fs.publication.components.ActivityComponent
 */
@ForActivity
@Component(modules = ActivityModule.class, dependencies = AppComponent.class)
public interface ActivityComponent {

    void inject(ContentsFragmentPresenter presenter);
    void inject(MenuFragmentPresenter presenter);
    void inject(ReadActivityPresenter presenter);
    void inject(PageFragmentPresenter presenter);
    void inject(ShelfActivityPresenter presenter);

    IFileManager        getFileManager();
    IPreferenceManager  getPreferenceManager();
    IDatabaseManager    getDatabaseManager();
}

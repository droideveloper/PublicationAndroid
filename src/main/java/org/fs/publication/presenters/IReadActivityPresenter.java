package org.fs.publication.presenters;

import android.os.Bundle;

import org.fs.common.IPresenter;

import java.io.File;

/**
 * Created by Fatih on 06/06/16.
 * as org.fs.publication.presenters.IReadActivityPresenter
 */
public interface IReadActivityPresenter extends IPresenter {

    void restoreState(Bundle input);
    void storeState(Bundle output);
    void extractAsync(File file);
    void notifyError(String msg);

    File    findMenuFile(File parent);
    boolean isMenuFileExists(File menuFile);
}
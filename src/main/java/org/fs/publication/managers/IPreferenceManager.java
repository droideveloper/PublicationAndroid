package org.fs.publication.managers;

/**
 * Created by Fatih on 04/06/16.
 * as org.fs.publication.managers.IPreferenceManager
 */
public interface IPreferenceManager {


    /*
        Log Helpers
     */

    void    log(String msg);
    void    log(Exception exp);
    void    log(int lv, String msg);
    String  getClassTag();
    boolean isLogEnabled();

    boolean keepArchiveFiles();

}

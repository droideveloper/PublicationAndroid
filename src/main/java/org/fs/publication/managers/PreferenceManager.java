package org.fs.publication.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.fs.core.AbstractApplication;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Fatih on 06/06/16.
 * as org.fs.publication.managers.PreferenceManager
 */
public class PreferenceManager implements IPreferenceManager {

    private final static String KEY_KEEP_ARCHIVE_FILES = "keepArchiveFiles";

    private final SharedPreferences defaultPreference;

    public PreferenceManager(Context context) {
        defaultPreference = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override public boolean keepArchiveFiles() {
        return defaultPreference.getBoolean(KEY_KEEP_ARCHIVE_FILES, false);
    }

    @Override public void log(String msg) {
        log(Log.DEBUG, msg);
    }

    @Override public void log(Exception exp) {
        StringWriter str = new StringWriter();
        PrintWriter ptr = new PrintWriter(str);
        exp.printStackTrace(ptr);
        log(Log.ERROR, str.toString());
    }

    @Override public void log(int lv, String msg) {
        if(isLogEnabled()) {
            Log.println(lv, getClassTag(), msg);
        }
    }

    @Override public String getClassTag() {
        return PreferenceManager.class.getSimpleName();
    }

    @Override public boolean isLogEnabled() {
        return AbstractApplication.isDebug();
    }
}

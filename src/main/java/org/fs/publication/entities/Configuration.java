package org.fs.publication.entities;

import android.os.Parcel;

import org.fs.core.AbstractApplication;
import org.fs.core.AbstractEntity;
import org.fs.util.StringUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fatih on 05/06/16.
 * as org.fs.publication.entities.Info
 */
public final class Configuration extends AbstractEntity {

    private String       title;
    private List<String> contents;

    public Configuration() { }
    public Configuration(Parcel input) {
        super(input);
    }

    public ArrayList<String> getContents() {
        return new ArrayList<>(contents);
    }
    public String getTitle() {
        return title;
    }

    @Override protected void readParcel(Parcel input) {
        boolean contentsHaveValue = input.readInt() == 1;
        if(contentsHaveValue) {
            contents = new ArrayList<>();
            input.readStringList(contents);
        }
        boolean titleHasValue = input.readInt() == 1;
        if(titleHasValue) {
            title = input.readString();
        }
    }

    @Override public void writeToParcel(Parcel out, int flags) {
        boolean contentsHaveValue = contents != null && !contents.isEmpty();
        out.writeInt(contentsHaveValue ? 1 : 0);
        if(contentsHaveValue) {
            out.writeStringList(contents);
        }
        boolean titleHasValue = !StringUtility.isNullOrEmpty(title);
        out.writeInt(titleHasValue ? 1 : 0);
        if(titleHasValue) {
            out.writeString(title);
        }
    }

    @Override public int describeContents() {
        return 0;
    }

    public final static Creator<Configuration> CREATOR = new Creator<Configuration>() {

        @Override public Configuration createFromParcel(Parcel input) {
            return new Configuration(input);
        }

        @Override public Configuration[] newArray(int size) {
            return new Configuration[size];
        }
    };

    @Override protected String getClassTag() {
        return Configuration.class.getSimpleName();
    }

    @Override protected boolean isLogEnabled() {
        return AbstractApplication.isDebug();
    }
}
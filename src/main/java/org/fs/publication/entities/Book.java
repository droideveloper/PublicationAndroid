package org.fs.publication.entities;

import android.os.Parcel;

import org.fs.core.AbstractApplication;
import org.fs.core.AbstractEntity;
import org.fs.util.StringUtility;

import java.util.Date;

/**
 * Created by Fatih on 05/06/16.
 * as org.fs.publication.entities.Book
 */
public final class Book extends AbstractEntity {

    private String name;
    private String title;
    private String info;
    private Date   date;
    private String cover;
    private String url;

    /*GSON needs this*/
    public Book() { }
    public Book(Parcel input) {
        super(input);
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getInfo() {
        return info;
    }

    public Date getDate() {
        return date;
    }

    public String getCover() {
        return cover;
    }

    public String getUrl() {
        return url;
    }

    @Override protected void readParcel(Parcel input) {
        boolean nameHasValue = input.readInt() == 1;
        if(nameHasValue) {
            name = input.readString();
        }
        boolean titleHasValue = input.readInt() == 1;
        if(titleHasValue) {
            title = input.readString();
        }
        boolean infoHasValue = input.readInt() == 1;
        if(infoHasValue) {
            info = input.readString();
        }
        boolean dateHasValue = input.readInt() == 1;
        if (dateHasValue) {
            date = new Date(input.readLong());
        }
        boolean coverHasValue = input.readInt() == 1;
        if(coverHasValue) {
            cover = input.readString();
        }
        boolean urlHasValue = input.readInt() == 1;
        if(urlHasValue) {
            url = input.readString();
        }
    }

    @Override public void writeToParcel(Parcel out, int flags) {
        boolean nameHasValue = !StringUtility.isNullOrEmpty(name);
        out.writeInt(nameHasValue ? 1 : 0);
        if(nameHasValue) {
            out.writeString(name);
        }
        boolean titleHasValue = !StringUtility.isNullOrEmpty(title);
        out.writeInt(titleHasValue ? 1 : 0);
        if(titleHasValue) {
            out.writeString(title);
        }
        boolean infoHasValue = !StringUtility.isNullOrEmpty(info);
        out.writeInt(infoHasValue ? 1 : 0);
        if(infoHasValue) {
            out.writeString(info);
        }
        boolean dateHasValue = !StringUtility.isNullOrEmpty(date);
        out.writeInt(dateHasValue ? 1 : 0);
        if(dateHasValue) {
            out.writeLong(date.getTime());
        }
        boolean coverHasValue = !StringUtility.isNullOrEmpty(cover);
        out.writeInt(coverHasValue ? 1 : 0);
        if(coverHasValue) {
            out.writeString(cover);
        }
        boolean urlHasValue = !StringUtility.isNullOrEmpty(url);
        out.writeInt(urlHasValue ? 1 : 0);
        if(urlHasValue) {
            out.writeString(url);
        }
    }

    @Override public int describeContents() {
        return 0;
    }

    public final static Creator<Book> CREATOR = new Creator<Book>() {

        @Override public Book createFromParcel(Parcel input) {
            return new Book(input);
        }

        @Override public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override protected String getClassTag() {
        return Book.class.getSimpleName();
    }

    @Override protected boolean isLogEnabled() {
        return AbstractApplication.isDebug();
    }
}
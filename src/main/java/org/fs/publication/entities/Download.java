package org.fs.publication.entities;

import android.os.Parcel;
import android.support.annotation.Nullable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.fs.core.AbstractApplication;
import org.fs.core.AbstractEntity;
import org.fs.publication.utils.TableUtility;
import org.fs.util.StringUtility;

/**
 * Created by Fatih on 05/06/16.
 * as org.fs.publication.entities.Download
 */
@DatabaseTable(tableName = TableUtility.DownloadTable.TABLE_NAME)
public final class Download extends AbstractEntity {

    @DatabaseField(id = true)
    private Long   id;

    @DatabaseField(columnName = TableUtility.DownloadTable.CLM_COVER_NAME)
    private String coverName;

    @DatabaseField(columnName = TableUtility.DownloadTable.CLM_FILE_PATH)
    private String filePath;

    public Download() { }

    public Download(Parcel input) {
        super(input);
    }

    public Download(String coverName, String filePath, @Nullable Long id) {
        this.coverName = coverName;
        this.filePath = filePath;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getCoverName() {
        return coverName;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override protected void readParcel(Parcel input) {
        boolean idHasValue = input.readInt() == 1;
        if(idHasValue) {
            id = input.readLong();
        }
        boolean coverNameHasValue = input.readInt() == 1;
        if(coverNameHasValue) {
            coverName = input.readString();
        }
        boolean filePathHasValue = input.readInt() == 1;
        if(filePathHasValue) {
            filePath = input.readString();
        }
    }

    @Override public void writeToParcel(Parcel out, int flags) {
        boolean idHasValue = !StringUtility.isNullOrEmpty(id);
        out.writeInt(idHasValue ? 1 : 0);
        if(idHasValue) {
            out.writeLong(id);
        }
        boolean coverNameHasValue = !StringUtility.isNullOrEmpty(coverName);
        out.writeInt(coverNameHasValue ? 1 : 0);
        if(coverNameHasValue) {
            out.writeString(coverName);
        }
        boolean filePathHasValue = !StringUtility.isNullOrEmpty(filePath);
        out.writeInt(filePathHasValue ? 1 : 0);
        if(filePathHasValue) {
            out.writeString(filePath);
        }
    }

    @Override protected String getClassTag() {
        return Download.class.getSimpleName();
    }

    @Override protected boolean isLogEnabled() {
        return AbstractApplication.isDebug();
    }

    @Override public int describeContents() {
        return 0;
    }

    public final static Creator<Download> CREATOR = new Creator<Download>() {

        @Override public Download createFromParcel(Parcel input) {
            return new Download(input);
        }

        @Override public Download[] newArray(int size) {
            return new Download[size];
        }
    };
}
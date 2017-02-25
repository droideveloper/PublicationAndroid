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
package org.fs.publication.entities;

import android.os.Parcel;
import java.util.Date;
import org.fs.core.AbstractEntity;
import org.fs.publication.BuildConfig;
import org.fs.util.Objects;

public final class Book extends AbstractEntity {

  private String name;
  private String title;
  private String info;
  private Date   date;
  private String cover;
  private String url;

  public Book() {/*default constructor*/}
  Book(Parcel input) {
    super(input);
  }

  public String name() {
    return name;
  }

  public String title() {
    return title;
  }

  public String info() {
    return info;
  }

  public Date date() {
    return date;
  }

  public String cover() {
    return cover;
  }

  public String url() {
    return url;
  }

  @Override protected void readParcel(Parcel input) {
    boolean hasName = input.readInt() == 1;
    if (hasName) {
      name = input.readString();
    }
    boolean hasTitle = input.readInt() == 1;
    if (hasTitle) {
      title = input.readString();
    }
    boolean hasInfo = input.readInt() == 1;
    if (hasInfo) {
      info = input.readString();
    }
    boolean hasDate = input.readInt() == 1;
    if (hasDate) {
      date = new Date(input.readLong());
    }
    boolean hasCover = input.readInt() == 1;
    if (hasCover) {
      cover = input.readString();
    }
    boolean hasUrl = input.readInt() == 1;
    if (hasUrl) {
      url = input.readString();
    }
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    boolean hasName = !Objects.isNullOrEmpty(name);
    out.writeInt(hasName ? 1 : 0);
    if (hasName) {
      out.writeString(name);
    }
    boolean hasTitle = !Objects.isNullOrEmpty(title);
    out.writeInt(hasTitle ? 1 : 0);
    if (hasTitle) {
      out.writeString(title);
    }
    boolean hasInfo = !Objects.isNullOrEmpty(info);
    out.writeInt(hasInfo ? 1 : 0);
    if (hasInfo) {
      out.writeString(info);
    }
    boolean hasDate = !Objects.isNullOrEmpty(date);
    out.writeInt(hasDate ? 1 : 0);
    if (hasDate) {
      out.writeLong(date.getTime());
    }
    boolean hasCover = !Objects.isNullOrEmpty(cover);
    out.writeInt(hasCover ? 1 : 0);
    if (hasCover) {
      out.writeString(cover);
    }
    boolean hasUrl = !Objects.isNullOrEmpty(url);
    out.writeInt(hasUrl ? 1 : 0);
    if (hasUrl) {
      out.writeString(url);
    }
  }

  @Override protected String getClassTag() {
    return Book.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
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
}
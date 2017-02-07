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
import java.util.ArrayList;
import org.fs.core.AbstractEntity;
import org.fs.publication.BuildConfig;
import org.fs.util.Collections;
import org.fs.util.Objects;

public final class Configuration extends AbstractEntity {

  private String            title;
  private String            index;
  private ArrayList<String> contents;

  public Configuration() {/*default constructor*/}
  private Configuration(Parcel input) {
    super(input);
  }

  public String title() {
    return title;
  }

  public String index() { return index; }

  public void index(String index) { this.index = index; }

  public ArrayList<String> contents() {
    return contents;
  }

  @Override protected void readParcel(Parcel input) {
    boolean hasTitle = input.readInt() == 1;
    if (hasTitle) {
      title = input.readString();
    }
    boolean hasIndex = input.readInt() == 1;
    if (hasIndex) {
      index = input.readString();
    }
    boolean hasContents = input.readInt() == 1;
    if (hasContents) {
      contents = new ArrayList<>();
      input.readStringList(contents);
    }
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    boolean hasTitle = !Objects.isNullOrEmpty(title);
    out.writeInt(hasTitle ? 1 : 0);
    if (hasTitle) {
      out.writeString(title);
    }
    boolean hasIndex = !Objects.isNullOrEmpty(index);
    out.writeInt(hasIndex ? 1 : 0);
    if (hasIndex) {
      out.writeString(index);
    }
    boolean hasContents = !Collections.isNullOrEmpty(contents);
    out.writeInt(hasContents ? 1 : 0);
    if (hasContents) {
      out.writeStringList(contents);
    }
  }

  @Override protected String getClassTag() {
    return Configuration.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
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
}
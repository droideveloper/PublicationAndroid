package org.fs.publication.managers;

import org.fs.publication.entities.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipInputStream;

/**
 * Created by Fatih on 04/06/16.
 * as org.fs.publication.managers.IFileManager
 */
public interface IFileManager {

    //TODO add java doc

    /*
        Log Helpers
    */
    void    log(String msg);
    void    log(Exception exp);
    void    log(int lv, String msg);
    String  getClassTag();
    boolean isLogEnabled();


    boolean             hasEnoughStorage(long required);
    boolean             newDirectory(File parent, String newDirectoryName);
    void                newFile(File parent, String fileName, ZipInputStream zis) throws IOException;
    void                extract(File file);
    File                findByTagAndGetFirst(File directory, String tag);
    void                readStream(ZipInputStream zis, File directory) throws IOException;
    Configuration       readContentsInfo(File directory) throws IOException;
}

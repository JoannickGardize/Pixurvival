package com.pixurvival.core.contentPack.serialization.io;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;

@UtilityClass
public class StoreFactory {

    public static StoreInput input(File file) throws IOException {
        if (file.isFile()) {
            return new ZipStoreInput(file);
        } else if (file.isDirectory()) {
            return new DirectoryStoreInput(file);
        } else {
            throw new IOException(file + " is not a file or a directory");
        }
    }
}

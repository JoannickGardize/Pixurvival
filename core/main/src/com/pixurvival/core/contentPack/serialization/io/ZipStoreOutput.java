package com.pixurvival.core.contentPack.serialization.io;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipStoreOutput implements StoreOutput {

    private ZipOutputStream zipOutputStream;

    public ZipStoreOutput(File file) throws FileNotFoundException {
        zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
    }

    @Override
    public OutputStream nextEntry(String path) throws IOException {
        zipOutputStream.putNextEntry(new ZipEntry(path));
        return zipOutputStream;
    }

    @Override
    public void close() throws IOException {
        zipOutputStream.closeEntry();
        zipOutputStream.close();
    }
}

package com.pixurvival.core.contentPack.serialization.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipStoreInput implements StoreInput {

    private File file;
    private ZipFile zipFile;

    public ZipStoreInput(File file) throws IOException {
        this.file = file;
        zipFile = new ZipFile(file, StandardCharsets.UTF_8);
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public boolean exists(String path) {
        return zipFile.getEntry(path) != null;
    }

    @Override
    public InputStream nextEntry(String path) throws IOException {
        return zipFile.getInputStream(zipFile.getEntry(path));
    }

    @Override
    public void forEachEntry(String root, InputEntryConsumer consumer) throws IOException {
        Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
        while (enumeration.hasMoreElements()) {
            ZipEntry entry = enumeration.nextElement();
            if (entry.isDirectory()) {
                continue;
            }
            if (entry.getName().startsWith(root)) {
                consumer.apply(entry.getName());
            }
        }
    }

    @Override
    public void close() throws IOException {
        zipFile.close();
    }
}

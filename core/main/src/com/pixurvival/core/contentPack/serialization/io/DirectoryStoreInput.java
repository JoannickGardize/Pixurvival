package com.pixurvival.core.contentPack.serialization.io;

import com.pixurvival.core.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

public class DirectoryStoreInput implements StoreInput {

    private InputStream currentInputStream;
    private File root;

    public DirectoryStoreInput(File root) {
        this.root = root;
    }

    @Override
    public String getName() {
        return root.getName();
    }

    @Override
    public boolean exists(String path) {
        return new File(root, path).isFile();
    }

    @Override
    public InputStream nextEntry(String path) throws IOException {
        close();
        currentInputStream = new FileInputStream(new File(root, path));
        return currentInputStream;
    }

    @Override
    public void forEachEntry(String root, InputEntryConsumer consumer) throws IOException {
        Iterator<Path> it = Files.walk(new File(this.root, root).toPath()).filter(Files::isRegularFile).iterator();
        while (it.hasNext()) {
            consumer.apply(FileUtils.getRelativeStandardPath(this.root, it.next()));
        }
    }

    @Override
    public void close() throws IOException {
        if (currentInputStream != null) {
            currentInputStream.close();
        }
    }
}

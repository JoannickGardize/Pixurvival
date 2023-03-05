package com.pixurvival.core.contentPack.serialization.io;

import com.pixurvival.core.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

// TODO intelligent write (especially for resources)
public class DirectoryStoreOutput implements StoreOutput {

    private File root;

    private OutputStream currentOutputStream;

    public DirectoryStoreOutput(File root) throws IOException {
        this.root = root.getAbsoluteFile();
        if (!this.root.exists()) {
            if (!this.root.mkdirs()) {
                throw new IOException("Unable to creates the directory " + this.root);
            }
        } else {
            if (!this.root.isDirectory()) {
                throw new IOException("The path " + this.root + "is not a directory");
            }
            if (!this.root.canWrite()) {
                throw new IOException("Cannot write on the directory " + this.root);
            }
        }
    }

    @Override
    public OutputStream nextEntry(String path) throws IOException {
        close();
        File entryFile = new File(root, path).getAbsoluteFile();
        if (!FileUtils.areParentAndChild(root, entryFile)) {
            throw new IOException("The entry " + entryFile + " is not a child of the Store root " + root);
        }
        entryFile.getParentFile().mkdirs();
        currentOutputStream = new FileOutputStream(entryFile);
        return currentOutputStream;
    }

    @Override
    public void close() throws IOException {
        if (currentOutputStream != null) {
            currentOutputStream.close();
        }
    }
}

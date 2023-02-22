package com.pixurvival.gdxcore.util;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.function.Supplier;

/**
 * A proxy to {@link FileHandle} to use an InputStream instead of a regular
 * FileHandle where required.
 *
 * @author SharkHendrix
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class InputStreamFileHandleProxy extends FileHandle {

    private Supplier<InputStream> inputStreamSupplier;
    private String extension;

    @Override
    public InputStream read() {
        return inputStreamSupplier.get();
    }

    @Override
    public String extension() {
        return extension;
    }

    @Override
    public String path() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String name() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String nameWithoutExtension() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String pathWithoutExtension() {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileType type() {
        throw new UnsupportedOperationException();
    }

    @Override
    public File file() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ByteBuffer map() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ByteBuffer map(FileChannel.MapMode mode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputStream write(boolean append) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputStream write(boolean append, int bufferSize) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(InputStream input, boolean append) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Writer writer(boolean append) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Writer writer(boolean append, String charset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeString(String string, boolean append) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeString(String string, boolean append, String charset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeBytes(byte[] bytes, boolean append) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeBytes(byte[] bytes, int offset, int length, boolean append) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileHandle[] list() {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileHandle[] list(FileFilter filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileHandle[] list(FilenameFilter filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileHandle[] list(String suffix) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDirectory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileHandle child(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileHandle sibling(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileHandle parent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void mkdirs() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean exists() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteDirectory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void emptyDirectory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void emptyDirectory(boolean preserveTree) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void copyTo(FileHandle dest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void moveTo(FileHandle dest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long length() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long lastModified() {
        throw new UnsupportedOperationException();
    }
}

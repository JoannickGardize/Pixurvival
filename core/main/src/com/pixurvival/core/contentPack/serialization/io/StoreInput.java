package com.pixurvival.core.contentPack.serialization.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * Abstraction for reading "entries" somewhere.
 */
public interface StoreInput extends Closeable {

    String getName();

    boolean exists(String path);

    InputStream nextEntry(String path) throws IOException;

    void forEachEntry(String root, InputEntryConsumer consumer) throws IOException;
}

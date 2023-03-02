package com.pixurvival.core.contentPack.serialization.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Abstraction for writing "entries" somewhere.
 * <p>
 * The implementation is responsible for the lifecycle of the returned OutputStreams,
 * and only the lastly returned one should be used.
 */
public interface StoreOutput extends Closeable {

    OutputStream nextEntry(String path) throws IOException;
}
